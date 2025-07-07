-- Create users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('CUSTOMER', 'ADMIN', 'EMPLOYEE') NOT NULL DEFAULT 'CUSTOMER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    preferred_services TEXT,
    last_visit TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_role ON users(role);

-- Insert default admin user (password: admin123)
INSERT INTO users (email, name, password, role) VALUES
('admin@salonhub.com', 'Admin User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5fNjbM6Qy2jdR4YKlMCe', 'ADMIN');

-- Insert default employee user (password: employee123)
INSERT INTO users (email, name, password, role) VALUES
('employee@salonhub.com', 'Employee User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5fNjbM6Qy2jdR4YKlMCe', 'EMPLOYEE');
