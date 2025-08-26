package com.example.demo.dto.requestsDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AddUserToGroupDto {
    @NotBlank
    private String groupId;

    private List<String> username;
}