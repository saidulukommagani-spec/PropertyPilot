ALTER TABLE documents
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE documents
    ADD CONSTRAINT fk_documents_created_by
        FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_documents_updated_by
        FOREIGN KEY (updated_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT ck_documents_audit_timestamps
        CHECK (updated_at >= created_at);

CREATE INDEX idx_documents_created_by
    ON documents (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_documents_updated_by
    ON documents (updated_by)
    WHERE updated_by IS NOT NULL;
