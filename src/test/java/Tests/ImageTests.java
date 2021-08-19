package Tests;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class ImageTests extends BaseTest {

    private final String PATH_TO_IMAGE = "src/test/resources/act.jpg";
    static String encodedFile;
    String uploadedImageId;

    @BeforeEach
    void beforeTest(){
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @Test//добавить файл
    void uploadFileTest(){
        uploadedImageId = given()
                .header("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "act")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test//добавить картинку в избранное
    void favoriteFileTest(){
        //добавляем картинку, в uploadedImageId записываем id (он же imageHash)
        uploadedImageId = given()
                .header("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "act")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
        createFavorite();// метод добавляет картинку в избранное
    }

    void createFavorite(){
        given()
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId)
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
        given()
                .header("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
