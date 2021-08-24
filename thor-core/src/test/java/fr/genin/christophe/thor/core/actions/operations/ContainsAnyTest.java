package fr.genin.christophe.thor.core.actions.operations;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainsAnyTest {

    @Test
    public void should_contains_string() {
        final String value = "yan solo aime la princess leia";
        assertThat(new ContainsAny().apply(value, "leia")).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("solo").add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("luke").add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, "luke")).isFalse();
        assertThat(new ContainsAny().apply(null, "luke")).isFalse();
        assertThat(new ContainsAny().apply(value, null)).isFalse();

    }

    @Test
    public void should_contains_jsonobject() {
        final JsonObject value = new JsonObject().put("solo", false).put("leia", true);
        assertThat(new ContainsAny().apply(value, "leia")).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("solo").add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, new JsonArray().add("luke").add("leia"))).isTrue();
        assertThat(new ContainsAny().apply(value, "luke")).isFalse();

    }
}
