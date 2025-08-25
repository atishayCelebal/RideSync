package com.example.demo.service;

import com.example.demo.model.LocationData;
import com.example.demo.repository.LocationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationDataService {

    @Autowired
    private LocationDataRepository locationDataRepository;

    public LocationData ingestLocationData(LocationData locationData) {
        // Logic to save location data to the database
        return locationDataRepository.save(locationData);
    }
}