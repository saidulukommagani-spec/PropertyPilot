ALTER TABLE evidence
    ADD COLUMN IF NOT EXISTS device_info VARCHAR(500),
    ADD COLUMN IF NOT EXISTS validation_status VARCHAR(32) NOT NULL DEFAULT 'APPROVED',
    ADD COLUMN IF NOT EXISTS validation_comment VARCHAR(2000),
    ADD COLUMN IF NOT EXISTS validated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS validated_at TIMESTAMPTZ;

ALTER TABLE evidence ALTER COLUMN validation_status SET DEFAULT 'PENDING';

CREATE TABLE IF NOT EXISTS evidence_validation_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evidence_id UUID NOT NULL REFERENCES evidence(evidence_id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL,
    comment VARCHAR(2000) NOT NULL,
    user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_evidence_validation_queue ON evidence(validation_status, captured_at);
CREATE INDEX IF NOT EXISTS idx_evidence_validation_history_item ON evidence_validation_history(evidence_id, occurred_at DESC);
