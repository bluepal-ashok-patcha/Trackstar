package com.fleetmanager.auth.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fleetmanager.auth.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySubdomain(String subdomain);

    boolean existsBySubdomain(String subdomain);
}
