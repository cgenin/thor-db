package fr.genin.christophe.thor.core.utils;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Commons {
  public static final String ID = "$thor";
  public static final Function<Object, JsonObject> TO_JSON_OBJECT = o -> (JsonObject) o;
  public static final Function<Object, Integer> TO_INTEGER = l -> (Integer) l;

  public static Option<Long> extractIdThor(JsonObject obj) {
    return Option.of(obj)
      .map(o -> o.getLong(ID))
      .flatMap(Option::of);
  }

  public static <T> Option<T> getIn(JsonObject object, String path) {
    return getIn(object, path, true);
  }

  @SuppressWarnings("unchecked")
  public static <T> Option<T> getIn(JsonObject object, String path, boolean usingDotNotation) {
    if (Objects.isNull(object)) {
      return Option.none();
    }

    if (!usingDotNotation) {
      return Option.of(object)
        .map(o -> (T) o.getValue(path))
        .flatMap(Option::of);
    }

    return Option.of(path)
      .map(p -> p.split("\\."))
      .flatMap(Option::of)
      .map(List::of)
      .flatMap(l -> getInRecursive(object, l));
  }

  public static <T> Option<T> getIn(JsonObject object, List<String> path) {
    return getInRecursive(object, path);
  }

  @SuppressWarnings("unchecked")
  private static <T> Option<T> getInRecursive(Object object, List<String> path) {
    if (path.isEmpty()) {
      return Option.of((T) object);
    }

    return path.headOption()
      .flatMap(head -> {
        final List<String> tail = path.tail();
        if (object instanceof JsonObject) {
          final Object value = ((JsonObject) object).getValue(head);
          return Option.of(value)
            .flatMap(v -> getInRecursive(v, tail));
        }

        return Option.none();
      });
  }

  public static boolean doQueryOp(Object val, JsonObject op) {
    return Option.of(op)
      .map(o -> List.ofAll(o)
        .find(e -> ThorOperations.run(e.getKey()).apply(val, e.getValue()))
        .isDefined()).getOrElse(false);
  }

  public static boolean dotSubScan(JsonObject root, List<String> paths, BiFunction<Object, Object, Boolean> fun, Object value) {
    if (!paths.isEmpty() && Objects.nonNull(root)) {
      final String path = paths.head();
      Object element = root.getValue(path);
      final List<String> tail = paths.tail();
      if (tail.isEmpty()) {
        return fun.apply(element, value);
      }
      if (element instanceof JsonObject) {
        return dotSubScan((JsonObject) element, tail, fun, value);
      }
      if (Objects.nonNull(element) && element instanceof Iterable) {
        return List.ofAll((Iterable<?>) element)
          .filter(e -> e instanceof JsonObject)
          .map(e -> (JsonObject) e)
          .find(obj -> dotSubScan(obj, tail, fun, value))
          .isDefined();
      }
    }
    return false;
  }


  public static List<Object> extract(String field, List<JsonObject> data) {
    if (Objects.isNull(field) || Objects.isNull(data)) {
      return List.empty();
    }
    boolean isDotNotation = isDeepProperty(field);
    return data.flatMap(obj -> deepProperty(obj, field, isDotNotation));
  }

  public static boolean isDeepProperty(String field) {
    return field.contains(".");
  }

  public static Option<Object> deepProperty(JsonObject obj, String property, boolean isDeep) {
    if (Objects.isNull(obj) || Objects.isNull(property)) {
      return Option.none();
    }

    if (!isDeep) {
      return Option.of(obj.getValue(property));
    }
    final Array<String> split = Array.of(property.split("\\."));
    return innerDeepProperty(obj, split);
  }

  private static Option<Object> innerDeepProperty(Object obj, Array<String> pieces) {
    if (Objects.nonNull(obj)) {
      if (pieces.isEmpty()) {
        return Option.of(obj);
      }

      if (obj instanceof JsonObject) {
        final JsonObject o = (JsonObject) obj;
        final Object value = o.getValue(pieces.head());
        return innerDeepProperty(value, pieces.tail());
      }
    }

    return Option.none();
  }
}
