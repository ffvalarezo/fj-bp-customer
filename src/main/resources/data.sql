-- Initial data for Customer exercise
-- Based on fullstack exercise requirements

-- Insert test customers
INSERT INTO customers (person_id, customer_id, full_name, gender, age, identification, address, celular, email, password, active, created_at, updated_at) VALUES
(1, 1, 'Jose Lema', 'Male', 35, '1234567890', 'Otavalo st and principal', '0982547852', 'jose.lema@example.com', '1234', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Maria Garcia', 'Female', 28, '0987654321', 'Quito st and central', '0987654321', 'maria.garcia@example.com', '5678', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Carlos Rodriguez', 'Male', 42, '1122334455', 'Guayaquil st and main', '0999888777', 'carlos.rodriguez@example.com', 'abcd', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 'Ana Martinez', 'Female', 31, '5566778899', 'Cuenca st and secondary', '0966554433', 'ana.martinez@example.com', 'efgh', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
