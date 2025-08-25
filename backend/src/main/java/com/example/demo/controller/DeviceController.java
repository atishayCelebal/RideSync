package com.example.demo.controller;

import com.example.demo.model.Device;
import com.example.demo.service.DeviceService;
import com.example.demo.dto.DeviceRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        Device device = new Device();
        device.setDeviceId(request.getDeviceId());
        deviceService.registerDevice(device);
        return ResponseEntity.ok("Device registered successfully");
    }
}