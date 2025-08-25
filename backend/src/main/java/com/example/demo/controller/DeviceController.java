package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        // Logic for device registration
        return ResponseEntity.ok("Device registered successfully");
    }
}

class DeviceRegistrationRequest {
    private String deviceId;

    // Getters and Setters
}