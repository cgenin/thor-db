package fr.genin.christophe.thor.core.infrastructure;

import java.io.Serializable;

public class FileToSave implements Serializable {
   public final String filename;
   public final byte[] content;

    public FileToSave(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }
}
