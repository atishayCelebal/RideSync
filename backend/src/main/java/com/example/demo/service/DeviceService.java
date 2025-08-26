package com.example.demo.service;

import com.example.demo.dto.requestsDto.DeviceRegistrationDto;

public interface DeviceService {
    String registerDevice(DeviceRegistrationDto deviceRegistrationDto);
}