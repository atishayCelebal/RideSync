package com.example.demo.service;

import com.example.demo.dto.requestsDto.LocationDataDto;

public interface LocationService {
    String ingestLocationData(LocationDataDto locationDataDto);
}