package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Comparators;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_GT;

public class Gt extends Operation {

    public Gt() {
        super($_GT);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return Comparators.gtHelper(p1, p2, () -> false);
    }
}
