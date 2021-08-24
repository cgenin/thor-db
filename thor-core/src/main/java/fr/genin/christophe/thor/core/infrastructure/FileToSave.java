package fr.genin.christophe.thor.core.infrastructure;

public class FileToSave {
   public final String filename;
   public final byte[] content;

    public FileToSave(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }
}
