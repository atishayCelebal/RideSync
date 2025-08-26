package com.example.demo.controllers;

import com.example.demo.dto.requestsDto.UserRegistrationDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        String response = userService.registerUser(userRegistrationDto);
        return ResponseEntity.ok(response);
    }
}