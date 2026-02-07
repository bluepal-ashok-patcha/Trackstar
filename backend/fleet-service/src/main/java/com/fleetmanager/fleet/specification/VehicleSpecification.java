package com.fleetmanager.fleet.specification;

import com.fleetmanager.fleet.dto.request.VehicleFilterDTO;
import com.fleetmanager.fleet.entity.Vehicle;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VehicleSpecification {

    public static Specification<Vehicle> withFilters(VehicleFilterDTO filters) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filters.getStatus()));
            }

            if (filters.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filters.getType()));
            }

            if (filters.getSearch() != null && !filters.getSearch().isBlank()) {
                String search = "%" + filters.getSearch().toLowerCase() + "%";

                Predicate licensePlate =
                        cb.like(cb.lower(root.get("licensePlate")), search);

                Predicate make =
                        cb.like(cb.lower(root.get("make")), search);

                predicates.add(cb.or(licensePlate, make));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
