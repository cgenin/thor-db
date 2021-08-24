package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static fr.genin.christophe.thor.core.event.ThorEvent.insert;
import static fr.genin.christophe.thor.core.event.ThorEvent.preInsert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ThorEventEmitterTest {

    protected Thor thor;
    protected Collection test;
    private AtomicInteger nb = new AtomicInteger(0);
    private String id;

    @BeforeEach
    public void before() {
        Infrastructure infrastructure = mock(Infrastructure.class);
        thor = new Thor(infrastructure, new ThorOptions());
        test = thor.addCollection("test");
        id = test.addListener(insert, o -> {
            assertThat(o).isNotNull();
            nb.incrementAndGet();
        });
    }

    @Test
    public void should_send_insert() {
        test.insert(new JsonObject().put("test", 1));
        assertThat(nb.get()).isEqualTo(1);
        test.insert(new JsonObject().put("test", 2));
        assertThat(nb.get()).isEqualTo(2);
    }

    @Test
    public void should_send_for_list_of_listeners() {
        AtomicInteger nbPreINsert = new AtomicInteger(0);
        final List<String> ids = test.on(preInsert, List.of(obj -> {
            nbPreINsert.incrementAndGet();
        }));
        assertThat(ids).hasSize(1);
        test.insert(new JsonObject().put("test", 1));
        assertThat(nb.get()).isEqualTo(1);
        assertThat(nbPreINsert.get()).isEqualTo(1);

        test.removeListener(preInsert, ids.get(0));
        test.insert(new JsonObject().put("test", 2));
        assertThat(nb.get()).isEqualTo(2);
        assertThat(nbPreINsert.get()).isEqualTo(1);
    }

    @Test
    public void should_not_listen_if_deleted() {
        test.removeListener(insert, id);
        test.insert(new JsonObject().put("test", 1));
        assertThat(nb.get()).isEqualTo(0);
    }
}
