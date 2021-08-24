package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.index.UniqueIndex;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class CollectionTest {

    private Collection collection;

    @BeforeEach
    public void before() {
        final Infrastructure infrastructure = mock(Infrastructure.class);
        collection = new Collection(infrastructure, "test", new CollectionOptions());
    }

    @Test
    public void should_add() {
        assertThat(collection.data()).isEmpty();
        final Try<JsonObject> add = collection.add(new JsonObject().put("true", true));
        assertThat(add.isSuccess()).isTrue();
        final JsonObject obj = add.get();
        final Long id = obj.getLong(Commons.ID);
        assertThat(id).isGreaterThan(0L);
        assertThat(collection.data()).hasSize(1).contains(obj);
        assertThat(collection.count()).isEqualTo(1);

        assertThat(collection.add(obj).isFailure()).isTrue();
        assertThat(collection.add(null).isFailure()).isTrue();
    }

    @Test
    public void should_get() {
        final JsonObject o = collection.add(new JsonObject().put("true", true)).get();
        assertThat(collection.get(-1L).isEmpty()).isTrue();
        assertThat(collection.get(1L).get()).isEqualTo(o);
        assertThat(collection.get(99L).isEmpty()).isTrue();
    }

    @Test
    public void should_ensureUniqueIndex() {
        final List<JsonObject> insert = collection.insert(List.of(new JsonObject().put("i", 1),
                new JsonObject().put("i", 2)
        )).getOrElseThrow(e -> new IllegalStateException("", e));
        assertThat(collection.getUniqueIndex("i", false).isEmpty()).isTrue();
        final Option<UniqueIndex> opt = collection.getUniqueIndex("i", true);
        assertThat(opt.isDefined()).isTrue();
        final UniqueIndex uniqueIndex = opt.get();
        assertThat(uniqueIndex.get(2).get()).isEqualTo(insert.get(1));
        assertThat(uniqueIndex.get(4).isEmpty()).isTrue();

        assertThat(collection.getUniqueIndex("i", false).isDefined()).isTrue();

        assertThat(collection.insertOne(new JsonObject().put("i", 2), false).isEmpty()).isTrue();
        assertThat(collection.add(new JsonObject().put("i", 2)).isFailure()).isTrue();
        assertThat(collection.insert(List.of(new JsonObject().put("i", 2))).get()).isEmpty();

        assertThat(collection.insertOne(new JsonObject().put("i", 3), false).isDefined()).isTrue();
        assertThat(collection.add(new JsonObject().put("i", 4)).isSuccess()).isTrue();
        assertThat(collection.insert(List.of(new JsonObject().put("i", 5)))).hasSize(1);
    }

    @Test
    public void should_insert() {
        final List<JsonObject> inserted = collection.insert(List.of(new JsonObject().put("i", 1),
                new JsonObject().put("i", 2)
        )).getOrElseThrow(e -> new IllegalStateException("", e));
        assertThat(inserted).hasSize(2);
        assertThat(collection.count()).isEqualTo(2);
    }

    @Test
    public void should_update() {
        final JsonObject o = collection.add(new JsonObject().put("true", true)).get();
        assertThat(collection.update(new JsonObject()).isFailure()).isTrue();
        final Try<JsonObject> update = collection.update(o.put("true", false));
        assertThat(update.isSuccess()).isTrue();
        final JsonObject ou = update.get();
        assertThat(ou.getLong(Commons.ID)).isEqualTo(o.getLong(Commons.ID));
        assertThat(ou.getBoolean("true")).isFalse();
        assertThat(collection.count()).isEqualTo(1);
    }

    @Test
    public void should_update_list() {
        final JsonObject o = collection.add(new JsonObject().put("true", true)).get();
        assertThat(collection.update((List<JsonObject>) null)).isEmpty();

        assertThat(collection.update(List.empty())).isEmpty();

        final List<Try<JsonObject>> tries = collection.update(List.of(o));
        assertThat(tries).hasSize(1);
        assertThat(tries.get(0).isSuccess()).isTrue();

        final List<Try<JsonObject>> tries2 = collection.update(List.of(new JsonObject()));
        assertThat(tries2).hasSize(1);
        assertThat(tries2.get(0).isFailure()).isTrue();
    }

    @Test
    public void should_remove() {
        final JsonObject objOriginal = new JsonObject().put("true", true);
        final JsonObject obj = collection.add(objOriginal).get();
        assertThat(collection.remove(new JsonObject()).isFailure()).isTrue();
        assertThat(collection.remove(obj).isSuccess()).isTrue();
        assertThat(collection.remove((JsonObject) null).isFailure()).isTrue();

        assertThat(collection.add(new JsonObject().put("true", true)).isSuccess()).isTrue();
        assertThat(collection.remove(1).isFailure()).isTrue();
        assertThat(collection.remove(0).isSuccess()).isTrue();

        assertThat(collection.remove((Integer) null).isFailure()).isTrue();
    }

    @Test
    public void should_removeBatch_obj() {
        final JsonObject obj = collection.add(new JsonObject().put("true", true)).get();
        final JsonObject obj2 = collection.add(new JsonObject().put("true", false)).get();
        final Try<List<JsonObject>> aTry = collection.removeBatch(List.of(obj, new JsonObject()));
        assertThat(aTry.isSuccess()).isTrue();
        assertThat(aTry.get()).hasSize(1).contains(obj);
        assertThat(collection.count()).isEqualTo(1);
        final JsonObject obj3 = collection.add(new JsonObject()).get();
        assertThat(collection.removeBatch(List.empty()).get()).isEmpty();
        assertThat(collection.removeBatch(List.of(obj3, obj2)).get()).hasSize(2);
        assertThat(collection.count()).isEqualTo(0);
        assertThat(collection.removeBatch(null).isFailure()).isTrue();
    }

    @Test
    public void should_removeBatch_id() {
        final JsonObject obj = collection.add(new JsonObject().put("true", true)).get();
        final JsonObject obj2 = collection.add(new JsonObject().put("true", false)).get();
        final Try<List<JsonObject>> aTry = collection
                .removeBatchById(List.of(obj.getLong(Commons.ID), 344L));
        assertThat(aTry.isFailure()).isTrue();
        assertThat(collection.count()).isEqualTo(2);
        final JsonObject obj3 = collection.add(new JsonObject()).get();
        assertThat(collection.removeBatchById(List.empty()).get()).isEmpty();
        assertThat(collection.removeBatchById(List.of(obj3.getLong(Commons.ID), obj2.getLong(Commons.ID))).get()).hasSize(2);
        assertThat(collection.count()).isEqualTo(1);
        assertThat(collection.removeBatchById(null).isFailure()).isTrue();
    }
}
