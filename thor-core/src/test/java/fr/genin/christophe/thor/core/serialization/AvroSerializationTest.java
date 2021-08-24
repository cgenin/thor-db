package fr.genin.christophe.thor.core.serialization;


import fr.genin.christophe.thor.core.AbstractSerializationTest;
import fr.genin.christophe.thor.core.Serialization;
import fr.genin.christophe.thor.core.Thor;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AvroSerializationTest extends AbstractSerializationTest {

    @Test
    public void should_get_avro_schema() {
        Schema dynamicViewSort = SchemaBuilder.record("AvroDynamicViewSort")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .requiredString("type")
                .requiredString("val")
                .requiredString("uid")
                .endRecord();

        Schema dynamicViewFilter = SchemaBuilder.record("AvroDynamicViewFilter")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .requiredString("type")
                .requiredString("val")
                .requiredString("uid")
                .endRecord();

        Schema dynamicView = SchemaBuilder.record("AvroDynamicView")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .requiredString("name")
                .name("filterPipeline")
                .type()
                .array()
                .items()
                .type(dynamicViewFilter)
                .arrayDefault(List.of())
                .endRecord();

        Schema transform = SchemaBuilder.record("AvroTransform")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .requiredString("name")
                .name("operations")
                .type()
                .array()
                .items()
                .stringType()
                .arrayDefault(List.of())
                .endRecord();
        Schema collection = SchemaBuilder.record("AvroCollection")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .requiredString("name")
                .name("data")
                .type()
                .array()
                .items()
                .stringType()
                .arrayDefault(List.of())
                .name("binaryIndices")
                .type()
                .array()
                .items()
                .stringType()
                .arrayDefault(List.of())

                .name("uniques")
                .type()
                .array()
                .items()
                .stringType()

                .arrayDefault(List.of())
                .name("exacts")
                .type()
                .array()
                .items()
                .stringType()
                .arrayDefault(List.of())

                .name("transforms")
                .type()
                .array()
                .items(transform)
                .arrayDefault(List.of())

                .name("dynamicViews")
                .type()
                .array()
                .items(dynamicView)
                .arrayDefault(List.of())


                .endRecord();

        Schema avroThor = SchemaBuilder.record("AvroThor")
                .namespace("fr.genin.christophe.thor.core.serialization.avro")
                .fields()
                .name("collections")
                .type()
                .array()
                .items(collection)
                .arrayDefault(List.of())
                .endRecord();
        System.out.println(avroThor.toString(true));
    }


    @Override
    protected Serialization getSerializer(Thor thor) {
        return new AvroSerialization(thor);
    }
}
