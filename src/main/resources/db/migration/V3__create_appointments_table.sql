CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT   NOT NULL,
    employee_id BIGINT   NULL,
    start_time TIMESTAMP NOT NULL,
    actual_end_time TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_appointments_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_employee FOREIGN KEY (employee_id)
        REFERENCES employees(id) ON DELETE SET NULL
);

CREATE INDEX idx_appointments_customer ON appointments(customer_id);
CREATE INDEX idx_appointments_employee ON appointments(employee_id);
CREATE INDEX idx_appointments_start_time ON appointments(start_time);