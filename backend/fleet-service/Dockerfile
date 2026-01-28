# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# 1. Copy Parent POM and Common Module
COPY pom.xml .
COPY common common

# 2. Copy the Service Source
COPY fleet-service fleet-service

# 3. Build only fleet-service
RUN mvn clean package -pl fleet-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/fleet-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]