-- Combine setup and seeding for the containerized DB
CREATE DATABASE IF NOT EXISTS book_db;
CREATE DATABASE IF NOT EXISTS customer_db;

USE book_db;
CREATE TABLE IF NOT EXISTS books (
    isbn VARCHAR(32) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    summary TEXT NULL
);

INSERT INTO books (isbn, title, author, description, genre, price, quantity, summary)
VALUES
('9780134685991', 'Effective Java', 'Joshua Bloch', 'A practical guide to best practices in Java programming.', 'non-fiction', 45.99, 10, 'A well-known guide to writing robust and maintainable Java code.');

USE customer_db;
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    address2 VARCHAR(255) NULL,
    city VARCHAR(100) NOT NULL,
    state CHAR(2) NOT NULL,
    zipcode VARCHAR(20) NOT NULL
);

INSERT INTO customers (user_id, name, phone, address, address2, city, state, zipcode)
VALUES
('sampleuser@gmail.com', 'Sample User', '+14125550111', '5000 Forbes Ave', 'Apt 1', 'Pittsburgh', 'PA', '15213');
