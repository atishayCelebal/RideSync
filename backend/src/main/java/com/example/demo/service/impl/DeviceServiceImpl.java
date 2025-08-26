package com.example.demo.service.impl;

import com.example.demo.dto.requestsDto.DeviceRegistrationDto;
import com.example.demo.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Override
    public String registerDevice(DeviceRegistrationDto deviceRegistrationDto) {
        // Logic to register device
        return "Device registered successfully.";
    }
}