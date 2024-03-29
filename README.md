[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=alert_status)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=security_rating)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=coverage)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
# cc-spring-training
Quality-driven software development training on a Spring Boot example. Shows clean coding on a micro service storing data in a relational database as well as documents on a MinIO system.

#### This branch contains the Result of Step 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
##### Step 1: Generated Code Clean-Up
- Removed unnecessary classes
- Removed further unnecessary code
- Removed the special configuration
- Removed unnecessary dependencies
- Refactored the DTOs into Records

##### Step 2: Useful Helpers
- Included Lombok
     - **Warning** Don't use Lombok annotations without knowing what you do! Before using `@Data`, `@Value` better think about it!
- Included the Spring Dev Tools
     - **Warning** Don't forget to set the Spring parameter `spring.mvc.log-resolved-exception = true`

##### Step 3: JavaDoc
- (Re)Wrote the JavaDoc
- Automated the JavaDoc generation in the Maven build
- Enriched the `pom.xml` with developer information

##### Step 4: Sonar
- Using Sonar
- Activated nearly all Sonar rules
- Killed 2 Sonar Issues

##### Step 5: Error Handling
 - Implemented 8 Exception Handlers
     - Implemented a factory with 5 sub classes for JSON violations
 - Added `@Builder` annotation to the `Error` and `InvalidParam` records
 - Added the `@Service` bean `ErrorService` for general error handling
 - Added configuration parameters to `application.yml`
 - Added `@Configuration` bean for enabling case insensitivity for the `PetStatus` enumeration in path query parameters

##### Step 6: Further Error Handling
 - Implemented own annotations for validating with `ConstraintValidator` implementations
     - Implemented the annotation `MultipleOf` as the REST constraint
     - Implemented the annotation `PetIdNull` as a special checker

##### Step 7: JUnit
 - Implemented the JUnit integration tests for the Exception Handlers
 - Implemented the JUnit integration tests for the success cases (very rudimentary)
 - Implemented the first JUnit test with a mocked controller
 - Implemented some more JUnit tests for code coverage

##### Step 8: Simple Database Operations
 - Refactored the Exception handling in case of JSON parsing errors for showing a different approach
 - Implemented the database operation for adding a new pet resource
 - Implemented the annotation `PhotoUrlsNull` as special checker
 - Introduced `Mapstruct` as mapping framework
 - Introduced `Spring Data JPA` as database framework
 - Extended the Unit tests, some of the functional tests are now system tests since they include database transactions

##### Step 9: Spring JPA Derived Queries
 - Implemented the `GET` endpoints
 - Introduced Spring Derived Query for demonstration purpose
 - Introduced `Specification` objects using the `CriteriaBuilder` with extending not only the `JpaRepository` but also `JpaSpecificationExecutor` in the DAO interface as technology for dynamic `WHERE` clauses 
 - Implemented the unit tests as system tests
 - Enriched the `GET` endpoint's query parameters with the parameter `category`
 - Introduced the additional error handling with the class `EntityNotFoundExceptionHandler`

##### Step 10: Database 1:N Relations
 - Implemented the `DELETE` endpoint
 - Implemented the `PUT` endpoint for updating a resource as overwriting
 - Introduced the `TagRepositoryDao` for updates in 1:n relations
 
##### Step 11: S3 Connectivity
 - Implemented the `PUT` endpoint for adding images to Pet resources
 - Introduced the `MinioService` class for operating on the MinIo server
 - Introduced the Read-In of the Micro Service's properties in a static context
 - Introduced the response code `409`
 - Introduced the Exception Handler for the `DataIntegrityViolationException`
 - Introduced the `properties` parameter for the `@SpringBootTest` annotation
 - Introduced a new constraint violation annotation that uses the `Tika` framework for checking on the real content type
 - Introduced Spring's `@ConfigurationProperties` annotation
