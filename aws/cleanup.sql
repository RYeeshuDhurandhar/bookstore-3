-- -----------------------------------------------------------------------------
-- Cleanup script for the Bookstore application.
--
-- This script removes all existing data from the application tables
-- inside the bookstore database. It is useful for resetting the
-- database to a clean state before re-running seed scripts or tests.
-- -----------------------------------------------------------------------------

-- Select the bookstore database before performing delete operations.
USE bookstore;

-- Remove all rows from the customers table.
DELETE FROM customers;

-- Remove all rows from the books table.
DELETE FROM books;