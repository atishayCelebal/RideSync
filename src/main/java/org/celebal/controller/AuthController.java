package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.AuthDtos;
import org.celebal.dto.UserDtos;
import org.celebal.mapper.UserMapper;
import org.celebal.model.User;
import org.celebal.service.UserService;
import org.celebal.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDtos.UserResponse>> register(@Valid @RequestBody UserDtos.RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully.", userMapper.toResponse(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.TokenResponse>> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String tokenStr = jwtUtil.generateToken(authentication.getName());
        AuthDtos.TokenResponse token = new AuthDtos.TokenResponse();
        token.setToken(tokenStr);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", token));
    }
}


