package org.celebal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

public class VehicleDtos {
    @Getter
    @Setter
    public static class UpsertRequest {
        @NotBlank
        @Size(max = 150)
        private String vehicleName;

        @NotBlank
        @Size(max = 50)
        private String regNo;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleResponse {
        private UUID vehicleId;
        private String vehicleName;
        private String regNo;
        private UUID ownerUserId;
    }
}


