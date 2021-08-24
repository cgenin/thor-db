package fr.genin.christophe.thor.server.rest.database.databases;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class EmptyDatabases {

    public static void testDatabases() {
        given()
                .when().get("/api/databases")
                .then()
                .statusCode(200)
                .body(containsString("[{\"name\":\"thor\","));

        given()
                .when().get("/api/databases/unknown")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/thor")
                .then()
                .statusCode(200)
                .body(containsString("{\"name\":\"thor\""));
    }

    public static void test_database_modification() {
        given()
                .when().post("/api/databases/thor/load")
                .then()
                .statusCode(200);

        given()
                .when().post("/api/databases/essai/load")
                .then()
                .statusCode(400);

        given()
                .when().post("/api/databases/thor")
                .then()
                .statusCode(400);

        given()
                .when().post("/api/databases/essai")
                .then()
                .statusCode(200);

        given()
                .when().post("/api/databases/essai/load")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases")
                .then()
                .statusCode(200)
                .body(allOf(
                        containsString("thor"),
                        containsString("essai")
                ));

        given()
                .when().put("/api/databases/thor")
                .then()
                .statusCode(200);

        given()
                .when().put("/api/databases/essai")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases")
                .then()
                .statusCode(200)
                .body(allOf(
                        containsString("thor"),
                        containsString("essai")
                ));


        given()
                .when().delete("/api/databases/essai")
                .then()
                .statusCode(200);

        given()
                .when().get("/api/databases")
                .then()
                .statusCode(200)
                .body(allOf(
                        containsString("thor"),
                        not(containsString("essai"))
                ));
    }

    public static void testCollection(){
        given()
                .when().get("/api/databases/thor/collections")
                .then()
                .statusCode(200)
                .body(containsString("[]"));

        given()
                .when().get("/api/databases/thor/collections/unknown")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/thor/collections/unknown/dynamicViews")
                .then()
                .statusCode(404);

        given()
                .when().get("/api/databases/thor/collections/unknown/dynamicViews")
                .then()
                .statusCode(404);
    }
}
