-- Existing rows remain NULL. API policy treats an unresolved legacy row as internal.
ALTER TABLE lead_activities
    ADD COLUMN visibility VARCHAR(20);

ALTER TABLE lead_activities
    ADD CONSTRAINT ck_lead_activity_visibility
        CHECK (visibility IS NULL OR visibility IN ('INTERNAL', 'CUSTOMER_VISIBLE'));

COMMENT ON COLUMN lead_activities.visibility IS
    'Visibility classification. NULL is legacy/unresolved and must be handled as INTERNAL.';
