package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.index.ExactIndex;
import fr.genin.christophe.thor.core.index.UniqueIndex;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;

public class Constraints implements Serializable {
  public Set<UniqueIndex> uniques = HashSet.empty();
  public Set<ExactIndex> exacts = HashSet.empty();

  public void clear() {
    uniques = HashSet.empty();
    exacts = HashSet.empty();
  }

  public Constraints copy() {
    final Constraints constraints = new Constraints();
    constraints.uniques = uniques.map(UniqueIndex::copy);
    constraints.exacts = exacts.map(ExactIndex::copy);
    return constraints;
  }

  public Option<UniqueIndex> getUniqueIndex(String field) {
    return this.uniques.find(i -> i.field.equals(field));
  }

  public UniqueIndex pushUniqueIndex(String field) {
    final UniqueIndex uniqueIndex = new UniqueIndex(field);
    uniques = uniques.add(uniqueIndex);
    return uniqueIndex;
  }

  public void pushExactIndex(String field) {
    final ExactIndex exactIndex = new ExactIndex(field);
    exacts = exacts.add(exactIndex);
  }


  public void removeIndex(Long idLoki, JsonObject doc) {
    uniques.forEach(u -> u.removeIndex(idLoki, doc));
    exacts.forEach(e -> e.removeValue(doc));
  }

  public JsonObject serialize() {
    return new JsonObject()
      .put("exacts", new JsonArray(
        exacts.map(ExactIndex::to).toJavaList()
      ))
      .put("uniques", new JsonArray(
        uniques.map(UniqueIndex::to).toJavaList()
      ));
  }

  public void removeIndex(List<JsonObject> results) {
    results.flatMap(o -> Commons.extractIdThor(o)
      .map(idLoki -> Tuple.of(idLoki, o))
    )
      .forEach(t -> removeIndex(t._1, t._2));
  }


}
