# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# 1. Copy the Parent POM (Critical Step)
COPY pom.xml .

# 2. Copy the Eureka Server Module
COPY eureka-server/pom.xml eureka-server/
COPY eureka-server/src eureka-server/src

# 3. Build only the eureka-server module
# -pl : project list (build specific module)
# -am : also make dependents (build parent if needed)
RUN mvn clean package -pl eureka-server -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Note the path change: /app/eureka-server/target/...
COPY --from=build /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]