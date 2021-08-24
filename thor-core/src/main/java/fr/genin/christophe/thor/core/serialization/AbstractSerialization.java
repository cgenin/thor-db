package fr.genin.christophe.thor.core.serialization;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Serialization;
import fr.genin.christophe.thor.core.Thor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public abstract class AbstractSerialization implements Serialization {

    private final Thor thor;

    protected AbstractSerialization(Thor thor) {
        this.thor = thor;
    }

    public JsonObject toJsonObject(){
        final JsonObject o = JsonObject.mapFrom(thor())
                .put("collections", new JsonArray(
                                thor.collections()
                                        .map(Collection::serialize)
                                        .toJavaList()
                        )
                )
                .put("options", JsonObject.mapFrom(thor().options()));

        o.remove("autosaveHandle");
        o.remove("persistenceAdapter");
        o.remove("constraints");
        o.remove("ttl");
        o.remove("throttledSavePending");
        o.remove("throttledCallbacks");
        return o;
    }

    @Override
    public Thor thor() {
        return thor;
    }
}
