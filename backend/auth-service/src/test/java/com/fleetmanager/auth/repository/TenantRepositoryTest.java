package com.fleetmanager.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fleetmanager.auth.entity.Tenant;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TenantRepositoryTest {

    // -------------------------------------------------
    // PostgreSQL Test Container
    // -------------------------------------------------

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("fleet_test")
            .withUsername("test")
            .withPassword("test");

    // -------------------------------------------------
    // Dynamic DB Configuration
    // -------------------------------------------------

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TenantRepository tenantRepository;

    // -------------------------------------------------
    // POSITIVE: Save Tenant + createdAt Timestamp
    // -------------------------------------------------

    @Test
    void givenTenant_whenSave_thenTenantIsPersistedAndCreatedAtIsSet() {

        // GIVEN
        Tenant tenant = new Tenant();
        tenant.setName("Fleet Corp");
        tenant.setSubdomain("fleetcorp");
        tenant.setActive(true);

        LocalDateTime beforeSave = LocalDateTime.now();

        // WHEN
        Tenant savedTenant = tenantRepository.save(tenant);

        LocalDateTime afterSave = LocalDateTime.now();

        // THEN
        assertThat(savedTenant.getId()).isNotNull();
        assertThat(savedTenant.getCreatedAt()).isNotNull();
        assertThat(savedTenant.getCreatedAt()).isBetween(beforeSave, afterSave);
        assertThat(savedTenant.isActive()).isTrue();
    }

    // -------------------------------------------------
    // POSITIVE: findBySubdomain
    // -------------------------------------------------

    @Test
    void givenTenantSaved_whenFindBySubdomain_thenReturnTenant() {

        // GIVEN
        Tenant tenant = new Tenant();
        tenant.setName("TrackStar");
        tenant.setSubdomain("trackstar");

        tenantRepository.save(tenant);

        // WHEN
        Optional<Tenant> result = tenantRepository.findBySubdomain("trackstar");

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("TrackStar");
        assertThat(result.get().getSubdomain()).isEqualTo("trackstar");
    }

    // -------------------------------------------------
    // NEGATIVE: findBySubdomain Not Found
    // -------------------------------------------------

    @Test
    void givenNoTenant_whenFindBySubdomain_thenReturnEmpty() {

        // WHEN
        Optional<Tenant> result = tenantRepository.findBySubdomain("unknown");

        // THEN
        assertThat(result).isEmpty();
    }

    // -------------------------------------------------
    // POSITIVE: existsBySubdomain
    // -------------------------------------------------

    @Test
    void givenTenantSaved_whenExistsBySubdomain_thenReturnTrue() {

        // GIVEN
        Tenant tenant = new Tenant();
        tenant.setName("Demo Org");
        tenant.setSubdomain("demo");

        tenantRepository.save(tenant);

        // WHEN
        boolean exists = tenantRepository.existsBySubdomain("demo");

        // THEN
        assertThat(exists).isTrue();
    }

    // -------------------------------------------------
    // NEGATIVE: existsBySubdomain
    // -------------------------------------------------

    @Test
    void givenNoTenant_whenExistsBySubdomain_thenReturnFalse() {

        // WHEN
        boolean exists = tenantRepository.existsBySubdomain("invalid");

        // THEN
        assertThat(exists).isFalse();
    }

    // -------------------------------------------------
    // NEGATIVE: Unique Constraint Violation (subdomain)
    // -------------------------------------------------

    @Test
    void givenDuplicateSubdomain_whenSave_thenThrowException() {

        // GIVEN
        Tenant tenant1 = new Tenant();
        tenant1.setName("Company A");
        tenant1.setSubdomain("unique");

        Tenant tenant2 = new Tenant();
        tenant2.setName("Company B");
        tenant2.setSubdomain("unique"); // Duplicate

        tenantRepository.save(tenant1);

        // WHEN + THEN
        assertThrows(DataIntegrityViolationException.class, () -> {
            tenantRepository.saveAndFlush(tenant2);
        });
    }
}