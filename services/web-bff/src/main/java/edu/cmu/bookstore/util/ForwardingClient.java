package edu.cmu.bookstore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Utility component responsible for forwarding HTTP requests
 * from the BFF layer to downstream backend services.
 *
 * This class provides helper methods for GET, POST, and PUT calls,
 * and centralizes the logic for:
 * - building the backend URI
 * - creating request headers
 * - sending the request using RestTemplate
 * - copying selected response headers
 * - propagating both success and error responses
 */
@Component
public class ForwardingClient {

    /**
     * RestTemplate used to perform outbound HTTP calls.
     */
    private final RestTemplate restTemplate;

    /**
     * Base URL of the Book service, injected from application configuration.
     */
    @Value("${book-service.url:http://localhost:3000}")
    private String bookServiceUrl;

    /**
     * Base URL of the Customer service, injected from application configuration.
     */
    @Value("${customer-service.url:http://localhost:3000}")
    private String customerServiceUrl;

    /**
     * Creates a forwarding client with the required RestTemplate dependency.
     *
     * @param restTemplate HTTP client used for forwarding requests
     */
    public ForwardingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Forwards an HTTP GET request to the backend service.
     *
     * @param path relative backend path
     * @return response returned by the backend service
     */
    public ResponseEntity<String> get(String path) {
        // Delegate GET requests to the shared exchange method.
        return exchange(path, HttpMethod.GET, null);
    }

    /**
     * Forwards an HTTP POST request to the backend service.
     *
     * @param path relative backend path
     * @param body request body to forward
     * @return response returned by the backend service
     */
    public ResponseEntity<String> post(String path, String body) {
        // Delegate POST requests to the shared exchange method.
        return exchange(path, HttpMethod.POST, body);
    }

    /**
     * Forwards an HTTP PUT request to the backend service.
     *
     * @param path relative backend path
     * @param body request body to forward
     * @return response returned by the backend service
     */
    public ResponseEntity<String> put(String path, String body) {
        // Delegate PUT requests to the shared exchange method.
        return exchange(path, HttpMethod.PUT, body);
    }

    /**
     * Executes an outbound HTTP request to the backend service.
     *
     * This method:
     * - builds the full URI from the configured base URL and relative path
     * - sets JSON content type when a request body is present
     * - sends the request using RestTemplate
     * - returns the backend response while preserving selected headers
     * - handles HTTP error responses and passes them through
     *
     * @param path relative backend path
     * @param method HTTP method to use
     * @param body optional request body
     * @return response entity containing backend status, selected headers, and body
     */
    private ResponseEntity<String> exchange(String path, HttpMethod method, String body) {
        // Determine which backend service to route to based on the relative path.
        String baseUrl = bookServiceUrl;
        if (path != null && path.startsWith("/customers")) {
            baseUrl = customerServiceUrl;
        }

        // Construct the target URI by combining the appropriate backend base URL with the relative path.
        URI uri = URI.create(baseUrl + path);

        // Prepare headers for the outbound request.
        HttpHeaders headers = new HttpHeaders();

        // Set JSON content type only when a request body is present.
        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        // Wrap the request body and headers in an HttpEntity.
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            // Execute the outbound request against the backend service.
            ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);

            // Return the backend response while copying selected headers.
            return ResponseEntity.status(response.getStatusCode())
                    .headers(copyResponseHeaders(response.getHeaders()))
                    .body(response.getBody());
        } catch (HttpStatusCodeException ex) {
            // If the backend returned an HTTP error response, propagate its status, headers, and body.
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(copyResponseHeaders(ex.getResponseHeaders()))
                    .body(ex.getResponseBodyAsString());
        }
    }

    /**
     * Copies selected response headers from the backend response.
     *
     * Only specific headers are preserved:
     * - Location
     * - Content-Type
     *
     * If the source headers are null, an empty header set is returned.
     *
     * @param source headers received from the backend response
     * @return filtered headers to include in the forwarded response
     */
    private HttpHeaders copyResponseHeaders(HttpHeaders source) {
        // Create a new target header container for the forwarded response.
        HttpHeaders target = new HttpHeaders();

        // Return empty headers if the source is null.
        if (source == null) {
            return target;
        }

        // Preserve the Location header if present.
        if (source.getLocation() != null) {
            target.setLocation(source.getLocation());
        }

        // Preserve the Content-Type header if present.
        if (source.getContentType() != null) {
            target.setContentType(source.getContentType());
        }

        // Return the copied header subset.
        return target;
    }
}