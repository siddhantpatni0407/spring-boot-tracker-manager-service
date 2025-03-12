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
    public static final String VEHICLE_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/register";
    public static final String VEHICLE_BULK_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/bulk-register";
    public static final String FETCH_ALL_VEHICLES_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/fetch";
    public static final String VEHICLE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle";
    public static final String FETCH_ALL_USERS_ENDPOINT = "/api/v1/tracker-manager-service/user/fetch";
    public static final String USER_ENDPOINT = "/api/v1/tracker-manager-service/user";
    public static final String VEHICLE_FUEL_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/fuel-expense";
    public static final String VEHICLE_FUEL_BULK_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/bulk-fuel-expense";
    public static final String VEHICLE_ALL_FUEL_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/all-fuel-expense";

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
    public static final String SECRET_KEY = "Xf9aLp3qzT7vN2sYgW5KbVc6Rm8QJ0dP"; // 16, 24, or 32 chars

    public static final String DEFAULT_USER = "DEFAULT_USER";

}