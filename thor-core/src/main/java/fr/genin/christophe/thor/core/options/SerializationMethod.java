package fr.genin.christophe.thor.core.options;

import io.vavr.collection.List;
import io.vavr.control.Option;

public enum SerializationMethod {
    normal, pretty, avro;

    public static SerializationMethod parse(String value) {
        return Option.of(value)
                .flatMap(v -> List.of(values())
                        .find(sm -> sm.name().equals(v))
                )
                .getOrElse(normal);
    }
    }
