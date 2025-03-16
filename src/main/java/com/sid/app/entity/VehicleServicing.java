package com.sid.app.entity;

import com.sid.app.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Author: Siddhant Patni
 */
@Entity
@Table(name = "vehicle_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class VehicleServicing extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicing_id", nullable = false)
    private Long servicingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "odometer_reading", nullable = false)
    private Long odometerReading;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "service_center", nullable = false)
    private String serviceCenter;

    @Column(name = "service_manager", nullable = false)
    private String serviceManager;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "next_service_due", nullable = false)
    private LocalDate nextServiceDue;

    @Column(name = "service_cost", nullable = false)
    private Double serviceCost;

    @Column(name = "remarks", nullable = true)
    private String remarks;

}