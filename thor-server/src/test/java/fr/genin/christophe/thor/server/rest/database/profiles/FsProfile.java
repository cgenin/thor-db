package fr.genin.christophe.thor.server.rest.database.profiles;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class FsProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("thor.options.adapter-type","fs",
                "thor.options.directory", "target");
    }
}
