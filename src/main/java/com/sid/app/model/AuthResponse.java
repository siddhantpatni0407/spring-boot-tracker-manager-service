package com.sid.app.model;

import lombok.Data;

/**
 * @author Siddhant Patni
 */
@Data
public class AuthResponse {

    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

}