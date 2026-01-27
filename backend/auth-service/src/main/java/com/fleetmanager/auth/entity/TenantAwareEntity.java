package com.fleetmanager.auth.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TenantAwareEntity {
    // TODO: Add tenant_id field and logic
}
