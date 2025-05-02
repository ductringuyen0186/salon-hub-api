CREATE TABLE service_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    estimated_duration_minutes INT NOT NULL
);