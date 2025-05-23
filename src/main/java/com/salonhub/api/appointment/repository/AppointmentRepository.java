package com.salonhub.api.appointment.repository;

import com.salonhub.api.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerId(Long customerId);
    List<Appointment> findByEmployeeIdAndStartTimeBetween(Long employeeId, LocalDateTime start, LocalDateTime end);
}