package com.sid.app.entity;

import com.sid.app.audit.Auditable;
import com.sid.app.model.enums.CardNetwork;
import com.sid.app.model.enums.CardStatus;
import com.sid.app.model.enums.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_cards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bank_account_id", "card_number"}),
        @UniqueConstraint(columnNames = {"bank_account_id", "card_number_last_four"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BankCard extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_card_id", nullable = false)
    private Long bankCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "card_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "card_network", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardNetwork cardNetwork;

    @Column(name = "card_number", nullable = false)
    private String cardNumber; // Encrypted full card number

    @Column(name = "card_number_last_four", nullable = false, length = 4)
    private String cardNumberLastFour;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Column(name = "valid_from_date", nullable = false)
    private LocalDate validFromDate;

    @Column(name = "valid_thru_date", nullable = false)
    private LocalDate validThruDate;

    @Column(name = "card_pin")
    private String cardPin; // Encrypted CVV

    @Column(name = "cvv")
    private String cvv; // Encrypted CVV

    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "available_credit", precision = 12, scale = 2)
    private BigDecimal availableCredit;

    @Column(name = "billing_cycle_day")
    private Integer billingCycleDay;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "is_contactless", nullable = false)
    private Boolean isContactless;

    @Column(name = "is_virtual", nullable = false)
    private Boolean isVirtual;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "encryption_key_version", nullable = false)
    private Integer encryptionKeyVersion;

}