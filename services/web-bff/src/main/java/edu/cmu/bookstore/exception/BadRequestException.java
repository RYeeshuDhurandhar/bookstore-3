package edu.cmu.bookstore.exception;

/**
 * Exception thrown when the client sends an invalid request.
 *
 * This exception is typically used for HTTP 400 Bad Request scenarios,
 * such as missing required fields, malformed input, or invalid parameter values.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new bad request exception with the provided message.
     *
     * @param message detailed explanation of why the request is invalid
     */
    public BadRequestException(String message) {
        // Pass the message to the parent RuntimeException class.
        super(message);
    }
}