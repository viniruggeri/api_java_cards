package fiap.tds;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class CardResourceTest {

    @ConfigProperty(name = "api.key")
    String apiKey;

    @Test
    void testGetAllCards() {
        given()
            .header("X-API-Key", apiKey)
            .when().get("/card")
            .then()
                .statusCode(200)
                .body("size()", is(5)); // We expect 5 cards as defined in CardResource
    }

    @Test
    void testGetCardById() {
        given()
            .header("X-API-Key", apiKey)
            .when().get("/card/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Card 1"))
                .body("description", is("Description 1"));
    }

    @Test
    void testGetCardByIdNotFound() {
        given()
            .header("X-API-Key", apiKey)
            .when().get("/card/999")
            .then()
                .statusCode(404);
    }

    @Test
    void testSearchCards() {
        given()
            .header("X-API-Key", apiKey)
            .when().get("/card/search?page=1&name=Card&direction=asc")
            .then()
                .statusCode(200)
                .body("page", is(1))
                .body("direction", is("asc"))
                .body("pageSize", is(3))
                .body("totalItems", is(5))
                .body("data.size()", is(3)); // First page should have 3 items (PAGE_SIZE)
    }
}
