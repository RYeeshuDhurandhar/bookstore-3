package edu.cmu.bookstore.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * This exception is used when an entity requested by the client
 * does not exist in the system and should result in an
 * HTTP 404 Not Found response.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new not-found exception with the specified message.
     *
     * @param message error message describing the missing resource
     */
    public NotFoundException(String message) {
        super(message);
    }
}