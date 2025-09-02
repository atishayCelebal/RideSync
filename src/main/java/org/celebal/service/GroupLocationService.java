package org.celebal.service;

import org.celebal.dto.SmartcarDtos.VehicleLocation;
import java.util.List;

public interface GroupLocationService {
    
    /**
     * Get locations for ALL members of a group and broadcast to group
     */
    List<VehicleLocation> getAndBroadcastGroupLocations(String groupId);
    
    /**
     * Get locations for ALL members of a group (without broadcasting)
     */
    List<VehicleLocation> getGroupLocations(String groupId);
    
    /**
     * Broadcast aggregated group locations to all group members via WebSocket
     */
    void broadcastGroupLocations(String groupId, List<VehicleLocation> locations);
    
    /**
     * Send group locations to Kafka for processing
     */
    void sendGroupLocationsToKafka(String groupId, List<VehicleLocation> locations);
    
    /**
     * Get group location summary (count of vehicles, last update time, etc.)
     */
    GroupLocationSummary getGroupLocationSummary(String groupId);
    
    // ===== INNER DTO CLASS =====
    
    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class GroupLocationSummary {
        private String groupId;
        private int totalVehicles;
        private int activeVehicles;
        private java.time.OffsetDateTime lastUpdateTime;
        private List<VehicleLocation> recentLocations;
    }
}
