package edu.cmu.bookstore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.util.ForwardingClient;
import edu.cmu.bookstore.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * REST controller for the mobile Backend For Frontend (BFF).
 *
 * This controller validates the Authorization header and the presence
 * of the X-Client-Type header, forwards requests, and applies mobile-specific
 * transformations to selected responses.
 */
@RestController
public class MobileBffController {

    /**
     * Utility used to validate JWT-based Authorization headers.
     */
    private final JwtUtil jwtUtil;

    /**
     * Client responsible for forwarding incoming BFF requests to backend services.
     */
    private final ForwardingClient forwardingClient;

    /**
     * ObjectMapper used to parse and rewrite JSON responses for mobile clients.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates the mobile BFF controller with its required dependencies.
     *
     * @param jwtUtil utility for JWT validation
     * @param forwardingClient client used to forward requests to upstream services
     */
    public MobileBffController(JwtUtil jwtUtil, ForwardingClient forwardingClient) {
        this.jwtUtil = jwtUtil;
        this.forwardingClient = forwardingClient;
    }

    /**
     * Forwards book creation requests after validating Authorization and client type headers.
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
     * Retrieves all books from the backend and applies mobile-specific
     * transformation to the response payload.
     *
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return transformed response for mobile clients
     */
    @GetMapping("/books")
    public ResponseEntity getAllBooks(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);

        ResponseEntity upstream = forwardingClient.get("/books");
        return transformBookListResponse(upstream);
    }

    /**
     * Retrieves a single book by ISBN and applies mobile-specific
     * transformation to the response payload.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return transformed response for mobile clients
     */
    @GetMapping("/books/{isbn}")
    public ResponseEntity getBook(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);

        ResponseEntity upstream = forwardingClient.get("/books/" + encodePathSegment(isbn));
        return transformBookResponse(upstream);
    }

    /**
     * Retrieves a single book using the alternative ISBN path and applies
     * mobile-specific transformation to the response payload.
     *
     * @param isbn ISBN path parameter
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return transformed response for mobile clients
     */
    @GetMapping("/books/isbn/{isbn}")
    public ResponseEntity getBookAlt(
            @PathVariable String isbn,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);

        ResponseEntity upstream = forwardingClient.get("/books/isbn/" + encodePathSegment(isbn));
        return transformBookResponse(upstream);
    }

    /**
     * Forwards customer creation requests after validating headers.
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
     * Retrieves a customer by internal identifier and removes address-related
     * fields from the response for mobile clients.
     *
     * @param id customer identifier
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return transformed customer response
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity getCustomerById(
            @PathVariable String id,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        jwtUtil.validateAuthorizationHeader(authorization);
        requireClientTypeHeader(clientType);

        ResponseEntity upstream = forwardingClient.get("/customers/" + encodePathSegment(id));
        return transformCustomerResponse(upstream);
    }

    /**
     * Retrieves a customer using query parameters from the original request.
     * The query string is forwarded as-is after validating that it is present.
     *
     * @param request original HTTP request
     * @param clientType X-Client-Type header value
     * @param authorization Authorization header value
     * @return transformed customer response
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

        ResponseEntity upstream = forwardingClient.get("/customers?" + rawQuery);
        return transformCustomerResponse(upstream);
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
     * Applies mobile-specific transformation to a single book response.
     * Currently converts genre value "non-fiction" into numeric value 3.
     *
     * @param upstream original upstream response
     * @return transformed response if applicable, otherwise the original response
     */
    private ResponseEntity transformBookResponse(ResponseEntity upstream) {
        if (!upstream.getStatusCode().is2xxSuccessful() || upstream.getBody() == null) {
            return upstream;
        }

        try {
            JsonNode root = objectMapper.readTree(upstream.getBody().toString());

            if (root instanceof ObjectNode objectNode) {
                JsonNode genreNode = objectNode.get("genre");
                if (genreNode != null && "non-fiction".equalsIgnoreCase(genreNode.asText())) {
                    objectNode.put("genre", 3);
                }

                String updatedBody = objectMapper.writeValueAsString(objectNode);
                return ResponseEntity
                        .status(upstream.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedBody);
            }

            return upstream;
        } catch (Exception e) {
            // If transformation fails, preserve the original upstream response.
            return upstream;
        }
    }

    /**
     * Applies mobile-specific transformation to a list of book responses.
     * For each book, genre value "non-fiction" is converted into numeric value 3.
     *
     * @param upstream original upstream response
     * @return transformed response if applicable, otherwise the original response
     */
    private ResponseEntity transformBookListResponse(ResponseEntity upstream) {
        if (!upstream.getStatusCode().is2xxSuccessful() || upstream.getBody() == null) {
            return upstream;
        }

        try {
            JsonNode root = objectMapper.readTree(upstream.getBody().toString());

            if (root.isArray()) {
                for (JsonNode node : root) {
                    if (node instanceof ObjectNode objectNode) {
                        JsonNode genreNode = objectNode.get("genre");
                        if (genreNode != null && "non-fiction".equalsIgnoreCase(genreNode.asText())) {
                            objectNode.put("genre", 3);
                        }
                    }
                }

                String updatedBody = objectMapper.writeValueAsString(root);
                return ResponseEntity
                        .status(upstream.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedBody);
            }

            return upstream;
        } catch (Exception e) {
            // If transformation fails, preserve the original upstream response.
            return upstream;
        }
    }

    /**
     * Applies mobile-specific transformation to customer responses by removing
     * detailed address fields from the payload.
     *
     * @param upstream original upstream response
     * @return transformed response if applicable, otherwise the original response
     */
    private ResponseEntity transformCustomerResponse(ResponseEntity upstream) {
        if (!upstream.getStatusCode().is2xxSuccessful() || upstream.getBody() == null) {
            return upstream;
        }

        try {
            JsonNode root = objectMapper.readTree(upstream.getBody().toString());

            if (root instanceof ObjectNode objectNode) {
                objectNode.remove("address");
                objectNode.remove("address2");
                objectNode.remove("city");
                objectNode.remove("state");
                objectNode.remove("zipcode");

                String updatedBody = objectMapper.writeValueAsString(objectNode);
                return ResponseEntity
                        .status(upstream.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedBody);
            }

            return upstream;
        } catch (Exception e) {
            // If transformation fails, preserve the original upstream response.
            return upstream;
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