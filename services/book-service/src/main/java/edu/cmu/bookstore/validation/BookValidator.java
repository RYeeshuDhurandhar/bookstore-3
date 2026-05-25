package edu.cmu.bookstore.validation;

import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.model.request.CreateBookRequest;
import edu.cmu.bookstore.model.request.UpdateBookRequest;
import edu.cmu.bookstore.util.PriceUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validator class responsible for checking book-related request data.
 *
 * This class enforces required fields and basic business rules for
 * both create and update book operations. Validation failures are
 * reported through {@link BadRequestException}.
 */
@Component
public class BookValidator {

    /**
     * Validates the request body for book creation.
     *
     * @param request request payload for book creation
     */
    public void validateCreateRequest(CreateBookRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }

        validateIsbn(request.getIsbn());
        validateTitle(request.getTitle());
        validateAuthor(request.getAuthor());
        validateDescription(request.getDescription());
        validateGenre(request.getGenre());
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
    }

    /**
     * Validates the request body for book update.
     *
     * @param request request payload for book update
     */
    public void validateUpdateRequest(UpdateBookRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }

        validateIsbn(request.getIsbn());
        validateTitle(request.getTitle());
        validateAuthor(request.getAuthor());
        validateDescription(request.getDescription());
        validateGenre(request.getGenre());
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
    }

    /**
     * Validates the ISBN provided in the request path.
     *
     * @param isbn ISBN from the path variable
     */
    public void validatePathIsbn(String isbn) {
        if (isBlank(isbn)) {
            throw new BadRequestException("ISBN is required.");
        }
    }

    /**
     * Validates the ISBN field.
     *
     * @param isbn book ISBN
     */
    private void validateIsbn(String isbn) {
        if (isBlank(isbn)) {
            throw new BadRequestException("ISBN is required.");
        }
    }

    /**
     * Validates the title field.
     *
     * @param title book title
     */
    private void validateTitle(String title) {
        if (isBlank(title)) {
            throw new BadRequestException("Title is required.");
        }
    }

    /**
     * Validates the author field.
     *
     * @param author book author
     */
    private void validateAuthor(String author) {
        if (isBlank(author)) {
            throw new BadRequestException("Author is required.");
        }
    }

    /**
     * Validates the description field.
     *
     * @param description book description
     */
    private void validateDescription(String description) {
        if (isBlank(description)) {
            throw new BadRequestException("Description is required.");
        }
    }

    /**
     * Validates the genre field.
     *
     * @param genre book genre
     */
    private void validateGenre(String genre) {
        if (isBlank(genre)) {
            throw new BadRequestException("Genre is required.");
        }
    }

    /**
     * Validates the price field.
     *
     * The price must be present, non-negative, and have at most two decimal places.
     *
     * @param price book price
     */
    private void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new BadRequestException("Price is required.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Price must be non-negative.");
        }

        if (!PriceUtil.hasAtMostTwoDecimalPlaces(price)) {
            throw new BadRequestException("Price must have at most two decimal places.");
        }
    }

    /**
     * Validates the quantity field.
     *
     * @param quantity available inventory quantity
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new BadRequestException("Quantity is required.");
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