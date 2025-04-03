package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sid.app.model.enums.CardStatus;
import com.sid.app.model.enums.CardType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Author: Siddhant Patni
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCardDTO {

    @JsonProperty("cardId")
    private Long cardId;

    @JsonProperty("bankAccountId")
    private Long bankAccountId;

    @JsonProperty("cardType")
    private CardType cardType; // CREDIT, DEBIT, PREPAID

    @JsonProperty("cardNetwork")
    private String cardNetwork; // Visa, MasterCard, etc.

    @JsonProperty("cardHolderName")
    private String cardHolderName;

    @JsonProperty("cardNumber")
    private String cardNumber; // Will be stored in encrypted format

    @JsonProperty("lastFourDigits")
    private String lastFourDigits; // Only last 4 digits for display

    @JsonProperty("cvv")
    private String cvv; // Will be stored in encrypted format

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("validFromDate")
    private LocalDate validFromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("validThruDate")
    private LocalDate validThruDate;

    @JsonProperty("cardPin")
    private String cardPin;

    @JsonProperty("billingCycleDay")
    private Integer billingCycleDay; // Day of the month for billing

    @JsonProperty("creditLimit")
    private BigDecimal creditLimit; // Applicable for credit cards

    @JsonProperty("availableCredit")
    private BigDecimal availableCredit; // Remaining credit balance

    @JsonProperty("cardStatus")
    private CardStatus cardStatus; // ACTIVE, BLOCKED, EXPIRED

    @JsonProperty("isContactless")
    private Boolean isContactless;

    @JsonProperty("isVirtual")
    private Boolean isVirtual;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("encryptionKeyVersion")
    private String encryptionKeyVersion; // Tracking encryption version

}