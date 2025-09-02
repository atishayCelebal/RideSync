package org.celebal.service;

public interface GroupLocationSchedulerService {
    
    /**
     * Start continuous group location updates for all active groups
     */
    void startContinuousGroupLocationUpdates();
    
    /**
     * Stop continuous group location updates
     */
    void stopContinuousGroupLocationUpdates();
    
    /**
     * Check if continuous updates are active
     */
    boolean isContinuousUpdatesActive();
    
    /**
     * Set update interval in seconds
     */
    void setUpdateInterval(int intervalSeconds);
    
    /**
     * Get current update interval
     */
    int getUpdateInterval();
    
    /**
     * Manually trigger location update for a specific group
     */
    void triggerGroupLocationUpdate(String groupId);
    
    /**
     * Get statistics about the scheduler
     */
    SchedulerStatistics getSchedulerStatistics();
    
    // ===== INNER DTO CLASS =====
    
    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class SchedulerStatistics {
        private boolean isActive;
        private int updateIntervalSeconds;
        private int totalGroupsProcessed;
        private int totalLocationUpdates;
        private java.time.OffsetDateTime lastUpdateTime;
        private java.time.OffsetDateTime startTime;
    }
}
