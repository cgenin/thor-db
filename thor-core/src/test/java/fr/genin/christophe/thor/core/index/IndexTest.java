package fr.genin.christophe.thor.core.index;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Infrastructure;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class IndexTest {

    @Test
    public void should_ensure_index() {
        Infrastructure infrastructure = mock(Infrastructure.class);
        Thor thor = new Thor(infrastructure, new ThorOptions());
        Collection test = thor.addCollection("test");
        test.ensureIndex("value");
        JsonObject value = test.add(new JsonObject().put("value", 1)).get();

        assertThat( test.add(new JsonObject().put("value", 1))
                .isSuccess()).isTrue();

        assertThat( test.add(new JsonObject().put("value2", 1))
                .isSuccess()).isTrue();
        assertThat( test.add(new JsonObject().put("value", 2))
                .isSuccess()).isTrue();
        assertThat(test.remove(value).isSuccess()).isTrue();
        assertThat( test.add(new JsonObject().put("value", 1))
                .isSuccess()).isTrue();

        assertThat(test.find(new JsonObject().put("value", new JsonObject().put("$eq", 1))))
                .hasSize(2);

        assertThat(test.find(new JsonObject().put("value",  2)))
                .hasSize(1);
    }

}
