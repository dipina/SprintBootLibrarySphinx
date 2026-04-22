# HOWTO: Spring Boot Library Application + Testing frameworks documentation

## Overview

This document provides a detailed explanation of the different components of the Library Borrowing System built with **Spring Boot**.

## Project Structure

The project follows the **MVC (Model-View-Controller)** pattern. Notices that the skeleton of the project can be generated with [Sprint initializer](https://start.spring.io/).

In summary, the layers that are involved are:
- *Controller*: Handles HTTP requests and sends responses.
- *Service*: Contains business logic and interacts with repositories.
- *Repository*: Manages data access and CRUD operations using Spring Data JPA.
- *Entity*: Represents the data model and maps to a database table.


### 1. Model Layer

- Located in `src/main/java/com/example/library/model/`
- Defines **JPA entities** representing database tables.
- Uses **Hibernate annotations** for ORM mapping.
- Example:
  ```java
  @Entity
  public class Book {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String title;
      private String author;
  }
  ```
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### 2. Repository Layer

- Located in `src/main/java/com/example/library/repository/`
- Interfaces extend **JpaRepository** to provide CRUD operations.
- Example:
  ```java
  public interface BookRepository extends JpaRepository<Book, Long> {}
  ```
- [Spring Data Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories)

### 3. Service Layer

- Located in `src/main/java/com/example/library/service/`
- Contains business logic.
- Example:
  ```java
  @Service
  public class BookService {
      private final BookRepository bookRepository;
      public List<Book> getAllBooks() { return bookRepository.findAll(); }
  }
  ```
- [Spring Service Layer](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)

### 4. Controller Layer

- Located in `src/main/java/com/example/library/controller/`
- Handles HTTP requests and routes them to services.
- Example:
  ```java
  @RestController
  @RequestMapping("/books")
  public class BookController {
      private final BookService bookService;
      @GetMapping public List<Book> getBooks() { return bookService.getAllBooks(); }
  }
  ```
- [Spring MVC Controllers](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc)

### 5. View Layer

- Located in `src/main/resources/static/`
- Contains **HTML, JavaScript, and CSS** for the UI.
- It could use **Thymeleaf** as the templating engine. However, it uses simple HTML with JavaScript code. 
- Example:
  ```html
  <html>
  <body>
      <h1>Library System</h1>
      <script src="app.js"></script>
  </body>
  </html>
  ```
- [Thymeleaf Documentation](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)

### 6. API Documentation with Swagger

- Uses **Springdoc OpenAPI** for generating API documentation.
- URL: `http://localhost:8080/swagger-ui.html`
- Example Configuration:
  ```java
  @OpenAPIDefinition(info = @Info(title = "Library API", version = "1.0"))
  @Configuration
  public class OpenAPIConfig {}
  ```
- [Springdoc OpenAPI](https://springdoc.org/)

### 7. DTO - Data Transfer Objects

When building a REST API in Java using Spring Boot, it’s common to use Data Transfer Objects (DTOs) to encapsulate data that will be sent or received by the API. 
DTOs are simple objects that only contain fields and getter/setter methods, without any business logic, and are used to transfer data between layers of the application.

- Example DTO:
  ```java
  package com.example.demo.dto;

  public class ProductDTO {

      private Long id;
      private String name;
      private String description;
      private double price;

      // Constructors, getters, and setters

      public ProductDTO() {}

      public ProductDTO(Long id, String name, String description, double price) {
          this.id = id;
          this.name = name;
          this.description = description;
          this.price = price;
      }

      // Getters and Setters
  }
  ```


## Running the Application

1. Start MySQL and create the database:
   ```sh
   mysql -u root -p < src/main/resources/dbsetup.sql
   ```
2. Update `application.properties` with database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/libraryapidb
   spring.datasource.username=root
   spring.datasource.password=password
   ```
3. Start the application:
   ```sh
   mvn clean compile
   mvn spring-boot:run
   ```

## Running Tests

- Run all tests:
  ```sh
  mvn test
  ```
- Run unit tests only:
  ```sh
  mvn -Dtest=UserServiceTest,BookServiceTest test
  mvn -Dtest=*ServiceTest test
  ```
- Run integration tests:
  ```sh
  mvn verify
  mvn -Dtest=LibraryIntegrationTest test
  ```

- Run performance tests:
  ```sh
  mvn -Dtest=PerformanceTest test
  ```
- Run tests providing more details:
  ```sh
  mvn verify
  mvn test -Dspring-boot.run.profiles=test
  ```
- Generate a test report (JaCoCo, PMD, Checkstyle):
  ```sh
  mvn site
  ```

## Additional Resources

- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Spring Boot Guides](https://spring.io/guides)
- [Very good explaination of the project at Medium, only including Book service](https://medium.com/@pratik.941/building-rest-api-using-spring-boot-a-comprehensive-guide-3e9b6d7a8951) 
- [Building REST services with Sprint](https://spring.io/guides/tutorials/rest)
- [Docker example with Spring](https://medium.com/@yunuseulucay/end-to-end-spring-boot-with-mysql-and-docker-2c42a6e036c0)
- [Documentation about JaCoCo and Spring Boot](https://medium.com/@truongbui95/jacoco-code-coverage-with-spring-boot-835af8debc68)
- [Documentation JUnitPerf](https://noconnor.github.io/JUnitPerf/docs/junit5.html)
- [JaCoCo official documentation](https://www.jacoco.org/jacoco/trunk/index.html)

