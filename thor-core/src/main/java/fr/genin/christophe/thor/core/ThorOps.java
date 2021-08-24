package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.core.utils.Comparators;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;

public class ThorOps {
    private final static Logger LOG = LoggerFactory.getLogger(ThorOps.class);

    public static BiFunction<Object, Object, Boolean> run(String function) {
        final ThorOps thorOps = new ThorOps();
        switch (function) {
            case Commons.$_NOT:
                return thorOps::$not;
            case Commons.$_AND:
                return thorOps::$and;
            case Commons.$_OR:
                return thorOps::$or;
            case Commons.$_EXISTS:
                return thorOps::$exists;
            case Commons.$_SIZE:
                return thorOps::$size;
            case Commons.$_FINITE:
                return thorOps::$finite;
            case Commons.$_GTE:
                return thorOps::$gte;
            case Commons.$_TYPE:
                return thorOps::$type;
            case Commons.$_LT:
                return thorOps::$lt;
            case Commons.$_NE:
                return thorOps::$ne;
            case Commons.$_BETWEEN:
                return thorOps::$between;
            case Commons.$_ELEM_MATCH:
                return thorOps::$elemMatch;
            case Commons.$_IN:
                return thorOps::$in;
            case Commons.$_IN_SET:
                return thorOps::$inSet;
            case Commons.$_NIN:
                return thorOps::$nin;
            case Commons.$_CONTAINS:
                return thorOps::$contains;
            case Commons.$_CONTAINS_ANY:
                return thorOps::$containsAny;
            case Commons.$_CONTAINS_STRING:
                return thorOps::$containsString;
            case Commons.$_DEFINEDIN:
                return thorOps::$definedin;
            case Commons.$_UNDEFINEDIN:
                return thorOps::$undefinedin;
            case Commons.$_NKEYIN:
                return thorOps::$nkeyin;
            case Commons.$_KEYIN:
                return thorOps::$keyin;
            case Commons.$_EQ:
                return thorOps::$eq;
            case Commons.$_AEQ:
                return thorOps::$aeq;
            case Commons.$_DTEQ:
                return thorOps::$dteq;
            case Commons.$_LTE:
                return thorOps::$lte;
            case Commons.$_GT:
                return thorOps::$gt;
            default:
                LOG.error("Method not found in LokiOps " + function);
        }
        return (a, b) -> false;
    }

    public boolean $eq(Object p1, Object p2) {
        return Objects.equals(p1, p2);
    }

