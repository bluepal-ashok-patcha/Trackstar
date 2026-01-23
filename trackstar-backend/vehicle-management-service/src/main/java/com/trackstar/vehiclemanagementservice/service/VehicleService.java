package com.trackstar.vehiclemanagementservice.service;

import com.trackstar.vehiclemanagementservice.dto.VehicleDto;

import java.util.List;

/**
 * Service interface for managing vehicles.
 */
public interface VehicleService {

    /**
     * Creates a new vehicle.
     * @param vehicleDto the vehicle data.
     * @return the created vehicle.
     */
    VehicleDto createVehicle(VehicleDto vehicleDto);

    /**
     * Finds a vehicle by its ID.
     * @param id the vehicle ID.
     * @return the vehicle if found.
     */
    VehicleDto getVehicleById(Long id);

    /**
     * Retrieves all vehicles for the current tenant.
     * @return a list of all vehicles.
     */
    List<VehicleDto> getAllVehicles();
}
