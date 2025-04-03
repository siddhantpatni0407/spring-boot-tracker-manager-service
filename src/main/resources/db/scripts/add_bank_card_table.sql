CREATE TABLE IF NOT EXISTS bank_cards (
    bank_card_id BIGSERIAL PRIMARY KEY,
    bank_account_id BIGINT NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    card_network VARCHAR(20) NOT NULL,
    card_number VARCHAR(50) NOT NULL, -- Encrypted full card number
    card_number_last_four VARCHAR(4) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    valid_from_date DATE NOT NULL,
    valid_thru_date DATE NOT NULL,
    card_pin VARCHAR(30), -- Encrypted PIN
    cvv VARCHAR(30), -- Encrypted CVV
    credit_limit NUMERIC(12,2),
    available_credit NUMERIC(12,2),
    billing_cycle_day INT,
    status VARCHAR(20) NOT NULL,
    is_contactless BOOLEAN NOT NULL,
    is_virtual BOOLEAN NOT NULL,
    remarks VARCHAR(500),
    encryption_key_version INT NOT NULL, -- Added encryption key version column
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Remove ON UPDATE
    CONSTRAINT fk_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_account(bank_account_id) ON DELETE CASCADE,
    CONSTRAINT unique_bank_account_card_number UNIQUE (bank_account_id, card_number),
    CONSTRAINT unique_bank_account_card_last_four UNIQUE (bank_account_id, card_number_last_four)
);