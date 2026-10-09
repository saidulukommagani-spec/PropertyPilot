ALTER TABLE invoices
    ADD COLUMN deal_id UUID;

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoices_deal
        FOREIGN KEY (deal_id)
        REFERENCES deals (deal_id)
        ON DELETE RESTRICT;

CREATE INDEX idx_invoices_deal
    ON invoices (deal_id)
    WHERE deal_id IS NOT NULL;

COMMENT ON COLUMN invoices.deal_id IS
    'Commercial deal that originated this invoice. Nullable for legacy invoices created before deal-based billing.';
