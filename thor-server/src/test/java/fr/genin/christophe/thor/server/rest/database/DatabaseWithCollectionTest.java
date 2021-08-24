package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.core.utils.Commons;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import fr.genin.christophe.thor.server.rest.database.profiles.Profiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestProfile(Profiles.NoTags.class)
public class DatabaseWithCollectionTest {

    @Inject
    DatabasePort databasePort;

    @BeforeEach
    public void beforeAll() {
        if (databasePort.getCollection("testdwr", "1DWR").isEmpty()) {
            final Thor testdwr =  databasePort.create("testdwr");
            final Collection collection = testdwr.addCollection("1DWR");
            System.out.println(collection.add(new JsonObject().put("testdwr", true))
                    .getOrElseThrow(e -> new IllegalStateException("s", e)));
            testdwr.saveDatabase();
        }


    }

    @Test
    public void testdwrDatabases() {
        given()
                .when()
                .get("/api/databases")
                .then()
                .statusCode(200)
                .body(allOf(
                        containsString("{\"name\":\"thor\","),
                        containsString("{\"name\":\"testdwr\",")
                ));

        given()
                .when().get("/api/databases/unknown")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/testdwr")
                .then()
                .statusCode(200)
                .body(containsString("{\"name\":\"testdwr\""));
    }


    @Test
    public void testCollection() {
        given()
                .when().get("/api/databases/testdwr/collections")
                .then()
                .statusCode(200)
                .body(containsString("[]"));



        given()
                .when().get("/api/databases/testdwr/collections/1DWR/data/1")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/maxId")
                .then()
                .statusCode(200)
                .body(containsString("1"));

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/data/2")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/count")
                .then()
                .statusCode(200)
                .body(equalTo("1"));


        given()
                .when()
                .contentType(ContentType.JSON)
                .body(new JsonObject().put("test", true).encode())
                .post("/api/databases/testdwr/collections/1DWR/data")
                .then()
                .statusCode(200)
                .body(containsString(Commons.ID));

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/data/2")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/data/3")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/count")
                .then()
                .statusCode(200)
                .body(equalTo("2"));

        final String test = new JsonArray().add(new JsonObject().put("test", true)).encode();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(test)
                .post("/api/databases/testdwr/collections/1DWR/insert")
                .then()
                .statusCode(200)
                .body(containsString(Commons.ID));


        given()
                .when().get("/api/databases/testdwr/collections/1DWR/data/3")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases/testdwr/collections/1DWR/count")
                .then()
                .statusCode(200)
                .body(equalTo("3"));
    }
}
