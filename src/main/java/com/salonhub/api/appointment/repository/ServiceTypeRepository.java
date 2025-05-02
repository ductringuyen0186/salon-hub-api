package com.salonhub.api.appointment.repository;

import com.salonhub.api.appointment.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}