package edu.cmu.bookstore.repository;

import edu.cmu.bookstore.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repository class responsible for database operations related to customers.
 *
 * This class uses {@link JdbcTemplate} to interact with the underlying
 * relational database and performs create and read operations on the
 * customers table.
 */
@Repository
public class CustomerRepository {

    /**
     * Spring JDBC helper used to execute SQL queries and updates.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Maps a database row from the customers table to a {@link Customer} object.
     */
    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setUserId(rs.getString("user_id"));
        customer.setName(rs.getString("name"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setAddress2(rs.getString("address2"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setZipcode(rs.getString("zipcode"));
        return customer;
    };

    /**
     * Constructs the repository with the required JDBC dependency.
     *
     * @param jdbcTemplate JDBC helper for executing SQL statements
     */
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Checks whether a customer with the given user identifier already exists.
     *
     * @param userId user identifier to check
     * @return true if a matching customer exists, otherwise false
     */
    public boolean existsByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM customers WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    /**
     * Inserts a new customer record into the customers table.
     *
     * This method captures the generated primary key using a
     * {@link GeneratedKeyHolder} and returns it to the caller.
     *
     * @param customer customer entity containing the values to insert
     * @return generated database identifier for the inserted customer
     */
    public long insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customers (user_id, name, phone, address, address2, city, state, zipcode)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getUserId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getAddress2());
            ps.setString(6, customer.getCity());
            ps.setString(7, customer.getState());
            ps.setString(8, customer.getZipcode());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();

        // Fail fast if the database insert succeeded but no generated key was returned.
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated customer id.");
        }

        return key.longValue();
    }

    /**
     * Finds a customer by internal numeric identifier.
     *
     * @param id internal identifier of the requested customer
     * @return an {@link Optional} containing the customer if found,
     *         or an empty Optional if no matching customer exists
     */
    public Optional<Customer> findById(Long id) {
        String sql = """
                SELECT id, user_id, name, phone, address, address2, city, state, zipcode
                FROM customers
                WHERE id = ?
                """;

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper, id);

        // Return an empty Optional when no matching customer exists.
        if (customers.isEmpty()) {
            return Optional.empty();
        }

        // Customer IDs are expected to be unique, so return the first matching record.
        return Optional.of(customers.get(0));
    }

    /**
     * Finds a customer by user identifier.
     *
     * @param userId user identifier associated with the requested customer
     * @return an {@link Optional} containing the customer if found,
     *         or an empty Optional if no matching customer exists
     */
    public Optional<Customer> findByUserId(String userId) {
        String sql = """
                SELECT id, user_id, name, phone, address, address2, city, state, zipcode
                FROM customers
                WHERE user_id = ?
                """;

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper, userId);

        // Return an empty Optional when no matching customer exists.
        if (customers.isEmpty()) {
            return Optional.empty();
        }

        // User IDs are expected to be unique, so return the first matching record.
        return Optional.of(customers.get(0));
    }
}