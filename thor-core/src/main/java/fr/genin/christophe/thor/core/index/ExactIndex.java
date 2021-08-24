package fr.genin.christophe.thor.core.index;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class ExactIndex {
  private Map<Object, List<JsonObject>> index = HashMap.empty();
  public final String field;

  public static JsonObject to(ExactIndex exactIndex) {
    return new JsonObject().put("field", exactIndex.field);
  }

  public ExactIndex(String field) {
    this.field = field;
  }

  public ExactIndex copy(){
    final ExactIndex exactIndex = new ExactIndex(field);
    exactIndex.index = index.toMap(t->t._1, t->t._2.map(JsonObject::copy));
    return exactIndex;
  }

  public synchronized void add(Object key, JsonObject val) {
    final List<JsonObject> n = get(key).getOrElse(List.empty()).append(val);
    index = index.put(key, n);
  }

  public synchronized void remove(Object key, JsonObject val) {
    final List<JsonObject> n = get(key).getOrElse(List.empty()).remove(val);
    index = index.put(key, n);
  }

  public synchronized void removeValue(JsonObject val) {
    index = index.map(t -> Tuple.of(t._1, t._2.remove(val)))
      .filter(t -> !t._2.isEmpty())
      .toMap(Tuple2::_1, Tuple2::_2);
  }

  public synchronized void clear() {
    index = HashMap.empty();
  }

  public Option<List<JsonObject>> get(Object key) {
    return index.get(key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExactIndex that = (ExactIndex) o;
    return Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field);
  }
}
