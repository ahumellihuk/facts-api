# Coding Challenge - Facts API - v1

Author: Dmitri Samoilov

License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

### Prerequisites

 - JDK 11
 - Yandex Translate API key

### Running the code

It's a Maven-based project, so executing `./mvnw spring-boot:run` or (`mvnw spring-boot:run` in Windows) in command line should run Maven wrapper, download and install dependencies, compile the code and start the server using Spring Boot plugin.

The endpoints are protected with Basic/Form authorization, default user and password are defined in `application.properties`

Swagger 2.0 spec is available at http://localhost:8080/v2/api-docs
For Swagger UI documentation visit http://localhost:8080/swagger-ui.html

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/maven-plugin/)

