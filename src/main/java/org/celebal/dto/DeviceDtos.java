package org.celebal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class DeviceDtos {
    @Getter
    @Setter
    public static class RegisterRequest {
        @NotBlank
        @Size(max = 100)
        private String deviceId;

        @Size(max = 255)
        private String description;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceResponse {
        private String deviceId;
        private String description;
        private String status;
    }
}


