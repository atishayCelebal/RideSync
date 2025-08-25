package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        // Logic for user registration
        return ResponseEntity.ok("User registered successfully");
    }
}

class UserRegistrationRequest {
    private String email;
    private String password;
    private String groupName;

    // Getters and Setters
}