package edu.cmu.bookstore.exception;

/**
 * Exception thrown when a request conflicts with the current state
 * of the system.
 *
 * This exception is typically used for cases such as duplicate
 * resource creation or operations that violate uniqueness or
 * consistency constraints, and should result in an HTTP 409
 * Conflict response.
 */
public class ConflictException extends RuntimeException {

    /**
     * Constructs a new conflict exception with the specified message.
     *
     * @param message error message describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}