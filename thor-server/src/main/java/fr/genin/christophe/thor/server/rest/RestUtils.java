package fr.genin.christophe.thor.server.rest;

import io.vavr.collection.List;

import javax.ws.rs.NotFoundException;
import java.util.function.Supplier;

public final class RestUtils {
    public static Supplier<NotFoundException> notFound(String databaseName) {
        return () -> new NotFoundException("Not found : " + databaseName);
    }

    public static Supplier<NotFoundException> notFound(String databaseName, String collectionName) {
        return () -> new NotFoundException("Not found : " + databaseName + " / " + collectionName);
    }


    public static Supplier<NotFoundException> notFound(String databaseName, String collectionName,
                                                       String... subs) {
        return () -> {
            final String message = "Not found : " + databaseName + " / " + collectionName + " " +
                    List.of(subs).foldRight("", (a, b) -> a + " / " + b);
            return new NotFoundException(message);
        };
    }
}
