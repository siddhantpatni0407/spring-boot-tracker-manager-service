package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.CredentialDTO;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.CredentialService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class CredentialsController {

    private final CredentialService credentialService;

    public CredentialsController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    /**
     * Save a new credential.
     *
     * @param credentialDTO The credential details.
     * @return ResponseEntity containing the saved credential or an error message.
     */
    @PostMapping(value = AppConstants.CREDENTIALS_ENDPOINT)
    public ResponseEntity<ResponseDTO<CredentialDTO>> saveCredential(@RequestBody CredentialDTO credentialDTO) {
        log.info("Saving new credential: {}", credentialDTO);

        try {
            CredentialDTO savedCredential = credentialService.saveCredential(credentialDTO);
            log.info("Credential saved successfully: {}", savedCredential);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(savedCredential, "Credential saved successfully", "SUCCESS"));
        } catch (EntityExistsException e) {
            log.error("Credential already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error saving credential: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    /**
     * Get credentials by user ID.
     *
     * @param userId The user ID.
     * @return ResponseEntity containing the list of credentials or an error message.
     */
    @GetMapping(value = AppConstants.CREDENTIALS_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<CredentialDTO>>> getCredentialsByUser(@RequestParam("userId") Long userId) {
        log.info("Fetching credentials for user ID: {}", userId);

        try {
            List<CredentialDTO> credentials = credentialService.getCredentialsByUser(userId);
            if (credentials.isEmpty()) {
                log.warn("No credentials found for user ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, "No credentials found", "ERROR"));
            }
            log.info("Successfully retrieved {} credentials for user ID: {}", credentials.size(), userId);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(credentials, "Credentials retrieved successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error fetching credentials: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    /**
     * Update a credential.
     *
     * @param credentialDTO The updated credential details.
     * @return ResponseEntity containing the updated credential or an error message.
     */
    @PutMapping(value = AppConstants.CREDENTIALS_ENDPOINT)
    public ResponseEntity<ResponseDTO<CredentialDTO>> updateCredential(@RequestBody CredentialDTO credentialDTO) {
        log.info("Updating credential: {}", credentialDTO);

        try {
            CredentialDTO updatedCredential = credentialService.updateCredential(credentialDTO);
            log.info("Credential updated successfully: {}", updatedCredential);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(updatedCredential, "Credential updated successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Credential not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error updating credential: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    /**
     * Delete a credential by ID.
     *
     * @param credentialId The credential ID.
     * @return ResponseEntity containing a success or error message.
     */
    @DeleteMapping(value = AppConstants.CREDENTIALS_ENDPOINT)
    public ResponseEntity<ResponseDTO<String>> deleteCredential(@RequestParam("credentialId") Long credentialId) {
        log.info("Deleting credential with ID: {}", credentialId);

        try {
            credentialService.deleteCredential(credentialId);
            log.info("Credential deleted successfully with ID: {}", credentialId);
            return ResponseEntity.ok(ApplicationUtils.buildResponse("Credential deleted successfully", "Operation successful", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Credential not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error deleting credential: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

}