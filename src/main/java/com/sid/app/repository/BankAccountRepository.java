package com.sid.app.repository;

import com.sid.app.entity.BankAccount;
import com.sid.app.model.enums.AccountStatus;
import com.sid.app.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    // Check if account exists by account number
    boolean existsByAccountNumber(String accountNumber);

    // Find account by account number
    Optional<BankAccount> findByAccountNumber(String accountNumber);

    // Find all accounts by user ID
    List<BankAccount> findByUser_UserId(Long userId);

    // Find accounts by account type
    List<BankAccount> findByAccountType(AccountType accountType);

    // Find accounts by account status
    List<BankAccount> findByAccountStatus(AccountStatus accountStatus);

    // Find accounts by bank name
    List<BankAccount> findByBankName(String bankName);

    // Find accounts by IFSC code
    List<BankAccount> findByIfscCode(String ifscCode);

    // Find accounts by account holder name (case insensitive)
    List<BankAccount> findByAccountHolderNameContainingIgnoreCase(String accountHolderName);

    // Custom query to find accounts opened between dates
    @Query("SELECT ba FROM BankAccount ba WHERE ba.openingDate BETWEEN :startDate AND :endDate")
    List<BankAccount> findAccountsOpenedBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Custom query to count accounts by type for a specific user
    @Query("SELECT COUNT(ba) FROM BankAccount ba WHERE ba.user.userId = :userId AND ba.accountType = :accountType")
    long countByUserAndAccountType(
            @Param("userId") Long userId,
            @Param("accountType") AccountType accountType);

    // Find accounts by multiple criteria
    @Query("SELECT ba FROM BankAccount ba WHERE " +
            "(:accountType IS NULL OR ba.accountType = :accountType) AND " +
            "(:accountStatus IS NULL OR ba.accountStatus = :accountStatus) AND " +
            "(:bankName IS NULL OR ba.bankName = :bankName)")
    List<BankAccount> findByCriteria(
            @Param("accountType") AccountType accountType,
            @Param("accountStatus") AccountStatus accountStatus,
            @Param("bankName") String bankName);

}