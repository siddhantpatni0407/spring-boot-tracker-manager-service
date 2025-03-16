package com.sid.app.service;

import com.sid.app.entity.Vehicle;
import com.sid.app.entity.VehicleServicing;
import com.sid.app.model.VehicleServicingDTO;
import com.sid.app.repository.VehicleRepository;
import com.sid.app.repository.VehicleServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing servicing details.
 * Handles operations related to adding, retrieving, and deleting servicing details.
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class VehicleServicingService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private final VehicleServiceRepository vehicleServiceRepository;

    public VehicleServicingService(VehicleServiceRepository vehicleServiceRepository) {
        this.vehicleServiceRepository = vehicleServiceRepository;
    }

    /**
     * Saves the vehicle servicing details in the database and calculates the next service due date.
     *
     * @param vehicleServicingDTO The vehicle servicing details.
     * @return The saved vehicle servicing DTO with updated details.
     */
    public VehicleServicingDTO saveVehicleServicing(VehicleServicingDTO vehicleServicingDTO) {
        log.info("Saving vehicle servicing: {}", vehicleServicingDTO);

        // Fetch the vehicle entity from the database using the vehicleId from the DTO
        Vehicle vehicle = vehicleRepository.findById(vehicleServicingDTO.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleServicingDTO.getVehicleId()));

        // Calculate the next service due date as service date + 1 year
        LocalDate serviceDate = vehicleServicingDTO.getServiceDate();
        LocalDate nextServiceDue = serviceDate.plus(1, ChronoUnit.YEARS);
        vehicleServicingDTO.setNextServiceDue(nextServiceDue);

        // Create the vehicle servicing entity from the DTO
        VehicleServicing vehicleServicing = new VehicleServicing();
        vehicleServicing.setVehicle(vehicle);  // Set the vehicle object
        vehicleServicing.setServiceDate(vehicleServicingDTO.getServiceDate());
        vehicleServicing.setOdometerReading(vehicleServicingDTO.getOdometerReading());
        vehicleServicing.setServiceType(vehicleServicingDTO.getServiceType());
        vehicleServicing.setServiceCenter(vehicleServicingDTO.getServiceCenter());
        vehicleServicing.setServiceManager(vehicleServicingDTO.getServiceManager());
        vehicleServicing.setLocation(vehicleServicingDTO.getLocation());
        vehicleServicing.setNextServiceDue(vehicleServicingDTO.getNextServiceDue());
        vehicleServicing.setServiceCost(vehicleServicingDTO.getServiceCost());
        vehicleServicing.setRemarks(vehicleServicingDTO.getRemarks());

        // Save to repository
        VehicleServicing savedVehicleService = vehicleServiceRepository.save(vehicleServicing);
        log.info("Vehicle servicing saved successfully with ID: {}", savedVehicleService.getServicingId());

        // Convert the saved entity to DTO and return
        return convertToDTO(savedVehicleService);
    }

    public List<VehicleServicingDTO> saveVehicleServices(List<VehicleServicingDTO> vehicleServicingDTOList) {
        log.info("Saving multiple vehicle servicing records");

        List<VehicleServicing> vehicleServicings = vehicleServicingDTOList.stream().map(vehicleServicingDTO -> {
            // Fetch the vehicle entity by ID
            Vehicle vehicle = vehicleRepository.findById(vehicleServicingDTO.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleServicingDTO.getVehicleId()));

            // Calculate the next service due date (service date + 1 year)
            LocalDate nextServiceDue = vehicleServicingDTO.getServiceDate().plusYears(1);
            vehicleServicingDTO.setNextServiceDue(nextServiceDue);

            // Map DTO to entity
            VehicleServicing vehicleServicing = new VehicleServicing();
            vehicleServicing.setVehicle(vehicle);
            vehicleServicing.setServiceDate(vehicleServicingDTO.getServiceDate());
            vehicleServicing.setOdometerReading(vehicleServicingDTO.getOdometerReading());
            vehicleServicing.setServiceType(vehicleServicingDTO.getServiceType());
            vehicleServicing.setServiceCenter(vehicleServicingDTO.getServiceCenter());
            vehicleServicing.setServiceManager(vehicleServicingDTO.getServiceManager());
            vehicleServicing.setLocation(vehicleServicingDTO.getLocation());
            vehicleServicing.setNextServiceDue(vehicleServicingDTO.getNextServiceDue());
            vehicleServicing.setServiceCost(vehicleServicingDTO.getServiceCost());
            vehicleServicing.setRemarks(vehicleServicingDTO.getRemarks());

            return vehicleServicing;
        }).collect(Collectors.toList());

        // Save all vehicle servicing records to the repository
        List<VehicleServicing> savedServicings = vehicleServiceRepository.saveAll(vehicleServicings);
        log.info("Vehicle servicing records saved successfully");

        // Convert the saved entities back to DTO and return
        return savedServicings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleServicingDTO> getVehicleServices(Long vehicleId, String registrationNumber) {
        log.info("Fetching vehicle services for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);

        List<VehicleServicing> vehicleServicings = new ArrayList<>();

        if (vehicleId != null && registrationNumber != null) {
            log.debug("Both vehicleId and registrationNumber provided. Validating...");
            validateVehicle(vehicleId, registrationNumber);
            vehicleServicings = vehicleServiceRepository.findByVehicle_VehicleIdAndVehicle_RegistrationNumber(vehicleId, registrationNumber);
            if (vehicleServicings.isEmpty()) {
                throw new EntityNotFoundException("No vehicle servicing found for vehicleId: " + vehicleId + " and registrationNumber: " + registrationNumber);
            }
            log.debug("Found {} vehicle services for vehicleId: {} and registrationNumber: {}", vehicleServicings.size(), vehicleId, registrationNumber);
        } else if (vehicleId != null) {
            vehicleServicings = vehicleServiceRepository.findByVehicle_VehicleId(vehicleId);
            if (vehicleServicings.isEmpty()) {
                throw new EntityNotFoundException("No vehicle servicing found for vehicleId: " + vehicleId);
            }
            log.debug("Found {} vehicle services for vehicleId: {}", vehicleServicings.size(), vehicleId);
        } else if (registrationNumber != null) {
            vehicleServicings = vehicleServiceRepository.findByVehicle_RegistrationNumber(registrationNumber);
            if (vehicleServicings.isEmpty()) {
                throw new EntityNotFoundException("No vehicle servicing found for registrationNumber: " + registrationNumber);
            }
            log.debug("Found {} vehicle services for registrationNumber: {}", vehicleServicings.size(), registrationNumber);
        }

        return vehicleServicings
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void validateVehicle(Long vehicleId, String registrationNumber) {
        log.debug("Validating vehicle with vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);

        if (vehicleId != null && registrationNumber != null) {
            Vehicle vehicle = vehicleRepository.findByVehicleIdAndRegistrationNumber(vehicleId, registrationNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with vehicleId: " + vehicleId + " and registrationNumber: " + registrationNumber));
            log.debug("Vehicle found: {}", vehicle);
        } else if (vehicleId != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with vehicleId: " + vehicleId));
            log.debug("Vehicle found: {}", vehicle);
        } else if (registrationNumber != null) {
            Vehicle vehicle = vehicleRepository.findByRegistrationNumber(registrationNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with registrationNumber: " + registrationNumber));
            log.debug("Vehicle found: {}", vehicle);
        }
    }

    /**
     * Delete a vehicle service by ID.
     *
     * @param id Vehicle service ID
     */
    public void deleteVehicleService(Long id) {
        log.info("Deleting vehicle service with ID: {}", id);

        VehicleServicing vehicleService = vehicleServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle service not found with ID: " + id));

        vehicleServiceRepository.delete(vehicleService);
        log.info("Vehicle service deleted successfully with ID: {}", id);
    }

    private VehicleServicingDTO convertToDTO(VehicleServicing vehicleServicing) {
        return VehicleServicingDTO.builder()
                .vehicleId(vehicleServicing.getVehicle().getVehicleId())  // Include vehicle ID from the vehicle object
                .serviceDate(vehicleServicing.getServiceDate())
                .odometerReading(vehicleServicing.getOdometerReading())
                .serviceType(vehicleServicing.getServiceType())
                .serviceCenter(vehicleServicing.getServiceCenter())
                .serviceManager(vehicleServicing.getServiceManager())
                .location(vehicleServicing.getLocation())
                .nextServiceDue(vehicleServicing.getNextServiceDue())
                .serviceCost(vehicleServicing.getServiceCost())
                .remarks(vehicleServicing.getRemarks())
                .build();
    }

}