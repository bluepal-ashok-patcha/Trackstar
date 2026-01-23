package com.trackstar.vehiclemanagementservice.mapper;

import com.trackstar.vehiclemanagementservice.dto.VehicleDto;
import com.trackstar.vehiclemanagementservice.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity Vehicle and its DTO VehicleDto.
 */
@Mapper
public interface VehicleMapper {

    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    VehicleDto vehicleToVehicleDto(Vehicle vehicle);

    Vehicle vehicleDtoToVehicle(VehicleDto vehicleDto);
}
