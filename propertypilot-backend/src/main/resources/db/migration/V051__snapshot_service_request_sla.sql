ALTER TABLE service_requests
    ADD COLUMN sla_due_at TIMESTAMPTZ,
    ADD COLUMN sla_policy_id_snapshot UUID,
    ADD COLUMN sla_snapshot_captured BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN service_requests.sla_due_at IS 'Completion deadline captured from the active service SLA policy when the request was created.';
COMMENT ON COLUMN service_requests.sla_policy_id_snapshot IS 'Identifier of the service SLA policy used for this request snapshot; historical snapshot only.';
COMMENT ON COLUMN service_requests.sla_snapshot_captured IS 'Whether this request has an immutable SLA policy snapshot, including an explicit no-policy result.';