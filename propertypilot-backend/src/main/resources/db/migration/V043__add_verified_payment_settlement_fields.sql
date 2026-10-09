ALTER TABLE payments
    ADD COLUMN provider_event_id VARCHAR(160),
    ADD COLUMN settled_at TIMESTAMPTZ;

CREATE UNIQUE INDEX uq_payments_provider_event_id
    ON payments (provider_event_id)
    WHERE provider_event_id IS NOT NULL;

COMMENT ON COLUMN payments.provider_event_id IS
    'Unique provider settlement event identifier used to make settlement processing idempotent.';

COMMENT ON COLUMN payments.settled_at IS
    'Timestamp at which a verified provider settlement event confirmed payment.';
