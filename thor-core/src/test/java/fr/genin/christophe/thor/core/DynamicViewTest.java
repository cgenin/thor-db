package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewSort;
import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

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

        test.addDynamicView("dv").applyFind(new JsonObject().put("value", new JsonObject().put(ThorOperations.$_GT, 1)))
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

    @Test
    public void should_multiple_sorting() {
        test.add(new JsonObject().put("value", 2).put("name", "b"));
        test.add(new JsonObject().put("value", 3).put("name", "a"));
        test.add(new JsonObject().put("value", 3).put("name", "b"));
        test.add(new JsonObject().put("value", 1).put("name", "c"));

        final DynamicView dv = test.addDynamicView("dv");
        dv
                .applySortCriteria(
                        List.of(
                                new DynamicViewSort.SimpleCriteria("value", true),
                                new DynamicViewSort.SimpleCriteria("name")
                        ));
        assertThat(dv.data().map(o->o.getInteger("value")))
                .isEqualTo(List.of(3,3,2,1));

        assertThat(dv.data().map(o->o.getString("name")))
                .isEqualTo(List.of("a","b","b","c"));
        dv
                .applySortCriteria(
                        List.of(
                                new DynamicViewSort.SimpleCriteria("value", false),
                                new DynamicViewSort.SimpleCriteria("name", true)
                        ));
        assertThat(dv.data().map(o->o.getInteger("value")))
                .isEqualTo(List.of(1, 2, 3,3));

        assertThat(dv.data().map(o->o.getString("name")))
                .isEqualTo(List.of("c", "b","b","a"));
    }

    @Test
    public void should_function_sorting() {
        test.add(new JsonObject().put("value", 2).put("name", "b"));
        test.add(new JsonObject().put("value", 3).put("name", "a"));
        test.add(new JsonObject().put("value", 3).put("name", "b"));
        test.add(new JsonObject().put("value", 1).put("name", "c"));

        final DynamicView dv = test.addDynamicView("dv");
        final Comparator<JsonObject> valueAsc = Comparator.comparing(a -> a.getInteger("value"));
        dv.applySort(valueAsc);
        assertThat(dv.data().map(o->o.getInteger("value")))
                .isEqualTo(List.of(1, 2, 3,3));
        dv.applySort(valueAsc.reversed());
        assertThat(dv.data().map(o->o.getInteger("value")))
                .isEqualTo(List.of(3,3, 2, 1));
    }
}
