package com.sid.app.constants;

/**
 * Application-wide constants segregated based on categories.
 *
 * @author Siddhant Patni
 */
public class AppConstants {

    /**
     * API Endpoints
     */
    public static final String USER_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/user/register";
    public static final String USER_LOGIN_ENDPOINT = "/api/v1/tracker-manager-service/user/login";
    public static final String LOGIN_REQUEST_OTP_ENDPOINT = "/api/v1/tracker-manager-service/user/otp-login";
    public static final String FORGOT_PASSWORD_REQUEST_OTP_ENDPOINT = "/api/v1/tracker-manager-service/forgot-password/request-otp";
    public static final String FORGOT_PASSWORD_RESET_ENDPOINT = "/api/v1/tracker-manager-service/forgot-password/reset";
    public static final String VERIFY_OTP_ENDPOINT = "/api/v1/tracker-manager-service/auth/verify-otp";
    public static final String VEHICLE_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/register";
    public static final String VEHICLE_BULK_REGISTER_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/bulk-register";
    public static final String FETCH_ALL_VEHICLES_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/fetch";
    public static final String FETCH_ALL_VEHICLES_BY_USER_ENDPOINT = "/api/v1/tracker-manager-service/user-vehicle/fetch";
    public static final String VEHICLE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle";
    public static final String VEHICLE_SERVICING_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/service";
    public static final String VEHICLE_SERVICE_BULK_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/bulk-service";
    public static final String FETCH_ALL_USERS_ENDPOINT = "/api/v1/tracker-manager-service/user/fetch";
    public static final String USER_ENDPOINT = "/api/v1/tracker-manager-service/user";
    public static final String VEHICLE_FUEL_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/fuel-expense";
    public static final String VEHICLE_FUEL_BULK_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/bulk-fuel-expense";
    public static final String VEHICLE_ALL_FUEL_EXPENSE_ENDPOINT = "/api/v1/tracker-manager-service/vehicle/all-fuel-expense";
    public static final String STOCK_NIFTY_STOCK_DATA_ENDPOINT = "/api/v1/tracker-manager-service/stock/nifty-data";
    public static final String CREDENTIALS_ENDPOINT = "/api/v1/tracker-manager-service/credentials";

    /**
     * Status Messages
     */
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    /**
     * Success Messages
     */
    public static final String SUCCESS_MESSAGE_REGISTRATION_SUCCESSFUL = "Registration successful";
    public static final String LOGIN_SUCCESSFUL_MESSAGE = "Login successful";
    public static final String MSG_STOCK_DATA_RETRIEVED = "Stock data retrieved successfully for index -> ";

    /**
     * Error Messages
     */
    public static final String ERROR_MESSAGE_REGISTRATION = "Error processing registration";
    public static final String ERROR_MESSAGE_EMAIL_EXISTS = "Email already registered";
    public static final String ERROR_MESSAGE_MOBILE_EXISTS = "Mobile number already registered";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "User not found";
    public static final String ERROR_MESSAGE_LOGIN = "Error processing login";
    public static final String ERROR_MESSAGE_INVALID_LOGIN = "Invalid credentials";
    public static final String ERROR_STOCK_DATA_NOT_FOUND = "No stock data found for the given index.";
    public static final String ERROR_EXTERNAL_API_FAILURE = "Failed to retrieve stock data from external API.";
    public static final String ERROR_UNEXPECTED = "An unexpected error occurred. Please contact support.";
    public static final String NO_STOCK_DATA_FOUND = "No stock data found.";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";

    /**
     * Logging Messages
     */
    public static final String FLOW_START = "*************** START *************** ";
    public static final String FLOW_END = "*************** END *************** ";
    public static final String METHOD_GET_STOCK_DATA = "getStockData() : ";
    public static final String METHOD_INVOKE_STOCK_DATA = "invokeStockData() : ";
    public static final String LOG_REQUEST_FETCH_STOCK_DATA = "Received request to fetch stock data for index: {}";
    public static final String LOG_STOCK_DATA_RETRIEVED = "Stock data retrieved successfully for index: {}";
    public static final String LOG_STOCK_EXCEPTION = "StockException occurred: {}";
    public static final String LOG_EXTERNAL_API_ERROR = "External API error: {} - Response: {}";
    public static final String LOG_NO_STOCK_DATA = "No stock data found for index: {}";
    public static final String LOG_FETCHING_STOCK_DATA = "Fetching stock data from NSE API: {}";
    public static final String LOG_RETRYING_API_CALL = "Retrying NSE API call... Attempt: {}";
    public static final String LOG_UNAUTHORIZED_ACCESS = "Unauthorized access detected while fetching stock data: {}";
    public static final String LOG_SERVER_ERROR = "Server error occurred while fetching stock data from NSE: {}";
    public static final String LOG_UNEXPECTED_ERROR = "Unexpected error occurred while processing stock data request.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to NSE API. Please check API permissions.";
    public static final String NSE_API_UNAVAILABLE = "NSE API is currently unavailable. Please try again later.";

    /**
     * Security and Encryption
     */
    public static final String AES_ALGORITHM = "AES";
    public static final String SECRET_KEY = "Xf9aLp3qzT7vN2sYgW5KbVc6Rm8QJ0dP"; //32 chars length
    public static final String DEFAULT_USER = "DEFAULT_USER";

    /**
     * WebClient Headers for NSE API
     */
    public static final String HEADER_SEC_FETCH_SITE = "Sec-Fetch-Site";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_HOST = "Host";
    public static final String WEBCLIENT_USER_AGENT = "Mozilla/5.0";
    public static final String WEBCLIENT_REFERER = "https://www.nseindia.com/";
    public static final String WEBCLIENT_ACCEPT = "application/json, text/plain, */*";
    public static final String WEBCLIENT_ACCEPT_LANGUAGE = "en-US,en;q=0.9";
    public static final String WEBCLIENT_SEC_FETCH_SITE = "same-origin";
    public static final String WEBCLIENT_CACHE_CONTROL = "no-cache";
    public static final String WEBCLIENT_PRAGMA = "no-cache";
    public static final String WEBCLIENT_CONNECTION = "keep-alive";
    public static final String WEBCLIENT_HOST = "www.nseindia.com";

}