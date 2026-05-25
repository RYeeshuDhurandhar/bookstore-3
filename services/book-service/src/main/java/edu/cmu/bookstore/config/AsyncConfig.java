package edu.cmu.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class that enables asynchronous method execution.
 *
 * By using {@link EnableAsync}, Spring can run methods annotated with
 * asynchronous support in separate threads, allowing non-blocking execution
 * for suitable tasks such as background processing or external service calls.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}