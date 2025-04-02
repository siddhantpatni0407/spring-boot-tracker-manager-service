package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sid.app.model.enums.AccountStatus;
import com.sid.app.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Author: Siddhant Patni
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDTO {

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("accountHolderName")
    private String accountHolderName;

    @JsonProperty("accountType")
    private AccountType accountType;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("branchName")
    private String branchName;

    @JsonProperty("ifscCode")
    private String ifscCode;

    @JsonProperty("branchLocation")
    private String branchLocation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("openingDate")
    private LocalDate openingDate;

    @JsonProperty("nomineeName")
    private String nomineeName;

    @JsonProperty("accountStatus")
    private AccountStatus accountStatus;

}