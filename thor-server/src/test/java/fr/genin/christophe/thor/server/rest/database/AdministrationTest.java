package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.server.rest.database.profiles.Profiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;


@QuarkusTest
@TestProfile(Profiles.NoTags.class)
public class AdministrationTest {

    @Test
    public void should_health() {
        given()
                .when().get("/api/health")
                .then()
                .statusCode(200)
                .body(equalTo("{\"up\":true,\"nbDatabases\":1}"));
    }

    @Test
    public void should_openapi() {
        given()
                .when().get("/q/openapi")
                .then()
                .statusCode(200);
    }

    @Test
    public void should_openapi_interface() {
        given()
                .when().get("/q/swagger-ui")
                .then()
                .statusCode(200)
                 .body(containsString("html"))
        ;
    }
}
