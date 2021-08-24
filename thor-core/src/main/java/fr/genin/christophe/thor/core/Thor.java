package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.incremental.Changes;
import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.options.ThorOptions;
import fr.genin.christophe.thor.core.serialization.NormalSerialization;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Objects;

public class Thor {
    private final static Logger LOG = LoggerFactory.getLogger(Thor.class);
    public final String name;
    public final String filename;
    private final Infrastructure infrastructure;
    private List<Collection> collections = List.empty();
    public double databaseVersion = 1.5;
    public double engineVersion = 1.5;
    public boolean autosave = false;
    public int autosaveInterval = 5000;
    public Long autosaveHandle;
    private final ThorOptions options;

    public Thor(Infrastructure infrastructure, ThorOptions options) {

        this(infrastructure, options.getDefaultName(), options);

    }

    public Thor(Infrastructure infrastructure, String name, ThorOptions options) {
        this.infrastructure = infrastructure;
        this.name = name;
        this.filename = Paths.get(options.getDirectory(), name + "." + options.getExtensionFile()).toString();
        this.options = options;
        this.configureOptions();
    }

    private void configureOptions() {
        if (options.isAutoload()) {
            loadDatabase();
        }

        if (Objects.nonNull(options.getAutosaveInterval())) {
            this.autosaveDisable();
            this.autosaveInterval = this.options.getAutosaveInterval();
        }


        if (options.isAutosave()) {
            this.autosaveDisable();
            this.autosave = true;
            this.autosaveEnable();
        }

    }

    public ThorOptions options() {
        return options;
    }

    public void clearChanges() {
        collections
                .map(c -> c.changes)
                .forEach(Changes::flushChanges);
    }

    public void autosaveEnable() {
        this.autosaveHandle = infrastructure.setPeriodic(this.autosaveInterval, id -> {
            if (this.autosaveDirty()) {
                this.saveDatabase();
            }
            if (options.isVerbose()) {
                LOG.info("auto save enabled " + id);
            }
        });
    }


    private boolean autosaveDirty() {
        return false;
    }

    public void autosaveDisable() {
        if (Objects.nonNull(autosaveHandle)) {
            autosaveHandle = infrastructure.cancelTimer(autosaveHandle);
        }
    }

    public Try<Boolean> loadDatabase() {
        return infrastructure.loadDatabase(filename)
                .map(body -> {
                    this.loadJSON(body);
                    return true;
                })
                .onFailure(ex -> {
                    LOG.error("loadDatabase error", ex);
                    this.loadJSON(null);
                })
                .toTry();


    }

    public void loadJSON(byte[] serializedDb) {
        Serialization.from(this).deserialize(serializedDb);
    }

    private void autosaveClearFlags() {
        collections.forEach(c -> c.setDirty(false));
    }


    public Try<Boolean> deleteDatabase() {
        return infrastructure.deleteDatabase(filename)
                .onSuccess(b -> {
                    if (options.isVerbose()) {
                        LOG.info("Database Deleted");
                    }
                })
                .toTry();

    }

    public Try<Boolean> saveDatabase() {
        return Try.of(() -> {
                    this.autosaveClearFlags();
                    return this.serialize();
                })
                .flatMap(buffer -> infrastructure
                        .save(new FileToSave(filename, buffer))
                        .toTry()
                );
                    /*
                    vertx.eventBus().<String>request(Infrastructure.SAVE, payload,
                            new DeliveryOptions().setSendTimeout(10000L),
                            ar -> {
                                if (ar.succeeded()) {
                                    if (options.isVerbose()) {
                                        LOG.info("Database saved");
                                    }
                                    promise.success(true);
                                    return;
                                }
                                final Throwable ex = ar.cause();
                                LOG.error("error in saving json ", ex);
                                promise.failure(ex);
                            });
                    return promise;
                }).fold(Future::failed, Promise::future);

                     */
    }

    public byte[] serialize() {
        return Serialization.from(this).serialize();
    }


    public Collection addCollection(String name) {
        return addCollection(name, new CollectionOptions());
    }

    public Collection addCollection(String name, CollectionOptions options) {
        if (options.isDisableMeta()) {
            if (!options.isDisableChangesApi()) {
                throw new IllegalArgumentException("disableMeta option cannot be passed as true when disableChangesApi is passed as false");
            }
            if (!options.isDisableDeltaChangesApi()) {
                throw new IllegalArgumentException("disableMeta option cannot be passed as true when disableDeltaChangesApi is passed as false");
            }
            if (Objects.nonNull(options.getTtl()) && options.getTtl() > 0) {
                throw new IllegalArgumentException("disableMeta option cannot be passed as true when ttl is enabled");
            }
        }

        final Option<Collection> alredyExist = this.collections.find(c -> c.name().equals(name));
        if (alredyExist.isDefined()) {
            return alredyExist.get();
        }
        final Collection collection = new Collection(infrastructure, name, options);

        this.collections = this.collections.append(collection);
        return collection;
    }


    public List<Collection> collections() {
        return collections;
    }

    public void removeCollection(String collectionName) {
        collections = collections.removeFirst(c -> c.name().equals(collectionName));
    }

    public Option<Collection> getCollection(String collectionName) {
        return collections.find(c -> c.name().equals(collectionName));
    }

    public void loadCollection(Collection collection) {
        collections = collections.append(collection);
    }


    public Thor copy() {
        return copy(false);
    }

    public Thor copy(boolean removeNonSerializable) {
        final Thor databaseCopy = new Thor(infrastructure, filename, new ThorOptions());
        final JsonObject t = JsonObject.mapFrom(this);
        new NormalSerialization(databaseCopy).loadJSONObject(t, true);
        if (removeNonSerializable) {
            databaseCopy.autosaveHandle = null;
            databaseCopy.collections.forEach(c -> {
                c.constraints.clear();
                c.ttl = new Ttl();
            });
        }
        return databaseCopy;
    }

    public Option<Collection> renameCollection(String oldName, String newName) {
        Objects.requireNonNull(newName);
        return this.getCollection(oldName)
                .peek(c -> c.name = newName);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thor thor = (Thor) o;
        return Objects.equals(name, thor.name) && Objects.equals(filename, thor.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, filename);
    }
}
