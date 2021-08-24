package fr.genin.christophe.thor.server.dto;

import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.collection.List;

public class ThorDto {
    public String name;
    public String filename;
    private List<CollectionDto> collections = List.empty();
    public double databaseVersion = 1.5;
    public double engineVersion = 1.5;
    public boolean autosave = false;
    public int autosaveInterval = 5000;
    public Long autosaveHandle;
    public ThorOptions options;

    public ThorDto() {
    }

    public ThorDto(Thor thor) {
        this.name = thor.name;
        this.filename = thor.filename;
        this.collections = thor.collections()
                .map(CollectionDto::new);
        this.databaseVersion = thor.databaseVersion;
        this.engineVersion = thor.engineVersion;
        this.autosave = thor.autosave;
        this.autosaveInterval = thor.autosaveInterval;
        this.autosaveHandle = thor.autosaveHandle;
        this.options = thor.options();
    }
}
