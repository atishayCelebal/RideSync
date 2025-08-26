package com.example.demo.dto.requestsDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRegistrationDto {
    @NotBlank
    private String deviceId;

    @NotBlank
    private String vehicleId;
}