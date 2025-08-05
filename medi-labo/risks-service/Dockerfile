# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/risks-service/target/risks-service-0.0.1-SNAPSHOT.jar risks-service.jar
ENTRYPOINT ["java","-jar","/app/risks-service.jar"]