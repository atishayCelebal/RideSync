package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.model.Group;
import org.celebal.repository.GroupRepository;
import org.celebal.service.GroupLocationService;
import org.celebal.service.GroupLocationSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupLocationSchedulerServiceImpl implements GroupLocationSchedulerService {

    private final GroupRepository groupRepository;
    private final GroupLocationService groupLocationService;
    
    private final AtomicBoolean isContinuousUpdatesActive = new AtomicBoolean(false);
    private final AtomicInteger updateIntervalSeconds = new AtomicInteger(60); // Default: 1 minute
    private final AtomicInteger totalGroupsProcessed = new AtomicInteger(0);
    private final AtomicInteger totalLocationUpdates = new AtomicInteger(0);
    
    private OffsetDateTime startTime;
    private OffsetDateTime lastUpdateTime;

    @Override
    public void startContinuousGroupLocationUpdates() {
        if (isContinuousUpdatesActive.compareAndSet(false, true)) {
            startTime = OffsetDateTime.now();
            log.info("Starting continuous group location updates with {} second interval", 
                updateIntervalSeconds.get());
        } else {
            log.warn("Continuous group location updates are already active");
        }
    }

    @Override
    public void stopContinuousGroupLocationUpdates() {
        if (isContinuousUpdatesActive.compareAndSet(true, false)) {
            log.info("Stopping continuous group location updates");
        } else {
            log.warn("Continuous group location updates are already stopped");
        }
    }

    @Override
    public boolean isContinuousUpdatesActive() {
        return isContinuousUpdatesActive.get();
    }

    @Override
    public void setUpdateInterval(int intervalSeconds) {
        this.updateIntervalSeconds.set(Math.max(10, intervalSeconds)); // Minimum 10 seconds
        log.info("Group location update interval set to {} seconds", this.updateIntervalSeconds.get());
    }

    @Override
    public int getUpdateInterval() {
        return updateIntervalSeconds.get();
    }

    @Override
    public void triggerGroupLocationUpdate(String groupId) {
        try {
            log.info("Manually triggering location update for group: {}", groupId);
            
            // Get and broadcast locations for the specific group
            var locations = groupLocationService.getAndBroadcastGroupLocations(groupId);
            
            totalLocationUpdates.incrementAndGet();
            lastUpdateTime = OffsetDateTime.now();
            
            log.info("Manual location update completed for group: {}, got {} locations", 
                groupId, locations.size());
                
        } catch (Exception e) {
            log.error("Failed to manually trigger location update for group {}: {}", 
                groupId, e.getMessage(), e);
        }
    }

    @Override
    public GroupLocationSchedulerService.SchedulerStatistics getSchedulerStatistics() {
        return GroupLocationSchedulerService.SchedulerStatistics.builder()
                .isActive(isContinuousUpdatesActive.get())
                .updateIntervalSeconds(updateIntervalSeconds.get())
                .totalGroupsProcessed(totalGroupsProcessed.get())
                .totalLocationUpdates(totalLocationUpdates.get())
                .lastUpdateTime(lastUpdateTime)
                .startTime(startTime)
                .build();
    }

    /**
     * Scheduled task that runs every configured interval to update group locations
     */
    @Scheduled(fixedRate = 60000) // Check every minute, actual interval controlled by updateIntervalSeconds
    public void scheduledGroupLocationUpdates() {
        if (!isContinuousUpdatesActive.get()) {
            return;
        }
        
        try {
            log.debug("Running scheduled group location updates...");
            
            // Get all active groups
            List<Group> allGroups = groupRepository.findAll();
            
            if (allGroups.isEmpty()) {
                log.debug("No groups found, skipping location updates");
                return;
            }
            
            log.info("Processing {} groups for location updates", allGroups.size());
            
            int groupsProcessed = 0;
            int totalLocations = 0;
            
            // Process each group
            for (Group group : allGroups) {
                try {
                    String groupId = group.getGroupId().toString();
                    
                    // Check if it's time to update this group based on interval
                    if (shouldUpdateGroup(groupId)) {
                        
                        log.debug("Updating locations for group: {}", groupId);
                        
                        // Get and broadcast locations for this group
                        var locations = groupLocationService.getAndBroadcastGroupLocations(groupId);
                        
                        if (!locations.isEmpty()) {
                            totalLocations += locations.size();
                            log.debug("Updated {} locations for group: {}", locations.size(), groupId);
                        }
                        
                        groupsProcessed++;
                        
                        // Add small delay between groups to avoid overwhelming the system
                        Thread.sleep(100);
                    }
                    
                } catch (Exception e) {
                    log.error("Failed to update locations for group {}: {}", 
                        group.getGroupId(), e.getMessage());
                }
            }
            
            // Update statistics
            totalGroupsProcessed.addAndGet(groupsProcessed);
            totalLocationUpdates.addAndGet(totalLocations);
            lastUpdateTime = OffsetDateTime.now();
            
            log.info("Scheduled group location updates completed: {} groups processed, {} total locations", 
                groupsProcessed, totalLocations);
                
        } catch (Exception e) {
            log.error("Error in scheduled group location updates: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if a group should be updated based on the configured interval
     * This is a simple implementation - in production you might want more sophisticated logic
     */
    private boolean shouldUpdateGroup(String groupId) {
        // For now, always update all groups
        // In the future, you could implement:
        // - Per-group update intervals
        // - Update only active ride sessions
        // - Update based on group activity
        return true;
    }
}
