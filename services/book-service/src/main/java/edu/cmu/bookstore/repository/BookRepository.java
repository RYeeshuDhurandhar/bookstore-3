package edu.cmu.bookstore.repository;

import edu.cmu.bookstore.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class responsible for performing database operations
 * related to books using Spring's {@link JdbcTemplate}.
 */
@Repository
public class BookRepository {

    /**
     * JdbcTemplate used for executing SQL queries and updates.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper used to convert a database row into a {@link Book} object.
     */
    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setGenre(rs.getString("genre"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setQuantity(rs.getInt("quantity"));
        book.setSummary(rs.getString("summary"));
        return book;
    };

    /**
     * Creates the repository with the required JdbcTemplate dependency.
     *
     * @param jdbcTemplate template used for database access
     */
    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Checks whether a book with the given ISBN exists in the database.
     *
     * @param isbn ISBN to check
     * @return true if a matching book exists, otherwise false
     */
    public boolean existsByIsbn(String isbn) {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, isbn);
        return count != null && count > 0;
    }

    /**
     * Inserts a new book record into the database.
     *
     * @param book book entity to insert
     */
    public void insertBook(Book book) {
        String sql = """
                INSERT INTO books (isbn, title, author, description, genre, price, quantity, summary)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
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
     * Updates the non-summary fields of an existing book.
     *
     * @param book book entity containing updated values
     * @return number of rows affected
     */
    public int updateBook(Book book) {
        String sql = """
                UPDATE books
                SET title = ?, author = ?, description = ?, genre = ?, price = ?, quantity = ?
                WHERE isbn = ?
                """;

        return jdbcTemplate.update(
                sql,
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getGenre(),
                book.getPrice(),
                book.getQuantity(),
                book.getIsbn()
        );
    }

    /**
     * Updates only the summary field of a book identified by ISBN.
     *
     * @param isbn    ISBN of the book to update
     * @param summary generated or updated summary text
     * @return number of rows affected
     */
    public int updateSummary(String isbn, String summary) {
        String sql = "UPDATE books SET summary = ? WHERE isbn = ?";
        return jdbcTemplate.update(sql, summary, isbn);
    }

    /**
     * Finds a single book by its ISBN.
     *
     * @param isbn ISBN of the requested book
     * @return optional containing the found book, or empty if not found
     */
    public Optional<Book> findByIsbn(String isbn) {
        String sql = """
                SELECT isbn, title, author, description, genre, price, quantity, summary
                FROM books
                WHERE isbn = ?
                """;

        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, isbn);

        if (books.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(books.get(0));
    }

    /**
     * Retrieves all books from the database.
     *
     * @return list of all books
     */
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Book book = new Book();

            book.setIsbn(rs.getString("isbn"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setDescription(rs.getString("description"));
            book.setGenre(rs.getString("genre"));
            book.setPrice(rs.getBigDecimal("price"));
            book.setQuantity(rs.getInt("quantity"));

            // Safely attempts to read the summary column in case it is absent
            // in some database states or migrations.
            try {
                book.setSummary(rs.getString("summary"));
            } catch (Exception ignored) {
                book.setSummary(null);
            }

            return book;
        });
    }

}