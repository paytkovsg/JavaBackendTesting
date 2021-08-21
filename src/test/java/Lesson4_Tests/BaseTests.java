package Lesson4_Tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class BaseTests {

    static Properties prop = new Properties();
    static String token;
    static String username;



    @BeforeAll
    public static void beforeAll () {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://api.imgur.com/3";
        getProperties();
        token = prop.getProperty("token");
        username = prop.getProperty("username");
    }


    private static void getProperties(){
        try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
            prop.load(output);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
