package io.benwilcock.productquery;

import io.benwilcock.productquery.domain.Product;
import io.benwilcock.productquery.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by benwilcock on 30/09/2016.
 */

@RestController
public class InsertOneController {

    Logger logger = LoggerFactory.getLogger(InsertOneController.class);

    @Autowired
    ProductRepository repo;

    @RequestMapping("/insertone")
    Product injectProduct() {
        Product prod = repo.save(new Product(UUID.randomUUID().toString(), "Some thing!", false));
        logger.debug("Product created and inserted: {}", prod);
        return prod;
    }
}
