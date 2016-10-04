package io.benwilcock.productcommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by benwilcock on 30/09/2016.
 */

//@RefreshScope
@RestController
public class ConfigCheckController {

    Logger logger = LoggerFactory.getLogger(ConfigCheckController.class);

    @Value("${message}")
    private String message;

    @RequestMapping("/configcheck")
    String getMessage() {
        logger.debug("Configured message is: '{}'", message);
        return this.message;
    }
}
