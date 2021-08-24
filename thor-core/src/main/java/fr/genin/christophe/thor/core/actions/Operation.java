package fr.genin.christophe.thor.core.actions;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class Operation implements BiFunction<Object, Object, Boolean> {

    private final String name;


    protected static Option<Integer> toInt(Object o) {
        if (o instanceof Number) {
            return Option.some(((Number) o).intValue());
        }
        if (o instanceof String) {
            return Try.of(() -> Integer.valueOf(o.toString()))
                    .toOption();
        }
        return Option.none();
    }

    protected static Object sanitizeNull(Object p1) {
        return Option.of(p1).getOrElse("null");
    }

    protected static Option<Predicate<Object>> containsCheckFn(Object a) {
        if (a instanceof String) {
            return Option.some((b) -> a.toString().contains(b.toString()));
        }
        if (a instanceof JsonObject) {
            return Option.some((b) -> ((JsonObject) a).containsKey(b.toString()));
        }
        if (a instanceof Iterable) {
            final List<?> objects = List.ofAll((Iterable<?>) a);
            return Option.some((b) -> objects
                    .find(v -> containsCheckFn(v)
                            .getOrElse(o -> false)
                            .test(b)
                    )
                    .isDefined()
            );
        }

        return Option.none();
    }

    protected Operation(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(name, operation.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
