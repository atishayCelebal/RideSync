package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.VehicleDtos;
import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.celebal.repository.VehicleRepository;
import org.celebal.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public Vehicle upsert(User owner, VehicleDtos.UpsertRequest request) {
        Optional<Vehicle> existing = vehicleRepository.findByOwner(owner);
        Vehicle vehicle = existing.orElseGet(Vehicle::new);
        vehicle.setOwner(owner);
        vehicle.setVehicleName(request.getVehicleName());
        vehicle.setRegNo(request.getRegNo());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> findByOwner(User owner) {
        return vehicleRepository.findByOwner(owner);
    }
}


