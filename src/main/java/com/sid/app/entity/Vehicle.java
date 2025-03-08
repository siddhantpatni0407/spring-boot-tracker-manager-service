package com.sid.app.entity;

import com.sid.app.audit.Auditable;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author Siddhant Patni
 */
@Entity
@Table(name = "vehicle", uniqueConstraints = {
        @UniqueConstraint(columnNames = "chassis_number"),
        @UniqueConstraint(columnNames = "engine_number"),
        @UniqueConstraint(columnNames = "registration_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "vehicle_company", nullable = false)
    private String vehicleCompany;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "chassis_number", nullable = false, unique = true)
    private String chassisNumber;

    @Column(name = "engine_number", nullable = false, unique = true)
    private String engineNumber;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "registration_validity_date", nullable = false)
    private LocalDate registrationValidityDate;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

}