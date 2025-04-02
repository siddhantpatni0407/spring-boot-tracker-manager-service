package com.sid.app.service;

import com.sid.app.entity.BankAccount;
import com.sid.app.entity.User;
import com.sid.app.model.BankAccountDTO;
import com.sid.app.repository.BankAccountRepository;
import com.sid.app.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BankAccountDTO addBankAccount(BankAccountDTO bankAccountDTO) {
        log.info("Adding new bank account with account number: {}", bankAccountDTO.getAccountNumber());

        // Check if account already exists
        if (bankAccountRepository.existsByAccountNumber(bankAccountDTO.getAccountNumber())) {
            throw new EntityExistsException("Bank account already exists with number: " + bankAccountDTO.getAccountNumber());
        }

        User user = userRepository.findById(bankAccountDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + bankAccountDTO.getUserId()));

        BankAccount account = mapToEntity(bankAccountDTO);
        account.setUser(user);

        BankAccount savedAccount = bankAccountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    @Transactional
    public List<BankAccountDTO> addBulkBankAccounts(List<BankAccountDTO> bankAccountDTOs) {
        return bankAccountDTOs.stream()
                .map(dto -> {
                    try {
                        return addBankAccount(dto);
                    } catch (EntityExistsException e) {
                        log.warn("Skipping duplicate account: {}", dto.getAccountNumber());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BankAccountDTO getBankAccount(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + accountId));
    }

    @Transactional(readOnly = true)
    public List<BankAccountDTO> getBankAccountsByUserId(Long userId) {
        log.info("getBankAccountsByUserId() : Fetching bank accounts for user with ID: {}", userId);

        // Fetch bank accounts associated with the userId
        List<BankAccount> accounts = bankAccountRepository.findByUser_UserId(userId);

        if (accounts.isEmpty()) {
            log.warn("getBankAccountsByUserId() : No bank accounts found for user with ID: {}", userId);
            return Collections.emptyList(); // Return an empty list if no accounts found
        }

        log.info("getBankAccountsByUserId() : Total bank accounts found for user with ID {}: {}", userId, accounts.size());

        // Map entities to DTOs and return
        return accounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BankAccountDTO> getAllBankAccounts() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        return accounts.isEmpty() ? Collections.emptyList() :
                accounts.stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
    }

    @Transactional
    public BankAccountDTO updateBankAccount(BankAccountDTO bankAccountDTO) {
        BankAccount account = bankAccountRepository.findById(bankAccountDTO.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + bankAccountDTO.getAccountId()));

        // Update fields
        account.setAccountNumber(bankAccountDTO.getAccountNumber());
        account.setAccountHolderName(bankAccountDTO.getAccountHolderName());
        account.setAccountType(bankAccountDTO.getAccountType());
        account.setBankName(bankAccountDTO.getBankName());
        account.setBranchName(bankAccountDTO.getBranchName());
        account.setIfscCode(bankAccountDTO.getIfscCode());
        account.setBranchLocation(bankAccountDTO.getBranchLocation());
        account.setOpeningDate(bankAccountDTO.getOpeningDate());
        account.setNomineeName(bankAccountDTO.getNomineeName());
        account.setAccountStatus(bankAccountDTO.getAccountStatus());

        BankAccount updatedAccount = bankAccountRepository.save(account);
        return mapToDTO(updatedAccount);
    }

    @Transactional
    public void deleteBankAccount(Long accountId) {
        if (!bankAccountRepository.existsById(accountId)) {
            throw new EntityNotFoundException("Bank account not found with ID: " + accountId);
        }
        bankAccountRepository.deleteById(accountId);
    }

    private BankAccount mapToEntity(BankAccountDTO dto) {
        return BankAccount.builder()
                .accountId(dto.getAccountId())
                .accountNumber(dto.getAccountNumber())
                .accountHolderName(dto.getAccountHolderName())
                .accountType(dto.getAccountType())
                .bankName(dto.getBankName())
                .branchName(dto.getBranchName())
                .ifscCode(dto.getIfscCode())
                .branchLocation(dto.getBranchLocation())
                .openingDate(dto.getOpeningDate())
                .nomineeName(dto.getNomineeName())
                .accountStatus(dto.getAccountStatus())
                .build();
    }

    private BankAccountDTO mapToDTO(BankAccount entity) {
        return BankAccountDTO.builder()
                .accountId(entity.getAccountId())
                .userId(entity.getUser().getUserId())
                .accountNumber(entity.getAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .accountType(entity.getAccountType())
                .bankName(entity.getBankName())
                .branchName(entity.getBranchName())
                .ifscCode(entity.getIfscCode())
                .branchLocation(entity.getBranchLocation())
                .openingDate(entity.getOpeningDate())
                .nomineeName(entity.getNomineeName())
                .accountStatus(entity.getAccountStatus())
                .build();
    }

}