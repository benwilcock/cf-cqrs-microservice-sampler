# Building Scalable CQRS Microservices with Cloud Foundry

**This is currently a Work In Progress!**

This is a non-trivial suite of CQRS microservices that demonstrates how to build CQRS applications using Cloud Foundry and Spring Cloud Services, Spring Boot and the Axon Framework. 

It combines together all of the following elements in order to produce one logical application - a **Product Data Service**.

 - Command & Query Responsibility Separation (CQRS) and Event Sourcing (ES) using the [Axon CQRS Framework](http://www.axonframework.org/)
 - Java Microservices with [Spring Boot](http://projects.spring.io/spring-boot/)
 - Externalised 12-factor configuration using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)
 
I'm using [PCF-Dev](https://pivotal.io/pcf-dev) for this project. PCF-Dev is a free open-source "_cloud-foundry-on-your-desktop_" tool from Pivotal. This tool comes with RabbitMQ, MySQL and Spring Cloud Services out-of-the-box. Apps tested against PCF-Dev will run against Pivotal Cloud Foundry wilthout modification if the same backing-services are present on your target environment.

# Status

I'm in the process of porting the original code from here: https://github.com/benwilcock/microservice-sampler

 - Query Side: Working
 - Command Side: Working
 - Integration Test: Working
 - Cloud Based Config: Working
 - Service Registry: Not Started
 - Service Circuit Breaker: Not Started
 
> Tested on PCF-Dev and PEZ environments. PCF-Dev expected.
 
 
# Setup

> **Requires Mac, Linux or Windows PC with 16GB RAM**

 - Install the CF CLI command line tool as instructed here: https://github.com/cloudfoundry/cli
 - Install VirtualBox 5+ from here: https://www.virtualbox.org/
 - Install the PCF-Dev developer environment as instructed here: https://github.com/pivotal-cf/pcfdev
 
> Once you've installed these, you'll have a local environment that mimics Pivotal Cloud Foundry.

 - Checkout the source code: 
 
 `git clone https://github.com/benwilcock/cf-cqrs-microservice-sampler.git`
 
 - Build the project (requires Java JDK 1.8): 
 
 `./gradlew clean test assemble`
 
> You **don't** need to install Gradle. The source code includes a `gradlew.sh` and a `gradlew.bat` file that you can use to run gradle commands. 

 - Start PCF-Dev with Spring Cloud Services:
 
 `cf dev start -s all`
 
> Starting PCF-Dev takes a while - it emulates the internet :)
 
 - Login to PCF-Dev from the cf CLI: 
 
 `cf dev target`
 
> You have now activated your PCF-Dev client and it's ready to push applications to your local cloud development environment.
 
 - Create a MySQL database backing-service called `mysql` in your local cloud:
 
 `cf create-service p-mysql 512mb mysql`
 
 - Create a RabbitMQ messaging backing-service called `rabbit` in your local cloud:
 
 `cf create-service p-rabbitmq standard rabbit`
 
 - Setup the Spring Cloud Config backing-service in your local cloud (use the script provided, takes a few minutes - use `cf services` to check progress):
 
 `config-server-setup.sh`
 
 - Now **"Push"** the project to your local cloud:
  
 `cf push`

> The push uses the `manifest.yml` file. This file tells the cf CLI where to find the apps to deploy and which backing-services to "bind" the apps to.

 - Run the integration tests to check everything works: 
 
 `./gradlew integration-test:integrationTest`

> The tests should run and pass. Commands executed on the **command** side result in events being stored in the database and sent out via RabbitMQ. The **query** side is listening to RabbitMQ for these events, and then posts records into the Product materialised-view (it's own database).


# About the Author

[Ben Wilcock](https://uk.linkedin.com/in/benwilcock) works for Pivotal as a Cloud Solutions Architect. Ben has a passion for microservices, cloud and mobile applications and helps [Pivotal's Cloud Foundry](http://pivotal.io/platform) customers to become more responsive, innovate faster and gain greater returns from their software investments. Ben is also a respected technology [blogger](http://benwilcock.wordpress.com) who's articles have featured in [DZone](https://dzone.com/users/296242/benwilcock.html), [Java Code Geeks](https://www.javacodegeeks.com/author/ben-wilcock/), [InfoQ](https://www.infoq.com/author/Ben-Wilcock) and more.
