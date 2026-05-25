package edu.cmu.bookstore.service;

import edu.cmu.bookstore.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing domain events to Kafka.
 *
 * In this implementation, it is used to send "Customer Registered" events
 * whenever a new customer is successfully created in the system.
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic:rdhurand.customer.evt}")
    private String topic;

    /**
     * Constructs the producer service with the required KafkaTemplate.
     *
     * @param kafkaTemplate Spring Kafka template used for sending messages
     */
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a customer registration event to Kafka.
     *
     * The message contains the full customer object as JSON, which
     * corresponds to the "Customer Registered" domain event.
     *
     * @param customer the newly registered customer details
     */
    public void publishCustomerRegisteredEvent(Customer customer) {
        logger.info("Publishing customer registered event to topic {}: {}", topic, customer.getUserId());
        try {
            kafkaTemplate.send(topic, customer);
        } catch (Exception e) {
            logger.error("Failed to publish customer registration event to Kafka", e);
            // Typically, we might want to retry or handle this asynchronously,
            // but for this assignment, we'll log the error.
        }
    }
}
