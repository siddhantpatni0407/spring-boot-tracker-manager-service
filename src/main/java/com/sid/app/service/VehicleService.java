package com.sid.app.service;

import com.sid.app.entity.User;
import com.sid.app.entity.Vehicle;
import com.sid.app.model.VehicleDTO;
import com.sid.app.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new vehicle and save it in the database.
     *
     * @param vehicleDTO VehicleDTO
     * @return VehicleDTO
     * @throws EntityExistsException if the vehicle already exists.
     */
    @Transactional
    public VehicleDTO registerVehicle(VehicleDTO vehicleDTO) throws EntityExistsException {
        log.info("registerVehicle() : Registering a new vehicle with chassis number: {} and registration number: {}",
                vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

        // Fetch the User entity based on the userId
        User user = userRepository.findById(vehicleDTO.getUserId())
                .orElseThrow(() -> new EntityExistsException("User not found with ID: " + vehicleDTO.getUserId()));

        // Check if the vehicle already exists by chassis number or registration number
        Optional<Vehicle> existingVehicle = vehicleRepository.findByChassisNumberOrRegistrationNumber(
                vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

        if (existingVehicle.isPresent()) {
            log.warn("registerVehicle() : Vehicle with chassis number {} or registration number {} already exists.",
                    vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());
            throw new EntityExistsException("Vehicle already registered with the given details.");
        }

        // Save new vehicle details with the User entity
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
                .user(user)  // Set the User entity
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

            // Fetch the User entity based on the userId in the vehicleDTO
            User user = userRepository.findById(vehicleDTO.getUserId())
                    .orElseThrow(() -> new EntityExistsException("User not found with ID: " + vehicleDTO.getUserId()));

            // Check if the vehicle already exists
            Optional<Vehicle> existingVehicle = vehicleRepository.findByChassisNumberOrRegistrationNumber(
                    vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());

            if (existingVehicle.isPresent()) {
                log.warn("registerVehicles() : Vehicle with chassis number {} or registration number {} already exists. Skipping...",
                        vehicleDTO.getChassisNumber(), vehicleDTO.getRegistrationNumber());
                continue; // Skip duplicate vehicle
            }

            // Save new vehicle details with the User entity
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
                    .user(user) // Set the User entity
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

        // Include the userId in the VehicleDTO
        return vehicleList.stream()
                .map(vehicle -> {
                    VehicleDTO vehicleDTO = mapToDTO(vehicle);
                    // Ensure userId is mapped correctly (can be added as part of the DTO mapping logic)
                    vehicleDTO.setUserId(vehicle.getUser().getUserId());
                    return vehicleDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<VehicleDTO> getVehiclesByUserId(Long userId) {
        log.info("getVehiclesByUserId() : Fetching vehicles for user with ID: {}", userId);

        // Fetch vehicles associated with the userId
        List<Vehicle> vehicles = vehicleRepository.findByUser_UserId(userId);

        if (vehicles.isEmpty()) {
            log.warn("getVehiclesByUserId() : No vehicles found for user with ID: {}", userId);
            return Collections.emptyList(); // Return an empty list if no vehicles found
        }

        log.info("getVehiclesByUserId() : Total vehicles found for user with ID {}: {}", userId, vehicles.size());

        // Map entities to DTOs and return
        return vehicles.stream()
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

        // Ensure userId is included in the DTO
        VehicleDTO vehicleDTO = mapToDTO(vehicle);
        vehicleDTO.setUserId(vehicle.getUser().getUserId()); // Add the userId

        return vehicleDTO;
    }

    @Transactional
    public VehicleDTO updateVehicle(VehicleDTO vehicleDTO) {
        log.info("updateVehicle() : Updating vehicle with ID: {}", vehicleDTO.getVehicleId());

        Vehicle vehicle = vehicleRepository.findById(vehicleDTO.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleDTO.getVehicleId()));

        // Update the vehicle's details
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setVehicleCompany(vehicleDTO.getVehicleCompany());
        vehicle.setVehicleModel(vehicleDTO.getVehicleModel());
        vehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        vehicle.setEngineNumber(vehicleDTO.getEngineNumber());
        vehicle.setRegistrationDate(vehicleDTO.getRegistrationDate());
        vehicle.setRegistrationValidityDate(vehicleDTO.getRegistrationValidityDate());
        vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
        vehicle.setOwnerName(vehicleDTO.getOwnerName());

        // Save the updated vehicle to the database
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("updateVehicle() : Vehicle updated successfully with ID: {}", updatedVehicle.getVehicleId());

        // Include userId in the DTO
        VehicleDTO updatedVehicleDTO = mapToDTO(updatedVehicle);
        updatedVehicleDTO.setUserId(updatedVehicle.getUser().getUserId()); // Add the userId to the DTO

        return updatedVehicleDTO;
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param vehicleId The ID of the vehicle to delete.
     */
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        log.info("deleteVehicle() : Checking if vehicle with ID {} exists before deletion.", vehicleId);

        // Check if the vehicle exists in the repository
        if (!vehicleRepository.existsById(vehicleId)) {
            log.warn("deleteVehicle() : Vehicle with ID {} not found, cannot delete.", vehicleId);
            throw new EntityNotFoundException("Vehicle not found with ID: " + vehicleId);
        }

        // Get the vehicle details before deletion (to log user information)
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleId));

        // Log the user associated with the vehicle
        log.info("deleteVehicle() : Vehicle belongs to user with ID: {}", vehicle.getUser().getUserId());

        // Delete the vehicle
        vehicleRepository.deleteById(vehicleId);
        log.info("deleteVehicle() : Vehicle with ID {} deleted successfully.", vehicleId);
    }

    /**
     * Converts a Vehicle entity to a VehicleDTO.
     *
     * @param vehicle Vehicle entity
     * @return VehicleDTO
     */
    private VehicleDTO mapToDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .userId(vehicle.getUser().getUserId())  // Get userId from the User entity
                .vehicleType(vehicle.getVehicleType())
                .vehicleCompany(vehicle.getVehicleCompany())
                .vehicleModel(vehicle.getVehicleModel())
                .chassisNumber(vehicle.getChassisNumber())
                .engineNumber(vehicle.getEngineNumber())
                .registrationNumber(vehicle.getRegistrationNumber())
                .registrationDate(vehicle.getRegistrationDate())
                .registrationValidityDate(vehicle.getRegistrationValidityDate())
                .ownerName(vehicle.getOwnerName())
                .build();
    }

}