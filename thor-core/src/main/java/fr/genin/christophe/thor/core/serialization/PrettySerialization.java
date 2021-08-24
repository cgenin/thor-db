package fr.genin.christophe.thor.core.serialization;

import fr.genin.christophe.thor.core.Thor;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class PrettySerialization extends AbstractSerialization {
    Logger LOG = LoggerFactory.getLogger(PrettySerialization.class);

    public PrettySerialization(Thor thor) {
        super(thor);
    }

    @Override
    public byte[] serialize() {
        return toJsonObject()
                .encodePrettily()
                .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void deserialize(byte[] bytes) {
        loadJSONObject(BYTES_TO_STRING.apply(bytes).toTry()
                .map(JsonObject::new)
                .fold(e -> {
                    LOG.error("Error in deserialiing", e);
                    return new JsonObject();
                }, Function.identity())
        );
    }
}
