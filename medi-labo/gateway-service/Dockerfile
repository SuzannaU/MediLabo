## Recover pom.xml file from parent project and fetch and cache dependencies
#FROM maven:3.9.11-eclipse-temurin-17-alpine AS dependencies
#WORKDIR /app
#COPY pom.xml .
#COPY gateway-service/pom.xml gateway-service/pom.xml
#COPY front-service/pom.xml front-service/pom.xml
#COPY notes-service/pom.xml notes-service/pom.xml
#COPY patients-service/pom.xml patients-service/pom.xml
#COPY registry-service/pom.xml registry-service/pom.xml
#COPY risks-service/pom.xml risks-service/pom.xml
#RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
#
## Build the artifact using the pre-fetched dependencies
#FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder
#WORKDIR /app
#COPY --from=dependencies /root/.m2 /root/.m2
#COPY --from=dependencies /app/ /app
#COPY gateway-service/src /app/gateway-service/src
#RUN mvn -B -e clean install -DskipTests

# Set up the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/gateway-service.jar"]

# Minimal version of the Dockerfile, without auto-building of the artifact or dependencies optimization
#FROM openjdk:17-jdk-alpine
#COPY ./gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
#ENTRYPOINT ["java","-jar","/app/gateway-service.jar"]