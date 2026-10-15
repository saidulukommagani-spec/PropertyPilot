CREATE TABLE deals (
    deal_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    property_id UUID REFERENCES properties (property_id) ON DELETE RESTRICT,
    seller_customer_id UUID NOT NULL
        REFERENCES customers (customer_id) ON DELETE RESTRICT,
    lead_id UUID REFERENCES leads (lead_id) ON DELETE SET NULL,

    deal_type VARCHAR(255) NOT NULL,
    deal_status VARCHAR(255) NOT NULL,
    deal_source VARCHAR(255) NOT NULL,

    asking_price NUMERIC,
    expected_price NUMERIC,
    final_deal_amount NUMERIC,
    buyer_commission_percent NUMERIC,
    seller_commission_percent NUMERIC,
    buyer_commission_amount NUMERIC,
    seller_commission_amount NUMERIC,

    selected_buyer_id UUID,
    exclusive_listing BOOLEAN,
    registration_date TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    remarks VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deals_audit_timestamps CHECK (updated_at >= created_at),
    CONSTRAINT ck_deals_version CHECK (version >= 0)
);

CREATE INDEX idx_deals_created_by
    ON deals (created_by)
    WHERE created_by IS NOT NULL;

CREATE INDEX idx_deals_updated_by
    ON deals (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_deals_set_audit_columns
BEFORE UPDATE ON deals
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();
