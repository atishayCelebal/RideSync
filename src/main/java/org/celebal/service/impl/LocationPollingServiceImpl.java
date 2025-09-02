package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.model.RideSession;
import org.celebal.model.Vehicle;
import org.celebal.repository.RideSessionRepository;
import org.celebal.repository.VehicleRepository;
import org.celebal.service.GroupLocationService;
import org.celebal.service.LocationPollingService;
import org.celebal.service.LocationService;
import org.celebal.service.SmartcarService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.celebal.model.RideSessionStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationPollingServiceImpl implements LocationPollingService {

    private final SmartcarService smartcarService;
    private final LocationService locationService;
    private final GroupLocationService groupLocationService;
    private final VehicleRepository vehicleRepository;
    private final RideSessionRepository rideSessionRepository;
    
    private final AtomicBoolean isPollingActive = new AtomicBoolean(false);
    
    // Configuration for polling intervals
    private static final int POLLING_INTERVAL_SECONDS = 120; // Poll every 120 seconds
    private static final int MAX_VEHICLES_PER_BATCH = 10; // Process max 10 vehicles per batch

    @Override
    public VehicleLocation pollVehicleLocation(String vehicleId, String accessToken) {
        try {
            log.debug("Polling location for vehicle: {}", vehicleId);
            VehicleLocation location = smartcarService.getVehicleLocation(accessToken, vehicleId);
            
            if (location != null) {
                // Send to Kafka pipeline and WebSocket for real-time updates
                locationService.ingestSmartcarLocation(location);
                log.debug("Successfully polled location for vehicle: {} at ({}, {})", 
                    vehicleId, location.getLatitude(), location.getLongitude());
            }
            
            return location;
        } catch (Exception e) {
            log.error("Failed to poll location for vehicle {}: {}", vehicleId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<VehicleLocation> pollUserVehicleLocations(String userId, String accessToken) {
        try {
            log.debug("Polling locations for user: {}", userId);
            
            // Get user's vehicles from Smartcar
            var vehicles = smartcarService.getUserVehicles(accessToken);
            
            return vehicles.stream()
                    .map(vehicle -> pollVehicleLocation(vehicle.getId(), accessToken))
                    .filter(location -> location != null)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Failed to poll locations for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<VehicleLocation> pollGroupVehicleLocations(String groupId) {
        try {
            log.debug("Polling locations for group: {}", groupId);
            
            // Use the new GroupLocationService to get ALL member locations at once
            List<VehicleLocation> groupLocations = groupLocationService.getAndBroadcastGroupLocations(groupId);
            
            log.info("Successfully polled {} vehicle locations for group: {}", 
                groupLocations.size(), groupId);
            
            return groupLocations;
                    
        } catch (Exception e) {
            log.error("Failed to poll locations for group {}: {}", groupId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public void startContinuousPolling() {
        if (isPollingActive.compareAndSet(false, true)) {
            log.info("Starting continuous location polling service");
        } else {
            log.warn("Continuous polling is already active");
        }
    }

    @Override
    public void stopContinuousPolling() {
        if (isPollingActive.compareAndSet(true, false)) {
            log.info("Stopping continuous location polling service");
        } else {
            log.warn("Continuous polling is already stopped");
        }
    }

    @Override
    public boolean isPollingActive() {
        return isPollingActive.get();
    }

    /**
     * Scheduled task that runs every 30 seconds to poll locations for active ride sessions
     */
    @Scheduled(fixedRate = POLLING_INTERVAL_SECONDS * 1000)
    public void scheduledLocationPolling() {
        if (!isPollingActive.get()) {
            return;
        }
        
        try {
            log.debug("Running scheduled location polling...");
            
            // Get all active ride sessions
            List<RideSession> activeSessions = rideSessionRepository.findByStatus(RideSessionStatus.ACTIVE);
            
            if (activeSessions.isEmpty()) {
                log.debug("No active ride sessions found, skipping location polling");
                return;
            }
            
            log.info("Polling locations for {} active ride sessions", activeSessions.size());
            
            // Process sessions in batches to avoid overwhelming the API
            for (int i = 0; i < activeSessions.size(); i += MAX_VEHICLES_PER_BATCH) {
                int endIndex = Math.min(i + MAX_VEHICLES_PER_BATCH, activeSessions.size());
                List<RideSession> batch = activeSessions.subList(i, endIndex);
                
                processLocationBatch(batch);
                
                // Add delay between batches to respect rate limits
                if (endIndex < activeSessions.size()) {
                    Thread.sleep(1000); // 1 second delay between batches
                }
            }
            
        } catch (Exception e) {
            log.error("Error in scheduled location polling: {}", e.getMessage(), e);
        }
    }

    /**
     * Process a batch of ride sessions for location polling
     */
    private void processLocationBatch(List<RideSession> sessions) {
        for (RideSession session : sessions) {
            try {
                // Get group vehicles for this session
                List<VehicleLocation> locations = pollGroupVehicleLocations(session.getGroup().getGroupId().toString());
                
                if (!locations.isEmpty()) {
                    log.debug("Processed {} location updates for session: {}", 
                        locations.size(), session.getSessionId());
                }
                
            } catch (Exception e) {
                log.error("Failed to process location batch for session {}: {}", 
                    session.getSessionId(), e.getMessage());
            }
        }
    }

    /**
     * Get stored access token for a user (placeholder for now)
     * In real implementation, you'd store this securely in the database
     */
    private String getStoredAccessToken(String userId) {
        // TODO: Implement token storage and retrieval
        // For now, return null to indicate no token available
        log.warn("Access token storage not implemented for user: {}", userId);
        return null;
    }

    /**
     * Manual trigger for location polling (can be called via API)
     */
    public void triggerManualPolling(String groupId) {
        if (isPollingActive.get()) {
            log.info("Manual location polling triggered for group: {}", groupId);
            List<VehicleLocation> locations = pollGroupVehicleLocations(groupId);
            log.info("Manual polling completed for group: {}, got {} locations", 
                groupId, locations.size());
        } else {
            log.warn("Cannot trigger manual polling - service is not active");
        }
    }
}
