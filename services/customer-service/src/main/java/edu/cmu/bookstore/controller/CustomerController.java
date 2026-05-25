package edu.cmu.bookstore.controller;

import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.model.Customer;
import edu.cmu.bookstore.model.request.CreateCustomerRequest;
import edu.cmu.bookstore.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * REST controller responsible for handling customer-related HTTP endpoints.
 *
 * This controller supports:
 * - creating a new customer
 * - retrieving a customer by numeric ID
 * - retrieving a customer by userId query parameter
 *
 * Business logic is delegated to {@link CustomerService}, while this class
 * handles request parsing, validation at the HTTP layer, and response creation.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    /**
     * Service layer dependency used for customer-related operations.
     */
    private final CustomerService customerService;

    /**
     * Creates a controller with the required customer service dependency.
     *
     * @param customerService service used to process customer operations
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Creates a new customer.
     *
     * This endpoint accepts customer details in the request body, delegates
     * creation to the service layer, and returns:
     * - HTTP 201 Created
     * - a Location header pointing to the created resource
     * - the created customer in the response body
     *
     * @param request request payload containing customer creation data
     * @return HTTP response with the created customer
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CreateCustomerRequest request) {
        // Create the customer using the service layer.
        Customer createdCustomer = customerService.createCustomer(request);

        // Add a Location header so the client knows where the created resource can be accessed.
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/customers/" + createdCustomer.getId());

        // Return the created customer with HTTP 201 Created.
        return new ResponseEntity<>(createdCustomer, headers, HttpStatus.CREATED);
    }

    /**
     * Retrieves a customer by numeric ID.
     *
     * The customer ID is taken from the path variable and used to fetch the
     * corresponding customer from the service layer.
     *
     * @param id numeric identifier of the customer
     * @return HTTP response containing the matching customer
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") Long id) {
        // Fetch the customer by ID from the service layer.
        Customer customer = customerService.getCustomerById(id);

        // Return the customer with HTTP 200 OK.
        return ResponseEntity.ok(customer);
    }

    /**
     * Retrieves a customer by the userId query parameter.
     *
     * The method reads the raw query string directly from the request instead of
     * relying on automatic parameter binding. This helps preserve values such as
     * encoded email addresses and gives explicit control over query parsing.
     *
     * Expected query format:
     * /customers?userId=<value>
     *
     * @param request incoming HTTP servlet request
     * @return HTTP response containing the matching customer
     * @throws BadRequestException if the query string is missing or does not contain userId
     */
    @GetMapping
    public ResponseEntity<Customer> getCustomerByUserId(HttpServletRequest request) {
        // Read the raw query string from the incoming HTTP request.
        String rawQuery = request.getQueryString();

        // Reject the request if no query string was provided.
        if (rawQuery == null || rawQuery.isBlank()) {
            throw new BadRequestException("userId is required.");
        }

        // Extract and decode the userId parameter value.
        String userId = extractUserId(rawQuery);

        // Fetch the customer matching the provided userId.
        Customer customer = customerService.getCustomerByUserId(userId);

        // Return the customer with HTTP 200 OK.
        return ResponseEntity.ok(customer);
    }

    /**
     * Extracts the value of the userId query parameter from the raw query string.
     *
     * The raw query is split on '&' to support multiple parameters. Each parameter
     * is then split on the first '=' character to separate key and value. If the
     * key is "userId", the value is URL-decoded using UTF-8 and returned.
     *
     * Example:
     * userId=star%2Blord%40gmail.com
     *
     * @param rawQuery raw query string from the HTTP request
     * @return decoded value of the userId parameter
     * @throws BadRequestException if userId is not found in the query string
     */
    private String extractUserId(String rawQuery) {
        // Split the full query string into individual key-value parameter parts.
        for (String part : rawQuery.split("&")) {
            // Find the first '=' character separating key and value.
            int idx = part.indexOf('=');

            // Skip malformed query parts that do not contain a valid key=value structure.
            if (idx <= 0) {
                continue;
            }

            // Extract the parameter name and raw parameter value.
            String key = part.substring(0, idx);
            String value = part.substring(idx + 1);

            // If this is the userId parameter, decode and return its value.
            if ("userId".equals(key)) {
                return URLDecoder.decode(value, StandardCharsets.UTF_8);
            }
        }

        // If userId was not present anywhere in the query string, reject the request.
        throw new BadRequestException("userId is required.");
    }
}