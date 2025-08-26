package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    private String groupId;
    private String vehicleId;
}