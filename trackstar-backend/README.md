# Trackstar Backend Architecture

## 1. High-Level System Architecture

Trackstar is a multi-tenant fleet management SaaS platform built on a microservices architecture. The system is designed for scalability, resilience, and maintainability, leveraging the Spring Boot and Spring Cloud ecosystems.

### Core Principles:
- **Domain-Driven Design (DDD):** Each microservice is organized around a specific business domain (e.g., User, Vehicle, Trip).
- **Multi-Tenancy:** A shared database, shared schema approach is used, with a `tenant_id` column in all relevant tables to isolate tenant data.
- **Asynchronous Communication:** Apache Kafka is used for event-driven communication, particularly for high-volume telemetry data and for decoupling services.
- **Centralized Entry Point:** A single API Gateway provides a unified interface for all clients, handling routing, security, and cross-cutting concerns.
- **Service Discovery:** Eureka Server allows services to find and communicate with each other dynamically.

### Architecture Diagram Explanation

```
+----------------+      +------------------+      +----------------------+
|   Web/Mobile   |<---->|   API Gateway    |<---->|    Eureka Server     |
|     Clients    |      | (Spring Cloud GW)|      | (Service Discovery)  |
+----------------+      +------------------+      +----------------------+
       ^                      |
       |                      | (Routes to...)
       v                      |
+----------------------+      v
| Spring Security      |<--->+---------------------------------------------------+
| (JWT Auth)           |     |                  Backend Services                 |
+----------------------+     |                                                   |
                              |  +---------------------+  +---------------------+ |
                              |  | User Mgmt Service   |  | Vehicle Mgmt Service| |
                              |  +---------------------+  +---------------------+ |
                              |           |                      |              |
                              |           v                      v              |
                              |  +---------------------+  +---------------------+ |
                              |  | Trip Mgmt Service   |  | Maintenance Service | |
                              |  +---------------------+  +---------------------+ |
                              |           |                      |              |
                              |           v                      v              |
                              |  +---------------------+  +---------------------+ |
                              |  | Financial Service   |  | Notification Service| |
                              |  +---------------------+  +---------------------+ |
                              |                                                   |
                              +---------------------------------------------------+
                                      |         ^        |           ^
                                      | (Feign) |        | (DB)      | (Kafka)
                                      v         |        v           |
+------------------------------------------------------------------------------------+
|                                     Data & Events                                  |
|                                                                                    |
| +------------------------+    +-------------------------+   +--------------------+ |
| |   PostgreSQL DB        |    |      TimescaleDB        |   |   Apache Kafka     | |
| | (Shared Schema)        |<-->|   (Time-Series Data)    |<->| (Event Streaming)  | |
| | - User Data            |    |   - Telemetry           |   | - Trip Events      | |
| | - Vehicle Info         |    |   - Geolocation         |   | - Maintenance Triggers| |
| | - Maintenance Records  |    +-------------------------+   +--------------------+ |
| | - Financial Data       |                                                       |
| +------------------------+                                                       |
+------------------------------------------------------------------------------------+
```

## 2. Service Responsibilities

| Service                  | Responsibilities                                                                                             |
|--------------------------|--------------------------------------------------------------------------------------------------------------|
| **Eureka Server**        | Handles service registration and discovery, allowing services to find each other dynamically.                  |
| **API Gateway**          | Single entry point for all client requests. Handles routing, rate limiting, security (JWT validation), and logging.|
| **User Management**      | Manages tenants, users, roles, and permissions. Handles user authentication and JWT generation.              |
| **Vehicle Management**   | Manages vehicle fleet, including vehicle details, assignments, and status.                                     |
| **Trip Management**      | Tracks and manages all trips. Processes incoming telemetry data from vehicles via Kafka.                       |
| **Maintenance Service**  | Manages vehicle maintenance schedules, records, and work orders. Can be triggered by events from Kafka.        |
| **Financial Service**    | Handles billing, invoicing, and financial reporting for tenants.                                               |
| **Notification Service** | Sends notifications to users (e.g., email, SMS, push notifications) based on system events.                    |

## 3. Multi-Tenancy Implementation Blueprint

1.  **JWT-based Tenant Resolution:**
    *   When a user logs in, the `User Management Service` authenticates them and issues a JWT.
    *   This JWT contains a `tenant_id` claim.
