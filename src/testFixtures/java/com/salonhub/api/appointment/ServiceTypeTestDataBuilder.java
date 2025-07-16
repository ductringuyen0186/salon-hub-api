package com.salonhub.api.appointment;

import com.salonhub.api.appointment.dto.ServiceTypeRequestDTO;
import com.salonhub.api.appointment.model.ServiceType;

import java.math.BigDecimal;

/**
 * Builder pattern for creating ServiceType test data
 */
public class ServiceTypeTestDataBuilder {
    
    private Long id;
    private String name;
    private Integer estimatedDurationMinutes;
    private BigDecimal price;
    
    public static ServiceTypeTestDataBuilder aServiceType() {
        return new ServiceTypeTestDataBuilder()
                .withId(1L)
                .withName("Test Service")
                .withEstimatedDurationMinutes(30)
                .withPrice(new BigDecimal("25.00"));
    }
    
    public static ServiceTypeTestDataBuilder aHaircut() {
        return new ServiceTypeTestDataBuilder()
                .withId(ServiceTypeDatabaseDefault.HAIRCUT_ID)
                .withName(ServiceTypeDatabaseDefault.HAIRCUT_NAME)
                .withEstimatedDurationMinutes(ServiceTypeDatabaseDefault.HAIRCUT_DURATION)
                .withPrice(ServiceTypeDatabaseDefault.HAIRCUT_PRICE);
    }
    
    public static ServiceTypeTestDataBuilder aHairColor() {
        return new ServiceTypeTestDataBuilder()
                .withId(ServiceTypeDatabaseDefault.HAIR_COLOR_ID)
                .withName(ServiceTypeDatabaseDefault.HAIR_COLOR_NAME)
                .withEstimatedDurationMinutes(ServiceTypeDatabaseDefault.HAIR_COLOR_DURATION)
                .withPrice(ServiceTypeDatabaseDefault.HAIR_COLOR_PRICE);
    }
    
    public static ServiceTypeTestDataBuilder aManicure() {
        return new ServiceTypeTestDataBuilder()
                .withId(ServiceTypeDatabaseDefault.MANICURE_ID)
                .withName(ServiceTypeDatabaseDefault.MANICURE_NAME)
                .withEstimatedDurationMinutes(ServiceTypeDatabaseDefault.MANICURE_DURATION)
                .withPrice(ServiceTypeDatabaseDefault.MANICURE_PRICE);
    }
    
    public ServiceTypeTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public ServiceTypeTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ServiceTypeTestDataBuilder withEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        return this;
    }
    
    public ServiceTypeTestDataBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    
    public ServiceType build() {
        return new ServiceType(id, name, estimatedDurationMinutes, price);
    }
    
    public ServiceTypeRequestDTO buildRequestDTO() {
        return ServiceTypeRequestDTO.builder()
                .name(name)
                .estimatedDurationMinutes(estimatedDurationMinutes)
                .price(price)
                .build();
    }
}
