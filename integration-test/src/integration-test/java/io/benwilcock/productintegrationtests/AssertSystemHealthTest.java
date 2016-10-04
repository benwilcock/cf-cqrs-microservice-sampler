package io.benwilcock.productintegrationtests;


import io.benwilcock.utils.Statics;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by ben on 24/02/16.
 */
public class AssertSystemHealthTest {

    private static final Logger LOG = LoggerFactory.getLogger(AssertSystemHealthTest.class);

    private String productId = UUID.randomUUID().toString();


    @Before
    public void setup(){
        System.out.println("PRODUCTION MODE: " + Statics.PRODUCTION);
    }


    @Test
    public void assertCommandSideConfig(){

        given().
                baseUri(Statics.COMMAND_APP_ROUTE).
                when().
                get("/configcheck").
                then().
                statusCode(HttpStatus.SC_OK).
                body(Matchers.is(Statics.PROD_CMD_MESSAGE));
    }

    @Test
    public void assertCommandSideHealth() {

        given().
                baseUri(Statics.COMMAND_APP_ROUTE).
                when().
                get("/health").
                then().
                statusCode(HttpStatus.SC_OK).
                body("status", Matchers.is("UP")).
                body("rabbit.status", Matchers.is("UP")).
                body("configServer.status", Matchers.is("UP")).
                body("db.status", Matchers.is("UP")).
                body("db.database", Matchers.is("MySQL"));

//        given().
//                port(Statics.PORT).
//                when().
//                get("/commands/instances").
//                then().
//                statusCode(HttpStatus.SC_OK).
//                body("serviceId", Matchers.hasItems(Statics.CMD_SERVICE_ID)).
//                body("instanceInfo.actionType", Matchers.hasItems("ADDED"));
    }

    @Test
    public void assertQuerySideConfig(){

        given().
                baseUri(Statics.QUERY_APP_ROUTE).
                when().
                get("/configcheck").
                then().
                statusCode(HttpStatus.SC_OK).
                body(Matchers.is(Statics.PROD_QRY_MESSAGE));
    }

    @Test
    public void assertQuerySideHealth() {

        given().
                baseUri(Statics.QUERY_APP_ROUTE).
                when().
                get("/health/").
                then().
                statusCode(HttpStatus.SC_OK).
                body("status", Matchers.is("UP")).
                body("configServer.status", Matchers.is("UP")).
                body("db.status", Matchers.is("UP")).
                body("rabbit.status", Matchers.is("UP")).
                body("db.database", Matchers.is("MySQL"));


//        given().
//                baseUri(Statics.QUERY_APP_ROUTE).
//                when().
//                get("/queries/instances").
//                then().
//                statusCode(HttpStatus.SC_OK).
//                body("serviceId", Matchers.hasItems(Statics.QRY_SERVICE_ID)).
//                body("instanceInfo.actionType", Matchers.hasItems("ADDED"));
    }
}
