package fr.genin.christophe.thor.server.infrastructure.adapter;

import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Try;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Singleton
public class MemoryAdapter implements Adapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryAdapter.class);

    private Map<String, Buffer> databases = HashMap.empty();

    @Inject
    Vertx vertx;
    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);


    @PostConstruct
    public void postConstruct() {
        LOGGER.info("MemoryAdapter loaded");
    }

    private synchronized void put(String name, Buffer data) {
        databases = databases.put(name, data);
    }

    private synchronized void remove(String name) {
        databases = databases.remove(name);
    }

    public Future<Buffer> load(String filename) {
        final Promise<Buffer> promise = Promise.make(executor);
        vertx.executeBlocking(p -> {
            Try.of(() -> databases.find(t -> t._1.equals(filename))
                    .getOrElseThrow(() -> new IllegalArgumentException("Database " + filename + " not found."))
            ).fold(e->{
                promise.failure(e);
                p.fail(e);
                return true;
            }, t -> {
                promise.success(t._2);
                p.complete(t._2);
                return true;
            });
        }, r -> {
            System.out.println("memory:"+r);
        });
        return promise.future();
    }

    public Future<Boolean> save(FileToSave payload) {
        return Future.of(() -> {
            final String filename = payload.filename;
            final byte[] buffer = payload.content;
            put(filename, Buffer.buffer(buffer));
            return true;
        });


    }

    public Future<Boolean> delete(String filename) {
        return Future.of(() -> {
            remove(filename);
            return true;
        });
    }
}
