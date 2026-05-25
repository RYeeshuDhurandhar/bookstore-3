package edu.cmu.bookstore.exception;

import edu.cmu.bookstore.model.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Centralized exception handler for all REST controllers.
 * It converts thrown exceptions into consistent HTTP responses
 * with an {@link ApiMessage} body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom bad request exceptions and returns HTTP 400.
     *
     * @param ex the thrown bad request exception
     * @return response containing the exception message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiMessage> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles resource not found exceptions and returns HTTP 404.
     *
     * @param ex the thrown not found exception
     * @return response containing the exception message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiMessage> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles conflict-style exceptions and returns HTTP 422.
     *
     * @param ex the thrown conflict exception
     * @return response containing the exception message
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiMessage> handleConflict(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiMessage(ex.getMessage()));
    }

    /**
     * Handles malformed or unreadable JSON request bodies and returns HTTP 400.
     *
     * @param ex the thrown JSON parsing exception
     * @return response with a generic malformed JSON message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiMessage> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Malformed JSON request."));
    }

    /**
     * Handles missing required query parameters and returns HTTP 400.
     *
     * @param ex the thrown missing parameter exception
     * @return response describing the missing query parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiMessage> handleMissingQueryParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Missing required query parameter: " + ex.getParameterName()));
    }

    /**
     * Handles request parameter type mismatch errors and returns HTTP 400.
     *
     * @param ex the thrown type mismatch exception
     * @return response with a generic invalid parameter message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage("Invalid request parameter."));
    }

    /**
     * Handles Spring {@link ResponseStatusException} and preserves
     * the original status code and reason message.
     *
     * @param ex the thrown response status exception
     * @return response containing the status reason or code string
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiMessage> handleResponseStatus(ResponseStatusException ex) {
        String reason = ex.getReason() == null ? ex.getStatusCode().toString() : ex.getReason();
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ApiMessage(reason));
    }

    /**
     * Fallback handler for all uncaught exceptions.
     * Returns HTTP 500 with a generic internal error message.
     *
     * @param ex the thrown unexpected exception
     * @return generic internal server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiMessage("Internal server error."));
    }
}