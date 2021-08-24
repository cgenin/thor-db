package fr.genin.christophe.thor.core.event;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Consumer;

public class ThorEventEmitter {
    private final static Logger LOG = LoggerFactory.getLogger(ThorEventEmitter.class);


    private Map<String, List<Tuple2<String, Consumer<JsonObject>>>> events = HashMap.empty();

    public List<String> on(ThorEvent eventName, List<Consumer<JsonObject>> listeners) {
        return on(eventName.name(), listeners);
    }

    public List<String> on(String eventName, List<Consumer<JsonObject>> listeners) {
        return listeners.map(c -> this.on(eventName, c));
    }

    public String addListener(ThorEvent eventName, Consumer<JsonObject> listener) {
        return this.addListener(eventName.name(), listener);
    }

    public String addListener(String eventName, Consumer<JsonObject> listener) {
        return this.on(eventName, listener);
    }



    private synchronized String on(String eventName, Consumer<JsonObject> listener) {
        final String id = UUID.randomUUID().toString();
        final Tuple2<String, Consumer<JsonObject>> consumer = Tuple.of(id, listener);
        final List<Tuple2<String, Consumer<JsonObject>>> lists = events.get(eventName)
                .map(l -> l.append(consumer))
                .getOrElse(() -> List.of(consumer));
        events = events.put(eventName, lists);
        return id;
    }

    public  void removeListener(ThorEvent eventName, String id) {
        removeListener(eventName.toString(), id);
    }

    public synchronized void removeListener(String eventName, String id) {
        final List<Tuple2<String, Consumer<JsonObject>>> lists = events.get(eventName)
                .map(l -> l.filter(t -> !t._1.equals(id)))
                .getOrElse(List::of);
        events = events.put(eventName, lists);
    }

    public void emit(ThorEvent eventName, JsonObject data) {
        events.get(eventName.name())
                .peek(l -> l.forEach(c -> c._2.accept(data)))
                .onEmpty(() -> LOG.debug("eventName " + eventName + " not registered"));
    }


}
