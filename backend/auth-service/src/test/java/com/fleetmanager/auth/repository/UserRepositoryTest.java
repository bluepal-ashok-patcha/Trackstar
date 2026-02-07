package com.fleetmanager.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fleetmanager.auth.context.TenantContext;
import com.fleetmanager.auth.entity.Tenant;
import com.fleetmanager.auth.entity.User;
import com.fleetmanager.auth.enums.Role;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    // ---------------------------
    // Testcontainer Configuration
    // ---------------------------

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("fleet_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");

        registry.add("spring.jpa.show-sql", () -> "true");
    }

    // ---------------------------
    // Repositories
    // ---------------------------

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    // ---------------------------
    // Helper Method
    // ---------------------------

    private Long createTenant(String name, String subdomain) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setSubdomain(subdomain);
        tenant.setActive(true);
        return tenantRepository.saveAndFlush(tenant).getId();
    }

    // ==========================================================
    // TASK 1 — Entity Creation & Constraints
    // ==========================================================

    @Test
    @DisplayName("Should automatically set createdAt timestamp when user is saved")
    void shouldSetCreatedAtWhenUserIsSaved() {

        Long tenantId = createTenant("Fleet Corp", "fleet");

        TenantContext.setCurrentTenantId(tenantId);
        try {
            User user = new User();
            user.setEmail("timestamp@test.com");
            user.setPasswordHash("password");
            user.setName("Timestamp User");
            user.setRole(Role.ADMIN);

            User savedUser = userRepository.save(user);

            assertThat(savedUser.getCreatedAt()).isNotNull();
            assertThat(savedUser.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        } finally {
            TenantContext.clear();
        }
    }

    // ==========================================================

    @Test
    @DisplayName("Should NOT allow duplicate email in same tenant")
    void shouldNotAllowDuplicateEmailInSameTenant() {

        Long tenantId = createTenant("Tenant One", "tenant1");

        TenantContext.setCurrentTenantId(tenantId);
        try {
            User firstUser = new User();
            firstUser.setEmail("duplicate@test.com");
            firstUser.setPasswordHash("pass1");
            firstUser.setName("User One");
            firstUser.setRole(Role.MANAGER);

            userRepository.saveAndFlush(firstUser);

            User duplicateUser = new User();
            duplicateUser.setEmail("duplicate@test.com");
            duplicateUser.setPasswordHash("pass2");
            duplicateUser.setName("User Two");
            duplicateUser.setRole(Role.MANAGER);

            assertThrows(Exception.class, () ->
                    userRepository.saveAndFlush(duplicateUser)
            );
        } finally {
            TenantContext.clear();
        }
    }

    // ==========================================================
    // TASK 2 — Multi-Tenant Repository Behavior
    // ==========================================================

    @Test
    @DisplayName("Should allow same email for different tenants")
    void shouldAllowSameEmailForDifferentTenants() {

        Long tenant1 = createTenant("Tenant A", "a");
        Long tenant2 = createTenant("Tenant B", "b");

        TenantContext.setCurrentTenantId(tenant1);
        User tenant1User = new User();
        tenant1User.setEmail("shared@test.com");
        tenant1User.setPasswordHash("pass1");
        tenant1User.setName("Tenant1 User");
        tenant1User.setRole(Role.DRIVER);
        userRepository.save(tenant1User);
        TenantContext.clear();

        TenantContext.setCurrentTenantId(tenant2);
        User tenant2User = new User();
        tenant2User.setEmail("shared@test.com");
        tenant2User.setPasswordHash("pass2");
        tenant2User.setName("Tenant2 User");
        tenant2User.setRole(Role.DRIVER);
        userRepository.save(tenant2User);
        TenantContext.clear();

        assertThat(userRepository.count()).isEqualTo(2);
    }

    // ==========================================================

    @Test
    @DisplayName("Find by email and tenant should return correct user")
    void findByEmailAndTenantIdShouldReturnCorrectUser() {

        Long tenant1 = createTenant("Tenant X", "x");
        Long tenant2 = createTenant("Tenant Y", "y");

        TenantContext.setCurrentTenantId(tenant1);
        User tenant1User = new User();
        tenant1User.setEmail("lookup@test.com");
        tenant1User.setPasswordHash("pass1");
        tenant1User.setName("Tenant1 User");
        tenant1User.setRole(Role.MANAGER);
        userRepository.save(tenant1User);
        TenantContext.clear();

        TenantContext.setCurrentTenantId(tenant2);
        User tenant2User = new User();
        tenant2User.setEmail("lookup@test.com");
        tenant2User.setPasswordHash("pass2");
        tenant2User.setName("Tenant2 User");
        tenant2User.setRole(Role.MANAGER);
        userRepository.save(tenant2User);
        TenantContext.clear();

        TenantContext.setCurrentTenantId(tenant1);
        try {
            Optional<User> result =
                    userRepository.findByEmailAndTenantId("lookup@test.com", tenant1);

            assertThat(result).isPresent();
            assertThat(result.get().getTenantId()).isEqualTo(tenant1);
            assertThat(result.get().getName()).isEqualTo("Tenant1 User");
        } finally {
            TenantContext.clear();
        }
    }
}
