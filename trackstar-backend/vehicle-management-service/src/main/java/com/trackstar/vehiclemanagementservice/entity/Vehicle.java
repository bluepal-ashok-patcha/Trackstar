package com.trackstar.vehiclemanagementservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a vehicle in the system.
 * This will be a JPA entity mapped to the 'vehicles' table.
 */
@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    private Long id;

    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String vin; // Vehicle Identification Number

    // This column is crucial for our multi-tenancy strategy.
    private String tenantId;
}
