package fr.genin.christophe.thor.server.rest.database.collections;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Resultset;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.server.dto.CollectionDto;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vavr.Value;
import io.vavr.control.Option;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_COLL;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName", produces = MediaType.APPLICATION_JSON)
public class Collections {

    @Inject
    DatabasePort databasePort;

    @Operation(summary = "List",
            description = "List collections of an datbase")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections", methods = GET)
    public List<CollectionDto> listByDatabase(@Param("databaseName") String databaseName) {
        final Thor thor = databasePort.get(databaseName).getOrElseThrow(notFound(databaseName));
        return thor.collections().map(CollectionDto::new).toJavaList();
    }


    @Operation(summary = "Get",
            description = "Find an collection by name")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName", methods = GET)
    public CollectionDto getByName(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(CollectionDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Add",
            description = "Add an collection")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName", methods = POST)
    public CollectionDto create(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName, @Body CollectionOptions collectionOptions) {
        final Thor thor = databasePort.get(databaseName).getOrElseThrow(notFound(databaseName));
        final CollectionOptions withCollectionOPtions = Option.of(collectionOptions).getOrElse(CollectionOptions::new);
        final Collection collection = thor.addCollection(collectionName, withCollectionOPtions);
        return new CollectionDto(collection);
    }

    @Operation(summary = "Delete",
            description = "Remove an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName", methods = DELETE)
    public Boolean delete(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        final Thor thor = databasePort.get(databaseName).getOrElseThrow(notFound(databaseName));
        thor.removeCollection(collectionName);
        return true;
    }

    @Operation(summary = "Get all datas",
            description = "get all data from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/data", methods = GET)
    public List<JsonObject> getDataByName(@Param("databaseName") String databaseName,
                                          @Param("collectionName") String collectionName,
                                          @Param("limit") Integer limit,
                                          @Param("skip") Integer skip) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(collection -> {
                    boolean hasLimitOrSkip = Objects.nonNull(limit) || Objects.nonNull(skip);
                    if (hasLimitOrSkip) {
                        final Resultset resultset = collection.chain();
                        if (Objects.nonNull(skip))
                            resultset.offset(skip);
                        if (Objects.nonNull(limit))
                            resultset.offset(limit);
                        return resultset.data();
                    }
                    return collection.data();
                })
                .map(Value::toJavaList)
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Count",
            description = "Count all data from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/count", methods = GET)
    public Integer count(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::count)
                .getOrElse(0);
    }

    @Operation(summary = "add a new data",
            description = "Add data to an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/data", methods = POST)
    public JsonObject addData(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName, @Body JsonObject body) {
        return databasePort.getCollection(databaseName, collectionName)
                .toTry()
                .flatMap(c -> c.add(body))
                .getOrElseThrow(e -> {
                    if (e instanceof RuntimeException) {
                        return (RuntimeException) e;
                    }
                    return new IllegalStateException("createData  error", e);
                });
    }

    @Operation(summary = "Insert array of data",
            description = "Insert array of data to an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/insert", methods = POST)
    public List<JsonObject> insert(@Param("databaseName") String databaseName,
                                   @Param("collectionName") String collectionName,
                                   @Body JsonArray body) {
        return databasePort.getCollection(databaseName, collectionName)
                .toTry()
                .flatMap(c -> c.insert(
                        io.vavr.collection.List.ofAll(body)
                            .map(Commons.TO_JSON_OBJECT)
                        )
                        .map(Value::toJavaList)
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "clears datas",
            description = "Clear all data from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/data", methods = DELETE)
    public List<JsonObject> clearData(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::removeDataOnly)
                .map(c -> new ArrayList<JsonObject>())
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Get an data object",
            description = "Get an data object by id from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/data/:idLoki", methods = GET)
    public JsonObject get(@Param("databaseName") String databaseName,
                          @Param("collectionName") String collectionName,
                          @Param("idLoki") Long idLoki
    ) {
        return databasePort.getCollection(databaseName, collectionName)
                .flatMap(c -> c.get(idLoki))
                .getOrElseThrow(notFound(databaseName, collectionName, idLoki + ""));
    }


    @Operation(summary = "Get binary indices",
            description = "Get binary indices from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/binaryIndices", methods = GET)
    public List<String> binaryIndices(@Param("databaseName") String databaseName,
                                      @Param("collectionName") String collectionName
    ) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.binaryIndices()
                        .map(b -> b.name)
                        .toJavaList()
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Get clone objects property",
            description = "Get clone objects property from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/cloneObjects", methods = GET)
    public Boolean cloneObjects(@Param("databaseName") String databaseName,
                                @Param("collectionName") String collectionName
    ) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::cloneObjects)
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Get isIncremental property",
            description = "Get isIncremental property from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/isIncremental", methods = GET)
    public Boolean isIncremental(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName
    ) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::isIncremental)
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(summary = "Get maxId property",
            description = "Get maxId property from an collection.")
    @Tags(@Tag(name = TAG_COLL))
    @Route(path = "/collections/:collectionName/maxId", methods = GET)
    public Long maxId(@Param("databaseName") String databaseName,
                      @Param("collectionName") String collectionName
    ) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::maxId)
                .getOrElseThrow(notFound(databaseName, collectionName));
    }
}
