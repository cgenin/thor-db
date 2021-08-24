package fr.genin.christophe.thor.server.rest.database.databases;

import fr.genin.christophe.thor.server.rest.database.profiles.FsProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestProfile(FsProfile.class)
public class FsEmptyDatabasesTest {

    @BeforeAll
    public static void before() throws IOException {
        Files.deleteIfExists(Path.of("target", "thor.json"));
        Files.deleteIfExists(Path.of("target", "essai.json"));
    }

    @Test
    public void testDatabases() {
        EmptyDatabases.testDatabases();
    }

    @Test
    public void test_database_modification() {
       EmptyDatabases.test_database_modification();
    }

    @Test
    public void testCollection() {
      EmptyDatabases.testCollection();

    }


}
