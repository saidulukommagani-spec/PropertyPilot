-- V5__marketplace_and_complaint_tables.sql

-- Migration for Marketplace and Complaint Tables

-- Create Marketplace Table
CREATE TABLE marketplace (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Complaint Table
CREATE TABLE complaint (
    id SERIAL PRIMARY KEY,
    marketplace_id INT NOT NULL,
    user_id INT NOT NULL,
    complaint_text TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (marketplace_id) REFERENCES marketplace(id) ON DELETE CASCADE
);

-- Create Indexes
CREATE INDEX idx_marketplace_name ON marketplace(name);
CREATE INDEX idx_complaint_status ON complaint(status);
CREATE INDEX idx_complaint_marketplace ON complaint(marketplace_id);

-- Seed Data for Marketplace
INSERT INTO marketplace (name, description) VALUES
('Marketplace A', 'Description for Marketplace A'),
('Marketplace B', 'Description for Marketplace B');

-- Seed Data for Complaints
INSERT INTO complaint (marketplace_id, user_id, complaint_text) VALUES
(1, 1, 'Complaint for Marketplace A by User 1'),
(2, 2, 'Complaint for Marketplace B by User 2');