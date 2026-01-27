package com.fleetmanager.fleet.repository;

import com.fleetmanager.fleet.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // TODO: Add custom query methods
}
