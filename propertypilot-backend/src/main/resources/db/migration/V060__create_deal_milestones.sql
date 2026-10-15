CREATE TABLE deal_milestones (
    deal_milestone_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id UUID NOT NULL
        REFERENCES deals (deal_id) ON DELETE CASCADE,
    milestone_type VARCHAR(255) NOT NULL,
    milestone_status VARCHAR(255) NOT NULL,
    planned_date TIMESTAMPTZ,
    completed_date TIMESTAMPTZ,
    remarks VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deal_milestone_type CHECK (
        milestone_type IN (
            'PROPERTY_LISTED',
            'BUYER_IDENTIFIED',
            'SITE_VISIT',
            'OFFER_SUBMITTED',
            'OFFER_ACCEPTED',
            'DOCUMENT_VERIFICATION',
            'AGREEMENT_SIGNED',
            'REGISTRATION_COMPLETED',
            'COMMISSION_COLLECTED'
        )
    ),
    CONSTRAINT ck_deal_milestone_status CHECK (
        milestone_status IN (
            'PENDING',
            'IN_PROGRESS',
            'COMPLETED',
            'CANCELLED'
        )
    ),
    CONSTRAINT ck_deal_milestones_audit
        CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_deal_milestones_deal_id
    ON deal_milestones (deal_id);

CREATE INDEX idx_deal_milestones_created_by
    ON deal_milestones (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_deal_milestones_updated_by
    ON deal_milestones (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_deal_milestones_audit
BEFORE UPDATE ON deal_milestones
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();
