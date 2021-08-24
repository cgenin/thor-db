package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.actions.ThorOperations;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.function.Predicate;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_LT;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_OR;
import static io.vavr.Predicates.anyOf;

public class Or extends Operation {

    public Or() {
        super($_OR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof Iterable) {
            return List.ofAll((Iterable<?>) p2)
                    .map(ThorOperations.JsonPredicate::new)
                    .find(pred->pred.test(p1))
                    .isDefined();
        }
        return false;
    }
}
