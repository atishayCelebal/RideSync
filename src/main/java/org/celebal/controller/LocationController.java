package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.LocationDtos;
import org.celebal.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> ingest(@Valid @RequestBody LocationDtos.IngestRequest request) {
        locationService.ingest(request);
        return ResponseEntity.ok(ApiResponse.ok("Location data ingested successfully.", null));
    }
}


