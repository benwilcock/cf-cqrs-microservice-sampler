package io.benwilcock.productcommand.configuration;

import io.benwilcock.productcommand.aggregates.ProductAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.common.jpa.ContainerManagedEntityManagerProvider;
import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPConsumerConfiguration;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.jpa.JpaEventStore;
import org.axonframework.serializer.json.JacksonSerializer;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Created by ben on 23/02/16.
 */
@Configuration
@AnnotationDriven
@EnableTransactionManagement
@EntityScan(basePackages = "org.axonframework.eventstore.jpa")
public class AxonConfiguration {

    private static final String AMQP_CONFIG_KEY = "AMQP.Config";

    @Value("${spring.application.queue}")
    private String queueName;

    @Value("${spring.application.exchange}")
    private String exchangeName;

    @PersistenceContext
    EntityManager entityManager;

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
    SpringAMQPConsumerConfiguration springAMQPConsumerConfiguration(PlatformTransactionManager transactionManager) {
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

    @Bean
    JpaEventStore jpaEventStore(JacksonSerializer jacksonSerializer){
        ContainerManagedEntityManagerProvider containerManagedEntityManagerProvider = new ContainerManagedEntityManagerProvider();
        containerManagedEntityManagerProvider.setEntityManager(entityManager);
        JpaEventStore jpaEventStore = new JpaEventStore(containerManagedEntityManagerProvider, jacksonSerializer);
        return jpaEventStore;
    }

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
    EventSourcingRepository<ProductAggregate> productEventSourcingRepository(JpaEventStore eventStore, EventBus eventBus) {
        EventSourcingRepository<ProductAggregate> repo = new EventSourcingRepository<ProductAggregate>(ProductAggregate.class, eventStore);
        repo.setEventBus(eventBus);
        return repo;
    }

    @Bean
    CommandBus commandBus(PlatformTransactionManager transactionManager) {
        SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setTransactionManager(new SpringTransactionManager(transactionManager));
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
