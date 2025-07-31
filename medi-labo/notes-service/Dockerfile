## Recover pom.xml file from parent project and fetch and cache dependencies
#FROM maven:3.9.11-eclipse-temurin-17-alpine AS dependencies
#WORKDIR /app
#COPY pom.xml .
#COPY notes-service/pom.xml notes-service/pom.xml
#COPY front-service/pom.xml front-service/pom.xml
#COPY gateway-service/pom.xml gateway-service/pom.xml
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
#COPY notes-service/src /app/notes-service/src
#RUN mvn -B -e clean install -DskipTests

# Set up the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app
ENV MONGO_HOST=host.docker.internal \
    MONGO_PORT=27017
COPY --from=medilabo-builder /app/notes-service/target/notes-service-0.0.1-SNAPSHOT.jar notes-service.jar
ENTRYPOINT ["java","-jar","/app/notes-service.jar"]

# Minimal version of the Dockerfile, without auto-building of the artifact or dependencies optimization
#FROM openjdk:17-jdk-alpine
#COPY ./notes-service/target/notes-service-0.0.1-SNAPSHOT.jar notes-service.jar
#ENTRYPOINT ["java","-jar","/app/notes-service.jar"]