package fr.genin.christophe.thor.core;

import io.vavr.collection.List;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Objects;

import static fr.genin.christophe.thor.core.utils.Commons.TO_JSON_OBJECT;

public class Transform implements Serializable {
  public final String name;
  public final List<JsonObject> operations;

  public static JsonObject to(Transform transform) {
    return new JsonObject().put("name", transform)
      .put("operations", new JsonArray(transform.operations
        .toJavaList()));
  }

  public static Transform from(JsonObject obj) {
    return new Transform(obj.getString("name"),
      List.ofAll(obj.getJsonArray("operations", new JsonArray()))
        .map(TO_JSON_OBJECT)
    );
  }

  public Transform(String name, List<JsonObject> operations) {
    this.name = name;
    this.operations = operations;
  }

  public Transform copy() {
    return new Transform(name, operations.map(JsonObject::copy));
  }

  public List<TransformData> get() {
    return operations
      .filter(o -> o.containsKey("type"))
      .map(obj -> {
        final TransformData data = new TransformData(obj.getString("type"));
        data.value = obj.getValue("value");
        data.property = obj.getString("property");
        data.desc = obj.getBoolean("desc", false);
        return data;
      });
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Transform transform = (Transform) o;
    return Objects.equals(name, transform.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


  @SuppressWarnings("unchecked")
  public static class TransformData {
    public final String type;
    public Object value;
    public String property;
    public boolean desc;

    public TransformData(String type) {
      this.type = type;
    }

    public <T> T value() {
      return (T) value;
    }
  }
}
