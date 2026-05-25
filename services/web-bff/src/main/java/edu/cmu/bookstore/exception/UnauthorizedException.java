package edu.cmu.bookstore.exception;

/**
 * Exception thrown when a request is not authorized.
 *
 * This exception is typically used for HTTP 401 Unauthorized cases,
 * such as missing Authorization headers, malformed bearer tokens,
 * invalid JWT claims, or expired JWT tokens.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Creates a new unauthorized exception with the provided message.
     *
     * @param message detailed explanation of why authorization failed
     */
    public UnauthorizedException(String message) {
        // Pass the message to the parent RuntimeException class.
        super(message);
    }
}