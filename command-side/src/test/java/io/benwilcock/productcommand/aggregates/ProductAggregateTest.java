package io.benwilcock.productcommand.aggregates;

import io.benwilcock.productcommand.commands.AddProductCommand;
import io.benwilcock.productcommand.commands.MarkProductAsSaleableCommand;
import io.benwilcock.productcommand.commands.MarkProductAsUnsaleableCommand;
import io.benwilcock.productevents.events.AbstractEvent;
import io.benwilcock.productevents.events.ProductAddedEvent;
import io.benwilcock.productevents.events.ProductSaleableEvent;
import io.benwilcock.productevents.events.ProductUnsaleableEvent;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 07/08/2015.
 */
public class ProductAggregateTest {

    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ProductAggregate.class);
    }

    @Test
    public void testAddProduct() throws Exception {
        fixture.given()
                .when(new AddProductCommand("product-1", "product name"))
                .expectEvents(new ProductAddedEvent("product-1", "product name"));
    }

    @Test
    public void testMarkProductItemAsSaleable() throws Exception {
        fixture.given(new ProductAddedEvent("product-2", "product name"))
                .when(new MarkProductAsSaleableCommand("product-2"))
                .expectVoidReturnType()
                .expectEvents(new ProductSaleableEvent("product-2"));
    }

    @Test
    public void testMarkProductItemAsUnsaleableIsAllowed() throws Exception {
        List<AbstractEvent> events = new ArrayList<AbstractEvent>();
        events.add(new ProductAddedEvent("product-3", "product name"));
        events.add(new ProductSaleableEvent("product-3"));

        fixture.given(events)
                .when(new MarkProductAsUnsaleableCommand("product-3"))
                .expectVoidReturnType()
                .expectEvents(new ProductUnsaleableEvent("product-3"));
    }

    @Test
    public void testMarkProductItemAsUnsaleableIsPrevented() throws Exception {
        List<AbstractEvent> events = new ArrayList<AbstractEvent>();
        events.add(new ProductAddedEvent("product-3", "product name"));

        fixture.given(events)
                .when(new MarkProductAsUnsaleableCommand("product-3"))
                .expectException(IllegalStateException.class);
    }
}
