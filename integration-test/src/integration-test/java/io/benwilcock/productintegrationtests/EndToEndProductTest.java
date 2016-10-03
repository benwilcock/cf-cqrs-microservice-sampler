package io.benwilcock.productintegrationtests;



import io.benwilcock.utils.Statics;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by ben on 24/02/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EndToEndProductTest {

    private static final Logger LOG = LoggerFactory.getLogger(EndToEndProductTest.class);
    private static String id;
    private static String name;

    @BeforeClass
    public static void setupClass(){
        id = UUID.randomUUID().toString();
        name = "End2EndTest"+id;
    }

    @After
    public void afterEach() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2l);
    }

    /**
     * Send a command to the command-side to create a new Product.
     */
    @Test
    public void testA_PostAProduct() {

        LOG.info("Route: {}{}{}", Statics.COMMAND_APP_ROUTE + Statics.ADD_PRODUCT_ENDPOINT + "/",id, "?name="+name);

        given().
                baseUri(Statics.COMMAND_APP_ROUTE).
        when().
                post(Statics.ADD_PRODUCT_ENDPOINT + "/{id}?name={name}", id, name).
        then().
                statusCode(HttpStatus.SC_CREATED);

    }
    /**
     *  Check that the new Product created event has arrived on the query-side and been
     *  made available for clients to view.
     */

    @Test
    public void testB_GetAProduct(){

        LOG.info("Route: {}{}", Statics.QUERY_APP_ROUTE + Statics.VIEW_PRODUCTS_ENDPOINT + "/",id);

        given().
                baseUri(Statics.QUERY_APP_ROUTE).
        when().
                get(Statics.VIEW_PRODUCTS_ENDPOINT + "/{id}", id).
        then().
                statusCode(HttpStatus.SC_OK).
                body("name", Matchers.is(name));
    }
}
