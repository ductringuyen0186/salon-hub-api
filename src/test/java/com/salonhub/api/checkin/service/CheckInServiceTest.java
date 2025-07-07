package com.salonhub.api.checkin.service;

import com.salonhub.api.checkin.dto.CheckInRequestDTO;
import com.salonhub.api.checkin.dto.CheckInResponseDTO;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CheckInService checkInService;

    private Customer existingCustomer;
    private CheckInRequestDTO guestRequest;
    private CheckInRequestDTO existingCustomerRequest;

    @BeforeEach
    void setUp() {
        existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("John Doe");
        existingCustomer.setEmail("john@example.com");
        existingCustomer.setPhoneNumber("555-1234");
        existingCustomer.setGuest(false);
        existingCustomer.setCreatedAt(LocalDateTime.now());

        guestRequest = new CheckInRequestDTO();
        guestRequest.setName("Jane Guest");
        guestRequest.setPhoneNumber("555-5678");
        guestRequest.setGuest(true);

        existingCustomerRequest = new CheckInRequestDTO();
        existingCustomerRequest.setName("John Doe");
        existingCustomerRequest.setPhoneNumber("555-1234");
        existingCustomerRequest.setEmail("john@example.com");
        existingCustomerRequest.setGuest(false);
    }

    @Test
    void testCheckInGuest_Success() {
        // Arrange
        when(customerRepository.findByPhoneOrEmail(anyString(), anyString()))
            .thenReturn(Optional.empty());
        
        Customer savedGuest = new Customer();
        savedGuest.setId(2L);
        savedGuest.setName("Jane Guest");
        savedGuest.setPhoneNumber("555-5678");
        savedGuest.setGuest(true);
        savedGuest.setCreatedAt(LocalDateTime.now());
        
        when(customerRepository.save(any(Customer.class))).thenReturn(savedGuest);

        // Act
        CheckInResponseDTO response = checkInService.checkIn(guestRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Jane Guest", response.getName());
        assertEquals("555-5678", response.getPhoneNumber());
        assertTrue(response.isGuest());
        assertEquals("Guest checked in successfully", response.getMessage());
        
        verify(customerRepository).findByPhoneOrEmail("555-5678", "555-5678");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCheckInExistingCustomer_Success() {
        // Arrange
        when(customerRepository.findByPhoneOrEmail(anyString(), anyString()))
            .thenReturn(Optional.of(existingCustomer));

        // Act
        CheckInResponseDTO response = checkInService.checkIn(existingCustomerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("555-1234", response.getPhoneNumber());
        assertEquals("john@example.com", response.getEmail());
        assertFalse(response.isGuest());
        assertEquals("Existing customer checked in successfully", response.getMessage());
        
        verify(customerRepository).findByPhoneOrEmail("555-1234", "john@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCheckInGuest_PhoneNumberAlreadyExists() {
        // Arrange
        when(customerRepository.findByPhoneOrEmail(anyString(), anyString()))
            .thenReturn(Optional.of(existingCustomer));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> checkInService.checkIn(guestRequest)
        );
        
        assertEquals("A customer with this phone number already exists. Use existing customer check-in instead.", 
                    exception.getMessage());
        
        verify(customerRepository).findByPhoneOrEmail("555-5678", "555-5678");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCheckInExistingCustomer_NotFound() {
        // Arrange
        when(customerRepository.findByPhoneOrEmail(anyString(), anyString()))
            .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> checkInService.checkIn(existingCustomerRequest)
        );
        
        assertEquals("Customer not found with provided phone number or email", exception.getMessage());
        
        verify(customerRepository).findByPhoneOrEmail("555-1234", "john@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCheckInGuest_MissingPhoneNumber() {
        // Arrange
        guestRequest.setPhoneNumber(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> checkInService.checkIn(guestRequest)
        );
        
        assertEquals("Phone number is required for guest check-in", exception.getMessage());
        
        verify(customerRepository, never()).findByPhoneOrEmail(anyString(), anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCheckInGuest_MissingName() {
        // Arrange
        guestRequest.setName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> checkInService.checkIn(guestRequest)
        );
        
        assertEquals("Name is required for guest check-in", exception.getMessage());
        
        verify(customerRepository, never()).findByPhoneOrEmail(anyString(), anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
