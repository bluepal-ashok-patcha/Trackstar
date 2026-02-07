package com.fleetmanager.fleet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fleetmanager.fleet.context.TenantContext;
import com.fleetmanager.fleet.dto.request.VehicleCreateDTO;
import com.fleetmanager.fleet.dto.response.VehicleResponseDTO;
import com.fleetmanager.fleet.entity.Vehicle;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;
import com.fleetmanager.fleet.exception.DuplicateLicensePlateException;
import com.fleetmanager.fleet.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    // -----------------------------------------
    // Test 1: Valid vehicle → created
    // -----------------------------------------
    @Test
    void createVehicle_validVehicle_shouldCreateVehicle() {
        // Arrange
        TenantContext.setCurrentTenantId(1L);

        VehicleCreateDTO dto = new VehicleCreateDTO(
                "ABC-123",
                "Tesla",
                "Model 3",
                2022,
                null,
                VehicleType.CAR,
                null,
                0
        );

        when(vehicleRepository.existsByLicensePlateAndTenantId("ABC-123", 1L))
                .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VehicleResponseDTO response = vehicleService.createVehicle(dto);

        // Assert
        assertNotNull(response);
        assertEquals("ABC-123", response.getLicensePlate());
        assertEquals("Tesla", response.getMake());
        assertEquals("Model 3", response.getModel());
        assertEquals(VehicleStatus.AVAILABLE, response.getStatus());

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    // -----------------------------------------
    // Test 2: Duplicate license plate → exception
    // -----------------------------------------
    @Test
    void createVehicle_duplicateLicensePlate_shouldThrowException() {
        // Arrange
        TenantContext.setCurrentTenantId(1L);

        VehicleCreateDTO dto = new VehicleCreateDTO(
                "ABC-123",
                "Tesla",
                "Model 3",
                2022,
                null,
                VehicleType.CAR,
                null,
                0
        );

        when(vehicleRepository.existsByLicensePlateAndTenantId("ABC-123", 1L))
                .thenReturn(true);

        // Act + Assert
        assertThrows(DuplicateLicensePlateException.class, () ->
                vehicleService.createVehicle(dto)
        );

        verify(vehicleRepository, never()).save(any());
    }

    // -----------------------------------------
    // Test 3: Tenant ID auto-set (context used)
    // -----------------------------------------
    @Test
    void createVehicle_shouldUseTenantContext() {
        // Arrange
        TenantContext.setCurrentTenantId(99L);

        VehicleCreateDTO dto = new VehicleCreateDTO(
                "XYZ-999",
                "Ford",
                "Transit",
                2023,
                null,
                VehicleType.VAN,
                null,
                100
        );

        when(vehicleRepository.existsByLicensePlateAndTenantId("XYZ-999", 99L))
                .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VehicleResponseDTO response = vehicleService.createVehicle(dto);

        // Assert
        assertNotNull(response);
        assertEquals("XYZ-999", response.getLicensePlate());
    }
}
