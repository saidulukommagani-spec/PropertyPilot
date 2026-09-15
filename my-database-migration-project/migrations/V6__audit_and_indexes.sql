-- V6__audit_and_indexes.sql

-- Create audit tables
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255) NOT NULL
);

CREATE TABLE user_audit (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create indexes for performance optimization
CREATE INDEX idx_audit_log_table_name ON audit_log(table_name);
CREATE INDEX idx_user_audit_user_id ON user_audit(user_id);
CREATE INDEX idx_user_audit_action_time ON user_audit(action_time);

-- Additional indexes can be added as needed based on query performance analysis.