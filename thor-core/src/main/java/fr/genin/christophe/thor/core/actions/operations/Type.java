package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.actions.ThorOperations;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_SIZE;
import static fr.genin.christophe.thor.core.actions.ThorOperations.$_TYPE;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class Type extends Operation {
    private final static Logger LOG = LoggerFactory.getLogger(Type.class);
    public static final String TYPE_STRING = "string";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_ARRAY = "array";

    public Type() {
        super($_TYPE);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
        if (Objects.nonNull(p2)) {
            return Option.of(p1).flatMap(a ->
                            Option.of(Match(a).of(
                                    Case($(instanceOf(String.class)), v -> TYPE_STRING),
                                    Case($(instanceOf(Date.class)), v -> TYPE_DATE),
                                    Case($(instanceOf(LocalDateTime.class)), v -> TYPE_DATE),
                                    Case($(instanceOf(LocalDate.class)), v -> TYPE_DATE),
                                    Case($(instanceOf(Number.class)), v -> TYPE_NUMBER),
                                    Case($(instanceOf(Boolean.class)), v -> TYPE_BOOLEAN),
                                    Case($(instanceOf(JsonObject.class)), v -> TYPE_OBJECT),
                                    Case($(instanceOf(Iterable.class)), v -> TYPE_ARRAY),
                                    Case($(), v -> {
                                        LOG.error("type of " + a + " not managed");
                                        return "undefined";
                                    })
                            ))
                    )
                    .map(type -> {
                        if (p2 instanceof JsonObject) {
                            return Commons.doQueryOp(type, (JsonObject) p2);
                        }
                        return p2.toString().equals(type);
                    }).getOrElse(false);

        }
        return false;
    }

}
