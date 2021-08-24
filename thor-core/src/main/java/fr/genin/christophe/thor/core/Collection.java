package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.event.ThorEventEmitter;
import fr.genin.christophe.thor.core.incremental.Changes;
import fr.genin.christophe.thor.core.index.Index;
import fr.genin.christophe.thor.core.index.UniqueIndex;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.options.DynamicViewOption;
import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.core.utils.Comparators;
import fr.genin.christophe.thor.core.utils.Numbers;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static fr.genin.christophe.thor.core.event.ThorEvent.*;
import static fr.genin.christophe.thor.core.utils.Commons.*;
import static fr.genin.christophe.thor.core.utils.Numbers.MAX;
import static fr.genin.christophe.thor.core.utils.Numbers.MIN;


public class Collection extends ThorEventEmitter implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(Collection.class);
    private final Infrastructure infrastructure;

    String name;
    final CollectionOptions options;
    private boolean adaptiveBinaryIndices = false;
    private boolean asyncListeners;
    boolean cloneObjects;
    private boolean autoupdate;
    private boolean dirty = true;
    private AtomicLong maxId = new AtomicLong(0L);
    private boolean isIncremental = true;
    private List<Index> binaryIndices = List.empty();
    List<Long> idIndex = List.empty();
    private List<Long> dirtyIds = List.empty();
    private List<String> uniqueNames = List.empty();
    private Set<Transform> transforms = HashSet.empty();
    private List<JsonObject> data = List.empty();
    Constraints constraints = new Constraints();
    private Set<DynamicView> dynamicViews = HashSet.empty();
    Changes changes;
    private Transactional transactional;
    Ttl ttl;


    Collection(Infrastructure infrastructure, String name, CollectionOptions collectionOptions) {
        this.infrastructure = infrastructure;
        this.name = name;
        this.options = collectionOptions;
        this.changes = new Changes(options);
        this.transactional = Transactional.build(true);
        this.uniqueNames = Option.of(options.getUnique())
                .map(List::ofAll)
                .getOrElse(List.empty());
        Option.of(options.getExact())
                .map(List::ofAll)
                .getOrElse(List.empty())
                .forEach(constraints::pushExactIndex);
        this.cloneObjects = options.isClone();
        this.autoupdate = options.isAutoupdate();
        this.ttl = new Ttl();
        setTTL(options.getTtl(), options.getTtlInterval());

        Option.of(options.getIndices())
                .map(List::ofAll)
                .getOrElse(List.empty()).forEach(this::ensureIndex);
    }

    public void setTTL(Long timeToLive, Integer timeToLiveInterval) {
        final Long age = Option.of(timeToLive).getOrElse(-1L);
        final Integer interval = Option.of(timeToLiveInterval).getOrElse(-1);
        if (age < 0) {
            Option.of(ttl.getDaemon()).peek(infrastructure::cancelTimer);
        } else if (interval > 0) {

            ttl.setAge(timeToLive).setTtlInterval(interval);
            ttl.setDaemon(infrastructure.setPeriodic(interval, i -> {
                final long now = new Date().getTime();
                final List<JsonObject> data = chain().where(obj -> {
                    final JsonObject meta = obj.getJsonObject("meta", new JsonObject());
                    return Option.of(meta.getLong("updated"))
                            .orElse(Option.of(meta.getLong("created")))
                            .map(timestamp -> {
                                final long diff = now - timestamp;
                                return age < diff;
                            })
                            .getOrElse(false);
                }).data();
                this.removeBatch(data);
            }));
        }
    }

    public String name() {
        return name;
    }

    Collection setAdaptiveBinaryIndices(boolean adaptiveBinaryIndices) {
        this.adaptiveBinaryIndices = adaptiveBinaryIndices;
        return this;
    }

    Collection setTransactional(boolean transactional) {
        this.transactional = Transactional.build(transactional);
        return this;
    }

    Collection setAsyncListeners(boolean asyncListeners) {
        this.asyncListeners = asyncListeners;
        return this;
    }

    Collection setCloneObjects(boolean cloneObjects) {
        this.cloneObjects = cloneObjects;
        return this;
    }

    Collection setAutoupdate(boolean autoupdate) {
        this.autoupdate = autoupdate;
        return this;
    }

    Collection setChanges(Changes changes) {
        this.changes = changes;
        return this;
    }

    Collection setDirtyIds(List<Long> dirtyIds) {
        this.dirtyIds = dirtyIds;
        return this;
    }

    Collection setDirty(boolean dirty) {
        this.dirty = dirty;
        return this;
    }

    Collection setMaxId(Long maxId) {
        this.maxId = new AtomicLong(maxId);
        return this;
    }

    Collection setBinaryIndices(List<Index> binaryIndices) {
        this.binaryIndices = binaryIndices;
        return this;
    }

    Collection setUniqueNames(List<String> uniqueNames) {
        this.uniqueNames = uniqueNames;
        return this;
    }


    List<Index> getBinaryIndices() {
        return binaryIndices;
    }

    List<Long> getDirtyIds() {
        return dirtyIds;
    }

    List<Long> getIdIndex() {
        return idIndex;
    }


    Collection setData(List<JsonObject> data) {
        this.data = data;
        return this;
    }

    Collection setIdIndex(List<Long> data) {
        this.idIndex = data;
        return this;
    }

    public Set<Transform> transforms() {
        return transforms;
    }

    public void addTransform(String name, List<JsonObject> actions) {
        Objects.requireNonNull(name);
        this.transforms = this.transforms
                .filter(t -> !name.equals(t.name))
                .add(new Transform(name, actions));
    }

    public Option<Transform> getTransform(String name) {
        return this.transforms.find(t -> t.name.equals(name));
    }

    public void removeTransform(String name) {
        this.transforms = this.transforms.filter(t -> !t.name.equals(name));
    }

    public void clearTransform() {
        this.transforms = this.transforms.filter(t -> !t.name.equals(name));
    }

    public void setTransform(String name, List<JsonObject> actions) {
        this.transforms = this.transforms.filter(t -> !t.name.equals(name))
                .add(new Transform(name, actions));
    }

    Collection setTransforms(Set<Transform> transforms) {
        this.transforms = transforms;
        return this;
    }

    synchronized void addAutoUpdateObserver(JsonObject collObj) {
        // TODO
        //data = data.append(collObj);
    }

    public List<Try<JsonObject>> update(List<JsonObject> doc) {
        if (Objects.isNull(doc)) {
            return List.empty();
        }
        return doc.map(this::update);
    }

    public Try<JsonObject> update(JsonObject doc) {
        return Try.of(() -> {
            Objects.requireNonNull(doc.getLong(ID), "Trying to update unsynced document. Please save the document first by using insert() or addMany()");
            return doc;
        }).flatMap(d ->
                Try.of(() -> {
                            final Long id = doc.getLong(ID);
                            transactional.startTransaction(this);
                            Tuple2<JsonObject, Integer> rt = returnPosition(id)
                                    .getOrElseThrow(() -> new IllegalArgumentException("Trying to update a document not in collection."));
                            JsonObject oldInternal = rt._1;
                            int position = rt._2;
                            JsonObject newInternal = (cloneObjects) ? doc.copy() : doc;
                            emit(preUpdate, doc);
                            this.uniqueNames.forEach(n -> getUniqueIndex(n, true).peek(u -> u.update(oldInternal, newInternal)));
                            data = data.replace(oldInternal, newInternal);
                            dynamicViews.forEach(DynamicView::evaluateDocument);

                            flagBinaryIndexesDirty();
                            idIndex = idIndex.zipWithIndex().map(t -> {
                                if (t._2.equals(position)) {
                                    return id;
                                }
                                return t._1;
                            });
                            if (this.isIncremental) {
                                this.dirtyIds = this.dirtyIds.append(id);
                            }
                            transactional.commit(this);
                            dirty = true;
                            if (options.isDisableChangesApi()) {
                                updateMeta(newInternal);
                            } else {
                                updateMetaWithChange(newInternal, oldInternal);
                            }
                            emit(update, new JsonObject().put("returnObj", newInternal).put("oldInternal", oldInternal));
                            return (cloneObjects) ? newInternal.copy() : newInternal;
                        })
                        .onFailure(onFailureTransactional())
        );
    }


    void appendAll(List<JsonObject> objs) {
        this.data = this.data.appendAll(objs);
    }

    public Try<JsonObject> add(JsonObject obj) {

        return Try.of(() -> {
                    Objects.requireNonNull(obj, "object null in adding data");
                    if (obj.containsKey(ID)) {
                        throw new IllegalStateException("Document is already in collection, please use update();" + obj.getInteger(ID));
                    }
                    transactional.startTransaction(this);
                    final long newId = this.maxId.incrementAndGet();
                    obj.put(ID, newId);
                    if (!this.options.isDisableMeta()) {
                        if (!obj.containsKey("meta")) {
                            obj.put("meta", new JsonObject().put("revision", 0).put("created", 0));
                        }
                    }
                    uniqueNames.forEach(n -> getUniqueIndex(n, true)
                            .peek(d -> d.set(obj))
                    );
                    this.idIndex = this.idIndex.append(newId);
                    if (this.isIncremental) {
                        this.dirtyIds = this.dirtyIds.append(newId);
                    }

                    if (this.options.isDisableChangesApi()) {
                        this.insertMeta(obj);
                    } else {
                        this.insertMetaWithChange(obj);
                    }
                    synchronized (this) {
                        this.data = this.data.append(obj);
                    }

                    Future.sequence(dynamicViews.map(DynamicView::evaluateDocument)).await();

                    this.flagBinaryIndexesDirty();
                    transactional.commit(this);
                    this.dirty = true;
                    return (this.cloneObjects) ? obj.copy() : obj;
                })
                .onFailure(onFailureTransactional());

    }

    public Try<JsonObject> remove(JsonObject doc) {
        return Try.of(() -> {
                    Objects.requireNonNull(doc, "document must not be null");
                    if (!doc.containsKey(ID)) {
                        throw new IllegalStateException("Object is not a document stored in the collection");
                    }
                    transactional.startTransaction(this);
                    final Long idLoki = doc.getLong(ID);
                    final Tuple2<JsonObject, Integer> tuple = returnPosition(idLoki)
                            .getOrElseThrow(() -> new IllegalStateException("doc '" + doc.encode() + "' not found."));
                    final Integer index = tuple._2;
                    this.constraints.removeIndex(idLoki, tuple._1);
                    dynamicViews.forEach(DynamicView::evaluateDocument);

                    this.data = this.data.removeAt(index);
                    this.idIndex = this.idIndex.removeAt(index);
                    if (this.isIncremental) {
                        this.dirtyIds = this.dirtyIds.append(idLoki);
                    }
                    return tuple._1;
                }).onSuccess(obj -> {
                    transactional.commit(this);
                    this.dirty = true;
                    this.emit(delete, obj);
                })
                .onFailure(onFailureTransactional());

    }

    private Consumer<Throwable> onFailureTransactional() {
        return ex -> {
            transactional.rollback(this);
            this.emit(error, JsonObject.mapFrom(ex));
        };
    }

    public Try<JsonObject> remove(Integer index) {
        return Try.of(() -> {
            Objects.requireNonNull(index, "index must not be null");
            return data.get(index);
        }).flatMap(this::remove);
    }

    public Try<List<JsonObject>> removeBatch(List<JsonObject> objs) {
        return Try.of(() -> objs.flatMap(o -> extractIdLoki(o)))
                .flatMap(this::removeBatchById);
    }

    public Try<List<JsonObject>> removeBatchById(List<Long> idLokis) {
        return Try.of(() -> data.zipWithIndex().toMap(t -> Tuple.of(t._1.getLong(ID), t._2)))
                .mapTry(xlt -> idLokis.map(i -> xlt.get(i)
                        .getOrElseThrow(() -> new IllegalArgumentException("id " + i + " not found.")))
                )
                .flatMap(this::removeBatchByPositions);
    }

    Try<List<JsonObject>> removeBatchByPositions(List<Integer> positions) {
        return Try.of(() -> {
            transactional.startTransaction(this);
            this.ensureId();
            final Map<Long, Boolean> xo = positions.map(p -> idIndex.get(p))
                    .toMap(i -> Tuple.of(i, false));
            // if we will need to notify dynamic views and/or binary indices to update themselves...


            final Predicate<JsonObject> filterPrediacte = d -> xo.get(d.getLong(ID)).getOrElse(true);
            List<JsonObject> results = data.filter(filterPrediacte.negate());
            this.data = this.data.filter(filterPrediacte);
            dynamicViews.forEach(DynamicView::evaluateDocument);
            flagBinaryIndexesDirty();
            constraints.removeIndex(results);

            if (this.isIncremental) {
                this.dirtyIds = this.dirtyIds.appendAll(xo.map(e -> e._1));
            }
            this.idIndex = this.idIndex.filter(i -> xo.get(i).getOrElse(true));
            transactional.commit(this);
            this.dirty = true;
            return results;
        }).onFailure(onFailureTransactional());
    }

    private void ensureId() {
        if (Objects.isNull(idIndex) || idIndex.isEmpty()) {
            this.idIndex = this.data.map(o -> o.getLong(ID));
        }
    }

    private void flagBinaryIndexesDirty() {
        this.ensureAllIndexes();
        binaryIndices.forEach(index -> index.dirty = true);
    }

    public Option<UniqueIndex> getUniqueIndex(String field, boolean force) {
        return constraints.getUniqueIndex(field)
                .orElse(() -> {
                    if (force) {
                        return Option.of(ensureUniqueIndex(field));
                    }
                    return Option.none();
                });
    }

    public UniqueIndex ensureUniqueIndex(String field) {
        return constraints.getUniqueIndex(field)
                .peek(ui -> {
                    if (this.uniqueNames.find(s -> s.equals(field)).isEmpty()) {
                        uniqueNames = uniqueNames.append(field);
                    }
                })
                .getOrElse(() -> {
                    if (this.uniqueNames.find(s -> s.equals(field)).isEmpty()) {
                        uniqueNames = uniqueNames.append(field);
                    }
                    final UniqueIndex uniqueIndex = this.constraints.pushUniqueIndex(field);
                    data.forEach(uniqueIndex::set);
                    return uniqueIndex;
                });
    }

    public void ensureExact(String property) {
        constraints.pushExactIndex(property);
    }

    public void clearConstraints() {
        constraints = new Constraints();
    }

    public void clearIndex() {
        binaryIndices = List.empty();
    }

    public void ensureIndex(String property) {
        Objects.requireNonNull(property, "Attempting to set index without an associated property");
        if (binaryIndices.find(b -> b.name.equals(property))
                .filter(b -> !b.dirty).isDefined()) {
            return;
        }
        final List<Integer> values = List.ofAll(IntStream.range(0, data.size()).boxed());
        final Index index = new Index(property, true, values);
        binaryIndices = binaryIndices.append(index);
        final boolean propPath = property.contains(".");
        index.values = index.values.sorted((a, b) -> {
            Object val1, val2;
            if (propPath) {
                val1 = Commons.getIn(data.get(a), property).getOrNull();
                val2 = Commons.getIn(data.get(b), property).getOrNull();
            } else {
                val1 = Option.of(data.get(a)).map(o -> o.getValue(property)).getOrNull();
                val2 = Option.of(data.get(b)).map(o -> o.getValue(property)).getOrNull();
            }
            if (Comparators.ltHelper(val1, val2, () -> false)) {
                return -1;
            }
            if (Comparators.gtHelper(val1, val2, () -> false)) {
                return 1;
            }
            return 0;
        });
        index.dirty = false;
        dirty = true;
    }

    public void ensureAllIndexes() {
        binaryIndices.forEach(i -> ensureIndex(i.name));
    }

    public Option<JsonObject> insert(JsonObject obj) {
        return insertOne(obj, false);
    }

    public Try<List<JsonObject>> insert(List<JsonObject> docs) {
        return this.insert(docs, false);
    }

    Try<List<JsonObject>> insert(List<JsonObject> docs, boolean overrideAdaptiveIndices) {
        return Try.of(() -> Objects.requireNonNull(docs))
                .mapTry(l -> {
                    if (adaptiveBinaryIndices) {
                        this.adaptiveBinaryIndices = false;
                    }
                    this.emit(preInsert, new JsonObject()
                            .put("datas", new JsonArray(docs.toJavaList()))
                    );
                    final List<JsonObject> results = docs
                            .flatMap(d -> insertOne(d, true))
                            .map(d -> {
                                if (cloneObjects) {
                                    return d.copy();
                                }
                                return d;
                            });
                    this.emit(insert, new JsonObject()
                            .put("datas", new JsonArray(results.toJavaList()))
                    );
                    return results;
                }).onSuccess(r -> {
                    if (adaptiveBinaryIndices) {
                        this.ensureAllIndexes();
                        this.adaptiveBinaryIndices = true;
                    }
                }).onFailure(e -> {
                    LOG.error("error", e);
                    if (adaptiveBinaryIndices) {
                        this.ensureAllIndexes();
                        this.adaptiveBinaryIndices = true;
                    }
                });
    }

    Option<JsonObject> insertOne(JsonObject doc, boolean bulkInsert) {
        return Try.of(() -> {
                    Objects.requireNonNull(doc, "Object cannot be null");
                    JsonObject obj = (cloneObjects) ? doc.copy() : doc;
                    if (!options.isDisableMeta()) {
                        if (!doc.containsKey("meta")) {
                            obj.put("meta", new JsonObject().put("revision", 0).put("created", 0));
                        }
                    }
                    if (!bulkInsert) {
                        this.emit(preInsert, new JsonObject()
                                .put("datas", obj)
                        );
                    }
                    return obj;
                })
                .flatMap(this::add)
                .mapTry(obj -> {
                    if (this.options.isDisableChangesApi()) {
                        this.insertMeta(obj);
                    } else {
                        this.insertMetaWithChange(obj);
                    }
                    JsonObject returnObj = (cloneObjects) ? obj.copy() : obj;
                    if (!bulkInsert) {
                        this.emit(insert, new JsonObject()
                                .put("datas", returnObj)
                        );
                    }
                    this.addAutoUpdateObserver(returnObj);
                    return returnObj;
                })
                .onFailure(ex -> {
                    LOG.error("insertOne " + doc, ex);
                    this.emit(error, new JsonObject().put("message", ex.getMessage()));
                })
                .toOption();
    }

    private void insertMetaWithChange(JsonObject obj) {
        insertMeta(obj);
        changes.createInsertChange(this.name, obj);
    }

    private void updateMetaWithChange(JsonObject obj, JsonObject old) {
        updateMeta(obj);
        changes.createUpdateChange(this.name, obj, old);
    }

    JsonObject insertMeta(JsonObject obj) {
        if (Objects.isNull(obj) || this.options.isDisableMeta()) {
            return obj;
        }
        JsonObject meta = obj.getJsonObject("meta", new JsonObject());
        obj.put("meta", meta
                .put("created", new Date().getTime())
                .put("revision", 0));
        return obj;
    }

    private JsonObject updateMeta(JsonObject obj) {
        if (Objects.isNull(obj) || this.options.isDisableMeta()) {
            return obj;
        }
        JsonObject meta = obj.getJsonObject("meta", new JsonObject());
        int revision = meta.getInteger("revision", 0);
        obj.put("meta", meta
                .put("updated", new Date().getTime())
                .put("revision", revision + 1));
        return obj;
    }

    public Set<DynamicView> dynamicViews() {
        return dynamicViews;
    }

    public Option<DynamicView> getDynamicView(String name) {
        return dynamicViews.find(d -> d.name.equals(name));
    }

    public void addDynamicView(DynamicView dynamicView) {
        dynamicViews = dynamicViews.add(dynamicView);
    }

    public DynamicView addDynamicView(String name) {
        return addDynamicView(name, new DynamicViewOption());
    }

    public void removeDynamicView(String name) {
        this.dynamicViews = this.dynamicViews.filter(t -> !t.name.equals(name));
    }

    public DynamicView addDynamicView(String name, DynamicViewOption options) {
        final DynamicView dynamicView = new DynamicView(this, name, options);
        dynamicViews = dynamicViews.add(dynamicView);
        return dynamicView;
    }

    //access Method
    public List<JsonObject> data() {
        return data;
    }

    public Option<JsonObject> get(Long id) {
        this.ensureId();
        return Option.of(idIndex.indexOf(id))
                .filter(i -> i >= 0)
                .flatMap(index -> Option.of(data.get(index)));
    }

    public Option<JsonObject> findOne(JsonObject obj) {
        return find(obj).headOption();
    }


    public List<JsonObject> find(JsonObject obj) {
        return chain().find(obj).data();
    }

    public Option<JsonObject> findFirst(JsonObject obj) {
        return findOne(byExample(obj));
    }

    public List<JsonObject> findByQueries(JsonObject obj) {
        return find(byExample(obj));
    }

    public List<JsonObject> findByQueries(List<JsonObject> obj) {
        return find(new JsonObject()
                .put("$and", obj));
    }

    private JsonObject byExample(JsonObject obj) {
        return new JsonObject()
                .put("$and", List.ofAll(obj)
                        .map(e -> new JsonObject().put(e.getKey(), e.getValue())));
    }

    /**
     * Applies a 'mongo-like' find query object removes all documents which match that filter.
     *
     * @param {object} filterObject - 'mongo-like' query object
     * @memberof Collection
     */
    public void findAndRemove(JsonObject obj) {
        chain().find(obj).remove();
    }

    public List<JsonObject> where(Predicate<JsonObject> predicate) {
        return Option.of(predicate)
                .map(p -> data().filter(p))
                .getOrElse(List.empty());
    }

    public Option<Tuple2<JsonObject, Integer>> returnPosition(Long id) {
        this.ensureId();
        return Option.of(idIndex.indexOf(id))
                .filter(i -> i >= 0)
                .flatMap(index -> Option.of(data.get(index))
                        .map(d -> Tuple.of(d, index)));
    }

    public Option<JsonObject> by(String field, Object value) {
        Objects.requireNonNull(field, "field must not be null");
        return this.getUniqueIndex(field, true)
                .flatMap(ui -> ui.get(value));
    }

    public Resultset chain() {
        return new Resultset(this);
    }

    public Resultset chain(String transform) {
        return chain().transform(transform);
    }


    public int count() {
        return data.size();
    }

    // StaticticMethod
    public Option<Number> max(String field) {
        return Numbers.max(Numbers.extractNumerical(field, data));
    }

    public Option<Tuple2<Number, Integer>> maxRecord(String field) {
        if (data.isEmpty()) {
            return Option.none();
        }
        final boolean deep = isDeepProperty(field);
        if (data.size() == 1) {
            return deepProperty(data.get(0), field, deep)
                    .map(v -> Tuple.of((Number) v, 0));
        }
        return Option.some(
                data.zipWithIndex()
                        .flatMap(t -> deepProperty(t._1, field, deep)
                                .map(v -> Tuple.of((Number) v, t._2))
                        )
                        .reduce((a, b) -> {
                            final Number max = MAX.apply(a._1, b._1);
                            if (max.equals(a._1)) {
                                return a;
                            }
                            return b;
                        })
        );
    }

    public Option<Number> min(String field) {
        return Numbers.min(Numbers.extractNumerical(field, data));
    }

    public Option<Tuple2<Number, Integer>> minRecord(String field) {
        if (data.isEmpty()) {
            return Option.none();
        }
        final boolean deep = isDeepProperty(field);
        if (data.size() == 1) {
            return deepProperty(data.get(0), field, deep)
                    .map(v -> Tuple.of((Number) v, 0));
        }
        return Option.some(
                data.zipWithIndex()
                        .flatMap(t -> deepProperty(t._1, field, deep)
                                .map(v -> Tuple.of((Number) v, t._2))
                        )
                        .reduce((a, b) -> {
                            final Number max = MIN.apply(a._1, b._1);
                            if (max.equals(a._1)) {
                                return a;
                            }
                            return b;
                        })
        );
    }

    public double avg(String field) {
        return Numbers.average(Numbers.extractNumerical(field, data));
    }

    public double stdDev(String field) {
        return Numbers.standardDeviation(Numbers.extractNumerical(field, data));
    }

    public double median(String field) {
        return Numbers.median(Numbers.extractNumerical(field, data));
    }

    public Option<Object> mode(String field) {

        final HashMap<Object, Integer> dict = Commons.extract(field, data)
                .foldRight(HashMap.<Object, Integer>empty(), (o, map) -> {
                    final Integer value = map.get(o)
                            .map(i -> i + 1)
                            .getOrElse(1);
                    return map.put(o, value);
                });
        return List.ofAll(dict.values()).max()
                .flatMap(m -> dict.find(t -> m.equals(t._2)))
                .map(Tuple2::_1);
    }

    public List<Integer> prepareFullDocIndex() {
        return List.ofAll(IntStream.range(0, data.length()).boxed());

    }

    public JsonObject serialize() {
        return new JsonObject()
                .put("name", name)
                .put("dirty", dirty)
                .put("options", JsonObject.mapFrom(options))
                .put("isIncremental", isIncremental)
                .put("data", new JsonArray(data.toJavaList()))
                .put("idIndex", new JsonArray(idIndex.toJavaList()))
                .put("dirtyIds", new JsonArray(dirtyIds.toJavaList()))
                .put("uniqueNames", new JsonArray(uniqueNames.toJavaList()))
                .put("transforms", new JsonArray(transforms.map(Transform::to).toJavaList()))
                .put("binaryIndices", new JsonArray(binaryIndices.map(Index::to).toJavaList()))
                .put("dynamicViews", new JsonArray(dynamicViews.map(DynamicView::to).toJavaList()))
                .put("changes", changes.serialize())
                .put("constraints", constraints.serialize())
                .put("ttl", JsonObject.mapFrom(ttl));
    }

    public Collection copy() {
        final Collection collection = new Collection(infrastructure, name, options);
        collection.adaptiveBinaryIndices = adaptiveBinaryIndices;
        collection.asyncListeners = asyncListeners;
        collection.isIncremental = isIncremental;
        collection.cloneObjects = cloneObjects;
        collection.autoupdate = autoupdate;
        collection.dirty = dirty;
        collection.maxId = new AtomicLong(maxId.get());

        collection.idIndex = idIndex.map(Function.identity());
        collection.dirtyIds = dirtyIds.map(Function.identity());

        collection.binaryIndices = binaryIndices.map(Index::copy);


        collection.uniqueNames = uniqueNames.map(Function.identity());
        collection.transforms = transforms.map(Transform::copy);
        collection.data = data.map(JsonObject::copy);
        collection.constraints = constraints.copy();
        collection.dynamicViews = dynamicViews.map(dv -> dv.copy().apply(collection));
        collection.changes = changes.copy();
        collection.transactional = transactional.copy();
        collection.ttl = ttl.copy();

        return collection;
    }

    public Collection clearData() {
        data = List.empty();
        return this;
    }

    public Collection removeDataOnly() {
        removeBatch(data);
        return this;
    }


    public CollectionOptions options() {
        return options;
    }

    public boolean cloneObjects() {
        return cloneObjects;
    }


    public Long maxId() {
        return maxId.get();
    }

    public Ttl ttl() {
        return ttl;
    }

    public Constraints constraints() {
        return constraints;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public List<Index> binaryIndices() {
        return binaryIndices;
    }

    public List<Long> idIndex() {
        return idIndex;
    }


    public List<String> uniqueNames() {
        return uniqueNames;
    }
}
