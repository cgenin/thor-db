package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrTest {

    @Test
    public void should_Or() {
        assertThat(new Or().apply("5", "5")).isFalse();
        final JsonArray andClause = new JsonArray()
                .add(new JsonObject()
                        .put(ThorOperations.$_GT, 4))
                .add(new JsonObject()
                        .put(ThorOperations.$_EQ, -1));
        assertThat(new Or().apply(5, andClause)).isTrue();
        assertThat(new Or().apply(6, andClause)).isTrue();
        assertThat(new Or().apply(3, andClause)).isFalse();
        assertThat(new Or().apply(-1, andClause)).isTrue();
        assertThat(new Or().apply(null, andClause)).isFalse();
        assertThat(new Or().apply(5, null)).isFalse();



    }
}
