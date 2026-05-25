-- -----------------------------------------------------------------------------
-- Seed data for the Bookstore application.
--
-- This script inserts initial sample records into the books and customers
-- tables so the application can be tested with known data.
-- -----------------------------------------------------------------------------

-- Insert a sample book record.
USE book_db;

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
),
('978-0321815736', 'Software Architecture in Practice', 'Bass, L.', '...', 'Software', 45.00, 10, NULL),
('978-0-321-55268-6', 'Documenting Software Architectures Second Edition', 'Clements, P. et al', '...', 'Software', 50.00, 8, NULL),
('9780133065107', 'Some Title', 'Some Author', '...', 'Software', 42.00, 5, NULL),
('978-0395489321', 'Some Title', 'Some Author', '...', 'Fiction', 18.00, 7, NULL),
('978-0544174221', 'Some Title', 'Some Author', '...', 'Fantasy', 20.00, 6, NULL);

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