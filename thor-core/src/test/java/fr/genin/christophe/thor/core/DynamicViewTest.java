package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.dynamicview.DynamicViewSort;
import fr.genin.christophe.thor.core.options.ThorOptions;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DynamicViewTest {

    private Thor thor;
    private Collection test;

    @BeforeEach
    public void before() {
        Infrastructure infrastructure = mock(Infrastructure.class);
        thor = new Thor(infrastructure, new ThorOptions());
        test = thor.addCollection("test");
    }

    @Test
    public void should_dynamic_view() {
        test.add(new JsonObject().put("value", 2));
        test.add(new JsonObject().put("value", 3));
        test.add(new JsonObject().put("value", 1));

        test.addDynamicView("dv").applyFind(new JsonObject().put("value", new JsonObject().put(Commons.$_GT, 1)))
                .applySimpleSort(new DynamicViewSort.SimpleCriteria("value", true));

        final DynamicView dv = test.getDynamicView("dv").get();
        assertThat(dv.count()).isEqualTo(2);
        assertThat(dv.data().map(o -> o.getInteger("value")))
                .isEqualTo(List.of(3, 2));
        test.add(new JsonObject().put("value2", 2));
        test.add(new JsonObject().put("value", 2));
        assertThat(dv.data().map(o -> o.getInteger("value")))
                .isEqualTo(List.of(3, 2, 2));
    }
}
