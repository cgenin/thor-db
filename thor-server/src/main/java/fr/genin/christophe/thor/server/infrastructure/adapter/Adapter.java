package fr.genin.christophe.thor.server.infrastructure.adapter;

import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import io.vavr.concurrent.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public interface Adapter {
    Future<Buffer> load(String filename);

    Future<Boolean> save(FileToSave payload);

    Future<Boolean> delete(String filename);
}
