CREATE TABLE contracts (
    contract_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    property_id UUID REFERENCES properties(property_id) ON DELETE SET NULL, partner_id UUID REFERENCES partners(partner_id) ON DELETE SET NULL,
    contract_type VARCHAR(80) NOT NULL, contract_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', total_value NUMERIC(15,2), signed_at TIMESTAMPTZ,
    effective_from DATE, effective_to DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_contracts_type CHECK (length(btrim(contract_type)) > 0), CONSTRAINT ck_contracts_status CHECK (contract_status IN ('DRAFT','PENDING_SIGNATURE','ACTIVE','SUSPENDED','EXPIRED','TERMINATED','CANCELLED')),
    CONSTRAINT ck_contracts_value CHECK (total_value IS NULL OR total_value >= 0), CONSTRAINT ck_contracts_effective_period CHECK (effective_to IS NULL OR effective_from IS NULL OR effective_to >= effective_from),
    CONSTRAINT ck_contracts_signed CHECK (contract_status NOT IN ('ACTIVE','SUSPENDED','EXPIRED','TERMINATED') OR signed_at IS NOT NULL),
    CONSTRAINT ck_contracts_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE invoices (
    invoice_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), contract_id UUID REFERENCES contracts(contract_id) ON DELETE SET NULL,
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT, invoice_number VARCHAR(80) NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR', invoice_amount NUMERIC(15,2) NOT NULL, tax_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', issued_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, due_at TIMESTAMPTZ, paid_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_invoices_number UNIQUE(invoice_number), CONSTRAINT ck_invoices_currency CHECK (currency_code ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_invoices_amount CHECK (invoice_amount >= 0), CONSTRAINT ck_invoices_tax CHECK (tax_amount >= 0),
    CONSTRAINT ck_invoices_status CHECK (status IN ('DRAFT','PENDING','PARTIALLY_PAID','PAID','OVERDUE','VOID','CANCELLED')),
    CONSTRAINT ck_invoices_due CHECK (due_at IS NULL OR due_at >= issued_at), CONSTRAINT ck_invoices_paid CHECK (status <> 'PAID' OR paid_at IS NOT NULL),
    CONSTRAINT ck_invoices_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    contract_id UUID REFERENCES contracts(contract_id) ON DELETE SET NULL, invoice_id UUID REFERENCES invoices(invoice_id) ON DELETE SET NULL,
    provider VARCHAR(80), provider_payment_id VARCHAR(255), idempotency_key VARCHAR(120) NOT NULL,
    payment_method VARCHAR(80), currency_code CHAR(3) NOT NULL DEFAULT 'INR', amount NUMERIC(15,2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', payment_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_payments_idempotency_key UNIQUE(idempotency_key), CONSTRAINT uq_payments_provider_id UNIQUE(provider,provider_payment_id),
    CONSTRAINT ck_payments_currency CHECK (currency_code ~ '^[A-Z]{3}$'), CONSTRAINT ck_payments_amount CHECK (amount > 0),
    CONSTRAINT ck_payments_status CHECK (payment_status IN ('PENDING','AUTHORIZED','PROCESSING','SUCCESS','FAILED','PARTIAL_REFUND','REFUNDED','CANCELLED','EXPIRED')),
    CONSTRAINT ck_payments_provider_reference CHECK (provider_payment_id IS NULL OR provider IS NOT NULL),
    CONSTRAINT ck_payments_failure CHECK (payment_status <> 'FAILED' OR length(btrim(failure_reason)) > 0),
    CONSTRAINT ck_payments_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_contracts_customer_status ON contracts(customer_id,contract_status); CREATE INDEX idx_contracts_property ON contracts(property_id) WHERE property_id IS NOT NULL;
CREATE INDEX idx_invoices_customer_status ON invoices(customer_id,status,issued_at DESC); CREATE INDEX idx_invoices_contract ON invoices(contract_id) WHERE contract_id IS NOT NULL; CREATE INDEX idx_invoices_due ON invoices(due_at) WHERE status IN ('PENDING','PARTIALLY_PAID','OVERDUE');
CREATE INDEX idx_payments_customer_status ON payments(customer_id,payment_status,payment_date DESC); CREATE INDEX idx_payments_invoice ON payments(invoice_id) WHERE invoice_id IS NOT NULL; CREATE INDEX idx_payments_provider_reference ON payments(provider,provider_payment_id) WHERE provider_payment_id IS NOT NULL;

CREATE TRIGGER trg_contracts_audit BEFORE UPDATE ON contracts FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_invoices_audit BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_payments_audit BEFORE UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE contracts IS 'Stores customer commercial agreements for managed properties and services.';
COMMENT ON TABLE invoices IS 'Stores immutable-numbered customer billing documents.';
COMMENT ON TABLE payments IS 'Stores idempotent payment transactions and provider references.';
COMMENT ON COLUMN contracts.contract_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN contracts.customer_id IS 'Customer party to the contract.'; COMMENT ON COLUMN contracts.property_id IS 'Optional contracted property.'; COMMENT ON COLUMN contracts.partner_id IS 'Optional partner associated with the contract.'; COMMENT ON COLUMN contracts.contract_type IS 'Business-defined contract classification.'; COMMENT ON COLUMN contracts.contract_status IS 'Current contract lifecycle state.'; COMMENT ON COLUMN contracts.total_value IS 'Total contract value.'; COMMENT ON COLUMN contracts.signed_at IS 'Signature completion timestamp.'; COMMENT ON COLUMN contracts.effective_from IS 'Contract effective start date.'; COMMENT ON COLUMN contracts.effective_to IS 'Contract effective end date.';
COMMENT ON COLUMN invoices.invoice_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN invoices.contract_id IS 'Related contract when applicable.'; COMMENT ON COLUMN invoices.customer_id IS 'Customer billed by the invoice.'; COMMENT ON COLUMN invoices.invoice_number IS 'Globally unique human-facing invoice number.'; COMMENT ON COLUMN invoices.currency_code IS 'ISO 4217 currency code.'; COMMENT ON COLUMN invoices.invoice_amount IS 'Invoice amount before or including tax according to billing policy.'; COMMENT ON COLUMN invoices.tax_amount IS 'Tax component of the invoice.'; COMMENT ON COLUMN invoices.status IS 'Current invoice lifecycle state.'; COMMENT ON COLUMN invoices.issued_at IS 'Invoice issue timestamp.'; COMMENT ON COLUMN invoices.due_at IS 'Payment due timestamp.'; COMMENT ON COLUMN invoices.paid_at IS 'Timestamp when fully paid.';
COMMENT ON COLUMN payments.payment_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN payments.customer_id IS 'Customer making the payment.'; COMMENT ON COLUMN payments.contract_id IS 'Related contract when applicable.'; COMMENT ON COLUMN payments.invoice_id IS 'Related invoice when applicable.'; COMMENT ON COLUMN payments.provider IS 'External payment provider code.'; COMMENT ON COLUMN payments.provider_payment_id IS 'Provider-assigned transaction identifier.'; COMMENT ON COLUMN payments.idempotency_key IS 'Client or system key preventing duplicate payment creation.'; COMMENT ON COLUMN payments.payment_method IS 'Payment method classification.'; COMMENT ON COLUMN payments.currency_code IS 'ISO 4217 currency code.'; COMMENT ON COLUMN payments.amount IS 'Positive payment amount.'; COMMENT ON COLUMN payments.payment_status IS 'Current governed payment state.'; COMMENT ON COLUMN payments.payment_date IS 'Payment initiation timestamp.'; COMMENT ON COLUMN payments.failure_reason IS 'Required failure reason for failed payments.';
COMMENT ON COLUMN contracts.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN contracts.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN contracts.created_by IS 'Creating user.'; COMMENT ON COLUMN contracts.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN contracts.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN invoices.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN invoices.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN invoices.created_by IS 'Creating user.'; COMMENT ON COLUMN invoices.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN invoices.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN payments.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN payments.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN payments.created_by IS 'Creating user.'; COMMENT ON COLUMN payments.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN payments.version IS 'Optimistic-lock version.';
