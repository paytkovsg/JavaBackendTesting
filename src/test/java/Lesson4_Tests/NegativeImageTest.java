package Lesson4_Tests;

import Lesson4.Endpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

public class NegativeImageTest extends BaseTests{


    @Test//запрос без файла
    void uploadNullFileTest(){
        given()
                .header("Authorization", token)
                .multiPart("image", "file")
                .expect()
                .body("success", is(false))
                .body("data.id", is(nullValue()))
                .when()
                .post(Endpoints.UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .statusCode(400);

    }

    @Test//добавление без авторизации
    void uploadFileNotAuthorizationTest(){
        given()
                .header("Authorization", token+1)
                .multiPart("image", "file")
                .expect()
                .body("success", is(false))
                .body("data.id", is(nullValue()))
                .when()
                .post(Endpoints.UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}
