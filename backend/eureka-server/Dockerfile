# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# FIX: Copy the entire project (backend folder) into the container.
# This ensures Maven finds 'common', 'api-gateway', etc., and doesn't crash.
COPY . .

# Build only the eureka-server module
# -pl : project list (eureka-server)
# -am : also make dependents (builds parent pom automatically)
RUN mvn clean package -pl eureka-server -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Copy the built JAR from the target folder
COPY --from=build /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
