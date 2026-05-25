package edu.cmu.bookstore.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes a simple status endpoint for the service.
 *
 * This endpoint can be used for health checks, monitoring,
 * automated tests, and verifying that the application is running.
 */
@RestController
public class StatusController {

    /**
     * Returns the current status of the service as plain text.
     *
     * Response details:
     * - HTTP status: 200 OK
     * - Content-Type: text/plain
     * - Body: "OK"
     *
     * @return plain-text response indicating the service is operational
     */
    @GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getStatus() {
        // Return a successful health/status response.
        return ResponseEntity.ok("OK");
    }
}