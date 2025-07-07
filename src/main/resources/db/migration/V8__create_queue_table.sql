-- Create queue table for managing salon check-in queue
CREATE TABLE queue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    employee_id BIGINT,
    appointment_id BIGINT,
    queue_number INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    estimated_wait_time INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_queue_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_queue_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL,
    CONSTRAINT fk_queue_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Create index for efficient queue management queries
CREATE INDEX idx_queue_status_created_at ON queue(status, created_at);
CREATE INDEX idx_queue_customer_id ON queue(customer_id);
CREATE INDEX idx_queue_employee_id ON queue(employee_id);
CREATE INDEX idx_queue_appointment_id ON queue(appointment_id);
