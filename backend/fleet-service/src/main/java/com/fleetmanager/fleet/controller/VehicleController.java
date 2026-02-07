package com.fleetmanager.fleet.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fleetmanager.fleet.dto.request.VehicleCreateDTO;
import com.fleetmanager.fleet.dto.request.VehicleFilterDTO;
import com.fleetmanager.fleet.dto.response.VehicleResponseDTO;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;
import com.fleetmanager.fleet.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
	
	private final VehicleService vehicleService;


	@GetMapping("/test")
	public String test() {
		return "Vehicle Service is reachable!";
	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Fleet Service is UP");
	}

	@PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VehicleResponseDTO> createVehicle(
            @Valid @RequestBody VehicleCreateDTO dto) {

        VehicleResponseDTO response = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
	
	@GetMapping
	public ResponseEntity<Page<VehicleResponseDTO>> listVehicles(
	        @RequestParam(required = false) VehicleStatus status,
	        @RequestParam(required = false) VehicleType type,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "20") int size
	) {

	    VehicleFilterDTO filters = new VehicleFilterDTO();
	    filters.setStatus(status);
	    filters.setType(type);
	    filters.setSearch(search);
	    filters.setPage(page);
	    filters.setSize(size);

	    Page<VehicleResponseDTO> result = vehicleService.listVehicles(filters);
	    return ResponseEntity.ok(result);
	}

}