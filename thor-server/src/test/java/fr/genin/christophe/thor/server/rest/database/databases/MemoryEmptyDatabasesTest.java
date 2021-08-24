package fr.genin.christophe.thor.server.rest.database.databases;

import fr.genin.christophe.thor.server.rest.database.profiles.Profiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(Profiles.NoTags.class)
public class MemoryEmptyDatabasesTest {

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
