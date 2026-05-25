package edu.cmu.bookstore.controller;

import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.util.ForwardingClient;
import edu.cmu.bookstore.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * REST controller for the web Backend For Frontend (BFF).
 *
 * This controller validates the Authorization header and the presence
 * of the X-Client-Type header, then forwards requests to downstream services.
 */
@RestController
public class WebBffController {

    /**
     * Utility used to validate JWT-based Authorization headers.
     */
    private final JwtUtil jwtUtil;

    /**
     * Client responsible for forwarding incoming BFF requests to downstream services.
     */
    private final ForwardingClient forwardingClient;

    /**
     * Creates the web BFF controller with its required dependencies.
     *
     * @param jwtUtil utility for JWT validation
     * @param forwardingClient client used to forward requests upstream
     */
    public WebBffController(JwtUtil jwtUtil, ForwardingClient forwardingClient) {
        this.jwtUtil = jwtUtil;
        this.forwardingClient = forwardingClient;
    }

    /**
     * Forwards book creation requests after validating required headers.
     *
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @param body raw request body
     * @return upstream response from the backend service
     */
    @PostMapping("/books")
    public ResponseEntity createBook(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.post("/books", body);
    }

    /**
     * Forwards book update requests for the specified ISBN.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @param body raw request body
     * @return upstream response from the backend service
     */
    @PutMapping("/books/{isbn}")
    public ResponseEntity updateBook(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.put("/books/" + encodePathSegment(isbn), body);
    }

    /**
     * Retrieves all books by forwarding the request to the downstream service.
     *
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing all books
     */
    @GetMapping("/books")
    public ResponseEntity getAllBooks(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.get("/books");
    }

    /**
     * Retrieves a single book by ISBN by forwarding the request upstream.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing the book
     */
    @GetMapping("/books/{isbn}")
    public ResponseEntity getBook(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.get("/books/" + encodePathSegment(isbn));
    }

    /**
     * Retrieves a single book using the alternative ISBN path.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing the book
     */
    @GetMapping("/books/isbn/{isbn}")
    public ResponseEntity getBookAlt(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.get("/books/isbn/" + encodePathSegment(isbn));
    }

    /**
     * Forwards customer creation requests after validating required headers.
     *
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @param body raw request body
     * @return upstream response from the backend service
     */
    @PostMapping("/customers")
    public ResponseEntity createCustomer(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.post("/customers", body);
    }

    /**
     * Retrieves a customer by internal identifier by forwarding the request upstream.
     *
     * @param id customer identifier
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing the customer
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity getCustomerById(
            @PathVariable String id,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.get("/customers/" + encodePathSegment(id));
    }

    /**
     * Retrieves a customer using the original query string from the incoming request.
     * The query string is forwarded as-is after validating that it is present.
     *
     * @param request original HTTP request
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing the customer
     */
    @GetMapping("/customers")
    public ResponseEntity getCustomerByUserId(
            HttpServletRequest request,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);

        String rawQuery = request.getQueryString();
        if (rawQuery == null || rawQuery.isBlank()) {
            throw new BadRequestException("Missing required query parameter: userId");
        }

        return forwardingClient.get("/customers?" + rawQuery);
    }

    /**
     * Retrieves related books for a given ISBN by forwarding the request upstream.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return upstream response containing related books
     */
    @GetMapping("/books/{isbn}/related-books")
    public ResponseEntity getRelatedBooks(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);
        return forwardingClient.get("/books/" + encodePathSegment(isbn) + "/related-books");
    }

    /**
     * Ensures that the X-Client-Type header is present and non-empty.
     *
     * @param clientType X-Client-Type header value
     */
    private void requireClientTypeHeader(String clientType) {
        if (clientType == null || clientType.trim().isEmpty()) {
            throw new BadRequestException("Missing X-Client-Type header.");
        }
    }

    /**
     * URL-encodes a path segment so it can be safely inserted into a forwarded URL.
     *
     * @param value raw path segment value
     * @return encoded path segment with spaces preserved as %20 instead of +
     */
    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}