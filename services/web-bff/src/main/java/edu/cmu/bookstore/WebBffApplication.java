package edu.cmu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Web BFF Spring Boot application.
 *
 * The {@code @SpringBootApplication} annotation enables:
 * - component scanning
 * - auto-configuration
 * - application configuration support
 *
 * Running this class starts the embedded server and initializes
 * the Spring application context for the web backend-for-frontend service.
 */
@SpringBootApplication
public class WebBffApplication {

    /**
     * Starts the Web BFF application.
     *
     * This method boots the Spring Boot application and launches
     * the embedded runtime environment.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        // Launch the Spring Boot web BFF application.
        SpringApplication.run(WebBffApplication.class, args);
    }
}