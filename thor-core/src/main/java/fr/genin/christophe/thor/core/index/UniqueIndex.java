package fr.genin.christophe.thor.core.index;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Objects;

import static fr.genin.christophe.thor.core.utils.Commons.ID;

public class UniqueIndex implements Serializable {
  public final String field;
  private Map<Object, JsonObject> keyMap = HashMap.empty();
  private Map<Long, Object> thorMap = HashMap.empty();

  public static JsonObject to(UniqueIndex uniqueIndex) {
    return new JsonObject().put("field", uniqueIndex.field);
  }

  public UniqueIndex(String field) {
    this.field = field;
  }

  public UniqueIndex copy() {
    final UniqueIndex uniqueIndex = new UniqueIndex(field);
    uniqueIndex.keyMap = keyMap.toMap(t -> t._1, t -> t._2.copy());
    uniqueIndex.thorMap = thorMap.toMap(t -> t._1, t -> t._2);
    return uniqueIndex;
  }

  public void set(JsonObject obj) {
    final Object fieldValue = obj.getValue(field);
    if (Objects.nonNull(fieldValue)) {
      if (this.keyMap.containsKey(fieldValue)) {
        throw new IllegalStateException("Duplicate key for property " + this.field + ": " + fieldValue);
      } else {
        synchronized (this) {
          this.keyMap = this.keyMap.put(fieldValue, obj);
          this.thorMap = this.thorMap.put(obj.getLong(ID), fieldValue);
        }
      }
    }
  }

  public Option<JsonObject> get(Object key) {
    return keyMap.get(key);
  }

  @SuppressWarnings("unused")
  public Option<JsonObject> byId(Long key) {
    return thorMap.get(key)
      .flatMap(k -> keyMap.get(k));
  }

  public void update(JsonObject obj, JsonObject doc) {
    final Object v = doc.getValue(field);

    final Option<Object> old = thorMap.get(obj.getLong(ID));
    if (old.map(cacheValue -> !cacheValue.equals(v))
      .getOrElse(true)
    ) {
      this.set(doc);
      synchronized (this) {
        this.keyMap = keyMap.remove(old.get());
      }
    } else {
      synchronized (this) {
        this.keyMap = keyMap.put(v, doc);
      }
    }
  }

  public void remove(Object key) {
    final JsonObject obj = keyMap.get(key)
      .getOrElseThrow(() -> new IllegalStateException("Key is not in unique index: " + this.field));
    synchronized (this) {
      keyMap = keyMap.remove(key);
      thorMap = thorMap.remove(obj.getLong(ID));
    }
  }

  public void clear() {
    synchronized (this) {
      this.keyMap = HashMap.empty();
      this.thorMap = HashMap.empty();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UniqueIndex that = (UniqueIndex) o;
    return Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field);
  }

  public void removeIndex(Long idLoki, JsonObject doc) {
    final Option<Tuple2<Object, JsonObject>> tupleKey = keyMap.find(e -> doc.equals(e._2));
    if (tupleKey.isDefined()) {
      keyMap = keyMap.remove(tupleKey.get()._1);
    }
    thorMap = thorMap.remove(idLoki);
  }


}
