package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_AEQ;

public class Aeq extends Operation {

    public Aeq() {
        super($_AEQ);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.isNull(p1)) {
            return Objects.isNull(p2);
        }
        if (Objects.isNull(p2)) {
            return false;
        }
        if (p1.equals(p2))
            return true;

        return p1.toString().equals(p2.toString());
    }
}
