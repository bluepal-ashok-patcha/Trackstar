package com.fleetmanager.fleet.service;

import com.fleetmanager.fleet.context.TenantContext;
import com.fleetmanager.fleet.dto.request.VehicleCreateDTO;
import com.fleetmanager.fleet.dto.response.VehicleResponseDTO;
import com.fleetmanager.fleet.entity.Vehicle;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.exception.DuplicateLicensePlateException;
import com.fleetmanager.fleet.repository.VehicleRepository;
import com.fleetmanager.fleet.dto.request.VehicleFilterDTO;
import com.fleetmanager.fleet.specification.VehicleSpecification;
import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponseDTO createVehicle(VehicleCreateDTO dto) {

        Long tenantId = TenantContext.getCurrentTenantIdOrThrow();

        // 1️⃣ Check uniqueness
        if (vehicleRepository.existsByLicensePlateAndTenantId(
                dto.getLicensePlate(),
                tenantId)) {
            throw new DuplicateLicensePlateException(
                    "License plate already exists for this tenant");
        }

        // 2️⃣ Map DTO → Entity
        Vehicle vehicle = Vehicle.builder()
                .licensePlate(dto.getLicensePlate())
                .make(dto.getMake())
                .model(dto.getModel())
                .year(dto.getYear())
                .vin(dto.getVin())
                .type(dto.getType())
                .imageUrl(dto.getImageUrl())
                .odometerReading(
                        dto.getOdometerReading() != null ? 
                        dto.getOdometerReading() : 0)
                .status(VehicleStatus.AVAILABLE)
                .build();

        // tenantId auto-set via TenantAwareEntity

        // 3️⃣ Save
        Vehicle saved = vehicleRepository.save(vehicle);

        // 4️⃣ Return response DTO
        return mapToResponseDTO(saved);
    }
    
    
    public Page<VehicleResponseDTO> listVehicles(VehicleFilterDTO filters) {

        Sort.Direction direction =
                filters.getSortDir().equalsIgnoreCase("ASC")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, filters.getSortBy());

        Pageable pageable = PageRequest.of(
                filters.getPage(),
                filters.getSize(),
                sort
        );

        Page<Vehicle> vehicles = vehicleRepository.findAll(
                VehicleSpecification.withFilters(filters),
                pageable
        );

        return vehicles.map(this::mapToResponseDTO);
    }

    private VehicleResponseDTO mapToResponseDTO(Vehicle v) {
        return VehicleResponseDTO.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .make(v.getMake())
                .model(v.getModel())
                .year(v.getYear())
                .vin(v.getVin())
                .type(v.getType())
                .status(v.getStatus())
                .imageUrl(v.getImageUrl())
                .odometerReading(v.getOdometerReading())
                .createdAt(v.getCreatedAt())
                .build();
    }
    
    
}
