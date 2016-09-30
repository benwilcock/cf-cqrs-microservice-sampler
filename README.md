# Building Scalable CQRS Microservices with Cloud Foundry

** Work In Progress! **

This is a non-trivial suite of CQRS microservices that demonstrates how to build CQRS apps using Cloud Foundry and Spring Cloud Services. It combines together all of the following elements in order to produce one logical application - a **Product Data Service**.

 - Service Registry using [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/) (Eureka)
 - External configuration using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)
 - Java Microservices with [Spring Boot](http://projects.spring.io/spring-boot/)
 - Command & Query Responsibility Separation (CQRS) using the [Axon CQRS Framework](http://www.axonframework.org/)
 - Event Sourcing & Materialised Views with RabbitMQ, MongoDB and MySQL

# Status

I'm in the process of porting the code from here: https://github.com/benwilcock/microservice-sampler

 - Query Side: Started
 - Command Side: Not Started
 - Spring Cloud Config: Started
 - Spring Cloud Registry: Not Started
 - Spring Cloud Circuit Breaker: Not Started

# About the Author

[Ben Wilcock](https://uk.linkedin.com/in/benwilcock) works for Pivotal as a Cloud Solutions Architect. Ben has a passion for microservices, cloud and mobile applications and helps [Pivotal's Cloud Foundry](http://pivotal.io/platform) customers to become more responsive, innovate faster and gain greater returns from their software investments. Ben is also a respected technology [blogger](http://benwilcock.wordpress.com) who's articles have featured in [DZone](https://dzone.com/users/296242/benwilcock.html), [Java Code Geeks](https://www.javacodegeeks.com/author/ben-wilcock/), [InfoQ](https://www.infoq.com/author/Ben-Wilcock) and more.
