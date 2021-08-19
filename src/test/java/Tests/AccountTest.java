package Tests;



import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class AccountTest extends BaseTest {

     @Test
      void getAccountInfoTest() {
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .then()
                .statusCode(200);
    }

    @Test //с принудительным логированием
     void getAccountInfoLogTest(){
         given()
                 .header("Authorization", token)
                 .log()
                 .method()
                 .log()
                 .uri()
                 .when()
                 .get("https://api.imgur.com/3/account/{username}", username)
                 .prettyPeek()
                 .then()
                 .statusCode(200);
    }

    @Test//с ассертами
    void getAccountInfoLogTestAssertion(){
        given()
                .header("Authorization", token)
                .log()
                .method()
                .log()
                .uri()
                .expect()
                .body("data.url", equalTo(username))
                .body("success", equalTo(true))
                .body("status", equalTo(200))
                .contentType("application/json")
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .prettyPeek();
    }



}
