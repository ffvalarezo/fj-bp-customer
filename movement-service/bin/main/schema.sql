-- Tabla de movimientos
CREATE TABLE movement (
    movement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movement_type VARCHAR(10) NOT NULL,
    movement_value DECIMAL(12, 2) NOT NULL,
    movement_balance DECIMAL(12, 2) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);