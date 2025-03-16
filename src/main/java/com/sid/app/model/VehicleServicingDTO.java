package com.sid.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
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
public class VehicleServicingDTO {

    @JsonProperty("vehicleId")
    private Long vehicleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("serviceDate")
    private LocalDate serviceDate;

    @JsonProperty("odometerReading")
    private Long odometerReading;

    @JsonProperty("serviceType")
    private String serviceType;

    @JsonProperty("serviceCenter")
    private String serviceCenter;

    @JsonProperty("serviceManager")
    private String serviceManager;

    @JsonProperty("location")
    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("nextServiceDue")
    private LocalDate nextServiceDue;

    @Positive(message = "Service cost must be a positive number")
    @JsonProperty("serviceCost")
    private Double serviceCost;

    @JsonProperty("remarks")
    private String remarks;

}