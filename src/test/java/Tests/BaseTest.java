package Tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public abstract class BaseTest {
    static Properties prop = new Properties();
    static String token;
    static String username;


    @BeforeAll
    public static void beforeAll () {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
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
