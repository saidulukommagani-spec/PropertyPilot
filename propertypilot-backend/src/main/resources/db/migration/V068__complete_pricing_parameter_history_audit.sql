ALTER TABLE pricing_parameter_history
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE pricing_parameter_history
    ADD CONSTRAINT fk_pricing_parameter_history_created_by
        FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_pricing_parameter_history_updated_by
        FOREIGN KEY (updated_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT ck_pricing_parameter_history_audit_timestamps
        CHECK (updated_at >= created_at),
    ADD CONSTRAINT ck_pricing_parameter_history_version
        CHECK (version >= 0);

CREATE INDEX idx_pricing_parameter_history_created_by
    ON pricing_parameter_history (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_pricing_parameter_history_updated_by
    ON pricing_parameter_history (updated_by)
    WHERE updated_by IS NOT NULL;
