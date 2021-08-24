/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package fr.genin.christophe.thor.core.serialization.avro;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class AvroDynamicView extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -290136313937554574L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroDynamicView\",\"namespace\":\"fr.genin.christophe.thor.core.serialization.avro\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"filterPipeline\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"AvroDynamicViewFilter\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"val\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"}]}},\"default\":[]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroDynamicView> ENCODER =
      new BinaryMessageEncoder<AvroDynamicView>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroDynamicView> DECODER =
      new BinaryMessageDecoder<AvroDynamicView>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroDynamicView> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroDynamicView> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroDynamicView> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroDynamicView>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroDynamicView to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroDynamicView from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroDynamicView instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroDynamicView fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence name;
   private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> filterPipeline;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroDynamicView() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param filterPipeline The new value for filterPipeline
   */
  public AvroDynamicView(java.lang.CharSequence name, java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> filterPipeline) {
    this.name = name;
    this.filterPipeline = filterPipeline;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return filterPipeline;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = (java.lang.CharSequence)value$; break;
    case 1: filterPipeline = (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'filterPipeline' field.
   * @return The value of the 'filterPipeline' field.
   */
  public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> getFilterPipeline() {
    return filterPipeline;
  }


  /**
   * Sets the value of the 'filterPipeline' field.
   * @param value the value to set.
   */
  public void setFilterPipeline(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> value) {
    this.filterPipeline = value;
  }

  /**
   * Creates a new AvroDynamicView RecordBuilder.
   * @return A new AvroDynamicView RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder newBuilder() {
    return new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder();
  }

  /**
   * Creates a new AvroDynamicView RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroDynamicView RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder newBuilder(fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder other) {
    if (other == null) {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder();
    } else {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder(other);
    }
  }

  /**
   * Creates a new AvroDynamicView RecordBuilder by copying an existing AvroDynamicView instance.
   * @param other The existing instance to copy.
   * @return A new AvroDynamicView RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder newBuilder(fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView other) {
    if (other == null) {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder();
    } else {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroDynamicView instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroDynamicView>
    implements org.apache.avro.data.RecordBuilder<AvroDynamicView> {

    private java.lang.CharSequence name;
    private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> filterPipeline;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.filterPipeline)) {
        this.filterPipeline = data().deepCopy(fields()[1].schema(), other.filterPipeline);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroDynamicView instance
     * @param other The existing instance to copy.
     */
    private Builder(fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.filterPipeline)) {
        this.filterPipeline = data().deepCopy(fields()[1].schema(), other.filterPipeline);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder setName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'filterPipeline' field.
      * @return The value.
      */
    public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> getFilterPipeline() {
      return filterPipeline;
    }


    /**
      * Sets the value of the 'filterPipeline' field.
      * @param value The value of 'filterPipeline'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder setFilterPipeline(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> value) {
      validate(fields()[1], value);
      this.filterPipeline = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'filterPipeline' field has been set.
      * @return True if the 'filterPipeline' field has been set, false otherwise.
      */
    public boolean hasFilterPipeline() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'filterPipeline' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView.Builder clearFilterPipeline() {
      filterPipeline = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroDynamicView build() {
      try {
        AvroDynamicView record = new AvroDynamicView();
        record.name = fieldSetFlags()[0] ? this.name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.filterPipeline = fieldSetFlags()[1] ? this.filterPipeline : (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroDynamicView>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroDynamicView>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroDynamicView>
    READER$ = (org.apache.avro.io.DatumReader<AvroDynamicView>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.name);

    long size0 = this.filterPipeline.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter e0: this.filterPipeline) {
      actualSize0++;
      out.startItem();
      e0.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      long size0 = in.readArrayStart();
      java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> a0 = this.filterPipeline;
      if (a0 == null) {
        a0 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>((int)size0, SCHEMA$.getField("filterPipeline").schema());
        this.filterPipeline = a0;
      } else a0.clear();
      SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>)a0 : null);
      for ( ; 0 < size0; size0 = in.arrayNext()) {
        for ( ; size0 != 0; size0--) {
          fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter e0 = (ga0 != null ? ga0.peek() : null);
          if (e0 == null) {
            e0 = new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter();
          }
          e0.customDecode(in);
          a0.add(e0);
        }
      }

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 1:
          long size0 = in.readArrayStart();
          java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> a0 = this.filterPipeline;
          if (a0 == null) {
            a0 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>((int)size0, SCHEMA$.getField("filterPipeline").schema());
            this.filterPipeline = a0;
          } else a0.clear();
          SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter>)a0 : null);
          for ( ; 0 < size0; size0 = in.arrayNext()) {
            for ( ; size0 != 0; size0--) {
              fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter e0 = (ga0 != null ? ga0.peek() : null);
              if (e0 == null) {
                e0 = new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicViewFilter();
              }
              e0.customDecode(in);
              a0.add(e0);
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










