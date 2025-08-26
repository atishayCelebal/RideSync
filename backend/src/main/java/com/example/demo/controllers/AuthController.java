package com.example.demo.controllers;

import com.example.demo.dto.requestsDto.UserRegistrationDto;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRegistrationDto userRegistrationDto) {
        // Logic to authenticate user and generate token
        String token = jwtUtil.generateToken(userRegistrationDto.getUsername());
        return ResponseEntity.ok(token);
    }
}