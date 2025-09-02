package org.celebal.service;

import org.celebal.dto.SmartcarDtos.VehicleLocation;

import java.util.List;

public interface LocationPollingService {
    
    /**
     * Poll location for a specific vehicle
     */
    VehicleLocation pollVehicleLocation(String vehicleId, String accessToken);
    
    /**
     * Poll locations for all vehicles of a user
     */
    List<VehicleLocation> pollUserVehicleLocations(String userId, String accessToken);
    
    /**
     * Poll locations for all vehicles in a group
     */
    List<VehicleLocation> pollGroupVehicleLocations(String groupId);
    
    /**
     * Start continuous polling for active ride sessions
     */
    void startContinuousPolling();
    
    /**
     * Stop continuous polling
     */
    void stopContinuousPolling();
    
    /**
     * Check if continuous polling is active
     */
    boolean isPollingActive();
}
