ALTER TABLE pricing_estimates
    DROP CONSTRAINT ck_estimate_status;

ALTER TABLE pricing_estimates
    ADD CONSTRAINT ck_estimate_status
        CHECK (status IN ('ACTIVE', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'CANCELLED'));

ALTER TABLE pricing_estimates
    ADD COLUMN accepted_at TIMESTAMPTZ,
    ADD COLUMN accepted_by_user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    ADD COLUMN rejected_at TIMESTAMPTZ,
    ADD COLUMN rejected_by_user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    ADD COLUMN rejection_reason VARCHAR(1000);

CREATE INDEX idx_estimates_open_expiry
    ON pricing_estimates(expires_at)
    WHERE status = 'ACTIVE';

COMMENT ON COLUMN pricing_estimates.accepted_at IS 'Time customer accepted this immutable quote.';
COMMENT ON COLUMN pricing_estimates.accepted_by_user_id IS 'Authenticated user who accepted the quote.';
COMMENT ON COLUMN pricing_estimates.rejected_at IS 'Time customer rejected this quote.';
COMMENT ON COLUMN pricing_estimates.rejected_by_user_id IS 'Authenticated user who rejected the quote.';
COMMENT ON COLUMN pricing_estimates.rejection_reason IS 'Optional customer-provided reason for rejection.';
