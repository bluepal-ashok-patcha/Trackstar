package com.fleetmanager.auth.security;

import com.fleetmanager.auth.context.TenantContext;
import com.fleetmanager.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class TenantFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Public endpoints (same as auth-service / gateway)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register-tenant",
            "/actuator/health"
    );

    public TenantFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream()
                .anyMatch(p -> p.contains("*") ? pathMatcher.match(p, path) : path.equals(p));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // If excluded, continue
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7).trim();

        try {
            // parse claims (JwtUtil.extractClaims should throw on invalid tokens)
            Claims claims = jwtUtil.extractClaims(token);

            // Check expiry if present
            Date exp = claims.getExpiration();
            if (exp != null && exp.before(new Date())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }

            // Extract tenant_id claim
            Object tenantIdObj = claims.get("tenant_id");
            if (tenantIdObj == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token missing tenant_id");
                return;
            }

            Long tenantId;
            if (tenantIdObj instanceof Number) {
                tenantId = ((Number) tenantIdObj).longValue();
            } else {
                tenantId = Long.valueOf(String.valueOf(tenantIdObj));
            }

            // Set TenantContext for downstream usage
            TenantContext.setCurrentTenantId(tenantId);

            try {
                filterChain.doFilter(request, response);
            } finally {
                // Always clear to avoid leaks
                TenantContext.clear();
            }

        } catch (ExpiredJwtException eje) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }
}
