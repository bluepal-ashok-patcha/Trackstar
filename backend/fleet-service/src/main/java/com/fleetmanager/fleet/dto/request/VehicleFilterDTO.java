package com.fleetmanager.fleet.dto.request;

import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;
import lombok.Data;

@Data
public class VehicleFilterDTO {

    private VehicleStatus status;
    private VehicleType type;
    private String search;

    private Integer page = 0;
    private Integer size = 20;

    private String sortBy = "createdAt";
    private String sortDir = "DESC";
}
