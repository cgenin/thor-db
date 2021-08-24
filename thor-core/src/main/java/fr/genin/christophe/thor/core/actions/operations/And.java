package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vavr.collection.List;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_AND;

public class And extends Operation {

    public And() {
        super($_AND);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof Iterable) {
            return List.ofAll((Iterable<?>) p2)
                    .map(ThorOperations.JsonPredicate::new)
                    .foldLeft(true, (bool, pred)-> bool && pred.test(p1));
        }
        return false;
    }


}
