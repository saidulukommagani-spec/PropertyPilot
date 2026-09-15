CREATE TABLE reports (
    report_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    report_type VARCHAR(80) NOT NULL,
    report_status VARCHAR(30) NOT NULL DEFAULT 'draft',
    summary TEXT,
    generated_by VARCHAR(150),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE contracts (
    contract_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    property_id UUID REFERENCES properties(property_id) ON DELETE SET NULL,
    partner_id UUID REFERENCES partners(partner_id) ON DELETE SET NULL,
    contract_type VARCHAR(80) NOT NULL,
    contract_status VARCHAR(30) NOT NULL DEFAULT 'draft',
    total_value DECIMAL(15,2),
    signed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE invoices (
    invoice_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id UUID REFERENCES contracts(contract_id) ON DELETE SET NULL,
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    invoice_number VARCHAR(80) NOT NULL UNIQUE,
    invoice_amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'pending',
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    contract_id UUID REFERENCES contracts(contract_id) ON DELETE SET NULL,
    invoice_id UUID REFERENCES invoices(invoice_id) ON DELETE SET NULL,
    payment_method VARCHAR(80),
    amount DECIMAL(15,2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'pending',
    payment_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
