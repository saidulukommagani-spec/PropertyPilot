CREATE TABLE service_eligibility_rules (
    eligibility_rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE CASCADE,
    rule_name VARCHAR(150) NOT NULL, rule_definition JSONB NOT NULL, priority INTEGER NOT NULL DEFAULT 100, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_service_eligibility_rule UNIQUE (service_id, rule_name), CONSTRAINT ck_eligibility_definition CHECK (jsonb_typeof(rule_definition) = 'object'),
    CONSTRAINT ck_eligibility_priority CHECK (priority >= 0), CONSTRAINT ck_eligibility_status CHECK (status IN ('ACTIVE','INACTIVE')), CONSTRAINT ck_eligibility_audit CHECK (updated_at >= created_at AND version >= 0)
);
CREATE TABLE service_sla_policies (
    sla_policy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE CASCADE,
    policy_name VARCHAR(150) NOT NULL, acknowledgement_minutes INTEGER NOT NULL, completion_minutes INTEGER NOT NULL, escalation_policy JSONB NOT NULL DEFAULT '{}'::jsonb, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_service_sla_policy UNIQUE(service_id, policy_name), CONSTRAINT ck_sla_durations CHECK(acknowledgement_minutes > 0 AND completion_minutes >= acknowledgement_minutes),
    CONSTRAINT ck_sla_escalation CHECK(jsonb_typeof(escalation_policy) = 'object'), CONSTRAINT ck_sla_status CHECK(status IN ('ACTIVE','INACTIVE')), CONSTRAINT ck_sla_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE service_price_rules (
    price_rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE CASCADE,
    rule_name VARCHAR(150) NOT NULL, base_amount NUMERIC(15,2) NOT NULL, currency_code CHAR(3) NOT NULL DEFAULT 'INR', rule_definition JSONB NOT NULL DEFAULT '{}'::jsonb,
    effective_from TIMESTAMPTZ NOT NULL, effective_to TIMESTAMPTZ, status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_service_price_rule UNIQUE(service_id, rule_name, effective_from), CONSTRAINT ck_price_rule_amount CHECK(base_amount >= 0), CONSTRAINT ck_price_rule_currency CHECK(currency_code ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_price_rule_definition CHECK(jsonb_typeof(rule_definition) = 'object'), CONSTRAINT ck_price_rule_period CHECK(effective_to IS NULL OR effective_to > effective_from),
    CONSTRAINT ck_price_rule_status CHECK(status IN ('DRAFT','ACTIVE','RETIRED')), CONSTRAINT ck_price_rule_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE pricing_estimates (
    pricing_estimate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    property_id UUID REFERENCES properties(property_id) ON DELETE RESTRICT, service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT,
    subtotal NUMERIC(15,2) NOT NULL, discount_amount NUMERIC(15,2) NOT NULL DEFAULT 0, tax_amount NUMERIC(15,2) NOT NULL DEFAULT 0, total_amount NUMERIC(15,2) NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR', calculation_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_estimate_amounts CHECK(subtotal >= 0 AND discount_amount >= 0 AND tax_amount >= 0 AND total_amount >= 0), CONSTRAINT ck_estimate_currency CHECK(currency_code ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_estimate_snapshot CHECK(jsonb_typeof(calculation_snapshot) = 'object'), CONSTRAINT ck_estimate_status CHECK(status IN ('ACTIVE','ACCEPTED','EXPIRED','CANCELLED')),
    CONSTRAINT ck_estimate_expiry CHECK(expires_at > created_at), CONSTRAINT ck_estimate_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE pricing_quote_line_items (
    quote_line_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), pricing_estimate_id UUID NOT NULL REFERENCES pricing_estimates(pricing_estimate_id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(service_id) ON DELETE RESTRICT, line_type VARCHAR(30) NOT NULL, description VARCHAR(500) NOT NULL,
    quantity NUMERIC(12,3) NOT NULL DEFAULT 1, unit_amount NUMERIC(15,2) NOT NULL, line_amount NUMERIC(15,2) NOT NULL, sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_quote_line_type CHECK(line_type IN ('SERVICE','FEE','TAX','DISCOUNT','ADD_ON')), CONSTRAINT ck_quote_line_values CHECK(quantity > 0 AND unit_amount >= 0 AND line_amount >= 0 AND sort_order >= 0),
    CONSTRAINT ck_quote_line_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE coupons (
    coupon_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), coupon_code VARCHAR(80) NOT NULL UNIQUE, description VARCHAR(500), discount_type VARCHAR(20) NOT NULL,
    discount_value NUMERIC(15,2) NOT NULL, maximum_discount NUMERIC(15,2), minimum_order_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    valid_from TIMESTAMPTZ NOT NULL, valid_to TIMESTAMPTZ NOT NULL, redemption_limit INTEGER, per_customer_limit INTEGER, status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_coupon_code CHECK(coupon_code ~ '^[A-Z0-9_-]{3,80}$'), CONSTRAINT ck_coupon_type CHECK(discount_type IN ('FIXED','PERCENTAGE')),
    CONSTRAINT ck_coupon_values CHECK(discount_value > 0 AND (discount_type <> 'PERCENTAGE' OR discount_value <= 100) AND (maximum_discount IS NULL OR maximum_discount > 0) AND minimum_order_amount >= 0),
    CONSTRAINT ck_coupon_period CHECK(valid_to > valid_from), CONSTRAINT ck_coupon_limits CHECK((redemption_limit IS NULL OR redemption_limit > 0) AND (per_customer_limit IS NULL OR per_customer_limit > 0)),
    CONSTRAINT ck_coupon_status CHECK(status IN ('DRAFT','ACTIVE','SUSPENDED','EXPIRED')), CONSTRAINT ck_coupon_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE coupon_redemptions (
    coupon_redemption_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), coupon_id UUID NOT NULL REFERENCES coupons(coupon_id) ON DELETE RESTRICT,
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT, pricing_estimate_id UUID REFERENCES pricing_estimates(pricing_estimate_id) ON DELETE RESTRICT,
    payment_id UUID REFERENCES payments(payment_id) ON DELETE RESTRICT, redeemed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, discount_amount NUMERIC(15,2) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_redemption_discount CHECK(discount_amount >= 0), CONSTRAINT ck_redemption_status CHECK(status IN ('RESERVED','APPLIED','REVERSED')),
    CONSTRAINT ck_redemption_target CHECK(pricing_estimate_id IS NOT NULL OR payment_id IS NOT NULL), CONSTRAINT ck_redemption_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE pricing_discount_applications (
    discount_application_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), pricing_estimate_id UUID NOT NULL REFERENCES pricing_estimates(pricing_estimate_id) ON DELETE CASCADE,
    coupon_id UUID REFERENCES coupons(coupon_id) ON DELETE RESTRICT, discount_type VARCHAR(30) NOT NULL, description VARCHAR(500), amount NUMERIC(15,2) NOT NULL, metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_discount_type CHECK(discount_type IN ('COUPON','PLAN','MANUAL','PROMOTION')), CONSTRAINT ck_discount_amount CHECK(amount > 0),
    CONSTRAINT ck_discount_metadata CHECK(jsonb_typeof(metadata) = 'object'), CONSTRAINT ck_discount_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE TABLE pricing_rule_approvals (
    pricing_rule_approval_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), price_rule_id UUID NOT NULL REFERENCES service_price_rules(price_rule_id) ON DELETE CASCADE,
    requested_by UUID NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT, decided_by UUID REFERENCES users(user_id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', decision_notes TEXT, requested_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, decided_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_pricing_approval_status CHECK(status IN ('PENDING','APPROVED','REJECTED','CANCELLED')), CONSTRAINT ck_pricing_approval_decision CHECK((status = 'PENDING' AND decided_at IS NULL) OR status <> 'PENDING'),
    CONSTRAINT ck_pricing_approval_audit CHECK(updated_at >= created_at AND version >= 0)
);
CREATE INDEX idx_eligibility_service_status ON service_eligibility_rules(service_id,status,priority);
CREATE INDEX idx_sla_service_status ON service_sla_policies(service_id,status);
CREATE INDEX idx_price_rules_service_period ON service_price_rules(service_id,effective_from,effective_to);
CREATE INDEX idx_estimates_customer_status ON pricing_estimates(customer_id,status,expires_at);
CREATE INDEX idx_quote_items_estimate ON pricing_quote_line_items(pricing_estimate_id,sort_order);
CREATE INDEX idx_coupons_status_period ON coupons(status,valid_from,valid_to);
CREATE INDEX idx_redemptions_coupon_customer ON coupon_redemptions(coupon_id,customer_id,redeemed_at);
CREATE INDEX idx_discount_estimate ON pricing_discount_applications(pricing_estimate_id);
CREATE INDEX idx_pricing_approvals_rule_status ON pricing_rule_approvals(price_rule_id,status);
CREATE TRIGGER trg_eligibility_audit BEFORE UPDATE ON service_eligibility_rules FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_sla_audit BEFORE UPDATE ON service_sla_policies FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_price_rules_audit BEFORE UPDATE ON service_price_rules FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_estimates_audit BEFORE UPDATE ON pricing_estimates FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_quote_items_audit BEFORE UPDATE ON pricing_quote_line_items FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_coupons_audit BEFORE UPDATE ON coupons FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_redemptions_audit BEFORE UPDATE ON coupon_redemptions FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_discounts_audit BEFORE UPDATE ON pricing_discount_applications FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_pricing_approvals_audit BEFORE UPDATE ON pricing_rule_approvals FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
COMMENT ON TABLE service_eligibility_rules IS 'Machine-evaluable service eligibility policies.';
COMMENT ON TABLE service_sla_policies IS 'Service acknowledgement, completion, and escalation targets.';
COMMENT ON TABLE service_price_rules IS 'Effective-dated service pricing policies.';
COMMENT ON TABLE pricing_estimates IS 'Persisted customer pricing calculations.';
COMMENT ON TABLE pricing_quote_line_items IS 'Detailed components of a pricing estimate.';
COMMENT ON TABLE coupons IS 'Controlled promotional discount definitions.';
COMMENT ON TABLE coupon_redemptions IS 'Customer coupon usage ledger.';
COMMENT ON TABLE pricing_discount_applications IS 'Discounts applied to pricing estimates.';
COMMENT ON TABLE pricing_rule_approvals IS 'Approval workflow for price-rule changes.';
DO $$ DECLARE t text; c text; BEGIN FOREACH t IN ARRAY ARRAY['service_eligibility_rules','service_sla_policies','service_price_rules','pricing_estimates','pricing_quote_line_items','coupons','coupon_redemptions','pricing_discount_applications','pricing_rule_approvals'] LOOP FOREACH c IN ARRAY ARRAY['created_at','updated_at','created_by','updated_by','version'] LOOP EXECUTE format('COMMENT ON COLUMN %I.%I IS %L',t,c,'Standard audit metadata.'); END LOOP; END LOOP; END $$;
COMMENT ON COLUMN service_eligibility_rules.eligibility_rule_id IS 'Primary key.'; COMMENT ON COLUMN service_eligibility_rules.service_id IS 'Governed service.'; COMMENT ON COLUMN service_eligibility_rules.rule_name IS 'Rule name.'; COMMENT ON COLUMN service_eligibility_rules.rule_definition IS 'Eligibility expression.'; COMMENT ON COLUMN service_eligibility_rules.priority IS 'Evaluation priority.'; COMMENT ON COLUMN service_eligibility_rules.status IS 'Lifecycle state.';
COMMENT ON COLUMN service_sla_policies.sla_policy_id IS 'Primary key.'; COMMENT ON COLUMN service_sla_policies.service_id IS 'Governed service.'; COMMENT ON COLUMN service_sla_policies.policy_name IS 'Policy name.'; COMMENT ON COLUMN service_sla_policies.acknowledgement_minutes IS 'Acknowledgement target.'; COMMENT ON COLUMN service_sla_policies.completion_minutes IS 'Completion target.'; COMMENT ON COLUMN service_sla_policies.escalation_policy IS 'Escalation configuration.'; COMMENT ON COLUMN service_sla_policies.status IS 'Lifecycle state.';
COMMENT ON COLUMN service_price_rules.price_rule_id IS 'Primary key.'; COMMENT ON COLUMN service_price_rules.service_id IS 'Priced service.'; COMMENT ON COLUMN service_price_rules.rule_name IS 'Rule name.'; COMMENT ON COLUMN service_price_rules.base_amount IS 'Base price.'; COMMENT ON COLUMN service_price_rules.currency_code IS 'ISO currency.'; COMMENT ON COLUMN service_price_rules.rule_definition IS 'Pricing expression.'; COMMENT ON COLUMN service_price_rules.effective_from IS 'Effective start.'; COMMENT ON COLUMN service_price_rules.effective_to IS 'Effective end.'; COMMENT ON COLUMN service_price_rules.status IS 'Lifecycle state.';
COMMENT ON COLUMN pricing_estimates.pricing_estimate_id IS 'Primary key.'; COMMENT ON COLUMN pricing_estimates.customer_id IS 'Quoted customer.'; COMMENT ON COLUMN pricing_estimates.property_id IS 'Quoted property.'; COMMENT ON COLUMN pricing_estimates.service_id IS 'Quoted service.'; COMMENT ON COLUMN pricing_estimates.subtotal IS 'Pre-discount subtotal.'; COMMENT ON COLUMN pricing_estimates.discount_amount IS 'Discount total.'; COMMENT ON COLUMN pricing_estimates.tax_amount IS 'Tax total.'; COMMENT ON COLUMN pricing_estimates.total_amount IS 'Final total.'; COMMENT ON COLUMN pricing_estimates.currency_code IS 'ISO currency.'; COMMENT ON COLUMN pricing_estimates.calculation_snapshot IS 'Calculation inputs and outputs.'; COMMENT ON COLUMN pricing_estimates.status IS 'Estimate state.'; COMMENT ON COLUMN pricing_estimates.expires_at IS 'Acceptance deadline.';
COMMENT ON COLUMN pricing_quote_line_items.quote_line_item_id IS 'Primary key.'; COMMENT ON COLUMN pricing_quote_line_items.pricing_estimate_id IS 'Parent estimate.'; COMMENT ON COLUMN pricing_quote_line_items.service_id IS 'Related service.'; COMMENT ON COLUMN pricing_quote_line_items.line_type IS 'Component type.'; COMMENT ON COLUMN pricing_quote_line_items.description IS 'Line description.'; COMMENT ON COLUMN pricing_quote_line_items.quantity IS 'Quantity.'; COMMENT ON COLUMN pricing_quote_line_items.unit_amount IS 'Unit price.'; COMMENT ON COLUMN pricing_quote_line_items.line_amount IS 'Extended amount.'; COMMENT ON COLUMN pricing_quote_line_items.sort_order IS 'Presentation order.';
COMMENT ON COLUMN coupons.coupon_id IS 'Primary key.'; COMMENT ON COLUMN coupons.coupon_code IS 'Customer-entered code.'; COMMENT ON COLUMN coupons.description IS 'Promotion description.'; COMMENT ON COLUMN coupons.discount_type IS 'Fixed or percentage discount.'; COMMENT ON COLUMN coupons.discount_value IS 'Discount value.'; COMMENT ON COLUMN coupons.maximum_discount IS 'Maximum applied amount.'; COMMENT ON COLUMN coupons.minimum_order_amount IS 'Minimum qualifying order.'; COMMENT ON COLUMN coupons.valid_from IS 'Validity start.'; COMMENT ON COLUMN coupons.valid_to IS 'Validity end.'; COMMENT ON COLUMN coupons.redemption_limit IS 'Global redemption cap.'; COMMENT ON COLUMN coupons.per_customer_limit IS 'Per-customer cap.'; COMMENT ON COLUMN coupons.status IS 'Lifecycle state.';
COMMENT ON COLUMN coupon_redemptions.coupon_redemption_id IS 'Primary key.'; COMMENT ON COLUMN coupon_redemptions.coupon_id IS 'Redeemed coupon.'; COMMENT ON COLUMN coupon_redemptions.customer_id IS 'Redeeming customer.'; COMMENT ON COLUMN coupon_redemptions.pricing_estimate_id IS 'Related estimate.'; COMMENT ON COLUMN coupon_redemptions.payment_id IS 'Related payment.'; COMMENT ON COLUMN coupon_redemptions.redeemed_at IS 'Redemption time.'; COMMENT ON COLUMN coupon_redemptions.discount_amount IS 'Applied discount.'; COMMENT ON COLUMN coupon_redemptions.status IS 'Redemption state.';
COMMENT ON COLUMN pricing_discount_applications.discount_application_id IS 'Primary key.'; COMMENT ON COLUMN pricing_discount_applications.pricing_estimate_id IS 'Discounted estimate.'; COMMENT ON COLUMN pricing_discount_applications.coupon_id IS 'Source coupon.'; COMMENT ON COLUMN pricing_discount_applications.discount_type IS 'Discount source.'; COMMENT ON COLUMN pricing_discount_applications.description IS 'Discount description.'; COMMENT ON COLUMN pricing_discount_applications.amount IS 'Applied amount.'; COMMENT ON COLUMN pricing_discount_applications.metadata IS 'Calculation metadata.';
COMMENT ON COLUMN pricing_rule_approvals.pricing_rule_approval_id IS 'Primary key.'; COMMENT ON COLUMN pricing_rule_approvals.price_rule_id IS 'Rule under review.'; COMMENT ON COLUMN pricing_rule_approvals.requested_by IS 'Requester.'; COMMENT ON COLUMN pricing_rule_approvals.decided_by IS 'Decision maker.'; COMMENT ON COLUMN pricing_rule_approvals.status IS 'Approval state.'; COMMENT ON COLUMN pricing_rule_approvals.decision_notes IS 'Decision rationale.'; COMMENT ON COLUMN pricing_rule_approvals.requested_at IS 'Request time.'; COMMENT ON COLUMN pricing_rule_approvals.decided_at IS 'Decision time.';
