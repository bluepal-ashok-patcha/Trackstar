# TrackStar API Gateway

The **API Gateway** is the single entry point for all client requests in the **TrackStar Fleet Management System**.
It handles routing, service discovery, and cross-cutting concerns like CORS.

---

## 📌 Responsibilities

* Route requests to microservices
* Register with Eureka Service Registry
* Enable CORS for frontend applications
* Expose health and monitoring endpoints

---

## 🧱 Tech Stack

| Technology           | Purpose                 |
| -------------------- | ----------------------- |
| Spring Boot          | Application framework   |
| Spring Cloud Gateway | API routing & filtering |
| Eureka Client        | Service discovery       |
| Spring Boot Actuator | Health & monitoring     |
| Docker               | Containerization        |

---

## 📂 Project Structure

```
api-gateway
└── src
    └── main
        ├── java
        │   └── com.fleetmanager.gateway
        │       ├── ApiGatewayApplication.java
        │       │
        │       ├── config
        │       │   ├── CorsConfiguration.java        # Global CORS setup
        │       │   ├── RouteConfiguration.java       # Custom route definitions (future use)
        │       │   └── SecurityConfig.java           # Gateway security rules
        │       │
        │       ├── filter
        │       │   ├── JwtAuthenticationFilter.java  # JWT validation filter
        │       │   ├── RequestLoggingFilter.java     # Logs incoming requests
        │       │   └── TenantContextFilter.java      # Handles tenant headers
        │       │
        │       └── util
        │           └── JwtUtil.java                  # JWT helper utilities
        │
        └── resources
            ├── application.yml        # Default configuration
            ├── application-dev.yml    # Development environment config
            └── application-prod.yml   # Production environment config
    
    └── test
        └── java
            └── com.fleetmanager.gateway
                ├── ApiGatewayApplicationTests.java
                └── filter
                    └── JwtAuthenticationFilterTests.java

```

---

## ⚙️ Configuration (`application.yml`)

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lowerCaseServiceId: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-user:${EUREKA_PASSWORD:secret}@localhost:8761/eureka/
```

---

## 🚀 Application Entry Point

```java
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

* Registers the Gateway with Eureka
* Runs on **port 8080**

---

## 🌍 Routes

The API Gateway routes requests to backend microservices as follows:

| Route           | Target Service |
| --------------- | -------------- |
| `/api/auth/**`  | AUTH-SERVICE   |
| `/api/fleet/**` | FLEET-SERVICE  |

---

## 🌐 Global CORS Configuration

Allows the React frontend (`http://localhost:3000`) to access backend services.

* All headers
* All HTTP methods
* Credentials support

Configured using:

```java
@Bean
public CorsWebFilter corsWebFilter()
```

---

## 🏥 Health Monitoring

Check the gateway status:

```
GET http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

---

## 🧪 Running Locally

### Step 1 — Start Eureka Server

```
http://localhost:8761
```

### Step 2 — Start API Gateway

```bash
mvn spring-boot:run
```

### Step 3 — Verify Registration

Gateway should appear in the Eureka dashboard.

---

## 🐳 Docker

The Docker setup uses a multi-stage build to:

1. Build the JAR using Maven
2. Run the application using a lightweight JRE image

---

## 👨‍💻 Author

**Backend Lead — TrackStar Fleet Management System**

---
