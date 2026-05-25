package edu.cmu.bookstore.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application-wide configuration class.
 *
 * This class defines Spring beans that are shared across the application.
 * In particular, it provides a configured {@link RestTemplate} instance
 * used for outbound HTTP calls, such as requests to the LLM service.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a {@link RestTemplate} bean with configured connection and read timeouts.
     *
     * The timeout value is read from the application property
     * {@code app.llm.timeout.ms}. If the property is not provided,
     * a default value of 15000 milliseconds is used.
     *
     * @param builder Spring-provided builder used to create the RestTemplate
     * @param timeoutMs timeout in milliseconds for both connection establishment
     *                  and reading the response
     * @return configured RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${app.llm.timeout.ms:15000}") int timeoutMs) {
        return builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }
}