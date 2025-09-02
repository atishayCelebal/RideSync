package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketService webSocketService;

    /**
     * Handle client connection and register session
     */
    @MessageMapping("/connect")
    public void handleConnect(@Payload ConnectRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            String userId = request.getUserId();
            String groupId = request.getGroupId();
            
            log.info("Client connecting - Session: {}, User: {}, Group: {}", sessionId, userId, groupId);
            
            // Register the session
            webSocketService.registerSession(sessionId, userId, groupId);
            
            // Send connection confirmation
            ConnectionResponse response = ConnectionResponse.builder()
                    .type("CONNECTION_CONFIRMED")
                    .sessionId(sessionId)
                    .userId(userId)
                    .groupId(groupId)
                    .message("Successfully connected to RideSync WebSocket")
                    .build();
            
            webSocketService.sendToSession(sessionId, response);
            
        } catch (Exception e) {
            log.error("Failed to handle client connection: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle client disconnection
     */
    @MessageMapping("/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            log.info("Client disconnecting - Session: {}", sessionId);
            
            // Unregister the session
            webSocketService.unregisterSession(sessionId);
            
        } catch (Exception e) {
            log.error("Failed to handle client disconnection: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle location subscription request
     */
    @MessageMapping("/subscribe/location")
    public void handleLocationSubscription(@Payload LocationSubscriptionRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            String groupId = request.getGroupId();
            
            log.info("Client subscribing to location updates - Session: {}, Group: {}", sessionId, groupId);
            
            // Send subscription confirmation
            SubscriptionResponse response = SubscriptionResponse.builder()
                    .type("LOCATION_SUBSCRIPTION_CONFIRMED")
                    .groupId(groupId)
                    .message("Successfully subscribed to location updates")
                    .build();
            
            webSocketService.sendToSession(sessionId, response);
            
        } catch (Exception e) {
            log.error("Failed to handle location subscription: {}", e.getMessage(), e);
        }
    }

    // ===== DTOs =====

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class ConnectRequest {
        private String userId;
        private String groupId;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class ConnectionResponse {
        private String type;
        private String sessionId;
        private String userId;
        private String groupId;
        private String message;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class LocationSubscriptionRequest {
        private String groupId;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    public static class SubscriptionResponse {
        private String type;
        private String groupId;
        private String message;
    }
}
