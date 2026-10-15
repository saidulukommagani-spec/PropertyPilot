ALTER TABLE evidence
    ADD COLUMN captured_by UUID;

ALTER TABLE evidence
    ADD CONSTRAINT fk_evidence_captured_by
        FOREIGN KEY (captured_by) REFERENCES users (user_id) ON DELETE SET NULL;

CREATE INDEX idx_evidence_captured_by
    ON evidence (captured_by)
    WHERE captured_by IS NOT NULL;
