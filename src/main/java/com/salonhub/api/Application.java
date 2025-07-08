package com.salonhub.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
  info = @Info(
    title       = "SalonHub API",
    version     = "3.0.0",
    description = "REST endpoints for SalonHub application"
  )
)
public class Application {

	public static void main(String[] args) {
		// Check for SALON_HUB_ENV environment variable
		String env = System.getenv("SALON_HUB_ENV");
		if (env != null && !env.isBlank()) {
			System.setProperty("spring.config.name", env);
			System.out.println("[INFO] Using config profile: " + env + ".yml");
		} else {
			System.out.println("[INFO] Using default config: application.yml");
		}
		SpringApplication.run(Application.class, args);
	}

}
