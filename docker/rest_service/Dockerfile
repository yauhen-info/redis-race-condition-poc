FROM ubuntu:latest
FROM maven:3.8.1-jdk-11-slim as build

WORKDIR /home/rest-service
COPY ./rest-service-java/src /home/rest-service/src
COPY ./rest-service-java/pom.xml /home/rest-service/
RUN rm -rf target && mvn clean install
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/redis-poc-2.0.M1.jar","--server", "--config=classpath:config.yml"]
