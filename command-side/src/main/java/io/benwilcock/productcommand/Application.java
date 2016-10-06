package io.benwilcock.productcommand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/**
 * Created by ben on 19/01/16.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Application {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
    }
}


