package com.sid.app.repository;

import com.sid.app.entity.FuelExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Siddhant Patni
 */
@Repository
public interface FuelExpenseRepository extends JpaRepository<FuelExpense, Long> {

    List<FuelExpense> findByVehicle_VehicleId(Long vehicleId);

    List<FuelExpense> findByVehicle_RegistrationNumber(String registrationNumber);

    List<FuelExpense> findByVehicle_VehicleIdAndVehicle_RegistrationNumber(Long vehicleId, String registrationNumber);

}