package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.dynamicview.DynamicViewFilter;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewSort;
import fr.genin.christophe.thor.core.event.ThorEventEmitter;
import fr.genin.christophe.thor.core.options.DynamicViewDataOption;
import fr.genin.christophe.thor.core.options.DynamicViewOption;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;

import static fr.genin.christophe.thor.core.event.ThorEvent.*;
import static fr.genin.christophe.thor.core.utils.Commons.TO_JSON_OBJECT;

public class DynamicView extends ThorEventEmitter {
    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicView.class);
    private final static ExecutorService DV_EXECUTOR_SERVICE = ForkJoinPool.commonPool();

    public final String name;
    private final DynamicViewOption options;
    private List<JsonObject> resultdata;
    private Set<DynamicViewFilter> filterPipeline;
    private Resultset cachedresultset;
    private boolean resultsdirty;
    private Resultset resultset;
    private DynamicViewSort sorting;

    public static JsonObject to(DynamicView dynamicView) {
        return new JsonObject()
                .put("name", dynamicView.name)
                .put("resultset", Resultset.to(dynamicView.resultset))
                .put("filterPipeline", new JsonArray(
                        dynamicView.filterPipeline
                                .map(JsonObject::mapFrom)
                                .toJavaList())
                )
                .put("sorting", DynamicViewSort.to(dynamicView.sorting));
    }

    public static Function<Collection, DynamicView> from(JsonObject obj) {
        return collection -> {
            final String name = obj.getString("name");
            final JsonObject objResultSet = obj.getJsonObject("resultset");
            final DynamicView dynamicView = new DynamicView(collection, name, new DynamicViewOption());
            dynamicView.resultset = Resultset.from(objResultSet).apply(collection);
            dynamicView.filterPipeline = HashSet.ofAll(obj.getJsonArray("filterPipeline", new JsonArray()))
                    .map(TO_JSON_OBJECT)
                    .map(o -> new JsonObject().mapTo(DynamicViewFilter.class));
            dynamicView.sorting = DynamicViewSort.to(obj.getJsonObject("sorting", new JsonObject()));
            return dynamicView;
        };
    }

    public DynamicView(Collection collection, String name, DynamicViewOption options) {
        this.name = name;
        this.options = options;
        this.resultset = new Resultset(collection);
        this.resultdata = List.empty();
        this.resultsdirty = false;
        this.cachedresultset = null;
        this.filterPipeline = HashSet.empty();
        sorting = new DynamicViewSort(options);

    }

    public Function<Collection, DynamicView> copy() {
        return collection -> {
            final DynamicView dynamicView = new DynamicView(collection, name, options);
            dynamicView.resultset = resultset.copy().apply(collection);
            dynamicView.resultdata = resultdata.map(JsonObject::copy);
            dynamicView.resultsdirty = resultsdirty;
            dynamicView.filterPipeline = filterPipeline.map(DynamicViewFilter::copy);
            dynamicView.sorting = sorting.copy();

            return dynamicView;
        };
    }

    public DynamicView applySimpleSort(String prop) {
        return applySimpleSort(prop, false);
    }

    public DynamicView applySimpleSort(DynamicViewSort.SimpleCriteria sc) {
        sorting.applySimpleSort(sc);
        return afterSorting();
    }

    public DynamicView applySimpleSort(String prop, boolean desc) {
        sorting.applySimpleSort(prop, desc);
        return afterSorting();
    }

    private DynamicView afterSorting() {
        queueSortPhase();
        emit(sort, new JsonObject());
        return this;
    }

    public DynamicView applyFind(JsonObject query) {
        return applyFilter(new DynamicViewFilter()
                .setType("find")
                .setVal(query)
                .setUid(UUID.randomUUID().toString())
        );
    }

    public Resultset branchResultset() {
        return resultset;
    }

    public Resultset branchResultset(String transform) {
        return resultset.transform(transform);
    }

    public DynamicView applyWhere(Predicate<JsonObject> predicate) {
        return applyFilter(new DynamicViewFilter()
                .setType("where")
                .setVal(predicate)
                .setUid(UUID.randomUUID().toString())
        );
    }

    public DynamicView applyFilter(DynamicViewFilter find) {
        filterPipeline = filterPipeline.add(find);
        return reapplyFilters();
    }

    private DynamicView reapplyFilters() {
        resultset.reset();
        cachedresultset = null;
        resultdata = List.empty();
        resultsdirty = true;
        final Set<DynamicViewFilter> filters = filterPipeline;
        filterPipeline = HashSet.empty();
        filters.forEach(this::addFilter);
        if (sorting.isDefined()) {
            queueSortPhase();
        } else {
            queueRebuildEvent();
        }
        emit(filter, new JsonObject());
        return this;
    }

    public DynamicView removeFilters() {
        resultset.reset();
        resultdata = List.empty();
        cachedresultset = null;
        boolean filterChanged = !filterPipeline.isEmpty();
        filterPipeline = HashSet.empty();
        sorting.reset();
        queueSortPhase();
        if (filterChanged) {
            emit(filter, new JsonObject());
        }
        return this;

    }

    public DynamicView removeFilter(String uid) {
        if (Objects.nonNull(uid)) {
            filterPipeline = filterPipeline.filter(d -> !d.getUid().equals(uid));
            reapplyFilters();
        }
        return this;
    }

    public int count() {
        if (resultsdirty) {
            resultdata = resultset.data();
        }
        return resultset.count();
    }

    public List<JsonObject> data() {
        return data(new DynamicViewDataOption());
    }

    public List<JsonObject> data(DynamicViewDataOption options) {
        if (this.sorting.isDirty() || resultsdirty) {
            queueSortPhase();
        }
        return resultset.data(options);
    }

    private void addFilter(DynamicViewFilter filter) {
        filterPipeline = filterPipeline.add(filter);
        resultset.execute(filter.getType(), filter.getVal());
    }


    public DynamicView applySortCriteria(List<DynamicViewSort.SimpleCriteria> arr) {
        sorting.applySortCriteria(arr);
        return afterSorting();
    }

    public DynamicView applySort(Comparator<JsonObject> arr) {
        sorting.applySort(arr);
        return afterSorting();
    }

    public void startTransaction() {
        cachedresultset = this.resultset.copy().apply(resultset.collection);
    }

    public void commit() {
        cachedresultset = null;
    }

    public void rollback() {
        resultset = cachedresultset;
        persistence();
    }

    public void queueSortPhase() {
        if (sorting.isDirty()) {
            return;
        }
        sorting.setDirty(true);
        sorting.queueSortPhase().getOrElse(
                        () -> r -> {
                        })
                .accept(resultset);
        resultsdirty = false;
        persistence();
        sorting.setDirty(false);
    }

    private void persistence() {
        resultdata = resultset.data();
        emit(rebuild, new JsonObject().put("resultdata", new JsonArray(resultdata.toJavaList())));
    }

    public void queueRebuildEvent() {

        emit(rebuild, new JsonObject().put("resultdata", new JsonArray(resultdata.toJavaList())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicView that = (DynamicView) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public Future<DynamicView> evaluateDocument() {
       return Future.of(DV_EXECUTOR_SERVICE, this::reapplyFilters)
                .onSuccess(dv -> LOGGER.debug(this.name + " updated from collection change."))
                .onFailure(th -> LOGGER.error(this.name + " error from collection change.", th));
    }

    public DynamicViewOption options() {
        return options;
    }


    public Set<DynamicViewFilter> filterPipeline() {
        return filterPipeline;
    }

    public DynamicViewSort sorting() {
        return sorting;
    }
}
