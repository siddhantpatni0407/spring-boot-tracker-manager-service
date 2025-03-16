-- Create Vehicle Service Table with Foreign Key to Vehicle
CREATE TABLE IF NOT EXISTS vehicle_service (
    servicing_id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    service_date DATE NOT NULL,
    odometer_reading BIGINT NOT NULL,
    service_type VARCHAR(255) NOT NULL,
    service_center VARCHAR(255) NOT NULL,
    service_manager VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    next_service_due DATE NOT NULL,
    service_cost DECIMAL(10,2) NOT NULL,
    remarks TEXT,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT fk_vehicle_service FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE
);