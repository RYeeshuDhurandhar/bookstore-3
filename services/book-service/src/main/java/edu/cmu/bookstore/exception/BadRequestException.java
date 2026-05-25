package edu.cmu.bookstore.exception;

/**
 * Exception thrown when a client sends an invalid request.
 *
 * This exception is used for request validation failures and other
 * client-side errors that should result in an HTTP 400 Bad Request
 * response.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new bad request exception with the specified message.
     *
     * @param message error message describing the invalid request
     */
    public BadRequestException(String message) {
        super(message);
    }
}