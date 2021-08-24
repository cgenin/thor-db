package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NE;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NOT;

public class Not extends Operation {

    public Not() {
        super($_NOT);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof JsonObject) {
            return !Commons.doQueryOp(p1, (JsonObject) p2);
        }
        return false;
    }
}
