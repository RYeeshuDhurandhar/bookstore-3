package edu.cmu.bookstore.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Request model for creating a new book.
 *
 * This class represents the JSON payload accepted by the book creation
 * endpoint. It is used to deserialize client input into a Java object
 * before validation and service-layer processing.
 */
public class CreateBookRequest {

    /**
     * International Standard Book Number for the book.
     *
     * This field is mapped from the JSON property "ISBN" to match
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
     * This field is mapped from the JSON property "Author" to match
     * the API contract expected by the assignment.
     */
    @JsonProperty("Author")
    private String author;

    /**
     * Short description or summary text provided for the book.
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
     * Default constructor required for JSON deserialization.
     */
    public CreateBookRequest() {
    }

    /**
     * Returns the ISBN provided in the request.
     *
     * @return book ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN from the request payload.
     *
     * @param isbn book ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Returns the title provided in the request.
     *
     * @return book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title from the request payload.
     *
     * @param title book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author provided in the request.
     *
     * @return author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author from the request payload.
     *
     * @param author author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the description provided in the request.
     *
     * @return book description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description from the request payload.
     *
     * @param description book description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the genre provided in the request.
     *
     * @return book genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre from the request payload.
     *
     * @param genre book genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns the price provided in the request.
     *
     * @return book price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price from the request payload.
     *
     * @param price book price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the quantity provided in the request.
     *
     * @return available quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity from the request payload.
     *
     * @param quantity available quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}