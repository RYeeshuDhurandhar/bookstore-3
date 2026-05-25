package edu.cmu.bookstore.service;

import edu.cmu.bookstore.exception.ConflictException;
import edu.cmu.bookstore.exception.NotFoundException;
import edu.cmu.bookstore.model.Customer;
import edu.cmu.bookstore.model.request.CreateCustomerRequest;
import edu.cmu.bookstore.repository.CustomerRepository;
import edu.cmu.bookstore.validation.CustomerValidator;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for customer-related business logic.
 *
 * This service handles:
 * - customer creation
 * - customer lookup by numeric ID
 * - customer lookup by userId
 *
 * It delegates persistence operations to {@link CustomerRepository}
 * and validation responsibilities to {@link CustomerValidator}.
 */
@Service
public class CustomerService {

    /**
     * Repository used for customer persistence and retrieval.
     */
    private final CustomerRepository customerRepository;

    /**
     * Validator used to enforce customer-related input rules.
     */
    private final CustomerValidator customerValidator;

    /**
     * Service used to publish customer registration events to Kafka.
     */
    private final KafkaProducerService kafkaProducerService;

    /**
     * Creates a service instance with required dependencies.
     *
     * @param customerRepository repository used for database operations
     * @param customerValidator validator used for request and parameter validation
     * @param kafkaProducerService service used to publish Kafka events
     */
    public CustomerService(CustomerRepository customerRepository,
                           CustomerValidator customerValidator,
                           KafkaProducerService kafkaProducerService) {
        this.customerRepository = customerRepository;
        this.customerValidator = customerValidator;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Creates a new customer after validating the request and ensuring
     * that the provided userId is unique.
     *
     * The method trims and normalizes incoming text fields before storing them.
     * If a customer with the same userId already exists, a conflict error is raised.
     *
     * @param request request payload containing customer creation data
     * @return the created customer including the generated ID
     * @throws ConflictException if the userId already exists
     */
    public Customer createCustomer(CreateCustomerRequest request) {
        // Validate the full customer creation request before processing.
        customerValidator.validateCreateRequest(request);

        // Trim the userId so uniqueness checks and persistence use a normalized value.
        String trimmedUserId = request.getUserId().trim();

        // Reject creation if another customer already exists with the same userId.
        if (customerRepository.existsByUserId(trimmedUserId)) {
            throw new ConflictException("This user ID already exists in the system.");
        }

        // Build the customer entity and normalize string fields before saving.
        Customer customer = new Customer();
        customer.setUserId(trimmedUserId);
        customer.setName(request.getName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setAddress(request.getAddress().trim());
        customer.setAddress2(request.getAddress2() == null ? null : request.getAddress2().trim());
        customer.setCity(request.getCity().trim());
        customer.setState(request.getState().trim().toUpperCase());
        customer.setZipcode(request.getZipcode().trim());

        // Insert the customer into the repository and capture the generated ID.
        long generatedId = customerRepository.insertCustomer(customer);
        customer.setId(generatedId);

        // Publish the "Customer Registered" domain event to Kafka.
        kafkaProducerService.publishCustomerRegisteredEvent(customer);

        // Return the fully constructed customer object.
        return customer;
    }

    /**
     * Retrieves a customer by numeric ID.
     *
     * The input ID is validated before querying the repository.
     * If no customer exists for the given ID, a not-found error is raised.
     *
     * @param id numeric identifier of the customer
     * @return the matching customer
     * @throws NotFoundException if no matching customer exists
     */
    public Customer getCustomerById(Long id) {
        // Validate the provided customer ID.
        customerValidator.validateCustomerId(id);

        // Look up the customer, or fail if it does not exist.
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
    }

    /**
     * Retrieves a customer by userId.
     *
     * The input userId is first normalized, then validated, and finally used
     * to query the repository. If no matching customer exists, a not-found
     * error is raised.
     *
     * @param userId user identifier, typically an email address
     * @return the matching customer
     * @throws NotFoundException if no matching customer exists
     */
    public Customer getCustomerByUserId(String userId) {
        // Normalize the userId before validation and lookup.
        String normalizedUserId = normalizeUserId(userId);

        // Validate the normalized userId query parameter.
        customerValidator.validateUserIdQuery(normalizedUserId);

        // Retrieve the customer by userId, or fail if not found.
        return customerRepository.findByUserId(normalizedUserId)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
    }

    /**
     * Normalizes the incoming userId string before validation and lookup.
     *
     * This method:
     * - returns null if the input is null
     * - trims surrounding whitespace
     * - replaces spaces with '+' characters
     *
     * The space-to-plus replacement helps handle cases where '+' in an email-like
     * identifier may have been decoded into a space when passed through query parameters.
     *
     * @param userId raw incoming userId
     * @return normalized userId, or null if the input was null
     */
    private String normalizeUserId(String userId) {
        // Preserve null values so validation can handle them appropriately.
        if (userId == null) {
            return null;
        }

        // Normalize surrounding whitespace and repair '+' values that may have become spaces.
        return userId.trim().replace(' ', '+');
    }
}