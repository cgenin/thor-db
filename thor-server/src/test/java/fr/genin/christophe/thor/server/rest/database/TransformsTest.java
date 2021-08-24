package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
public class TransformsTest {

    @Inject
    DatabasePort databasePort;

    @BeforeEach
    public void before() {
        if (databasePort.getCollection("test", "1coll").isEmpty()) {
            final Thor test = databasePort.create("test");
            final Collection collection = test.addCollection("1coll");
            System.out.println(collection.add(new JsonObject().put("test", true))
                    .getOrElseThrow(e -> new IllegalStateException("s", e)));
            System.out.println(collection.add(new JsonObject().put("test", false))
                    .getOrElseThrow(e -> new IllegalStateException("s", e)));
            test.saveDatabase();
        }
    }

    @Test
    public void should_create_before() {
        given()
                .when().get("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("[]"));


        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"transform1\"," +
                        "\"operations\":[{\"type\":\"find\",\"value\":{\"test\":{\"$eq\":true}}}]}")
                .post("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("true"));

        given()
                .when().get("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("[{\"name\":\"transform1\",\"operations\":[{\"type\":\"find\",\"value\":{\"test\":{\"$eq\":true}}}]}]"));


        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"transform1\"," +
                        "\"operations\":[{\"type\":\"find\",\"value\":{\"test\":{\"$eq\":false}}}]}")
                .post("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("true"));

        given()
                .when().get("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("[{\"name\":\"transform1\",\"operations\":[{\"type\":\"find\",\"value\":{\"test\":{\"$eq\":false}}}]}]"));

        given()
                .when()
                .contentType(ContentType.JSON)
                .delete("/api/databases/test/collections/1coll/transforms/transform1")
                .then()
                .statusCode(200)
                .body(equalTo("true"));

        given()
                .when().get("/api/databases/test/collections/1coll/transforms")
                .then()
                .statusCode(200)
                .body(equalTo("[]"));
    }
}
