package edu.cmu.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the CRM service.
 *
 * This service acts as an asynchronous Kafka consumer that listens
 * for customer registration events and sends welcome emails.
 */
@SpringBootApplication
public class CrmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmServiceApplication.class, args);
    }
}
