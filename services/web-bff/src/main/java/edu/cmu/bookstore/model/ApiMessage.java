package edu.cmu.bookstore.model;

/**
 * Simple API response model that holds a single message string.
 *
 * This class is commonly used for error responses or any endpoint
 * that needs to return a plain message in JSON form.
 */
public class ApiMessage {

    /**
     * Message text returned in the API response body.
     */
    private String message;

    /**
     * Default constructor.
     *
     * Required by frameworks and serializers that instantiate
     * the object before setting fields.
     */
    public ApiMessage() {
    }

    /**
     * Creates an ApiMessage with the given message text.
     *
     * @param message message to store in the response object
     */
    public ApiMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the stored message text.
     *
     * @return message value
     */
    public String getMessage() {
        return message;
    }

    /**
     * Updates the stored message text.
     *
     * @param message new message value
     */
    public void setMessage(String message) {
        this.message = message;
    }
}