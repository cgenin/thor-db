package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_IN;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_IN_SET;

public class InSet extends Operation {

    public InSet() {
        super($_IN_SET);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.isNull(p2) || Objects.isNull(p1)) {
            return false;
        }
        String s1 = p1.toString();

        if (p2 instanceof JsonObject) {
            final JsonObject object = (JsonObject) p2;
            return object.containsKey(s1);
        }

        if (p2 instanceof Iterable) {
            final List<?> objects = List.ofAll((Iterable<?>) p2);
            if (objects.find(p1::equals).isDefined()) {
                return true;
            }
            return objects
                    .map(o -> sanitizeNull(o).toString())
                    .find(s1::equals)
                    .isDefined();
        }

        return false;
    }
}
