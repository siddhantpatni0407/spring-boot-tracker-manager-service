package com.sid.app.model;

import lombok.Data;

/**
 * @author Siddhant Patni
 */
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}