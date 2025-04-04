package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.AuthResponse;
import com.sid.app.model.LoginRequest;
import com.sid.app.model.RegisterRequest;
import com.sid.app.model.ForgotPasswordOtpRequest;
import com.sid.app.model.ForgotPasswordResetRequest;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.AuthService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        log.info("register() : Registering user with email: {} and mobile: {}", request.getEmail(), request.getMobileNumber());
        AuthResponse response = authService.register(request);
        log.info("register() : response - > {}", ApplicationUtils.getJSONString(response));
        log.info("register() : User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = AppConstants.USER_LOGIN_ENDPOINT)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("login() : request -> {}", ApplicationUtils.getJSONString(request));
        log.info("Login attempt for user: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("login() : response -> {}", ApplicationUtils.getJSONString(response));

        if ("SUCCESS".equals(response.getStatus())) {
            log.info("login() : Login successful for user: {}. Last login: {}",
                    request.getEmail(), response.getLastLoginTime());
            return ResponseEntity.ok(response);
        } else {
            HttpStatus status = response.getAccountLocked() != null && response.getAccountLocked()
                    ? HttpStatus.FORBIDDEN // 403 for locked accounts
                    : HttpStatus.UNAUTHORIZED; // 401 for other failures
            log.warn("login() : Login failed for user: {} - Reason: {}",
                    request.getEmail(), response.getMessage());
            return ResponseEntity.status(status).body(response);
        }
    }

    /**
     * Request OTP for password reset
     *
     * @param request Contains user email
     * @return ResponseEntity<ResponseDTO < Void>>
     */
    @PostMapping(AppConstants.FORGOT_PASSWORD_REQUEST_OTP_ENDPOINT)
    public ResponseEntity<ResponseDTO<Void>> requestOtp(@Valid @RequestBody ForgotPasswordOtpRequest request) {
        log.info("Received OTP request for email: {}", request.getEmail());
        return authService.sendOtpForPasswordReset(request.getEmail());
    }

    /**
     * Reset Password using OTP
     *
     * @param request Contains email, OTP, and new password
     * @return ResponseEntity<ResponseDTO < Void>>
     */
    @PostMapping(AppConstants.FORGOT_PASSWORD_RESET_ENDPOINT)
    public ResponseEntity<ResponseDTO<Void>> resetPassword(@Valid @RequestBody ForgotPasswordResetRequest request) {
        log.info("Received password reset request for email: {}", request.getEmail());
        return authService.resetPassword(request);
    }

    @PostMapping(AppConstants.LOGIN_REQUEST_OTP_ENDPOINT)
    public ResponseEntity<ResponseDTO<Void>> requestLoginOtp(@RequestBody LoginRequest request) {
        log.info("Received login OTP request for email: {}", request.getEmail());
        return authService.sendOtpForLogin(request.getEmail());
    }

    @PostMapping(AppConstants.VERIFY_OTP_ENDPOINT)
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody ForgotPasswordOtpRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());
        return authService.verifyOtp(request.getEmail(), request.getOtp());
    }

}