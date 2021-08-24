package fr.genin.christophe.thor.core.incremental;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class Change {
  public final String name;
  public final Operation operation;
  public final JsonObject obj;

  public Change(String name, Operation operation, JsonObject obj) {
    this.name = name;
    this.operation = operation;
    this.obj = obj;
  }

  public Change copy(){
    return new Change(name, operation, obj.copy());
  }

  public static JsonObject to(Change c) {
    return new JsonObject()
      .put("operation", c.operation.code)
      .put("name", c.name)
      .put("obj", c.obj);
  }

  public static Change from(JsonObject o) {
    final Operation operation = Operation.fromCode(o.getString("operation"));
    final JsonObject obj = o.getJsonObject("obj");
    final String name = o.getString("name");
    return new Change(name, operation, obj);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Change change = (Change) o;
    return Objects.equals(name, change.name) && operation == change.operation && Objects.equals(obj, change.obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, operation, obj);
  }
}
