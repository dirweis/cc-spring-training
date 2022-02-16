# cc-spring-training
Quality driven software development training on a Spring Boot example including Quarkus migration.

#### This branch contains the Result of Step 1, 2, 3
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