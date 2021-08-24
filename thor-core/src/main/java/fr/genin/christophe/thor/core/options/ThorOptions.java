package fr.genin.christophe.thor.core.options;

import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.StringJoiner;

public class ThorOptions implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(ThorOptions.class);

    private String defaultName = "thor";
    private String directory = "db";
    private String extensionFile = "json";
    private String adapterType = "memory";
    private Boolean verbose = false;
    private Boolean autosave = false;
    private Boolean autoload = false;
    private Boolean throttledSaves = false;
    private Boolean partitioned = false;
    private Boolean delimited = true;
    private String delimiter;
    private Integer autosaveInterval = 5000;
    private String serializationMethod = SerializationMethod.normal.name();
    private String destructureDelimiter;

    public static ThorOptions from(JsonObject config) {
        return Option.of(config)
                .flatMap(c -> Option.of(c.getJsonObject("lokiOptions")))
                .map(json -> json.mapTo(ThorOptions.class))
                .peek(opt -> LOG.debug(opt.toString()))
                .getOrElse(() -> {
                    LOG.info("config file not found. use default instead.");
                    return new ThorOptions();
                });
    }

    public ThorOptions() {
    }

    public Boolean isVerbose() {
        return verbose;
    }

    public ThorOptions setVerbose(Boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public Boolean isAutosave() {
        return autosave;
    }

    public ThorOptions setAutosave(Boolean autosave) {
        this.autosave = autosave;
        return this;
    }

    public Boolean isAutoload() {
        return autoload;
    }

    public ThorOptions setAutoload(Boolean autoload) {
        this.autoload = autoload;
        return this;
    }

    public Boolean isThrottledSaves() {
        return throttledSaves;
    }

    public ThorOptions setThrottledSaves(Boolean throttledSaves) {
        this.throttledSaves = throttledSaves;
        return this;
    }

    public Integer getAutosaveInterval() {
        return autosaveInterval;
    }

    public ThorOptions setAutosaveInterval(Integer autosaveInterval) {
        this.autosaveInterval = autosaveInterval;
        return this;
    }


    public SerializationMethod serializationMethod() {
        return SerializationMethod.parse(serializationMethod);
    }

    public String getSerializationMethod() {
        return serializationMethod;
    }

    public ThorOptions setSerializationMethod(String serializationMethod) {
        this.serializationMethod = serializationMethod;
        return this;
    }

    public String getDestructureDelimiter() {
        return destructureDelimiter;
    }

    public ThorOptions setDestructureDelimiter(String destructureDelimiter) {
        this.destructureDelimiter = destructureDelimiter;
        return this;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public ThorOptions setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public ThorOptions setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public String getExtensionFile() {
        return extensionFile;
    }

    public ThorOptions setExtensionFile(String extensionFile) {
        this.extensionFile = extensionFile;
        return this;
    }

    public String getAdapterType() {
        return adapterType;
    }

    public ThorOptions setAdapterType(String adapterType) {
        this.adapterType = adapterType;
        return this;
    }

    public Boolean isPartitioned() {
        return partitioned;
    }

    public ThorOptions setPartitioned(Boolean partitioned) {
        this.partitioned = partitioned;
        return this;
    }


    public Boolean isDelimited() {
        return delimited;
    }

    public ThorOptions setDelimited(Boolean delimited) {
        this.delimited = delimited;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public ThorOptions setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public ThorOptions copy() {
        final ThorOptions thorOptions = new ThorOptions();

        thorOptions.defaultName = this.defaultName;
        thorOptions.directory = this.directory;
        thorOptions.extensionFile = this.extensionFile;
        thorOptions.adapterType = this.adapterType;
        thorOptions.verbose = this.verbose;
        thorOptions.autosave = this.autosave;
        thorOptions.autoload = this.autoload;
        thorOptions.throttledSaves = this.throttledSaves;
        thorOptions.partitioned = this.partitioned;
        thorOptions.delimited = this.delimited;
        thorOptions.delimiter = this.delimiter;
        thorOptions.autosaveInterval = this.autosaveInterval;
        thorOptions.serializationMethod = this.serializationMethod;
        thorOptions.destructureDelimiter = this.destructureDelimiter;
        return thorOptions;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ThorOptions.class.getSimpleName() + "[", "]")
                .add("defaultName='" + defaultName + "'")
                .add("directory='" + directory + "'")
                .add("extensionFile='" + extensionFile + "'")
                .add("adapterType='" + adapterType + "'")
                .add("verbose=" + verbose)
                .add("autosave=" + autosave)
                .add("autoload=" + autoload)
                .add("throttledSaves=" + throttledSaves)
                .add("partitioned=" + partitioned)
                .add("delimited=" + delimited)
                .add("delimiter='" + delimiter + "'")
                .add("autosaveInterval=" + autosaveInterval)
                .add("serializationMethod=" + serializationMethod)
                .add("destructureDelimiter='" + destructureDelimiter + "'")
                .toString();
    }
}
