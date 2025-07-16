package com.salonhub.api.appointment.mapper;

import com.salonhub.api.appointment.dto.ServiceTypeRequestDTO;
import com.salonhub.api.appointment.dto.ServiceTypeResponseDTO;
import com.salonhub.api.appointment.model.ServiceType;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeMapper {
    
    /**
     * Convert ServiceType entity to response DTO
     */
    public ServiceTypeResponseDTO toResponse(ServiceType serviceType) {
        if (serviceType == null) {
            return null;
        }
        
        return ServiceTypeResponseDTO.builder()
                .id(serviceType.getId())
                .name(serviceType.getName())
                .estimatedDurationMinutes(serviceType.getEstimatedDurationMinutes())
                .price(serviceType.getPrice())
                .build();
    }
    
    /**
     * Convert request DTO to ServiceType entity (for creation)
     */
    public ServiceType toEntity(ServiceTypeRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        return new ServiceType(
                null, // ID will be generated
                requestDTO.getName().trim(),
                requestDTO.getEstimatedDurationMinutes(),
                requestDTO.getPrice()
        );
    }
    
    /**
     * Update existing ServiceType entity with request DTO data
     */
    public void updateEntity(ServiceType existingServiceType, ServiceTypeRequestDTO requestDTO) {
        if (existingServiceType == null || requestDTO == null) {
            return;
        }
        
        existingServiceType.setName(requestDTO.getName().trim());
        existingServiceType.setEstimatedDurationMinutes(requestDTO.getEstimatedDurationMinutes());
        existingServiceType.setPrice(requestDTO.getPrice());
    }
}
