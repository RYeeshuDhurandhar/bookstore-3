package edu.cmu.bookstore.service;

import edu.cmu.bookstore.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service responsible for processing customer-related events.
 * It currently listens for customer registration events and triggers
 * the welcome email workflow.
 */
@Service
public class KafkaConsumerService {

    /**
     * Logger used for tracking consumed Kafka events.
     */
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Service used to send emails after processing Kafka events.
     */
    private final EmailService emailService;

    /**
     * Creates the Kafka consumer service with the required email service dependency.
     *
     * @param emailService service used to send welcome emails
     */
    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Listens for customer registration events from the configured Kafka topic.
     * Once a customer event is received, a welcome email is sent to that customer.
     *
     * @param customer deserialized customer payload received from Kafka
     */
    @KafkaListener(topics = "${app.kafka.topic:rdhurand.customer.evt}", groupId = "crm-group")
    public void listenCustomerRegistered(Customer customer) {
        logger.info("Received customer registration event for: {}", customer.getUserId());
        emailService.sendWelcomeEmail(customer);
    }
}