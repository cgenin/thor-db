package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_EQ;

public class Eq extends Operation {

    public Eq() {
        super($_EQ);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        return Objects.equals(p1, p2);
    }
}
