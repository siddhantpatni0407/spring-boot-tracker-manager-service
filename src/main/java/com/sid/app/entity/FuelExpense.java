package com.sid.app.entity;

import com.sid.app.audit.Auditable;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Siddhant Patni
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fuel_expense")
@EqualsAndHashCode(callSuper = true)
public class FuelExpense extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fuel_expense_id", nullable = false)
    private Long fuelExpenseId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "fuel_filled_date", nullable = false)
    private LocalDate fuelFilledDate;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "odometer_reading", nullable = false)
    private int odometerReading;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "payment_mode", nullable = false)
    private String paymentMode;

}