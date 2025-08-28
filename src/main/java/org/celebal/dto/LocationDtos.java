package org.celebal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

public class LocationDtos {
    @Getter
    @Setter
    public static class IngestRequest {
        @NotBlank
        private String deviceId;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private OffsetDateTime timestamp;
    }
}


