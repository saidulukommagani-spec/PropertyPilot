CREATE TABLE customer_addresses (
    customer_address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL, address_line1 VARCHAR(255) NOT NULL, address_line2 VARCHAR(255), locality_id UUID REFERENCES localities(locality_id) ON DELETE RESTRICT,
    city VARCHAR(120) NOT NULL, postal_code VARCHAR(20) NOT NULL, country_code CHAR(2) NOT NULL DEFAULT 'IN', is_primary BOOLEAN NOT NULL DEFAULT FALSE, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_customer_address_type CHECK(address_type IN ('HOME','WORK','BILLING','MAILING','OTHER')), CONSTRAINT ck_customer_address_country CHECK(country_code ~ '^[A-Z]{2}$'),
    CONSTRAINT ck_customer_address_status CHECK(status IN ('ACTIVE','INACTIVE')), CONSTRAINT ck_customer_address_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE UNIQUE INDEX uq_customer_primary_address ON customer_addresses(customer_id,address_type) WHERE is_primary AND status='ACTIVE';
CREATE TABLE customer_verifications (
    customer_verification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    verification_type VARCHAR(30) NOT NULL, reference_number_hash VARCHAR(255), document_id UUID REFERENCES property_documents(document_id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', verified_by UUID REFERENCES users(user_id) ON DELETE RESTRICT, verified_at TIMESTAMPTZ, rejection_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_customer_verification UNIQUE(customer_id,verification_type), CONSTRAINT ck_customer_verification_type CHECK(verification_type IN ('IDENTITY','ADDRESS','EMAIL','PHONE','TAX')),
    CONSTRAINT ck_customer_verification_status CHECK(status IN ('PENDING','VERIFIED','REJECTED','EXPIRED')), CONSTRAINT ck_customer_verification_result CHECK((status='VERIFIED' AND verified_at IS NOT NULL AND verified_by IS NOT NULL) OR status<>'VERIFIED'),
    CONSTRAINT ck_customer_verification_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE customer_preferences (
    customer_preference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL UNIQUE REFERENCES customers(customer_id) ON DELETE CASCADE,
    preferred_language VARCHAR(10) NOT NULL DEFAULT 'en', preferred_timezone VARCHAR(64) NOT NULL DEFAULT 'Asia/Kolkata', communication_channels JSONB NOT NULL DEFAULT '["EMAIL"]'::jsonb,
    marketing_consent BOOLEAN NOT NULL DEFAULT FALSE, preference_data JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_customer_channels CHECK(jsonb_typeof(communication_channels)='array'), CONSTRAINT ck_customer_preference_data CHECK(jsonb_typeof(preference_data)='object'),
    CONSTRAINT ck_customer_preference_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE property_owners (
    property_owner_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE CASCADE,
    customer_id UUID REFERENCES customers(customer_id) ON DELETE RESTRICT, owner_name VARCHAR(200) NOT NULL, ownership_percentage NUMERIC(5,2), ownership_type VARCHAR(30) NOT NULL DEFAULT 'SOLE',
    valid_from DATE NOT NULL DEFAULT CURRENT_DATE, valid_to DATE, is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_property_owner_name CHECK(length(btrim(owner_name))>0), CONSTRAINT ck_ownership_percentage CHECK(ownership_percentage IS NULL OR ownership_percentage > 0 AND ownership_percentage <= 100),
    CONSTRAINT ck_ownership_type CHECK(ownership_type IN ('SOLE','JOINT','COMPANY','TRUST','OTHER')), CONSTRAINT ck_property_owner_period CHECK(valid_to IS NULL OR valid_to >= valid_from),
    CONSTRAINT ck_property_owner_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE UNIQUE INDEX uq_property_primary_owner ON property_owners(property_id) WHERE is_primary AND valid_to IS NULL;
CREATE TABLE lead_activities (
    lead_activity_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), lead_id UUID NOT NULL REFERENCES leads(lead_id) ON DELETE CASCADE,
    activity_type VARCHAR(30) NOT NULL, subject VARCHAR(200) NOT NULL, details TEXT, performed_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, next_action_at TIMESTAMPTZ, outcome VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_lead_activity_type CHECK(activity_type IN ('CALL','EMAIL','MEETING','NOTE','STATUS_CHANGE','FOLLOW_UP')), CONSTRAINT ck_lead_activity_subject CHECK(length(btrim(subject))>0),
    CONSTRAINT ck_lead_activity_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE customer_subscription_add_ons (
    customer_subscription_add_on_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
    subscription_add_on_id UUID NOT NULL REFERENCES subscription_add_ons(subscription_add_on_id) ON DELETE RESTRICT, pricing_estimate_id UUID REFERENCES pricing_estimates(pricing_estimate_id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL DEFAULT 1, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', starts_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, ends_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_customer_subscription_add_on UNIQUE(customer_subscription_id,subscription_add_on_id,starts_at), CONSTRAINT ck_subscription_add_on_quantity CHECK(quantity>0),
    CONSTRAINT ck_subscription_add_on_status CHECK(status IN ('ACTIVE','SUSPENDED','CANCELLED','EXPIRED')), CONSTRAINT ck_subscription_add_on_period CHECK(ends_at IS NULL OR ends_at>starts_at),
    CONSTRAINT ck_subscription_add_on_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE INDEX idx_customer_addresses_customer ON customer_addresses(customer_id,status);
CREATE INDEX idx_customer_verifications_status ON customer_verifications(customer_id,status);
CREATE INDEX idx_property_owners_property ON property_owners(property_id,valid_from,valid_to);
CREATE INDEX idx_lead_activities_lead_time ON lead_activities(lead_id,occurred_at DESC);
CREATE INDEX idx_customer_add_ons_subscription ON customer_subscription_add_ons(customer_subscription_id,status);
CREATE TRIGGER trg_customer_addresses_audit BEFORE UPDATE ON customer_addresses FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_customer_verifications_audit BEFORE UPDATE ON customer_verifications FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_customer_preferences_audit BEFORE UPDATE ON customer_preferences FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_property_owners_audit BEFORE UPDATE ON property_owners FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_lead_activities_audit BEFORE UPDATE ON lead_activities FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_customer_add_ons_audit BEFORE UPDATE ON customer_subscription_add_ons FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
COMMENT ON TABLE customer_addresses IS 'Reusable customer postal addresses.'; COMMENT ON TABLE customer_verifications IS 'Customer identity and contact verification results.';
COMMENT ON TABLE customer_preferences IS 'Customer locale, consent, and communication preferences.'; COMMENT ON TABLE property_owners IS 'Effective-dated property ownership records.';
COMMENT ON TABLE lead_activities IS 'Chronological CRM activity for leads.'; COMMENT ON TABLE customer_subscription_add_ons IS 'Add-ons attached to customer subscriptions.';
DO $$ DECLARE t text; c text; BEGIN FOREACH t IN ARRAY ARRAY['customer_addresses','customer_verifications','customer_preferences','property_owners','lead_activities','customer_subscription_add_ons'] LOOP FOREACH c IN ARRAY ARRAY['created_at','updated_at','created_by','updated_by','version'] LOOP EXECUTE format('COMMENT ON COLUMN %I.%I IS %L',t,c,'Standard audit metadata.'); END LOOP; END LOOP; END $$;
COMMENT ON COLUMN customer_addresses.customer_address_id IS 'Primary key.'; COMMENT ON COLUMN customer_addresses.customer_id IS 'Address owner.'; COMMENT ON COLUMN customer_addresses.address_type IS 'Address purpose.'; COMMENT ON COLUMN customer_addresses.address_line1 IS 'Primary street line.'; COMMENT ON COLUMN customer_addresses.address_line2 IS 'Secondary street line.'; COMMENT ON COLUMN customer_addresses.locality_id IS 'Canonical locality.'; COMMENT ON COLUMN customer_addresses.city IS 'City.'; COMMENT ON COLUMN customer_addresses.postal_code IS 'Postal code.'; COMMENT ON COLUMN customer_addresses.country_code IS 'ISO country code.'; COMMENT ON COLUMN customer_addresses.is_primary IS 'Primary address flag.'; COMMENT ON COLUMN customer_addresses.status IS 'Lifecycle state.';
COMMENT ON COLUMN customer_verifications.customer_verification_id IS 'Primary key.'; COMMENT ON COLUMN customer_verifications.customer_id IS 'Verified customer.'; COMMENT ON COLUMN customer_verifications.verification_type IS 'Verification category.'; COMMENT ON COLUMN customer_verifications.reference_number_hash IS 'Non-reversible reference fingerprint.'; COMMENT ON COLUMN customer_verifications.document_id IS 'Supporting document.'; COMMENT ON COLUMN customer_verifications.status IS 'Verification state.'; COMMENT ON COLUMN customer_verifications.verified_by IS 'Reviewer.'; COMMENT ON COLUMN customer_verifications.verified_at IS 'Verification time.'; COMMENT ON COLUMN customer_verifications.rejection_reason IS 'Rejection rationale.';
COMMENT ON COLUMN customer_preferences.customer_preference_id IS 'Primary key.'; COMMENT ON COLUMN customer_preferences.customer_id IS 'Preference owner.'; COMMENT ON COLUMN customer_preferences.preferred_language IS 'BCP-47 language tag.'; COMMENT ON COLUMN customer_preferences.preferred_timezone IS 'IANA time zone.'; COMMENT ON COLUMN customer_preferences.communication_channels IS 'Allowed channels.'; COMMENT ON COLUMN customer_preferences.marketing_consent IS 'Marketing consent flag.'; COMMENT ON COLUMN customer_preferences.preference_data IS 'Additional preferences.';
COMMENT ON COLUMN property_owners.property_owner_id IS 'Primary key.'; COMMENT ON COLUMN property_owners.property_id IS 'Owned property.'; COMMENT ON COLUMN property_owners.customer_id IS 'Linked customer.'; COMMENT ON COLUMN property_owners.owner_name IS 'Legal owner name.'; COMMENT ON COLUMN property_owners.ownership_percentage IS 'Ownership share.'; COMMENT ON COLUMN property_owners.ownership_type IS 'Ownership form.'; COMMENT ON COLUMN property_owners.valid_from IS 'Ownership start.'; COMMENT ON COLUMN property_owners.valid_to IS 'Ownership end.'; COMMENT ON COLUMN property_owners.is_primary IS 'Primary contact flag.';
COMMENT ON COLUMN lead_activities.lead_activity_id IS 'Primary key.'; COMMENT ON COLUMN lead_activities.lead_id IS 'Parent lead.'; COMMENT ON COLUMN lead_activities.activity_type IS 'Activity category.'; COMMENT ON COLUMN lead_activities.subject IS 'Activity subject.'; COMMENT ON COLUMN lead_activities.details IS 'Activity details.'; COMMENT ON COLUMN lead_activities.performed_by IS 'Responsible user.'; COMMENT ON COLUMN lead_activities.occurred_at IS 'Activity time.'; COMMENT ON COLUMN lead_activities.next_action_at IS 'Scheduled follow-up.'; COMMENT ON COLUMN lead_activities.outcome IS 'Activity outcome.';
COMMENT ON COLUMN customer_subscription_add_ons.customer_subscription_add_on_id IS 'Primary key.'; COMMENT ON COLUMN customer_subscription_add_ons.customer_subscription_id IS 'Parent subscription.'; COMMENT ON COLUMN customer_subscription_add_ons.subscription_add_on_id IS 'Selected add-on.'; COMMENT ON COLUMN customer_subscription_add_ons.pricing_estimate_id IS 'Accepted estimate.'; COMMENT ON COLUMN customer_subscription_add_ons.quantity IS 'Purchased quantity.'; COMMENT ON COLUMN customer_subscription_add_ons.status IS 'Lifecycle state.'; COMMENT ON COLUMN customer_subscription_add_ons.starts_at IS 'Activation time.'; COMMENT ON COLUMN customer_subscription_add_ons.ends_at IS 'Termination time.';
