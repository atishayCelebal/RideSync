package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.RideSessionDtos;
import org.celebal.service.WebSocketService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastLocationUpdate(String groupId, RideSessionDtos.LocationUpdate update) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/location", update);
    }

    @Override
    public void broadcastSessionEvent(String groupId, String eventType, Object data) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/" + eventType, data);
    }
}
