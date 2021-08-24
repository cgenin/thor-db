package fr.genin.christophe.thor.core.utils;

import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class CommonsTest {

  @Test
  public void should_extract() {
    final List<JsonObject> test = List.of(
      new JsonObject(),
      new JsonObject().put("a", 1)
        .put("b", new JsonObject()
          .put("c", "test")
          .put("d", new JsonObject()
            .put("e", 5.0)
          )
        ),
      new JsonObject().put("a", 2)
        .put("b",  new JsonObject()
          .put("c", "test2")
          .put("d", new JsonObject())
        )
    );
    assertThat(Commons.extract(null, test)).isEmpty();
    assertThat(Commons.extract("a", null)).isEmpty();
    assertThat(Commons.extract("a", test)).isEqualTo(List.of(1, 2));
    assertThat(Commons.extract("b.c", test)).isEqualTo(List.of("test", "test2"));
    assertThat(Commons.extract("b.d.e", test)).isEqualTo(List.of(5.0));
    assertThat(Commons.extract("z", test)).isEmpty();


  }

  @Test
  public void should_getIn() {
    assertThat(Commons.getIn(null, "a").isEmpty()).isTrue();
    assertThat(Commons.getIn(new JsonObject(), (String) null).isEmpty()).isTrue();
    assertThat(Commons.getIn(new JsonObject(), "a").isEmpty()).isTrue();
    assertThat(Commons.getIn(new JsonObject().put("b", 1), "a").isEmpty()).isTrue();

    assertThat(Commons.getIn(new JsonObject().put("a", 1), "a").get()).isEqualTo(1);
    assertThat(Commons.getIn(new JsonObject().put("a", 1), "b").isEmpty()).isTrue();

    JsonObject subObject = new JsonObject().put("c", "2");
    final JsonObject mainObj = new JsonObject().put("a", subObject);
    assertThat(Commons.getIn(mainObj, "a").get()).isEqualTo(subObject);
    assertThat(Commons.getIn(mainObj, "a.c").get()).isEqualTo("2");
    assertThat(Commons.getIn(mainObj, "a.b").isEmpty()).isTrue();

    assertThat(Commons.getIn(mainObj, List.of("a", "c")).get()).isEqualTo("2");
  }
}
