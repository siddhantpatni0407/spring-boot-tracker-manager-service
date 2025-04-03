package com.sid.app.service;

import com.sid.app.entity.BankAccount;
import com.sid.app.entity.BankCard;
import com.sid.app.model.BankCardDTO;
import com.sid.app.model.enums.CardNetwork;
import com.sid.app.model.enums.CardStatus;
import com.sid.app.repository.BankAccountRepository;
import com.sid.app.repository.BankCardRepository;
import com.sid.app.utils.AESUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankCardService {

    private final BankAccountRepository bankAccountRepository;
    private final BankCardRepository bankCardRepository;
    private final AESUtils aesUtils;
    private final EncryptionKeyService encryptionKeyService;

    @Transactional
    public BankCardDTO addCard(BankCardDTO bankCardDTO) throws Exception {
        log.info("Adding new bank card for account ID: {}", bankCardDTO.getBankAccountId());

        // 1. Validate card details first
        validateCardDetails(bankCardDTO);

        // 2. Check if bank account exists
        BankAccount bankAccount = bankAccountRepository.findById(bankCardDTO.getBankAccountId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bank account not found with ID: " + bankCardDTO.getBankAccountId()));

        // 3. Check if card with same last four digits already exists for this account
        if (bankCardRepository.existsByCardNumberLastFourAndBankAccount_BankAccountId(
                getLastFourDigits(bankCardDTO.getCardNumber()),
                bankCardDTO.getBankAccountId())) {
            throw new EntityExistsException("Card with same last four digits already exists for this account");
        }

        // 4. Create new card
        BankCard bankCard = new BankCard();
        bankCard.setBankAccount(bankAccount); // Set the actual bank account entity

        // Set card details
        bankCard.setCardType(bankCardDTO.getCardType());
        bankCard.setCardNetwork(CardNetwork.valueOf(bankCardDTO.getCardNetwork()));

        // Encrypt sensitive data
        String encryptedCardNumber = aesUtils.encrypt(bankCardDTO.getCardNumber());
        String encryptedCvv = aesUtils.encrypt(bankCardDTO.getCvv());
        String encryptedPin = aesUtils.encrypt(bankCardDTO.getCardPin());

        bankCard.setCardNumber(encryptedCardNumber);
        bankCard.setCardNumberLastFour(getLastFourDigits(bankCardDTO.getCardNumber()));
        bankCard.setCardHolderName(bankCardDTO.getCardHolderName());
        bankCard.setValidFromDate(bankCardDTO.getValidFromDate());
        bankCard.setValidThruDate(bankCardDTO.getValidThruDate());
        bankCard.setCardPin(encryptedPin);
        bankCard.setCvv(encryptedCvv);
        bankCard.setCreditLimit(bankCardDTO.getCreditLimit());
        bankCard.setAvailableCredit(bankCardDTO.getAvailableCredit());
        bankCard.setBillingCycleDay(bankCardDTO.getBillingCycleDay());
        bankCard.setStatus(CardStatus.valueOf(bankCardDTO.getCardStatus().name()));
        bankCard.setIsContactless(bankCardDTO.getIsContactless());
        bankCard.setIsVirtual(bankCardDTO.getIsVirtual());
        bankCard.setRemarks(bankCardDTO.getRemarks());
        bankCard.setEncryptionKeyVersion(encryptionKeyService.getLatestKey().getKeyVersion());

        // 5. Save the card
        BankCard savedCard = bankCardRepository.save(bankCard);
        log.info("Bank card saved successfully with ID: {}", savedCard.getBankCardId());

        return convertToDTO(savedCard);
    }

    @Transactional
    public List<BankCardDTO> addBulkCards(List<BankCardDTO> bankCards) throws Exception {
        log.info("Adding {} bank cards in bulk", bankCards.size());

        if (bankCards.isEmpty()) {
            throw new IllegalArgumentException("Bank cards list cannot be null or empty");
        }

        // Pre-validate all cards before processing
        bankCards.forEach(this::validateCardDetails);

        // Group cards by account ID for batch processing
        Map<Long, List<BankCardDTO>> cardsByAccount = bankCards.stream()
                .collect(Collectors.groupingBy(BankCardDTO::getBankAccountId));

        // Verify all accounts exist in a single query
        Set<Long> accountIds = cardsByAccount.keySet();
        List<BankAccount> existingAccounts = bankAccountRepository.findAllById(accountIds);

        if (existingAccounts.size() != accountIds.size()) {
            Set<Long> existingAccountIds = existingAccounts.stream()
                    .map(BankAccount::getBankAccountId)
                    .collect(Collectors.toSet());

            List<Long> missingAccountIds = accountIds.stream()
                    .filter(id -> !existingAccountIds.contains(id))
                    .toList();

            throw new EntityNotFoundException(
                    "One or more bank accounts not found with IDs: " + missingAccountIds);
        }

        // Check for duplicate card numbers within the batch
        Map<Long, Set<String>> accountCardNumbers = new HashMap<>();
        for (BankCardDTO card : bankCards) {
            String lastFour = getLastFourDigits(card.getCardNumber());
            accountCardNumbers.computeIfAbsent(card.getBankAccountId(), k -> new HashSet<>())
                    .add(lastFour);
        }

        // Check for existing cards in database (batch query)
        List<Object[]> existingCards = bankCardRepository.findExistingCardNumbers(
                accountCardNumbers.entrySet().stream()
                        .flatMap(entry -> entry.getValue().stream()
                                .map(lastFour -> new Object[]{entry.getKey(), lastFour}))
                        .collect(Collectors.toList()));

        if (!existingCards.isEmpty()) {
            String duplicates = existingCards.stream()
                    .map(arr -> String.format("(Account: %s, Last4: %s)", arr[0], arr[1]))
                    .collect(Collectors.joining(", "));
            throw new EntityExistsException(
                    "Duplicate card numbers detected for: " + duplicates);
        }

        // Prepare all entities for batch insert
        List<BankCard> cardsToSave = bankCards.stream().map(card -> {
            BankCard bankCard = new BankCard();
            BankAccount account = existingAccounts.stream()
                    .filter(a -> a.getBankAccountId().equals(card.getBankAccountId()))
                    .findFirst()
                    .orElseThrow(); // Should never happen due to previous check

            bankCard.setBankAccount(account);
            bankCard.setCardType(card.getCardType());
            bankCard.setCardNetwork(CardNetwork.valueOf(card.getCardNetwork()));

            try {
                bankCard.setCardNumber(aesUtils.encrypt(card.getCardNumber()));
                bankCard.setCardPin(aesUtils.encrypt(card.getCardPin()));
                bankCard.setCvv(aesUtils.encrypt(card.getCvv()));
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt card data", e);
            }

            bankCard.setCardNumberLastFour(getLastFourDigits(card.getCardNumber()));
            bankCard.setCardHolderName(card.getCardHolderName());
            bankCard.setValidFromDate(card.getValidFromDate());
            bankCard.setValidThruDate(card.getValidThruDate());
            bankCard.setCreditLimit(card.getCreditLimit());
            bankCard.setAvailableCredit(card.getAvailableCredit());
            bankCard.setBillingCycleDay(card.getBillingCycleDay());
            bankCard.setStatus(CardStatus.valueOf(card.getCardStatus().name()));
            bankCard.setIsContactless(card.getIsContactless());
            bankCard.setIsVirtual(card.getIsVirtual());
            bankCard.setRemarks(card.getRemarks());
            bankCard.setEncryptionKeyVersion(encryptionKeyService.getLatestKey().getKeyVersion());

            return bankCard;
        }).collect(Collectors.toList());

        // Batch save all cards
        List<BankCard> savedCards = bankCardRepository.saveAll(cardsToSave);
        log.info("Successfully saved {} bank cards in bulk", savedCards.size());

        return savedCards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BankCardDTO> getAllCards() {
        return bankCardRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BankCardDTO> getCardsByBankAccountId(Long bankAccountId) {
        try {
            log.debug("Fetching cards for account ID: {}", bankAccountId);
            List<BankCard> bankCards = bankCardRepository.findByBankAccount_BankAccountId(bankAccountId);

            if (bankCards.isEmpty()) {
                throw new EntityNotFoundException("No cards found for bank account ID: " + bankAccountId);
            }

            return bankCards.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("Database error while fetching cards for account ID {}: {}", bankAccountId, e.getMessage());
            throw new DataRetrievalFailureException("Failed to retrieve cards due to database error", e);
        }
    }

    public BankCardDTO getCardById(Long cardId) {
        BankCard bankCard = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Bank card not found with ID: " + cardId));
        return convertToDTO(bankCard);
    }

    @Transactional
    public BankCardDTO updateCard(Long cardId, BankCardDTO updatedCardDTO) throws Exception {
        BankCard bankCard = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Bank card not found with ID: " + cardId));

        validateCardDetails(updatedCardDTO);

        // Update basic information
        bankCard.setCardType(updatedCardDTO.getCardType());
        bankCard.setCardNetwork(CardNetwork.valueOf(updatedCardDTO.getCardNetwork()));
        bankCard.setCardHolderName(updatedCardDTO.getCardHolderName());
        bankCard.setValidFromDate(updatedCardDTO.getValidFromDate());
        bankCard.setValidThruDate(updatedCardDTO.getValidThruDate());
        bankCard.setCreditLimit(updatedCardDTO.getCreditLimit());
        bankCard.setAvailableCredit(updatedCardDTO.getAvailableCredit());
        bankCard.setBillingCycleDay(updatedCardDTO.getBillingCycleDay());
        bankCard.setStatus(CardStatus.valueOf(updatedCardDTO.getCardStatus().name()));
        bankCard.setIsContactless(updatedCardDTO.getIsContactless());
        bankCard.setIsVirtual(updatedCardDTO.getIsVirtual());
        bankCard.setRemarks(updatedCardDTO.getRemarks());

        // Get current encryption key version
        Integer currentKeyVersion = bankCard.getEncryptionKeyVersion();

        // Update encrypted fields if changed
        if (!aesUtils.decrypt(bankCard.getCardNumber(), currentKeyVersion).equals(updatedCardDTO.getCardNumber())) {
            bankCard.setCardNumber(aesUtils.encrypt(updatedCardDTO.getCardNumber()));
            bankCard.setCardNumberLastFour(getLastFourDigits(updatedCardDTO.getCardNumber()));
        }

        if (!aesUtils.decrypt(bankCard.getCvv(), currentKeyVersion).equals(updatedCardDTO.getCvv())) {
            bankCard.setCvv(aesUtils.encrypt(updatedCardDTO.getCvv()));
        }

        if (!aesUtils.decrypt(bankCard.getCardPin(), currentKeyVersion).equals(updatedCardDTO.getCardPin())) {
            bankCard.setCardPin(aesUtils.encrypt(updatedCardDTO.getCardPin()));
        }

        // Update encryption key version if changed
        Integer latestKeyVersion = encryptionKeyService.getLatestKey().getKeyVersion();
        if (!latestKeyVersion.equals(currentKeyVersion)) {
            bankCard.setEncryptionKeyVersion(latestKeyVersion);
        }

        BankCard updatedCard = bankCardRepository.save(bankCard);
        return convertToDTO(updatedCard);
    }

    @Transactional
    public BankCardDTO updateCardStatus(Long cardId, String status) {
        BankCard bankCard = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Bank card not found with ID: " + cardId));

        bankCard.setStatus(CardStatus.valueOf(status.toUpperCase()));
        BankCard updatedCard = bankCardRepository.save(bankCard);
        return convertToDTO(updatedCard);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        if (!bankCardRepository.existsById(cardId)) {
            throw new EntityNotFoundException("Bank card not found with ID: " + cardId);
        }
        bankCardRepository.deleteById(cardId);
        log.info("Deleted bank card with ID: {}", cardId);
    }

    private BankCardDTO convertToDTO(BankCard bankCard) {
        try {
            Integer encryptionKeyVersion = bankCard.getEncryptionKeyVersion();
            return BankCardDTO.builder()
                    .cardId(bankCard.getBankCardId())
                    .bankAccountId(bankCard.getBankAccount().getBankAccountId())  // Fixed method name
                    .cardType(bankCard.getCardType())
                    .cardNetwork(bankCard.getCardNetwork().name())
                    .cardNumber(aesUtils.decrypt(bankCard.getCardNumber(), encryptionKeyVersion))
                    .lastFourDigits(bankCard.getCardNumberLastFour())
                    .cardHolderName(bankCard.getCardHolderName())
                    .validFromDate(bankCard.getValidFromDate())
                    .validThruDate(bankCard.getValidThruDate())
                    .cardPin(aesUtils.decrypt(bankCard.getCardPin(), encryptionKeyVersion))
                    .cvv(aesUtils.decrypt(bankCard.getCvv(), encryptionKeyVersion))
                    .creditLimit(bankCard.getCreditLimit())
                    .availableCredit(bankCard.getAvailableCredit())
                    .billingCycleDay(bankCard.getBillingCycleDay())
                    .cardStatus(CardStatus.valueOf(bankCard.getStatus().name()))  // Convert enum to string
                    .isContactless(bankCard.getIsContactless())
                    .isVirtual(bankCard.getIsVirtual())
                    .remarks(bankCard.getRemarks())
                    .encryptionKeyVersion(String.valueOf(encryptionKeyVersion))
                    .build();
        } catch (Exception e) {
            log.error("Error decrypting card data for card ID: {}", bankCard.getBankCardId(), e);
            throw new RuntimeException("Error processing card data", e);
        }
    }

    private void validateCardDetails(BankCardDTO bankCardDTO) {
        // Basic validation logic
        if (bankCardDTO.getCardNumber() == null || bankCardDTO.getCardNumber().length() < 15) {
            throw new IllegalArgumentException("Invalid card number");
        }
        if (bankCardDTO.getCvv() == null || bankCardDTO.getCvv().length() < 3) {
            throw new IllegalArgumentException("Invalid CVV");
        }
        if (bankCardDTO.getValidThruDate().isBefore(bankCardDTO.getValidFromDate())) {
            throw new IllegalArgumentException("Valid thru date must be after valid from date");
        }
    }

    private String getLastFourDigits(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }

}