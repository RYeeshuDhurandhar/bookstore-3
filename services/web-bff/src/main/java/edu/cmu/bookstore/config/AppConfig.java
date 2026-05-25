package edu.cmu.bookstore.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application configuration class.
 *
 * This class defines shared Spring beans used across the application.
 * In this case, it provides a configured {@link RestTemplate} bean
 * for making outbound HTTP requests to backend services.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a {@link RestTemplate} bean.
     *
     * The configured RestTemplate uses:
     * - a connection timeout of 10 seconds
     * - a read timeout of 20 seconds
     *
     * These timeout values help prevent the application from waiting
     * indefinitely when an external service is slow or unavailable.
     *
     * @param builder Spring-provided builder used to create RestTemplate instances
     * @return configured RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Build a RestTemplate with explicit connection and read timeout settings.
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }
}