package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Author: Siddhant Patni
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredentialDTO {

    @JsonProperty("credentialId")
    private Long credentialId;

    @NotNull(message = "User ID cannot be null")
    @JsonProperty("userId")
    private Long userId;

    @NotBlank(message = "Account name is required")
    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("website")
    private String website;

    @JsonProperty("url")
    private String url;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    private String password; // Will be stored in encrypted format

    @JsonProperty("status")
    private String status;

    @JsonProperty("remarks")
    private String remarks;

}