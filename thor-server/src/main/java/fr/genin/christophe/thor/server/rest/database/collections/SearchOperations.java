package fr.genin.christophe.thor.server.rest.database.collections;

import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_COLL;
import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_SEARCH;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName/collections/:collectionName", produces = MediaType.APPLICATION_JSON)
public class SearchOperations {

    @Inject
    DatabasePort databasePort;

    @Operation(
            summary = "Find",
            description = "Find data from  query"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/find", methods = POST)
    public List<JsonObject> find(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Body JsonObject body) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.find(body).toJavaList())
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Find first",
            description = "Find first object from  query"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/find-first", methods = POST)
    public JsonObject findFirst(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Body JsonObject body) {
        return databasePort.getCollection(databaseName, collectionName)
                .flatMap(c -> c.findFirst(body))
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Find by multiple queries",
            description = "Find by multiple queries (array body) from  query"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/find-queries", methods = POST)
    public List<JsonObject> findQueries(@Param("databaseName") String databaseName,
                                @Param("collectionName") String collectionName,
                                @Body List<JsonObject> body) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.findByQueries(io.vavr.collection.List.ofAll(body))
                        .toJavaList()
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Find by multiple queries",
            description = "Find by multiple queries (object body) from  query"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/find-queries-object", methods = POST)
    public List<JsonObject> findQueriesObject(@Param("databaseName") String databaseName,
                                        @Param("collectionName") String collectionName,
                                        @Body JsonObject body) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.findByQueries(body)
                        .toJavaList()
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Find and remove",
            description = "Find and remove from  query"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/find", methods = DELETE)
    public Boolean findAndRemove(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Body JsonObject body) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> {
                    c.findAndRemove(body);
                    return true;
                })
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "By Unique Index",
            description = "Find an object by unique index"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/by/:property/:value", methods = GET)
    public JsonObject by(@Param("databaseName") String databaseName,
                         @Param("collectionName") String collectionName,
                         @Param("property") String property,
                         // TODO trouver une méthode pour définir un type Object dans une url
                         @Param("value") String value) {
        return databasePort.getCollection(databaseName, collectionName)
                .flatMap(c -> c.by(property, value))
                .getOrElseThrow(notFound(databaseName, collectionName, property, value+""));
    }


    @Operation(
            summary = "Get results from an specified transform",
            description = "Get results from an specified transform"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/chain/:transform/data", methods = GET)
    public List<JsonObject> chainTransformData(@Param("databaseName") String databaseName,
                         @Param("collectionName") String collectionName,
                         @Param("transform") String transform) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.chain(transform).data().toJavaList())
                .getOrElseThrow(notFound(databaseName, collectionName, transform));
    }

    @Operation(
            summary = "Remove from an specified transform",
            description = "Remove from an specified transform"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/chain/:transform", methods = DELETE)
    public List<JsonObject> chainTransformDelete(@Param("databaseName") String databaseName,
                                               @Param("collectionName") String collectionName,
                                               @Param("transform") String transform) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.chain(transform).remove().data().toJavaList())
                .getOrElseThrow(notFound(databaseName, collectionName, transform));
    }

    @Operation(
            summary = "Get number of results for an specified transform",
            description = "Get number of results for an specified transform"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_SEARCH),})
    @Route(path = "/chain/:transform/count", methods = GET)
    public Integer chainTransformCount(@Param("databaseName") String databaseName,
                                               @Param("collectionName") String collectionName,
                                               @Param("transform") String transform) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.chain(transform).count())
                .getOrElse(0);
    }
}
