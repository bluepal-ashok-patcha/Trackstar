package com.trackstar.usermanagementservice.tenant;

/**
 * Holds the current tenant ID in a ThreadLocal variable.
 * This allows the tenant ID to be accessible throughout the application
 * during the lifecycle of a request.
 */
public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }
}
