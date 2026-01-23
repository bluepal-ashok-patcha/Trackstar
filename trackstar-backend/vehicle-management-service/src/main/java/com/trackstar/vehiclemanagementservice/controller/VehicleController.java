package com.trackstar.vehiclemanagementservice.controller;

import com.trackstar.vehiclemanagementservice.dto.VehicleDto;
import com.trackstar.vehiclemanagementservice.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing vehicles.
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Creates a new vehicle.
     * Accessible only by users with TENANT_ADMIN or MANAGER role.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleDto) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleDto), HttpStatus.CREATED);
    }

    /**
     * Retrieves a vehicle by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * Retrieves all vehicles for the tenant.
     */
    @GetMapping
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
}
