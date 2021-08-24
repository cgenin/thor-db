package fr.genin.christophe.thor.core.index;

import io.vavr.collection.List;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Objects;

import static java.util.function.Function.identity;

public class Index implements Serializable {

  public final String name;
  public boolean dirty = true;
  public List<Integer> values;

  public static JsonObject to(Index index) {
    return new JsonObject().put("name", index.name)
      .put("dirty", index.dirty)
      .put("values", new JsonArray(index.values.toJavaList()));
  }

  public static Index from(JsonObject o) {
    final Index index = new Index(o.getString("name"));
    index.dirty = o.getBoolean("dirty");
    index.values = List.ofAll(o.getJsonArray("values", new JsonArray()))
      .map(i -> (Integer) i);
    return index;
  }

  public Index(String name) {
    this.name = name;
  }

  public Index copy() {
    final Index index = new Index(name);
    index.dirty = dirty;
    index.values = values.map(identity());
    return index;
  }

  public Index(String name, boolean dirty, List<Integer> values) {
    this.name = name;
    this.dirty = dirty;
    this.values = values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Index index = (Index) o;
    return Objects.equals(name, index.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


}
