package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.FuelExpenseDTO;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.FuelExpenseService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing fuel expenses.
 * Provides endpoints for adding, retrieving, and deleting fuel expenses.
 * <p>
 * Author: Siddhant Patni
 */
@RestController
@Slf4j
@CrossOrigin
public class FuelExpenseController {

    @Autowired
    private FuelExpenseService fuelExpenseService;

    /**
     * Add a new fuel expense record.
     *
     * @param fuelExpenseDTO Fuel expense details
     * @return Response with added fuel expense
     */
    @PostMapping(AppConstants.VEHICLE_FUEL_EXPENSE_ENDPOINT)
    public ResponseEntity<ResponseDTO<FuelExpenseDTO>> addFuelExpense(@RequestBody FuelExpenseDTO fuelExpenseDTO) {
        log.info("Adding new fuel expense: {}", fuelExpenseDTO);
        try {
            FuelExpenseDTO savedExpense = fuelExpenseService.saveFuelExpense(fuelExpenseDTO);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(savedExpense, "Fuel expense added successfully", "SUCCESS"));
        } catch (EntityExistsException e) {
            log.error("Fuel expense already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error adding fuel expense: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @PostMapping(AppConstants.VEHICLE_FUEL_BULK_EXPENSE_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<FuelExpenseDTO>>> addFuelExpenses(@RequestBody List<FuelExpenseDTO> fuelExpenseDTOList) {
        log.info("Adding multiple fuel expenses: {}", fuelExpenseDTOList);
        try {
            List<FuelExpenseDTO> savedExpenses = fuelExpenseService.saveFuelExpenses(fuelExpenseDTOList);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(savedExpenses, "Fuel expenses added successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error adding fuel expenses: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    /**
     * Retrieve all fuel expenses.
     *
     * @return List of fuel expenses
     */
    @GetMapping(AppConstants.VEHICLE_ALL_FUEL_EXPENSE_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<FuelExpenseDTO>>> getAllFuelExpenses() {
        log.info("Fetching all fuel expenses");
        List<FuelExpenseDTO> fuelExpenses = fuelExpenseService.getAllFuelExpenses();
        return ResponseEntity.ok(ApplicationUtils.buildResponse(fuelExpenses, "Fuel expenses retrieved successfully", "SUCCESS"));
    }

    /**
     * Retrieve fuel expenses by vehicle ID and/or registration number.
     *
     * @param vehicleId          Vehicle ID (optional request parameter)
     * @param registrationNumber Vehicle registration number (optional request parameter)
     * @return List of fuel expenses matching the criteria
     */
    @GetMapping(AppConstants.VEHICLE_FUEL_EXPENSE_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<FuelExpenseDTO>>> getFuelExpenses(@RequestParam(value = "vehicleId", required = false) Long vehicleId,
                                                                             @RequestParam(value = "registrationNumber", required = false) String registrationNumber) {

        log.info("Received request to fetch fuel expenses for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);

        if (vehicleId == null && registrationNumber == null) {
            log.warn("Validation failed: Both vehicleId and registrationNumber are missing.");
            return ResponseEntity.badRequest()
                    .body(ApplicationUtils.buildResponse(null, "Either vehicleId or registrationNumber must be provided", "ERROR"));
        }

        try {
            List<FuelExpenseDTO> fuelExpenses = fuelExpenseService.getFuelExpenses(vehicleId, registrationNumber);

            if (fuelExpenses.isEmpty()) {
                log.warn("No fuel expenses found for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApplicationUtils.buildResponse(null, "No fuel expenses found", "ERROR"));
            }

            log.info("Successfully retrieved {} fuel expenses", fuelExpenses.size());
            return ResponseEntity.ok(ApplicationUtils.buildResponse(fuelExpenses, "Fuel expenses retrieved successfully", "SUCCESS"));

        } catch (IllegalArgumentException ex) {
            log.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApplicationUtils.buildResponse(null, ex.getMessage(), "ERROR"));
        }
    }

    /**
     * Delete fuel expense by ID using request parameter.
     *
     * @param id Fuel expense ID (Request Parameter)
     * @return Response entity
     */
    @DeleteMapping(value = AppConstants.VEHICLE_FUEL_EXPENSE_ENDPOINT, params = "id")
    public ResponseEntity<ResponseDTO<String>> deleteFuelExpense(@RequestParam("id") Long id) {
        log.info("Deleting fuel expense by ID: {}", id);
        try {
            fuelExpenseService.deleteFuelExpense(id);
            return ResponseEntity.ok(ApplicationUtils.buildResponse("Fuel expense deleted successfully", "Operation successful", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Fuel expense not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error deleting fuel expense: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

}