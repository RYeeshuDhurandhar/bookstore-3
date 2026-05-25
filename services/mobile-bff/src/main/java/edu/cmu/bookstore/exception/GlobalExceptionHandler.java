package edu.cmu.bookstore.exception;

import edu.cmu.bookstore.model.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 *
 * This class centralizes exception-to-HTTP-response mapping so that
 * controllers and services can throw exceptions without having to build
 * error responses manually in each endpoint.
 *
 * It converts known application exceptions into structured API responses
 * containing an {@link ApiMessage}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles application-specific bad request errors.
     *
     * This maps {@link BadRequestException} to HTTP 400 Bad Request
     * and returns the exception message in the response body.
     *
     * @param ex thrown bad request exception
     * @return HTTP 400 response containing the error message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiMessage> handleBadRequest(BadRequestException ex) {
        // Return a 400 Bad Request response with the exception message.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles application-specific unauthorized access errors.
     *
     * This maps {@link UnauthorizedException} to HTTP 401 Unauthorized
     * and returns the exception message in the response body.
     *
     * @param ex thrown unauthorized exception
     * @return HTTP 401 response containing the error message
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiMessage> handleUnauthorized(UnauthorizedException ex) {
        // Return a 401 Unauthorized response with the exception message.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles any unexpected exception not covered by more specific handlers.
     *
     * This acts as a fallback handler and maps unhandled exceptions to
     * HTTP 500 Internal Server Error with a generic response message.
     *
     * @param ex unexpected exception
     * @return HTTP 500 response with a generic internal error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGeneric(Exception ex) {
        // Return a generic 500 Internal Server Error response.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiMessage("Internal server error."));
    }
}