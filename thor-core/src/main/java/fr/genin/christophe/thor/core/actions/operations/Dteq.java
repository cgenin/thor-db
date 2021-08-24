package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Comparators;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_DTEQ;

public class Dteq extends Operation {

    public Dteq() {
        super($_DTEQ);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return Comparators.aeqHelper(p1, p2);

    }
}
