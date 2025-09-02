package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.SmartcarDtos.*;
import org.celebal.service.SmartcarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smartcar")
@RequiredArgsConstructor
public class SmartcarController {

    private final SmartcarService smartcarService;

    /**
     * Generate OAuth authorization URL for Smartcar
     */
    @PostMapping("/auth/url")
    public ResponseEntity<ApiResponse<AuthUrlResponse>> generateAuthUrl(@RequestBody AuthUrlRequest request) {
        try {
            AuthUrlResponse response = smartcarService.generateAuthUrl(request.getUserId());
            return ResponseEntity.ok(ApiResponse.ok("Authorization URL generated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to generate authorization URL: " + e.getMessage()));
        }
    }

    /**
     * Exchange authorization code for access token
     */
    @PostMapping("/auth/token")
    public ResponseEntity<ApiResponse<TokenResponse>> exchangeCodeForToken(@RequestBody TokenExchangeRequest request) {
        try {
            TokenResponse response = smartcarService.exchangeCodeForToken(request.getCode(), request.getState());
            return ResponseEntity.ok(ApiResponse.ok("Token exchanged successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to exchange code for token: " + e.getMessage()));
        }
    }

    /**
     * Refresh access token
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestParam String refreshToken) {
        try {
            TokenResponse response = smartcarService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to refresh token: " + e.getMessage()));
        }
    }

    /**
     * Get user's vehicles
     */
    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleInfo>>> getUserVehicles(@RequestParam String accessToken) {
        try {
            List<VehicleInfo> vehicles = smartcarService.getUserVehicles(accessToken);
            return ResponseEntity.ok(ApiResponse.ok("Vehicles retrieved successfully", vehicles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get vehicles: " + e.getMessage()));
        }
    }

    /**
     * Get specific vehicle information
     */
    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleInfo>> getVehicleInfo(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            VehicleInfo vehicle = smartcarService.getVehicleInfo(accessToken, vehicleId);
            return ResponseEntity.ok(ApiResponse.ok("Vehicle info retrieved successfully", vehicle));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get vehicle info: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle location
     */
    @GetMapping("/vehicles/{vehicleId}/location")
    public ResponseEntity<ApiResponse<VehicleLocation>> getVehicleLocation(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            VehicleLocation location = smartcarService.getVehicleLocation(accessToken, vehicleId);
            return ResponseEntity.ok(ApiResponse.ok("Vehicle location retrieved successfully", location));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get vehicle location: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle battery level (for electric vehicles)
     */
    @GetMapping("/vehicles/{vehicleId}/battery")
    public ResponseEntity<ApiResponse<Double>> getBatteryLevel(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Double batteryLevel = smartcarService.getBatteryLevel(accessToken, vehicleId);
            if (batteryLevel != null) {
                return ResponseEntity.ok(ApiResponse.ok("Battery level retrieved successfully", batteryLevel));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle does not support battery level", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get battery level: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle fuel level (for gas vehicles)
     */
    @GetMapping("/vehicles/{vehicleId}/fuel")
    public ResponseEntity<ApiResponse<Double>> getFuelLevel(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Double fuelLevel = smartcarService.getFuelLevel(accessToken, vehicleId);
            if (fuelLevel != null) {
                return ResponseEntity.ok(ApiResponse.ok("Fuel level retrieved successfully", fuelLevel));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle does not support fuel level", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get fuel level: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle odometer reading
     */
    @GetMapping("/vehicles/{vehicleId}/odometer")
    public ResponseEntity<ApiResponse<Double>> getOdometer(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Double odometer = smartcarService.getOdometer(accessToken, vehicleId);
            if (odometer != null) {
                return ResponseEntity.ok(ApiResponse.ok("Odometer reading retrieved successfully", odometer));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle does not support odometer", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get odometer: " + e.getMessage()));
        }
    }

    /**
     * Check if vehicle is locked
     */
    @GetMapping("/vehicles/{vehicleId}/lock-status")
    public ResponseEntity<ApiResponse<Boolean>> isVehicleLocked(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Boolean isLocked = smartcarService.isVehicleLocked(accessToken, vehicleId);
            if (isLocked != null) {
                return ResponseEntity.ok(ApiResponse.ok("Lock status retrieved successfully", isLocked));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle does not support lock status", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get lock status: " + e.getMessage()));
        }
    }

    /**
     * Lock vehicle (if supported)
     */
    @PostMapping("/vehicles/{vehicleId}/lock")
    public ResponseEntity<ApiResponse<Boolean>> lockVehicle(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Boolean success = smartcarService.lockVehicle(accessToken, vehicleId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle locked successfully", true));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Failed to lock vehicle", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to lock vehicle: " + e.getMessage()));
        }
    }

    /**
     * Unlock vehicle (if supported)
     */
    @PostMapping("/vehicles/{vehicleId}/unlock")
    public ResponseEntity<ApiResponse<Boolean>> unlockVehicle(
            @PathVariable String vehicleId,
            @RequestParam String accessToken) {
        try {
            Boolean success = smartcarService.unlockVehicle(accessToken, vehicleId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.ok("Vehicle unlocked successfully", true));
            } else {
                return ResponseEntity.ok(ApiResponse.ok("Failed to unlock vehicle", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to unlock vehicle: " + e.getMessage()));
        }
    }
}
