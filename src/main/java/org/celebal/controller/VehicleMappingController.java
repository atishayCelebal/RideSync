package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.api.ApiResponse;
import org.celebal.dto.VehicleMappingDtos;
import org.celebal.service.VehicleMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-mappings")
@RequiredArgsConstructor
@Slf4j
public class VehicleMappingController {

    private final VehicleMappingService vehicleMappingService;

    /**
     * Create a new vehicle-Smartcar mapping
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleMappingDtos.VehicleMappingResponse>> createVehicleMapping(
            @RequestBody VehicleMappingDtos.CreateVehicleMappingRequest request) {
        try {
            log.info("Creating vehicle mapping for vehicle: {} with Smartcar ID: {}", 
                request.getVehicleId(), request.getSmartcarVehicleId());

            VehicleMappingDtos.VehicleMappingResponse response = vehicleMappingService.createVehicleMapping(request);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping created successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to create vehicle mapping: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to create vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Update an existing vehicle mapping
     */
    @PutMapping("/{mappingId}")
    public ResponseEntity<ApiResponse<VehicleMappingDtos.VehicleMappingResponse>> updateVehicleMapping(
            @PathVariable UUID mappingId,
            @RequestBody VehicleMappingDtos.UpdateVehicleMappingRequest request) {
        try {
            log.info("Updating vehicle mapping: {}", mappingId);

            VehicleMappingDtos.VehicleMappingResponse response = vehicleMappingService.updateVehicleMapping(mappingId, request);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping updated successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to update vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to update vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Get vehicle mapping by ID
     */
    @GetMapping("/{mappingId}")
    public ResponseEntity<ApiResponse<VehicleMappingDtos.VehicleMappingResponse>> getVehicleMapping(
            @PathVariable UUID mappingId) {
        try {
            log.debug("Getting vehicle mapping: {}", mappingId);

            VehicleMappingDtos.VehicleMappingResponse response = vehicleMappingService.getVehicleMapping(mappingId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping retrieved successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to get vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Get vehicle mapping by internal vehicle ID
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleMappingDtos.VehicleMappingResponse>> getVehicleMappingByVehicleId(
            @PathVariable UUID vehicleId) {
        try {
            log.debug("Getting vehicle mapping for vehicle: {}", vehicleId);

            VehicleMappingDtos.VehicleMappingResponse response = vehicleMappingService.getVehicleMappingByVehicleId(vehicleId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping retrieved successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to get vehicle mapping for vehicle {}: {}", vehicleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all vehicle mappings for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VehicleMappingDtos.VehicleMappingResponse>>> getUserVehicleMappings(
            @PathVariable UUID userId) {
        try {
            log.debug("Getting vehicle mappings for user: {}", userId);

            List<VehicleMappingDtos.VehicleMappingResponse> response = vehicleMappingService.getUserVehicleMappings(userId);

            return ResponseEntity.ok(ApiResponse.ok(
                "User vehicle mappings retrieved successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to get vehicle mappings for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get user vehicle mappings: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all active vehicle mappings
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VehicleMappingDtos.VehicleMappingResponse>>> getAllActiveMappings() {
        try {
            log.debug("Getting all active vehicle mappings");

            List<VehicleMappingDtos.VehicleMappingResponse> response = vehicleMappingService.getAllActiveMappings();

            return ResponseEntity.ok(ApiResponse.ok(
                "Active vehicle mappings retrieved successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to get all active vehicle mappings: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get active vehicle mappings: " + e.getMessage()
            ));
        }
    }

    /**
     * Deactivate a vehicle mapping
     */
    @PostMapping("/{mappingId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateVehicleMapping(@PathVariable UUID mappingId) {
        try {
            log.info("Deactivating vehicle mapping: {}", mappingId);

            vehicleMappingService.deactivateVehicleMapping(mappingId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping deactivated successfully",
                "Mapping " + mappingId + " has been deactivated"
            ));

        } catch (Exception e) {
            log.error("Failed to deactivate vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to deactivate vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Activate a vehicle mapping
     */
    @PostMapping("/{mappingId}/activate")
    public ResponseEntity<ApiResponse<String>> activateVehicleMapping(@PathVariable UUID mappingId) {
        try {
            log.info("Activating vehicle mapping: {}", mappingId);

            vehicleMappingService.activateVehicleMapping(mappingId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping activated successfully",
                "Mapping " + mappingId + " has been activated"
            ));

        } catch (Exception e) {
            log.error("Failed to activate vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to activate vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Delete a vehicle mapping
     */
    @DeleteMapping("/{mappingId}")
    public ResponseEntity<ApiResponse<String>> deleteVehicleMapping(@PathVariable UUID mappingId) {
        try {
            log.info("Deleting vehicle mapping: {}", mappingId);

            vehicleMappingService.deleteVehicleMapping(mappingId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping deleted successfully",
                "Mapping " + mappingId + " has been deleted"
            ));

        } catch (Exception e) {
            log.error("Failed to delete vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to delete vehicle mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Refresh vehicle information from Smartcar
     */
    @PostMapping("/{mappingId}/refresh")
    public ResponseEntity<ApiResponse<VehicleMappingDtos.VehicleMappingResponse>> refreshVehicleInfo(
            @PathVariable UUID mappingId) {
        try {
            log.info("Refreshing vehicle info for mapping: {}", mappingId);

            VehicleMappingDtos.VehicleMappingResponse response = vehicleMappingService.refreshVehicleInfo(mappingId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle information refreshed successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to refresh vehicle info for mapping {}: {}", mappingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to refresh vehicle information: " + e.getMessage()
            ));
        }
    }

    /**
     * Bulk create vehicle mappings
     */
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<VehicleMappingDtos.VehicleMappingResponse>>> bulkCreateMappings(
            @RequestBody VehicleMappingDtos.BulkCreateMappingsRequest request) {
        try {
            log.info("Bulk creating {} vehicle mappings for user: {}", 
                request.getMappings().size(), request.getUserId());

            List<VehicleMappingDtos.VehicleMappingResponse> response = vehicleMappingService.bulkCreateMappings(request);

            return ResponseEntity.ok(ApiResponse.ok(
                "Bulk vehicle mappings created successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to bulk create vehicle mappings: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to bulk create vehicle mappings: " + e.getMessage()
            ));
        }
    }

    /**
     * Get mapping statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<VehicleMappingDtos.MappingStatistics>> getMappingStatistics() {
        try {
            log.debug("Getting vehicle mapping statistics");

            VehicleMappingDtos.MappingStatistics response = vehicleMappingService.getMappingStatistics();

            return ResponseEntity.ok(ApiResponse.ok(
                "Vehicle mapping statistics retrieved successfully",
                response
            ));

        } catch (Exception e) {
            log.error("Failed to get vehicle mapping statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get mapping statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Check if a vehicle has an active Smartcar mapping
     */
    @GetMapping("/check/{vehicleId}")
    public ResponseEntity<ApiResponse<Boolean>> checkActiveMapping(@PathVariable UUID vehicleId) {
        try {
            log.debug("Checking if vehicle has active mapping: {}", vehicleId);

            boolean hasActiveMapping = vehicleMappingService.hasActiveMapping(vehicleId);

            return ResponseEntity.ok(ApiResponse.ok(
                "Active mapping check completed",
                hasActiveMapping
            ));

        } catch (Exception e) {
            log.error("Failed to check active mapping for vehicle {}: {}", vehicleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to check active mapping: " + e.getMessage()
            ));
        }
    }

    /**
     * Get Smartcar vehicle ID for internal vehicle
     */
    @GetMapping("/smartcar-id/{vehicleId}")
    public ResponseEntity<ApiResponse<String>> getSmartcarVehicleId(@PathVariable UUID vehicleId) {
        try {
            log.debug("Getting Smartcar vehicle ID for vehicle: {}", vehicleId);

            String smartcarVehicleId = vehicleMappingService.getSmartcarVehicleId(vehicleId);

            if (smartcarVehicleId != null) {
                return ResponseEntity.ok(ApiResponse.ok(
                    "Smartcar vehicle ID retrieved successfully",
                    smartcarVehicleId
                ));
            } else {
                return ResponseEntity.ok(ApiResponse.ok(
                    "No active Smartcar mapping found for vehicle",
                    null
                ));
            }

        } catch (Exception e) {
            log.error("Failed to get Smartcar vehicle ID for vehicle {}: {}", vehicleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get Smartcar vehicle ID: " + e.getMessage()
            ));
        }
    }
}
