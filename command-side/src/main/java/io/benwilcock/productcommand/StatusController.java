package io.benwilcock.productcommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by benwilcock on 03/08/2016.
 */
@RestController
public class StatusController {

    Logger logger = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    Status status;

    @RequestMapping("/status")
    public Status index(){
        logger.debug("Returning status: {}", status.toString());
        return status;
    }
}