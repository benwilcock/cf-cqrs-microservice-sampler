package io.benwilcock.productcommand.handlers;

import io.benwilcock.productevents.events.ProductAddedEvent;
import io.benwilcock.productevents.events.ProductSaleableEvent;
import io.benwilcock.productevents.events.ProductUnsaleableEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * EventHandler's (a.k.a. EventListeners) are used to react to events and perform associated
 * actions.
 * Created by ben on 24/09/15.
 */
@Component
public class EventLoggingHandler {

    @Value("${INSTANCE_GUID}")
    String id;

    private static final Logger LOG = LoggerFactory.getLogger(EventLoggingHandler.class);

    @EventHandler
    public void handle(ProductAddedEvent event) {
        LOG.debug("Instance:{} EventType:{} EventId:[{}] '{}'", id, event.getClass().getSimpleName(), event.getId(), event.getName());
    }

    @EventHandler
    public void handle(ProductSaleableEvent event) {
        LOG.debug("Instance:{} EventType:{} EventId:[{}]", id, event.getClass().getSimpleName(), event.getId());
    }

    @EventHandler
    public void handle(ProductUnsaleableEvent event) {
        LOG.debug("Instance:{} EventType:{} EventId:[{}]", id, event.getClass().getSimpleName(), event.getId());
    }
}






