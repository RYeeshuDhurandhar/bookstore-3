package edu.cmu.bookstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Response model containing detailed information about a book.
 *
 * This class is used when returning a full book record to clients,
 * including descriptive information, inventory quantity, and the
 * generated summary.
 */
public class BookDetailsResponse {

    /**
     * International Standard Book Number for the book.
     *
     * This field is mapped to the JSON property "ISBN" to match
     * the API contract expected by the assignment.
     */
    @JsonProperty("ISBN")
    private String isbn;

    /**
     * Title of the book.
     */
    private String title;

    /**
     * Author name for the book.
     *
     * This field is mapped to the JSON property "Author" to match
     * the API contract expected by the assignment.
     */
    @JsonProperty("Author")
    private String author;

    /**
     * Description provided for the book.
     */
    private String description;

    /**
     * Genre associated with the book.
     */
    private String genre;

    /**
     * Price of the book.
     */
    private BigDecimal price;

    /**
     * Available inventory quantity for the book.
     */
    private Integer quantity;

    /**
     * Generated summary associated with the book.
     */
    private String summary;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public BookDetailsResponse() {
    }

    /**
     * Constructs a detailed book response with all fields initialized.
     *
     * @param isbn ISBN of the book
     * @param title title of the book
     * @param author author of the book
     * @param description description of the book
     * @param genre genre of the book
     * @param price price of the book
     * @param quantity available quantity of the book
     * @param summary generated summary of the book
     */
    public BookDetailsResponse(String isbn, String title, String author, String description,
                               String genre, BigDecimal price, Integer quantity, String summary) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.quantity = quantity;
        this.summary = summary;
    }

    /**
     * Creates a detailed response object from a {@link Book} domain model.
     *
     * @param book domain model containing book information
     * @return populated detailed response object
     */
    public static BookDetailsResponse fromBook(Book book) {
        return new BookDetailsResponse(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getGenre(),
                book.getPrice(),
                book.getQuantity(),
                book.getSummary()
        );
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return book ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn book ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Returns the title of the book.
     *
     * @return book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of the book.
     *
     * @return author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the description of the book.
     *
     * @return book description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     *
     * @param description book description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the genre of the book.
     *
     * @return book genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the book.
     *
     * @param genre book genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns the price of the book.
     *
     * @return book price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price of the book.
     *
     * @param price book price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the available quantity of the book.
     *
     * @return available quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the available quantity of the book.
     *
     * @param quantity available quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the generated summary of the book.
     *
     * @return book summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the generated summary of the book.
     *
     * @param summary book summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }
}