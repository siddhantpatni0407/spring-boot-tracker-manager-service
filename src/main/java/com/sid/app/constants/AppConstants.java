package com.sid.app.constants;

/**
 * @author Siddhant Patni
 */
public class AppConstants {

    /**
     * The constant ENDPOINT.
     */
    public static final String USER_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/user/register";
    public static final String USER_LOGIN_ENDPOINT = "/api/v1/tracker-manager-service/user/login";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String SUCCESS_MESSAGE_REGISTRATION_SUCCESSFUL = "Registration successful";
    public static final String ERROR_MESSAGE_REGISTRATION = "Error processing registration";
    public static final String ERROR_MESSAGE_EMAIL_EXISTS = "Email already registered";
    public static final String ERROR_MESSAGE_MOBILE_EXISTS = "Mobile number already registered";
    public static final String LOGIN_SUCCESSFUL_MESSAGE = "Login successful";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "User not found";
    public static final String ERROR_MESSAGE_LOGIN = "Error processing login";
    public static final String ERROR_MESSAGE_INVALID_LOGIN = "Invalid credentials";
    public static final String AES_ALGORITHM = "AES";
    public static final String SECRET_KEY = "lP2B6+TzdF8K8mBhqRZoX4zt8ktHl6D4jbF6IY76BvI="; // 16, 24, or 32 chars

}