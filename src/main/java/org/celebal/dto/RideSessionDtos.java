package org.celebal.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RideSessionDtos {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionResponse {
        private UUID sessionId;
        private UUID groupId;
        private OffsetDateTime rideStart;
        private OffsetDateTime rideEnd;
        private String status;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationUpdate {
        private String deviceId;
        private Double latitude;
        private Double longitude;
        private OffsetDateTime timestamp;
        private String username;
    }
}
