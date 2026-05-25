package edu.cmu.bookstore.model;

/**
 * Simple response model used to return a message in API responses.
 *
 * This class is commonly used for error responses or other endpoints
 * that only need to send a single message field in the JSON body.
 */
public class ApiMessage {

    /**
     * Message content returned to the client.
     */
    private String message;

    /**
     * Default constructor.
     *
     * Required by some serialization and deserialization frameworks.
     */
    public ApiMessage() {
    }

    /**
     * Creates an ApiMessage with the provided message text.
     *
     * @param message message content to include in the response
     */
    public ApiMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the message content.
     *
     * @return message stored in this response object
     */
    public String getMessage() {
        return message;
    }

    /**
     * Updates the message content.
     *
     * @param message new message value
     */
    public void setMessage(String message) {
        this.message = message;
    }
}