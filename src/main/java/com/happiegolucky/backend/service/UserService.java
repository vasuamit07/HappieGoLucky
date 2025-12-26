package com.happiegolucky.backend.service;

import com.happiegolucky.backend.entity.UserEntity;
import com.happiegolucky.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injected from SecurityConfig

    /**
     * Handles user registration: checks if username exists and encrypts the password.
     */
    public UserEntity registerNewUser(String username, String password, String confirmPassword, String email) throws RuntimeException {

        // 1. Check if the user already exists
        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match!");
        }
        Optional<UserEntity> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username '" + username + "' is already taken.");
        }

        // 2. Create the new user entity
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setEmail(email);

        // 3. Encrypt the raw password using BCrypt before saving (CRITICAL SECURITY STEP)
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        // 4. Save the new user to the database
        return userRepository.save(newUser);
    }
}