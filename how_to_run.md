# How to run the image service application?

This application is developed with Java 17


## Run the Application

To run the application with local profile ( port 8080 ):

```shell
./mvnw spring-boot:run
```

To run the application with **live** profile 

you need to have a postgres database running on 5432 port. Also, you need to run the dev/log.sql before you start the project. 

```shell
./mvnw spring-boot:run "-Dspring-boot.run.profiles=live"
```
Another option to run the application in production is to run the docker-compose file ( you need to have docker service running ):

```shell
./mvnw spring-boot:build-image
docker-compose -f dev/docker-compose.yml up
```

To create a jar file from application:

```shell
./mvnw package
```

And for executing the jar:

```shell
java -jar target/debijenkorf-assignment-0.0.1-SNAPSHOT.jar
```
To run the test:

```shell
./mvnw test
```