package com.example.demo.dto.requestsDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    private String vehicleName;
    private String vehicleRegNo;
}