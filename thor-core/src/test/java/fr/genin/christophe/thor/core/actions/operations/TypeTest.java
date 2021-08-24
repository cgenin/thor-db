package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static fr.genin.christophe.thor.core.actions.operations.Type.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeTest {

    @Test
    public void should_test_types() {
        assertThat(new Type().apply("5", "5")).isFalse();
        assertThat(new Type().apply("5", TYPE_STRING)).isTrue();
        assertThat(new Type().apply("5", TYPE_NUMBER)).isFalse();
        assertThat(new Type().apply(5, TYPE_NUMBER)).isTrue();
        assertThat(new Type().apply(5L, TYPE_NUMBER)).isTrue();
        assertThat(new Type().apply(5.0, TYPE_NUMBER)).isTrue();
        assertThat(new Type().apply(5.0f, TYPE_NUMBER)).isTrue();
        assertThat(new Type().apply(5.0f, TYPE_OBJECT)).isFalse();
        assertThat(new Type().apply(new JsonObject(), TYPE_OBJECT)).isTrue();
        assertThat(new Type().apply(new JsonArray(), TYPE_OBJECT)).isFalse();
        assertThat(new Type().apply(new JsonArray(), TYPE_ARRAY)).isTrue();
        /*
        assertThat(new Type().apply(new JsonArray(),
                new JsonObject().put(ThorOperations.$_TYPE, TYPE_ARRAY)))
                .isTrue();

         */
        assertThat(new Type().apply(null, TYPE_ARRAY)).isFalse();
        assertThat(new Type().apply("test", null)).isFalse();
        assertThat(new Type().apply(new JsonArray(), "Je ne cnnais pas")).isFalse();

    }
}
