package fr.genin.christophe.thor.core.serialization;


import fr.genin.christophe.thor.core.AbstractSerializationTest;
import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Serialization;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.serialization.AvroSerialization;
import fr.genin.christophe.thor.core.serialization.NormalSerialization;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalSerializationTest extends AbstractSerializationTest {


    @Override
    protected Serialization getSerializer(Thor thor) {
        return new NormalSerialization(thor);
    }
}
