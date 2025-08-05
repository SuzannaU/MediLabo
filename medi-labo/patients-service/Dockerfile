# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/patients-service/target/patients-service-0.0.1-SNAPSHOT.jar patients-service.jar
ENTRYPOINT ["java","-jar","/app/patients-service.jar"]