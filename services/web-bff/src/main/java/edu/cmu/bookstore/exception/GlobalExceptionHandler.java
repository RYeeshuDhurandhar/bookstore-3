package edu.cmu.bookstore.exception;

import edu.cmu.bookstore.model.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 *
 * This class centralizes exception handling across all REST controllers
 * and converts thrown exceptions into consistent HTTP responses.
 *
 * Each handled exception is mapped to:
 * - an appropriate HTTP status code
 * - a response body containing an {@link ApiMessage}
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles bad request exceptions.
     *
     * This method converts {@link BadRequestException} into an
     * HTTP 400 Bad Request response with the exception message
     * included in the response body.
     *
     * @param ex the thrown bad request exception
     * @return HTTP 400 response containing the error message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiMessage> handleBadRequest(BadRequestException ex) {
        // Return a 400 Bad Request response with the exception message.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles unauthorized exceptions.
     *
     * This method converts {@link UnauthorizedException} into an
     * HTTP 401 Unauthorized response with the exception message
     * included in the response body.
     *
     * @param ex the thrown unauthorized exception
     * @return HTTP 401 response containing the error message
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiMessage> handleUnauthorized(UnauthorizedException ex) {
        // Return a 401 Unauthorized response with the exception message.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles all unexpected exceptions not covered by more specific handlers.
     *
     * This method acts as a fallback and converts any unhandled exception
     * into an HTTP 500 Internal Server Error response with a generic message.
     *
     * @param ex the unexpected exception
     * @return HTTP 500 response containing a generic internal error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGeneric(Exception ex) {
        // Return a generic 500 Internal Server Error response.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiMessage("Internal server error."));
    }
}