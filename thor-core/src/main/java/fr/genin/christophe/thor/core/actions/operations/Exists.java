package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;

import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_EXISTS;

public class Exists extends Operation {

    public Exists() {
        super($_EXISTS);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {

        if (p2 instanceof String && !"null".equals(p2)) {
            return Objects.nonNull(p1);
        }
        if (p2 instanceof Boolean && Boolean.TRUE.equals(p2)) {
            return Objects.nonNull(p1);
        }
        return Objects.isNull(p1);
    }
}
