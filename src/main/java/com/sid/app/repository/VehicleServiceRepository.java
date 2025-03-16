package com.sid.app.repository;

import com.sid.app.entity.VehicleServicing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Siddhant Patni
 */
@Repository
public interface VehicleServiceRepository extends JpaRepository<VehicleServicing, Long> {

    // Find by both vehicleId and registrationNumber of the Vehicle
    List<VehicleServicing> findByVehicle_VehicleIdAndVehicle_RegistrationNumber(Long vehicleId, String registrationNumber);

    // Find by vehicleId of the Vehicle
    List<VehicleServicing> findByVehicle_VehicleId(Long vehicleId);

    // Find by registrationNumber of the Vehicle
    List<VehicleServicing> findByVehicle_RegistrationNumber(String registrationNumber);

}