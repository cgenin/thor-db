package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_FINITE;

public class Finite extends Operation {

    public Finite() {
        super($_FINITE);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        boolean isFinite = Objects.nonNull(p1) && p1 instanceof Number;
        return Objects.equals(p2, isFinite);
    }
}