    public boolean $aeq(Object p1, Object p2) {
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

    public boolean $dteq(Object p1, Object p2) {
        return Comparators.aeqHelper(p1, p2);
    }

    public boolean $gt(Object p1, Object p2) {
        return Comparators.gtHelper(p1, p2, () -> false);
    }

    public boolean $gte(Object p1, Object p2) {
        return Comparators.gtHelper(p1, p2, () -> true);
    }

    public boolean $lt(Object p1, Object p2) {
        return Comparators.ltHelper(p1, p2, () -> false);
    }

    public boolean $lte(Object p1, Object p2) {
        return Comparators.ltHelper(p1, p2, () -> true);
    }

    public boolean $ne(Object p1, Object p2) {
        return !$eq(p1, p2);
    }

    public boolean $between(Object p1, Object p2) {
        if (Objects.nonNull(p2) && p2 instanceof Iterable) {
            return this.$between(p1, List.ofAll((Iterable<?>) p2));
        }
        return false;
    }

    public boolean $between(Object p1, List<Object> p2) {
        if (Objects.isNull(p1) || Objects.isNull(p2) || p2.isEmpty()) {
            return false;
        }
        return Comparators.gtHelper(p1, p2.head(), () -> true) && Comparators.ltHelper(p1, p2.get(1), () -> true);
    }

    public boolean $in(Object p1, Object p2) {
        if (Objects.isNull(p2)) {
            return false;
        }
        final Object o1 = sanitizeNull(p1);
        final String s1 = o1.toString();

        if (p2 instanceof Iterable) {
            final List<?> objects = List.ofAll((Iterable<?>) p2);
            if (objects.find(o1::equals).isDefined()) {
                return true;
            }
            return objects
                    .map(o -> sanitizeNull(o).toString())
                    .find(s1::equals)
                    .isDefined();
        }
        return p2.toString().contains(s1);
    }

    public boolean $inSet(Object p1, Object p2) {
        if (Objects.isNull(p2) || Objects.isNull(p1)) {
            return false;
        }
        String s1 = p1.toString();

        if (p2 instanceof JsonObject) {
            final JsonObject object = (JsonObject) p2;
            return object.containsKey(s1);
        }

        if (p2 instanceof Iterable) {
            final List<?> objects = List.ofAll((Iterable<?>) p2);
            if (objects.find(p1::equals).isDefined()) {
                return true;
            }
            return objects
                    .map(o -> sanitizeNull(o).toString())
                    .find(s1::equals)
                    .isDefined();
        }

        return false;
    }

    public boolean $nin(Object p1, Object p2) {
        return !$in(p1, p2);
    }

    public boolean $keyin(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1) && p2 instanceof JsonObject) {
            JsonObject o2 = (JsonObject) p2;
            return o2.containsKey(p1.toString());
        }
        return false;
    }

    public boolean $nkeyin(Object p1, Object p2) {
        return !$keyin(p1, p2);
    }

    public boolean $definedin(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1)) {
            if (p2 instanceof JsonObject) {
                JsonObject o2 = (JsonObject) p2;
                return o2.containsKey(p1.toString());
            }
            if (p2 instanceof Iterable) {
                return toInt(p1)
                        .flatMap(index -> {
                            final List<?> objects = List.ofAll((Iterable<?>) p2);
                            return Try.of(() -> objects.get(index))
                                    .map(Objects::nonNull)
                                    .toOption();
                        }).getOrElse(false);
            }
        }
        return false;
    }

    public boolean $undefinedin(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1)) {
            if (p2 instanceof JsonObject) {
                JsonObject o2 = (JsonObject) p2;
                return !o2.containsKey(p1.toString());
            }
            if (p2 instanceof Iterable) {
                return toInt(p1)
                        .map(index -> {
                            final List<?> objects = List.ofAll((Iterable<?>) p2);
                            return Try.of(() -> objects.get(index))
                                    .isEmpty();
                        }).getOrElse(true);
            }
        }
        return false;
    }

    public boolean $containsString(Object p1, Object p2) {
        if (Objects.nonNull(p2) && Objects.nonNull(p1) && p1 instanceof String) {
            return p1.toString().contains(p2.toString());
        }
        return false;
    }

    public boolean $containsAny(Object p1, Object p2) {
        return Commons.containsCheckFn(p1)
                .flatMap(predicate -> Option.of(p2)
                        .map(v -> {
                            if (v instanceof Iterable) {
                                return List.ofAll((Iterable<?>) v).find(predicate).isDefined();
                            }
                            return predicate.test(v);
                        })
                )
                .getOrElse(false);
    }

    public boolean $contains(Object p1, Object p2) {
        return Commons.containsCheckFn(p1)
                .flatMap(predicate -> Option.of(p2)
                        .map(v -> {
                            if (v instanceof Iterable) {
                                return List.ofAll((Iterable<?>) v).find(predicate.negate()).isEmpty();
                            }
                            return predicate.test(v);
                        })
                )
                .getOrElse(false);
    }

    public boolean $elemMatch(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2)
                && p1 instanceof Iterable && p2 instanceof JsonObject) {
            final List<?> a = List.ofAll((Iterable<?>) p1);
            final JsonObject b = (JsonObject) p2;
            final List<Map.Entry<String, Object>> entries = List.ofAll(b);
            return a.find(i -> {
                if (!(i instanceof JsonObject)) {
                    return false;
                }
                JsonObject item = (JsonObject) i;
                final int size = entries.filter(e -> {
                    final String property = e.getKey();
                    final Object value = e.getValue();
                    JsonObject filter = (value instanceof JsonObject) ? (JsonObject) value : new JsonObject().put("$eq", value);
                    if (property.contains(".")) {
                        final List<String> paths = List.of(property.split("\\."));
                        BiFunction<Object, Object, Boolean> fun = (c1, c2) -> {
                            if (c2 instanceof JsonObject) {
                                return Commons.doQueryOp(c1, (JsonObject) c2);
                            }
                            return false;
                        };
                        return Commons.dotSubScan(item, paths, fun, value);
                    }
                    return Commons.doQueryOp(item.getValue(property), filter);
                }).size();
                return size == entries.size();
            }).isDefined();
        }
        return false;
    }

    public boolean $size(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p1 instanceof Iterable) {
            final int length = List.ofAll((Iterable<?>) p1).size();
            if (p2 instanceof Number) {
                return length == ((Number) p2).intValue();
            }
            if (p2 instanceof JsonObject) {
                return Commons.doQueryOp(length, (JsonObject) p2);
            }
        }
        return false;
    }

    public boolean $finite(Object p1, Object p2) {
        boolean isFinite = Objects.nonNull(p1) && p1 instanceof Number;
        return Objects.equals(p2, isFinite);
    }

    public boolean $type(Object p1, Object p2) {
        if (Objects.nonNull(p2)) {
            return Option.of(p1).flatMap(a ->
                            Option.of(Match(a).of(
                                    Case($(instanceOf(String.class)), v -> "string"),
                                    Case($(instanceOf(Date.class)), v -> "date"),
                                    Case($(instanceOf(LocalDateTime.class)), v -> "date"),
                                    Case($(instanceOf(LocalDate.class)), v -> "date"),
                                    Case($(instanceOf(Number.class)), v -> "number"),
                                    Case($(instanceOf(Boolean.class)), v -> "boolean"),
                                    Case($(instanceOf(JsonObject.class)), v -> "object"),
                                    Case($(instanceOf(Iterable.class)), v -> "array"),
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

    public boolean $not(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof JsonObject) {
            return !Commons.doQueryOp(p1, (JsonObject) p2);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean $and(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof Iterable) {
            final Predicate<Object>[] originals = (Predicate<Object>[]) List.ofAll((Iterable<?>) p2)
                    .map(b -> (Predicate<Object>) (
                                    (a) -> b instanceof JsonObject
                                            && Commons.doQueryOp(a, (JsonObject) b)
                            )
                    ).toJavaArray();
            return allOf(originals).test(p1);

        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean $or(Object p1, Object p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2) && p2 instanceof Iterable) {
            final Predicate<Object>[] originals = (Predicate<Object>[]) List.ofAll((Iterable<?>) p2)
                    .map(b -> (Predicate<Object>) (
                                    (a) -> b instanceof JsonObject
                                            && Commons.doQueryOp(a, (JsonObject) b)
                            )
                    ).toJavaArray();
            return anyOf(originals).test(p1);

        }
        return false;
    }

    public boolean $exists(Object p1, Object p2) {

        if (p2 instanceof String && !"null".equals(p2)) {
            return Objects.nonNull(p1);
        }
        if (p2 instanceof Boolean && Boolean.TRUE.equals(p2)) {
            return Objects.nonNull(p1);
        }
        return Objects.isNull(p1);
    }


    private Option<Integer> toInt(Object o) {
        if (o instanceof Number) {
            return Option.some(((Number) o).intValue());
        }
        if (o instanceof String) {
            return Try.of(() -> Integer.valueOf(o.toString()))
                    .toOption();
        }
        return Option.none();
    }

    private Object sanitizeNull(Object p1) {
        return Option.of(p1).getOrElse("null");
    }
}
