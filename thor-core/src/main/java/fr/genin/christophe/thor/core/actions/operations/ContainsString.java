package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_CONTAINS_STRING;

public class ContainsString extends Operation {

    public ContainsString() {
        super($_CONTAINS_STRING);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1) && p1 instanceof String) {
            return p1.toString().contains(p2.toString());
        }
        return false;
    }
}
