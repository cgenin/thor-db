package fr.genin.christophe.thor.core.actions.operations;

import fr.genin.christophe.thor.core.actions.Operation;
import fr.genin.christophe.thor.core.utils.Commons;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static fr.genin.christophe.thor.core.actions.ThorOperations.$_ELEM_MATCH;

public class ElemMatch extends Operation {

    public ElemMatch() {
        super($_ELEM_MATCH);
    }

    @Override
    public Boolean apply(Object p1, Object p2) {
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
}
