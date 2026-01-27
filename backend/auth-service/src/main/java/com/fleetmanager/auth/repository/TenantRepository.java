package com.fleetmanager.auth.repository;

import com.fleetmanager.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // TODO: Add custom query methods
}
