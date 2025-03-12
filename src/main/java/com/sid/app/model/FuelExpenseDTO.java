package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Author: Siddhant Patni
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties("fuelExpenseId")
public class FuelExpenseDTO {

    @JsonProperty("fuelExpenseId")
    private Long fuelExpenseId;

    @JsonProperty("vehicleId")
    private Long vehicleId;

    @JsonProperty("vehicleRegistrationNumber")
    private String vehicleRegistrationNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("fuelFilledDate")
    private LocalDate fuelFilledDate;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("odometerReading")
    private int odometerReading;

    @JsonProperty("location")
    private String location;

    @JsonProperty("paymentMode")
    private String paymentMode;

}