package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.BankAccountDTO;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.BankAccountService;
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

@RestController
@Slf4j
@CrossOrigin
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @PostMapping(AppConstants.BANK_ACCOUNT_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankAccountDTO>> addBankAccount(@RequestBody BankAccountDTO request) {
        log.info("addBankAccount() : Received request to add bank account: {}", ApplicationUtils.getJSONString(request));

        try {
            BankAccountDTO savedAccount = bankAccountService.addBankAccount(request);
            log.info("addBankAccount() : Bank account added successfully with ID: {}", savedAccount.getAccountId());

            ResponseDTO<BankAccountDTO> response = ResponseDTO.<BankAccountDTO>builder()
                    .status("SUCCESS")
                    .message("Bank account added successfully.")
                    .data(savedAccount)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityExistsException e) {
            log.error("addBankAccount() : Bank account addition failed - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("FAILURE")
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("addBankAccount() : Unexpected error during bank account addition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    @PostMapping(AppConstants.BULK_BANK_ACCOUNT_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<BankAccountDTO>>> addBulkBankAccounts(@RequestBody List<BankAccountDTO> requests) {
        log.info("addBulkBankAccounts() : Received request to add {} bank accounts", requests.size());

        try {
            List<BankAccountDTO> savedAccounts = bankAccountService.addBulkBankAccounts(requests);
            log.info("addBulkBankAccounts() : Successfully added {} bank accounts", savedAccounts.size());

            ResponseDTO<List<BankAccountDTO>> response = ResponseDTO.<List<BankAccountDTO>>builder()
                    .status("SUCCESS")
                    .message("Bank accounts added successfully.")
                    .data(savedAccounts)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("addBulkBankAccounts() : Unexpected error during bulk addition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<List<BankAccountDTO>>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    @GetMapping(AppConstants.BANK_ACCOUNT_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankAccountDTO>> getBankAccount(@RequestParam("accountId") Long accountId) {
        log.info("getBankAccount() : Received request to fetch bank account with ID: {}", accountId);

        try {
            BankAccountDTO account = bankAccountService.getBankAccount(accountId);

            ResponseDTO<BankAccountDTO> response = ResponseDTO.<BankAccountDTO>builder()
                    .status("SUCCESS")
                    .message("Bank account retrieved successfully.")
                    .data(account)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("getBankAccount() : Bank account not found with ID: {}", accountId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("FAILURE")
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("getBankAccount() : Unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    @GetMapping(AppConstants.FETCH_BANK_ACCOUNT_BY_USER_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<BankAccountDTO>>> getBankAccountsByUserId(@RequestParam("userId") Long userId) {
        log.info("getBankAccountsByUserId() : Received request to fetch bank accounts for user with ID: {}", userId);

        try {
            List<BankAccountDTO> accounts = bankAccountService.getBankAccountsByUserId(userId);

            if (accounts.isEmpty()) {
                log.warn("getBankAccountsByUserId() : No bank accounts found for user with ID: {}", userId);

                ResponseDTO<List<BankAccountDTO>> response = ResponseDTO.<List<BankAccountDTO>>builder()
                        .status("FAILURE")
                        .message("No bank accounts found for the user with ID: " + userId)
                        .data(Collections.emptyList())
                        .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404 Not Found
            }

            log.info("getBankAccountsByUserId() : Successfully retrieved {} bank accounts for user with ID: {}", accounts.size(), userId);

            ResponseDTO<List<BankAccountDTO>> response = ResponseDTO.<List<BankAccountDTO>>builder()
                    .status("SUCCESS")
                    .message("Bank accounts retrieved successfully for user with ID: " + userId)
                    .data(accounts)
                    .build();

            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (Exception e) {
            log.error("getBankAccountsByUserId() : Unexpected error occurred while fetching bank accounts for user with ID: {}", userId, e);

            ResponseDTO<List<BankAccountDTO>> response = ResponseDTO.<List<BankAccountDTO>>builder()
                    .status("ERROR")
                    .message("An unexpected error occurred while fetching bank accounts.")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(AppConstants.FETCH_ALL_BANK_ACCOUNTS)
    public ResponseEntity<ResponseDTO<List<BankAccountDTO>>> getAllBankAccounts() {
        log.info("getAllBankAccounts() : Received request to fetch all bank accounts");

        try {
            List<BankAccountDTO> accounts = bankAccountService.getAllBankAccounts();

            if (accounts.isEmpty()) {
                log.warn("getAllBankAccounts() : No bank accounts found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseDTO.<List<BankAccountDTO>>builder()
                                .status("FAILURE")
                                .message("No bank accounts found")
                                .data(Collections.emptyList())
                                .build()
                );
            }

            ResponseDTO<List<BankAccountDTO>> response = ResponseDTO.<List<BankAccountDTO>>builder()
                    .status("SUCCESS")
                    .message("Bank accounts retrieved successfully.")
                    .data(accounts)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("getAllBankAccounts() : Unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<List<BankAccountDTO>>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    @PutMapping(AppConstants.BANK_ACCOUNT_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankAccountDTO>> updateBankAccount(@RequestBody BankAccountDTO request) {
        log.info("updateBankAccount() : Received request to update bank account: {}", ApplicationUtils.getJSONString(request));

        try {
            BankAccountDTO updatedAccount = bankAccountService.updateBankAccount(request);

            ResponseDTO<BankAccountDTO> response = ResponseDTO.<BankAccountDTO>builder()
                    .status("SUCCESS")
                    .message("Bank account updated successfully.")
                    .data(updatedAccount)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("updateBankAccount() : Bank account update failed - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("FAILURE")
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("updateBankAccount() : Unexpected error during update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<BankAccountDTO>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    @DeleteMapping(AppConstants.BANK_ACCOUNT_ENDPOINT)
    public ResponseEntity<ResponseDTO<Void>> deleteBankAccount(@RequestParam("accountId") Long accountId) {
        log.info("deleteBankAccount() : Received request to delete bank account with ID: {}", accountId);

        try {
            bankAccountService.deleteBankAccount(accountId);

            ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                    .status("SUCCESS")
                    .message("Bank account deleted successfully.")
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("deleteBankAccount() : Bank account not found with ID: {}", accountId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.<Void>builder()
                            .status("FAILURE")
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("deleteBankAccount() : Unexpected error during deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.<Void>builder()
                            .status("ERROR")
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

}