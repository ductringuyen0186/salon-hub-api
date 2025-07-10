package com.salonhub.api.appointment;

import java.math.BigDecimal;

/**
 * Database default values for ServiceType entities used in tests
 */
public class ServiceTypeDatabaseDefault {
    
    // Entity IDs
    public static final Long HAIRCUT_ID = 1L;
    public static final Long HAIR_COLOR_ID = 2L;
    public static final Long MANICURE_ID = 3L;
    
    // Entity names
    public static final String HAIRCUT_NAME = "Haircut";
    public static final String HAIR_COLOR_NAME = "Hair Color";
    public static final String MANICURE_NAME = "Manicure";
    
    // Durations (in minutes)
    public static final Integer HAIRCUT_DURATION = 30;
    public static final Integer HAIR_COLOR_DURATION = 120;
    public static final Integer MANICURE_DURATION = 45;
    
    // Prices
    public static final BigDecimal HAIRCUT_PRICE = new BigDecimal("25.00");
    public static final BigDecimal HAIR_COLOR_PRICE = new BigDecimal("75.00");
    public static final BigDecimal MANICURE_PRICE = new BigDecimal("35.00");
    
    // SQL insert statements for test data
    public static final String INSERT_HAIRCUT = 
        "INSERT INTO service_types (id, name, estimated_duration_minutes, price) VALUES " +
        "(1, 'Haircut', 30, 25.00)";
        
    public static final String INSERT_HAIR_COLOR = 
        "INSERT INTO service_types (id, name, estimated_duration_minutes, price) VALUES " +
        "(2, 'Hair Color', 120, 75.00)";
    
    public static final String INSERT_MANICURE = 
        "INSERT INTO service_types (id, name, estimated_duration_minutes, price) VALUES " +
        "(3, 'Manicure', 45, 35.00)";
    
    // Array of all inserts for batch operations
    public static final String[] ALL_INSERTS = {
        INSERT_HAIRCUT,
        INSERT_HAIR_COLOR,
        INSERT_MANICURE
    };
}
