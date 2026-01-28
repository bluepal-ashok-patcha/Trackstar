# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# FIX: Copy everything (common, eureka, auth, fleet, etc.)
# This satisfies Maven's requirement to see all modules listed in parent pom
COPY . .

# Build only api-gateway
RUN mvn clean package -pl api-gateway -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]