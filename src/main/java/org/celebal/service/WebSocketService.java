package org.celebal.service;

import org.celebal.dto.LocationDtos;
import org.celebal.dto.SmartcarDtos.VehicleLocation;

public interface WebSocketService {
    
    /**
     * Broadcast location update to all connected clients
     */
    void broadcastLocationUpdate(LocationDtos.IngestRequest locationRequest);
    
    /**
     * Broadcast Smartcar vehicle location update to all connected clients
     */
    void broadcastLocationUpdate(VehicleLocation vehicleLocation);
    
    /**
     * Broadcast location update to specific group members
     */
    void broadcastToGroup(String groupId, Object message);
    
    /**
     * Broadcast to specific user
     */
    void broadcastToUser(String userId, Object message);
    
    /**
     * Broadcast to all connected clients
     */
    void broadcastToAll(Object message);
    
    /**
     * Send message to specific session
     */
    void sendToSession(String sessionId, Object message);
    
    /**
     * Get count of connected clients
     */
    int getConnectedClientsCount();
    
    /**
     * Get count of connected clients for a specific group
     */
    int getGroupConnectedClientsCount(String groupId);
    
    /**
     * Register a new client session
     */
    void registerSession(String sessionId, String userId, String groupId);
    
    /**
     * Unregister a client session
     */
    void unregisterSession(String sessionId);
}
