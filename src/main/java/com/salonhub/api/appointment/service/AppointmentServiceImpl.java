package com.salonhub.api.appointment.service;

import com.salonhub.api.appointment.dto.AppointmentRequestDTO;
import com.salonhub.api.appointment.dto.AppointmentResponseDTO;
import com.salonhub.api.appointment.mapper.AppointmentMapper;
import com.salonhub.api.appointment.model.Appointment;
import com.salonhub.api.appointment.model.BookingStatus;
import com.salonhub.api.appointment.model.ServiceType;
import com.salonhub.api.appointment.repository.AppointmentRepository;
import com.salonhub.api.appointment.repository.ServiceTypeRepository;

import com.salonhub.api.customer.repository.CustomerRepository;
import com.salonhub.api.employee.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repo;
    private final ServiceTypeRepository serviceTypeRepo;
    private final AppointmentMapper mapper;
    private final CustomerRepository customerRepo;
    private final EmployeeRepository employeeRepo;

    public AppointmentServiceImpl(
            AppointmentRepository repo,
            ServiceTypeRepository serviceTypeRepo,
            AppointmentMapper mapper,
            CustomerRepository customerRepo,
            EmployeeRepository employeeRepo) {
        this.repo = repo;
        this.serviceTypeRepo = serviceTypeRepo;
        this.mapper = mapper;
        this.customerRepo = customerRepo;
        this.employeeRepo = employeeRepo;
    }

    @Override
    public AppointmentResponseDTO book(AppointmentRequestDTO req) {
        // Convert DTO → Entity
        Appointment appt = mapper.toEntity(req);
        // Set relations
        appt.setCustomer(customerRepo.findById(req.getCustomerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found")));
        appt.setEmployee(req.getEmployeeId() == null ? null :
            employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found")));
        List<ServiceType> services = serviceTypeRepo.findAllById(req.getServiceIds());
        if (services.size() != req.getServiceIds().size()) {
            throw new EntityNotFoundException("One or more services not found");
        }
        appt.setServices(services);
        appt.setStartTime(req.getStartTime());
        appt.setStatus(BookingStatus.PENDING);

        // Conflict check
        int totalEstimate = services.stream()
            .mapToInt(ServiceType::getEstimatedDurationMinutes)
            .sum();
        if (appt.getEmployee() != null) {
            List<Appointment> conflicts = repo.findByEmployeeIdAndStartTimeBetween(
                appt.getEmployee().getId(), appt.getStartTime(), appt.getStartTime().plusMinutes(totalEstimate)
            );
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Time slot is already booked");
            }
        }

        // Save and map to response
        Appointment saved = repo.save(appt);
        AppointmentResponseDTO response = mapper.toResponse(saved);
        response.setTotalEstimatedDuration(totalEstimate);
        return response;
    }

    @Override
    public List<AppointmentResponseDTO> listByCustomer(Long customerId) {
        return repo.findByCustomerId(customerId).stream()
            .map(this::enrich)
            .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> listByEmployee(Long employeeId) {
        return repo.findByEmployeeIdAndStartTimeBetween(
            employeeId, LocalDateTime.MIN, LocalDateTime.MAX
        ).stream()
         .map(this::enrich)
         .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDTO getById(Long id) {
        Appointment appt = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        return enrich(appt);
    }

    @Override
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO req) {
        Appointment appt = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        // Update fields
        mapper.updateEntity(req, appt);
        if (req.getEmployeeId() != null) {
            appt.setEmployee(employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found")));
        }
        if (req.getServiceIds() != null && !req.getServiceIds().isEmpty()) {
            List<ServiceType> services = serviceTypeRepo.findAllById(req.getServiceIds());
            if (services.size() != req.getServiceIds().size()) {
                throw new EntityNotFoundException("One or more services not found");
            }
            appt.setServices(services);
        }
        Appointment saved = repo.save(appt);
        return enrich(saved);
    }

    @Override
    public AppointmentResponseDTO updateStatus(Long id, String status) {
        Appointment appt = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        appt.setStatus(BookingStatus.valueOf(status));
        if (appt.getStatus() == BookingStatus.COMPLETED) {
            appt.setActualEndTime(LocalDateTime.now());
        }
        Appointment saved = repo.save(appt);
        return enrich(saved);
    }

    @Override
    public void cancel(Long id) {
        updateStatus(id, BookingStatus.CANCELLED.name());
    }

    @Override
    public void complete(Long id) {
        updateStatus(id, BookingStatus.COMPLETED.name());
    }

    private AppointmentResponseDTO enrich(Appointment appt) {
        AppointmentResponseDTO response = mapper.toResponse(appt);
        int totalEstimate = appt.getServices().stream()
            .mapToInt(ServiceType::getEstimatedDurationMinutes)
            .sum();
        response.setTotalEstimatedDuration(totalEstimate);
        return response;
    }
}