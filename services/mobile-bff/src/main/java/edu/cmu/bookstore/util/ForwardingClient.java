package edu.cmu.bookstore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Utility component responsible for forwarding incoming BFF requests
 * to downstream backend services.
 *
 * This class provides simple helper methods for GET, POST, and PUT
 * requests, and uses a shared internal exchange method to perform
 * the actual HTTP call.
 *
 * It also preserves selected response headers and propagates both
 * successful and error responses back to the caller.
 */
@Component
public class ForwardingClient {

    /**
     * Spring RestTemplate used to perform outbound HTTP requests.
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
     * @param restTemplate HTTP client used for outbound calls
     */
    public ForwardingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Forwards an HTTP GET request to the downstream service.
     *
     * @param path relative backend path to call
     * @return response returned by the downstream service
     */
    public ResponseEntity<String> get(String path) {
        // Delegate to the shared exchange method using HTTP GET.
        return exchange(path, HttpMethod.GET, null);
    }

    /**
     * Forwards an HTTP POST request to the downstream service.
     *
     * @param path relative backend path to call
     * @param body request body to forward
     * @return response returned by the downstream service
     */
    public ResponseEntity<String> post(String path, String body) {
        // Delegate to the shared exchange method using HTTP POST.
        return exchange(path, HttpMethod.POST, body);
    }

    /**
     * Forwards an HTTP PUT request to the downstream service.
     *
     * @param path relative backend path to call
     * @param body request body to forward
     * @return response returned by the downstream service
     */
    public ResponseEntity<String> put(String path, String body) {
        // Delegate to the shared exchange method using HTTP PUT.
        return exchange(path, HttpMethod.PUT, body);
    }

    /**
     * Performs the actual outbound HTTP exchange with the backend service.
     *
     * This method:
     * - constructs the target URI from the configured base URL and path
     * - sets JSON content type when a request body is present
     * - forwards the request using RestTemplate
     * - returns the backend response while preserving selected headers
     * - catches HTTP status exceptions and converts them into response entities
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

        // Prepare request headers for the outbound call.
        HttpHeaders headers = new HttpHeaders();

        // Set JSON content type only when a request body is present.
        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        // Wrap the outgoing body and headers into an HTTP entity.
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            // Execute the outbound HTTP request.
            ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);

            // Rebuild the response while preserving selected headers and the original body.
            return ResponseEntity.status(response.getStatusCode())
                    .headers(copyResponseHeaders(response.getHeaders()))
                    .body(response.getBody());
        } catch (HttpStatusCodeException ex) {
            // If the backend returns an HTTP error status, propagate that status and body back.
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(copyResponseHeaders(ex.getResponseHeaders()))
                    .body(ex.getResponseBodyAsString());
        }
    }

    /**
     * Copies selected response headers from the backend response.
     *
     * This method preserves only headers that are relevant for current use:
     * - Location
     * - Content-Type
     *
     * If the source headers are null, an empty HttpHeaders object is returned.
     *
     * @param source source headers from the backend response
     * @return copied headers containing only selected values
     */
    private HttpHeaders copyResponseHeaders(HttpHeaders source) {
        // Create a new target headers object for the forwarded response.
        HttpHeaders target = new HttpHeaders();

        // If no source headers were provided, return an empty headers object.
        if (source == null) {
            return target;
        }

        // Preserve the Location header when present.
        if (source.getLocation() != null) {
            target.setLocation(source.getLocation());
        }

        // Preserve the Content-Type header when present.
        if (source.getContentType() != null) {
            target.setContentType(source.getContentType());
        }

        // Return the filtered set of copied headers.
        return target;
    }
}