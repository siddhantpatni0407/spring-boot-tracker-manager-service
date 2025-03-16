-- Create Table: vehicles
CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- Foreign key for user_id
    vehicle_type VARCHAR(255) NOT NULL,
    vehicle_company VARCHAR(255) NOT NULL,
    vehicle_model VARCHAR(255) NOT NULL,
    chassis_number VARCHAR(255) UNIQUE NOT NULL,
    engine_number VARCHAR(255) UNIQUE NOT NULL,
    registration_number VARCHAR(255) UNIQUE NOT NULL,
    registration_date DATE NOT NULL,
    registration_validity_date DATE NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id) -- Foreign Key constraint
);