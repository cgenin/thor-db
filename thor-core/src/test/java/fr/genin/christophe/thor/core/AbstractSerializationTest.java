package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public abstract class AbstractSerializationTest {

    protected Thor thor;
    protected Collection test;
    private Infrastructure infrastructure;

    @BeforeEach
    public void before() {
        infrastructure = mock(Infrastructure.class);
        thor = new Thor(infrastructure, new ThorOptions());
        test = thor.addCollection("test");
        test.add(new JsonObject().put("value", 1));
    }

    @Test
    public void should_deserialize_without_exception() {
        final Thor copy = copy();
        getSerializer(copy).deserialize(null);
        getSerializer(copy).deserialize(new byte[0]);
        getSerializer(copy).deserialize(new JsonObject().toString().getBytes());

    }


        @Test
    public void should_data() {

        test.add(new JsonObject().put("value", 2));
        final byte[] bytes = getSerializer(thor).serialize();
        assertThat(bytes).isNotEmpty().isNotNull();
        final Thor copy = copy();
        getSerializer(copy).deserialize(bytes);
        assertThat(copy.collections().length()).isEqualTo(1);
        final Collection collection = copy.getCollection("test").get();
        assertThat(collection.data().length()).isEqualTo(2);
        assertThat(collection.data().map(Object::toString))
                .isEqualTo(test.data().map(Object::toString));
    }

    @Test
    public void should_multiple_collections() {
        Collection test2 = thor.addCollection("test2");
        test2.add(new JsonObject().put("value", 2));
        final byte[] bytes = getSerializer(thor).serialize();
        assertThat(bytes).isNotEmpty().isNotNull();
        final Thor copy = copy();
        getSerializer(copy).deserialize(bytes);
        assertThat(copy.collections().length()).isEqualTo(2);
        final Collection collection = copy.getCollection("test").get();
        assertThat(collection.data().length()).isEqualTo(1);
        assertThat(collection.data().map(Object::toString))
                .isEqualTo(test.data().map(Object::toString));
        final Collection collection2 = copy.getCollection("test2").get();
        assertThat(collection2.data().length()).isEqualTo(1);
        assertThat(collection2.data().map(Object::toString))
                .isEqualTo(test2.data().map(Object::toString));
    }

    protected abstract Serialization getSerializer(Thor thor);

    protected Thor copy() {
       return new Thor(infrastructure, new ThorOptions());
    }


}
