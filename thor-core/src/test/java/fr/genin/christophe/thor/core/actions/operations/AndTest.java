package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AndTest {

    @Test
    public void should_and() {
        assertThat(new And().apply("5", "5")).isFalse();
        final JsonArray andClause = new JsonArray()
                .add(new JsonObject()
                        .put(ThorOperations.$_GT, 4))
                .add(new JsonObject()
                        .put(ThorOperations.$_LT, 8));
        assertThat(new And().apply(5, andClause)).isTrue();
        assertThat(new And().apply(6, andClause)).isTrue();
        assertThat(new And().apply(3, andClause)).isFalse();
        assertThat(new And().apply(8, andClause)).isFalse();
        assertThat(new And().apply(null, andClause)).isFalse();
        assertThat(new And().apply(5, null)).isFalse();



    }
}
