package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_NKEYIN;

public class Nkeyin extends Operation {

    public Nkeyin() {
        super($_NKEYIN);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return !new Keyin().apply(p1, p2);
    }
}
