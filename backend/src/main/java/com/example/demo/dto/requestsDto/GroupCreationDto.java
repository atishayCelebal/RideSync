package com.example.demo.dto.requestsDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupCreationDto {
    @NotBlank
    private String groupName;

    @NotBlank
    private String adminId;
}