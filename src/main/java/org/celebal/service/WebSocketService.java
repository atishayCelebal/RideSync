package org.celebal.service;

import org.celebal.dto.RideSessionDtos;

public interface WebSocketService {
    void broadcastLocationUpdate(String groupId, RideSessionDtos.LocationUpdate update);
    void broadcastSessionEvent(String groupId, String eventType, Object data);
}
