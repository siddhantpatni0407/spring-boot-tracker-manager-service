package com.sid.app.service;

import com.sid.app.entity.FuelExpense;
import com.sid.app.entity.Vehicle;
import com.sid.app.model.FuelExpenseDTO;
import com.sid.app.repository.FuelExpenseRepository;
import com.sid.app.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing fuel expenses.
 * Handles operations related to adding, retrieving, and deleting fuel expenses.
 * <p>
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class FuelExpenseService {

    @Autowired
    private FuelExpenseRepository fuelExpenseRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Save a fuel expense entry.
     *
     * @param fuelExpenseDTO DTO containing fuel expense details
     * @return Saved fuel expense DTO
     */
    public FuelExpenseDTO saveFuelExpense(FuelExpenseDTO fuelExpenseDTO) {
        log.info("Saving new fuel expense: {}", fuelExpenseDTO);

        Vehicle vehicle = vehicleRepository.findById(fuelExpenseDTO.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + fuelExpenseDTO.getVehicleId()));

        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setVehicle(vehicle);
        fuelExpense.setFuelFilledDate(fuelExpenseDTO.getFuelFilledDate());
        fuelExpense.setQuantity(fuelExpenseDTO.getQuantity());
        fuelExpense.setRate(fuelExpenseDTO.getRate());
        fuelExpense.setAmount(fuelExpenseDTO.getAmount());
        fuelExpense.setOdometerReading(fuelExpenseDTO.getOdometerReading());
        fuelExpense.setLocation(fuelExpenseDTO.getLocation());
        fuelExpense.setPaymentMode(fuelExpenseDTO.getPaymentMode());

        FuelExpense savedExpense = fuelExpenseRepository.save(fuelExpense);
        log.info("Fuel expense saved successfully with ID: {}", savedExpense.getFuelExpenseId());

        return convertToDTO(savedExpense);
    }

    public List<FuelExpenseDTO> saveFuelExpenses(List<FuelExpenseDTO> fuelExpenseDTOList) {
        log.info("Saving multiple fuel expenses");

        List<FuelExpense> fuelExpenses = fuelExpenseDTOList.stream().map(fuelExpenseDTO -> {
            Vehicle vehicle = vehicleRepository.findById(fuelExpenseDTO.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + fuelExpenseDTO.getVehicleId()));

            FuelExpense fuelExpense = new FuelExpense();
            fuelExpense.setVehicle(vehicle);
            fuelExpense.setFuelFilledDate(fuelExpenseDTO.getFuelFilledDate());
            fuelExpense.setQuantity(fuelExpenseDTO.getQuantity());
            fuelExpense.setRate(fuelExpenseDTO.getRate());
            fuelExpense.setAmount(fuelExpenseDTO.getAmount());
            fuelExpense.setOdometerReading(fuelExpenseDTO.getOdometerReading());
            fuelExpense.setLocation(fuelExpenseDTO.getLocation());
            fuelExpense.setPaymentMode(fuelExpenseDTO.getPaymentMode());

            return fuelExpense;
        }).collect(Collectors.toList());

        List<FuelExpense> savedExpenses = fuelExpenseRepository.saveAll(fuelExpenses);
        log.info("Fuel expenses saved successfully");

        return savedExpenses
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all fuel expenses.
     *
     * @return List of fuel expense DTOs
     */
    public List<FuelExpenseDTO> getAllFuelExpenses() {
        log.info("Fetching all fuel expenses");
        List<FuelExpense> expenses = fuelExpenseRepository.findAll();
        return expenses
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve fuel expenses by vehicle ID and/or registration number.
     *
     * @param vehicleId          Vehicle ID (optional)
     * @param registrationNumber Vehicle registration number (optional)
     * @return List of fuel expense DTOs
     */
    public List<FuelExpenseDTO> getFuelExpenses(Long vehicleId, String registrationNumber) {
        log.info("Fetching fuel expenses for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);

        List<FuelExpense> fuelExpenses = new ArrayList<>();

        if (vehicleId != null && registrationNumber != null) {
            log.debug("Both vehicleId and registrationNumber provided. Validating...");
            validateVehicle(vehicleId, registrationNumber);
            fuelExpenses = fuelExpenseRepository.findByVehicle_VehicleIdAndVehicle_RegistrationNumber(vehicleId, registrationNumber);
            log.debug("Found {} fuel expenses for vehicleId: {} and registrationNumber: {}", fuelExpenses.size(), vehicleId, registrationNumber);
        } else if (vehicleId != null) {
            fuelExpenses = fuelExpenseRepository.findByVehicle_VehicleId(vehicleId);
            log.debug("Found {} fuel expenses for vehicleId: {}", fuelExpenses.size(), vehicleId);
        } else if (registrationNumber != null) {
            fuelExpenses = fuelExpenseRepository.findByVehicle_RegistrationNumber(registrationNumber);
            log.debug("Found {} fuel expenses for registrationNumber: {}", fuelExpenses.size(), registrationNumber);
        }

        return fuelExpenses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Validates if the given vehicleId and registrationNumber belong to the same vehicle.
     *
     * @param vehicleId          Vehicle ID
     * @param registrationNumber Vehicle registration number
     */
    private void validateVehicle(Long vehicleId, String registrationNumber) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty() || !vehicleOpt.get().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
            log.error("Validation failed: vehicleId {} does not match registrationNumber {}", vehicleId, registrationNumber);
            throw new IllegalArgumentException("Mismatch between vehicleId and registrationNumber.");
        }
    }

    /**
     * Delete a fuel expense by ID.
     *
     * @param id Fuel expense ID
     */
    public void deleteFuelExpense(Long id) {
        log.info("Deleting fuel expense with ID: {}", id);
        FuelExpense expense = fuelExpenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fuel expense not found with ID: " + id));

        fuelExpenseRepository.delete(expense);
        log.info("Fuel expense deleted successfully with ID: {}", id);
    }

    /**
     * Convert FuelExpense entity to DTO.
     *
     * @param fuelExpense FuelExpense entity
     * @return FuelExpenseDTO
     */
    private FuelExpenseDTO convertToDTO(FuelExpense fuelExpense) {
        return FuelExpenseDTO.builder()
                .fuelExpenseId(fuelExpense.getFuelExpenseId())
                .vehicleId(fuelExpense.getVehicle().getVehicleId())
                .vehicleRegistrationNumber(fuelExpense.getVehicle().getRegistrationNumber())
                .fuelFilledDate(fuelExpense.getFuelFilledDate())
                .quantity(fuelExpense.getQuantity())
                .rate(fuelExpense.getRate())
                .amount(fuelExpense.getAmount())
                .odometerReading(fuelExpense.getOdometerReading())
                .location(fuelExpense.getLocation())
                .paymentMode(fuelExpense.getPaymentMode())
                .build();
    }

}