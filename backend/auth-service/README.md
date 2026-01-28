TrackStar Auth Service

Run
mvn spring-boot:run

Service Registration

The Authentication Service registers with the Eureka Server so that other services can discover it dynamically.

Configuration

Service registration is configured in application.yml using the following settings:

spring.application.name = auth-service
eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka/

Verification

Start Eureka Server
Start Authentication Service
Open http://localhost:8761   

Verify the service appears as AUTH-SERVICE with status UP

