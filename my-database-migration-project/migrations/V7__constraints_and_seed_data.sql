-- Establishing constraints for data integrity

-- Primary Key Constraints
ALTER TABLE core_table_name
ADD CONSTRAINT pk_core_table_name PRIMARY KEY (id);

ALTER TABLE reference_table_name
ADD CONSTRAINT pk_reference_table_name PRIMARY KEY (id);

ALTER TABLE subscription_table_name
ADD CONSTRAINT pk_subscription_table_name PRIMARY KEY (id);

ALTER TABLE payment_table_name
ADD CONSTRAINT pk_payment_table_name PRIMARY KEY (id);

ALTER TABLE notification_table_name
ADD CONSTRAINT pk_notification_table_name PRIMARY KEY (id);

ALTER TABLE marketplace_table_name
ADD CONSTRAINT pk_marketplace_table_name PRIMARY KEY (id);

ALTER TABLE complaint_table_name
ADD CONSTRAINT pk_complaint_table_name PRIMARY KEY (id);

ALTER TABLE audit_table_name
ADD CONSTRAINT pk_audit_table_name PRIMARY KEY (id);

-- Foreign Key Constraints
ALTER TABLE subscription_table_name
ADD CONSTRAINT fk_subscription_reference FOREIGN KEY (reference_id) REFERENCES reference_table_name(id);

ALTER TABLE payment_table_name
ADD CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) REFERENCES subscription_table_name(id);

ALTER TABLE notification_table_name
ADD CONSTRAINT fk_notification_payment FOREIGN KEY (payment_id) REFERENCES payment_table_name(id);

-- Unique Constraints
ALTER TABLE reference_table_name
ADD CONSTRAINT uq_reference_name UNIQUE (name);

-- Seed Data Insertion
INSERT INTO core_table_name (id, name, description) VALUES (1, 'Core Item 1', 'Description for core item 1');
INSERT INTO reference_table_name (id, name) VALUES (1, 'Reference Item 1');
INSERT INTO subscription_table_name (id, reference_id, start_date) VALUES (1, 1, CURRENT_DATE);
INSERT INTO payment_table_name (id, subscription_id, amount) VALUES (1, 1, 100.00);
INSERT INTO notification_table_name (id, payment_id, message) VALUES (1, 1, 'Payment successful');
INSERT INTO marketplace_table_name (id, name) VALUES (1, 'Marketplace Item 1');
INSERT INTO complaint_table_name (id, marketplace_id, description) VALUES (1, 1, 'Complaint about item 1');
INSERT INTO audit_table_name (id, action, timestamp) VALUES (1, 'Initial data load', CURRENT_TIMESTAMP);