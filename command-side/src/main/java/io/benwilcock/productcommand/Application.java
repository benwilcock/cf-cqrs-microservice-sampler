package io.benwilcock.productcommand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by ben on 19/01/16.
 */
@SpringBootApplication
public class Application {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
    }
}

//@RestController
//class ServiceInstanceRestController {
//
//    @Autowired
//    private DiscoveryClient discoveryClient;
//
//    @Value("${spring.application.name}")
//    private String appName;
//
//    @RequestMapping("/instances")
//    public List<ServiceInstance> serviceInstancesByApplicationName() {
//        return this.discoveryClient.getInstances(appName);
//    }
//}


