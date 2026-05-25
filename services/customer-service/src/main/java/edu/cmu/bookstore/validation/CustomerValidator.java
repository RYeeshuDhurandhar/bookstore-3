package edu.cmu.bookstore.validation;

import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.model.request.CreateCustomerRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator class responsible for checking customer-related request data.
 *
 * This class enforces required fields and basic business rules for
 * customer creation and retrieval operations. Validation failures are
 * reported through {@link BadRequestException}.
 */
@Component
public class CustomerValidator {

    /**
     * Regular expression used to validate user identifiers expected
     * to be email addresses.
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Validates the request body for customer creation.
     *
     * @param request request payload for customer creation
     */
    public void validateCreateRequest(CreateCustomerRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }

        validateUserId(request.getUserId());
        validateName(request.getName());
        validatePhone(request.getPhone());
        validateAddress(request.getAddress());
        validateCity(request.getCity());
        validateState(request.getState());
        validateZipcode(request.getZipcode());
    }

    /**
     * Validates the numeric customer identifier used in path parameters.
     *
     * @param id customer identifier
     */
    public void validateCustomerId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Customer id must be a positive number.");
        }
    }

    /**
     * Validates the userId query parameter used for customer lookup.
     *
     * @param userId user identifier expected to be a valid email address
     */
    public void validateUserIdQuery(String userId) {
        if (isBlank(userId)) {
            throw new BadRequestException("userId is required.");
        }

        if (!EMAIL_PATTERN.matcher(userId.trim()).matches()) {
            throw new BadRequestException("userId must be a valid email address.");
        }
    }

    /**
     * Validates the user identifier field.
     *
     * @param userId customer user identifier
     */
    private void validateUserId(String userId) {
        if (isBlank(userId)) {
            throw new BadRequestException("userId is required.");
        }

        if (!EMAIL_PATTERN.matcher(userId.trim()).matches()) {
            throw new BadRequestException("userId must be a valid email address.");
        }
    }

    /**
     * Validates the customer name field.
     *
     * @param name customer name
     */
    private void validateName(String name) {
        if (isBlank(name)) {
            throw new BadRequestException("Name is required.");
        }
    }

    /**
     * Validates the phone field.
     *
     * @param phone customer phone number
     */
    private void validatePhone(String phone) {
        if (isBlank(phone)) {
            throw new BadRequestException("Phone is required.");
        }
    }

    /**
     * Validates the primary address field.
     *
     * @param address customer address
     */
    private void validateAddress(String address) {
        if (isBlank(address)) {
            throw new BadRequestException("Address is required.");
        }
    }

    /**
     * Validates the city field.
     *
     * @param city customer city
     */
    private void validateCity(String city) {
        if (isBlank(city)) {
            throw new BadRequestException("City is required.");
        }
    }

    /**
     * Validates the state field.
     *
     * The state must be present and must correspond to a valid
     * two-letter US state abbreviation.
     *
     * @param state customer state
     */
    private void validateState(String state) {
        if (isBlank(state)) {
            throw new BadRequestException("State is required.");
        }

        if (!UsStates.isValid(state)) {
            throw new BadRequestException("State must be a valid 2-letter US abbreviation.");
        }
    }

    /**
     * Validates the ZIP code field.
     *
     * @param zipcode customer ZIP code
     */
    private void validateZipcode(String zipcode) {
        if (isBlank(zipcode)) {
            throw new BadRequestException("Zipcode is required.");
        }
    }

    /**
     * Checks whether a string is null, empty, or only whitespace.
     *
     * @param value string value to check
     * @return true if the value is blank, otherwise false
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}