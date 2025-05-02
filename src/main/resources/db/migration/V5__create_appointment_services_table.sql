CREATE TABLE appointment_services (
    appointment_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    PRIMARY KEY (appointment_id, service_id),
    CONSTRAINT fk_appt_serv_appointment FOREIGN KEY (appointment_id)
        REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_serv_service FOREIGN KEY (service_id)
        REFERENCES service_types(id) ON DELETE RESTRICT
);

CREATE INDEX idx_appt_serv_appointment ON appointment_services(appointment_id);
CREATE INDEX idx_appt_serv_service ON appointment_services(service_id);
