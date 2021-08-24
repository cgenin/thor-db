package fr.genin.christophe.thor.core.utils;

import fr.genin.christophe.thor.core.utils.Comparators;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ComparatorsTest {

  @Test
  public void should_aeqHelper() {
    assertThat(Comparators.aeqHelper(5, 5)).isTrue();
    assertThat(Comparators.aeqHelper(5, 3)).isFalse();
    assertThat(Comparators.aeqHelper(5, '5')).isTrue();
    assertThat(Comparators.aeqHelper(5, '3')).isFalse();
    assertThat(Comparators.aeqHelper(5.0, "5")).isTrue();
    assertThat(Comparators.aeqHelper(3.0, "5")).isFalse();

    assertThat(Comparators.aeqHelper("", "")).isTrue();
    assertThat(Comparators.aeqHelper("", null)).isFalse();

    assertThat(Comparators.aeqHelper(true, true)).isTrue();
    assertThat(Comparators.aeqHelper(null, true)).isFalse();
    assertThat(Comparators.aeqHelper(true, null)).isFalse();

    assertThat(Comparators.aeqHelper(false, false)).isTrue();
    assertThat(Comparators.aeqHelper(false, null)).isFalse();

    assertThat(Comparators.aeqHelper(null, null)).isTrue();

    assertThat(Comparators.aeqHelper(new JsonObject().put("a", "s"), new JsonObject())).isTrue();
    assertThat(Comparators.aeqHelper(null, new JsonObject())).isFalse();
    assertThat(Comparators.aeqHelper(new JsonObject().put("a", "s"), null)).isFalse();

    assertThat(Comparators.aeqHelper(List.of(1,2,3), List.of(1,3))).isFalse();
    assertThat(Comparators.aeqHelper(List.of(1,2,3), List.of(1,2,3))).isTrue();
  }
}
