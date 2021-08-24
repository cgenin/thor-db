package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_KEYIN;

public class Keyin extends Operation {

    public Keyin() {
        super($_KEYIN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1) && p2 instanceof JsonObject) {
            JsonObject o2 = (JsonObject) p2;
            return o2.containsKey(p1.toString());
        }
        return false;
    }
}
