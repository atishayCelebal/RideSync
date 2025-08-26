package com.example.demo.service;

import com.example.demo.dto.requestsDto.UserRegistrationDto;

public interface UserService {
    String registerUser(UserRegistrationDto userRegistrationDto);
}