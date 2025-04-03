package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.BankCardDTO;
import com.sid.app.model.ResponseDTO;
import com.sid.app.service.BankCardService;
import com.sid.app.utils.ApplicationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class BankCardController {

    private final BankCardService bankCardService;

    public BankCardController(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    @PostMapping(AppConstants.BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankCardDTO>> addCard(@Valid @RequestBody BankCardDTO bankCardDTO) {
        log.info("Adding new bank card for account: {}", bankCardDTO.getBankAccountId());
        try {
            BankCardDTO savedCard = bankCardService.addCard(bankCardDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApplicationUtils.buildResponse(savedCard, "Card added successfully", "SUCCESS"));
        } catch (Exception e) {
            log.error("Error adding card: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @PostMapping(AppConstants.BULK_BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<BankCardDTO>>> addBulkCards(@Valid @RequestBody List<BankCardDTO> bankCards) {
        log.info("Adding {} bank cards in bulk", bankCards.size());
        try {
            List<BankCardDTO> savedCards = bankCardService.addBulkCards(bankCards);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApplicationUtils.buildResponse(savedCards, "Cards added successfully", "SUCCESS"));
        } catch (Exception e) {
            log.error("Error adding bulk cards: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @GetMapping(AppConstants.BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<BankCardDTO>>> getAllCards() {
        log.info("Fetching all bank cards");
        List<BankCardDTO> cards = bankCardService.getAllCards();
        return ResponseEntity.ok(ApplicationUtils.buildResponse(cards, "Cards retrieved successfully", "SUCCESS"));
    }

    @GetMapping(AppConstants.BANK_CARD_BY_BANK_ID_ENDPOINT)
    public ResponseEntity<ResponseDTO<List<BankCardDTO>>> getCardsByAccountId(@RequestParam(name = "accountId", required = true) Long accountId) {
        log.info("Fetching cards for account ID: {}", accountId);
        try {
            List<BankCardDTO> cards = bankCardService.getCardsByBankAccountId(accountId);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(cards, "Cards retrieved successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Cards not found for account ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error fetching cards for account ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, "Failed to retrieve cards", "ERROR"));
        }
    }

    @GetMapping(AppConstants.BANK_CARD_BY_BANK_CARD_ID_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankCardDTO>> getCardById(@RequestParam(name = "cardId", required = true) Long cardId) {
        log.info("Fetching card with ID: {}", cardId);
        try {
            BankCardDTO card = bankCardService.getCardById(cardId);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(card, "Card retrieved successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Card not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @PutMapping(AppConstants.BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankCardDTO>> updateCard(@Valid @RequestBody BankCardDTO bankCardDTO) {
        log.info("Updating bank card with ID: {}", bankCardDTO.getCardId());
        try {
            BankCardDTO updatedCard = bankCardService.updateCard(bankCardDTO.getCardId(), bankCardDTO);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(updatedCard, "Card updated successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Card not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error updating card: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @PatchMapping(AppConstants.BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<BankCardDTO>> updateCardStatus(@RequestParam(name = "cardId", required = true) Long cardId,
                                                                     @RequestParam(name = "status", required = true) String status) {
        log.info("Updating status for card ID: {} to {}", cardId, status);
        try {
            BankCardDTO updatedCard = bankCardService.updateCardStatus(cardId, status);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(updatedCard, "Card status updated successfully", "SUCCESS"));
        } catch (EntityNotFoundException e) {
            log.error("Card not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error updating card status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

    @DeleteMapping(AppConstants.BANK_CARD_ENDPOINT)
    public ResponseEntity<ResponseDTO<String>> deleteCard(@RequestParam(name = "cardId", required = true) Long cardId) {
        log.info("Deleting bank card with ID: {}", cardId);
        try {
            bankCardService.deleteCard(cardId);
            return ResponseEntity.ok(ApplicationUtils.buildResponse(
                    "Card deleted successfully",
                    "Operation successful",
                    "SUCCESS"
            ));
        } catch (EntityNotFoundException e) {
            log.error("Card not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        } catch (Exception e) {
            log.error("Error deleting card: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationUtils.buildResponse(null, e.getMessage(), "ERROR"));
        }
    }

}