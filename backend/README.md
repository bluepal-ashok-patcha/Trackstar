# TrackStar Backend

Multi-tenant Fleet Management SaaS backend.

## Modules
- `eureka-server`: Service Discovery (Port 8761)
- `api-gateway`: API Gateway (Port 8080)
- `auth-service`: Authentication Service (Port 8081)
- `fleet-service`: Fleet Management Service (Port 8082)
- `common`: Shared DTOs and utilities

## How to run

### Using Maven
```bash
mvn clean install
# Then run each service individually
```

### Using Docker Compose
```bash
docker-compose up --build
```
