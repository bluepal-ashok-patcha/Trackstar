package com.fleetmanager.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return parseToken(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        Date exp = extractClaims(token).getExpiration();
        return exp == null || exp.before(new Date());
    }

    public Long extractUserId(String token) {
        Object v = extractClaims(token).get("user_id");
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) return Long.parseLong((String) v);
        return null;
    }

    public Long extractTenantId(String token) {
        Object v = extractClaims(token).get("tenant_id");
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) return Long.parseLong((String) v);
        return null;
    }

    public String extractRole(String token) {
        Object v = extractClaims(token).get("role");
        return v == null ? null : v.toString();
    }
}