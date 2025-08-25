package com.example.demo.controller;

import com.example.demo.dto.LocationDataRequest;
import com.example.demo.dto.LocationDataResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/location")
    @SendTo("/topic/locations")
    public LocationDataResponse sendLocationData(LocationDataRequest request) {
        // Simply echo back the request as a response
        return new LocationDataResponse(
                request.getDeviceId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getTimestamp()
        );
    }
}
