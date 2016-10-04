package io.benwilcock.productquery.configuration;

import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ben on 19/02/16.
 */
@Configuration
@AnnotationDriven
public class RabbitConfiguration {

    @Value("${spring.application.exchange}")
    String exchangeName;

    @Value("${spring.application.queue}")
    String queueName;

    @Bean
    Queue eventStream() {
        return new Queue(queueName, false, false, true);
    }

    @Bean
    FanoutExchange eventBusExchange() {
        return new FanoutExchange(exchangeName, true, false);
    }

    @Bean
    Binding binding() {
        return new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, "*.*", null);
    }

    @Bean
    @Required
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory, FanoutExchange fanoutExchange, Queue eventStream, Binding binding) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        admin.declareExchange(fanoutExchange);
        admin.declareQueue(eventStream);
        admin.declareBinding(binding);
        return admin;
    }
}
