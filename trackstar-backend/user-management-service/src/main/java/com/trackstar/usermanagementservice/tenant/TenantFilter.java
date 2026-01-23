package com.trackstar.usermanagementservice.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A servlet filter that intercepts incoming requests to extract the tenant ID.
 * This is a placeholder and needs to be integrated with Spring Security to
 * parse the JWT and extract the 'tenant_id' claim.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // In a real implementation, you would parse the JWT from the Authorization header.
        // For this example, we'll use a hardcoded header "X-Tenant-ID".
        String tenantId = request.getHeader("X-Tenant-ID");

        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Ensure the tenant context is cleared after the request is processed.
            TenantContext.clear();
        }
    }
}
