package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Author: Siddhant Patni
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("role")
    private String role;

    @JsonProperty("isActive")
    private Boolean isActive;

}