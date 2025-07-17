package com.salonhub.api.config;

import com.salonhub.api.auth.model.User;
import com.salonhub.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer for development and testing environments.
 * Creates default users for Swagger authentication testing.
 * 
 * Only runs in 'test' and 'h2' profiles for safety.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"test", "h2", "dev"}) // Only run in development/test environments
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("🚀 Initializing test users for Swagger authentication...");
            
            createTestUser("admin@salonhub.com", "admin123", "Admin User", User.Role.ADMIN);
            createTestUser("manager@salonhub.com", "manager123", "Manager User", User.Role.EMPLOYEE);
            createTestUser("frontdesk@salonhub.com", "frontdesk123", "Front Desk User", User.Role.EMPLOYEE);
            createTestUser("customer@salonhub.com", "customer123", "Test Customer", User.Role.CUSTOMER);
            
            log.info("✅ Test users created successfully!");
            log.info("🔑 Login credentials for Swagger testing:");
            log.info("   Admin: admin@salonhub.com / admin123");
            log.info("   Manager: manager@salonhub.com / manager123");
            log.info("   Front Desk: frontdesk@salonhub.com / frontdesk123");
            log.info("   Customer: customer@salonhub.com / customer123");
        } else {
            log.info("📝 Test users already exist, skipping initialization");
        }
    }

    private void createTestUser(String email, String password, String name, User.Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(role)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
            
            userRepository.save(user);
            log.debug("Created test user: {} with role: {}", email, role);
        }
    }
}
