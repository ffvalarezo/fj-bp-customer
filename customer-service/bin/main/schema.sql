-- Schema for Customer Microservice
--CREATE TABLE IF NOT EXISTS customer (
--   customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   password VARCHAR(255) NOT NULL,
--   status BOOLEAN NOT NULL DEFAULT TRUE,
--   name VARCHAR(255) NOT NULL,
--   gender VARCHAR(50) NOT NULL,
--   age INTEGER NOT NULL,
--   identification VARCHAR(10) UNIQUE NOT NULL,
--   address VARCHAR(500) NOT NULL,
--   phone VARCHAR(10),
--   email VARCHAR(255),
--   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);

-- Indexes for better performance
--CREATE INDEX IF NOT EXISTS idx_customer_identification ON customer(identification);
--CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);

CREATE TABLE IF NOT EXISTS customer (
  customer_id BIGSERIAL PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  status BOOLEAN NOT NULL DEFAULT TRUE,
  name VARCHAR(255) NOT NULL,
  gender VARCHAR(50) NOT NULL,
  age INTEGER NOT NULL,
  identification VARCHAR(10) UNIQUE NOT NULL,
  address VARCHAR(500) NOT NULL,
  phone VARCHAR(10),
  email VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- este índice extra en identification puedes eliminarlo:
--CREATE INDEX IF NOT EXISTS idx_customer_identification ON customer(identification);

-- este sí puede quedarse si consultas por email:
CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);