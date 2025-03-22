-- Create encryption_keys table
CREATE TABLE encryption_keys (
    encryption_keys_id BIGSERIAL PRIMARY KEY,
    key_version INT UNIQUE NOT NULL,
    secret_key VARCHAR(255) UNIQUE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);