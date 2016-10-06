# Building Scalable CQRS Microservices with Cloud Foundry

This project demonstrates how to build cloud native Command & Query Responsibility Separation and Event Sourcing applications using Pivotal Cloud Foundry, Spring Cloud Services, Spring Boot and the Axon Framework. 

It combines together all of the following elements in order to produce one logical application - a **'Product Data Service'**.

 - CQRS and Event Sourcing using the [Axon CQRS Framework](http://www.axonframework.org/)
 - [Spring Boot](http://projects.spring.io/spring-boot/) Microservices
 - Externalised 12-factor configuration using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)
 
I'm using [PCF-Dev](https://pivotal.io/pcf-dev) for this project.
 
> PCF-Dev is a free open-source _cloud-foundry-on-your-desktop_ from Pivotal. It's designed for cloud developers and comes with RabbitMQ, MySQL and Spring Cloud Services built-in. Apps tested against PCF-Dev will run against Pivotal Cloud Foundry without modification (assuming the same backing-services are present).

If you're not interested in the cloud (why?) then the original non-cloud project can be found here: https://github.com/benwilcock/microservice-sampler
 
# Getting Started

> **To follow this tutorial you'll need a Mac, Linux or Windows PC with 16GB RAM and Java JDK 1.8.**

 - Install the CF CLI command line tool (v6.22.1+) as instructed here: https://github.com/cloudfoundry/cli
 
 - Install VirtualBox 5+ from here: https://www.virtualbox.org
 
 - Install the PCF-Dev (v0.20.0+) developer environment as instructed here: https://github.com/pivotal-cf/pcfdev
 
> Once you've installed these tools, you're ready to create a local development environment that mimics Pivotal Cloud Foundry.

 - Checkout the source code for this project and build it: 
 
````bash
$ git clone https://github.com/benwilcock/cf-cqrs-microservice-sampler.git
$ ./gradlew clean test assemble
````
 
> You **don't** need to install Gradle. The source code includes a `gradlew.sh` and a `gradlew.bat` file that you can use to run gradle commands. 

 - Start PCF-Dev with Spring Cloud Services:
 
 `cf dev start -s all`
 
> Starting PCF-Dev takes about 8 minutes depending on your PC. It emulates complex cloud infrastructure in a local VirtualBox environemnt.
 
 - Attach to PCF-Dev from the cf CLI: 
 
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

 - Use the "add" command to add a Product:
 
`curl -X POST http://command.local.pcfdev.io:80/add/fb226b13-65f5-47bf-8a06-f97affaaf60f?name=MyTestProduct`

 - Query for the Product you just added:

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

# About the Author

[Ben Wilcock](https://uk.linkedin.com/in/benwilcock) works for Pivotal as a Cloud Solutions Architect. Ben has a passion for microservices, cloud and mobile applications and helps [Pivotal's Cloud Foundry](http://pivotal.io/platform) customers to become more responsive, innovate faster and gain greater returns from their software investments. Ben is also a respected technology [blogger](http://benwilcock.wordpress.com) who's articles have featured in [DZone](https://dzone.com/users/296242/benwilcock.html), [Java Code Geeks](https://www.javacodegeeks.com/author/ben-wilcock/), [InfoQ](https://www.infoq.com/author/Ben-Wilcock) and more.
