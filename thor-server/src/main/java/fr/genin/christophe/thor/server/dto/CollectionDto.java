package fr.genin.christophe.thor.server.dto;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Ttl;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class CollectionDto {
    public String name;
    public CollectionOptions options;
    public boolean cloneObjects;
    public Long maxId;
    public boolean isIncremental;
    public List<String> binaryIndices;
    public List<Long> idIndex;
    public List<String> uniqueNames;
    public List<String> transforms;
    public List<JsonObject> data;
    public List<String> exactIndices;
    public List<String> dynamicViews;
    public Ttl ttl;


    public CollectionDto() {
    }

    public CollectionDto(Collection collection) {
        this.name = collection.name();
        this.options = collection.options();
        this.cloneObjects = collection.cloneObjects();
        this.maxId = collection.maxId();
        this.isIncremental = collection.isIncremental();
        this.binaryIndices = collection.binaryIndices()
                .map(i -> i.name)
                .toJavaList();
        this.idIndex = collection.idIndex().toJavaList();
        this.uniqueNames = collection.uniqueNames().toJavaList();
        this.transforms = collection.transforms()
                .map(t->t.name)
                .toJavaList();
        this.data = collection.data().toJavaList();
        this.exactIndices = collection.constraints()
                .exacts
                .map(e -> e.field)
                .toJavaList();
        this.dynamicViews = collection.dynamicViews()
                .map(d-> d.name)
                .toJavaList();
        this.ttl = collection.ttl();


    }
}
