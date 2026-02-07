package com.fleetmanager.fleet.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fleetmanager.fleet.context.TenantContext;
import com.fleetmanager.fleet.entity.Vehicle;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;

import org.junit.jupiter.api.AfterEach;
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

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VehicleRepositoryTest {

    // ===========================
    // PostgreSQL Testcontainer
    // ===========================
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("fleet_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        // disable external infra
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private VehicleRepository vehicleRepository;

    // ===========================
    // Cleanup
    // ===========================
    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    // ===========================
    // Helper
    // ===========================
    private Vehicle buildVehicle(String plate) {
        return Vehicle.builder()
                .licensePlate(plate)
                .make("Toyota")
                .model("Camry")
                .year(2024)
                .vin("VIN123")
                .type(VehicleType.CAR)
               // .createdAt(LocalDateTime.now())
                .build();
    }

    // =====================================================
    // TEST 1 — Default Values
    // =====================================================
    @Test
    @DisplayName("Should apply default status and odometer")
    void shouldApplyDefaults() {

        TenantContext.setCurrentTenantId(1L);

        Vehicle saved =
                vehicleRepository.save(buildVehicle("AP01AA1111"));

        assertThat(saved.getStatus())
                .isEqualTo(VehicleStatus.AVAILABLE);

        assertThat(saved.getOdometerReading())
                .isEqualTo(0);
    }

    // =====================================================
    // TEST 2 — Enum Persistence
    // =====================================================
    @Test
    @DisplayName("Should persist enums correctly")
    void shouldPersistEnums() {

        TenantContext.setCurrentTenantId(2L);

        Vehicle v = buildVehicle("TS09BB9999");
        v.setType(VehicleType.SUV);
        v.setStatus(VehicleStatus.IN_USE);

        vehicleRepository.saveAndFlush(v);

        Optional<Vehicle> found =
                vehicleRepository.findByLicensePlateAndTenantId(
                        "TS09BB9999", 2L);

        assertThat(found).isPresent();
        assertThat(found.get().getType())
                .isEqualTo(VehicleType.SUV);
        assertThat(found.get().getStatus())
                .isEqualTo(VehicleStatus.IN_USE);
    }

    // =====================================================
    // TEST 3 — Unique Constraint
    // =====================================================
    @Test
    @DisplayName("Should NOT allow duplicate plate in same tenant")
    void shouldNotAllowDuplicatePlateSameTenant() {

        TenantContext.setCurrentTenantId(3L);

        vehicleRepository.saveAndFlush(
                buildVehicle("KA01AA1234"));

        assertThrows(Exception.class, () ->
                vehicleRepository.saveAndFlush(
                        buildVehicle("KA01AA1234")));
    }

    // =====================================================
    // TEST 4 — Same Plate Different Tenant
    // =====================================================
    @Test
    @DisplayName("Should allow same plate for different tenants")
    void shouldAllowSamePlateDifferentTenants() {

        // Tenant 1
        TenantContext.setCurrentTenantId(10L);
        vehicleRepository.save(
                buildVehicle("MH12AB1234"));

        // Tenant 2
        TenantContext.setCurrentTenantId(20L);
        vehicleRepository.save(
                buildVehicle("MH12AB1234"));

        assertThat(vehicleRepository.count())
                .isEqualTo(2);
    }
}
