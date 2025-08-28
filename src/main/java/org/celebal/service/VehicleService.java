package org.celebal.service;

import org.celebal.dto.VehicleDtos;
import org.celebal.model.User;
import org.celebal.model.Vehicle;

import java.util.Optional;

public interface VehicleService {
    Vehicle upsert(User owner, VehicleDtos.UpsertRequest request);
    Optional<Vehicle> findByOwner(User owner);
}


