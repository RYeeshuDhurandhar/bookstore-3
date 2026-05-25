-- -----------------------------------------------------------------------------
-- Seed data script for the Bookstore application.
--
-- This script selects the bookstore database and inserts sample rows
-- into the books and customers tables for testing and demonstration.
-- -----------------------------------------------------------------------------

-- Select the bookstore database before inserting seed records.
USE bookstore;

-- Insert a sample book record.
INSERT INTO books (isbn, title, author, description, genre, price, quantity, summary)
VALUES
(
    '9780134685991',                                              -- ISBN
    'Effective Java',                                             -- Title
    'Joshua Bloch',                                               -- Author
    'A practical guide to best practices in Java programming.',   -- Description
    'non-fiction',                                                -- Genre
    45.99,                                                        -- Price
    10,                                                           -- Quantity
    'A well-known guide to writing robust and maintainable Java code.' -- Summary
);

-- Insert a sample customer record.
INSERT INTO customers (user_id, name, phone, address, address2, city, state, zipcode)
VALUES
(
    'sampleuser@gmail.com',  -- User identifier
    'Sample User',           -- Customer name
    '+14125550111',          -- Phone number
    '5000 Forbes Ave',       -- Primary address line
    'Apt 1',                 -- Secondary address line
    'Pittsburgh',            -- City
    'PA',                    -- State
    '15213'                  -- ZIP code
);