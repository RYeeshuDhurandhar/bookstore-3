package edu.cmu.bookstore.exception;

/**
 * Exception thrown when a request fails authorization checks.
 *
 * This exception represents HTTP 401 Unauthorized scenarios,
 * such as a missing Authorization header, an invalid token,
 * or a token that does not satisfy required validation rules.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Creates a new unauthorized exception with the provided error message.
     *
     * @param message descriptive message explaining why authorization failed
     */
    public UnauthorizedException(String message) {
        // Pass the error message to the parent RuntimeException class.
        super(message);
    }
}