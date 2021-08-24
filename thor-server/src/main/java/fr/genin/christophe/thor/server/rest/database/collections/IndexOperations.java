package fr.genin.christophe.thor.server.rest.database.collections;

import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_COLL;
import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_INDEX;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName/collections/:collectionName", produces = MediaType.APPLICATION_JSON)
public class IndexOperations {

    @Inject
    DatabasePort databasePort;

    @Operation(
            summary = "Get id index",
            description = "Get id index"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_INDEX),})
    @Route(path = "/id-index", methods = GET)
    public List<Long> getIdIndex(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.idIndex().toJavaList())
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Add or update an index",
            description = "Add or update an index for an specific property"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_INDEX),})
    @Route(path = "/index/:property", methods = {POST, PUT})
    public Boolean ensureIndex(@Param("databaseName") String databaseName,
                               @Param("collectionName") String collectionName,
                               @Param("property") String property) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> {
                    c.ensureIndex(property);
                    return true;
                })
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Add or update an unique index",
            description = "Add or update an unique index for an specific property"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_INDEX),})
    @Route(path = "/unique-index/:property", methods = {POST, PUT})
    public Boolean ensureUniqueIndex(@Param("databaseName") String databaseName,
                                     @Param("collectionName") String collectionName,
                                     @Param("property") String property) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> {
                    c.ensureUniqueIndex(property);
                    return true;
                })
                .getOrElseThrow(notFound(databaseName, collectionName));
    }
}
