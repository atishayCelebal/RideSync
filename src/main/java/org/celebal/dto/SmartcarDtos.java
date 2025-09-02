package org.celebal.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

public class SmartcarDtos {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthUrlRequest {
        private String userId;
        private String state;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthUrlResponse {
        private String authUrl;
        private String state;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenExchangeRequest {
        private String code;
        private String state;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;
        private OffsetDateTime expiresAt;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleInfo {
        private String id;
        private String make;
        private String model;
        private Integer year;
        private String name;
        private String vin;
        private String color;
        private String bodyType;
        private String fuelType;
        private String transmission;
        private String engine;
        private String trim;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleLocation {
        private String vehicleId;
        private Double latitude;
        private Double longitude;
        private OffsetDateTime timestamp;
        private Double speed;
        private Double heading;
        private Double altitude;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleListResponse {
        private List<VehicleInfo> vehicles;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleConnection {
        private String userId;
        private String vehicleId;
        private String accessToken;
        private String refreshToken;
        private OffsetDateTime expiresAt;
        private String make;
        private String model;
        private Integer year;
        private String name;
    }
}
