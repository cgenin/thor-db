package fr.genin.christophe.thor.core.utils;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class Comparators {

  private static BigDecimal to(Object o) {
    if (o instanceof Double) {
      return BigDecimal.valueOf((Double) o);
    }
    if (o instanceof Long) {
      return BigDecimal.valueOf((Long) o);
    }
    return new BigDecimal(o.toString());
  }

  public static boolean hasSameClass(Object o1, Object o2, Class<?> clazz) {
    return clazz.equals(o1.getClass()) && clazz.equals(o2.getClass());
  }

  private final static List<Class<?>> SAME_AEQ = List.of(JsonObject.class, Date.class,
    LocalDate.class, LocalDateTime.class);

  private static Option<Tuple2<Integer, Integer>> testType(Object prop1, Object prop2) {
    if (Objects.isNull(prop1) || Objects.isNull(prop2)
      || Boolean.TRUE.equals(prop1) || Boolean.TRUE.equals(prop2)
      || Boolean.FALSE.equals(prop1) || Boolean.FALSE.equals(prop2)
      || "".equals(prop1) || "".equals(prop2)
    ) {
      final Function<Object, Integer> func = v -> {
        if (Boolean.FALSE.equals(v)) {

          return 3;
        }
        if (Boolean.TRUE.equals(v)) {
          return 4;
        }
        if ("".equals(v)) {
          return 5;
        }
        return 9;

      };
      int t1 = Option.of(prop1)
        .map(func)
        .getOrElse(1);
      int t2 = Option.of(prop2)
        .map(func)
        .getOrElse(1);

      // one or both is edge case
      return Option.some(Tuple.of(t1, t2))
        .filter(t -> t._1 != 9 || t._2 != 9);
    }
    return Option.none();
  }

  @SuppressWarnings("unchecked")
  public static boolean ltHelper(Object prop1, Object prop2, Supplier<Boolean> equal) {
    return testType(prop1, prop2)
      .map(tuple -> {
        if (tuple._1.equals(tuple._2)) {
          return equal.get();
        }
        return tuple._1 < tuple._2;
      })
      .getOrElse(() -> {
        if (prop1.equals(prop2)) {
          return equal.get();
        }
        if (prop1.getClass().equals(prop2.getClass()) && prop1 instanceof Comparable) {
          final int result = ((Comparable) prop1).compareTo(prop2);
          return result < 0;
        }
        final String s1 = prop1.toString();
        final String s2 = prop2.toString();
        return s1.compareTo(s2) < 0;
      });
  }

  @SuppressWarnings("unchecked")
  public static boolean gtHelper(Object prop1, Object prop2, Supplier<Boolean> equal) {
    return testType(prop1, prop2)
      .map(tuple -> {
        if (tuple._1.equals(tuple._2)) {
          return equal.get();
        }
        return tuple._1 > tuple._2;
      })
      .getOrElse(() -> {
        if (prop1.equals(prop2)) {
          return equal.get();
        }
        if (prop1.getClass().equals(prop2.getClass()) && prop1 instanceof Comparable) {
          final int result = ((Comparable) prop1).compareTo(prop2);
          return result > 0;
        }
        final String s1 = prop1.toString();
        final String s2 = prop2.toString();
        return s1.compareTo(s2) > 0;
      });
  }

  public static boolean aeqHelper(Object prop1, Object prop2) {

    if (Option.of(prop1)
      .flatMap(a -> Option.when(Objects.nonNull(prop2), Tuple.of(a, prop2)))
      .map(t -> t._1.equals(t._2))
      .getOrElse(false)
    ) {
      return true;
    }

    return testType(prop1, prop2)
      .map(tuple -> tuple._1.equals(tuple._2))
      .getOrElse(() -> {
        final boolean hasSpecificClass = SAME_AEQ.find(c -> hasSameClass(prop1, prop2, c)).isDefined();
        if (hasSpecificClass) {
          return true;
        }

        if (hasSameClass(prop1, prop2, Iterable.class)) {
          return List.ofAll((Iterable<?>) prop1).equals(List.ofAll((Iterable<?>) prop2));
        }

        final String s1 = prop1.toString();
        final String s2 = prop2.toString();
        if (s1.equals(s2)) {
          return true;
        }

        try {
          final BigDecimal bigDecimal = to(s1);
          final BigDecimal bigDecimal2 = to(s2);
          return bigDecimal.setScale(5).equals(bigDecimal2.setScale(5));
        } catch (Exception ex) {
          return false;
        }
      });
  }

  public static int sortHelper(Object prop1, Object prop2) {
    return sortHelper(prop1, prop2, false);
  }

  public static int sortHelper(Object prop1, Object prop2, boolean desc) {
    if (aeqHelper(prop1, prop2)) {
      return 0;
    }

    if (ltHelper(prop1, prop2, () -> false)) {
      return (desc) ? 1 : -1;
    }

    if (gtHelper(prop1, prop2, () -> false)) {
      return (desc) ? -1 : 1;
    }

    return 0;
  }

  public static int compoundeval(List<Tuple2<String, Boolean>> properties, JsonObject obj1, JsonObject obj2) {

    for (Tuple2<String, Boolean> prop : properties) {
      final Object val1 = Commons.getIn(obj1, prop._1).getOrNull();
      final Object val2 = Commons.getIn(obj2, prop._1).getOrNull();
      final int res = sortHelper(val1, val2);

      if (res != 0) {
        final int desc = (prop._2) ? -1 : 1;
        return res * desc;
      }
    }

    return 0;
  }
}
