-- Create Fuel Expense Table with Foreign Key to Vehicle
CREATE TABLE IF NOT EXISTS fuel_expense (
    fuel_expense_id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    fuel_filled_date DATE NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    rate DECIMAL(10,2) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    odometer_reading INT NOT NULL,
    location VARCHAR(255) NOT NULL,
    payment_mode VARCHAR(50) NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT fk_fuel_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE
);