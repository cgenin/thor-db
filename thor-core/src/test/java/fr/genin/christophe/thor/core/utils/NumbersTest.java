package fr.genin.christophe.thor.core.utils;

import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NumbersTest {

  @Test
  public void should_median(){
    assertThat(Numbers.median(null)).isEqualTo(0.0);
    assertThat(Numbers.median(List.empty())).isEqualTo(0.0);
    assertThat(Numbers.median(List.of(12, 5, 6, 89, 5, 2390, 1))).isEqualTo(6.0);
    assertThat(Numbers.median(List.of(12, 5, 6, 89, 5, 1))).isEqualTo(5.5);

  }

  @Test
  public void should_average(){
    assertThat(Numbers.average(null)).isEqualTo(0.0);
    assertThat(Numbers.average(List.empty())).isEqualTo(0.0);
    assertThat(Numbers.average(List.of(12, 5, 6, 89, 5, 2390, 1))).isEqualTo(358.2857142857143);
    assertThat(Numbers.average(List.of(5, 10))).isEqualTo(7.5);
  }

  @Test
  public void should_standardDeviation(){
    assertThat(Numbers.standardDeviation(null)).isEqualTo(0.0);
    assertThat(Numbers.standardDeviation(List.empty())).isEqualTo(0.0);
    assertThat(Numbers.standardDeviation(List.of(2, 1.8, 2.2, 2))).isEqualTo(0.14142135623730953);
  }

  @Test
  public void should_extractNumerical(){
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
    assertThat(Numbers.extractNumerical(null, test)).isEmpty();
    assertThat(Numbers.extractNumerical("a", null)).isEmpty();
    assertThat(Numbers.extractNumerical("a", test)).isEqualTo(List.of(1, 2));
    assertThat(Numbers.extractNumerical("b.c", test)).isEmpty();
    assertThat(Numbers.extractNumerical("b.d.e", test)).isEqualTo(List.of(5.0));
    assertThat(Numbers.extractNumerical("z", test)).isEmpty();

  }
}
