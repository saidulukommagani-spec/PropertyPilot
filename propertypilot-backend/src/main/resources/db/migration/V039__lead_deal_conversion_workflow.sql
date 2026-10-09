ALTER TABLE leads DROP CONSTRAINT IF EXISTS ck_leads_status;

ALTER TABLE leads
    ADD CONSTRAINT ck_leads_status CHECK (
        status IN (
            'NEW', 'CONTACTED', 'QUALIFIED', 'READY_FOR_DEAL',
            'DEAL_CONVERTED', 'CONVERTED', 'LOST', 'CLOSED'
        )
    );

CREATE INDEX IF NOT EXISTS idx_deals_lead_type_status_created
    ON deals(lead_id, deal_type, deal_status, created_at DESC)
    WHERE lead_id IS NOT NULL;

COMMENT ON COLUMN leads.converted_at IS
    'Timestamp of the first successful conversion to a linked deal. Lead status and audit activity are updated in the same transaction.';
