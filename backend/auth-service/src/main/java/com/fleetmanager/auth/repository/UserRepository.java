package com.fleetmanager.auth.repository;

import com.fleetmanager.auth.enums.Role;
import com.fleetmanager.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
    boolean existsByEmailAndTenantId(String email, Long tenantId);
    List<User> findByRole(Role role);
}
