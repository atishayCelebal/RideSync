package org.celebal.service;

import org.celebal.dto.DeviceDtos;
import org.celebal.model.Device;
import org.celebal.model.User;

import java.util.Optional;

public interface DeviceService {
    Device registerForOwner(User owner, DeviceDtos.RegisterRequest request);
    Optional<Device> findByOwner(User owner);
}


