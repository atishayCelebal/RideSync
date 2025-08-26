package com.example.demo.controllers;

import com.example.demo.dto.requestsDto.DeviceRegistrationDto;
import com.example.demo.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRegistrationDto deviceRegistrationDto) {
        String response = deviceService.registerDevice(deviceRegistrationDto);
        return ResponseEntity.ok(response);
    }
}