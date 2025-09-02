package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.api.ApiResponse;
import org.celebal.service.GroupLocationSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scheduler/group-locations")
@RequiredArgsConstructor
@Slf4j
public class GroupLocationSchedulerController {

    private final GroupLocationSchedulerService schedulerService;

    /**
     * Start continuous group location updates
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startContinuousUpdates() {
        try {
            log.info("Starting continuous group location updates");
            
            schedulerService.startContinuousGroupLocationUpdates();
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Continuous group location updates started successfully",
                "Scheduler is now active"
            ));
            
        } catch (Exception e) {
            log.error("Failed to start continuous group location updates: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to start scheduler: " + e.getMessage()
            ));
        }
    }

    /**
     * Stop continuous group location updates
     */
    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<String>> stopContinuousUpdates() {
        try {
            log.info("Stopping continuous group location updates");
            
            schedulerService.stopContinuousGroupLocationUpdates();
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Continuous group location updates stopped successfully",
                "Scheduler is now inactive"
            ));
            
        } catch (Exception e) {
            log.error("Failed to stop continuous group location updates: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to stop scheduler: " + e.getMessage()
            ));
        }
    }

    /**
     * Get scheduler status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<GroupLocationSchedulerService.SchedulerStatistics>> getSchedulerStatus() {
        try {
            log.debug("Getting scheduler status");
            
            GroupLocationSchedulerService.SchedulerStatistics stats = 
                schedulerService.getSchedulerStatistics();
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Scheduler status retrieved successfully",
                stats
            ));
            
        } catch (Exception e) {
            log.error("Failed to get scheduler status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get scheduler status: " + e.getMessage()
            ));
        }
    }

    /**
     * Set update interval
     */
    @PostMapping("/interval")
    public ResponseEntity<ApiResponse<String>> setUpdateInterval(@RequestParam int intervalSeconds) {
        try {
            log.info("Setting group location update interval to {} seconds", intervalSeconds);
            
            schedulerService.setUpdateInterval(intervalSeconds);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Update interval set successfully",
                "Interval updated to " + intervalSeconds + " seconds"
            ));
            
        } catch (Exception e) {
            log.error("Failed to set update interval: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to set update interval: " + e.getMessage()
            ));
        }
    }

    /**
     * Manually trigger location update for a specific group
     */
    @PostMapping("/trigger/{groupId}")
    public ResponseEntity<ApiResponse<String>> triggerGroupUpdate(@PathVariable String groupId) {
        try {
            log.info("Manually triggering location update for group: {}", groupId);
            
            schedulerService.triggerGroupLocationUpdate(groupId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Group location update triggered successfully",
                "Update completed for group: " + groupId
            ));
            
        } catch (Exception e) {
            log.error("Failed to trigger group location update for group {}: {}", 
                groupId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to trigger update: " + e.getMessage()
            ));
        }
    }

    /**
     * Get current update interval
     */
    @GetMapping("/interval")
    public ResponseEntity<ApiResponse<Integer>> getUpdateInterval() {
        try {
            log.debug("Getting current update interval");
            
            int interval = schedulerService.getUpdateInterval();
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Update interval retrieved successfully",
                interval
            ));
            
        } catch (Exception e) {
            log.error("Failed to get update interval: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to get update interval: " + e.getMessage()
            ));
        }
    }
}
