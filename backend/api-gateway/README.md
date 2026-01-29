
# 🚀 TrackStar API Gateway

The **API Gateway** is the centralized entry point for all client requests in the **TrackStar Fleet Management System**.  
It handles **routing, service discovery, security, logging, monitoring, and cross-cutting concerns** for all backend microservices.

---

## 📌 Responsibilities
```
- Route requests to backend microservices  
- Register with Eureka Service Registry  
- Enable CORS for frontend applications  
- Apply global request logging  
- Handle JWT authentication filtering  
- Manage tenant context headers  
- Expose health and monitoring endpoints  
- Provide centralized timeout control  
```
---

## 🧱 Tech Stack
```
| Technology | Purpose |
----------|---------
Spring Boot | Application framework  
Spring Cloud Gateway | API routing & filtering  
Eureka Client | Service discovery  
Spring Boot Actuator | Health & monitoring  
Docker | Containerization  
```
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
│       │   ├── RequestLoggingGatewayFilterFactory.java # Global gateway logging filter
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

      httpclient:
        connect-timeout: 5000
        response-timeout: 5s

      default-filters:
        - RequestLogging

      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/auth/**

        - id: fleet-service
          uri: lb://FLEET-SERVICE
          predicates:
            - Path=/api/dashboard/**, /api/vehicles/**, /api/trips/**, /api/drivers/**

eureka:
  client:
    service-url:
      defaultZone: http://eureka-user:${EUREKA_PASSWORD:secret}@localhost:8761/eureka/
  instance:
    preferIpAddress: true
    lease-renewal-interval-in-seconds: 30

```
⸻

🚀 Application Entry Point

```@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

⸻

🔀 API Gateway Routes

The API Gateway uses path-based routing with Eureka-backed load balancing.

⸻

✅ Auth Service Routing

Base Path

```
/api/auth/**
```
Examples

```
Incoming Request	Target Service
/api/auth/login	        AUTH-SERVICE
/api/auth/register	AUTH-SERVICE
```

⸻

✅ Fleet Service Routing

Base Paths

```
/api/dashboard/**
/api/vehicles/**
/api/trips/**
/api/drivers/**
```
Examples

```
Incoming Request	Target Service
/api/dashboard/stats	FLEET-SERVICE
/api/vehicles/list	FLEET-SERVICE
/api/trips/history	FLEET-SERVICE
/api/drivers/create	FLEET-SERVICE
```

⸻

⚖ Load Balanced Routing

All services use:

```
lb://SERVICE-NAME
```
Benefits

```
	•	Automatic service discovery
	•	Client-side load balancing
	•	Failover support
	•	Horizontal scalability
```
⸻

📊 Global Request Logging Filter

The gateway uses a custom RequestLoggingGatewayFilterFactory to log every incoming request and outgoing response.

⸻

🎯 Purpose

```
	•	Centralized API logging
	•	Easier debugging
	•	Traffic monitoring
	•	Production observability
```
⸻

🛠 Filter Behavior

Pre-Request Logging

Logs:

```
	•	HTTP Method
	•	Request URI
```
Example:

```
Incoming Request: GET http://localhost:8080/api/vehicles/list
```

⸻

Post-Response Logging

Logs:

```
	•	HTTP Status
	•	Request URI
```
Example:

```
Response Status: 200 OK for http://localhost:8080/api/vehicles/list
```

⸻

🌐 Global Filter Registration

The filter is enabled globally using:

```
default-filters:
  - RequestLogging
```
This applies the filter to all gateway routes automatically.

⸻

⏱ Gateway Timeout Configuration

Configured to prevent slow or stuck requests:

```
httpclient:
  connect-timeout: 5000
  response-timeout: 5s
```

⸻

Timeout Explanation

```
Setting	                Description
connect-timeout	        Maximum connection wait time
response-timeout	Maximum response wait time
```

⸻

🌍 Global CORS Configuration

Frontend access allowed from:

```
http://localhost:3000

```
Features

```
	•	Allows all HTTP methods
	•	Allows all headers
	•	Supports credentials
	•	Centralized gateway-level configuration
```
Configured using:

```
@Bean
public CorsWebFilter corsWebFilter()
```

⸻

🏥 Health Monitoring

Spring Boot Actuator provides health endpoints.

Health Check

```
GET http://localhost:8080/actuator/health

Response

{
  "status": "UP"
}
```

⸻

🧪 Running Locally

⸻

Step 1 — Start Eureka Server

```
http://localhost:8761
```

⸻

Step 2 — Start API Gateway

```
mvn spring-boot:run
```

⸻

Step 3 — Verify Registration

```
	•	Open Eureka Dashboard
	•	Confirm API-GATEWAY is registered
```
⸻

🐳 Docker Support

The API Gateway uses a multi-stage Docker build.

Stage 1 — Build

```
	•	Maven builds executable JAR
```
Stage 2 — Runtime

```
	•	Lightweight JRE image
	•	Faster startup
	•	Production optimized
```
⸻

🔐 Security Features

Integrated Gateway Security:

```
	•	JWT authentication filter
	•	Tenant context propagation
	•	Centralized authorization handling
```
⸻

✅ Summary

TrackStar API Gateway provides:

```
✔ Centralized entry point
✔ Dynamic routing
✔ Eureka load balancing
✔ Global logging
✔ Timeout protection
✔ JWT authentication
✔ Tenant support
✔ CORS handling
✔ Health monitoring
✔ Docker deployment
```
⸻

👨‍💻 Author

Backend Lead — TrackStar Fleet Management System

---
