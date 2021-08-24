package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import io.vavr.collection.List;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_GTE;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_IN;

public class In extends Operation {

    public In() {
        super($_IN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.isNull(p2)) {
            return false;
        }
        final Object o1 = sanitizeNull(p1);
        final String s1 = o1.toString();

        if (p2 instanceof Iterable) {
            final List<?> objects = List.ofAll((Iterable<?>) p2);
            if (objects.find(o1::equals).isDefined()) {
                return true;
            }
            return objects
                    .map(o -> sanitizeNull(o).toString())
                    .find(s1::equals)
                    .isDefined();
        }
        return p2.toString().contains(s1);
    }
}
