package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupDTO {
    private UUID id;

    @NotBlank
    private String groupName;
}