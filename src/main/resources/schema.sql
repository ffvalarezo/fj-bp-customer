-- Schema for Customer service using R2DBC with H2
-- This script creates the customers table with all required fields

CREATE TABLE IF NOT EXISTS customers (
    person_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    age INTEGER NOT NULL,
    identification VARCHAR(10) NOT NULL UNIQUE,
    address VARCHAR(200) NOT NULL,
    celular VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Insert sample data
INSERT INTO customers (full_name, gender, age, identification, address, celular, email, customer_id, password, active, status, created_at, updated_at)
VALUES 
('Juan Pérez', 'MALE', 30, '1234567890', 'Av. Amazonas 123', '0987654321', 'juan.perez@email.com', 'CUST001', 'pass1234', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('María García', 'FEMALE', 25, '0987654321', 'Av. 6 de Diciembre 456', '0912345678', 'maria.garcia@email.com', 'CUST002', 'pass5678', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carlos López', 'MALE', 35, '1122334455', 'Av. Pichincha 789', '0998877665', 'carlos.lopez@email.com', 'CUST003', 'pass9012', false, 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
