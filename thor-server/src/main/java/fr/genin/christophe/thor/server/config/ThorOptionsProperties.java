package fr.genin.christophe.thor.server.config;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Optional;

@ConfigProperties(prefix = "thor")
public class ThorOptionsProperties  {
    public Options options = new Options();

    public static class Options {
        public Optional<String> defaultName ;
        public Optional<String> directory ;
        public Optional<String> extensionFile ;
        public Optional<String> adapterType;
        public Optional<Boolean> verbose ;
        public Optional<Boolean> autosave ;
        public Optional<Boolean> autoload ;
        public Optional<Boolean> throttledSaves ;
        public Optional<Boolean> partitioned ;
        public Optional<Boolean> delimited ;
        public Optional<String> delimiter;
        public Optional<Integer> autosaveInterval;
        public Optional<String> serializationMethod;
        public Optional<String> destructureDelimiter;
    }
}
