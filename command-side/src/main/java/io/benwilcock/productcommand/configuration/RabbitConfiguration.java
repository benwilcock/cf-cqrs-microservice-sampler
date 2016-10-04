package io.benwilcock.productcommand.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ben on 23/02/16.
 */
@Configuration
public class RabbitConfiguration {

    @Value("${spring.application.exchange}")
    private String exchangeName;

    @Value("${spring.application.queue}")
    private String queueName;

    @Bean
    Queue defaultStream() {
        return new Queue(queueName, true);
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

    @Bean
    RabbitTransactionManager transactionManager(ConnectionFactory connectionFactory){
        RabbitTransactionManager txMgr = new RabbitTransactionManager(connectionFactory);
        return txMgr;
    }
}
