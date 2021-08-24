package fr.genin.christophe.thor.server.infrastructure.adapter;

import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Singleton
public class FsAdapter implements Adapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsAdapter.class);


    @Inject
    Vertx vertx;

    private Set<String> files = HashSet.empty();
    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    @PostConstruct
    public void postConstruct() {
        LOGGER.info("FsAdapter loaded");
    }

    private synchronized void add(String f) {
        files = files.add(f);
    }

    private synchronized void remove(String f) {
        files = files.remove(f);
    }

    public Future<Buffer> load(String filename) {
        return Future.of(() -> Files.readString(Path.of(filename), StandardCharsets.UTF_8))
                .map(Buffer::buffer)
                .recoverWith(ex -> {
                    if (ex instanceof IOException) {
                        return Future.failed(new IllegalArgumentException("Error in loading " + filename, ex));
                    }
                    return Future.failed(ex);
                });

    }

    public Future<Boolean> save(FileToSave payload) {
        final String filename = payload.filename;
        return Future.of(() -> {
            final Path path = Path.of(filename);
            Files.write(path, payload.content,
                    StandardOpenOption.CREATE_NEW);
            return filename;
        }).onSuccess(this::add)
                .map(s -> true);
    }

    public Future<Boolean> delete(String filename) {
        return Future.of(() -> {
            final Path path = Path.of(filename);
            final boolean result = Files.deleteIfExists(path);
            LOGGER.debug("file " + filename + " deleted : " + result);
            return true;
        }).onSuccess(b -> remove(filename));
    }
}
