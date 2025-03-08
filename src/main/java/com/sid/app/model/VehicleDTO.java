package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class VehicleDTO {

    @JsonProperty("vehicleId")
    private Long vehicleId;

    @JsonProperty("vehicleType")
    private String vehicleType;

    @JsonProperty("vehicleCompany")
    private String vehicleCompany;

    @JsonProperty("vehicleModel")
    private String vehicleModel;

    @JsonProperty("chassisNumber")
    private String chassisNumber;

    @JsonProperty("engineNumber")
    private String engineNumber;

    @JsonProperty("registrationNumber")
    private String registrationNumber;

    @JsonProperty("registrationDate")
    private LocalDate registrationDate;

    @JsonProperty("registrationValidityDate")
    private LocalDate registrationValidityDate;

    @JsonProperty("ownerName")
    private String ownerName;

}