package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainsTest {

    @Test
    public void should_contains_string() {
        final String value = "yan solo aime la princess leia";
        assertThat(new Contains().apply(value, "leia")).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("leia"))).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("solo").add("leia"))).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("luke").add("leia"))).isFalse();
        assertThat(new Contains().apply(value, "luke")).isFalse();
        assertThat(new Contains().apply(null, "luke")).isFalse();
        assertThat(new Contains().apply(value, null)).isFalse();

    }

    @Test
    public void should_contains_jsonobject() {
        final JsonObject value = new JsonObject().put("solo", false).put("leia", true);
        assertThat(new Contains().apply(value, "leia")).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("leia"))).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("solo").add("leia"))).isTrue();
        assertThat(new Contains().apply(value, new JsonArray().add("luke").add("leia"))).isFalse();
        assertThat(new Contains().apply(value, "luke")).isFalse();

    }
}
