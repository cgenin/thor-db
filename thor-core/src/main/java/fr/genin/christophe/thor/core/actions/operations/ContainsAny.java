package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import io.vavr.collection.List;
import io.vavr.control.Option;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_CONTAINS_ANY;

public class ContainsAny extends Operation {

    public ContainsAny() {
        super($_CONTAINS_ANY);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return Operation.containsCheckFn(p1)
                .flatMap(predicate -> Option.of(p2)
                        .map(v -> {
                            if (v instanceof Iterable) {
                                return List.ofAll((Iterable<?>) v).find(predicate).isDefined();
                            }
                            return predicate.test(v);
                        })
                )
                .getOrElse(false);
    }
}
