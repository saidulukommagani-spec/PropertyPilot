CREATE TABLE reference_data (
    reference_data_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category VARCHAR(80) NOT NULL,
    code VARCHAR(80) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    description TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    effective_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_reference_data_category_code UNIQUE(category,code),
    CONSTRAINT ck_reference_data_category CHECK (category ~ '^[A-Z][A-Z0-9_]{1,79}$'),
    CONSTRAINT ck_reference_data_code CHECK (code ~ '^[A-Z][A-Z0-9_]{1,79}$'),
    CONSTRAINT ck_reference_data_name CHECK (length(btrim(display_name)) > 0),
    CONSTRAINT ck_reference_data_sort CHECK (sort_order >= 0),
    CONSTRAINT ck_reference_data_status CHECK (status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT ck_reference_data_effective_period CHECK (effective_to IS NULL OR effective_to > effective_from),
    CONSTRAINT ck_reference_data_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE audit_log (
    audit_log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(120) NOT NULL,
    entity_id UUID,
    action VARCHAR(30) NOT NULL,
    actor_user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    old_data JSONB,
    new_data JSONB,
    correlation_id UUID,
    request_id VARCHAR(100),
    source_ip INET,
    user_agent TEXT,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_audit_log_entity_type CHECK (length(btrim(entity_type)) > 0),
    CONSTRAINT ck_audit_log_action CHECK (action IN ('CREATE','UPDATE','DELETE','STATUS_CHANGE','LOGIN','LOGOUT','ACCESS','EXPORT','SYSTEM')),
    CONSTRAINT ck_audit_log_payload CHECK (old_data IS NOT NULL OR new_data IS NOT NULL OR action IN ('LOGIN','LOGOUT','ACCESS','EXPORT','SYSTEM')),
    CONSTRAINT ck_audit_log_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_reference_data_category_status ON reference_data(category,status,sort_order);
CREATE INDEX idx_reference_data_effective ON reference_data(effective_from,effective_to);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type,entity_id,occurred_at DESC);
CREATE INDEX idx_audit_log_actor ON audit_log(actor_user_id,occurred_at DESC) WHERE actor_user_id IS NOT NULL;
CREATE INDEX idx_audit_log_action_time ON audit_log(action,occurred_at DESC);
CREATE INDEX idx_audit_log_correlation ON audit_log(correlation_id) WHERE correlation_id IS NOT NULL;
CREATE INDEX idx_audit_log_occurred_at ON audit_log(occurred_at DESC);

CREATE TRIGGER trg_reference_data_audit BEFORE UPDATE ON reference_data FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

CREATE OR REPLACE FUNCTION prevent_audit_log_mutation()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION 'audit_log records are append-only';
END;
$$;

CREATE TRIGGER trg_audit_log_prevent_update
BEFORE UPDATE ON audit_log
FOR EACH ROW EXECUTE FUNCTION prevent_audit_log_mutation();

CREATE TRIGGER trg_audit_log_prevent_delete
BEFORE DELETE ON audit_log
FOR EACH ROW EXECUTE FUNCTION prevent_audit_log_mutation();

COMMENT ON TABLE reference_data IS 'Stores governed, effective-dated reference codes used across PropertyPilot domains.';
COMMENT ON TABLE audit_log IS 'Append-only security and business audit trail for material actions and data changes.';
COMMENT ON COLUMN reference_data.reference_data_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN reference_data.category IS 'Stable uppercase reference category.'; COMMENT ON COLUMN reference_data.code IS 'Stable uppercase code unique within the category.'; COMMENT ON COLUMN reference_data.display_name IS 'Human-readable label.'; COMMENT ON COLUMN reference_data.description IS 'Optional meaning and usage notes.'; COMMENT ON COLUMN reference_data.sort_order IS 'Presentation order within the category.'; COMMENT ON COLUMN reference_data.status IS 'Reference-code lifecycle state.'; COMMENT ON COLUMN reference_data.effective_from IS 'Inclusive effective timestamp.'; COMMENT ON COLUMN reference_data.effective_to IS 'Exclusive expiry timestamp.';
COMMENT ON COLUMN audit_log.audit_log_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN audit_log.entity_type IS 'Logical type of the affected entity.'; COMMENT ON COLUMN audit_log.entity_id IS 'Affected entity identifier when applicable.'; COMMENT ON COLUMN audit_log.action IS 'Governed audit action type.'; COMMENT ON COLUMN audit_log.actor_user_id IS 'Authenticated user responsible for the action.'; COMMENT ON COLUMN audit_log.old_data IS 'Approved before-state payload.'; COMMENT ON COLUMN audit_log.new_data IS 'Approved after-state payload.'; COMMENT ON COLUMN audit_log.correlation_id IS 'Cross-service correlation identifier.'; COMMENT ON COLUMN audit_log.request_id IS 'Inbound request or trace identifier.'; COMMENT ON COLUMN audit_log.source_ip IS 'Source network address when collected.'; COMMENT ON COLUMN audit_log.user_agent IS 'Client user-agent string when collected.'; COMMENT ON COLUMN audit_log.occurred_at IS 'Business timestamp at which the audited action occurred.';
COMMENT ON COLUMN reference_data.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN reference_data.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN reference_data.created_by IS 'Creating user.'; COMMENT ON COLUMN reference_data.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN reference_data.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN audit_log.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN audit_log.updated_at IS 'Initial update timestamp retained for audit-column consistency.'; COMMENT ON COLUMN audit_log.created_by IS 'Creating user.'; COMMENT ON COLUMN audit_log.updated_by IS 'Creating user for this immutable record.'; COMMENT ON COLUMN audit_log.version IS 'Immutable optimistic-lock version, initially zero.';
COMMENT ON FUNCTION prevent_audit_log_mutation() IS 'Rejects updates and deletes so audit records remain append-only.';
