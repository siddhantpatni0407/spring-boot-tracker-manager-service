package com.sid.app.service;

import com.sid.app.entity.Credential;
import com.sid.app.entity.User;
import com.sid.app.model.CredentialDTO;
import com.sid.app.repository.CredentialsRepository;
import com.sid.app.repository.UserRepository;
import com.sid.app.utils.AESUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CredentialService {

    @Autowired
    private CredentialsRepository credentialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AESUtils aesUtils;

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    /**
     * Save a new credential.
     *
     * @param credentialDTO The credential details.
     * @return The saved credential DTO.
     */
    public CredentialDTO saveCredential(CredentialDTO credentialDTO) throws Exception {
        log.info("Saving credential: {}", credentialDTO);

        // Fetch the user entity
        User user = userRepository.findById(credentialDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + credentialDTO.getUserId()));

        // Check if a credential with the same user_id, account_name, username, and email already exists
        Optional<Credential> existingCredential = credentialRepository.findByUser_UserIdAndAccountNameAndUsernameAndEmail(
                credentialDTO.getUserId(),
                credentialDTO.getAccountName(),
                credentialDTO.getUsername(),
                credentialDTO.getEmail()
        );

        if (existingCredential.isPresent()) {
            throw new EntityExistsException("A credential with the same account name, username, and email already exists for this user.");
        }

        // Encrypt the password
        String encryptedPassword = aesUtils.encrypt(credentialDTO.getPassword());
        credentialDTO.setPassword(encryptedPassword);

        // Map DTO to entity
        Credential credential = new Credential();
        credential.setUser(user);
        credential.setAccountName(credentialDTO.getAccountName());
        credential.setAccountType(credentialDTO.getAccountType());
        credential.setWebsite(credentialDTO.getWebsite());
        credential.setUrl(credentialDTO.getUrl());
        credential.setUsername(credentialDTO.getUsername());
        credential.setEmail(credentialDTO.getEmail());
        credential.setMobileNumber(credentialDTO.getMobileNumber());
        credential.setPassword(credentialDTO.getPassword());
        // Set the encryption key version from the EncryptionKeyService
        credential.setPasswordEncryptionKeyVersion(encryptionKeyService.getLatestKey().getKeyVersion());
        credential.setStatus(credentialDTO.getStatus());
        credential.setRemarks(credentialDTO.getRemarks());

        // Save to repository
        Credential savedCredential = credentialRepository.save(credential);
        log.info("Credential saved successfully with ID: {}", savedCredential.getCredentialId());

        // Convert entity to DTO and return
        return convertToDTO(savedCredential);
    }

    /**
     * Get credentials by user ID.
     *
     * @param userId The user ID.
     * @return List of credentials for the user.
     */
    public List<CredentialDTO> getCredentialsByUser(Long userId) {
        log.info("Fetching credentials for user ID: {}", userId);

        // Fetch credentials for the user
        List<Credential> credentials = credentialRepository.findByUser_UserId(userId);
        if (credentials.isEmpty()) {
            throw new EntityNotFoundException("No credentials found for user ID: " + userId);
        }

        // Decrypt passwords before returning
        return credentials.stream()
                .map(this::convertToDTO)
                .peek(dto -> {
                    try {
                        dto.setPassword(aesUtils.decrypt(dto.getPassword(), dto.getPasswordEncryptionKeyVersion()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Update a credential.
     *
     * @param credentialDTO The updated credential details.
     * @return The updated credential DTO.
     */
    public CredentialDTO updateCredential(CredentialDTO credentialDTO) throws Exception {
        log.info("Updating credential with ID: {}", credentialDTO.getCredentialId());

        // Fetch the existing credential
        Credential credential = credentialRepository.findById(credentialDTO.getCredentialId())
                .orElseThrow(() -> new EntityNotFoundException("Credential not found with ID: " + credentialDTO.getCredentialId()));

        // Update fields
        credential.setAccountName(credentialDTO.getAccountName());
        credential.setAccountType(credentialDTO.getAccountType());
        credential.setWebsite(credentialDTO.getWebsite());
        credential.setUrl(credentialDTO.getUrl());
        credential.setUsername(credentialDTO.getUsername());
        credential.setEmail(credentialDTO.getEmail());
        credential.setMobileNumber(credentialDTO.getMobileNumber());
        credential.setPassword(aesUtils.encrypt(credentialDTO.getPassword())); // Encrypt updated password
        credential.setStatus(credentialDTO.getStatus());
        credential.setRemarks(credentialDTO.getRemarks());

        // Save to repository
        Credential updatedCredential = credentialRepository.save(credential);
        log.info("Credential updated successfully with ID: {}", updatedCredential.getCredentialId());

        // Convert entity to DTO and return
        return convertToDTO(updatedCredential);
    }

    /**
     * Delete a credential by ID.
     *
     * @param credentialId The credential ID.
     */
    public void deleteCredential(Long credentialId) {
        log.info("Deleting credential with ID: {}", credentialId);

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new EntityNotFoundException("Credential not found with ID: " + credentialId));

        credentialRepository.delete(credential);
        log.info("Credential deleted successfully with ID: {}", credentialId);
    }

    /**
     * Convert Credential entity to DTO.
     *
     * @param credential The credential entity.
     * @return The credential DTO.
     */
    private CredentialDTO convertToDTO(Credential credential) {
        return CredentialDTO.builder()
                .credentialId(credential.getCredentialId())
                .userId(credential.getUser().getUserId())
                .accountName(credential.getAccountName())
                .accountType(credential.getAccountType())
                .website(credential.getWebsite())
                .url(credential.getUrl())
                .username(credential.getUsername())
                .email(credential.getEmail())
                .mobileNumber(credential.getMobileNumber())
                .password(credential.getPassword()) // Password is already encrypted
                .passwordEncryptionKeyVersion(credential.getPasswordEncryptionKeyVersion())
                .status(credential.getStatus())
                .remarks(credential.getRemarks())
                .build();
    }

}