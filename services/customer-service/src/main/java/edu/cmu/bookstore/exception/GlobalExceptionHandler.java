package edu.cmu.bookstore.exception;

import edu.cmu.bookstore.model.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for REST endpoints.
 *
 * This class centralizes application exception handling and converts
 * exceptions into consistent HTTP responses with user-friendly error
 * messages. It applies to all controllers in the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid client requests represented by {@link BadRequestException}.
     *
     * @param ex exception containing the validation or request error message
     * @return HTTP 400 response with the corresponding error message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiMessage> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles resource-not-found cases represented by {@link NotFoundException}.
     *
     * @param ex exception containing the not-found message
     * @return HTTP 404 response with the corresponding error message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiMessage> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles request conflicts represented by {@link ConflictException}.
     *
     * This application maps such conflicts to HTTP 422 Unprocessable Entity.
     *
     * @param ex exception containing the conflict message
     * @return HTTP 422 response with the corresponding error message
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiMessage> handleConflict(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles malformed or unreadable JSON request bodies.
     *
     * @param ex exception thrown when the request body cannot be parsed
     * @return HTTP 400 response indicating malformed JSON input
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiMessage> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Malformed JSON request."));
    }

    /**
     * Handles requests missing a required query parameter.
     *
     * @param ex exception containing the missing parameter information
     * @return HTTP 400 response identifying the missing query parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiMessage> handleMissingQueryParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Missing required query parameter: " + ex.getParameterName()));
    }

    /**
     * Handles request parameter type mismatches, such as invalid path variable
     * or query parameter formats.
     *
     * @param ex exception describing the parameter type mismatch
     * @return HTTP 400 response indicating invalid request parameters
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Invalid request parameter."));
    }

    /**
     * Handles any uncaught exception as a generic internal server error.
     *
     * This ensures that unexpected failures still return a controlled
     * JSON response rather than exposing internal implementation details.
     *
     * @param ex unexpected exception
     * @return HTTP 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiMessage("Internal server error."));
    }
}