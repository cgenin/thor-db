package fr.genin.christophe.thor.core.actions;

import fr.genin.christophe.thor.core.actions.operations.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ThorOperationsTest {

    @Test
    public void should_$eq() {
        assertThat(new Eq().apply("5", "5")).isTrue();
        assertThat(new Eq().apply("5", "6")).isFalse();
        assertThat(new Eq().apply("5", 6)).isFalse();
        assertThat(new Eq().apply("5", 5)).isFalse();
        assertThat(new Eq().apply(null, null)).isTrue();
    }

    @Test
    public void should_$ne() {
        assertThat(new Ne().apply("5", "5")).isFalse();
        assertThat(new Ne().apply("5", "6")).isTrue();
        assertThat(new Ne().apply("5", 6)).isTrue();
        assertThat(new Ne().apply("5", 5)).isTrue();
        assertThat(new Ne().apply(null, null)).isFalse();
    }

    @Test
    public void should_$keyin() {
        assertThat(new Keyin().apply("test", new JsonObject().put("test", 1))).isTrue();
        assertThat(new Keyin().apply("tet", new JsonObject().put("test", 1))).isFalse();
        assertThat(new Keyin().apply("test", new JsonObject().put("tet", 1))).isFalse();
        assertThat(new Keyin().apply("test", null)).isFalse();
        assertThat(new Keyin().apply(null, new JsonObject().put("test", 1))).isFalse();
        assertThat(new Keyin().apply(null, null)).isFalse();
    }


    @Test
    public void should_$nkeyin() {
        assertThat(new Nkeyin().apply("test", new JsonObject().put("test", 1))).isFalse();
        assertThat(new Nkeyin().apply("tet", new JsonObject().put("test", 1))).isTrue();
        assertThat(new Nkeyin().apply("test", new JsonObject().put("tet", 1))).isTrue();
        assertThat(new Nkeyin().apply("test", null)).isTrue();
        assertThat(new Nkeyin().apply(null, new JsonObject().put("test", 1))).isTrue();
        assertThat(new Nkeyin().apply(null, null)).isTrue();
    }


    @Test
    public void should_$not() {
        assertThat(new Not().apply("5", new JsonObject().put("$eq", "5"))).isFalse();
        assertThat(new Not().apply("5", new JsonObject().put("$eq", "6"))).isTrue();
        assertThat(new Not().apply("5", new JsonObject().put("$eq", 6))).isTrue();
        assertThat(new Not().apply("5", new JsonObject().put("$eq", 5))).isTrue();
        assertThat(new Not().apply(null, new JsonObject().put("$eq", 5))).isFalse();
        assertThat(new Not().apply("5", 1)).isFalse();
        assertThat(new Not().apply("5", null)).isFalse();
        assertThat(new Not().apply(null, null)).isFalse();
    }


    @Test
    public void should_$in() {
        assertThat(new In().apply("5", "5")).isTrue();
        assertThat(new In().apply("5", "6")).isFalse();
        assertThat(new In().apply("5", 6)).isFalse();
        assertThat(new In().apply("5", 5)).isTrue();
        assertThat(new In().apply(null, null)).isFalse();

        assertThat(new In().apply("5", List.of("5"))).isTrue();
        assertThat(new In().apply("5", List.of("6"))).isFalse();
        assertThat(new In().apply("5", List.of(6, "7"))).isFalse();
        assertThat(new In().apply("5", List.of(6, 5))).isTrue();
        assertThat(new In().apply("5", List.of("6", "5"))).isTrue();

    }

    @Test
    public void should_$nin() {
        assertThat(new Nin().apply("5", "5")).isFalse();
        assertThat(new Nin().apply("5", "6")).isTrue();
        assertThat(new Nin().apply("5", 6)).isTrue();
        assertThat(new Nin().apply("5", 5)).isFalse();
        assertThat(new Nin().apply(null, null)).isTrue();

        assertThat(new Nin().apply("5", List.of("5"))).isFalse();
        assertThat(new Nin().apply("5", List.of("6"))).isTrue();
        assertThat(new Nin().apply("5", List.of(6, "7"))).isTrue();
        assertThat(new Nin().apply("5", List.of(6, 5))).isFalse();
        assertThat(new Nin().apply("5", List.of("6", "5"))).isFalse();

    }

    @Test
    public void should_$aeq() {
        assertThat(new Aeq().apply("5", "5")).isTrue();
        assertThat(new Aeq().apply("5", "6")).isFalse();
        assertThat(new Aeq().apply("5", 6)).isFalse();
        assertThat(new Aeq().apply("5", 5)).isTrue();
        assertThat(new Aeq().apply(null, null)).isTrue();
        assertThat(new Aeq().apply(null, 1)).isFalse();
        assertThat(new Aeq().apply(1, null)).isFalse();
    }

    @Test
    public void should_$dteq() {
        assertThat(new Dteq().apply("5", "5")).isTrue();
    }

    @Test
    public void should_$gt() {
        assertThat(new Gt().apply("5", "5")).isFalse();
        assertThat(new Gt().apply(6, 5)).isTrue();
    }

    @Test
    public void should_$size() {
        assertThat(new Size().apply(List.of("5"), 2)).isFalse();
        assertThat(new Size().apply(List.of(6, 5), 2)).isTrue();
        assertThat(new Size().apply(List.of(6, 5, 3), 2)).isFalse();
        assertThat(new Size().apply("1", 2)).isFalse();
        assertThat(new Size().apply("12", 2)).isFalse();
        assertThat(new Size().apply(new JsonArray()
                        .add(1)
                        .add(1),
                new JsonObject().put("$size", 2)))
                .isFalse();
    }

    @Test
    public void should_$gte() {
        assertThat(new Gte().apply("5", "5")).isTrue();
        assertThat(new Gte().apply(6, 5)).isTrue();
    }

    @Test
    public void should_$lt() {
        assertThat(new Lt().apply("5", "5")).isFalse();
        assertThat(new Lt().apply(6, 5)).isFalse();
        assertThat(new Lt().apply(5, 6)).isTrue();
    }

    @Test
    public void should_$containsString() {
        assertThat(new ContainsString().apply("5", "5")).isTrue();
        assertThat(new ContainsString().apply("essaiUnTruc", "Un")).isTrue();
        assertThat(new ContainsString().apply(6, 5)).isFalse();
        assertThat(new ContainsString().apply("essaiUnTruc", null)).isFalse();
        assertThat(new ContainsString().apply(null, "Un")).isFalse();
    }

    @Test
    public void should_$definedin() {
        assertThat(new Definedin().apply("luke", new JsonObject()
                .put("luke", "skywalker"))
        ).isTrue();
        assertThat(new Definedin().apply("princess", new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new Definedin().apply("princess", null)).isFalse();
        assertThat(new Definedin().apply(null, new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new Definedin().apply(0, new JsonArray().add(1).add(2))).isTrue();
        assertThat(new Definedin().apply("0", new JsonArray().add(1).add(2))).isTrue();
        assertThat(new Definedin().apply("0S", new JsonArray().add(1).add(2))).isFalse();
        assertThat(new Definedin().apply(2, new JsonArray().add(1).add(2))).isFalse();
    }

    @Test
    public void should_$undefinedin() {
        assertThat(new Undefinedin().apply("luke", new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new Undefinedin().apply("princess", new JsonObject()
                .put("luke", "skywalker"))
        ).isTrue();
        assertThat(new Undefinedin().apply("princess", null)).isFalse();
        assertThat(new Undefinedin().apply(null, new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new Undefinedin().apply(0, new JsonArray().add(1).add(2))).isFalse();
        assertThat(new Undefinedin().apply("0", new JsonArray().add(1).add(2))).isFalse();
        assertThat(new Undefinedin().apply(2, new JsonArray().add(1).add(2))).isTrue();
    }

    @Test
    public void should_$lte() {
        assertThat(new Lte().apply("5", "5")).isTrue();
        assertThat(new Lte().apply(6, 5)).isFalse();
        assertThat(new Lte().apply(5, 6)).isTrue();
    }

    @Test
    public void should_$finite() {
        assertThat(new Finite().apply(5, true)).isTrue();
        assertThat(new Finite().apply(5, false)).isFalse();
        assertThat(new Finite().apply(null, true)).isFalse();
        assertThat(new Finite().apply(null, false)).isTrue();
        assertThat(new Finite().apply(null, null)).isFalse();
        assertThat(new Finite().apply(5, 6)).isFalse();
    }

    @Test
    public void should_$exists() {
        assertThat(new Exists().apply(null, "null")).isTrue();
        assertThat(new Exists().apply(null, null)).isTrue();
        assertThat(new Exists().apply(null, false)).isTrue();
        assertThat(new Exists().apply(null, true)).isFalse();
        assertThat(new Exists().apply(null, 1)).isTrue();
        assertThat(new Exists().apply("s", "null")).isFalse();
        assertThat(new Exists().apply("s", null)).isFalse();
        assertThat(new Exists().apply("s", false)).isFalse();
        assertThat(new Exists().apply("s", true)).isTrue();
        assertThat(new Exists().apply("s", 1)).isFalse();
    }

    @Test
    public void should_$between() {
        assertThat(new Between().apply(5, List.of(4, 6))).isTrue();
        assertThat(new Between().apply("5", List.of(4, 6))).isTrue();
        assertThat(new Between().apply(5L, List.of(4, 6))).isTrue();
        assertThat(new Between().apply(5.0, List.of(4, 6))).isTrue();
        assertThat(new Between().apply(4, List.of(4, 6))).isTrue();
        assertThat(new Between().apply(6, List.of(4, 6))).isTrue();
        assertThat(new Between().apply(1, List.of(4, 6))).isFalse();
        assertThat(new Between().apply("7", List.of(4, 6))).isFalse();
        assertThat(new Between().apply(7L, List.of(4, 6))).isFalse();
        assertThat(new Between().apply(7.0, List.of(4, 6))).isFalse();
        assertThat(new Between().apply(null, List.of(4, 6))).isFalse();
        assertThat(new Between().apply(2, null)).isFalse();
        assertThat(new Between().apply(2, "dsdsd")).isFalse();
    }
}
