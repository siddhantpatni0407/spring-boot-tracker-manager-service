package com.sid.app.config;

import com.sid.app.constants.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)  // ✅ Recommended way to disable CSRF
                .authorizeExchange(auth -> auth
                        .pathMatchers(AppConstants.USER_REGISTER_ENDPOINT, AppConstants.USER_LOGIN_ENDPOINT).permitAll()  // ✅ Allow register & login without auth
                        .anyExchange().authenticated()  // ✅ Secure all other endpoints
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}