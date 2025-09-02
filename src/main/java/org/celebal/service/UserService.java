package org.celebal.service;

import org.celebal.dto.UserDtos;
import org.celebal.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User register(UserDtos.RegisterRequest request);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
}


