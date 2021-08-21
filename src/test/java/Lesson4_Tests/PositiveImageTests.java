package Lesson4_Tests;

import Lesson4.Endpoints;
import Lesson4.dto.ResponseBase;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class PositiveImageTests extends BaseTests {

    private final String PATH_TO_IMAGE = "src/test/resources/act.jpg";
    static String encodedFile;
    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;
    static RequestSpecification requestSpecificationWithAuthAndMultipartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;
    static ResponseBase responseBase;
    static ResponseSpecification positiveResponseSpecification;

    @BeforeEach
    void beforeTest(){
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File("src/test/resources/act.jpg"))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Picture")
                .addFormParam("type", "jpg")
                .addMultiPart(base64MultiPartSpec)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        positiveResponseSpecification = new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", Is.is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

    }

    @Test//добавить файл
    void uploadFileTest(){
        responseBase = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(Endpoints.UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .as(ResponseBase.class);

    }

    @Test//добавить картинку в избранное
    void favoriteFileTest(){
        //добавляем картинку, в uploadedImageId записываем id (он же imageHash)
        responseBase = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(Endpoints.UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .as(ResponseBase.class);
        createFavorite();// метод добавляет картинку в избранное
    }

    void createFavorite(){
        given(requestSpecificationWithAuthWithBase64)
                .post(Endpoints.IMAGE_FAVORITE, responseBase.getData().getId())
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test//удалить картинку из избранного
    void unfavoritedImage(){
        favoriteFileTest();//создаем картинку, добавляем ее в избранное
        createFavorite();// удаляем созданную картинку из избранного
    }


    private byte[] getFileContent(){
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e){
            e.printStackTrace();
        }
        return byteArray;
    }


    @AfterEach
     void tearDown(){
        given(requestSpecificationWithAuthWithBase64)
                .delete(Endpoints.IMAGE_DELETE, username, responseBase.getData().getDeletehash())
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
