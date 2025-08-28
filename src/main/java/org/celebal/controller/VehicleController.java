package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.VehicleDtos;
import org.celebal.mapper.VehicleMapper;
import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.celebal.service.UserService;
import org.celebal.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final VehicleMapper vehicleMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDtos.VehicleResponse>> upsert(@Valid @RequestBody VehicleDtos.UpsertRequest request,
                                                                           Principal principal) {
        User owner = userService.findByUsername(principal.getName()).orElseThrow();
        Vehicle vehicle = vehicleService.upsert(owner, request);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle saved.", vehicleMapper.toResponse(vehicle)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<VehicleDtos.VehicleResponse>> myVehicle(Principal principal) {
        User owner = userService.findByUsername(principal.getName()).orElseThrow();
        Vehicle vehicle = vehicleService.findByOwner(owner).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return ResponseEntity.ok(ApiResponse.ok("Vehicle fetched.", vehicleMapper.toResponse(vehicle)));
    }
}


