package edu.cmu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Mobile BFF Spring Boot application.
 *
 * The {@code @SpringBootApplication} annotation enables:
 * - component scanning
 * - auto-configuration
 * - application configuration support
 *
 * Running this class starts the embedded server and initializes
 * the Spring application context for the mobile backend-for-frontend service.
 */
@SpringBootApplication
public class MobileBffApplication {

    /**
     * Starts the Mobile BFF application.
     *
     * This method boots the Spring Boot application and launches
     * the embedded runtime environment.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        // Launch the Spring Boot mobile BFF application.
        SpringApplication.run(MobileBffApplication.class, args);
    }
}