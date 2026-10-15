ALTER TABLE service_requests
    ADD COLUMN assigned_to UUID REFERENCES agents (agent_id) ON DELETE RESTRICT,
    ADD COLUMN assigned_at TIMESTAMPTZ,
    ADD COLUMN assigned_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    ADD COLUMN cancellation_reason_code VARCHAR(255);
