# Recover pom.xml file from parent project, child pom files, and fetch and cache dependencies
FROM maven:3.9.11-eclipse-temurin-17-alpine AS dependencies
WORKDIR /app
COPY pom.xml .
COPY front-service/pom.xml front-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY notes-service/pom.xml notes-service/pom.xml
COPY patients-service/pom.xml patients-service/pom.xml
COPY registry-service/pom.xml registry-service/pom.xml
COPY risks-service/pom.xml risks-service/pom.xml
RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

# Build artifacts using the pre-fetched dependencies
FROM maven:3.9.11-eclipse-temurin-17-alpine AS medilabo-builder
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn -B -e clean install -DskipTests