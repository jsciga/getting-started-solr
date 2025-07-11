package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookResourceTest {

    @Test
    @Order(1)
    public void testGetAllBooksEmpty() {
        given()
                .when().get("/books")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @Order(2)
    public void testCreateBook() {
        Book book = new Book("Testcontainers Book", "TC Author",
                "123-TC-456", 2023, "Testcontainers description");

        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/books")
                .then()
                .statusCode(201)
                .body("title", equalTo("Testcontainers Book"))
                .body("author", equalTo("TC Author"))
                .body("id", notNullValue());
    }

    @Test
    @Order(3)
    public void testGetAllBooksAfterCreate() {
        given()
                .when().get("/books")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("title", hasItem("Testcontainers Book"));
    }

    @Test
    @Order(4)
    public void testSearchByAuthor() {
        given()
                .when().get("/books/search/author/TC Author")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("author", everyItem(equalTo("TC Author")));
    }
}

