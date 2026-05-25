package edu.cmu.bookstore.service;

import edu.cmu.bookstore.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending application emails to customers.
 * Currently it supports sending the welcome email after customer creation.
 */
@Service
public class EmailService {

    /**
     * Logger used for tracking email sending activity.
     */
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Spring mail sender used to send plain text emails.
     */
    private final JavaMailSender mailSender;

    /**
     * Email address configured as the sender.
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Andrew ID used in the welcome email body.
     * Defaults to "rdhurand" if the property is not explicitly set.
     */
    @Value("${app.andrew.id:rdhurand}")
    private String andrewId;

    /**
     * Creates the email service with the required mail sender dependency.
     *
     * @param mailSender mail sender used to dispatch emails
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a welcome email to the newly created customer.
     * The message is sent as a plain text email and includes
     * the configured Andrew ID in the email body.
     *
     * @param customer customer who should receive the welcome email
     */
    public void sendWelcomeEmail(Customer customer) {
        logger.info("Sending welcome email to customer: {}", customer.getUserId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(customer.getUserId());
        message.setSubject("Activate your book store account");

        String body = String.format(
                "Dear %s,%n" +
                "Welcome to the Book store created by %s.%n" +
                "Exceptionally this time we won’t ask you to click a link to activate your account.",
                customer.getName(),
                andrewId
        );

        message.setText(body);
        mailSender.send(message);

        logger.info("Welcome email successfully sent to {}", customer.getUserId());
    }
}