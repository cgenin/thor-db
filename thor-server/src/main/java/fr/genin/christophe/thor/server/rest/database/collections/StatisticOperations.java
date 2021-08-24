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

import static fr.genin.christophe.thor.server.rest.OpenApis.*;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.GET;
import static io.quarkus.vertx.web.Route.HttpMethod.POST;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName/collections/:collectionName", produces = MediaType.APPLICATION_JSON)
public class StatisticOperations {

    @Inject
    DatabasePort databasePort;

    @Operation(
            summary = "Avg",
            description = "Avg  query for an specific property"
    )
    @Tags({@Tag(name = TAG_COLL), @Tag(name = TAG_STATS),})
    @Route(path = "/avg/:property", methods = GET)
    public Double list(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Param("property") String property) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c -> c.avg(property))
                .getOrElseThrow(notFound(databaseName, collectionName));
    }
}
