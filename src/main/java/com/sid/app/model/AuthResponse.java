package com.sid.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Siddhant Patni
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String role;
    private Long userId;
    private String name;
    private String status;
    private String message;

}