package com.example.demo.controllers;

import com.example.demo.dto.requestsDto.LocationDataDto;
import com.example.demo.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping
    public ResponseEntity<String> ingestLocationData(@RequestBody LocationDataDto locationDataDto) {
        String response = locationService.ingestLocationData(locationDataDto);
        return ResponseEntity.ok(response);
    }
}