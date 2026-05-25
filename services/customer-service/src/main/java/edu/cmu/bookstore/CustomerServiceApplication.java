package edu.cmu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Customer Service Spring Boot application.
 *
 * The {@code @SpringBootApplication} annotation enables:
 * - component scanning
 * - auto-configuration
 * - configuration support for the application
 *
 * Running this class starts the embedded server and initializes
 * the Spring application context for the customer service.
 */
@SpringBootApplication
public class CustomerServiceApplication {

    /**
     * Starts the Customer Service application.
     *
     * This method boots the Spring Boot application and launches
     * the embedded runtime environment.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        // Launch the Spring Boot customer service application.
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}