package com.fleetmanager.auth.entity;

import java.time.LocalDateTime;

import com.fleetmanager.auth.enums.Role;
import com.fleetmanager.auth.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
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
