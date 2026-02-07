package com.fleetmanager.fleet.dto.response;




import java.time.LocalDateTime;

import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private Long id;

    private String licensePlate;

    private String make;

    private String model;

    private Integer year;

    private String vin;

    private VehicleType type;

    private VehicleStatus status;

    private String imageUrl;

    private Integer odometerReading;

    private LocalDateTime createdAt;
}
