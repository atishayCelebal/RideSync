package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.model.Group;
import org.celebal.model.GroupMembership;
import org.celebal.model.MembershipStatus;
import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.celebal.model.VehicleSmartcarMapping;
import org.celebal.repository.GroupMembershipRepository;
import org.celebal.repository.GroupRepository;
import org.celebal.repository.VehicleRepository;
import org.celebal.repository.VehicleSmartcarMappingRepository;
import org.celebal.service.GroupLocationService;
import org.celebal.service.SmartcarService;
import org.celebal.service.SmartcarOAuthService;
import org.celebal.service.VehicleMappingService;
import org.celebal.service.WebSocketService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupLocationServiceImpl implements GroupLocationService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleSmartcarMappingRepository vehicleSmartcarMappingRepository;
    private final SmartcarService smartcarService;
    private final SmartcarOAuthService smartcarOAuthService;
    private final VehicleMappingService vehicleMappingService;
    private final WebSocketService webSocketService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String KAFKA_TOPIC = "ridesync.group.locations";
    private static final int LOCATION_FETCH_TIMEOUT_SECONDS = 30;

    @Override
    public List<VehicleLocation> getAndBroadcastGroupLocations(String groupId) {
        try {
            log.info("Getting and broadcasting locations for group: {}", groupId);
            
            // Get all locations for the group
            List<VehicleLocation> groupLocations = getGroupLocations(groupId);
            
            if (!groupLocations.isEmpty()) {
                // Broadcast to all group members via WebSocket
                broadcastGroupLocations(groupId, groupLocations);
                
                // Send to Kafka for processing
                sendGroupLocationsToKafka(groupId, groupLocations);
                
                log.info("Successfully processed {} vehicle locations for group: {}", 
                    groupLocations.size(), groupId);
            } else {
                log.warn("No vehicle locations found for group: {}", groupId);
            }
            
            return groupLocations;
            
        } catch (Exception e) {
            log.error("Failed to get and broadcast group locations for group {}: {}", 
                groupId, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<VehicleLocation> getGroupLocations(String groupId) {
        try {
            log.debug("Getting locations for group: {}", groupId);
            
            // Get all active members of the group
            List<GroupMembership> activeMemberships = groupMembershipRepository
                    .findByGroupGroupIdAndStatus(UUID.fromString(groupId), MembershipStatus.ACTIVE);
            
            if (activeMemberships.isEmpty()) {
                log.debug("No active members found for group: {}", groupId);
                return List.of();
            }
            
            log.debug("Found {} active members for group: {}", activeMemberships.size(), groupId);
            
            // Get vehicle mappings for all members directly
            List<VehicleSmartcarMapping> groupVehicleMappings = new ArrayList<>();
            for (GroupMembership membership : activeMemberships) {
                User member = membership.getUser();
                List<VehicleSmartcarMapping> memberMappings = vehicleSmartcarMappingRepository.findByUserIdAndIsActiveTrue(member.getUserId());
                groupVehicleMappings.addAll(memberMappings);
            }
            
            if (groupVehicleMappings.isEmpty()) {
                log.debug("No vehicle mappings found for group members: {}", groupId);
                return List.of();
            }
            
            log.debug("Found {} vehicle mappings for group: {}", groupVehicleMappings.size(), groupId);
            
            // Fetch locations for all vehicle mappings concurrently
            List<VehicleLocation> allLocations = fetchVehicleMappingLocationsConcurrently(groupVehicleMappings);
            
            log.info("Retrieved {} vehicle locations for group: {}", allLocations.size(), groupId);
            return allLocations;
            
        } catch (Exception e) {
            log.error("Failed to get group locations for group {}: {}", groupId, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void broadcastGroupLocations(String groupId, List<VehicleLocation> locations) {
        try {
            if (locations.isEmpty()) {
                log.debug("No locations to broadcast for group: {}", groupId);
                return;
            }
            
            // Create group location update message
            GroupLocationUpdateMessage message = GroupLocationUpdateMessage.builder()
                    .type("GROUP_LOCATION_UPDATE")
                    .groupId(groupId)
                    .timestamp(OffsetDateTime.now())
                    .vehicleCount(locations.size())
                    .locations(locations)
                    .build();
            
            // Broadcast to all group members via WebSocket
            webSocketService.broadcastToGroup(groupId, message);
            
            log.debug("Broadcasted {} vehicle locations to group: {}", locations.size(), groupId);
            
        } catch (Exception e) {
            log.error("Failed to broadcast group locations for group {}: {}", 
                groupId, e.getMessage(), e);
        }
    }

    @Override
    public void sendGroupLocationsToKafka(String groupId, List<VehicleLocation> locations) {
        try {
            if (locations.isEmpty()) {
                return;
            }
            
            // Create Kafka message with group location data
            GroupLocationKafkaMessage kafkaMessage = GroupLocationKafkaMessage.builder()
                    .groupId(groupId)
                    .timestamp(OffsetDateTime.now())
                    .vehicleCount(locations.size())
                    .locations(locations)
                    .build();
            
            // Send to Kafka topic
            String payload = objectMapper.writeValueAsString(kafkaMessage);
            kafkaTemplate.send(KAFKA_TOPIC, groupId, payload);
            
            log.debug("Sent {} vehicle locations to Kafka for group: {}", locations.size(), groupId);
            
        } catch (Exception e) {
            log.error("Failed to send group locations to Kafka for group {}: {}", 
                groupId, e.getMessage(), e);
        }
    }

    @Override
    public GroupLocationSummary getGroupLocationSummary(String groupId) {
        try {
            List<VehicleLocation> locations = getGroupLocations(groupId);
            
            return GroupLocationSummary.builder()
                    .groupId(groupId)
                    .totalVehicles(locations.size())
                    .activeVehicles((int) locations.stream().filter(loc -> loc != null).count())
                    .lastUpdateTime(OffsetDateTime.now())
                    .recentLocations(locations)
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to get group location summary for group {}: {}", 
                groupId, e.getMessage(), e);
            return null;
        }
    }

    private List<VehicleLocation> fetchVehicleMappingLocationsConcurrently(List<VehicleSmartcarMapping> mappings) {
        try {
            log.info("Fetching locations for {} vehicle mappings synchronously", mappings.size());
            
            // Process each mapping synchronously for testing
            List<VehicleLocation> locations = new ArrayList<>();
            for (VehicleSmartcarMapping mapping : mappings) {
                try {
                    log.info("Processing mapping for vehicle: {} (user: {})", 
                        mapping.getVehicleId(), mapping.getUserId());
                    
                    VehicleLocation location = fetchVehicleLocation(mapping);
                    if (location != null) {
                        locations.add(location);
                        log.info("Successfully fetched location for vehicle: {}", mapping.getVehicleId());
                    } else {
                        log.warn("No location returned for vehicle: {}", mapping.getVehicleId());
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch location for vehicle {}: {}", 
                        mapping.getVehicleId(), e.getMessage(), e);
                }
            }
            
            log.info("Successfully fetched {} locations out of {} mappings", 
                locations.size(), mappings.size());
            return locations;
                    
        } catch (Exception e) {
            log.error("Failed to fetch vehicle locations: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private VehicleLocation fetchVehicleLocation(VehicleSmartcarMapping mapping) {
        try {
            User owner = mapping.getUser();
            String accessToken = getStoredAccessToken(owner.getUserId().toString());

            if (accessToken == null) {
                log.warn("No access token found for user: {}", owner.getUserId());
                return null;
            }

            // Get Smartcar vehicle ID from mapping
            String smartcarVehicleId = mapping.getSmartcarVehicleId();
            
            if (smartcarVehicleId == null) {
                log.warn("No Smartcar mapping found for vehicle: {}", mapping.getVehicleId());
                return null;
            }

            log.debug("Fetching location for vehicle: {} (Smartcar ID: {}, owner: {})",
                mapping.getVehicleId(), smartcarVehicleId, owner.getUserId());

            VehicleLocation location = smartcarService.getVehicleLocation(accessToken, smartcarVehicleId);

            if (location != null) {
                log.debug("Successfully fetched location for vehicle: {} at ({}, {})",
                    smartcarVehicleId, location.getLatitude(), location.getLongitude());
                
                // Update last location fetch in mapping
                try {
                    // Update mapping with last location fetch time and coordinates
                    mapping.setLastLocationFetch(java.time.Instant.now());
                    mapping.setLastLocationLatitude(location.getLatitude());
                    mapping.setLastLocationLongitude(location.getLongitude());
                    
                    vehicleSmartcarMappingRepository.save(mapping);
                    
                    log.info("Updated mapping for vehicle: {} with location ({}, {}) at {}",
                        mapping.getVehicleId(), location.getLatitude(), location.getLongitude(), 
                        mapping.getLastLocationFetch());
                } catch (Exception updateError) {
                    log.warn("Failed to update location fetch time for vehicle: {}: {}", 
                        mapping.getVehicleId(), updateError.getMessage());
                }
            } else {
                log.warn("No location data returned for vehicle: {}", smartcarVehicleId);
            }

            return location;

        } catch (Exception e) {
            log.error("Failed to fetch location for vehicle {}: {}",
                mapping.getVehicleId(), e.getMessage(), e);
            return null;
        }
    }

    private String getStoredAccessToken(String userId) {
        try {
            return smartcarOAuthService.getStoredAccessToken(userId);
        } catch (Exception e) {
            log.error("Failed to retrieve access token for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class GroupLocationUpdateMessage {
        private String type;
        private String groupId;
        private java.time.OffsetDateTime timestamp;
        private int vehicleCount;
        private List<VehicleLocation> locations;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class GroupLocationKafkaMessage {
        private String groupId;
        private java.time.OffsetDateTime timestamp;
        private int vehicleCount;
        private List<VehicleLocation> locations;
    }
}
