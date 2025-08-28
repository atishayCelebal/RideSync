package org.celebal.repository;

import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByOwner(User owner);
    Optional<Vehicle> findByRegNo(String regNo);
    boolean existsByOwner(User owner);
    boolean existsByRegNo(String regNo);
}


