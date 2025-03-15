package com.sid.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Siddhant Patni
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordOtpRequest {

    private String email;

    private String otp;

}