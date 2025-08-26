package com.example.demo.service;

import com.example.demo.dto.requestsDto.UserRegistrationDto;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setUsername("testuser");
        userDto.setName("Test User");
        userDto.setPassword("password");
        userDto.setEmail("test@example.com");

        String response = userService.registerUser(userDto);
        assertEquals("User registered successfully.", response);
    }
}