package io.benwilcock.utils;

/**
 * Created by ben on 24/02/16.
 */
public class Statics {

    public static final String PROTOCOL = "http://";
    public static final String CMD_PORT = ":9001";
    public static final String QRY_PORT = ":9002";

    public static final String DOMAIN = "localhost";
    public static final String COMMAND_APP_NAME = "";
    public static final String QUERY_APP_NAME = "";

    public static final String COMMAND_APP_ROUTE = PROTOCOL + COMMAND_APP_NAME + DOMAIN + CMD_PORT;
    public static final String QUERY_APP_ROUTE = PROTOCOL + QUERY_APP_NAME + DOMAIN + QRY_PORT;

    public static final String VIEW_PRODUCTS_ENDPOINT = "/products";
    public static final String ADD_PRODUCT_ENDPOINT = "/add";

    public static final String PROD_CMD_MESSAGE = "Greetings from the PRODUCT-COMMAND-SIDE microservice [using the CONFIG SERVICE config].";
    public static final String PROD_QRY_MESSAGE = "Greetings from the PRODUCT-QUERY-SIDE microservice [using the CONFIG SERVICE config].";

    public static final boolean PRODUCTION = Boolean.valueOf(System.getProperty("production", "true"));

}
