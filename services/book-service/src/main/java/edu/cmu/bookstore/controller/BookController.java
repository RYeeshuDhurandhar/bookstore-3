package edu.cmu.bookstore.controller;

import edu.cmu.bookstore.model.Book;
import edu.cmu.bookstore.model.BookDetailsResponse;
import edu.cmu.bookstore.model.BookResponse;
import edu.cmu.bookstore.model.RelatedBook;
import edu.cmu.bookstore.model.request.CreateBookRequest;
import edu.cmu.bookstore.model.request.UpdateBookRequest;
import edu.cmu.bookstore.service.BookService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for handling all HTTP requests related to books.
 * It delegates business logic to {@link BookService} and converts service-layer
 * objects into API response models.
 */
@RestController
@RequestMapping("/books")
public class BookController {

    /**
     * Service used to perform book-related business operations.
     */
    private final BookService bookService;

    /**
     * Creates the controller with the required {@link BookService} dependency.
     *
     * @param bookService service layer bean for book operations
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Creates a new book from the request payload.
     * Returns HTTP 201 Created and sets the Location header
     * to the URI of the newly created book resource.
     *
     * @param request request body containing book creation data
     * @return created book response with Location header
     */
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest request) {
        Book createdBook = bookService.createBook(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/books/" + createdBook.getIsbn());

        return new ResponseEntity<>(BookResponse.fromBook(createdBook), headers, HttpStatus.CREATED);
    }

    /**
     * Updates an existing book identified by its ISBN.
     *
     * @param isbn    ISBN of the book to update
     * @param request request body containing updated book fields
     * @return updated book response
     */
    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("isbn") String isbn,
                                                   @RequestBody UpdateBookRequest request) {
        Book updatedBook = bookService.updateBook(isbn, request);
        return ResponseEntity.ok(BookResponse.fromBook(updatedBook));
    }

    /**
     * Retrieves all books available in the system.
     *
     * @return list of all books converted to response DTOs
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks()
                .stream()
                .map(BookResponse::fromBook)
                .toList();

        return ResponseEntity.ok(books);
    }

    /**
     * Retrieves detailed information for a single book by ISBN.
     *
     * @param isbn ISBN of the requested book
     * @return detailed book response
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<BookDetailsResponse> getBookByIsbn(@PathVariable("isbn") String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(BookDetailsResponse.fromBook(book));
    }

    /**
     * Alternative endpoint for retrieving detailed book information by ISBN.
     * This provides the same functionality as {@code GET /books/{isbn}} but
     * under a different URL pattern.
     *
     * @param isbn ISBN of the requested book
     * @return detailed book response
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDetailsResponse> getBookByAlternativePath(@PathVariable("isbn") String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(BookDetailsResponse.fromBook(book));
    }

    /**
     * Retrieves books related to the given ISBN.
     * Returns HTTP 204 No Content when no related books are found.
     *
     * @param isbn ISBN of the base book
     * @return list of related books, or no-content response if empty
     */
    @GetMapping("/{isbn}/related-books")
    public ResponseEntity<List<RelatedBook>> getRelatedBooks(@PathVariable("isbn") String isbn) {
        List<RelatedBook> relatedBooks = bookService.getRelatedBooks(isbn);

        if (relatedBooks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(relatedBooks);
    }
}