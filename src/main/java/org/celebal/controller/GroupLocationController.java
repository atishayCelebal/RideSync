package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.api.ApiResponse;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.service.GroupLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/locations")
@RequiredArgsConstructor
@Slf4j
public class GroupLocationController {

    private final GroupLocationService groupLocationService;

    /**
     * Get current locations for ALL members of a group
     * This will fetch locations from Smartcar APIs and return them
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleLocation>>> getGroupLocations(@PathVariable String groupId) {
        try {
            log.info("Getting locations for group: {}", groupId);
            
            // Get locations for all group members
            List<VehicleLocation> groupLocations = groupLocationService.getGroupLocations(groupId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Successfully retrieved group locations", 
                groupLocations
            ));
            
        } catch (Exception e) {
            log.error("Failed to get group locations for group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get group locations: " + e.getMessage()
            ));
        }
    }

    /**
     * Get group location summary (count, status, etc.)
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<GroupLocationService.GroupLocationSummary>> getGroupLocationSummary(
            @PathVariable String groupId) {
        try {
            log.info("Getting location summary for group: {}", groupId);
            
            GroupLocationService.GroupLocationSummary summary = 
                groupLocationService.getGroupLocationSummary(groupId);
            
            if (summary != null) {
                return ResponseEntity.ok(ApiResponse.ok(
                    "Successfully retrieved group location summary", 
                    summary
                ));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "Failed to get group location summary"
                ));
            }
            
        } catch (Exception e) {
            log.error("Failed to get group location summary for group {}: {}", 
                groupId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get group location summary: " + e.getMessage()
            ));
        }
    }

    /**
     * Trigger location update for ALL members of a group
     * This will fetch fresh locations and broadcast to WebSocket + Kafka
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<List<VehicleLocation>>> refreshGroupLocations(
            @PathVariable String groupId) {
        try {
            log.info("Refreshing locations for group: {}", groupId);
            
            // Get fresh locations and broadcast to group
            List<VehicleLocation> groupLocations = 
                groupLocationService.getAndBroadcastGroupLocations(groupId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Successfully refreshed and broadcasted group locations", 
                groupLocations
            ));
            
        } catch (Exception e) {
            log.error("Failed to refresh group locations for group {}: {}", 
                groupId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to refresh group locations: " + e.getMessage()
            ));
        }
    }

    /**
     * Get locations for a specific user's group
     * This endpoint can be used by authenticated users to get their group's locations
     */
    @GetMapping("/my-group")
    public ResponseEntity<ApiResponse<List<VehicleLocation>>> getMyGroupLocations() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponse.fail(
                    "User not authenticated"
                ));
            }
            
            // TODO: Extract user ID from authentication and find their group
            // For now, return error
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "User group lookup not implemented yet"
            ));
            
        } catch (Exception e) {
            log.error("Failed to get user's group locations: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get user's group locations: " + e.getMessage()
            ));
        }
    }
}
