package com.sid.app.repository;

import com.sid.app.entity.BankCard;
import com.sid.app.model.enums.CardNetwork;
import com.sid.app.model.enums.CardStatus;
import com.sid.app.model.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    // Find cards by bank account ID (using property path)
    List<BankCard> findByBankAccount_BankAccountId(Long bankAccountId);

    // Find card by ID and account ID (for security validation)
    Optional<BankCard> findByBankCardIdAndBankAccount_BankAccountId(Long cardId, Long bankAccountId);

    // Check if card with last four exists for account
    boolean existsByCardNumberLastFourAndBankAccount_BankAccountId(String lastFourDigits, Long bankAccountId);

    // Find cards by status
    List<BankCard> findByStatus(CardStatus status);

    // Find cards by type
    List<BankCard> findByCardType(CardType cardType);

    // Find cards by network
    List<BankCard> findByCardNetwork(CardNetwork cardNetwork);

    // Find virtual/physical cards
    List<BankCard> findByIsVirtual(Boolean isVirtual);

    // Find expiring cards (before specified date)
    List<BankCard> findByValidThruDateBefore(LocalDate date);

    // Custom query for finding existing card numbers
    @Query("SELECT c.bankAccount.bankAccountId, c.cardNumberLastFour FROM BankCard c " +
            "WHERE (c.bankAccount.bankAccountId, c.cardNumberLastFour) IN :accountAndNumbers")
    List<Object[]> findExistingCardNumbers(@Param("accountAndNumbers") List<Object[]> accountAndNumbers);

    // Custom query for finding cards by multiple criteria
    @Query("SELECT c FROM BankCard c WHERE " +
            "(:bankAccountId IS NULL OR c.bankAccount.bankAccountId = :bankAccountId) AND " +
            "(:cardType IS NULL OR c.cardType = :cardType) AND " +
            "(:status IS NULL OR c.status = :status)")
    List<BankCard> findCardsByCriteria(
            @Param("bankAccountId") Long bankAccountId,
            @Param("cardType") CardType cardType,
            @Param("status") CardStatus status);
}