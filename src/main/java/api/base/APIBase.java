
package api.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class APIBase {

    private static final Logger logger = LoggerFactory.getLogger(APIBase.class);
    protected String baseURL;

    public APIBase(String baseURL) {
        this.baseURL = baseURL;

        // Disables strict SSL certificate validation for HTTPS requests.
        RestAssured.useRelaxedHTTPSValidation();
    }

    public Response get(String endpoint){

       Response response = RestAssured.given().contentType(ContentType.JSON).when().log().all().get(baseURL + endpoint);
        logger.info("Response:" + response.asString());
        return response;
}


    public Response post(String endpoint, Object body){
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .body(body).when().log().all().post(baseURL + endpoint);
         logger.info("Response" + response.toString());
        return response;
    }

    public Response put(String endpoint, Object body, String token){

        Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .cookie("token", token).body(body).when()
                .log().all().put(baseURL + endpoint);

        logger.info("Response" + response.toString());
        return response;
}

    public Response delete(String endpoint, String token){

        Response response = RestAssured.given().contentType(ContentType.JSON)
                .cookie("token", token).when()
                .log().all().delete(baseURL + endpoint);

        return response;
    }

    public Response uploadFile (String endpoint, File file){

        Response response = RestAssured.given().contentType("multipart/form-date")
                .accept(ContentType.JSON)
                .multiPart("file", file)
                .post(baseURL + endpoint);

            return response;
    }

}
