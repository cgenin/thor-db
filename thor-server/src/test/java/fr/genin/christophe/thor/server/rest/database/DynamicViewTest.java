package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.Thor;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class DynamicViewTest {

    @Inject
    DatabasePort databasePort;

    @BeforeEach
    public void before() {
        if (databasePort.getCollection("test", "dv").isEmpty()) {
            final Thor test = databasePort.create("test");
            final Collection collection = test.addCollection("dv");
            collection.insert(
                    List.of(
                            new JsonObject()
                                    .put("name", "Odin")
                                    .put("address", "Asgard")
                                    .put("age", 50),
                            new JsonObject()
                                    .put("name", "Thor")
                                    .put("age", 35),
                            new JsonObject()
                                    .put("name", "Loki")
                                    .put("age", 30)

                    ))
                    .getOrElseThrow(e -> new IllegalStateException("s", e));
            final JsonObject before40 = new JsonObject()
                    .put("age", new JsonObject()
                            .put("$lte", 40));
            collection.addTransform("progeny", List.of(
                    new JsonObject()
                            .put("type", "find")
                            .put("value", before40)
            ));
            test.saveDatabase();
        }
    }

    @Test
    public void should_create_with_transform() {
        given()
                .when().get("/api/databases/test/collections/dv/dynamic-views")
                .then()
                .statusCode(200)
                .body(equalTo("[]"));

        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"test\"}")
                .post("/api/databases/test/collections/dv/dynamic-views")
                .then()
                .statusCode(200)
                .body("name", is("test"))
                .body(allOf(
                                containsString("\"resultdata\":[{\"name\":\"Odin\""),
                                containsString("{\"name\":\"Thor\""),
                                containsString("{\"name\":\"Loki\"")

                        )
                );

    }
}
