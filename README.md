# Image Service Assignment for debijenkorf Company

Implemented by Mahdi Shabani

## Technologies

- Spring boot
- **[Maven](https://maven.apache.org/)**
- Java 17
- Postgresql(log in live)
- Docker
- Swagger/OpenApi
- Mockito
- aws client
- logback

## Getting Started

### Requirements

- Docker
  - Needed in case you want to run it with Docker.
- Java 17
  - Need have the JAVA_HOME set in case you want to execute via Maven

### Architecture explanation

### how to run

Full instruction is available at: [how_to_run.md](how_to_run.md)

### swagger

Apis are documented and available at swagger default url:
http://localhost:8080/swagger-ui/index.html#

also api-doc is available at:
http://localhost:8080/v3/api-docs

## TODO
- add security to the project
- add integration tests and more unit tests
- add more validation
- add connection pooling to database log connection
- add some resiliency patterns when calling the external urls
