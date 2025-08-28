package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.DeviceDtos;
import org.celebal.mapper.DeviceMapper;
import org.celebal.model.Device;
import org.celebal.model.User;
import org.celebal.service.DeviceService;
import org.celebal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final DeviceMapper deviceMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeviceDtos.DeviceResponse>> register(@Valid @RequestBody DeviceDtos.RegisterRequest request,
                                                                           Principal principal) {
        User owner = userService.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceService.registerForOwner(owner, request);
        return ResponseEntity.ok(ApiResponse.ok("Device registered successfully.", deviceMapper.toResponse(device)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DeviceDtos.DeviceResponse>> myDevice(Principal principal) {
        User owner = userService.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceService.findByOwner(owner).orElseThrow(() -> new IllegalArgumentException("Device not found"));
        return ResponseEntity.ok(ApiResponse.ok("Device fetched.", deviceMapper.toResponse(device)));
    }
}


