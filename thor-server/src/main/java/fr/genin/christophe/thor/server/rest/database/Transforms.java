package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.server.dto.TransformDto;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_TR;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName/collections/:collectionName", produces = MediaType.APPLICATION_JSON)
public class Transforms {

    @Inject
    DatabasePort databasePort;

    @Operation(
            summary = "List",
            description = "List all registered map's operations"
    )
    @Tag(name = TAG_TR)
    @Route(path = "/transforms", methods = GET)
    public List<TransformDto> list(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::transforms)
                .map(l -> l.map(TransformDto::new)
                        .toJavaList()
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "Create",
            description = "Create an registered map's operations"
    )
    @Tag(name = TAG_TR)
    @Route(path = "/transforms", methods = POST)
    public Boolean create(@Param("databaseName") String databaseName,
                               @Param("collectionName") String collectionName,
                               @Body @Valid TransformDto transform) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c-> {
                    c.addTransform(transform.name, io.vavr.collection.List.ofAll(transform.operations)
                            .map(JsonObject::new)
                    );
                    return true;
                })
                .getOrElseThrow(notFound(databaseName, collectionName));
    }
    @Operation(
            summary = "Delete",
            description = "Delete an registered map's operations"
    )
    @Tag(name = TAG_TR)
    @Route(path = "/transforms/:transformName", methods = DELETE)
    public Boolean delete(@Param("databaseName") String databaseName,
                               @Param("collectionName") String collectionName,
                               @Param("transformName") String transformName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(c-> {
                    c.removeTransform(transformName);
                    return true;
                })
                .getOrElseThrow(notFound(databaseName, collectionName));
    }


}
