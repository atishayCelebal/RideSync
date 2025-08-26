package com.example.demo.service.impl;

import com.example.demo.dto.requestsDto.UserRegistrationDto;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public String registerUser(UserRegistrationDto userRegistrationDto) {
        // Logic to register user
        return "User registered successfully.";
    }
}