[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=alert_status)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=dirweis_cc-spring-training&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=dirweis_cc-spring-training)
# cc-spring-training
Quality driven software development training on a Spring Boot example.

#### This branch contains the Result of Step 1, 2, 3, 4, 5, 6, 7
##### Step 1
- Removed unnecessary classes
- Removed further unnecessary code
- Removed the special configuration
- Removed unnecessary dependencies
- Refactored the DTOs into Records

##### Step 2
- Included Lombok
     - **Warning** Don't use Lombok annotations without knowing what you do! Before using `@Data`, `@Value` better think about it!
- Included the Spring Dev Tools
     - **Warning** Don't forget to set the Spring parameter `spring.mvc.log-resolved-exception = true`

##### Step 3
- (Re)Wrote the JavaDoc
- Automated the JavaDoc generation in the Maven build
- Enriched the `pom.xml` with developer information

##### Step 4
- Using Sonar
- Activated nearly all Sonar rules
- Killed 2 Sonar Issues

##### Step 5
 - Implemented 8 Exception Handlers
     - Implemented a factory with 5 sub classes for JSON violations
 - Added `@Builder` annotation to the `Error` and `InvalidParam` records
 - Added the `@Service` bean `ErrorService` for general error handling
 - Added configuration parameters to `application.yml`
 - Added `@Configuration` bean for enabling case insensitivity for the `PetStatus` enumeration in path query parameters

##### Step 6
 - Implemented own annotations for validating with `ConstraintValidator` implementations
     - Implemented the annotation `MultipleOf` as the REST constraint
     - Implemented the annotation `PetIdNull` as a special checker

##### Step 7
 - Implemented the JUnit integration tests for the Exception Handlers
 - Implemented the JUnit integration tests for the success cases (very rudimentary)
 - Implemented the first JUnit test with a mocked controller
 - Implemented some more JUnit tests for code coverage
