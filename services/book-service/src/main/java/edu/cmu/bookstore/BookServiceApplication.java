package edu.cmu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Book Service Spring Boot application.
 *
 * The {@code @SpringBootApplication} annotation enables:
 * - component scanning
 * - auto-configuration
 * - additional Spring Boot configuration support
 *
 * Running this class starts the embedded application server and
 * initializes the Spring application context.
 */
@SpringBootApplication
public class BookServiceApplication {

    /**
     * Application startup method.
     *
     * This method boots the Spring application and starts the service.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        // Launch the Spring Boot application.
        SpringApplication.run(BookServiceApplication.class, args);
    }
}