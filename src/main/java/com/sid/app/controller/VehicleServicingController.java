package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.ResponseDTO;
import com.sid.app.model.VehicleServicingDTO;
import com.sid.app.service.VehicleServicingService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing servicing details.
 * Provides endpoints for adding, retrieving, and deleting servicing details.
 * Author: Siddhant Patni
 */
@RestController
@Slf4j
@CrossOrigin
public class VehicleServicingController {

    private final VehicleServicingService vehicleServicingService;

    public VehicleServicingController(VehicleServicingService vehicleServicingService) {
        this.vehicleServicingService = vehicleServicingService;
    }

    /**
     * Adds a new vehicle servicing record to the system.
     *
     * @param vehicleServicingDTO The vehicle servicing details.
     * @return ResponseEntity containing the saved servicing record or an error message.
     */
    @PostMapping(AppConstants.VEHICLE_SERVICING_ENDPOINT)
    public ResponseEntity<ResponseDTO<VehicleServicingDTO>> addVehicleServicing(
            @RequestBody VehicleServicingDTO vehicleServicingDTO) {
        log.info("Adding new vehicle servicing: {}", vehicleServicingDTO);

        try {
            // Call the service layer to save the vehicle servicing and calculate next service due date
            VehicleServicingDTO savedServicing = vehicleServicingService.saveVehicleServicing(vehicleServicingDTO);

            // Return a successful response with the saved servicing data
            log.info("Vehicle servicing added successfully: {}", savedServicing);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(savedServicing, "Vehicle servicing added successfully", "SUCCESS"));
        } catch (EntityExistsException e) {
            log.error("Vehicle servicing already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error adding vehicle servicing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @PostMapping(AppConstants.VEHICLE_SERVICE_BULK_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<VehicleServicingDTO>>> addVehicleServices(@RequestBody List<VehicleServicingDTO> vehicleServicingDTOList) {
        log.info("Adding multiple vehicle servicing records: {}", vehicleServicingDTOList);
        try {
            List<VehicleServicingDTO> savedServicings = vehicleServicingService.saveVehicleServices(vehicleServicingDTOList);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(savedServicings, "Vehicle servicing records added successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error adding vehicle servicing records: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @GetMapping(AppConstants.VEHICLE_SERVICING_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<VehicleServicingDTO>>> getVehicleServices(@RequestParam(value = "vehicleId", required = false) Long vehicleId,
                                                                                     @RequestParam(value = "registrationNumber", required = false) String registrationNumber) {

        log.info("Received request to fetch vehicle services for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);

        if (vehicleId == null && registrationNumber == null) {
            log.warn("Validation failed: Both vehicleId and registrationNumber are missing.");
            return ResponseEntity.badRequest()
                    .body(ApplicationUtils.buildResponse(null, "Either vehicleId or registrationNumber must be provided", "ERROR"));
        }

        try {
            List<VehicleServicingDTO> vehicleServices = vehicleServicingService.getVehicleServices(vehicleId, registrationNumber);

            if (vehicleServices.isEmpty()) {
                log.warn("No vehicle services found for vehicleId: {} and registrationNumber: {}", vehicleId, registrationNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApplicationUtils.buildResponse(null, "No vehicle services found", "ERROR"));
            }

            log.info("Successfully retrieved {} vehicle services", vehicleServices.size());
            return ResponseEntity.ok(ApplicationUtils.buildResponse(vehicleServices, "Vehicle services retrieved successfully", "SUCCESS"));

        } catch (IllegalArgumentException ex) {
            log.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApplicationUtils.buildResponse(null, ex.getMessage(), "ERROR"));
        } catch (EntityNotFoundException ex) {
            log.error("Error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, ex.getMessage(), "ERROR"));
        } catch (Exception ex) {
            log.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, "Internal server error", "ERROR"));
        }
    }

    /**
     * Delete a vehicle service by ID using request parameter.
     *
     * @param id Vehicle service ID (Request Parameter)
     * @return Response entity
     */
    @DeleteMapping(value = AppConstants.VEHICLE_SERVICING_ENDPOINT, params = "vehicleServiceId")
    public ResponseEntity<ResponseDTO<String>> deleteVehicleService(@RequestParam("vehicleServiceId") Long id) {
        log.info("Deleting vehicle service by ID: {}", id);

        try {
            vehicleServicingService.deleteVehicleService(id);
            return ResponseEntity.ok(ApplicationUtils.buildResponse("Vehicle service deleted successfully", "Operation successful", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Vehicle service not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error deleting vehicle service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }


}