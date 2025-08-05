# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
ENV MONGO_HOST=host.docker.internal \
    MONGO_PORT=27017
COPY --from=medilabo-builder /app/notes-service/target/notes-service-0.0.1-SNAPSHOT.jar notes-service.jar
ENTRYPOINT ["java","-jar","/app/notes-service.jar"]