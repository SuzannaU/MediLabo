# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/notes-service/target/notes-service-0.0.1-SNAPSHOT.jar notes-service.jar
ENTRYPOINT ["java","-jar","/app/notes-service.jar"]