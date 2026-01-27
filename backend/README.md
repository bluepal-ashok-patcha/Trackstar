# TrackStar Backend

Multi-tenant Fleet Management SaaS backend.

## Modules
- `eureka-server`: Service Discovery (Port 8761)
- `api-gateway`: API Gateway (Port 8080)
- `auth-service`: Authentication Service (Port 8081)
- `fleet-service`: Fleet Management Service (Port 8082)
- `common`: Shared DTOs and utilities

## Eureka Server Setup

The Eureka Server is used for service discovery.

- **URL**: `http://localhost:8761`
- **Authentication**: Enabled (Basic Auth)
- **Username**: `eureka-user`
- **Password**: Defined by `${EUREKA_PASSWORD}` (defaults to `secret`)

All client services must be configured to register with this server using the following security credentials in their `application.yml`:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-user:${EUREKA_PASSWORD:secret}@localhost:8761/eureka/
```

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
