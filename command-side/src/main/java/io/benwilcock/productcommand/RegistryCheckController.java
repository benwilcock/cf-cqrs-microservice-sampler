package io.benwilcock.productcommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by benwilcock on 06/10/2016.
 */
@RestController
public class RegistryCheckController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping(value = "/registrycheck", method = RequestMethod.GET)
    public List<ServiceInstance> serviceInstancesByApplicationName() {
        Logger logger = LoggerFactory.getLogger(RegistryCheckController.class);
        List<ServiceInstance> instances = this.discoveryClient.getInstances(appName);
        instances.forEach(instance -> logger.info("Instance Id: {} Uri: {}", instance.getServiceId(), instance.getUri().toString()));
        return instances;
    }
}
