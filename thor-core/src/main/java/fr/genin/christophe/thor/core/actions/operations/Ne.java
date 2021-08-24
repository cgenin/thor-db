package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NE;

public class Ne extends Operation {

    public Ne() {
        super($_NE);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return !new Eq().apply(p1, p2);
    }
}
