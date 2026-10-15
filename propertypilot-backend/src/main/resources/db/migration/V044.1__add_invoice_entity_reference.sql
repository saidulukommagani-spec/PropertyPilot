ALTER TABLE invoices
    ADD COLUMN entity_type VARCHAR(255),
    ADD COLUMN entity_id UUID;
