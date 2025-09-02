package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.DeviceDtos;
import org.celebal.model.*;
import org.celebal.repository.DeviceRepository;
import org.celebal.repository.VehicleRepository;
import org.celebal.service.DeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public Device registerForOwner(User owner, DeviceDtos.RegisterRequest request) {
        Vehicle vehicle = vehicleRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Owner has no vehicle"));

        Optional<Device> existing = deviceRepository.findByVehicle(vehicle);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Device already registered for this vehicle");
        }

        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .description(request.getDescription())
                .status(DeviceStatus.ACTIVE)
                .vehicle(vehicle)
                .build();
        return deviceRepository.save(device);
    }

    @Override
    public Optional<Device> findByOwner(User owner) {
        return vehicleRepository.findByOwner(owner)
                .flatMap(deviceRepository::findByVehicle);
    }
}


