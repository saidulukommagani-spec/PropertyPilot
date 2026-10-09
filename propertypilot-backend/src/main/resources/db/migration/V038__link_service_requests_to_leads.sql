ALTER TABLE service_requests
    ADD COLUMN lead_id UUID NULL,
    ADD CONSTRAINT fk_service_requests_lead
        FOREIGN KEY (lead_id) REFERENCES leads(lead_id) ON DELETE SET NULL;

CREATE INDEX idx_service_requests_lead_requested
    ON service_requests(lead_id, requested_at DESC)
    WHERE lead_id IS NOT NULL;

COMMENT ON COLUMN service_requests.lead_id IS
    'Optional originating lead; multiple service requests may reference one lead.';
