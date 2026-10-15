ALTER TABLE document_versions
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID;

ALTER TABLE document_versions
    ADD CONSTRAINT fk_document_versions_created_by
        FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_document_versions_updated_by
        FOREIGN KEY (updated_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT ck_document_versions_audit_timestamps
        CHECK (updated_at >= created_at);

CREATE INDEX idx_document_versions_created_by
    ON document_versions (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_document_versions_updated_by
    ON document_versions (updated_by)
    WHERE updated_by IS NOT NULL;
