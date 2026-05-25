package edu.cmu.bookstore.model;

/**
 * Simple API response model used for returning a message to the client.
 *
 * This class is typically used for error responses or other endpoints
 * that need to return a short textual message in JSON format.
 */
public class ApiMessage {

    /**
     * Message text returned to the client.
     */
    private String message;

    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public ApiMessage() {
    }

    /**
     * Constructs an API message with the provided text.
     *
     * @param message message to be returned in the response
     */
    public ApiMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the message text.
     *
     * @return response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message text.
     *
     * @param message response message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}