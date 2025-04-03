-- Create Table: bank_account
CREATE TABLE IF NOT EXISTS bank_account (
    bank_account_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- Foreign key for user_id
    account_number VARCHAR(255) UNIQUE NOT NULL,
    account_holder_name VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    bank_name VARCHAR(255) NOT NULL,
    branch_name VARCHAR(255),
    ifsc_code VARCHAR(255) NOT NULL,
    branch_location VARCHAR(255),
    opening_date DATE NOT NULL,
    nominee_name VARCHAR(255),
    account_status VARCHAR(50) NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id) -- Foreign Key constraint
);