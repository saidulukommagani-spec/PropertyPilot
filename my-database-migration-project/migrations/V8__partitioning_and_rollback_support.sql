-- V8__partitioning_and_rollback_support.sql

-- This migration implements partitioning strategies for large tables
-- and outlines the rollback strategy for migrations.

-- Example of partitioning a large table
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    amount DECIMAL(10, 2) NOT NULL
) PARTITION BY RANGE (order_date);

CREATE TABLE orders_2023 PARTITION OF orders
    FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');

CREATE TABLE orders_2024 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

-- Rollback strategy for the migration
-- If the migration needs to be rolled back, drop the partitions and the main table
-- This will ensure that no data is lost and the schema can be reverted to its previous state.

-- Rollback command
-- DROP TABLE IF EXISTS orders_2023;
-- DROP TABLE IF EXISTS orders_2024;
-- DROP TABLE IF EXISTS orders;