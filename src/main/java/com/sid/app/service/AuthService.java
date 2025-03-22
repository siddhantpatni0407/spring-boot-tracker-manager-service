package com.sid.app.service;

import com.sid.app.auth.JwtUtil;
import com.sid.app.constants.AppConstants;
import com.sid.app.entity.User;
import com.sid.app.model.AuthResponse;
import com.sid.app.model.LoginRequest;
import com.sid.app.model.RegisterRequest;
import com.sid.app.model.ForgotPasswordResetRequest;
import com.sid.app.model.ResponseDTO;
import com.sid.app.repository.UserRepository;
import com.sid.app.utils.AESUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AESUtils aesUtils;
    private final EncryptionKeyService encryptionKeyService;

    private static final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();

    public AuthResponse register(RegisterRequest request) {
        log.info("Checking if email {} or mobile {} already exists", request.getEmail(), request.getMobileNumber());

        // Check if email or mobile number already exists
        Optional<User> existingUser = userRepository.findByEmailOrMobileNumber(request.getEmail(), request.getMobileNumber());
        if (existingUser.isPresent()) {
            User foundUser = existingUser.get();
            if (foundUser.getEmail().equals(request.getEmail())) {
                log.warn("Registration failed: Email {} already exists", request.getEmail());
                return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_EMAIL_EXISTS);
            } else {
                log.warn("Registration failed: Mobile number {} already exists", request.getMobileNumber());
                return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_MOBILE_EXISTS);
            }
        }

        // Encrypt password using AESUtils and get the latest key version for storing with the user
        String encryptedPassword;
        try {
            encryptedPassword = aesUtils.encrypt(request.getPassword());
        } catch (Exception e) {
            log.error("Error encrypting password: {}", e.getMessage());
            return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_REGISTRATION);
        }

        // Create and save the new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setMobileNumber(request.getMobileNumber());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(request.getRole());

        // Set the encryption key version from the EncryptionKeyService
        newUser.setPasswordEncryptionKeyVersion(encryptionKeyService.getLatestKey().getKeyVersion());

        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully: Email: {}, Mobile: {}", savedUser.getEmail(), savedUser.getMobileNumber());

        return new AuthResponse(
                jwtUtil.generateToken(savedUser.getEmail()),
                savedUser.getRole(),
                savedUser.getUserId(),
                savedUser.getName(),
                AppConstants.STATUS_SUCCESS,
                AppConstants.SUCCESS_MESSAGE_REGISTRATION_SUCCESSFUL
        );
    }


    public AuthResponse login(LoginRequest request) {
        log.info("login() : Attempting login for email: {}", request.getEmail());

        // Fetch user from database
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            log.warn("login() : Login failed: User not found for email {}", request.getEmail());
            return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_USER_NOT_FOUND);
        }

        User user = optionalUser.get();

        // Decrypt stored password and validate
        String decryptedPassword;
        try {
            decryptedPassword = aesUtils.decrypt(user.getPassword(), user.getPasswordEncryptionKeyVersion());
        } catch (Exception e) {
            log.error("login() : Error decrypting password: {}", e.getMessage());
            return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_LOGIN);
        }

        if (!request.getPassword().equals(decryptedPassword)) {
            log.warn("login() : Login failed: Invalid credentials for email {}", request.getEmail());
            return new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, AppConstants.ERROR_MESSAGE_INVALID_LOGIN);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("login() : Login successful for email: {}", request.getEmail());

        return new AuthResponse(token, user.getRole(), user.getUserId(), user.getName(), AppConstants.STATUS_SUCCESS, AppConstants.LOGIN_SUCCESSFUL_MESSAGE);
    }

    /**
     * Sends OTP to user's email for password reset
     *
     * @param email User's registered email
     * @return ResponseEntity<ResponseDTO < Void>>
     */
    public ResponseEntity<ResponseDTO<Void>> sendOtpForPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("User not found for email: {}", email);
            return ResponseEntity.status(404).body(new ResponseDTO<>("FAILED", "User not found.", null));
        }

        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp);
        log.info("OTP sent successfully to {}", email);

        return ResponseEntity.ok(new ResponseDTO<>(AppConstants.STATUS_SUCCESS, "OTP sent successfully to your email.", null));
    }

    /**
     * Resets user password using OTP
     *
     * @param request ForgotPasswordResetRequest
     * @return ResponseEntity<ResponseDTO < Void>>
     */
    public ResponseEntity<ResponseDTO<Void>> resetPassword(ForgotPasswordResetRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        // Validate OTP
        if (!otpStore.containsKey(email) || !otpStore.get(email).equals(otp)) {
            log.warn("Invalid OTP attempt for email: {}", email);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(AppConstants.STATUS_FAILED, "Invalid OTP.", null));
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("User not found for email: {}", email);
            return ResponseEntity.status(404).body(new ResponseDTO<>(AppConstants.STATUS_FAILED, "User not found.", null));
        }

        // Encrypt new password and update user record
        User user = userOptional.get();
        try {
            user.setPassword(aesUtils.encrypt(newPassword));
            userRepository.save(user);
            otpStore.remove(email);
            log.info("Password reset successfully for {}", email);
            return ResponseEntity.ok(new ResponseDTO<>(AppConstants.STATUS_SUCCESS, "Password reset successful.", null));
        } catch (Exception e) {
            log.error("Error encrypting password: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ResponseDTO<>(AppConstants.STATUS_FAILED, "An error occurred while resetting password.", null));
        }
    }

    public ResponseEntity<ResponseDTO<Void>> sendOtpForLogin(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("Login OTP request failed: User not found for email {}", email);
            return ResponseEntity.status(404).body(new ResponseDTO<>("FAILED", "User not found.", null));
        }

        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp);
        log.info("Login OTP sent successfully to {}", email);

        return ResponseEntity.ok(new ResponseDTO<>(AppConstants.STATUS_SUCCESS, "OTP sent successfully to your email.", null));
    }

    public ResponseEntity<AuthResponse> verifyOtp(String email, String otp) {
        if (!otpStore.containsKey(email) || !otpStore.get(email).equals(otp)) {
            log.warn("Invalid OTP attempt for email: {}", email);
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, "Invalid OTP."));
        }

        // OTP verified successfully, remove from store
        otpStore.remove(email);

        // Fetch user details
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("OTP verification failed: User not found for email {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(null, null, null, null, AppConstants.STATUS_FAILED, "User not found."));
        }

        User user = optionalUser.get();

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("OTP verified successfully. Login successful for email: {}", email);

        return ResponseEntity.ok(
                new AuthResponse(token, user.getRole(), user.getUserId(), user.getName(), AppConstants.STATUS_SUCCESS, "OTP Login Successful."));
    }

}