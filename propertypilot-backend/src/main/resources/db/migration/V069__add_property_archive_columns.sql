ALTER TABLE properties
    ADD COLUMN archived_at TIMESTAMPTZ,
    ADD COLUMN archived_by UUID REFERENCES users (user_id) ON DELETE SET NULL;
