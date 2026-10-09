-- Normalize legacy invoice labels to the invoice lifecycle represented by InvoiceStatus.
-- PENDING invoices were already issued and awaiting payment, so they are SENT.
-- VOID is represented by the lifecycle's CANCELLED terminal state.
ALTER TABLE invoices
    DROP CONSTRAINT IF EXISTS ck_invoices_status;

UPDATE invoices
SET status = CASE status
    WHEN 'PENDING' THEN 'SENT'
    WHEN 'VOID' THEN 'CANCELLED'
    ELSE status
END
WHERE status IN ('PENDING', 'VOID');

ALTER TABLE invoices
    ADD CONSTRAINT ck_invoices_status
        CHECK (status IN ('DRAFT', 'SENT', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED'));

DROP INDEX IF EXISTS idx_invoices_due;
CREATE INDEX idx_invoices_due
    ON invoices (due_at)
    WHERE status IN ('SENT', 'PARTIALLY_PAID', 'OVERDUE');