2.  **TenantContext:**
    *   A `TenantContext` class using `ThreadLocal` will hold the `tenant_id` for the duration of a request.
3.  **Interceptor/Filter:**
    *   A Spring `HandlerInterceptor` or `OncePerRequestFilter` will be added to the request processing chain.
    *   This filter will execute before the controller. It will parse the incoming JWT, extract the `tenant_id`, and set it in the `TenantContext`.
4.  **Data Partitioning (JPA/Hibernate):**
    *   Hibernate Filters will be used to automatically append a `WHERE tenant_id = :current_tenant_id` clause to all SQL queries.
    *   The `:current_tenant_id` parameter will be dynamically supplied from the `TenantContext`. This ensures data isolation at the persistence layer.

## 4. Kafka Telemetry Processing Flow

1.  **Vehicle Data Ingestion:** Vehicles with IoT devices publish telemetry data (location, speed, diagnostics) to a specific Kafka topic, e.g., `vehicle_telemetry`.
2.  **Trip Management Service Consumption:**
    *   The `Trip Management Service` has a Kafka listener subscribed to the `vehicle_telemetry` topic.
    *   It processes the stream of data, aggregates it into trips, and persists trip details to the database.
3.  **Event Production for Maintenance:**
    *   As the `Trip Management Service` processes data, it checks for certain conditions (e.g., vehicle mileage exceeds a maintenance threshold).
    *   If a condition is met, it produces a new event to a different Kafka topic, e.g., `maintenance_triggers`.
4.  **Maintenance Service Consumption:**
    *   The `Maintenance Service` listens to the `maintenance_triggers` topic.
    *   Upon receiving an event, it automatically creates a maintenance work order for the respective vehicle.

## 5. Database Schema Summary

-   **Database:** PostgreSQL with TimescaleDB and PostGIS extensions.
-   **Multi-Tenancy:** All tables will have a `tenant_id` column for data isolation.
-   **User Management:** `tenants`, `users`, `roles`, `permissions`.
-   **Vehicle Management:** `vehicles`, `vehicle_assignments`.
-   **Trip Management:** `trips`, `trip_waypoints` (TimescaleDB hypertable for telemetry).
-   **Maintenance Service:** `maintenance_schedules`, `maintenance_records`, `work_orders`.
-   **Financial Service:** `invoices`, `billing_accounts`, `transactions`.

## 6. Service-to-Service Communication

-   **Method:** Synchronous, RESTful communication via Spring Cloud's Feign Client.
-   **Flow:**
    1.  A service needs data from another service (e.g., `Trip Service` needs vehicle details from `Vehicle Service`).
    2.  It calls a method on its Feign Client interface defined for the target service.
    3.  Feign, integrated with Eureka, looks up the network location of the target service.
    4.  The JWT from the incoming request is forwarded with the Feign request to maintain the security context.
    5.  The target service processes the request and returns a response.

## 7. API Gateway & Eureka Flow

1.  **Eureka Registration:**
    *   On startup, each microservice registers itself with the `Eureka Server`, providing its name, IP address, and port.
    *   Services send periodic heartbeats to Eureka to remain registered.
2.  **API Gateway Routing:**
    *   The `API Gateway` is also a Eureka client and fetches the registry of all available services.
    *   It is configured with route rules that map URL paths to service IDs (e.g., `/api/users/**` maps to `user-management-service`).
    *   When a client request arrives, the Gateway finds the appropriate service in its local Eureka cache and forwards the request.

## 8. Security Flow

1.  **Login:** A user sends credentials to the `User Management Service`.
2.  **JWT Generation:** The service validates the credentials and generates a JWT containing the `user_id`, `roles`, and `tenant_id`.
3.  **API Request:** The client includes this JWT in the `Authorization` header of all subsequent requests to the `API Gateway`.
4.  **Gateway Validation:** The `API Gateway` validates the JWT signature and expiration.
5.  **Tenant Resolution:** The request is forwarded to the target microservice. A security filter in the service extracts the `tenant_id` and sets it in the `TenantContext`.
6.  **RBAC (Role-Based Access Control):** Spring Security's `@PreAuthorize` annotations on controller or service methods check the `roles` claim from the JWT, ensuring the user has the necessary permissions to perform the operation.
