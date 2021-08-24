package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.options.ThorOptions;
import fr.genin.christophe.thor.server.dto.ThorDto;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vavr.control.Option;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_DB;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;


@ApplicationScoped
@RouteBase(path = "/api", produces = MediaType.APPLICATION_JSON)
public class Databases {

    @Inject
    DatabasePort databasePort;

    @Inject
    ThorOptions thorOptions;

    @Operation(summary = "List",
            description = "List all active databases")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases", methods = GET)
    public List<ThorDto> list() {
        return databasePort.list()
                .map(ThorDto::new)
                .toJavaList();
    }

    @Operation(summary = "Get",
            description = "Get an specific database")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases/:databaseName", methods = GET)
    public ThorDto get(@Param("databaseName") String databaseName) {
        return databasePort.get(databaseName)
                .map(ThorDto::new)
                .getOrElseThrow(notFound(databaseName));
    }


    @Operation(summary = "Create",
            description = "Create and save an database")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases/:databaseName", methods = POST)
    public ThorDto create(@Body ThorOptions options, @Param("databaseName") String databaseName) {
        final ThorOptions opts = Option.of(options).getOrElse(thorOptions);
        final Thor thor = databasePort.create(databaseName, opts);
        return new ThorDto(thor);
    }

    @Operation(summary = "Save",
            description = "Save an database")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases/:databaseName", methods = PUT)
    public ThorDto update(@Param("databaseName") String databaseName) {
        final Thor thor = databasePort.update(databaseName);
        return new ThorDto(thor);
    }

    @Operation(summary = "Load",
            description = "Load an database")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases/:databaseName/load", methods = POST)
    public ThorDto load(@Param("databaseName") String databaseName) {
        return databasePort.load(databaseName)
                .map(ThorDto::new)
                .getOrElseThrow(e -> {
                    if (e instanceof RuntimeException) {
                        return (RuntimeException) e;
                    }
                    return new IllegalStateException("erreur", e);
                });
    }

    @Operation(summary = "Remove",
            description = "Remove an specific database")
    @Tags(@Tag(name = TAG_DB))
    @Route(path = "/databases/:databaseName", methods = DELETE)
    public ThorDto delete(@Param("databaseName") String databaseName) {
        final Thor thor = databasePort.get(databaseName).getOrElseThrow(notFound(databaseName));
        databasePort.delete(thor);
        return new ThorDto(thor);
    }


}
