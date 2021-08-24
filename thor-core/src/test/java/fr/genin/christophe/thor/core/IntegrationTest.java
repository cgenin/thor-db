package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.mockito.Mockito.*;


import static fr.genin.christophe.thor.core.utils.Commons.ID;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

  @Test
  public void should_example() {
    final Infrastructure infrastructure = mock(Infrastructure.class);
    final Thor db = new Thor(infrastructure, new ThorOptions());
    final Collection users = db.addCollection("user");
    assertThat(users).isNotNull();
    assertThat(users.insert(new JsonObject()
      .put("name", "Odin")
      .put("address", "Asgard")
      .put("age", 50)
    ).isDefined()).isTrue();

    assertThat(users.insert(List.of(
      new JsonObject()
        .put("name", "Thor")
        .put("age", 35),
      new JsonObject()
        .put("name", "Loki")
        .put("age", 30)
    )).get()).hasSize(2);

    List<JsonObject> results = users.find(new JsonObject().put("age", new JsonObject().put("$gte", 35)));
    final Function<JsonObject, String> extractName = o -> o.getString("name");
    assertThat(results.map(extractName))
      .isEqualTo(List.of("Odin", "Thor"));

    assertThat(users.findOne(new JsonObject().put("name", "Odin")))
      .hasSize(1).allMatch(obj -> obj.getLong(ID).equals(1L));

    assertThat(users.where(obj -> obj.getInteger("age", -1) > 35).map(extractName))
      .hasSize(1).contains("Odin");

    assertThat(users.chain().find(new JsonObject().put("address", "Asgard")).data()
      .map(extractName))
      .contains("Odin");

    final JsonObject before40 = new JsonObject()
      .put("age", new JsonObject()
        .put("$lte", 40));
    users.addTransform("progeny", List.of(
      new JsonObject()
        .put("type", "find")
        .put("value", before40)
    ));

    assertThat(users.chain("progeny").data()
      .map(extractName)).isEqualTo(List.of("Thor", "Loki"));

    final DynamicView pView = users.addDynamicView("progeny");
    pView.applyFind(before40)
      .applySimpleSort("name");

    assertThat(pView.data().map(extractName)).isEqualTo(List.of("Loki", "Thor"));

  }
}
