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
public class AvroCollection extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7133356673970307549L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroCollection\",\"namespace\":\"fr.genin.christophe.thor.core.serialization.avro\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"data\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":[]},{\"name\":\"binaryIndices\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":[]},{\"name\":\"uniques\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":[]},{\"name\":\"exacts\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":[]},{\"name\":\"transforms\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"AvroTransform\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"operations\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":[]}]}},\"default\":[]},{\"name\":\"dynamicViews\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"AvroDynamicView\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"filterPipeline\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"AvroDynamicViewFilter\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"val\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"}]}},\"default\":[]}]}},\"default\":[]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroCollection> ENCODER =
      new BinaryMessageEncoder<AvroCollection>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroCollection> DECODER =
      new BinaryMessageDecoder<AvroCollection>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AvroCollection> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AvroCollection> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AvroCollection> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroCollection>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AvroCollection to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AvroCollection from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AvroCollection instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AvroCollection fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence name;
   private java.util.List<java.lang.CharSequence> data;
   private java.util.List<java.lang.CharSequence> binaryIndices;
   private java.util.List<java.lang.CharSequence> uniques;
   private java.util.List<java.lang.CharSequence> exacts;
   private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> transforms;
   private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> dynamicViews;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroCollection() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param data The new value for data
   * @param binaryIndices The new value for binaryIndices
   * @param uniques The new value for uniques
   * @param exacts The new value for exacts
   * @param transforms The new value for transforms
   * @param dynamicViews The new value for dynamicViews
   */
  public AvroCollection(java.lang.CharSequence name, java.util.List<java.lang.CharSequence> data, java.util.List<java.lang.CharSequence> binaryIndices, java.util.List<java.lang.CharSequence> uniques, java.util.List<java.lang.CharSequence> exacts, java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> transforms, java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> dynamicViews) {
    this.name = name;
    this.data = data;
    this.binaryIndices = binaryIndices;
    this.uniques = uniques;
    this.exacts = exacts;
    this.transforms = transforms;
    this.dynamicViews = dynamicViews;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return data;
    case 2: return binaryIndices;
    case 3: return uniques;
    case 4: return exacts;
    case 5: return transforms;
    case 6: return dynamicViews;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = (java.lang.CharSequence)value$; break;
    case 1: data = (java.util.List<java.lang.CharSequence>)value$; break;
    case 2: binaryIndices = (java.util.List<java.lang.CharSequence>)value$; break;
    case 3: uniques = (java.util.List<java.lang.CharSequence>)value$; break;
    case 4: exacts = (java.util.List<java.lang.CharSequence>)value$; break;
    case 5: transforms = (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>)value$; break;
    case 6: dynamicViews = (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>)value$; break;
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
   * Gets the value of the 'data' field.
   * @return The value of the 'data' field.
   */
  public java.util.List<java.lang.CharSequence> getData() {
    return data;
  }


  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(java.util.List<java.lang.CharSequence> value) {
    this.data = value;
  }

  /**
   * Gets the value of the 'binaryIndices' field.
   * @return The value of the 'binaryIndices' field.
   */
  public java.util.List<java.lang.CharSequence> getBinaryIndices() {
    return binaryIndices;
  }


  /**
   * Sets the value of the 'binaryIndices' field.
   * @param value the value to set.
   */
  public void setBinaryIndices(java.util.List<java.lang.CharSequence> value) {
    this.binaryIndices = value;
  }

  /**
   * Gets the value of the 'uniques' field.
   * @return The value of the 'uniques' field.
   */
  public java.util.List<java.lang.CharSequence> getUniques() {
    return uniques;
  }


  /**
   * Sets the value of the 'uniques' field.
   * @param value the value to set.
   */
  public void setUniques(java.util.List<java.lang.CharSequence> value) {
    this.uniques = value;
  }

  /**
   * Gets the value of the 'exacts' field.
   * @return The value of the 'exacts' field.
   */
  public java.util.List<java.lang.CharSequence> getExacts() {
    return exacts;
  }


  /**
   * Sets the value of the 'exacts' field.
   * @param value the value to set.
   */
  public void setExacts(java.util.List<java.lang.CharSequence> value) {
    this.exacts = value;
  }

  /**
   * Gets the value of the 'transforms' field.
   * @return The value of the 'transforms' field.
   */
  public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> getTransforms() {
    return transforms;
  }


  /**
   * Sets the value of the 'transforms' field.
   * @param value the value to set.
   */
  public void setTransforms(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> value) {
    this.transforms = value;
  }

  /**
   * Gets the value of the 'dynamicViews' field.
   * @return The value of the 'dynamicViews' field.
   */
  public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> getDynamicViews() {
    return dynamicViews;
  }


  /**
   * Sets the value of the 'dynamicViews' field.
   * @param value the value to set.
   */
  public void setDynamicViews(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> value) {
    this.dynamicViews = value;
  }

  /**
   * Creates a new AvroCollection RecordBuilder.
   * @return A new AvroCollection RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder newBuilder() {
    return new fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder();
  }

  /**
   * Creates a new AvroCollection RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroCollection RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder newBuilder(fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder other) {
    if (other == null) {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder();
    } else {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder(other);
    }
  }

  /**
   * Creates a new AvroCollection RecordBuilder by copying an existing AvroCollection instance.
   * @param other The existing instance to copy.
   * @return A new AvroCollection RecordBuilder
   */
  public static fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder newBuilder(fr.genin.christophe.thor.core.serialization.avro.AvroCollection other) {
    if (other == null) {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder();
    } else {
      return new fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder(other);
    }
  }

  /**
   * RecordBuilder for AvroCollection instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroCollection>
    implements org.apache.avro.data.RecordBuilder<AvroCollection> {

    private java.lang.CharSequence name;
    private java.util.List<java.lang.CharSequence> data;
    private java.util.List<java.lang.CharSequence> binaryIndices;
    private java.util.List<java.lang.CharSequence> uniques;
    private java.util.List<java.lang.CharSequence> exacts;
    private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> transforms;
    private java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> dynamicViews;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.data)) {
        this.data = data().deepCopy(fields()[1].schema(), other.data);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.binaryIndices)) {
        this.binaryIndices = data().deepCopy(fields()[2].schema(), other.binaryIndices);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.uniques)) {
        this.uniques = data().deepCopy(fields()[3].schema(), other.uniques);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.exacts)) {
        this.exacts = data().deepCopy(fields()[4].schema(), other.exacts);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.transforms)) {
        this.transforms = data().deepCopy(fields()[5].schema(), other.transforms);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.dynamicViews)) {
        this.dynamicViews = data().deepCopy(fields()[6].schema(), other.dynamicViews);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
    }

    /**
     * Creates a Builder by copying an existing AvroCollection instance
     * @param other The existing instance to copy.
     */
    private Builder(fr.genin.christophe.thor.core.serialization.avro.AvroCollection other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.data)) {
        this.data = data().deepCopy(fields()[1].schema(), other.data);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.binaryIndices)) {
        this.binaryIndices = data().deepCopy(fields()[2].schema(), other.binaryIndices);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.uniques)) {
        this.uniques = data().deepCopy(fields()[3].schema(), other.uniques);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.exacts)) {
        this.exacts = data().deepCopy(fields()[4].schema(), other.exacts);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.transforms)) {
        this.transforms = data().deepCopy(fields()[5].schema(), other.transforms);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.dynamicViews)) {
        this.dynamicViews = data().deepCopy(fields()[6].schema(), other.dynamicViews);
        fieldSetFlags()[6] = true;
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
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setName(java.lang.CharSequence value) {
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
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'data' field.
      * @return The value.
      */
    public java.util.List<java.lang.CharSequence> getData() {
      return data;
    }


    /**
      * Sets the value of the 'data' field.
      * @param value The value of 'data'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setData(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[1], value);
      this.data = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'data' field has been set.
      * @return True if the 'data' field has been set, false otherwise.
      */
    public boolean hasData() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'data' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearData() {
      data = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'binaryIndices' field.
      * @return The value.
      */
    public java.util.List<java.lang.CharSequence> getBinaryIndices() {
      return binaryIndices;
    }


    /**
      * Sets the value of the 'binaryIndices' field.
      * @param value The value of 'binaryIndices'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setBinaryIndices(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[2], value);
      this.binaryIndices = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'binaryIndices' field has been set.
      * @return True if the 'binaryIndices' field has been set, false otherwise.
      */
    public boolean hasBinaryIndices() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'binaryIndices' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearBinaryIndices() {
      binaryIndices = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'uniques' field.
      * @return The value.
      */
    public java.util.List<java.lang.CharSequence> getUniques() {
      return uniques;
    }


    /**
      * Sets the value of the 'uniques' field.
      * @param value The value of 'uniques'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setUniques(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[3], value);
      this.uniques = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'uniques' field has been set.
      * @return True if the 'uniques' field has been set, false otherwise.
      */
    public boolean hasUniques() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'uniques' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearUniques() {
      uniques = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'exacts' field.
      * @return The value.
      */
    public java.util.List<java.lang.CharSequence> getExacts() {
      return exacts;
    }


    /**
      * Sets the value of the 'exacts' field.
      * @param value The value of 'exacts'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setExacts(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[4], value);
      this.exacts = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'exacts' field has been set.
      * @return True if the 'exacts' field has been set, false otherwise.
      */
    public boolean hasExacts() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'exacts' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearExacts() {
      exacts = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'transforms' field.
      * @return The value.
      */
    public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> getTransforms() {
      return transforms;
    }


    /**
      * Sets the value of the 'transforms' field.
      * @param value The value of 'transforms'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setTransforms(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> value) {
      validate(fields()[5], value);
      this.transforms = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'transforms' field has been set.
      * @return True if the 'transforms' field has been set, false otherwise.
      */
    public boolean hasTransforms() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'transforms' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearTransforms() {
      transforms = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'dynamicViews' field.
      * @return The value.
      */
    public java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> getDynamicViews() {
      return dynamicViews;
    }


    /**
      * Sets the value of the 'dynamicViews' field.
      * @param value The value of 'dynamicViews'.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder setDynamicViews(java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> value) {
      validate(fields()[6], value);
      this.dynamicViews = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'dynamicViews' field has been set.
      * @return True if the 'dynamicViews' field has been set, false otherwise.
      */
    public boolean hasDynamicViews() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'dynamicViews' field.
      * @return This builder.
      */
    public fr.genin.christophe.thor.core.serialization.avro.AvroCollection.Builder clearDynamicViews() {
      dynamicViews = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroCollection build() {
      try {
        AvroCollection record = new AvroCollection();
        record.name = fieldSetFlags()[0] ? this.name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.data = fieldSetFlags()[1] ? this.data : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[1]);
        record.binaryIndices = fieldSetFlags()[2] ? this.binaryIndices : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[2]);
        record.uniques = fieldSetFlags()[3] ? this.uniques : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[3]);
        record.exacts = fieldSetFlags()[4] ? this.exacts : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[4]);
        record.transforms = fieldSetFlags()[5] ? this.transforms : (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>) defaultValue(fields()[5]);
        record.dynamicViews = fieldSetFlags()[6] ? this.dynamicViews : (java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>) defaultValue(fields()[6]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroCollection>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroCollection>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroCollection>
    READER$ = (org.apache.avro.io.DatumReader<AvroCollection>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.name);

    long size0 = this.data.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (java.lang.CharSequence e0: this.data) {
      actualSize0++;
      out.startItem();
      out.writeString(e0);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

    long size1 = this.binaryIndices.size();
    out.writeArrayStart();
    out.setItemCount(size1);
    long actualSize1 = 0;
    for (java.lang.CharSequence e1: this.binaryIndices) {
      actualSize1++;
      out.startItem();
      out.writeString(e1);
    }
    out.writeArrayEnd();
    if (actualSize1 != size1)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size1 + ", but element count was " + actualSize1 + ".");

    long size2 = this.uniques.size();
    out.writeArrayStart();
    out.setItemCount(size2);
    long actualSize2 = 0;
    for (java.lang.CharSequence e2: this.uniques) {
      actualSize2++;
      out.startItem();
      out.writeString(e2);
    }
    out.writeArrayEnd();
    if (actualSize2 != size2)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size2 + ", but element count was " + actualSize2 + ".");

    long size3 = this.exacts.size();
    out.writeArrayStart();
    out.setItemCount(size3);
    long actualSize3 = 0;
    for (java.lang.CharSequence e3: this.exacts) {
      actualSize3++;
      out.startItem();
      out.writeString(e3);
    }
    out.writeArrayEnd();
    if (actualSize3 != size3)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size3 + ", but element count was " + actualSize3 + ".");

    long size4 = this.transforms.size();
    out.writeArrayStart();
    out.setItemCount(size4);
    long actualSize4 = 0;
    for (fr.genin.christophe.thor.core.serialization.avro.AvroTransform e4: this.transforms) {
      actualSize4++;
      out.startItem();
      e4.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize4 != size4)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size4 + ", but element count was " + actualSize4 + ".");

    long size5 = this.dynamicViews.size();
    out.writeArrayStart();
    out.setItemCount(size5);
    long actualSize5 = 0;
    for (fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView e5: this.dynamicViews) {
      actualSize5++;
      out.startItem();
      e5.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize5 != size5)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size5 + ", but element count was " + actualSize5 + ".");

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      long size0 = in.readArrayStart();
      java.util.List<java.lang.CharSequence> a0 = this.data;
      if (a0 == null) {
        a0 = new SpecificData.Array<java.lang.CharSequence>((int)size0, SCHEMA$.getField("data").schema());
        this.data = a0;
      } else a0.clear();
      SpecificData.Array<java.lang.CharSequence> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a0 : null);
      for ( ; 0 < size0; size0 = in.arrayNext()) {
        for ( ; size0 != 0; size0--) {
          java.lang.CharSequence e0 = (ga0 != null ? ga0.peek() : null);
          e0 = in.readString(e0 instanceof Utf8 ? (Utf8)e0 : null);
          a0.add(e0);
        }
      }

      long size1 = in.readArrayStart();
      java.util.List<java.lang.CharSequence> a1 = this.binaryIndices;
      if (a1 == null) {
        a1 = new SpecificData.Array<java.lang.CharSequence>((int)size1, SCHEMA$.getField("binaryIndices").schema());
        this.binaryIndices = a1;
      } else a1.clear();
      SpecificData.Array<java.lang.CharSequence> ga1 = (a1 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a1 : null);
      for ( ; 0 < size1; size1 = in.arrayNext()) {
        for ( ; size1 != 0; size1--) {
          java.lang.CharSequence e1 = (ga1 != null ? ga1.peek() : null);
          e1 = in.readString(e1 instanceof Utf8 ? (Utf8)e1 : null);
          a1.add(e1);
        }
      }

      long size2 = in.readArrayStart();
      java.util.List<java.lang.CharSequence> a2 = this.uniques;
      if (a2 == null) {
        a2 = new SpecificData.Array<java.lang.CharSequence>((int)size2, SCHEMA$.getField("uniques").schema());
        this.uniques = a2;
      } else a2.clear();
      SpecificData.Array<java.lang.CharSequence> ga2 = (a2 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a2 : null);
      for ( ; 0 < size2; size2 = in.arrayNext()) {
        for ( ; size2 != 0; size2--) {
          java.lang.CharSequence e2 = (ga2 != null ? ga2.peek() : null);
          e2 = in.readString(e2 instanceof Utf8 ? (Utf8)e2 : null);
          a2.add(e2);
        }
      }

      long size3 = in.readArrayStart();
      java.util.List<java.lang.CharSequence> a3 = this.exacts;
      if (a3 == null) {
        a3 = new SpecificData.Array<java.lang.CharSequence>((int)size3, SCHEMA$.getField("exacts").schema());
        this.exacts = a3;
      } else a3.clear();
      SpecificData.Array<java.lang.CharSequence> ga3 = (a3 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a3 : null);
      for ( ; 0 < size3; size3 = in.arrayNext()) {
        for ( ; size3 != 0; size3--) {
          java.lang.CharSequence e3 = (ga3 != null ? ga3.peek() : null);
          e3 = in.readString(e3 instanceof Utf8 ? (Utf8)e3 : null);
          a3.add(e3);
        }
      }

      long size4 = in.readArrayStart();
      java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> a4 = this.transforms;
      if (a4 == null) {
        a4 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>((int)size4, SCHEMA$.getField("transforms").schema());
        this.transforms = a4;
      } else a4.clear();
      SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> ga4 = (a4 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>)a4 : null);
      for ( ; 0 < size4; size4 = in.arrayNext()) {
        for ( ; size4 != 0; size4--) {
          fr.genin.christophe.thor.core.serialization.avro.AvroTransform e4 = (ga4 != null ? ga4.peek() : null);
          if (e4 == null) {
            e4 = new fr.genin.christophe.thor.core.serialization.avro.AvroTransform();
          }
          e4.customDecode(in);
          a4.add(e4);
        }
      }

      long size5 = in.readArrayStart();
      java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> a5 = this.dynamicViews;
      if (a5 == null) {
        a5 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>((int)size5, SCHEMA$.getField("dynamicViews").schema());
        this.dynamicViews = a5;
      } else a5.clear();
      SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> ga5 = (a5 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>)a5 : null);
      for ( ; 0 < size5; size5 = in.arrayNext()) {
        for ( ; size5 != 0; size5--) {
          fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView e5 = (ga5 != null ? ga5.peek() : null);
          if (e5 == null) {
            e5 = new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView();
          }
          e5.customDecode(in);
          a5.add(e5);
        }
      }

    } else {
      for (int i = 0; i < 7; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 1:
          long size0 = in.readArrayStart();
          java.util.List<java.lang.CharSequence> a0 = this.data;
          if (a0 == null) {
            a0 = new SpecificData.Array<java.lang.CharSequence>((int)size0, SCHEMA$.getField("data").schema());
            this.data = a0;
          } else a0.clear();
          SpecificData.Array<java.lang.CharSequence> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a0 : null);
          for ( ; 0 < size0; size0 = in.arrayNext()) {
            for ( ; size0 != 0; size0--) {
              java.lang.CharSequence e0 = (ga0 != null ? ga0.peek() : null);
              e0 = in.readString(e0 instanceof Utf8 ? (Utf8)e0 : null);
              a0.add(e0);
            }
          }
          break;

        case 2:
          long size1 = in.readArrayStart();
          java.util.List<java.lang.CharSequence> a1 = this.binaryIndices;
          if (a1 == null) {
            a1 = new SpecificData.Array<java.lang.CharSequence>((int)size1, SCHEMA$.getField("binaryIndices").schema());
            this.binaryIndices = a1;
          } else a1.clear();
          SpecificData.Array<java.lang.CharSequence> ga1 = (a1 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a1 : null);
          for ( ; 0 < size1; size1 = in.arrayNext()) {
            for ( ; size1 != 0; size1--) {
              java.lang.CharSequence e1 = (ga1 != null ? ga1.peek() : null);
              e1 = in.readString(e1 instanceof Utf8 ? (Utf8)e1 : null);
              a1.add(e1);
            }
          }
          break;

        case 3:
          long size2 = in.readArrayStart();
          java.util.List<java.lang.CharSequence> a2 = this.uniques;
          if (a2 == null) {
            a2 = new SpecificData.Array<java.lang.CharSequence>((int)size2, SCHEMA$.getField("uniques").schema());
            this.uniques = a2;
          } else a2.clear();
          SpecificData.Array<java.lang.CharSequence> ga2 = (a2 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a2 : null);
          for ( ; 0 < size2; size2 = in.arrayNext()) {
            for ( ; size2 != 0; size2--) {
              java.lang.CharSequence e2 = (ga2 != null ? ga2.peek() : null);
              e2 = in.readString(e2 instanceof Utf8 ? (Utf8)e2 : null);
              a2.add(e2);
            }
          }
          break;

        case 4:
          long size3 = in.readArrayStart();
          java.util.List<java.lang.CharSequence> a3 = this.exacts;
          if (a3 == null) {
            a3 = new SpecificData.Array<java.lang.CharSequence>((int)size3, SCHEMA$.getField("exacts").schema());
            this.exacts = a3;
          } else a3.clear();
          SpecificData.Array<java.lang.CharSequence> ga3 = (a3 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a3 : null);
          for ( ; 0 < size3; size3 = in.arrayNext()) {
            for ( ; size3 != 0; size3--) {
              java.lang.CharSequence e3 = (ga3 != null ? ga3.peek() : null);
              e3 = in.readString(e3 instanceof Utf8 ? (Utf8)e3 : null);
              a3.add(e3);
            }
          }
          break;

        case 5:
          long size4 = in.readArrayStart();
          java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> a4 = this.transforms;
          if (a4 == null) {
            a4 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>((int)size4, SCHEMA$.getField("transforms").schema());
            this.transforms = a4;
          } else a4.clear();
          SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform> ga4 = (a4 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroTransform>)a4 : null);
          for ( ; 0 < size4; size4 = in.arrayNext()) {
            for ( ; size4 != 0; size4--) {
              fr.genin.christophe.thor.core.serialization.avro.AvroTransform e4 = (ga4 != null ? ga4.peek() : null);
              if (e4 == null) {
                e4 = new fr.genin.christophe.thor.core.serialization.avro.AvroTransform();
              }
              e4.customDecode(in);
              a4.add(e4);
            }
          }
          break;

        case 6:
          long size5 = in.readArrayStart();
          java.util.List<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> a5 = this.dynamicViews;
          if (a5 == null) {
            a5 = new SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>((int)size5, SCHEMA$.getField("dynamicViews").schema());
            this.dynamicViews = a5;
          } else a5.clear();
          SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView> ga5 = (a5 instanceof SpecificData.Array ? (SpecificData.Array<fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView>)a5 : null);
          for ( ; 0 < size5; size5 = in.arrayNext()) {
            for ( ; size5 != 0; size5--) {
              fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView e5 = (ga5 != null ? ga5.peek() : null);
              if (e5 == null) {
                e5 = new fr.genin.christophe.thor.core.serialization.avro.AvroDynamicView();
              }
              e5.customDecode(in);
              a5.add(e5);
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









