package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NIN;

public class Nin extends Operation {

    public Nin() {
        super($_NIN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return !new In().apply(p1, p2);
    }
}
