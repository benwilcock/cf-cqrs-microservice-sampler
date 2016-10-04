package io.benwilcock.productcommand.configuration;

import io.benwilcock.productcommand.aggregates.ProductAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPConsumerConfiguration;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.eventstore.mongo.MongoTemplate;
import org.axonframework.serializer.json.JacksonSerializer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * Created by ben on 23/02/16.
 */
@Configuration
@AnnotationDriven
public class AxonConfiguration {

    private static final String AMQP_CONFIG_KEY = "AMQP.Config";

    @Value("${spring.application.queue}")
    private String queueName;

    @Value("${spring.application.exchange}")
    private String exchangeName;

    @Bean
    JacksonSerializer axonJsonSerializer() {
        return new JacksonSerializer();
    }

    @Bean
    ListenerContainerLifecycleManager listenerContainerLifecycleManager(ConnectionFactory connectionFactory) {
        ListenerContainerLifecycleManager mgr = new ListenerContainerLifecycleManager();
        mgr.setConnectionFactory(connectionFactory);
        return mgr;
    }

    @Bean
    SpringAMQPConsumerConfiguration springAMQPConsumerConfiguration(RabbitTransactionManager transactionManager) {
        SpringAMQPConsumerConfiguration cfg = new SpringAMQPConsumerConfiguration();
        cfg.setTransactionManager(transactionManager);
        cfg.setQueueName(queueName);
        cfg.setTxSize(10);
        return cfg;
    }


    @Bean
    SimpleCluster simpleCluster(SpringAMQPConsumerConfiguration springAMQPConsumerConfiguration) {
        SimpleCluster cluster = new SimpleCluster(queueName);
        cluster.getMetaData().setProperty(AMQP_CONFIG_KEY, springAMQPConsumerConfiguration);
        return cluster;
    }

    @Bean
    EventBusTerminal terminal(ConnectionFactory connectionFactory, JacksonSerializer jacksonSerializer, ListenerContainerLifecycleManager listenerContainerLifecycleManager) {
        SpringAMQPTerminal terminal = new SpringAMQPTerminal();
        terminal.setConnectionFactory(connectionFactory);
        terminal.setExchangeName(exchangeName);
        terminal.setDurable(true);
        terminal.setTransactional(true);
        terminal.setSerializer(jacksonSerializer);
        terminal.setListenerContainerLifecycleManager(listenerContainerLifecycleManager);
        return terminal;
    }

    @Bean
    EventBus eventBus(SimpleCluster simpleCluster, EventBusTerminal eventBusTerminal) {
        return new ClusteringEventBus(new DefaultClusterSelector(simpleCluster), eventBusTerminal);
    }

    @Bean(name = "axonMongoTemplate")
    MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        MongoTemplate template = new DefaultMongoTemplate(mongoDbFactory.getDb().getMongo());
        return template;
    }

    @Bean
    EventStore eventStore(JacksonSerializer jacksonSerializer, MongoTemplate mongoTemplate) {
        MongoEventStore eventStore = new MongoEventStore(jacksonSerializer, mongoTemplate);
        return eventStore;
    }

    @Bean
    EventSourcingRepository<ProductAggregate> productEventSourcingRepository(EventStore eventStore, EventBus eventBus) {
        EventSourcingRepository<ProductAggregate> repo = new EventSourcingRepository<ProductAggregate>(ProductAggregate.class, eventStore);
        repo.setEventBus(eventBus);
        return repo;
    }

    @Bean
    CommandBus commandBus() {
        SimpleCommandBus commandBus = new SimpleCommandBus();
        return commandBus;
    }

    @Bean
    CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean(CommandBus commandBus) {
        CommandGatewayFactoryBean<CommandGateway> factory = new CommandGatewayFactoryBean<CommandGateway>();
        factory.setCommandBus(commandBus);
        return factory;
    }

    /**
     * This method allows Axon to automatically find your @EventHandler's
     *
     * @return
     */
    @Bean
    AnnotationEventListenerBeanPostProcessor eventListenerBeanPostProcessor(EventBus eventBus) {
        AnnotationEventListenerBeanPostProcessor proc = new AnnotationEventListenerBeanPostProcessor();
        proc.setEventBus(eventBus);
        return proc;
    }

    /**
     * This method allows Axon to automatically find your @CommandHandler's
     *
     * @return
     */
    @Bean
    AnnotationCommandHandlerBeanPostProcessor commandHandlerBeanPostProcessor(CommandBus commandBus) {
        AnnotationCommandHandlerBeanPostProcessor proc = new AnnotationCommandHandlerBeanPostProcessor();
        proc.setCommandBus(commandBus);
        return proc;
    }

    /**
     * This method registers your Aggregate Root as a @CommandHandler
     *
     * @return
     */
    @Bean
    AggregateAnnotationCommandHandler<ProductAggregate> productAggregateCommandHandler(EventSourcingRepository<ProductAggregate> eventSourcingRepository, CommandBus commandBus) {
        AggregateAnnotationCommandHandler<ProductAggregate> handler = new AggregateAnnotationCommandHandler<ProductAggregate>(
                ProductAggregate.class,
                eventSourcingRepository,
                commandBus);
        return handler;
    }

}
