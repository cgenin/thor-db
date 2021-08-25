package fr.genin.christophe.thor.server.rest;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import java.util.NoSuchElementException;

@ApplicationScoped
@RouteBase(path = "/api", produces = MediaType.APPLICATION_JSON)
public class ExceptionHandler {

    @Route(path = "/*", type = Route.HandlerType.FAILURE, order = 1)
    public void illegalArgument(IllegalArgumentException e, HttpServerResponse response) {
        response.setStatusCode(HttpStatus.SC_BAD_REQUEST).end(new JsonObject().put("error", e.getMessage()).encode());
    }

    @Route(path = "/*", type = Route.HandlerType.FAILURE, order = 2)
    public void notFound(NotFoundException e, HttpServerResponse response) {
        response.setStatusCode(HttpStatus.SC_NOT_FOUND).end(new JsonObject().put("error", e.getMessage()).encode());
    }

    @Route(path = "/*", type = Route.HandlerType.FAILURE, order = 3)
    public void noSuchElement(NoSuchElementException e, HttpServerResponse response) {
        response.setStatusCode(HttpStatus.SC_NOT_FOUND).end(new JsonObject().put("error", e.getMessage()).encode());
    }
}
