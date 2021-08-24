package fr.genin.christophe.thor.core;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ThorOpsTest {

    @Test
    public void should_$eq() {
        assertThat(new ThorOps().$eq("5", "5")).isTrue();
        assertThat(new ThorOps().$eq("5", "6")).isFalse();
        assertThat(new ThorOps().$eq("5", 6)).isFalse();
        assertThat(new ThorOps().$eq("5", 5)).isFalse();
        assertThat(new ThorOps().$eq(null, null)).isTrue();
    }

    @Test
    public void should_$ne() {
        assertThat(new ThorOps().$ne("5", "5")).isFalse();
        assertThat(new ThorOps().$ne("5", "6")).isTrue();
        assertThat(new ThorOps().$ne("5", 6)).isTrue();
        assertThat(new ThorOps().$ne("5", 5)).isTrue();
        assertThat(new ThorOps().$ne(null, null)).isFalse();
    }

    @Test
    public void should_$keyin() {
        assertThat(new ThorOps().$keyin("test", new JsonObject().put("test", 1))).isTrue();
        assertThat(new ThorOps().$keyin("tet", new JsonObject().put("test", 1))).isFalse();
        assertThat(new ThorOps().$keyin("test", new JsonObject().put("tet", 1))).isFalse();
        assertThat(new ThorOps().$keyin("test", null)).isFalse();
        assertThat(new ThorOps().$keyin(null, new JsonObject().put("test", 1))).isFalse();
        assertThat(new ThorOps().$keyin(null, null)).isFalse();
    }


    @Test
    public void should_$nkeyin() {
        assertThat(new ThorOps().$nkeyin("test", new JsonObject().put("test", 1))).isFalse();
        assertThat(new ThorOps().$nkeyin("tet", new JsonObject().put("test", 1))).isTrue();
        assertThat(new ThorOps().$nkeyin("test", new JsonObject().put("tet", 1))).isTrue();
        assertThat(new ThorOps().$nkeyin("test", null)).isTrue();
        assertThat(new ThorOps().$nkeyin(null, new JsonObject().put("test", 1))).isTrue();
        assertThat(new ThorOps().$nkeyin(null, null)).isTrue();
    }


    @Test
    public void should_$not() {
        assertThat(new ThorOps().$not("5", new JsonObject().put("$eq", "5"))).isFalse();
        assertThat(new ThorOps().$not("5", new JsonObject().put("$eq", "6"))).isTrue();
        assertThat(new ThorOps().$not("5", new JsonObject().put("$eq", 6))).isTrue();
        assertThat(new ThorOps().$not("5", new JsonObject().put("$eq", 5))).isTrue();
        assertThat(new ThorOps().$not(null, new JsonObject().put("$eq", 5))).isFalse();
        assertThat(new ThorOps().$not("5", 1)).isFalse();
        assertThat(new ThorOps().$not("5", null)).isFalse();
        assertThat(new ThorOps().$not(null, null)).isFalse();
    }


    @Test
    public void should_$in() {
        assertThat(new ThorOps().$in("5", "5")).isTrue();
        assertThat(new ThorOps().$in("5", "6")).isFalse();
        assertThat(new ThorOps().$in("5", 6)).isFalse();
        assertThat(new ThorOps().$in("5", 5)).isTrue();
        assertThat(new ThorOps().$in(null, null)).isFalse();

        assertThat(new ThorOps().$in("5", List.of("5"))).isTrue();
        assertThat(new ThorOps().$in("5", List.of("6"))).isFalse();
        assertThat(new ThorOps().$in("5", List.of(6, "7"))).isFalse();
        assertThat(new ThorOps().$in("5", List.of(6, 5))).isTrue();
        assertThat(new ThorOps().$in("5", List.of("6", "5"))).isTrue();

    }

    @Test
    public void should_$nin() {
        assertThat(new ThorOps().$nin("5", "5")).isFalse();
        assertThat(new ThorOps().$nin("5", "6")).isTrue();
        assertThat(new ThorOps().$nin("5", 6)).isTrue();
        assertThat(new ThorOps().$nin("5", 5)).isFalse();
        assertThat(new ThorOps().$nin(null, null)).isTrue();

        assertThat(new ThorOps().$nin("5", List.of("5"))).isFalse();
        assertThat(new ThorOps().$nin("5", List.of("6"))).isTrue();
        assertThat(new ThorOps().$nin("5", List.of(6, "7"))).isTrue();
        assertThat(new ThorOps().$nin("5", List.of(6, 5))).isFalse();
        assertThat(new ThorOps().$nin("5", List.of("6", "5"))).isFalse();

    }

    @Test
    public void should_$aeq() {
        assertThat(new ThorOps().$aeq("5", "5")).isTrue();
        assertThat(new ThorOps().$aeq("5", "6")).isFalse();
        assertThat(new ThorOps().$aeq("5", 6)).isFalse();
        assertThat(new ThorOps().$aeq("5", 5)).isTrue();
        assertThat(new ThorOps().$aeq(null, null)).isTrue();
        assertThat(new ThorOps().$aeq(null, 1)).isFalse();
        assertThat(new ThorOps().$aeq(1, null)).isFalse();
    }

    @Test
    public void should_$dteq() {
        assertThat(new ThorOps().$dteq("5", "5")).isTrue();
    }

    @Test
    public void should_$gt() {
        assertThat(new ThorOps().$gt("5", "5")).isFalse();
        assertThat(new ThorOps().$gt(6, 5)).isTrue();
    }

    @Test
    public void should_$size() {
        assertThat(new ThorOps().$size(List.of("5"), 2)).isFalse();
        assertThat(new ThorOps().$size(List.of(6, 5), 2)).isTrue();
        assertThat(new ThorOps().$size(List.of(6, 5, 3), 2)).isFalse();
        assertThat(new ThorOps().$size("1", 2)).isFalse();
        assertThat(new ThorOps().$size("12", 2)).isFalse();
        assertThat(new ThorOps().$size(new JsonArray()
                        .add(1)
                        .add(1),
                new JsonObject().put("$size", 2)))
                .isFalse();
    }

    @Test
    public void should_$gte() {
        assertThat(new ThorOps().$gte("5", "5")).isTrue();
        assertThat(new ThorOps().$gte(6, 5)).isTrue();
    }

    @Test
    public void should_$lt() {
        assertThat(new ThorOps().$lt("5", "5")).isFalse();
        assertThat(new ThorOps().$lt(6, 5)).isFalse();
        assertThat(new ThorOps().$lt(5, 6)).isTrue();
    }

    @Test
    public void should_$containsString() {
        assertThat(new ThorOps().$containsString("5", "5")).isTrue();
        assertThat(new ThorOps().$containsString("essaiUnTruc", "Un")).isTrue();
        assertThat(new ThorOps().$containsString(6, 5)).isFalse();
        assertThat(new ThorOps().$containsString("essaiUnTruc", null)).isFalse();
        assertThat(new ThorOps().$containsString(null, "Un")).isFalse();
    }

    @Test
    public void should_$definedin() {
        assertThat(new ThorOps().$definedin("luke", new JsonObject()
                .put("luke", "skywalker"))
        ).isTrue();
        assertThat(new ThorOps().$definedin("princess", new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new ThorOps().$definedin("princess", null)).isFalse();
        assertThat(new ThorOps().$definedin(null, new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new ThorOps().$definedin(0, new JsonArray().add(1).add(2))).isTrue();
        assertThat(new ThorOps().$definedin("0", new JsonArray().add(1).add(2))).isTrue();
        assertThat(new ThorOps().$definedin("0S", new JsonArray().add(1).add(2))).isFalse();
        assertThat(new ThorOps().$definedin(2, new JsonArray().add(1).add(2))).isFalse();
    }

    @Test
    public void should_$undefinedin() {
        assertThat(new ThorOps().$undefinedin("luke", new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new ThorOps().$undefinedin("princess", new JsonObject()
                .put("luke", "skywalker"))
        ).isTrue();
        assertThat(new ThorOps().$undefinedin("princess", null)).isFalse();
        assertThat(new ThorOps().$undefinedin(null, new JsonObject()
                .put("luke", "skywalker"))
        ).isFalse();
        assertThat(new ThorOps().$undefinedin(0, new JsonArray().add(1).add(2))).isFalse();
        assertThat(new ThorOps().$undefinedin("0", new JsonArray().add(1).add(2))).isFalse();
        assertThat(new ThorOps().$undefinedin(2, new JsonArray().add(1).add(2))).isTrue();
    }

    @Test
    public void should_$lte() {
        assertThat(new ThorOps().$lte("5", "5")).isTrue();
        assertThat(new ThorOps().$lt(6, 5)).isFalse();
        assertThat(new ThorOps().$lt(5, 6)).isTrue();
    }

    @Test
    public void should_$finite() {
        assertThat(new ThorOps().$finite(5, true)).isTrue();
        assertThat(new ThorOps().$finite(5, false)).isFalse();
        assertThat(new ThorOps().$finite(null, true)).isFalse();
        assertThat(new ThorOps().$finite(null, false)).isTrue();
        assertThat(new ThorOps().$finite(null, null)).isFalse();
        assertThat(new ThorOps().$finite(5, 6)).isFalse();
    }

    @Test
    public void should_$exists() {
        assertThat(new ThorOps().$exists(null, "null")).isTrue();
        assertThat(new ThorOps().$exists(null, null)).isTrue();
        assertThat(new ThorOps().$exists(null, false)).isTrue();
        assertThat(new ThorOps().$exists(null, true)).isFalse();
        assertThat(new ThorOps().$exists(null, 1)).isTrue();

        assertThat(new ThorOps().$exists("s", "null")).isFalse();
        assertThat(new ThorOps().$exists("s", null)).isFalse();
        assertThat(new ThorOps().$exists("s", false)).isFalse();
        assertThat(new ThorOps().$exists("s", true)).isTrue();
        assertThat(new ThorOps().$exists("s", 1)).isFalse();
    }

    @Test
    public void should_$between() {
        assertThat(new ThorOps().$between(5, List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between("5", List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between(5L, List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between(5.0, List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between(4, List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between(6, List.of(4, 6))).isTrue();
        assertThat(new ThorOps().$between(1, List.of(4, 6))).isFalse();
        assertThat(new ThorOps().$between("7", List.of(4, 6))).isFalse();
        assertThat(new ThorOps().$between(7L, List.of(4, 6))).isFalse();
        assertThat(new ThorOps().$between(7.0, List.of(4, 6))).isFalse();
        assertThat(new ThorOps().$between(null, List.of(4, 6))).isFalse();
        assertThat(new ThorOps().$between(2, null)).isFalse();
        assertThat(new ThorOps().$between(2, "dsdsd")).isFalse();
    }
}
