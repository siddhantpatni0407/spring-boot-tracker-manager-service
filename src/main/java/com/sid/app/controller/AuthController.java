package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.AuthResponse;
import com.sid.app.model.LoginRequest;
import com.sid.app.model.RegisterRequest;
import com.sid.app.service.AuthService;
import com.sid.app.utils.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = AppConstants.USER_REGISTER_ENDPOINT)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("register() : request - > {}", ApplicationUtils.getJSONString(request));
        log.info("Registering user with email: {} and mobile: {}", request.getEmail(), request.getMobileNumber());
        AuthResponse response = authService.register(request);
        log.info("register() : response - > {}", ApplicationUtils.getJSONString(response));
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = AppConstants.USER_LOGIN_ENDPOINT)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("login() : request - > {}", ApplicationUtils.getJSONString(request));
        log.info("Login attempt for user: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("login() : response - > {}", ApplicationUtils.getJSONString(response));
        log.info("Login successful for user: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

}