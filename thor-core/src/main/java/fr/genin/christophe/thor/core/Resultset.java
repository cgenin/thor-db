package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.actions.ThorOperations;
import fr.genin.christophe.thor.core.index.Index;
import fr.genin.christophe.thor.core.options.ResultSetDataOptions;
import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.core.utils.Comparators;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Resultset {
    private final static Logger LOG = LoggerFactory.getLogger(Resultset.class);

    final Collection collection;
    private List<Integer> filteredrows;
    private boolean filterInitialized;

    public Resultset(Collection collection) {
        this.collection = collection;
        this.filteredrows = List.empty();
        this.filterInitialized = false;
    }

    public static JsonObject to(Resultset resultset) {
        return new JsonObject()
                .put("filteredrows", new JsonArray(resultset.filteredrows.toJavaList()))
                .put("filterInitialized", resultset.filterInitialized)
                ;
    }

    public static Function<Collection, Resultset> from(JsonObject obj) {
        return collection -> {
            final Resultset resultset = new Resultset(collection);
            resultset.filteredrows = List.ofAll(obj.getJsonArray("filteredrows", new JsonArray())
                    .stream().map(i -> (Integer) i));
            resultset.filterInitialized = obj.getBoolean("filterInitialized", false);
            return resultset;
        };
    }

    public void reset() {
        this.filteredrows = List.empty();
        this.filterInitialized = false;
    }

    public Resultset transform(List<String> transform) {
        transform.forEach(this::transform);
        return this;
    }


    public Resultset transform(Transform transform) {
        if (Objects.nonNull(transform)) {
            transform.get().forEach(td -> {
                switch (td.type) {
                    case "find":
                        this.find(td.value());
                        break;
                    case "simplesort":
                        simplesort(td.property, td.desc);
                        break;
                    case "compoundsort":
                        JsonArray arr = td.value();
                        compoundsort(arr);
                        break;
                    case "limit":
                        limit(td.value());
                        break;
                    case "offset":
                        offset(td.value());
                        break;
                    case "remove":
                        remove();
                        break;
                    default:
                        LOG.warn("type not manage in transform : " + td.type);
                }
            });
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public void execute(String type, Object val) {
        switch (type) {
            case "find":
                this.find((JsonObject) val);
                break;
            case "simplesort":
                simplesort((String) val);
                break;
            case "compoundsort":
                compoundsort((JsonArray) val);
                break;
            case "limit":
                limit((Integer) val);
                break;
            case "where":
                where((Predicate<JsonObject>) val);
                break;
            case "offset":
                offset((Integer) val);
                break;
            case "remove":
                remove();
                break;
            default:
                LOG.warn("type not manage in execute : " + type);
        }
    }

    public Resultset compoundsort(JsonArray arr) {
        if (Objects.nonNull(arr) && !arr.isEmpty()) {
            if (arr.getValue(0) instanceof JsonArray) {
                compoundsortWithDesc(List.ofAll(arr)
                        .map(v -> (JsonArray) v)
                        .map(v -> {
                            final String string = v.getString(0);
                            final Boolean desc = v.getBoolean(1);
                            return Tuple.of(string, desc);
                        }));
                return this;
            }
            compoundsort(List.ofAll(arr).map(Object::toString));

        }
        return this;
    }

    public Resultset transform(String transform) {
        return collection.getTransform(transform)
                .map(this::transform)
                .getOrElse(this);
    }

    public JsonObject toJSON() {
        return new JsonObject().put("filteredrows", filteredrows)
                .put("filterInitialized", filterInitialized);
    }

    public Resultset limit(int qty) {
        if (filteredrows.isEmpty()) {
            filteredrows = collection.prepareFullDocIndex();
        }
        final Resultset resultset = new Resultset(collection);
        resultset.filteredrows = filteredrows.take(qty);
        resultset.filterInitialized = true;
        return resultset;
    }

    public Resultset offset(int pos) {
        if (filteredrows.isEmpty()) {
            filteredrows = collection.prepareFullDocIndex();
        }
        final Resultset resultset = new Resultset(collection);
        resultset.filteredrows = filteredrows.drop(pos);
        resultset.filterInitialized = true;
        return resultset;
    }

    public Function<Collection, Resultset> copy() {

        return coll -> {
            final Resultset resultset = new Resultset(coll);
            resultset.filteredrows = filteredrows;
            resultset.filterInitialized = filterInitialized;
            return resultset;
        };
    }

    public Resultset branch() {
        return copy().apply(collection);
    }

    public Resultset where(Predicate<JsonObject> fun) {
        Objects.requireNonNull(fun, "Argument is not a stored view or a function");
        if (filterInitialized) {
            filteredrows = filteredrows
                    .flatMap(j -> Option.of(collection.data().get(j))
                            .filter(fun)
                            .map(o -> j)
                    );
            return this;
        }
        filteredrows = collection.data().zipWithIndex()
                .filter(t -> fun.test(t._1))
                .map(t -> t._2);
        filterInitialized = true;
        return this;
    }

    public int count() {
        if (filterInitialized) {
            return filteredrows.size();
        }
        return collection.count();
    }

    public List<JsonObject> data() {
        return data(new ResultSetDataOptions());
    }

    public List<JsonObject> data(ResultSetDataOptions options) {
        boolean forceClones = options.isRemoveMeta() || collection.options.isDisableDeltaChangesApi() || options.isForceClones();
        final Function<JsonObject, JsonObject> removeMetadata = o -> {
            if (options.isRemoveMeta()) {
                o.remove(Commons.ID);
                o.remove("meta");
            }
            return o;
        };
        if (!filterInitialized) {
            if (filteredrows.isEmpty()) {
                if (forceClones) {

                    return collection.data()
                            .map(JsonObject::copy)
                            .map(removeMetadata);

                }
                return collection.data();
            } else {
                filterInitialized = true;
            }
        }

        final List<JsonObject> result = filteredrows
                .map(index -> collection.data().get(index))
                .flatMap(Option::of);

        if (forceClones) {
            return result.map(removeMetadata);
        }
        return result;
    }

    public Resultset update(Function<JsonObject, JsonObject> updateFunction) {
        Objects.requireNonNull(updateFunction, "Argument is not a function");
        if (!filterInitialized && filteredrows.isEmpty()) {
            filteredrows = collection.prepareFullDocIndex();
        }
        filteredrows
                .flatMap(index -> Option.of(collection.data().get(index)))
                .map(o -> {
                    if (collection.cloneObjects || !collection.options.isDisableDeltaChangesApi()) {
                        return o.copy();
                    }
                    return o;
                })
                .map(updateFunction)
                .forEach(collection::update);
        return this;
    }

    public Resultset remove() {
        if (!filterInitialized && filteredrows.isEmpty()) {
            filteredrows = collection.prepareFullDocIndex();
        }
        collection.removeBatchByPositions(filteredrows);
        filteredrows = List.empty();
        return this;
    }

    public Resultset simplesort(String propname) {
        return simplesort(propname, false);
    }

    public void sort(Comparator<JsonObject> c) {
        if (filteredrows.isEmpty()) {
            filteredrows = List.ofAll(IntStream.range(0, collection.count()).boxed());
        }
        filteredrows = filteredrows.map(index -> Tuple.of(index, collection.data().get(index)))
                .sorted((a, b) -> c.compare(a._2, b._2))
                .map(Tuple2::_1);
    }

    @SuppressWarnings("unchecked")
    public Resultset simplesort(String propname, boolean desc) {
        Objects.requireNonNull(propname, "propname is null");
        if (filteredrows.isEmpty()) {
            if (filterInitialized) {
                return this;
            }

            final Predicate<Index> selectProp = i -> i.name.equals(propname);
            if (collection.getBinaryIndices().find(selectProp).isDefined()) {
                collection.ensureIndex(propname);
                filteredrows = collection.getBinaryIndices().find(selectProp)
                        .map(index -> index.values)
                        .getOrElseThrow(() -> new IllegalStateException("propname not found : " + propname));
                if (desc) {
                    filteredrows = filteredrows.reverse();
                }
                return this;
            } else {
                this.filteredrows = collection.prepareFullDocIndex();
            }
        }
        Comparator<Tuple2<Integer, JsonObject>> sortFunction = (a, b) -> {

            final Object va = a._2.getValue(propname);
            final Object vb = b._2.getValue(propname);
            if (Objects.isNull(va)) {
                if (Objects.isNull(vb)) {
                    return 0;
                }
                return 1;
            }
            if (Objects.isNull(vb)) {
                return -1;
            }
            if (va instanceof Comparable) {
                return ((Comparable) va).compareTo(vb);
            }
            return va.toString().compareTo(vb.toString());
        };
        filteredrows = filteredrows.flatMap(index -> Option.of(collection.data().get(index))
                        .map(o -> Tuple.of(index, o))
                ).sorted(sortFunction)
                .map(t -> t._1);
        if (desc) {
            filteredrows = filteredrows.reverse();
        }
        return this;
    }

    public Resultset compoundsort(List<String> properties) {
        return compoundsortWithDesc(properties.map(p -> Tuple.of(p, false)));
    }

    public Resultset compoundsortWithDesc(List<Tuple2<String, Boolean>> properties) {
        if (!filterInitialized && filteredrows.isEmpty()) {
            filteredrows = collection.prepareFullDocIndex();
        }
        Comparator<? super Tuple2<Integer, JsonObject>> sortFunction = (a, b) -> Comparators.compoundeval(properties, a._2, b._2);
        filteredrows = filteredrows
                .flatMap(index -> Option.of(collection.data().get(index))
                        .map(o -> Tuple.of(index, o)))
                .sorted(sortFunction)
                .map(Tuple2::_1);
        return this;
    }

    public <mapResp, reduceResp> reduceResp mapReduce(Function<JsonObject, mapResp> mapFunction, Function<List<mapResp>, reduceResp> reduceFunction) {
        return reduceFunction.apply(data().map(mapFunction));
    }


    public Resultset find(JsonObject query) {
        if (collection.count() == 0) {
            filteredrows = List.empty();
            filterInitialized = true;
            return this;
        }

        final List<JsonObject> filters = List.ofAll(query).map(e -> new JsonObject().put(e.getKey(), e.getValue()));
        if (filters.size() > 1) {
            return find(new JsonObject().put("$and", new JsonArray(filters.toJavaList())));
        }
        final String property = filters.get().fieldNames().iterator().next();
        switch (property) {
            case "$and":
                findAnd(query.getJsonArray(property));
                return this;
            case "$or":
                findOr(query.getJsonArray(property));
                return this;
            default:
        }

        String operator;
        Object value;
        if (!(query.getValue(property) instanceof JsonObject)) {
            operator = "$eq";
            value = query.getValue(property);
        } else {
            final JsonObject queryObjectOp = query.getJsonObject(property);
            operator = queryObjectOp.fieldNames().iterator().next();
            value = queryObjectOp.getValue(operator);
        }

        boolean usingDotNotation = Commons.isDeepProperty(property);
        final BiFunction<Object, Object, Boolean> fun = ThorOperations.run(operator);
        Consumer<List<Tuple2<JsonObject, Integer>>> run = records -> {
            if (usingDotNotation) {
                final List<String> paths = List.of(property.split("\\."));
                filteredrows = records.filter(p -> Commons.dotSubScan(p._1, paths, fun, value))
                        .map(p -> p._2);
            } else {
                filteredrows = records.filter(p -> fun.apply(p._1.getValue(property), value))
                        .map(p -> p._2);
            }
        };

        if (filterInitialized) {
            final List<Tuple2<JsonObject, Integer>> records = filteredrows
                    .flatMap(index -> Option.of(collection.data().get(index))
                            .map(o -> Tuple.of(o, index))
                    );
            run.accept(records);
        } else {
            final List<Tuple2<JsonObject, Integer>> tuple2s = collection.data().zipWithIndex();
            run.accept(tuple2s);
        }
        filterInitialized = true;
        return this;
    }

    public Resultset findOr(JsonArray arr) {
        if (Objects.nonNull(arr) && !arr.isEmpty()) {
            filteredrows = List.ofAll(arr)
                    .map(Commons.TO_JSON_OBJECT)
                    .flatMap(query -> branch().find(query).filteredrows)
                    .toSet().toList();
            filterInitialized = true;
        }
        return this;
    }


    public Resultset findAnd(JsonArray arr) {
        if (Objects.nonNull(arr) && !arr.isEmpty()) {
            List.ofAll(arr).map(Commons.TO_JSON_OBJECT)
                    .forEach(this::find);
        }
        return this;
    }


}
