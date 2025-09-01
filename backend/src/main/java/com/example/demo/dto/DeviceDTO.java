package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class DeviceDTO {
    private UUID id;

    @NotBlank
    private String deviceId;

    private UUID groupId;
}