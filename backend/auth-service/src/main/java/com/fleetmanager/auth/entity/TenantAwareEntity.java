package com.fleetmanager.auth.entity;

import com.fleetmanager.auth.context.TenantContext;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(
    name = "tenantFilter",
    parameters = @ParamDef(name = "tenantId", type = long.class)
)
@Filter(
    name = "tenantFilter",
    condition = "tenant_id = :tenantId"
)
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    @PrePersist
    protected void prePersist() {
        if (tenantId == null) {
            tenantId = TenantContext.getCurrentTenantIdOrThrow();
        }
    }

    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Security check: prevent tenant override
     */
    public void setTenantId(Long tenantId) {
        Long currentTenant = TenantContext.getCurrentTenantId();
        if (currentTenant != null && !currentTenant.equals(tenantId)) {
            throw new IllegalStateException("Cross-tenant data access attempt blocked");
        }
        this.tenantId = tenantId;
    }
}
