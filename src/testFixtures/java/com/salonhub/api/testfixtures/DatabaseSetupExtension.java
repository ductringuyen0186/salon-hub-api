package com.salonhub.api.testfixtures;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;

/**
 * Sets up a real MySQL Testcontainer for integration tests.
 */
public class DatabaseSetupExtension implements BeforeAllCallback, AfterAllCallback {

    public static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.32")
            .withDatabaseName("salon")
            .withUsername("test")
            .withPassword("test");

    @Override
    public void beforeAll(ExtensionContext context) {
        // Start MySQL container
        MYSQL_CONTAINER.start();

        // Inject dynamic datasource properties
        System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
        System.setProperty("spring.datasource.driver-class-name", MYSQL_CONTAINER.getDriverClassName());

        // Now run Flyway clean + migrate
        Flyway flyway = Flyway.configure()
                .dataSource(
                    MYSQL_CONTAINER.getJdbcUrl(),
                    MYSQL_CONTAINER.getUsername(),
                    MYSQL_CONTAINER.getPassword()
                )
                .cleanDisabled(false)  // ✅ THIS FIXES IT
                .load();

        flyway.clean();
        flyway.migrate();

        ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
        JdbcTemplate jdbc = appCtx.getBean(JdbcTemplate.class);
        DatabaseDefaults.seedAll(jdbc);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Clean up database after all tests have run
        ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
        JdbcTemplate jdbc = appCtx.getBean(JdbcTemplate.class);
        DatabaseDefaults.cleanupAll(jdbc);
    }
}