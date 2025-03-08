package com.sid.app.service;

import com.sid.app.auth.JwtUtil;
import com.sid.app.constants.AppConstants;
import com.sid.app.entity.User;
import com.sid.app.model.AuthResponse;
import com.sid.app.model.LoginRequest;
import com.sid.app.model.RegisterRequest;
import com.sid.app.repository.UserRepository;
import com.sid.app.utils.AESUtils;
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
        log.info("Checking if email {} or mobile {} already exists", request.getEmail(), request.getMobileNumber());

        // Check if email or mobile number already exists
        Optional<User> existingUser = userRepository.findByEmailOrMobileNumber(request.getEmail(), request.getMobileNumber());

        if (existingUser.isPresent()) {
            User foundUser = existingUser.get();
            if (foundUser.getEmail().equals(request.getEmail())) {
                log.warn("Registration failed: Email {} already exists", request.getEmail());
                return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_EMAIL_EXISTS);
            } else {
                log.warn("Registration failed: Mobile number {} already exists", request.getMobileNumber());
                return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_MOBILE_EXISTS);
            }
        }

        // Encrypt password using AESUtil
        String encryptedPassword;
        try {
            encryptedPassword = AESUtils.encrypt(request.getPassword());
        } catch (Exception e) {
            log.error("Error encrypting password: {}", e.getMessage());
            return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_REGISTRATION);
        }

        // Create and save the new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setMobileNumber(request.getMobileNumber());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(request.getRole());

        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully: Email: {}, Mobile: {}", savedUser.getEmail(), savedUser.getMobileNumber());

        return new AuthResponse(jwtUtil.generateToken(savedUser.getEmail()), savedUser.getRole(), AppConstants.STATUS_SUCCESS, AppConstants.SUCCESS_MESSAGE_REGISTRATION_SUCCESSFUL);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("login() : Attempting login for email: {}", request.getEmail());

        // Fetch user from database
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            log.warn("login() : Login failed: User not found for email {}", request.getEmail());
            return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_USER_NOT_FOUND);
        }

        User user = optionalUser.get();

        // Decrypt stored password and validate
        String decryptedPassword;
        try {
            decryptedPassword = AESUtils.decrypt(user.getPassword());
        } catch (Exception e) {
            log.error("login() : Error decrypting password: {}", e.getMessage());
            return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_LOGIN);
        }

        if (!request.getPassword().equals(decryptedPassword)) {
            log.warn("login() : Login failed: Invalid credentials for email {}", request.getEmail());
            return new AuthResponse(null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_INVALID_LOGIN);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("login() : Login successful for email: {}", request.getEmail());

        return new AuthResponse(token, user.getRole(), AppConstants.STATUS_SUCCESS, AppConstants.LOGIN_SUCCESSFUL_MESSAGE);
    }

}
