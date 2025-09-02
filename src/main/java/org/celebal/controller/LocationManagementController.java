package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.service.LocationPollingService;
import org.celebal.service.LocationSimulatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location-management")
@RequiredArgsConstructor
public class LocationManagementController {

    private final LocationPollingService locationPollingService;
    private final LocationSimulatorService locationSimulatorService;

    // ===== LOCATION POLLING ENDPOINTS =====

    /**
     * Start continuous location polling from Smartcar APIs
     */
    @PostMapping("/polling/start")
    public ResponseEntity<ApiResponse<String>> startLocationPolling() {
        try {
            locationPollingService.startContinuousPolling();
            return ResponseEntity.ok(ApiResponse.ok("Location polling started successfully", "Polling active"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to start location polling: " + e.getMessage()));
        }
    }

    /**
     * Stop continuous location polling
     */
    @PostMapping("/polling/stop")
    public ResponseEntity<ApiResponse<String>> stopLocationPolling() {
        try {
            locationPollingService.stopContinuousPolling();
            return ResponseEntity.ok(ApiResponse.ok("Location polling stopped successfully", "Polling stopped"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to stop location polling: " + e.getMessage()));
        }
    }

    /**
     * Check polling status
     */
    @GetMapping("/polling/status")
    public ResponseEntity<ApiResponse<Boolean>> getPollingStatus() {
        try {
            boolean isActive = locationPollingService.isPollingActive();
            return ResponseEntity.ok(ApiResponse.ok("Polling status retrieved", isActive));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get polling status: " + e.getMessage()));
        }
    }

    /**
     * Manually trigger location polling for a specific group
     */
    @PostMapping("/polling/trigger/{groupId}")
    public ResponseEntity<ApiResponse<String>> triggerGroupPolling(@PathVariable String groupId) {
        try {
            List<VehicleLocation> locations = locationPollingService.pollGroupVehicleLocations(groupId);
            return ResponseEntity.ok(ApiResponse.ok(
                "Manual polling triggered successfully", 
                String.format("Retrieved %d locations for group %s", locations.size(), groupId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to trigger group polling: " + e.getMessage()));
        }
    }

    // ===== LOCATION SIMULATION ENDPOINTS =====

    /**
     * Start location simulation
     */
    @PostMapping("/simulation/start")
    public ResponseEntity<ApiResponse<String>> startLocationSimulation() {
        try {
            locationSimulatorService.startSimulation();
            return ResponseEntity.ok(ApiResponse.ok("Location simulation started successfully", "Simulation active"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to start location simulation: " + e.getMessage()));
        }
    }

    /**
     * Stop location simulation
     */
    @PostMapping("/simulation/stop")
    public ResponseEntity<ApiResponse<String>> stopLocationSimulation() {
        try {
            locationSimulatorService.stopSimulation();
            return ResponseEntity.ok(ApiResponse.ok("Location simulation stopped successfully", "Simulation stopped"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to stop location simulation: " + e.getMessage()));
        }
    }

    /**
     * Check simulation status
     */
    @GetMapping("/simulation/status")
    public ResponseEntity<ApiResponse<Boolean>> getSimulationStatus() {
        try {
            boolean isActive = locationSimulatorService.isSimulationActive();
            return ResponseEntity.ok(ApiResponse.ok("Simulation status retrieved", isActive));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get simulation status: " + e.getMessage()));
        }
    }

    /**
     * Set simulation speed
     */
    @PostMapping("/simulation/speed")
    public ResponseEntity<ApiResponse<String>> setSimulationSpeed(@RequestParam int intervalSeconds) {
        try {
            locationSimulatorService.setSimulationSpeed(intervalSeconds);
            return ResponseEntity.ok(ApiResponse.ok(
                "Simulation speed updated successfully", 
                String.format("Speed set to %d seconds", intervalSeconds)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to set simulation speed: " + e.getMessage()));
        }
    }

    /**
     * Generate simulated locations for a specific group
     */
    @PostMapping("/simulation/generate/{groupId}")
    public ResponseEntity<ApiResponse<List<VehicleLocation>>> generateGroupSimulation(@PathVariable String groupId) {
        try {
            List<VehicleLocation> locations = locationSimulatorService.generateGroupSimulatedLocations(groupId);
            return ResponseEntity.ok(ApiResponse.ok(
                "Group simulation generated successfully", 
                locations
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to generate group simulation: " + e.getMessage()));
        }
    }

    /**
     * Generate route simulation between two points
     */
    @PostMapping("/simulation/route/{vehicleId}")
    public ResponseEntity<ApiResponse<List<VehicleLocation>>> generateRouteSimulation(
            @PathVariable String vehicleId,
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double endLat,
            @RequestParam double endLng,
            @RequestParam(defaultValue = "10") int numberOfPoints) {
        try {
            List<VehicleLocation> route = locationSimulatorService.generateRouteSimulation(
                vehicleId, startLat, startLng, endLat, endLng, numberOfPoints
            );
            return ResponseEntity.ok(ApiResponse.ok(
                "Route simulation generated successfully", 
                route
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to generate route simulation: " + e.getMessage()));
        }
    }

    // ===== COMBINED STATUS ENDPOINT =====

    /**
     * Get overall location services status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<LocationServicesStatus>> getOverallStatus() {
        try {
            LocationServicesStatus status = LocationServicesStatus.builder()
                    .pollingActive(locationPollingService.isPollingActive())
                    .simulationActive(locationSimulatorService.isSimulationActive())
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.ok("Location services status retrieved", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Failed to get overall status: " + e.getMessage()));
        }
    }

    // ===== INNER DTO CLASS =====

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class LocationServicesStatus {
        private boolean pollingActive;
        private boolean simulationActive;
        private java.time.OffsetDateTime timestamp;
    }
}
