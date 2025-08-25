package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class LocationDataController {

    @PostMapping("/data")
    public ResponseEntity<String> ingestLocationData(@RequestBody LocationDataRequest request) {
        // Logic for ingesting location data
        return ResponseEntity.ok("Location data ingested successfully");
    }
}

class LocationDataRequest {
    private String deviceId;
    private double latitude;
    private double longitude;
    private String timestamp;

    // Getters and Setters
}