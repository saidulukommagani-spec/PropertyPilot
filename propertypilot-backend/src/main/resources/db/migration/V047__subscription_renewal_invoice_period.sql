ALTER TABLE invoices
    ADD COLUMN renewal_period_start DATE,
    ADD COLUMN renewal_period_end DATE;

ALTER TABLE invoices
    ADD CONSTRAINT ck_invoice_renewal_period_pair
        CHECK ((renewal_period_start IS NULL) = (renewal_period_end IS NULL)),
    ADD CONSTRAINT ck_invoice_renewal_period_order
        CHECK (renewal_period_start IS NULL OR renewal_period_end >= renewal_period_start);

COMMENT ON COLUMN invoices.renewal_period_start IS 'Start date for a subscription renewal invoice.';
COMMENT ON COLUMN invoices.renewal_period_end IS 'End date for a subscription renewal invoice.';
