package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Comparators;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_IN;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_LT;

public class Lt extends Operation {

    public Lt() {
        super($_LT);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return Comparators.ltHelper(p1, p2, () -> false);

    }
}
