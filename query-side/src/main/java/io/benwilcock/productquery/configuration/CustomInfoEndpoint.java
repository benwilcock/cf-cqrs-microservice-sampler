package io.benwilcock.config;

import io.benwilcock.productquery.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * Created by benwilcock on 04/08/2016.
 * This is the method we currently use to add information to the /info endpoint in Actuator.
 */
@Configuration
public class CustomInfoEndpoint {

    @Autowired
    Status status;

    /** These properties will show up in Spring Boot Actuator's /info endpoint **/
    @Autowired
    public void setInfoProperties(ConfigurableEnvironment env) {

        // Add the status to the /info endpoint using Properties
        Properties props = new Properties();
        props.put("info.id", status.getId());
        props.put("info.index", status.getIndex());
        props.put("info.sqldb", status.getSql());
        props.put("info.rabbitmq", status.getRabbit());
        props.put("info.mongodb", status.getMongo());

        // Set the new properties into the environment
        env.getPropertySources().addFirst(new PropertiesPropertySource("extra-info-props", props));
    }
}