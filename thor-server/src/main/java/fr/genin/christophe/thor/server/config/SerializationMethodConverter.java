package fr.genin.christophe.thor.server.config;

import fr.genin.christophe.thor.core.options.SerializationMethod;
import org.eclipse.microprofile.config.spi.Converter;

public class SerializationMethodConverter implements Converter<SerializationMethod> {

    @Override
    public SerializationMethod convert(String s) {
        return SerializationMethod.parse(s);
    }
}
