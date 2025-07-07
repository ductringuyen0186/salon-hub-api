package com.salonhub.api.checkin.service;

import com.salonhub.api.checkin.dto.CheckInRequestDTO;
import com.salonhub.api.checkin.dto.CheckInResponseDTO;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Unified check-in method that handles both guest and existing customer check-ins
     */
    public CheckInResponseDTO checkIn(CheckInRequestDTO request) {
        if (request.isGuest()) {
            return checkInGuest(request);
        } else {
            return checkInExistingCustomer(request);
        }
    }

    /**
     * Check in an existing customer by phone number or email
     */
    public CheckInResponseDTO checkInExistingCustomer(CheckInRequestDTO request) {
        // Try to find by phone number first, then by email if provided
        Optional<Customer> customerOpt = customerRepository.findByPhoneOrEmail(
            request.getPhoneNumber(), request.getEmail() != null ? request.getEmail() : request.getPhoneNumber()
        );

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return new CheckInResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getNote(),
                customer.isGuest(),
                LocalDateTime.now(),
                "Existing customer checked in successfully"
            );
        } else {
            throw new IllegalArgumentException("Customer not found with provided phone number or email");
        }
    }

    /**
     * Check in a guest user (creates a new customer record)
     */
    public CheckInResponseDTO checkInGuest(CheckInRequestDTO request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required for guest check-in");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required for guest check-in");
        }

        // Check if a customer with this phone number already exists
        Optional<Customer> existingCustomer = customerRepository.findByPhoneOrEmail(
            request.getPhoneNumber(), request.getPhoneNumber()
        );

        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException("A customer with this phone number already exists. Use existing customer check-in instead.");
        }

        Customer guest = new Customer();
        guest.setName(request.getName().trim());
        guest.setPhoneNumber(request.getPhoneNumber().trim());
        guest.setEmail(request.getEmail());
        guest.setNote(request.getNote());
        guest.setGuest(true);

        Customer savedGuest = customerRepository.save(guest);

        return new CheckInResponseDTO(
            savedGuest.getId(),
            savedGuest.getName(),
            savedGuest.getPhoneNumber(),
            savedGuest.getEmail(),
            savedGuest.getNote(),
            savedGuest.isGuest(),
            savedGuest.getCreatedAt(),
            "Guest checked in successfully"
        );
    }

    /**
     * Legacy method for backward compatibility
     */
    public Customer checkInExistingCustomer(String phoneOrEmail) {
        return customerRepository.findByPhoneOrEmail(phoneOrEmail, phoneOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    /**
     * Legacy method for backward compatibility
     */
    public Customer checkInGuest(String guestName, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required for guest check-in");
        }
        Customer guest = new Customer();
        guest.setName(guestName);
        guest.setPhoneNumber(phoneNumber);
        guest.setGuest(true);
        return customerRepository.save(guest);
    }

    public List<Customer> getTodayCheckedInGuests() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return customerRepository.findAllByGuestTrueAndCreatedAtBetween(start, end);
    }
}