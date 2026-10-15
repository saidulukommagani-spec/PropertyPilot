ALTER TABLE leads
    ADD COLUMN contacted_at TIMESTAMPTZ,
    ADD COLUMN property_type VARCHAR(255),
    ADD COLUMN location VARCHAR(255);
