CREATE TABLE deal_activities (
    deal_activity_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id UUID NOT NULL
        REFERENCES deals (deal_id) ON DELETE CASCADE,
    activity_type VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    details VARCHAR(255),
    outcome VARCHAR(255),
    performed_by UUID
        REFERENCES users (user_id) ON DELETE SET NULL,
    occurred_at TIMESTAMPTZ,
    next_action_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deal_activity_subject
        CHECK (length(btrim(subject)) > 0),
    CONSTRAINT ck_deal_activity_audit
        CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_deal_activities_deal_time
    ON deal_activities (deal_id, occurred_at DESC);

CREATE INDEX idx_deal_activities_performed_by
    ON deal_activities (performed_by)
    WHERE performed_by IS NOT NULL;

CREATE INDEX idx_deal_activities_created_by
    ON deal_activities (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_deal_activities_updated_by
    ON deal_activities (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_deal_activities_audit
BEFORE UPDATE ON deal_activities
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();
