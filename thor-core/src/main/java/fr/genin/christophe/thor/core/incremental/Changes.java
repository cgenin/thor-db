package fr.genin.christophe.thor.core.incremental;

import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Changes implements Serializable {
  private final CollectionOptions options;
  private List<Change> changes = List.empty();

  public Changes(CollectionOptions options) {
    this.options = options;
  }

  public Changes copy() {
    final Changes c = new Changes(options);
    c.changes = changes.map(Change::copy);
    return c;
  }

  public void flushChanges(){
    changes = List.empty();
  }

  public static Changes from(CollectionOptions options, JsonArray data) {
    List<Change> c = List.ofAll(data).map(Commons.TO_JSON_OBJECT).map(Change::from);
    final Changes changes1 = new Changes(options);
    changes1.changes = c;
    return changes1;
  }

  public void createInsertChange(String name, JsonObject obj) {
    this.createChange(name, Operation.Insert, obj, null);
  }

  public void createUpdateChange(String name, JsonObject obj, JsonObject old) {
    this.createChange(name, Operation.Update, obj, old);
  }

  public void createChange(String name, Operation op, JsonObject obj, JsonObject old) {
    final JsonObject o = (Operation.Update.equals(op) && !this.options.isDisableDeltaChangesApi()) ? this.getChangeDelta(obj, old) : obj.copy();
    changes = changes.append(new Change(name, op, o));
  }

  public List<Change> get() {
    return changes;
  }

  public JsonObject getChangeDelta(JsonObject obj, JsonObject old) {
    return Option.of(old)
      .map(o -> getObjectDelta(o, obj))
      .getOrElse(() -> {
        if (Objects.nonNull(obj))
          return obj.copy();
        return new JsonObject();
      });
  }

  private JsonObject getObjectDelta(JsonObject oldObject, JsonObject newObject) {
    return Option.of(newObject)
      .map(n -> {
          final HashSet<String> oldMinusNew = diffKey(oldObject, n);
          final HashSet<String> newMinusOld = diffKey(n, oldObject);
          final HashSet<String> allDeltas = oldMinusNew.addAll(newMinusOld);
          return oldMinusNew
            .map(k -> new JsonObject().put(k, oldObject.getValue(k)))
            .addAll(
              newMinusOld
                .map(k -> new JsonObject().put(k, n.getValue(k)))
            )
            .addAll(
              HashSet.ofAll(n)
                .filter(e -> !allDeltas.contains(e.getKey()))
                .map(e -> {
                  final Object value = e.getValue();
                  final String key = e.getKey();
                  final Object ovalue = oldObject.getValue(key);
                  if (value instanceof JsonObject) {
                    final JsonObject old = oldObject.getJsonObject(key, new JsonObject());
                    final JsonObject delta = getObjectDelta(old, (JsonObject) value);
                    if (delta.isEmpty()) {
                      return new JsonObject();
                    }
                    return new JsonObject().put(key, delta);
                  }
                  if (value.equals(ovalue)) {
                    return new JsonObject();
                  }
                  return new JsonObject().put(key, value);
                }))
            .reduce(JsonObject::mergeIn);
        }
      )
      .getOrElse(oldObject);
  }

  private HashSet<String> diffKey(JsonObject a, JsonObject b) {
    return HashSet.ofAll(a).map(Map.Entry::getKey)
      .diff(HashSet.ofAll(b).map(Map.Entry::getKey));
  }

  public JsonObject serialize() {
    return new JsonObject()
      .put("changes", new JsonArray(
        changes.map(JsonObject::mapFrom).toJavaList()
      ));
  }


}
