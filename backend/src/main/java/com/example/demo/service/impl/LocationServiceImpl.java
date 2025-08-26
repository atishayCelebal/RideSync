package com.example.demo.service.impl;

import com.example.demo.dto.requestsDto.LocationDataDto;
import com.example.demo.service.LocationService;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {
    @Override
    public String ingestLocationData(LocationDataDto locationDataDto) {
        // Logic to ingest location data
        return "Location data ingested successfully.";
    }
}