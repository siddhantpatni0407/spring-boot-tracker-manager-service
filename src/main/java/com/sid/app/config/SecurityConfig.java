package com.sid.app.config;

import com.sid.app.constants.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(auth -> auth
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                AppConstants.DB_BACKUP_ENDPOINT,
                                AppConstants.USER_REGISTER_ENDPOINT,
                                AppConstants.USER_LOGIN_ENDPOINT,
                                AppConstants.LOGIN_REQUEST_OTP_ENDPOINT,
                                AppConstants.VEHICLE_REGISTER_ENDPOINT,
                                AppConstants.VEHICLE_BULK_REGISTER_ENDPOINT,
                                AppConstants.VEHICLE_ENDPOINT,
                                AppConstants.FETCH_ALL_VEHICLES_ENDPOINT,
                                AppConstants.FETCH_ALL_VEHICLES_BY_USER_ENDPOINT,
                                AppConstants.FETCH_ALL_USERS_ENDPOINT,
                                AppConstants.USER_ENDPOINT,
                                AppConstants.VEHICLE_ALL_FUEL_EXPENSE_ENDPOINT,
                                AppConstants.VEHICLE_FUEL_EXPENSE_ENDPOINT,
                                AppConstants.VEHICLE_FUEL_BULK_EXPENSE_ENDPOINT,
                                AppConstants.STOCK_NIFTY_STOCK_DATA_ENDPOINT,
                                AppConstants.FORGOT_PASSWORD_REQUEST_OTP_ENDPOINT,
                                AppConstants.FORGOT_PASSWORD_RESET_ENDPOINT,
                                AppConstants.VERIFY_OTP_ENDPOINT,
                                AppConstants.VEHICLE_SERVICING_ENDPOINT,
                                AppConstants.VEHICLE_SERVICE_BULK_ENDPOINT,
                                AppConstants.CREDENTIALS_ENDPOINT,
                                AppConstants.BANK_ACCOUNT_ENDPOINT,
                                AppConstants.FETCH_BANK_ACCOUNT_BY_USER_ENDPOINT,
                                AppConstants.BULK_BANK_ACCOUNT_ENDPOINT,
                                AppConstants.FETCH_ALL_BANK_ACCOUNTS,
                                AppConstants.BANK_CARD_ENDPOINT,
                                AppConstants.BULK_BANK_CARD_ENDPOINT,
                                AppConstants.BANK_CARD_BY_BANK_ID_ENDPOINT,
                                AppConstants.BANK_CARD_BY_BANK_CARD_ID_ENDPOINT
                        )
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // ✅ Match frontend URL
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Access-Control-Allow-Origin"));
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

}