package com.example.demo.dto.requestsDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationDataDto {
    @NotBlank
    private String deviceId;

    @NotNull
    private Float latitude;

    @NotNull
    private Float longitude;

    @NotBlank
    private String timestamp;
}