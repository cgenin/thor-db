package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.incremental.Changes;
import fr.genin.christophe.thor.core.index.Index;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.options.SerializationMethod;
import fr.genin.christophe.thor.core.serialization.AvroSerialization;
import fr.genin.christophe.thor.core.serialization.NormalSerialization;
import fr.genin.christophe.thor.core.serialization.PrettySerialization;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static fr.genin.christophe.thor.core.utils.Commons.TO_INTEGER;
import static fr.genin.christophe.thor.core.utils.Commons.TO_JSON_OBJECT;

public interface Serialization {

    Logger LOG = LoggerFactory.getLogger(Serialization.class);
    Function<byte[], Option<String>> BYTES_TO_STRING = bytes -> Option.of(bytes)
            .map(b -> new String(bytes))
            .filter(s -> !s.isEmpty());

    static void appendData(List<JsonObject> obj, Collection collection) {
        collection.setData(obj);
        collection.uniqueNames().forEach(n -> collection.getUniqueIndex(n, true)
                .peek(d -> obj.forEach(d::set))
        );
        collection.idIndex = collection.idIndex.appendAll(obj.map(o -> o.getLong(Commons.ID)));
    }

    static Serialization from(Thor thor) {
        final SerializationMethod serializationMethod = thor.options().serializationMethod();
        switch (serializationMethod) {
            case avro:
                return new AvroSerialization(thor);
            case pretty:
                return new PrettySerialization(thor);
            case normal:
                return new NormalSerialization(thor);
            default:
                LOG.warn("serializer type not found " + serializationMethod);
                return new NormalSerialization(thor);
        }
    }

    default void loadJSONObject(JsonObject obj) {
        this.loadJSONObject(obj, false);
    }

    default void loadJSONObject(JsonObject thor, boolean retainDirtyFlags) {
        List.ofAll(thor.getJsonArray("collections", new JsonArray()))
                .map(TO_JSON_OBJECT)
                .map(serializeCollection(thor, retainDirtyFlags));

    }

    default Function<JsonObject, Collection> serializeCollection(JsonObject thor, boolean retainDirtyFlags) {
        return coll -> {
            final JsonObject options = coll.getJsonObject("options");
            final CollectionOptions collectionOptions = options.mapTo(CollectionOptions.class);
            Collection copyColl = thor().addCollection(
                    coll.getString("name"), collectionOptions);
            copyColl
                    .setAdaptiveBinaryIndices(coll.getBoolean("adaptiveBinaryIndices", false))
                    // TODO SERIALIZE
//                    .setTransactional(coll.getBoolean("transactional"))
                    //.setAsyncListeners(coll.getBoolean("asyncListeners"))
                    .setCloneObjects(collectionOptions.isClone())
                    .setAutoupdate(collectionOptions.isAutoupdate())
                    .setChanges(Changes.from(collectionOptions, thor.getJsonArray("changes", new JsonArray())))
                    .setDirtyIds(List.ofAll(coll.getJsonArray("dirtyIds", new JsonArray()))
                            .map(TO_INTEGER)
                            .map(Integer::longValue))
                    .setMaxId(coll.getLong("maxId", 0L))
                    .setBinaryIndices(List.ofAll(coll.getJsonArray("binaryIndices", new JsonArray()))
                            .map(TO_JSON_OBJECT)
                            .map(Index::from)
                    )
                    .setTransforms(HashSet.ofAll(coll.getJsonArray("transforms", new JsonArray()))
                            .map(TO_JSON_OBJECT)
                            .map(Transform::from)
                    )
                    .setUniqueNames(List.ofAll(coll.getJsonArray("uniqueNames", new JsonArray()))
                            .map(Object::toString));
            if (retainDirtyFlags) {
                copyColl.setDirty(coll.getBoolean("dirty"));
            } else {
                copyColl.setDirty(false);
            }

            copyColl.appendAll(
                    List.ofAll(coll.getJsonArray("data", new JsonArray()))
                            .filter(o -> o instanceof JsonObject)
                            .map(TO_JSON_OBJECT)
            );

            coll.getJsonArray("dynamicViews", new JsonArray())
                    .stream()
                    .filter(o -> o instanceof JsonObject)
                    .map(TO_JSON_OBJECT)
                    .forEach(o -> {
                        final DynamicView dynamicView = DynamicView.from(o).apply(copyColl);
                        dynamicView.data();
                        copyColl.addDynamicView(dynamicView);
                    });

            if (thor.getDouble("databaseVersion") < thor().databaseVersion) {
                copyColl.ensureAllIndexes();
                copyColl.setDirty(true);
            }

            return copyColl;
        };
    }

    Thor thor();

    byte[] serialize();

    void deserialize(byte[] bytes);

}
