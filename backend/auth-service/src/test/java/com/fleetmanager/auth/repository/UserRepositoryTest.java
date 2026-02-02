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

        // Flyway handles schema
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

        Tenant tenant = Tenant.builder()
                .name(name)
                .subdomain(subdomain)
                .active(true)
                .build();

        return tenantRepository.save(tenant).getId();
    }

    // ==========================================================
    // TASK 1 — Entity Creation & Constraints
    // ==========================================================

    @Test
    @DisplayName("Should automatically set createdAt timestamp when user is saved")
    void shouldSetCreatedAtWhenUserIsSaved() {

        // GIVEN — Tenant exists
        Long tenantId = createTenant("Fleet Corp", "fleet");

        User user = User.builder()
                .email("timestamp@test.com")
                .passwordHash("password")
                .name("Timestamp User")
                .role(Role.ADMIN)
                .tenantId(tenantId)
                .build();

        // WHEN — User is saved
        User savedUser = userRepository.save(user);

        // THEN — createdAt should be populated
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    // ==========================================================

    @Test
    @DisplayName("Should NOT allow duplicate email in same tenant")
    void shouldNotAllowDuplicateEmailInSameTenant() {

        // GIVEN — Tenant exists
        Long tenantId = createTenant("Tenant One", "tenant1");

        User firstUser = User.builder()
                .email("duplicate@test.com")
                .passwordHash("pass1")
                .name("User One")
                .role(Role.MANAGER)
                .tenantId(tenantId)
                .build();

        userRepository.saveAndFlush(firstUser);

        User duplicateUser = User.builder()
                .email("duplicate@test.com") // same email
                .passwordHash("pass2")
                .name("User Two")
                .role(Role.MANAGER)
                .tenantId(tenantId) // same tenant
                .build();

        // WHEN + THEN — Expect DB constraint violation
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(duplicateUser);
        });
    }

    // ==========================================================
    // TASK 2 — Multi-Tenant Repository Behavior
    // ==========================================================

    @Test
    @DisplayName("Should allow same email for different tenants")
    void shouldAllowSameEmailForDifferentTenants() {

        // GIVEN — Two tenants
        Long tenant1 = createTenant("Tenant A", "a");
        Long tenant2 = createTenant("Tenant B", "b");

        User tenant1User = User.builder()
                .email("shared@test.com")
                .passwordHash("pass1")
                .name("Tenant1 User")
                .role(Role.DRIVER)
                .tenantId(tenant1)
                .build();

        User tenant2User = User.builder()
                .email("shared@test.com") // same email
                .passwordHash("pass2")
                .name("Tenant2 User")
                .role(Role.DRIVER)
                .tenantId(tenant2) // different tenant
                .build();

        // WHEN
        userRepository.save(tenant1User);
        userRepository.save(tenant2User);

        // THEN
        assertThat(userRepository.count()).isEqualTo(2);
    }

    // ==========================================================

    @Test
    @DisplayName("Find by email and tenant should return correct user")
    void findByEmailAndTenantIdShouldReturnCorrectUser() {

        // GIVEN — Two tenants
        Long tenant1 = createTenant("Tenant X", "x");
        Long tenant2 = createTenant("Tenant Y", "y");

        User tenant1User = User.builder()
                .email("lookup@test.com")
                .passwordHash("pass1")
                .name("Tenant1 User")
                .role(Role.MANAGER)
                .tenantId(tenant1)
                .build();

        User tenant2User = User.builder()
                .email("lookup@test.com")
                .passwordHash("pass2")
                .name("Tenant2 User")
                .role(Role.MANAGER)
                .tenantId(tenant2)
                .build();

        userRepository.save(tenant1User);
        userRepository.save(tenant2User);

        // WHEN
        Optional<User> result =
                userRepository.findByEmailAndTenantId("lookup@test.com", tenant1);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getTenantId()).isEqualTo(tenant1);
        assertThat(result.get().getName()).isEqualTo("Tenant1 User");
    }
}