package edu.cmu.bookstore.service;

import edu.cmu.bookstore.exception.BadRequestException;
import edu.cmu.bookstore.exception.ConflictException;
import edu.cmu.bookstore.exception.NotFoundException;
import edu.cmu.bookstore.model.Book;
import edu.cmu.bookstore.model.RelatedBook;
import edu.cmu.bookstore.model.request.CreateBookRequest;
import edu.cmu.bookstore.model.request.UpdateBookRequest;
import edu.cmu.bookstore.repository.BookRepository;
import edu.cmu.bookstore.util.CircuitBreaker;
import edu.cmu.bookstore.validation.BookValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service class that contains the core business logic for book-related operations.
 * It handles validation, persistence, summary generation, and recommendation service access.
 */
@Service
public class BookService {

    /**
     * Repository used for book database operations.
     */
    private final BookRepository bookRepository;

    /**
     * Validator used to validate book-related requests and ISBN values.
     */
    private final BookValidator bookValidator;

    /**
     * Service used to generate LLM-based book summaries.
     */
    private final LlmService llmService;

    /**
     * Circuit breaker used to protect recommendation service calls.
     */
    private final CircuitBreaker circuitBreaker;

    /**
     * RestTemplate configured for calling the external recommendation service.
     */
    private final RestTemplate recommendationRestTemplate;

    /**
     * Base URL of the external recommendation service.
     * Uses the configured environment property if present, otherwise falls back to the default value.
     */
    @Value("${RECOMMENDATION_SERVICE_URL:http://100.51.187.149}")
    private String recommendationUrl;

    /**
     * Creates the service with all required dependencies and configures
     * the RestTemplate timeouts for outbound recommendation service calls.
     *
     * @param bookRepository repository for database access
     * @param bookValidator validator for incoming requests
     * @param llmService service for summary generation
     * @param circuitBreaker circuit breaker for recommendation calls
     * @param restTemplateBuilder builder used to configure RestTemplate
     */
    public BookService(BookRepository bookRepository,
                       BookValidator bookValidator,
                       LlmService llmService,
                       CircuitBreaker circuitBreaker,
                       RestTemplateBuilder restTemplateBuilder) {
        this.bookRepository = bookRepository;
        this.bookValidator = bookValidator;
        this.llmService = llmService;
        this.circuitBreaker = circuitBreaker;
        this.recommendationRestTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    /**
     * Creates a new book after validating the request and ensuring the ISBN is unique.
     * It also attempts to generate and persist a summary using the LLM service,
     * but summary generation failure does not fail book creation.
     *
     * @param request request containing new book details
     * @return created book entity
     */
    public Book createBook(CreateBookRequest request) {
        bookValidator.validateCreateRequest(request);

        String isbn = request.getIsbn().trim();

        if (bookRepository.existsByIsbn(isbn)) {
            throw new ConflictException("This ISBN already exists in the system.");
        }

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setDescription(request.getDescription().trim());
        book.setGenre(request.getGenre().trim());
        book.setPrice(request.getPrice());
        book.setQuantity(request.getQuantity());
        book.setSummary(null);

        bookRepository.insertBook(book);

        // Summary generation is best-effort and should not prevent book creation.
        try {
            String summary = llmService.generateSummary(book);
            if (summary != null && !summary.trim().isEmpty()) {
                bookRepository.updateSummary(book.getIsbn(), summary);
                book.setSummary(summary);
            }
        } catch (Exception ignored) {
        }

        return book;
    }

    /**
     * Updates an existing book after validating both the path ISBN and request body.
     * The ISBN in the request body must match the ISBN in the URL path.
     *
     * @param pathIsbn ISBN provided in the request path
     * @param request request containing updated book data
     * @return updated book entity reloaded from the database
     */
    public Book updateBook(String pathIsbn, UpdateBookRequest request) {
        bookValidator.validatePathIsbn(pathIsbn);
        bookValidator.validateUpdateRequest(request);

        String trimmedPathIsbn = pathIsbn.trim();
        String bodyIsbn = request.getIsbn().trim();

        if (!trimmedPathIsbn.equals(bodyIsbn)) {
            throw new BadRequestException("ISBN in path and body must match.");
        }

        Book existingBook = bookRepository.findByIsbn(trimmedPathIsbn)
                .orElseThrow(() -> new NotFoundException("Book not found."));

        existingBook.setTitle(request.getTitle().trim());
        existingBook.setAuthor(request.getAuthor().trim());
        existingBook.setDescription(request.getDescription().trim());
        existingBook.setGenre(request.getGenre().trim());
        existingBook.setPrice(request.getPrice());
        existingBook.setQuantity(request.getQuantity());

        bookRepository.updateBook(existingBook);

        return bookRepository.findByIsbn(trimmedPathIsbn)
                .orElseThrow(() -> new NotFoundException("Book not found."));
    }

    /**
     * Retrieves a single book by ISBN.
     * If the book does not already have a summary, it attempts to generate one
     * and store it in the database. Summary generation failure does not fail the request.
     *
     * @param isbn ISBN of the requested book
     * @return matching book entity
     */
    public Book getBookByIsbn(String isbn) {
        bookValidator.validatePathIsbn(isbn);

        Book book = bookRepository.findByIsbn(isbn.trim())
                .orElseThrow(() -> new NotFoundException("Book not found."));

        // Generate a summary lazily if it is missing.
        if (book.getSummary() == null || book.getSummary().trim().isEmpty()) {
            try {
                String summary = llmService.generateSummary(book);
                if (summary != null && !summary.trim().isEmpty()) {
                    bookRepository.updateSummary(book.getIsbn(), summary);
                    book.setSummary(summary);
                }
            } catch (Exception ignored) {
            }
        }

        return book;
    }

    /**
     * Retrieves all books from the repository.
     *
     * @return list of all stored books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves related books for the given ISBN by calling the external recommendation service.
     * This method is guarded by a circuit breaker to avoid repeated calls when the remote service
     * is failing or timing out.
     *
     * @param isbn ISBN for which related books are requested
     * @return list of related books, or an empty list if none are returned
     */
    public List<RelatedBook> getRelatedBooks(String isbn) {
        bookValidator.validatePathIsbn(isbn);

        // Capture the state before checking whether a call is allowed.
        CircuitBreaker.State stateBeforeCheck = circuitBreaker.getState();

        if (!circuitBreaker.isCallAllowed()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Circuit is open");
        }

        // Detect whether this request is the retry attempt performed after the open window expires.
        boolean retryAfterOpenWindow =
                stateBeforeCheck == CircuitBreaker.State.OPEN &&
                circuitBreaker.getState() == CircuitBreaker.State.HALF_OPEN;

        String url = String.format("%s/recommended-titles/isbn/%s", recommendationUrl, isbn.trim());

        try {
            RelatedBook[] results = recommendationRestTemplate.getForObject(url, RelatedBook[].class);

            // Successful call closes or keeps the circuit healthy.
            circuitBreaker.recordSuccess();

            if (results == null || results.length == 0) {
                return Collections.emptyList();
            }

            return Arrays.asList(results);
        } catch (ResourceAccessException e) {
            // Timeout or connection-level failure counts as a circuit breaker failure.
            circuitBreaker.recordFailure();

            if (retryAfterOpenWindow) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Circuit is open");
            }

            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Call to recommendation service timed out.");
        } catch (Exception e) {
            // Any other failure is recorded and surfaced as an internal server error.
            circuitBreaker.recordFailure();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling recommendation service.");
        }
    }
}