package fr.genin.christophe.thor.core.dynamicview;

import fr.genin.christophe.thor.core.Resultset;
import fr.genin.christophe.thor.core.options.DynamicViewOption;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;

public class DynamicViewSort implements Serializable {
    private final DynamicViewOption options;
    private Option<SimpleCriteria> simpleCriteria = Option.none();
    private Option<SortCriteria> sortCriteria = Option.none();
    private Option<Comparator<JsonObject>> sortFunction = Option.none();
    private boolean dirty = false;

    public static JsonObject to(DynamicViewSort dynamicViewSort) {
        final JsonObject entries = new JsonObject()
                .put("dirty", dynamicViewSort.dirty);
        dynamicViewSort.simpleCriteria.peek(sc -> entries.put("simpleCriteria", JsonObject.mapFrom(sc)));
        dynamicViewSort.sortCriteria.peek(sc -> entries.put("sortCriteria", JsonObject.mapFrom(sc)));
        return entries;
    }

    public static DynamicViewSort to(JsonObject obj) {
        final DynamicViewSort dynamicViewSort = new DynamicViewSort(new DynamicViewOption());
        dynamicViewSort.dirty = obj.getBoolean("dirty", false);
        dynamicViewSort.simpleCriteria = Option.of(obj.getJsonObject("simpleCriteria"))
                .map(o -> o.mapTo(SimpleCriteria.class));
        dynamicViewSort.sortCriteria = Option.of(obj.getJsonObject("sortCriteria"))
                .map(o -> o.mapTo(SortCriteria.class));
        return dynamicViewSort;
    }

    public DynamicViewSort(DynamicViewOption options) {
        this.options = options;
    }

    public DynamicViewSort copy() {
        final DynamicViewSort dynamicViewSort = new DynamicViewSort(options);
        dynamicViewSort.simpleCriteria = simpleCriteria.map(SimpleCriteria::copy);
        dynamicViewSort.sortCriteria = sortCriteria.map(SortCriteria::copy);
        // TODO
        dynamicViewSort.sortFunction = Option.none();
        dynamicViewSort.dirty = dirty;
        return dynamicViewSort;
    }

    public Option<Consumer<Resultset>> queueSortPhase() {

        return sortFunction
                .map(c -> (Consumer<Resultset>) r -> r.sort(c))
                .orElse(() -> sortCriteria.map(p -> (Consumer<Resultset>) r -> {
                            r.compoundsortWithDesc(p.value());
                        })
                                .orElse(() -> simpleCriteria.map(sc -> r -> {
                                    r.simplesort(sc.propname, sc.desc);
                                }))
                );
    }

    public void applySimpleSort(String propname, boolean desc) {
        Objects.requireNonNull(propname);
        applySimpleSort(new SimpleCriteria(propname, desc));
    }

    public boolean isDefined() {
        return sortFunction.isDefined() || sortCriteria.isDefined() || simpleCriteria.isDefined();
    }


    public void applySortCriteriaWithDesc(List<Tuple2<String, Boolean>> criterias) {
        Objects.requireNonNull(criterias);
        final List<SimpleCriteria> criteria = criterias.map(p -> new SimpleCriteria(p._1, p._2));
        applySortCriteria(criteria);
    }

    public void applySortCriteria(List<SimpleCriteria> criteria) {
        final SortCriteria sortCriteria = new SortCriteria(criteria);
        simpleCriteria = Option.none();

        this.sortCriteria = Option.some(sortCriteria);
        sortFunction = Option.none();
    }

    public void applySort(Comparator<JsonObject> fun) {
        Objects.requireNonNull(fun);
        simpleCriteria = Option.none();
        sortCriteria = Option.none();
        sortFunction = Option.some(fun);

    }

    public boolean isDirty() {
        return dirty;
    }

    public DynamicViewSort setDirty(boolean dirty) {
        this.dirty = dirty;
        return this;
    }

    public void reset() {
        simpleCriteria = Option.none();
        sortCriteria = Option.none();
        sortFunction = Option.none();
        dirty = false;
    }

    public Option<SimpleCriteria> simpleCriteria() {
        return simpleCriteria;
    }

    public Option<SortCriteria> sortCriteria() {
        return sortCriteria;
    }

    public void applySimpleSort(SimpleCriteria sc) {
        Objects.requireNonNull(sc);
        simpleCriteria = Option.some(sc);
        sortCriteria = Option.none();
        sortFunction = Option.none();
    }


    public static class SortCriteria implements Serializable {
        public final List<SimpleCriteria> criterias;

        public SortCriteria(List<SimpleCriteria> criterias) {
            this.criterias = criterias;
        }

        public SortCriteria copy() {
            return new SortCriteria(criterias.map(SimpleCriteria::copy));
        }

        public List<Tuple2<String, Boolean>> value() {
            return criterias.map(c -> Tuple.of(c.propname, c.desc));
        }
    }

    public static class SimpleCriteria implements Serializable {
        public final String propname;
        public final boolean desc;

        public SimpleCriteria(String propname) {
            this(propname, false);
        }

        public SimpleCriteria(String propname, boolean desc) {
            this.propname = propname;
            this.desc = desc;
        }

        public SimpleCriteria copy() {
            return new SimpleCriteria(propname, desc);
        }
    }
}
