package com.trackstar.vehiclemanagementservice.dto;

import lombok.Data;

/**
 * Data Transfer Object for Vehicle entity.
 */
@Data
public class VehicleDto {
    private Long id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String vin;
}
