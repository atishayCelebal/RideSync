package org.celebal.repository;

import org.celebal.model.Device;
import org.celebal.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByVehicle(Vehicle vehicle);
    boolean existsByVehicle(Vehicle vehicle);
}


