package fr.genin.christophe.thor.core.serialization;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.DynamicView;
import fr.genin.christophe.thor.core.Serialization;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewFilter;
import fr.genin.christophe.thor.core.options.DynamicViewOption;
import fr.genin.christophe.thor.core.serialization.avro.*;
import io.vavr.collection.List;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;

public class AvroSerialization extends AbstractSerialization {
    private static final Logger LOG = LoggerFactory.getLogger(AvroSerialization.class);


    public static final Function<String, CharSequence> TO_CHARSEQUENCE = s -> (CharSequence) s;

    public AvroSerialization(Thor thor) {
        super(thor);
    }

    @Override
    public byte[] serialize() {
        return Try.of(() -> {
            DatumWriter<AvroThor> writer = new SpecificDatumWriter<>(
                    AvroThor.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Encoder jsonEncoder = EncoderFactory.get().binaryEncoder(
                    stream, null);
            AvroThor avroThor = to(thor());
            writer.write(avroThor, jsonEncoder);
            jsonEncoder.flush();
            return stream.toByteArray();
        }).getOrElseGet(e -> {
            LOG.error("error in serializing " + thor().name, e);
            return new byte[0];
        });


    }

    private AvroThor to(Thor thor) {
        final AvroThor avroThor = new AvroThor();

        avroThor.setCollections(thor.collections().map(c -> {
            final AvroCollection avroCollection = new AvroCollection();
            avroCollection.setName(c.name());
            avroCollection.setData(c.data()
                    .map(JsonObject::encode)
                    .map(TO_CHARSEQUENCE)
                    .toJavaList());
            avroCollection.setBinaryIndices(c.binaryIndices()
                    .map(i -> i.name)
                    .map(TO_CHARSEQUENCE).toJavaList());
            avroCollection.setExacts(c.constraints().exacts
                    .map(i -> i.field)
                    .map(TO_CHARSEQUENCE)
                    .toJavaList()
            );
            avroCollection.setUniques(c.constraints().uniques
                    .map(i -> i.field)
                    .map(TO_CHARSEQUENCE)
                    .toJavaList()
            );
            avroCollection.setTransforms(c.transforms()
                    .map(i -> {
                        final AvroTransform avroTransform = new AvroTransform();
                        avroTransform.setName(i.name);
                        avroTransform.setOperations(i.operations
                                .map(JsonObject::encode)
                                .map(TO_CHARSEQUENCE)
                                .toJavaList());
                        return avroTransform;
                    })
                    .toJavaList()
            );
            avroCollection.setName(c.name());
            avroCollection.setDynamicViews(c.dynamicViews()
                    .map(dv -> {
                        final AvroDynamicView avroDynamicView = new AvroDynamicView();
                        avroDynamicView.setName(dv.name);
                        avroDynamicView.setFilterPipeline(
                                dv.filterPipeline()
                                        .map(filterPipeline -> {
                                            final AvroDynamicViewFilter avroDynamicViewFilter = new AvroDynamicViewFilter();
                                            avroDynamicViewFilter.setType(filterPipeline.getType());
                                            avroDynamicViewFilter.setUid(filterPipeline.getUid());
                                            avroDynamicViewFilter.setVal(filterPipeline.getVal().toString());
                                            return avroDynamicViewFilter;
                                        })
                                        .toJavaList()
                        );
                        return avroDynamicView;
                    })
                    .toJavaList()
            );
            return avroCollection;
        }).toJavaList());


        return avroThor;
    }

    @Override
    public void deserialize(byte[] bytes) {
        Try.of(() -> {
                    DatumReader<AvroThor> reader = new SpecificDatumReader<>(AvroThor.class);
                    Decoder decoder = DecoderFactory.get().binaryDecoder(
                            new ByteArrayInputStream(bytes), null);
                    return reader.read(null, decoder);
                }).map(avroThor -> {
                    final Thor thor = thor();
                    List.ofAll(avroThor.getCollections()).forEach(avroCollection -> {
                        final String name = avroCollection.getName().toString();
                        final Collection collection = thor.getCollection(name).getOrElse(() -> thor.addCollection(name));
                        collection.clearData();

                        collection.clearIndex();
                        List.ofAll(avroCollection.getBinaryIndices())
                                .map(CharSequence::toString)
                                .forEach(collection::ensureIndex);
                        collection.clearConstraints();
                        List.ofAll(avroCollection.getUniques())
                                .map(CharSequence::toString)
                                .forEach(collection::ensureUniqueIndex);
                        List.ofAll(avroCollection.getExacts())
                                .map(CharSequence::toString)
                                .forEach(collection::ensureExact);
                        collection.clearTransform();
                        List.ofAll(avroCollection.getTransforms())
                                .forEach(t -> {
                                    final String n = t.getName().toString();
                                    collection.setTransform(n,
                                            List.ofAll(t.getOperations())
                                                    .map(CharSequence::toString)
                                                    .map(JsonObject::new)
                                    );
                                });

                        Serialization.appendData(
                                List.ofAll(avroCollection.getData())
                                        .map(CharSequence::toString)
                                        .map(JsonObject::new),
                                collection
                        );
                        List.ofAll(avroCollection.getDynamicViews())
                                .forEach(avroDynamicView -> {
                                    final DynamicView dv = new DynamicView(collection, avroDynamicView.getName().toString(), new DynamicViewOption());
                                    avroDynamicView.getFilterPipeline().forEach(avroDynamicViewFilter -> {
                                        dv.applyFilter(new DynamicViewFilter()
                                                .setUid(avroDynamicViewFilter.getUid().toString())
                                                .setType(avroDynamicViewFilter.getType().toString())
                                                .setVal(new JsonObject(avroDynamicViewFilter.getVal().toString()))
                                        );
                                    });
                                    collection.addDynamicView(dv);
                                });
                    });
                    return thor;
                })
                .onSuccess(t -> LOG.info("Success in deserializing " + thor().name))
                .onFailure(ex -> {
                    LOG.error("Error  in deserializing " + thor().name, ex);
                });

    }
}
