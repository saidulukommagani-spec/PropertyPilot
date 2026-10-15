CREATE TABLE deal_participants (
    deal_participant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id UUID NOT NULL
        REFERENCES deals (deal_id) ON DELETE CASCADE,
    customer_id UUID NOT NULL
        REFERENCES customers (customer_id) ON DELETE RESTRICT,

    participant_role VARCHAR(255) NOT NULL,
    participant_status VARCHAR(255) NOT NULL,
    contact_visibility VARCHAR(255) NOT NULL,
    offered_amount NUMERIC,
    offer_date TIMESTAMPTZ,
    selected_flag BOOLEAN,
    rejected_reason VARCHAR(255),
    remarks VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deal_participants_role CHECK (
        participant_role IN ('SELLER', 'BUYER', 'TENANT', 'LANDLORD')
    ),
    CONSTRAINT ck_deal_participants_status CHECK (
        participant_status IN ('ACTIVE', 'SHORTLISTED', 'REJECTED', 'SELECTED', 'WITHDRAWN')
    ),
    CONSTRAINT ck_deal_participants_contact_visibility CHECK (
        contact_visibility IN ('HIDDEN', 'PROPERTY_PILOT_ONLY', 'SHARED')
    ),
    CONSTRAINT ck_deal_participants_audit
        CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_deal_participants_deal_id
    ON deal_participants (deal_id);
CREATE INDEX idx_deal_participants_customer_id
    ON deal_participants (customer_id);
CREATE INDEX idx_deal_participants_created_by
    ON deal_participants (created_by)
    WHERE created_by IS NOT NULL;
CREATE INDEX idx_deal_participants_updated_by
    ON deal_participants (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_deal_participants_audit
BEFORE UPDATE ON deal_participants
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();

CREATE TABLE deal_service_request_links (
    deal_service_request_link_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id UUID NOT NULL
        REFERENCES deals (deal_id) ON DELETE CASCADE,
    service_request_id UUID NOT NULL
        REFERENCES service_requests (service_request_id) ON DELETE CASCADE,
    link_reason VARCHAR(255),
    linked_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deal_service_request_links_audit
        CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_deal_service_request_links_deal_id
    ON deal_service_request_links (deal_id);
CREATE INDEX idx_deal_service_request_links_service_request_id
    ON deal_service_request_links (service_request_id);
CREATE INDEX idx_deal_service_request_links_created_by
    ON deal_service_request_links (created_by)
    WHERE created_by IS NOT NULL;
CREATE INDEX idx_deal_service_request_links_updated_by
    ON deal_service_request_links (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_deal_service_request_links_audit
BEFORE UPDATE ON deal_service_request_links
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();

CREATE TABLE subscription_notifications (
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_subscription_id UUID NOT NULL
        REFERENCES customer_subscriptions (customer_subscription_id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    message VARCHAR(2000),
    status VARCHAR(30) NOT NULL,
    sent_date TIMESTAMP WITHOUT TIME ZONE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_subscription_notifications_audit
        CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_subscription_notifications_subscription_id
    ON subscription_notifications (customer_subscription_id);
CREATE INDEX idx_subscription_notifications_status
    ON subscription_notifications (status);
CREATE INDEX idx_subscription_notifications_type
    ON subscription_notifications (notification_type);
CREATE INDEX idx_subscription_notifications_created_by
    ON subscription_notifications (created_by)
    WHERE created_by IS NOT NULL;
CREATE INDEX idx_subscription_notifications_updated_by
    ON subscription_notifications (updated_by)
    WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_subscription_notifications_audit
BEFORE UPDATE ON subscription_notifications
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();
