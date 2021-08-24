package fr.genin.christophe.thor.server.infrastructure;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Consumer;

@Singleton
public class DatabasePort {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePort.class);

    private Set<Thor> databases = HashSet.empty();


    @Inject
    ThorOptions thorOptions;

    @Inject
    InfrastructureImpl infrastructure;
    private Consumer<Boolean> initializeSucesssMessage = b -> LOGGER.info("Default db created or loaded : " + b);


    @PostConstruct()
    public void postConstruct() {

        if (databases.isEmpty()) {
            create();
        }
    }


    public Set<Thor> list() {
        return databases;
    }

    public Thor create(String name) {
        return create(name, thorOptions);
    }

    public Thor create(String name, ThorOptions withOptions) {
        if (databases.find(db -> db.name.equals(name)).isDefined()) {
            throw new IllegalArgumentException("Database already exist");
        }

        final Thor thor = new Thor(infrastructure, name, withOptions);
        addToList(thor);
        thor.saveDatabase();
        return thor;
    }

    public Thor update(String name) {
        final Thor thor = databases.find(db -> db.name.equals(name)).getOrElseThrow(() -> new IllegalArgumentException("Database not found"));
        thor.saveDatabase();
        return thor;
    }

    public Thor create() {
        final Thor thor = new Thor(infrastructure, thorOptions);
        addToList(thor);
        thor.loadDatabase()
                .onSuccess(initializeSucesssMessage)
                .onFailure(e -> {
                    if (thorOptions.isVerbose()) {
                        LOGGER.info("default db loading failed", e);
                    }
                    thor.saveDatabase()
                            .onSuccess(initializeSucesssMessage)
                            .onFailure(ex -> LOGGER.error("Error in creating default db ", ex));
                });

        return thor;
    }

    private void addToList(Thor thor) {
        databases = databases.add(thor);
    }


    public Try<Thor> load(String databaseName) {
        return Try.of(() -> Objects.requireNonNull(databaseName))
                .map(dn -> new Thor(infrastructure, dn, thorOptions))
                .flatMap(t -> t.loadDatabase()
                        .map(b -> t)
                        .toTry()
                )
                .onSuccess(this::addToList);

    }

    public Option<Thor> get(String name) {
        return databases.find(l -> l.name.equals(name));
    }

    public Option<Collection> getCollection(String dbName, String collectionName) {
        return Option.of(dbName)
                .flatMap(this::get)
                .flatMap(thor -> thor.getCollection(collectionName));
    }

    public void delete(Thor thor) {
        databases = databases.remove(thor);
    }
}
