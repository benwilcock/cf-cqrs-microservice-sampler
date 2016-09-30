package io.benwilcock.productquery.handlers;

import io.benwilcock.productevents.events.ProductAddedEvent;
import io.benwilcock.productevents.events.ProductSaleableEvent;
import io.benwilcock.productevents.events.ProductUnsaleableEvent;
import io.benwilcock.productquery.domain.Product;
import io.benwilcock.productquery.repository.ProductRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.replay.ReplayAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Ben on 10/08/2015.
 */
@Component
public class ProductViewEventHandler implements ReplayAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProductViewEventHandler.class);

    @Autowired
    private ProductRepository productRepository;

    @EventHandler
    public void handle(ProductAddedEvent event) {
        LOG.info("ProductAddedEvent: [{}] '{}'", event.getId(), event.getName());
        productRepository.save(new Product(event.getId(), event.getName(), false));
    }

    @EventHandler
    public void handle(ProductSaleableEvent event) {
        LOG.info("ProductSaleableEvent: [{}]", event.getId());
        if (productRepository.exists(event.getId())) {
            Product product = productRepository.findOne(event.getId());
            if (!product.isSaleable()) {
                product.setSaleable(true);
                productRepository.save(product);
            }
        }
    }

    @EventHandler
    public void handle(ProductUnsaleableEvent event) {
        LOG.info("ProductUnsaleableEvent: [{}]", event.getId());

        if (productRepository.exists(event.getId())) {
            Product product = productRepository.findOne(event.getId());
            if (product.isSaleable()) {
                product.setSaleable(false);
                productRepository.save(product);
            }
        }
    }

    public void beforeReplay() {
        LOG.info("Event Replay is about to START. Clearing the View...");
    }

    public void afterReplay() {
        LOG.info("Event Replay has FINISHED.");
    }

    public void onReplayFailed(Throwable cause) {
        LOG.error("Event Replay has FAILED.");
    }
}
