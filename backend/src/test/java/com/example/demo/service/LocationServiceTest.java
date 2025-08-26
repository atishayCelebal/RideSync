package com.example.demo.service;

import com.example.demo.dto.requestsDto.LocationDataDto;
import com.example.demo.repositories.LocationDataRepository;
import com.example.demo.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationServiceTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationDataRepository locationDataRepository;

    public LocationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIngestLocationData() {
        LocationDataDto locationDto = new LocationDataDto();
        locationDto.setDeviceId("device-id");
        locationDto.setLatitude(12.34f);
        locationDto.setLongitude(56.78f);
        locationDto.setTimestamp("2025-08-26T07:07:27Z");

        String response = locationService.ingestLocationData(locationDto);
        assertEquals("Location data ingested successfully.", response);
    }
}