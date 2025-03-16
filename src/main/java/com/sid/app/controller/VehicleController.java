package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.VehicleDTO;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.VehicleService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for managing vehicle registration and retrieval.
 * Provides endpoints for registering a vehicle and fetching all registered vehicles.
 * <p>
 * Author: Siddhant Patni
 */
@RestController
@Slf4j
@CrossOrigin
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * Registers a new vehicle.
     *
     * @param request The vehicle details to register.
     * @return ResponseEntity containing the response status, message, and registered vehicle details.
     */
    @PostMapping(AppConstants.VEHICLE_REGISTER_ENDPOINT)
    public ResponseEntity<ResponseDTO<VehicleDTO>> registerVehicle(@RequestBody VehicleDTO request) {
        log.info("registerVehicle() : Received request to register vehicle: {}", ApplicationUtils.getJSONString(request));

        try {
            VehicleDTO registeredVehicle = vehicleService.registerVehicle(request);
            log.info("registerVehicle() : Vehicle registered successfully with ID: {}", registeredVehicle.getVehicleId());

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("SUCCESS")
                    .message("Vehicle registered successfully.")
                    .data(registeredVehicle)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response); // HTTP 201 Created
        } catch (EntityExistsException e) {
            log.error("registerVehicle() : Vehicle registration failed - {}", e.getMessage());
            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("FAILURE")
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 Conflict
        } catch (Exception e) {
            log.error("registerVehicle() : Unexpected error during vehicle registration", e);
            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred during vehicle registration.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Registers bulk vehicles.
     *
     * @param requests The bulk vehicle details to register.
     * @return ResponseEntity containing the response status, message, and registered vehicle details.
     */
    @PostMapping(AppConstants.VEHICLE_BULK_REGISTER_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<VehicleDTO>>> registerVehicles(@RequestBody List<VehicleDTO> requests) {
        log.info("registerVehicles() : Received request to register {} vehicles", requests.size());

        try {
            // Pass the userId from each vehicleDTO to the service layer
            List<VehicleDTO> registeredVehicles = vehicleService.registerVehicles(requests);
            log.info("registerVehicles() : Successfully registered {} vehicles", registeredVehicles.size());

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("SUCCESS")
                    .message("Vehicles registered successfully.")
                    .data(registeredVehicles)
                    .build();

            log.info("registerVehicles() : response -> {}", ApplicationUtils.getJSONString(response));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("registerVehicles() : Unexpected error during bulk vehicle registration", e);

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred during bulk vehicle registration.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieves all registered vehicles.
     *
     * @return ResponseEntity containing the response status, message, and list of all registered vehicles.
     */
    @GetMapping(AppConstants.FETCH_ALL_VEHICLES_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<VehicleDTO>>> getAllVehicles() {
        log.info("getAllVehicles() : Received request to fetch all vehicles.");

        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehicles();

            if (vehicles.isEmpty()) {
                log.warn("getAllVehicles() : No vehicles found.");

                ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                        .status("FAILURE")
                        .message("No vehicles registered in the system.")
                        .data(Collections.emptyList())
                        .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404 Not Found
            }

            log.info("getAllVehicles() : Successfully retrieved {} vehicles.", vehicles.size());

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("SUCCESS")
                    .message("Vehicles retrieved successfully.")
                    .data(vehicles)
                    .build();

            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (Exception e) {
            log.error("getAllVehicles() : Unexpected error occurred while fetching vehicles", e);

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred while fetching vehicles.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(AppConstants.FETCH_ALL_VEHICLES_BY_USER_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<VehicleDTO>>> getVehiclesByUserId(@RequestParam("userId") Long userId) {
        log.info("getVehiclesByUserId() : Received request to fetch vehicles for user with ID: {}", userId);

        try {
            List<VehicleDTO> vehicles = vehicleService.getVehiclesByUserId(userId);

            if (vehicles.isEmpty()) {
                log.warn("getVehiclesByUserId() : No vehicles found for user with ID: {}", userId);

                ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                        .status("FAILURE")
                        .message("No vehicles found for the user with ID: " + userId)
                        .data(Collections.emptyList())
                        .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404 Not Found
            }

            log.info("getVehiclesByUserId() : Successfully retrieved {} vehicles for user with ID: {}", vehicles.size(), userId);

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("SUCCESS")
                    .message("Vehicles retrieved successfully for user with ID: " + userId)
                    .data(vehicles)
                    .build();

            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (Exception e) {
            log.error("getVehiclesByUserId() : Unexpected error occurred while fetching vehicles for user with ID: {}", userId, e);

            ResponseDTO<List<VehicleDTO>> response = ResponseDTO.<List<VehicleDTO>>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred while fetching vehicles.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * Retrieves vehicle details by ID.
     *
     * @param vehicleId The ID of the vehicle.
     * @return ResponseEntity containing the response status, message, and vehicle details.
     */
    @GetMapping(AppConstants.VEHICLE_ENDPOINT)
    public ResponseEntity<ResponseDTO<VehicleDTO>> getVehicleDetails(@RequestParam("vehicleId") Long vehicleId) {
        log.info("getVehicleDetails() : Received request to fetch vehicle with ID: {}", vehicleId);

        try {
            VehicleDTO vehicle = vehicleService.getVehicleDetails(vehicleId);

            if (vehicle == null) {
                log.warn("getVehicleDetails() : No vehicle found with ID: {}", vehicleId);

                ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                        .status("FAILED")
                        .message("Vehicle not found with ID: " + vehicleId)
                        .data(null)
                        .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Return 404 status
            }

            log.info("getVehicleDetails() : Vehicle details retrieved successfully for ID: {}", vehicleId);

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("SUCCESS")
                    .message("Vehicle details retrieved successfully.")
                    .data(vehicle)
                    .build();

            return ResponseEntity.ok(response); // Return 200 OK status
        } catch (Exception e) {
            log.error("getVehicleDetails() : Unexpected error occurred while fetching vehicle with ID: {}", vehicleId, e);

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred while fetching vehicle details.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 error status
        }
    }

    @PutMapping(AppConstants.VEHICLE_ENDPOINT)
    public ResponseEntity<ResponseDTO<VehicleDTO>> updateVehicle(@RequestBody VehicleDTO request) {
        log.info("updateVehicle() : Received request to update vehicle: {}", ApplicationUtils.getJSONString(request));

        try {
            VehicleDTO updatedVehicle = vehicleService.updateVehicle(request);
            log.info("updateVehicle() : Vehicle updated successfully with ID: {}", updatedVehicle.getVehicleId());

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("SUCCESS")
                    .message("Vehicle updated successfully.")
                    .data(updatedVehicle)
                    .build();

            return ResponseEntity.ok(response); // Return 200 OK status
        } catch (EntityNotFoundException e) {
            log.error("updateVehicle() : Vehicle update failed - {}", e.getMessage());

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("FAILURE")
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Return 404 Not Found
        } catch (Exception e) {
            log.error("updateVehicle() : Unexpected error during vehicle update", e);

            ResponseDTO<VehicleDTO> response = ResponseDTO.<VehicleDTO>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred during vehicle update.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param vehicleId The ID of the vehicle to delete.
     * @return ResponseEntity with the response status and message.
     */
    @DeleteMapping(AppConstants.VEHICLE_ENDPOINT)
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(@RequestParam("vehicleId") Long vehicleId) {
        log.info("deleteVehicle() : Received request to delete vehicle with ID: {}", vehicleId);

        try {
            // Call the service layer to delete the vehicle
            vehicleService.deleteVehicle(vehicleId);

            // Prepare the success response
            ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                    .status("SUCCESS")
                    .message("Vehicle deleted successfully.")
                    .build();

            log.info("deleteVehicle() : Vehicle with ID {} deleted successfully.", vehicleId);
            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (EntityNotFoundException ex) {
            log.warn("deleteVehicle() : Vehicle with ID {} not found, cannot delete.", vehicleId);

            // Prepare the failure response if the vehicle doesn't exist
            ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                    .status("FAILED")
                    .message(ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404 Not Found
        } catch (Exception e) {
            log.error("deleteVehicle() : Unexpected error while deleting vehicle with ID: {}", vehicleId, e);

            // Handle any other unexpected errors
            ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred while deleting the vehicle.")
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500 Internal Server Error
        }
    }

}