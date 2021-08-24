package fr.genin.christophe.thor.core.utils;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.function.BiFunction;

public final class Numbers {
  public static final BiFunction<Number, Number, Number> MAX = (a, b) -> {
    if (a instanceof Integer) {
      return Math.max(a.intValue(), b.intValue());
    }
    if (a instanceof Float) {
      return Math.max(a.floatValue(), b.floatValue());
    }
    if (a instanceof Double) {
      return Math.max(a.doubleValue(), b.doubleValue());
    }
    if (a instanceof Long) {
      return Math.max(a.longValue(), b.longValue());
    }
    final int i = a.toString().compareTo(b.toString());
    if (i < 0) {
      return b;
    }
    return a;
  };
  public static final BiFunction<Number, Number, Number> MIN = (a, b) -> {
    if (a instanceof Integer) {
      return Math.min(a.intValue(), b.intValue());
    }
    if (a instanceof Float) {
      return Math.min(a.floatValue(), b.floatValue());
    }
    if (a instanceof Double) {
      return Math.min(a.doubleValue(), b.doubleValue());
    }
    if (a instanceof Long) {
      return Math.min(a.longValue(), b.longValue());
    }
    final int i = a.toString().compareTo(b.toString());
    if (i > 0) {
      return b;
    }
    return a;
  };


  public static Number add(Number a, Number b) {

    final Number a1 = Option.of(a).getOrElse(0.0);
    final Number b1 = Option.of(b).getOrElse(0.0);
    return a1.doubleValue() + b1.doubleValue();

  }

  public static Number sub(Number a, Number b) {
    final Number a1 = Option.of(a).getOrElse(0);
    final Number b1 = Option.of(b).getOrElse(0);
    return a1.doubleValue() - b1.doubleValue();
  }


  public static double median(List<? extends Number> values) {
    if (Objects.isNull(values) || values.isEmpty()) {
      return 0.0;
    }
    final List<? extends Number> sorted = values.sorted((a, b) -> sub(a, b).intValue());
    Double h = Math.floor(values.size() / 2.0);
    final int half = h.intValue();
    if (values.size() % 2 == 0) {
      return ((sorted.get(half - 1).doubleValue() + sorted.get(half).doubleValue()) / 2.0);
    }
    return sorted.get(half).doubleValue();
  }

  public static Option<Number> min(List<? extends Number> objects) {
    return Option.of(objects)
      .filter(list -> !list.isEmpty())
      .map(list -> list.map(i -> (Number) i))
      .map(list -> {
        if (list.size() == 1) {
          return list.get(0);
        }
        return list.reduce(MIN);
      });

  }

  public static Option<Number> max(List<? extends Number> objects) {
    return Option.of(objects)
      .filter(list -> !list.isEmpty())
      .map(list -> list.map(i -> (Number) i))
      .map(list -> {
        if (list.size() == 1) {
          return list.get(0);
        }
        return list.reduce(MAX);
      });
  }

  public static double average(List<? extends Number> values) {
    if (Objects.isNull(values) || values.isEmpty()) {
      return 0.0;
    }
    return values.foldRight(0.0, Numbers::add).doubleValue() / values.size();

  }

  public static double standardDeviation(List<? extends Number> values) {
    if (Objects.isNull(values) || values.isEmpty()) {
      return 0.0;
    }
    double avg = average(values);

    double avgSquareDiff = average(values.map(value -> {
      final double diff = value.doubleValue() - avg;
      return diff * diff;
    }));
    return Math.sqrt(avgSquareDiff);
  }

  public static List<? extends Number> extractNumerical(String field, List<JsonObject> data) {
    return Commons.extract(field, data)
      .filter(e -> e instanceof Number)
      .map(e -> (Number) e);
  }
}
