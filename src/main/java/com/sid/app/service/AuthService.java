package com.sid.app.service;

import com.sid.app.auth.JwtUtil;
import com.sid.app.entity.User;
import com.sid.app.exception.UserNotFoundException;
import com.sid.app.exception.InvalidCredentialsException;
import com.sid.app.model.AuthResponse;
import com.sid.app.model.LoginRequest;
import com.sid.app.model.RegisterRequest;
import com.sid.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        log.info("Checking if email {} already exists", request.getEmail());
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new RuntimeException("Email already exists!");
        }

        User newUser = new User(null, request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), "USER");

        User savedUser = userRepository.save(newUser);
        log.info("User {} registered successfully", savedUser.getEmail());

        return new AuthResponse(jwtUtil.generateToken(savedUser.getEmail()));
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: User {} not found", request.getEmail());
                    return new UserNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Invalid credentials for user {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        log.info("Login successful for user: {}", request.getEmail());
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }

}
