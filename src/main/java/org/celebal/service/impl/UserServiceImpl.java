package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.UserDtos;
import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.celebal.repository.UserRepository;
import org.celebal.repository.VehicleRepository;
import org.celebal.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserDtos.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        if (request.getVehicleName() != null && request.getVehicleRegNo() != null) {
            Vehicle vehicle = Vehicle.builder()
                    .vehicleName(request.getVehicleName())
                    .regNo(request.getVehicleRegNo())
                    .owner(user)
                    .build();
            vehicleRepository.save(vehicle);
        }

        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}


