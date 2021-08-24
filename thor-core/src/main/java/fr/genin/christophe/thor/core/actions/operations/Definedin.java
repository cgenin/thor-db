package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import io.vavr.collection.List;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_DEFINEDIN;

public class Definedin extends Operation {

    public Definedin() {
        super($_DEFINEDIN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1)) {
            if (p2 instanceof JsonObject) {
                JsonObject o2 = (JsonObject) p2;
                return o2.containsKey(p1.toString());
            }
            if (p2 instanceof Iterable) {
                return toInt(p1)
                        .flatMap(index -> {
                            final List<?> objects = List.ofAll((Iterable<?>) p2);
                            return Try.of(() -> objects.get(index))
                                    .map(Objects::nonNull)
                                    .toOption();
                        }).getOrElse(false);
            }
        }
        return false;
    }
}
