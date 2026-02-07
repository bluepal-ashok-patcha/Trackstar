package com.fleetmanager.gateway.filter;

import com.fleetmanager.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // ✅ HARD-CODED excluded endpoints (Sprint-1 scope)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register-tenant",
            "/actuator/health"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 1️⃣ Skip public endpoints
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        // 2️⃣ Extract Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            // 3️⃣ Validate JWT
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return unauthorized(exchange, "Invalid or expired token");
            }

            Claims claims = jwtUtil.extractClaims(token);

            Object userId = claims.get("user_id");
            Object tenantId = claims.get("tenant_id");
            Object role = claims.get("role");

            if (userId == null || tenantId == null) {
                return unauthorized(exchange, "Token missing required claims");
            }

            // 4️⃣ Forward claims as headers
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-Tenant-Id", String.valueOf(tenantId))
                            .header("X-User-Role", role == null ? "" : role.toString())
                            .build())
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception ex) {
            return unauthorized(exchange, "Invalid token");
        }
    }

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(p -> p.contains("*")
                        ? pathMatcher.match(p, path)
                        : path.equals(p));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] body = (
                "{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}"
        ).getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(body)));
    }

    @Override
    public int getOrder() {
        return -1; // Run before routing filters
    }
}
