package edu.cmu.bookstore.exception;

/**
 * Exception thrown when the client sends an invalid request.
 *
 * This exception represents HTTP 400 Bad Request scenarios,
 * such as missing required input, malformed parameters,
 * or values that do not satisfy expected request rules.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new bad request exception with the provided error message.
     *
     * @param message descriptive message explaining why the request is invalid
     */
    public BadRequestException(String message) {
        // Pass the error message to the parent RuntimeException class.
        super(message);
    }
}