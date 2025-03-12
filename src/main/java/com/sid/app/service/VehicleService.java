package com.sid.app.service;

import com.sid.app.entity.Vehicle;
import com.sid.app.model.VehicleDTO;
import com.sid.app.repository.VehicleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for handling vehicle operations, including registration and retrieval.
 * Implements business logic for managing vehicles.
 * <p>
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Registers a new vehicle.
     *
     * @param vehicleDTO The vehicle details.
     * @return The registered vehicle details.
     */
    @Transactional
    public VehicleDTO registerVehicle(VehicleDTO vehicleDTO) {
        log.info("registerVehicle() : Registering a new vehicle with chassis number: {} and registration number: {}",
                vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

        // Check if the vehicle already exists by chassis number or registration number
        Optional<Vehicle> existingVehicle = vehicleRepository.findByChassisNumberOrRegistrationNumber(
                vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

        if (existingVehicle.isPresent()) {
            log.warn("registerVehicle() : Vehicle with chassis number {} or registration number {} already exists.",
                    vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());
            throw new EntityExistsException("Vehicle already registered with the given details.");
        }

        // Save new vehicle details
        Vehicle vehicle = Vehicle.builder()
                .vehicleType(vehicleDTO.getVehicleType())
                .vehicleCompany(vehicleDTO.getVehicleCompany())
                .vehicleModel(vehicleDTO.getVehicleModel())
                .chassisNumber(vehicleDTO.getChassisNumber())
                .engineNumber(vehicleDTO.getEngineNumber())
                .registrationDate(vehicleDTO.getRegistrationDate())
                .registrationValidityDate(vehicleDTO.getRegistrationValidityDate())
                .registrationNumber(vehicleDTO.getRegistrationNumber())
                .ownerName(vehicleDTO.getOwnerName())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("registerVehicle() : Vehicle registered successfully with ID: {}", savedVehicle.getVehicleId());

        return mapToDTO(savedVehicle);
    }

    @Transactional
    public List<VehicleDTO> registerVehicles(List<VehicleDTO> vehicleDTOs) {
        List<VehicleDTO> registeredVehicles = new ArrayList<>();

        for (VehicleDTO vehicleDTO : vehicleDTOs) {
            log.info("registerVehicles() : Processing vehicle with chassis number: {} and registration number: {}",
                    vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

            // Check if the vehicle already exists
            Optional<Vehicle> existingVehicle = vehicleRepository.findByChassisNumberOrRegistrationNumber(
                    vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

            if (existingVehicle.isPresent()) {
                log.warn("registerVehicles() : Vehicle with chassis number {} or registration number {} already exists. Skipping...",
                        vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());
                continue; // Skip duplicate vehicle
            }

            // Save new vehicle details
            Vehicle vehicle = Vehicle.builder()
                    .vehicleType(vehicleDTO.getVehicleType())
                    .vehicleCompany(vehicleDTO.getVehicleCompany())
                    .vehicleModel(vehicleDTO.getVehicleModel())
                    .chassisNumber(vehicleDTO.getChassisNumber())
                    .engineNumber(vehicleDTO.getEngineNumber())
                    .registrationDate(vehicleDTO.getRegistrationDate())
                    .registrationValidityDate(vehicleDTO.getRegistrationValidityDate())
                    .registrationNumber(vehicleDTO.getRegistrationNumber())
                    .ownerName(vehicleDTO.getOwnerName())
                    .build();

            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            registeredVehicles.add(mapToDTO(savedVehicle));
        }

        log.info("registerVehicles() : Bulk vehicle registration completed. Total registered: {}", registeredVehicles.size());
        return registeredVehicles;
    }


    /**
     * Retrieves all registered vehicles.
     *
     * @return A list of all registered vehicles.
     */
    public List<VehicleDTO> getAllVehicles() {
        log.info("getAllVehicles() : Fetching all registered vehicles...");

        List<Vehicle> vehicleList = vehicleRepository.findAll();

        if (vehicleList.isEmpty()) {
            log.warn("getAllVehicles() : No registered vehicles found.");
            return Collections.emptyList(); // Return an empty list instead of throwing an exception
        }

        log.info("getAllVehicles() : Total vehicles found: {}", vehicleList.size());

        return vehicleList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves vehicle details by ID.
     *
     * @param vehicleId The ID of the vehicle.
     * @return The vehicle details.
     */
    public VehicleDTO getVehicleDetails(Long vehicleId) {
        log.info("getVehicleDetails() : Fetching vehicle details for ID: {}", vehicleId);

        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);

        if (optionalVehicle.isEmpty()) {
            log.warn("getVehicleDetails() : No vehicle found with ID: {}", vehicleId);
            return null; // Returning null to indicate not found, handled in the controller
        }

        Vehicle vehicle = optionalVehicle.get();
        log.info("getVehicleDetails() : Vehicle details retrieved successfully for ID: {}", vehicleId);

        return mapToDTO(vehicle);
    }

    @Transactional
    public VehicleDTO updateVehicle(VehicleDTO vehicleDTO) {
        log.info("updateVehicle() : Updating vehicle with ID: {}", vehicleDTO.getVehicleId());

        Vehicle vehicle = vehicleRepository.findById(vehicleDTO.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleDTO.getVehicleId()));

        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setVehicleCompany(vehicleDTO.getVehicleCompany());
        vehicle.setVehicleModel(vehicleDTO.getVehicleModel());
        vehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        vehicle.setEngineNumber(vehicleDTO.getEngineNumber());
        vehicle.setRegistrationDate(vehicleDTO.getRegistrationDate());
        vehicle.setRegistrationValidityDate(vehicleDTO.getRegistrationValidityDate());
        vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
        vehicle.setOwnerName(vehicleDTO.getOwnerName());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("updateVehicle() : Vehicle updated successfully with ID: {}", updatedVehicle.getVehicleId());

        return mapToDTO(updatedVehicle);
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param vehicleId The ID of the vehicle to delete.
     */
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        log.info("deleteVehicle() : Checking if vehicle with ID {} exists before deletion.", vehicleId);

        if (!vehicleRepository.existsById(vehicleId)) {
            log.warn("deleteVehicle() : Vehicle with ID {} not found, cannot delete.", vehicleId);
            throw new EntityNotFoundException("Vehicle not found with ID: " + vehicleId);
        }

        vehicleRepository.deleteById(vehicleId);
        log.info("deleteVehicle() : Vehicle with ID {} deleted successfully.", vehicleId);
    }

    /**
     * Maps a Vehicle entity to a VehicleDTO.
     *
     * @param vehicle The Vehicle entity.
     * @return The corresponding VehicleDTO.
     */
    private VehicleDTO mapToDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleType(vehicle.getVehicleType())
                .vehicleCompany(vehicle.getVehicleCompany())
                .vehicleModel(vehicle.getVehicleModel())
                .chassisNumber(vehicle.getChassisNumber())
                .engineNumber(vehicle.getEngineNumber())
                .registrationDate(vehicle.getRegistrationDate())
                .registrationValidityDate(vehicle.getRegistrationValidityDate())
                .registrationNumber(vehicle.getRegistrationNumber())
                .ownerName(vehicle.getOwnerName())
                .build();
    }

}