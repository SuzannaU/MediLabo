# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
ENTRYPOINT ["java","-jar","/app/gateway-service.jar"]