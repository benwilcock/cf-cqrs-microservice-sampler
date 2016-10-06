# Building Scalable CQRS Microservices with Cloud Foundry

This project demonstrates how to build cloud native Command & Query Responsibility Separation and Event Sourcing applications using Pivotal Cloud Foundry.

It combines the following technical elements in order to produce one logical application - a **'Product Data Service'**...

 - CQRS and Event Sourcing using the [Axon CQRS Framework](http://www.axonframework.org/)
 - Microservices using [Spring Boot](http://projects.spring.io/spring-boot/)
 - 12-Factor configuration using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)
 - Various other Spring Cloud things in the background like:-
   - Spring Cloud Service Registry for service discovery (Netflix Eureka)
   - Spring Cloud Bus (for push-enabled configuration changes)
   - Spring Cloud Connectors (for connecting to backing-stores like MySQL)
 
I'm using [Pivotal PCF-Dev](https://pivotal.io/pcf-dev) for this demo.
 
> **About PCF-Dev.** PCF-Dev is a free open-source _'cloud-foundry-on-your-desktop'_ application from Pivotal. It's designed for cloud developers; emulates Pivotal Cloud Foundry; and comes with RabbitMQ, MySQL and Spring Cloud Services built-in. Apps tested against PCF-Dev will run against Pivotal Cloud Foundry without modification (assuming that the same backing-services required by your app are present).

If you're not interested in the cloud (why not?) then the original non-PaaS version of this project can be found here: https://github.com/benwilcock/microservice-sampler

If you'd like more general information on CQRS/ES Microservice architecture check out this handy [CQRS slideshare](http://www.slideshare.net/BenWilcock1/microservice-architecture-with-cqrs-and-event-sourcing).
 
## Getting Started

**To follow this tutorial you'll need a Mac, Linux or Windows PC with 16GB RAM, Java JDK 1.8 and Git.**

### Install the required software

1. Install the CF CLI command line tool (currently v6.22.1+) as instructed here: https://github.com/cloudfoundry/cli
 
2. Install VirtualBox 5+ from here: https://www.virtualbox.org
 
3. Install the PCF-Dev (currently v0.20.0+) developer environment as instructed here: https://github.com/pivotal-cf/pcfdev
 
> Once you've installed these tools, you're ready to create a local development environment that mimics Pivotal Cloud Foundry.

### Prepare the local cloud environment

1. Checkout the source code for this project to your machine and build it: 
 
````bash
$ git clone https://github.com/benwilcock/cf-cqrs-microservice-sampler.git
$ cd cf-cqrs-microservice-sampler
$ ./gradlew clean assemble
````
 
> You **don't** need to install Gradle. The source code includes a `gradlew.sh` and a `gradlew.bat` file that you can use to run gradle commands. 

2. Start PCF-Dev with Spring Cloud Services:
 
  `cf dev start -s all`
 
> Starting PCF-Dev takes about 8 minutes depending on your PC. It emulates complex cloud infrastructure in a local VirtualBox environemnt. Once started it can be suspended and resumed to save you time.
 
3. Attach to PCF-Dev from the cf CLI: 
 
 `cf dev target`
 
> You have now activated your PCF-Dev client and it's ready to push applications to your local cloud development environment.
 
4. Create a MySQL database backing-service called `mysql` in your local cloud:
 
 `cf create-service p-mysql 512mb mysql`
 
5. Create a RabbitMQ messaging backing-service called `rabbit` in your local cloud:
 
 `cf create-service p-rabbitmq standard rabbit`
 
6. Setup the Spring Cloud Config backing-service in your local cloud (takes a few minutes - use `cf services` to check progress):
 
 `cf create-service p-config-server standard config -c ./config-server-setup.json`
 
7. Create a Spring Cloud Service Registry:

 `cf create-service p-service-registry standard registry`
 
8. Now **"Push"** the project to your local cloud:
  
 `cf push`

> The push uses the `manifest.yml` file. This file tells the cf CLI where to find the apps to deploy and which backing-services to "bind" the apps to.

### Test the app

1. Run the integration tests to check everything works: 
 
 `./gradlew integration-test:integrationTest`

> The tests should run and pass. Commands executed on the **command** side result in events being stored in the database and sent out via RabbitMQ. The **query** side is listening to RabbitMQ for these events, and then posts records into the Product materialised-view (it's own database).

2. Use the "add" command to add a Product:
 
`curl -X POST http://command.local.pcfdev.io:80/add/fb226b13-65f5-47bf-8a06-f97affaaf60f?name=MyTestProduct`

3. Query for the Product you just added:

`curl -X GET http://query.local.pcfdev.io:80/products/fb226b13-65f5-47bf-8a06-f97affaaf60f`

> You should see the following JSON output:-

````json
{
  "name" : "MyTestProduct",
  "saleable" : false,
  "_links" : {
    "self" : {
      "href" : "http://query.local.pcfdev.io/products/fb226b13-65f5-47bf-8a06-f97affaaf60f"
    },
    "product" : {
      "href" : "http://query.local.pcfdev.io/products/fb226b13-65f5-47bf-8a06-f97affaaf60f"
    }
  }
````

Notice how the 'commands' go to the command url (`command.local.pcfdev.io`) and the queries go to the query url (`query.local.pcfdev.io`)s. This means you can __scale__ the command and query apps separately depending on load.

## Benefits

There are several clear business benefits that make switching to Cloud Foundry an attractive option for this project.

 - **Lower Maintenance**
 
   - I don't have to script my own PaaS features (like auto-restart).
   - I can provision my own backing-services (like MySQL etc.) without Ops.
   - I don't have to create, manage, secure or test container images.
   - I can achieve Continuous Delivery and add value constantly.

 - **Better Resiliance**
 
   - Cloud Foundry will restart my apps for me if they crash.
   - Cloud Foundry will scale my apps for me horizontally.
   - Cloud Foundry supports zero downtime app upgrades (blue/green).
   
 - **Detailed Operational Insights**
 
   - Cloud Foundry includes built in Metrics.
   - Cloud Foundry supports external logging services like Splunk and Logstash.
   
 - **Lower Cost and Fewer Resources**
 
   - Cloud Foundry uses cloud infrastructure like AWS, Azure, GCP etc.
   - Cloud Foundry uses cloud infrastructure efficiently, keeping running costs lows.
   - Developers can be more productive and retain greater control.
   - Operators can spend less time on the 'scaffolding' and more time on real operational issues.
   
## About the Author

[Ben Wilcock](https://uk.linkedin.com/in/benwilcock) works for Pivotal as a Cloud Solutions Architect. Ben has a passion for microservices, cloud and mobile applications and helps [Pivotal's Cloud Foundry](http://pivotal.io/platform) customers to become more responsive, innovate faster and gain greater returns from their software investments. Ben is also a respected technology [blogger](http://benwilcock.wordpress.com) who's articles have featured in [DZone](https://dzone.com/users/296242/benwilcock.html), [Java Code Geeks](https://www.javacodegeeks.com/author/ben-wilcock/), [InfoQ](https://www.infoq.com/author/Ben-Wilcock) and more.
