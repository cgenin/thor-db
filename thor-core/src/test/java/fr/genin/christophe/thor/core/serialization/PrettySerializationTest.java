package fr.genin.christophe.thor.core.serialization;


import fr.genin.christophe.thor.core.AbstractSerializationTest;
import fr.genin.christophe.thor.core.Serialization;
import fr.genin.christophe.thor.core.Thor;

public class PrettySerializationTest extends AbstractSerializationTest {


    @Override
    protected Serialization getSerializer(Thor thor) {
        return new PrettySerialization(thor);
    }
}
