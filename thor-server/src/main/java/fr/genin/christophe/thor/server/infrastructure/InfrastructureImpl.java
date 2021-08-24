package fr.genin.christophe.thor.server.infrastructure;

import fr.genin.christophe.thor.core.Infrastructure;
import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import fr.genin.christophe.thor.core.options.ThorOptions;
import fr.genin.christophe.thor.server.config.TypeAdapter;
import fr.genin.christophe.thor.server.infrastructure.adapter.FsAdapter;
import fr.genin.christophe.thor.server.infrastructure.adapter.MemoryAdapter;
import io.vavr.concurrent.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@ApplicationScoped
public class InfrastructureImpl implements Infrastructure {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureImpl.class);

    @Inject
    Vertx vertx;

    @Inject
    ThorOptions thorOptions;


    private TypeAdapter typeAdapter;

    @Inject
    MemoryAdapter memoryAdapter;

    @Inject
    FsAdapter fsAdapter;

    @PostConstruct
    public void postConstruct() {
        typeAdapter = TypeAdapter.valueOf(thorOptions.getAdapterType());
        LOGGER.info("AdapterManager loaded");
    }



    @Override
    public Future<byte[]> loadDatabase(String filename) {
        switch (typeAdapter) {
            case fs:
                return fsAdapter.load(filename).map(Buffer::getBytes);
            case memory:
                return memoryAdapter.load(filename).map(Buffer::getBytes);
            default:
                return Future.failed(new IllegalArgumentException("type Adapter not known : " + typeAdapter));
        }
    }

    @Override
    public Long cancelTimer(Long autosaveHandle) {
        vertx.cancelTimer(autosaveHandle);
        return null;
    }

    @Override
    public Long setPeriodic(int autosaveInterval, Consumer<Long> fn) {
        return vertx.setPeriodic(autosaveInterval, fn::accept);
    }

    @Override
    public Future<Boolean> deleteDatabase(String filename) {
        switch (typeAdapter) {
            case fs:
                return fsAdapter.delete(filename);
            case memory:
                return memoryAdapter.delete(filename);
            default:
                return errorAdapter();
        }
    }

    @Override
    public Future<Boolean> save(FileToSave payload) {
        switch (typeAdapter) {
            case fs:
                return fsAdapter.save(payload);
            case memory:
                return memoryAdapter.save(payload);
            default:
                return errorAdapter();
        }
    }

    private Future<Boolean> errorAdapter() {
        return Future.failed(new IllegalArgumentException("type Adapter not known : " + typeAdapter));
    }
}
