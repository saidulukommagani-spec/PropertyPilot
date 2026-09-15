-- V4__payment_and_notification_tables.sql

-- Migration for Payment and Notification Tables

-- Create Payment Table
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Notification Table
CREATE TABLE notification (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    notification_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Indexes
CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_notification_user_id ON notification(user_id);

-- Seed Data for Payment Table
INSERT INTO payment (user_id, amount, status) VALUES
(1, 100.00, 'Completed'),
(2, 50.00, 'Pending');

-- Seed Data for Notification Table
INSERT INTO notification (user_id, message) VALUES
(1, 'Your payment of $100.00 has been processed.'),
(2, 'You have a new notification.');