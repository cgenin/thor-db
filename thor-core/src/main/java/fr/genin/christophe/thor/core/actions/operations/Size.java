package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NIN;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_SIZE;

public class Size extends Operation {

    public Size() {
        super($_SIZE);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p1 instanceof Iterable) {
            final int length = List.ofAll((Iterable<?>) p1).size();
            if (p2 instanceof Number) {
                return length == ((Number) p2).intValue();
            }
            if (p2 instanceof JsonObject) {
                return Commons.doQueryOp(length, (JsonObject) p2);
            }
        }
        return false;
    }
}
