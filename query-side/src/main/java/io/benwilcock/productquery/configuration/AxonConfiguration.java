package io.benwilcock.productquery.configuration;

import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPConsumerConfiguration;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.serializer.json.JacksonSerializer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by ben on 18/02/16.
 */
@Configuration
@AnnotationDriven
class AxonConfiguration {

    private static final String AMQP_CONFIG_KEY = "AMQP.Config";

    @Value("${query.exchangeName}")
    String exchangeName;

    @Value("${query.queueName}")
    String queueName;

    @Value("${query.terminalName}")
    String terminalName;

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


    @Bean
    JacksonSerializer axonJsonSerializer() {
        return new JacksonSerializer();
    }

    @Bean
    ListenerContainerLifecycleManager listenerContainerLifecycleManager(ConnectionFactory connectionFactory) {
        ListenerContainerLifecycleManager listenerContainerLifecycleManager = new ListenerContainerLifecycleManager();
        listenerContainerLifecycleManager.setConnectionFactory(connectionFactory);
        return listenerContainerLifecycleManager;
    }

    @Bean
    SpringAMQPConsumerConfiguration springAMQPConsumerConfiguration(PlatformTransactionManager transactionManager) {
        SpringAMQPConsumerConfiguration amqpConsumerConfiguration = new SpringAMQPConsumerConfiguration();
        amqpConsumerConfiguration.setTxSize(10);
        amqpConsumerConfiguration.setTransactionManager(transactionManager);
        amqpConsumerConfiguration.setQueueName(queueName);
        return amqpConsumerConfiguration;
    }


    @Bean
    SimpleCluster simpleCluster(SpringAMQPConsumerConfiguration springAMQPConsumerConfiguration) {
        SimpleCluster simpleCluster = new SimpleCluster(queueName);
        simpleCluster.getMetaData().setProperty(AMQP_CONFIG_KEY, springAMQPConsumerConfiguration);
        return simpleCluster;
    }

    @Bean
    EventBusTerminal terminal(ConnectionFactory connectionFactory, JacksonSerializer jacksonSerializer, ListenerContainerLifecycleManager listenerContainerLifecycleManager) {
        SpringAMQPTerminal terminal = new SpringAMQPTerminal();
        terminal.setConnectionFactory(connectionFactory);
        terminal.setSerializer(jacksonSerializer);
        terminal.setExchangeName(terminalName);
        terminal.setListenerContainerLifecycleManager(listenerContainerLifecycleManager);
        terminal.setDurable(true);
        terminal.setTransactional(true);
        return terminal;
    }

    @Bean
    EventBus eventBus(SimpleCluster simpleCluster, EventBusTerminal terminal) {
        return new ClusteringEventBus(new DefaultClusterSelector(simpleCluster), terminal);
    }

}
