package edu.cmu.bookstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a related book recommended by the external engine.
 *
 * This class corresponds to the response structure of the external
 * recommendation service and the /books/{isbn}/related-books endpoint.
 */
public class RelatedBook {

    /**
     * International Standard Book Number for the related book.
     */
    @JsonProperty("isbn")
    private String isbn;

    /**
     * Title of the related book.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Author of the related book.
     */
    @JsonProperty("authors")
    private String author;

    /**
     * Default constructor for Jackson deserialization.
     */
    public RelatedBook() {
    }

    /**
     * Constructs a related book with all fields initialized.
     *
     * @param isbn book ISBN
     * @param title book title
     * @param author book author
     */
    public RelatedBook(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
