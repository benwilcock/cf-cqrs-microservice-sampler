# Building Scalable CQRS Microservices with Cloud Foundry

** Work In Progress! **

This is a non-trivial suite of CQRS microservices that demonstrates how to build CQRS applications using Cloud Foundry and Spring Cloud Services, Spring Boot and the Axon Framework. 

It combines together all of the following elements in order to produce one logical application - a **Product Data Service**.

 - Command & Query Responsibility Separation (CQRS) using the [Axon CQRS Framework](http://www.axonframework.org/)
 - Event Sourcing & Materialised Views (with RabbitMQ, MongoDB and MySQL)
 - Java Microservices with [Spring Boot](http://projects.spring.io/spring-boot/)
 - External configuration on Cloud Foundry using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)


 
 I'm testing the port with [PCF-Dev](https://pivotal.io/pcf-dev), the free "_cloud-foundry-on-your-desktop_" tool from Pivotal. This tool comes with Rabbit and MySQL built-in, but will require you to add a local MongoDB backing-service using the `cf cups` command.

# Status

I'm in the process of porting the original code from here: https://github.com/benwilcock/microservice-sampler

 - Query Side: Working
 - Command Side: Working
 - Integration Test: Working
 - Spring Cloud Config: Working
 - Spring Cloud Registry: Not Started
 - Spring Cloud Circuit Breaker: Not Started

# About the Author

[Ben Wilcock](https://uk.linkedin.com/in/benwilcock) works for Pivotal as a Cloud Solutions Architect. Ben has a passion for microservices, cloud and mobile applications and helps [Pivotal's Cloud Foundry](http://pivotal.io/platform) customers to become more responsive, innovate faster and gain greater returns from their software investments. Ben is also a respected technology [blogger](http://benwilcock.wordpress.com) who's articles have featured in [DZone](https://dzone.com/users/296242/benwilcock.html), [Java Code Geeks](https://www.javacodegeeks.com/author/ben-wilcock/), [InfoQ](https://www.infoq.com/author/Ben-Wilcock) and more.
