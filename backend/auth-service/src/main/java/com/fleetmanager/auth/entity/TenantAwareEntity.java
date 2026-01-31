package com.fleetmanager.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

//    @PrePersist
//    public void prePersist() {
//        if (tenantId == null) {
//            tenantId = TenantContext.getCurrentTenantIdOrThrow();
//        }
//    }
}
