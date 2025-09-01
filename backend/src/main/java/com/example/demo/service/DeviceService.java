package com.example.demo.service;

import com.example.demo.entity.Device;
import com.example.demo.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public Device registerDevice(Device device) {
        return deviceRepository.save(device);
    }

    public Device findDeviceById(UUID deviceId) {
        return deviceRepository.findById(deviceId).orElse(null);
    }

    public void deleteDevice(UUID deviceId) {
        deviceRepository.deleteById(deviceId);
    }
}