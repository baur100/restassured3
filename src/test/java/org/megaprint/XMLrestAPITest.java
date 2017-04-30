package org.megaprint;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

/**
 * Created by Baurz on 4/16/2017.
 */
public class XMLrestAPITest {

    Properties prop=new Properties();
    Response resp;
    String query1=null;
    XmlPath xpath;

    @BeforeMethod
    public void init() throws FileNotFoundException {

        FileInputStream fis=new FileInputStream("data\\env.properties");
        try {
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestAssured.baseURI=prop.getProperty("HOST");
        try {
            query1=GenerateStringFromFile("data\\query1.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//Find place
    @Test
    public void test1(){
        resp=given().
                param("location","-33.8670522,151.1957362").
                param("key",prop.getProperty("KEY")).
                param("radius","5000").
        when().
                get("/maps/api/place/nearbysearch/xml").
        then().
                assertThat().
                statusCode(200).
                and().
                contentType(ContentType.XML).
        extract().response();
//        System.out.println(resp.asString());
    }
//Create place
    @Test
    public void test2(){
        resp=given().
                queryParam("key",prop.getProperty("KEY")).
                body(query1).
        when().
                post("/maps/api/place/add/xml").
        then().
                assertThat().
                statusCode(200).
                and().
                contentType(ContentType.XML).
        extract().response();
        System.out.println(resp.asString());
        xpath=new XmlPath(resp.asString());

    }
//Delete Place
    @Test
    public void test3(){
        String body=xpath.get("PlaceAddResponse.place_id");
        given().
                queryParam("key",prop.getProperty("KEY")).
                body("<PlaceDeleteRequest><placeid>"+body+"</placeid></PlaceDeleteRequest>").
        when().
                post("/maps/api/place/delete/xml").
        then().
                assertThat().
                statusCode(200).
                and().
                contentType(ContentType.XML);

    }
    public static String GenerateStringFromFile(String path) throws Exception{
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}

