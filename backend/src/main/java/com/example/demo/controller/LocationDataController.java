package com.example.demo.controller;

import com.example.demo.model.LocationData;
import com.example.demo.service.LocationDataService;
import com.example.demo.dto.LocationDataRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class LocationDataController {

    private final LocationDataService locationDataService;

    public LocationDataController(LocationDataService locationDataService) {
        this.locationDataService = locationDataService;
    }

    @PostMapping
    public ResponseEntity<String> ingestLocationData(@RequestBody LocationDataRequest request) {
        LocationData locationData = new LocationData();
        locationData.setDeviceId(request.getDeviceId());
        locationData.setLatitude(request.getLatitude());
        locationData.setLongitude(request.getLongitude());
        locationData.setTimestamp(request.getTimestamp());
        locationDataService.ingestLocationData(locationData);
        return ResponseEntity.ok("Location data ingested successfully");
    }
}