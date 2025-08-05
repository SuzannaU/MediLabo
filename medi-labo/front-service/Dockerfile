# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/front-service/target/front-service-0.0.1-SNAPSHOT.jar front-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/front-service.jar"]