package edu.cmu.bookstore.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes a simple health/status endpoint.
 *
 * This endpoint can be used by clients, automated tests, load balancers,
 * or monitoring systems to verify that the service is up and responding.
 */
@RestController
public class StatusController {

    /**
     * Returns the current status of the service as plain text.
     *
     * The endpoint responds with:
     * - HTTP 200 OK
     * - response body: "OK"
     * - content type: text/plain
     *
     * @return plain-text status response indicating the service is healthy
     */
    @GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getStatus() {
        // Return a successful plain-text health response.
        return ResponseEntity.ok("OK");
    }
}