-- Flyway migration: create vehicles single-table
CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(255) PRIMARY KEY,
    brand VARCHAR(255),
    model VARCHAR(255),
    manufacture_year INT,
    vehicle_type VARCHAR(50),
    -- Car
    num_doors INT,
    fuel_type VARCHAR(100),
    -- Bike
    has_sidecar BOOLEAN,
    bike_type VARCHAR(100),
    -- Truck
    payload_capacity_kg DOUBLE,
    has_trailer BOOLEAN,
    -- Motorcycle
    engine_cc INT,
    category VARCHAR(100)
);
