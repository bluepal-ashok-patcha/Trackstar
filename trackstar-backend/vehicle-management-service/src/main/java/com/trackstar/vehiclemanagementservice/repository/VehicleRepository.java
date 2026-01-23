package com.trackstar.vehiclemanagementservice.repository;

import com.trackstar.vehiclemanagementservice.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Vehicle entity.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Finds a vehicle by its Vehicle Identification Number (VIN).
     * The multi-tenancy filter will be applied automatically.
     * @param vin the VIN to search for.
     * @return an Optional containing the vehicle if found.
     */
    Optional<Vehicle> findByVin(String vin);
}
