package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.infrastructure.FileToSave;
import io.vavr.concurrent.Future;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.function.Consumer;

public interface Infrastructure extends Serializable {

    Future<byte[]> loadDatabase(String filename) ;

    Long cancelTimer(Long autosaveHandle);

    Long setPeriodic(int autosaveInterval, Consumer<Long> fn);

    Future<Boolean> deleteDatabase(String filename);

    Future<Boolean> save(FileToSave payload);
}
