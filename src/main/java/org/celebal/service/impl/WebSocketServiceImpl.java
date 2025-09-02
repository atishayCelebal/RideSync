package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.LocationDtos;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.service.WebSocketService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    // Track connected clients by group and user
    private final ConcurrentHashMap<String, Set<String>> groupSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionGroups = new ConcurrentHashMap<>();

    @Override
    public void broadcastLocationUpdate(LocationDtos.IngestRequest locationRequest) {
        try {
            // Create location update message
            LocationUpdateMessage message = LocationUpdateMessage.builder()
                    .type("LOCATION_UPDATE")
                    .source("DEVICE")
                    .deviceId(locationRequest.getDeviceId())
                    .latitude(locationRequest.getLatitude())
                    .longitude(locationRequest.getLongitude())
                    .timestamp(locationRequest.getTimestamp())
                    .build();
            
            // Broadcast to all connected clients
            broadcastToAll(message);
            
            log.debug("Broadcasted device location update for device: {}", locationRequest.getDeviceId());
            
        } catch (Exception e) {
            log.error("Failed to broadcast device location update: {}", e.getMessage(), e);
        }
    }

    @Override
    public void broadcastLocationUpdate(VehicleLocation vehicleLocation) {
        try {
            // Create vehicle location update message
            VehicleLocationMessage message = VehicleLocationMessage.builder()
                    .type("VEHICLE_LOCATION_UPDATE")
                    .source("SMARTCAR")
                    .vehicleId(vehicleLocation.getVehicleId())
                    .latitude(vehicleLocation.getLatitude())
                    .longitude(vehicleLocation.getLongitude())
                    .timestamp(vehicleLocation.getTimestamp())
                    .speed(vehicleLocation.getSpeed())
                    .heading(vehicleLocation.getHeading())
                    .altitude(vehicleLocation.getAltitude())
                    .build();
            
            // Broadcast to all connected clients
            broadcastToAll(message);
            
            log.debug("Broadcasted vehicle location update for vehicle: {}", vehicleLocation.getVehicleId());
            
        } catch (Exception e) {
            log.error("Failed to broadcast vehicle location update: {}", e.getMessage(), e);
        }
    }

    @Override
    public void broadcastToGroup(String groupId, Object message) {
        try {
            Set<String> sessions = groupSessions.get(groupId);
            if (sessions != null && !sessions.isEmpty()) {
                String destination = "/topic/group/" + groupId;
                messagingTemplate.convertAndSend(destination, message);
                
                log.debug("Broadcasted message to group {}: {} sessions", groupId, sessions.size());
            } else {
                log.debug("No active sessions for group: {}", groupId);
            }
        } catch (Exception e) {
            log.error("Failed to broadcast to group {}: {}", groupId, e.getMessage(), e);
        }
    }

    @Override
    public void broadcastToUser(String userId, Object message) {
        try {
            // Find all sessions for this user
            sessionUsers.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(userId))
                    .forEach(entry -> {
                        String sessionId = entry.getKey();
                        String destination = "/user/" + sessionId + "/queue/messages";
                        messagingTemplate.convertAndSendToUser(sessionId, "/queue/messages", message);
                    });
            
            log.debug("Broadcasted message to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to broadcast to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public void broadcastToAll(Object message) {
        try {
            messagingTemplate.convertAndSend("/topic/locations", message);
            log.debug("Broadcasted message to all connected clients");
        } catch (Exception e) {
            log.error("Failed to broadcast to all: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendToSession(String sessionId, Object message) {
        try {
            String destination = "/user/" + sessionId + "/queue/messages";
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/messages", message);
            log.debug("Sent message to session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to send message to session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    @Override
    public int getConnectedClientsCount() {
        return sessionUsers.size();
    }

    @Override
    public int getGroupConnectedClientsCount(String groupId) {
        Set<String> sessions = groupSessions.get(groupId);
        return sessions != null ? sessions.size() : 0;
    }

    // ===== SESSION MANAGEMENT METHODS =====

    /**
     * Register a new client session
     */
    public void registerSession(String sessionId, String userId, String groupId) {
        try {
            // Track user-session mapping
            sessionUsers.put(sessionId, userId);
            
            // Track group-session mapping
            if (groupId != null) {
                sessionGroups.put(sessionId, groupId);
                groupSessions.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            }
            
            log.info("Registered session: {} for user: {} in group: {}", sessionId, userId, groupId);
        } catch (Exception e) {
            log.error("Failed to register session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    /**
     * Unregister a client session
     */
    public void unregisterSession(String sessionId) {
        try {
            String userId = sessionUsers.remove(sessionId);
            String groupId = sessionGroups.remove(sessionId);
            
            if (groupId != null) {
                Set<String> sessions = groupSessions.get(groupId);
                if (sessions != null) {
                    sessions.remove(sessionId);
                    if (sessions.isEmpty()) {
                        groupSessions.remove(groupId);
                    }
                }
            }
            
            log.info("Unregistered session: {} for user: {} from group: {}", sessionId, userId, groupId);
        } catch (Exception e) {
            log.error("Failed to unregister session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    // ===== MESSAGE DTOs =====

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class LocationUpdateMessage {
        private String type;
        private String source;
        private String deviceId;
        private Double latitude;
        private Double longitude;
        private java.time.OffsetDateTime timestamp;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class VehicleLocationMessage {
        private String type;
        private String source;
        private String vehicleId;
        private Double latitude;
        private Double longitude;
        private java.time.OffsetDateTime timestamp;
        private Double speed;
        private Double heading;
        private Double altitude;
    }
}
