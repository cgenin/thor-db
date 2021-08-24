package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Comparators;
import io.vavr.collection.List;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_BETWEEN;

public class Between extends Operation {

    public Between() {
        super($_BETWEEN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p2) && p2 instanceof Iterable) {
            final List<Object> l = List.ofAll((Iterable<?>) p2);
            if (Objects.isNull(p1) || l.isEmpty()) {
                return false;
            }
            return Comparators.gtHelper(p1, l.head(), () -> true) && Comparators.ltHelper(p1, l.get(1), () -> true);
        }
        return false;
    }
}
