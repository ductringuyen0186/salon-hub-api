package com.salonhub.api.appointment.repository;

import com.salonhub.api.appointment.model.ServiceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ServiceTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Test
    void findByNameIgnoreCase_whenServiceTypeExists_shouldReturnServiceType() {
        // Given
        ServiceType serviceType = new ServiceType();
        serviceType.setName("Haircut");
        serviceType.setEstimatedDurationMinutes(60);
        serviceType.setPrice(new BigDecimal("25.00"));
        entityManager.persistAndFlush(serviceType);

        // When
        Optional<ServiceType> result = serviceTypeRepository.findByNameIgnoreCase("HAIRCUT");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Haircut");
        assertThat(result.get().getPrice()).isEqualTo(new BigDecimal("25.00"));
    }

    @Test
    void findByNameIgnoreCase_whenServiceTypeDoesNotExist_shouldReturnEmpty() {
        // When
        Optional<ServiceType> result = serviceTypeRepository.findByNameIgnoreCase("NonExistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_whenServiceTypeExists_shouldReturnTrue() {
        // Given
        ServiceType serviceType = new ServiceType();
        serviceType.setName("Manicure");
        serviceType.setEstimatedDurationMinutes(45);
        serviceType.setPrice(new BigDecimal("30.00"));
        entityManager.persistAndFlush(serviceType);

        // When
        boolean exists = serviceTypeRepository.existsByNameIgnoreCase("MANICURE");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_whenServiceTypeDoesNotExist_shouldReturnFalse() {
        // When
        boolean exists = serviceTypeRepository.existsByNameIgnoreCase("NonExistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldPersistServiceTypeWithPrice() {
        // Given
        ServiceType serviceType = new ServiceType();
        serviceType.setName("Color Treatment");
        serviceType.setEstimatedDurationMinutes(120);
        serviceType.setPrice(new BigDecimal("85.50"));

        // When
        ServiceType saved = serviceTypeRepository.save(serviceType);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Color Treatment");
        assertThat(saved.getEstimatedDurationMinutes()).isEqualTo(120);
        assertThat(saved.getPrice()).isEqualTo(new BigDecimal("85.50"));
    }

    @Test
    void findAll_shouldReturnAllServiceTypes() {
        // Given
        ServiceType serviceType1 = new ServiceType();
        serviceType1.setName("Cut");
        serviceType1.setEstimatedDurationMinutes(30);
        serviceType1.setPrice(new BigDecimal("20.00"));
        
        ServiceType serviceType2 = new ServiceType();
        serviceType2.setName("Style");
        serviceType2.setEstimatedDurationMinutes(45);
        serviceType2.setPrice(new BigDecimal("35.00"));
        
        entityManager.persist(serviceType1);
        entityManager.persist(serviceType2);
        entityManager.flush();

        // When
        var result = serviceTypeRepository.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ServiceType::getName)
            .containsExactlyInAnyOrder("Cut", "Style");
    }
}
