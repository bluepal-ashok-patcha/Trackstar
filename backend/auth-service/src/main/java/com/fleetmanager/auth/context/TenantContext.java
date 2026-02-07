package com.fleetmanager.auth.context;

import com.fleetmanager.auth.exception.TenantContextMissingException;

public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCurrentTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getCurrentTenantId() {
        return currentTenant.get();
    }

    public static Long getCurrentTenantIdOrThrow() {
        Long tenantId = getCurrentTenantId();
        if (tenantId == null) {
            throw new TenantContextMissingException("Tenant id missing in request context");
        }
        return tenantId;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
