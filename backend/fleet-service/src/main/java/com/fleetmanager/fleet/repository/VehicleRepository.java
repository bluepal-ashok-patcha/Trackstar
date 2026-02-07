package com.fleetmanager.fleet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.fleetmanager.fleet.entity.Vehicle;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByLicensePlateAndTenantId(String licensePlate, Long tenantId);

    boolean existsByLicensePlateAndTenantId(String licensePlate, Long tenantId);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByType(VehicleType type);

    Page<Vehicle> findByStatusAndType(
            VehicleStatus status,
            VehicleType type,
            Pageable pageable
    );
}
