package com.sid.app.repository;

import com.sid.app.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing vehicle entities.
 * Extends JpaRepository to provide built-in CRUD operations.
 * <p>
 * Author: Siddhant Patni
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Finds a vehicle by chassis number or registration number.
     *
     * @param chassisNumber      The chassis number of the vehicle.
     * @param registrationNumber The registration number of the vehicle.
     * @return An optional vehicle if found.
     */
    Optional<Vehicle> findByChassisNumberOrRegistrationNumber(String chassisNumber, String registrationNumber);

    // Fetch vehicles by userId
    List<Vehicle> findByUser_UserId(Long userId);

    Optional<Vehicle> findById(Long id);

    Optional<Vehicle> findByVehicleIdAndRegistrationNumber(Long vehicleId, String registrationNumber);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

}