-- -----------------------------------------------------------------------------
-- Schema definition for the Bookstore application.
--
-- This script creates the core relational tables required by the application:
-- 1. books     - stores book metadata, inventory information, and summaries
-- 2. customers - stores customer identity and address information
--
-- The IF NOT EXISTS clause is used so that the script can be executed
-- multiple times without failing when the tables already exist.
-- -----------------------------------------------------------------------------

-- Create and populate the Books Database
CREATE DATABASE IF NOT EXISTS book_db;
USE book_db;

-- Create the books table used to store all book-related information.
CREATE TABLE IF NOT EXISTS books (
    isbn VARCHAR(32) PRIMARY KEY,          -- Unique ISBN identifier for each book
    title VARCHAR(255) NOT NULL,           -- Title of the book
    author VARCHAR(255) NOT NULL,          -- Author of the book
    description TEXT NOT NULL,             -- Description provided for the book
    genre VARCHAR(100) NOT NULL,           -- Genre/category of the book
    price DECIMAL(10, 2) NOT NULL,         -- Price stored with up to 2 decimal places
    quantity INT NOT NULL,                 -- Available inventory quantity
    summary TEXT NULL                      -- Optional LLM-generated summary
);

-- Create and populate the Customers Database
CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

-- Create the customers table used to store customer information.
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- Auto-generated unique customer ID
    user_id VARCHAR(255) NOT NULL UNIQUE,          -- Unique user identifier, typically email
    name VARCHAR(255) NOT NULL,                    -- Full customer name
    phone VARCHAR(50) NOT NULL,                    -- Customer phone number
    address VARCHAR(255) NOT NULL,                 -- Primary address line
    address2 VARCHAR(255) NULL,                    -- Optional secondary address line
    city VARCHAR(100) NOT NULL,                    -- City portion of the address
    state CHAR(2) NOT NULL,                        -- Two-letter US state abbreviation
    zipcode VARCHAR(20) NOT NULL                   -- ZIP or postal code
);
