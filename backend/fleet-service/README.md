
✅ Fleet Service – TrackStar Fleet Management System

📌 Overview

```
Fleet Service is a core microservice in the TrackStar Fleet Management System responsible for managing:
	•	Vehicles
	•	Drivers
	•	Trips
	•	Fleet Dashboard Analytics
	•	Reports & CSV Exports
	•	Multi-tenant fleet operations

It is built using Spring Boot, Spring Data JPA, PostgreSQL, Flyway, and integrates with Eureka Service Discovery and API Gateway.
```
⸻

🏗 Architecture Overview

```
Client
   |
API Gateway (8080)
   |
---------------------------
|        Eureka           |
|     Service Registry    |
---------------------------
   |
----------------------------------
| Fleet Service (8083)           |
| Auth Service                   |
----------------------------------
```

⸻

🚀 Tech Stack

```
Component	Technology
Backend Framework	Spring Boot
Database	PostgreSQL
ORM	Spring Data JPA + Hibernate
Service Discovery	Eureka Client
API Gateway	Spring Cloud Gateway
Migration Tool	Flyway
Security Communication	Feign Client
Documentation	SpringDoc OpenAPI
Build Tool	Maven
Containerization	Docker

```
⸻

📁 Project Structure

```
fleet-service
 ├── controller     → REST APIs
 ├── service        → Business Logic
 ├── repository     → Database Access Layer
 ├── entity         → JPA Entities
 ├── dto            → Request / Response DTOs
 ├── specification  → Dynamic Filters
 ├── client         → Auth Service Feign Client
 ├── config         → Application Configurations
 ├── exception      → Global Exception Handling
 ├── aspect         → Multi-Tenant Filters
 ├── context        → Tenant Context
 ├── enums          → Domain Enums
 ├── util           → Utility Classes
 ├── resources
 │    ├── application.yml
 │    ├── db/migration → Flyway Scripts
 └── Dockerfile

```
⸻

⚙ Service Configuration

✅ Application Name

```
spring:
  application:
    name: fleet-service
```

⸻

✅ Server Port

```
server:
  port: 8083

Fleet Service runs on:

http://localhost:8083
```

⸻

✅ Eureka Client Configuration

Fleet Service registers automatically with Eureka:

```
eureka:
  client:
    service-url:
      defaultZone: http://eureka-user:secret@localhost:8761/eureka/

```
⸻

✅ Database Configuration

```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/trackstar_db
    username: postgres
    password: ashok
```

⸻

✅ Flyway Migration Enabled

```
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration

```
Migration Scripts:

```
V1__create_vehicles_table.sql
V2__create_drivers_table.sql
V3__create_trips_table.sql

```
⸻

🔍 Health Check Endpoint

Fleet Service exposes a basic health endpoint.

✅ Endpoint

```
GET /health

```
✅ Response

```
{
  "status": "UP"
}

```
Used to verify service availability and Eureka health checks.

⸻

🌐 API Gateway Routing

Fleet Service routes are configured in API Gateway:

```
- id: fleet-service
  uri: lb://fleet-service
  predicates:
    - Path=/api/dashboard/**, /api/vehicles/**, /api/trips/**, /api/drivers/**

```
⸻

✅ Access Through Gateway

API	Gateway URL

```
Vehicles	http://localhost:8080/api/vehicles
Drivers	http://localhost:8080/api/drivers
Trips	http://localhost:8080/api/trips
Dashboard	http://localhost:8080/api/dashboard

```
⸻

🔐 Service Discovery Verification

After starting all services:

Start Order

```
1️⃣ Eureka Server
2️⃣ Auth Service
3️⃣ Fleet Service
4️⃣ API Gateway
```
⸻

Verify On Eureka Dashboard

Open:

```
http://localhost:8761
```
You should see:

```
✅ AUTH-SERVICE
✅ FLEET-SERVICE
✅ API-GATEWAY

```
⸻

🧪 Testing Coverage

Fleet Service includes:

✅ Unit Tests

```
	•	Service Layer Tests
	•	Utility Tests
	•	Repository Tests

```
✅ Integration Tests

```
	•	Controller Integration
	•	Multi-Tenancy Tests
	•	Database Integration
```
Test location:

```
src/test/java/com/fleetmanager/fleet
```

⸻

📦 Running Fleet Service Locally

Step 1 — Start PostgreSQL

Make sure PostgreSQL is running:

```
trackstar_db
```

⸻

Step 2 — Start Eureka Server

```
http://localhost:8761
```

⸻

Step 3 — Start Auth Service

Verify:

```
AUTH-SERVICE registered in Eureka

```
⸻

Step 4 — Start Fleet Service

Using Maven:

```
mvn spring-boot:run

```
Or from IDE:

```
Run FleetServiceApplication.java
```

⸻

Step 5 — Verify Registration

Open Eureka dashboard:

```
http://localhost:8761

```
Confirm:

```
FLEET-SERVICE → UP

```
⸻

🐳 Docker Support

Fleet Service includes Docker support.

Build Image

```
docker build -t fleet-service .
```

⸻

Run Container

```
docker run -p 8083:8083 fleet-service

```
⸻

📊 Core Functional Modules

Fleet Service provides:

🚗 Vehicle Management

```
	•	Add / Update / Delete Vehicles
	•	Vehicle Status Tracking
	•	Utilization Analytics
```
⸻

👨‍✈️ Driver Management

```
	•	Driver Registration
	•	Status Tracking
	•	Assignment Handling
```
⸻

🛣 Trip Management

```
	•	Start Trip
	•	End Trip
	•	Distance Calculation
	•	Trip History
```
⸻

📈 Dashboard Analytics

```
	•	Fleet Summary
	•	Trip Trends
	•	Top Drivers
	•	Vehicle Utilization
```
⸻

📄 Reports

```
	•	CSV Export
	•	Analytics Reports
```
⸻

🔒 Multi-Tenancy Support

Fleet Service supports:

```
	•	Tenant Header Based Isolation
	•	Tenant Context Handling
	•	Feign Tenant Propagation

```
Implemented Using:

```
	•	TenantContext
	•	TenantFilterAspect
	•	Feign Interceptor
```
⸻

📘 API Documentation (Swagger)

Swagger is enabled via OpenAPI Config.

Access via Gateway:

```
http://localhost:8080/swagger-ui.html

```
⸻

✅ Status

Fleet Service is fully integrated with:

```
✔ Eureka Service Discovery
✔ API Gateway Routing
✔ PostgreSQL Database
✔ Flyway Migration
✔ Multi-Tenant Support
✔ Docker Support
```
⸻

👨‍💻 Developed As Part Of

TrackStar Fleet Management Microservice Platform

⸻
