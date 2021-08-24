package fr.genin.christophe.thor.core.index;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ExactIndexTest {

  private ExactIndex exactIndex;

  @BeforeEach
  public void before() {
    exactIndex = new ExactIndex("a");
  }

  @Test
  public void should_get() {
    assertThat(exactIndex.get(2).isEmpty()).isTrue();
    final JsonObject a = new JsonObject().put("id", "A").put("a", 2);
    exactIndex.add(2, a);
    assertThat(exactIndex.get(2).get()).hasSize(1).contains(a);
    exactIndex.add(3, new JsonObject().put("a", 3));
    assertThat(exactIndex.get(2).get()).hasSize(1).contains(a);
    assertThat(exactIndex.get(3).get()).hasSize(1);
    exactIndex.clear();
    assertThat(exactIndex.get(2).isEmpty()).isTrue();
    assertThat(exactIndex.get(3).isEmpty()).isTrue();

  }
}
