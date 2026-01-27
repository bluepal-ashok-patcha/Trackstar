package com.fleetmanager.fleet.repository;

import com.fleetmanager.fleet.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // TODO: Add custom query methods
}
