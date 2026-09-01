CREATE TABLE subscription_plans (
 subscription_plan_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), plan_code VARCHAR(80) NOT NULL UNIQUE, plan_name VARCHAR(150) NOT NULL,
 description TEXT, status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
 updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_plans_code CHECK(plan_code~'^[A-Z][A-Z0-9_]{1,79}$'), CONSTRAINT ck_plans_name CHECK(length(btrim(plan_name))>0),
 CONSTRAINT ck_plans_status CHECK(status IN('DRAFT','ACTIVE','RETIRED')), CONSTRAINT ck_plans_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_plan_versions (
 plan_version_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), subscription_plan_id UUID NOT NULL REFERENCES subscription_plans(subscription_plan_id) ON DELETE CASCADE,
 version_number INTEGER NOT NULL, price NUMERIC(15,2) NOT NULL, currency_code CHAR(3) NOT NULL DEFAULT 'INR', effective_from TIMESTAMPTZ NOT NULL,
 effective_to TIMESTAMPTZ, status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', benefits_json JSONB NOT NULL DEFAULT '{}'::jsonb,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_plan_versions UNIQUE(subscription_plan_id,version_number), CONSTRAINT ck_plan_version_number CHECK(version_number>0),
 CONSTRAINT ck_plan_version_price CHECK(price>=0), CONSTRAINT ck_plan_version_currency CHECK(currency_code~'^[A-Z]{3}$'),
 CONSTRAINT ck_plan_version_period CHECK(effective_to IS NULL OR effective_to>effective_from), CONSTRAINT ck_plan_version_status CHECK(status IN('DRAFT','ACTIVE','RETIRED')),
 CONSTRAINT ck_plan_version_benefits CHECK(jsonb_typeof(benefits_json)='object'), CONSTRAINT ck_plan_version_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_plan_entitlements (
 entitlement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), plan_version_id UUID NOT NULL REFERENCES subscription_plan_versions(plan_version_id) ON DELETE CASCADE,
 service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT, quantity INTEGER NOT NULL, period_type VARCHAR(30) NOT NULL,
 carry_forward_allowed BOOLEAN NOT NULL DEFAULT FALSE, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_plan_entitlements UNIQUE(plan_version_id,service_id,period_type), CONSTRAINT ck_entitlement_quantity CHECK(quantity>=0),
 CONSTRAINT ck_entitlement_period CHECK(period_type IN('MONTHLY','ANNUAL','TERM')), CONSTRAINT ck_entitlement_status CHECK(status IN('ACTIVE','INACTIVE')),
 CONSTRAINT ck_entitlement_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_add_ons (
 subscription_add_on_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT,
 name VARCHAR(150) NOT NULL, price NUMERIC(15,2) NOT NULL DEFAULT 0, eligibility_rule_json JSONB NOT NULL DEFAULT '{}'::jsonb, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_add_on_name CHECK(length(btrim(name))>0), CONSTRAINT ck_add_on_price CHECK(price>=0),
 CONSTRAINT ck_add_on_rule CHECK(jsonb_typeof(eligibility_rule_json)='object'), CONSTRAINT ck_add_on_status CHECK(status IN('ACTIVE','INACTIVE')),
 CONSTRAINT ck_add_on_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE customer_subscriptions (
 customer_subscription_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
 plan_version_id UUID NOT NULL REFERENCES subscription_plan_versions(plan_version_id) ON DELETE RESTRICT, status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
 start_date DATE NOT NULL, end_date DATE, auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_customer_subscription_status CHECK(status IN('PENDING','ACTIVE','PAUSED','PAST_DUE','SUSPENDED','EXPIRED','CANCELLED')),
 CONSTRAINT ck_customer_subscription_period CHECK(end_date IS NULL OR end_date>=start_date), CONSTRAINT ck_customer_subscription_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_renewals (
 subscription_renewal_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
 payment_id UUID REFERENCES payments(payment_id) ON DELETE SET NULL, renewal_date DATE NOT NULL, period_start DATE NOT NULL, period_end DATE NOT NULL,
 status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_renewal_period CHECK(period_end>=period_start), CONSTRAINT ck_renewal_status CHECK(status IN('PENDING','SUCCESS','FAILED','CANCELLED')),
 CONSTRAINT ck_renewal_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_lifecycle_events (
 subscription_lifecycle_event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
 previous_status VARCHAR(30), new_status VARCHAR(30) NOT NULL, effective_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, reason_code VARCHAR(80),
 payment_id UUID REFERENCES payments(payment_id) ON DELETE SET NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_lifecycle_new CHECK(new_status IN('PENDING','ACTIVE','PAUSED','PAST_DUE','SUSPENDED','EXPIRED','CANCELLED')),
 CONSTRAINT ck_lifecycle_change CHECK(previous_status IS NULL OR previous_status<>new_status), CONSTRAINT ck_lifecycle_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_entitlement_consumptions (
 entitlement_consumption_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
 service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT, period_start DATE NOT NULL, period_end DATE NOT NULL,
 entitled_quantity INTEGER NOT NULL, consumed_quantity INTEGER NOT NULL DEFAULT 0, service_request_id UUID REFERENCES service_requests(service_request_id) ON DELETE SET NULL, expired_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_consumption_period CHECK(period_end>=period_start), CONSTRAINT ck_consumption_quantities CHECK(entitled_quantity>=0 AND consumed_quantity BETWEEN 0 AND entitled_quantity),
 CONSTRAINT ck_consumption_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE subscription_pauses (
 subscription_pause_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
 pause_start DATE NOT NULL, pause_end DATE NOT NULL, reason VARCHAR(255) NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'PENDING', approved_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_pause_period CHECK(pause_end>=pause_start), CONSTRAINT ck_pause_reason CHECK(length(btrim(reason))>0),
 CONSTRAINT ck_pause_status CHECK(status IN('PENDING','APPROVED','REJECTED','ACTIVE','COMPLETED','CANCELLED')), CONSTRAINT ck_pause_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE TABLE monitoring_schedules (
 monitoring_schedule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_subscription_id UUID NOT NULL REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE CASCADE,
 property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE CASCADE, service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT,
 frequency VARCHAR(30) NOT NULL, next_due_at TIMESTAMPTZ NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', last_generated_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_schedule_frequency CHECK(frequency IN('WEEKLY','MONTHLY','QUARTERLY','SEMI_ANNUAL','ANNUAL')),
 CONSTRAINT ck_schedule_status CHECK(status IN('ACTIVE','PAUSED','COMPLETED','CANCELLED')), CONSTRAINT ck_schedule_audit CHECK(updated_at>=created_at AND version>=0)
);

CREATE UNIQUE INDEX uq_active_plan_version ON subscription_plan_versions(subscription_plan_id) WHERE status='ACTIVE';
CREATE INDEX idx_plan_versions_effective ON subscription_plan_versions(subscription_plan_id,effective_from DESC);
CREATE INDEX idx_entitlements_service_status ON subscription_plan_entitlements(service_id,status);
CREATE UNIQUE INDEX uq_active_add_on_service ON subscription_add_ons(service_id) WHERE status='ACTIVE';
CREATE INDEX idx_customer_subscriptions_customer_status ON customer_subscriptions(customer_id,status);
CREATE INDEX idx_renewals_subscription_date ON subscription_renewals(customer_subscription_id,renewal_date DESC);
CREATE INDEX idx_lifecycle_subscription_time ON subscription_lifecycle_events(customer_subscription_id,effective_at DESC);
CREATE INDEX idx_consumptions_subscription_period ON subscription_entitlement_consumptions(customer_subscription_id,period_start,period_end);
CREATE INDEX idx_pauses_subscription_period ON subscription_pauses(customer_subscription_id,pause_start,pause_end);
CREATE INDEX idx_monitoring_due ON monitoring_schedules(status,next_due_at);

CREATE TRIGGER trg_plans_audit BEFORE UPDATE ON subscription_plans FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_plan_versions_audit BEFORE UPDATE ON subscription_plan_versions FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_entitlements_audit BEFORE UPDATE ON subscription_plan_entitlements FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_add_ons_audit BEFORE UPDATE ON subscription_add_ons FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_customer_subscriptions_audit BEFORE UPDATE ON customer_subscriptions FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_renewals_audit BEFORE UPDATE ON subscription_renewals FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_lifecycle_audit BEFORE UPDATE ON subscription_lifecycle_events FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_consumptions_audit BEFORE UPDATE ON subscription_entitlement_consumptions FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_pauses_audit BEFORE UPDATE ON subscription_pauses FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_monitoring_schedules_audit BEFORE UPDATE ON monitoring_schedules FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE subscription_plans IS 'Stable subscription-plan identities.'; COMMENT ON TABLE subscription_plan_versions IS 'Immutable effective-dated commercial plan versions.';
COMMENT ON TABLE subscription_plan_entitlements IS 'Service quantities included in a plan version.'; COMMENT ON TABLE subscription_add_ons IS 'Separately selectable subscription add-ons.';
COMMENT ON TABLE customer_subscriptions IS 'Customer acceptance of a specific plan version.'; COMMENT ON TABLE subscription_renewals IS 'Subscription renewal attempts and outcomes.';
COMMENT ON TABLE subscription_lifecycle_events IS 'Subscription state-transition history.'; COMMENT ON TABLE subscription_entitlement_consumptions IS 'Entitlement balances and consumption.';
COMMENT ON TABLE subscription_pauses IS 'Approved or requested subscription pause periods.'; COMMENT ON TABLE monitoring_schedules IS 'Recurring service-generation schedules.';

DO $$ DECLARE t text; c text; BEGIN
 FOREACH t IN ARRAY ARRAY['subscription_plans','subscription_plan_versions','subscription_plan_entitlements','subscription_add_ons','customer_subscriptions','subscription_renewals','subscription_lifecycle_events','subscription_entitlement_consumptions','subscription_pauses','monitoring_schedules'] LOOP
  FOREACH c IN ARRAY ARRAY['created_at','updated_at','created_by','updated_by','version'] LOOP EXECUTE format('COMMENT ON COLUMN %I.%I IS %L',t,c,'Standard audit metadata.'); END LOOP;
 END LOOP;
END $$;

COMMENT ON COLUMN subscription_plans.subscription_plan_id IS 'Primary key.'; COMMENT ON COLUMN subscription_plans.plan_code IS 'Stable plan code.'; COMMENT ON COLUMN subscription_plans.plan_name IS 'Display name.'; COMMENT ON COLUMN subscription_plans.description IS 'Plan description.'; COMMENT ON COLUMN subscription_plans.status IS 'Plan lifecycle state.';
COMMENT ON COLUMN subscription_plan_versions.plan_version_id IS 'Primary key.'; COMMENT ON COLUMN subscription_plan_versions.subscription_plan_id IS 'Parent plan.'; COMMENT ON COLUMN subscription_plan_versions.version_number IS 'Immutable plan version number.'; COMMENT ON COLUMN subscription_plan_versions.price IS 'Plan price.'; COMMENT ON COLUMN subscription_plan_versions.currency_code IS 'Currency.'; COMMENT ON COLUMN subscription_plan_versions.effective_from IS 'Effective start.'; COMMENT ON COLUMN subscription_plan_versions.effective_to IS 'Effective end.'; COMMENT ON COLUMN subscription_plan_versions.status IS 'Version state.'; COMMENT ON COLUMN subscription_plan_versions.benefits_json IS 'Versioned benefit configuration.';
COMMENT ON COLUMN subscription_plan_entitlements.entitlement_id IS 'Primary key.'; COMMENT ON COLUMN subscription_plan_entitlements.plan_version_id IS 'Plan version.'; COMMENT ON COLUMN subscription_plan_entitlements.service_id IS 'Entitled service.'; COMMENT ON COLUMN subscription_plan_entitlements.quantity IS 'Included quantity.'; COMMENT ON COLUMN subscription_plan_entitlements.period_type IS 'Entitlement period.'; COMMENT ON COLUMN subscription_plan_entitlements.carry_forward_allowed IS 'Carry-forward policy.'; COMMENT ON COLUMN subscription_plan_entitlements.status IS 'Entitlement state.';
COMMENT ON COLUMN subscription_add_ons.subscription_add_on_id IS 'Primary key.'; COMMENT ON COLUMN subscription_add_ons.service_id IS 'Add-on service.'; COMMENT ON COLUMN subscription_add_ons.name IS 'Display name.'; COMMENT ON COLUMN subscription_add_ons.price IS 'Add-on price.'; COMMENT ON COLUMN subscription_add_ons.eligibility_rule_json IS 'Eligibility policy.'; COMMENT ON COLUMN subscription_add_ons.status IS 'Add-on state.';
COMMENT ON COLUMN customer_subscriptions.customer_subscription_id IS 'Primary key.'; COMMENT ON COLUMN customer_subscriptions.customer_id IS 'Subscriber.'; COMMENT ON COLUMN customer_subscriptions.plan_version_id IS 'Accepted plan version.'; COMMENT ON COLUMN customer_subscriptions.status IS 'Subscription state.'; COMMENT ON COLUMN customer_subscriptions.start_date IS 'Start date.'; COMMENT ON COLUMN customer_subscriptions.end_date IS 'End date.'; COMMENT ON COLUMN customer_subscriptions.auto_renew IS 'Automatic renewal preference.';
COMMENT ON COLUMN subscription_renewals.subscription_renewal_id IS 'Primary key.'; COMMENT ON COLUMN subscription_renewals.customer_subscription_id IS 'Subscription.'; COMMENT ON COLUMN subscription_renewals.payment_id IS 'Renewal payment.'; COMMENT ON COLUMN subscription_renewals.renewal_date IS 'Renewal date.'; COMMENT ON COLUMN subscription_renewals.period_start IS 'Renewed period start.'; COMMENT ON COLUMN subscription_renewals.period_end IS 'Renewed period end.'; COMMENT ON COLUMN subscription_renewals.status IS 'Renewal outcome.';
COMMENT ON COLUMN subscription_lifecycle_events.subscription_lifecycle_event_id IS 'Primary key.'; COMMENT ON COLUMN subscription_lifecycle_events.customer_subscription_id IS 'Subscription.'; COMMENT ON COLUMN subscription_lifecycle_events.previous_status IS 'Prior state.'; COMMENT ON COLUMN subscription_lifecycle_events.new_status IS 'New state.'; COMMENT ON COLUMN subscription_lifecycle_events.effective_at IS 'Transition time.'; COMMENT ON COLUMN subscription_lifecycle_events.reason_code IS 'Reason.'; COMMENT ON COLUMN subscription_lifecycle_events.payment_id IS 'Related payment.';
COMMENT ON COLUMN subscription_entitlement_consumptions.entitlement_consumption_id IS 'Primary key.'; COMMENT ON COLUMN subscription_entitlement_consumptions.customer_subscription_id IS 'Subscription.'; COMMENT ON COLUMN subscription_entitlement_consumptions.service_id IS 'Entitled service.'; COMMENT ON COLUMN subscription_entitlement_consumptions.period_start IS 'Period start.'; COMMENT ON COLUMN subscription_entitlement_consumptions.period_end IS 'Period end.'; COMMENT ON COLUMN subscription_entitlement_consumptions.entitled_quantity IS 'Available quantity.'; COMMENT ON COLUMN subscription_entitlement_consumptions.consumed_quantity IS 'Consumed quantity.'; COMMENT ON COLUMN subscription_entitlement_consumptions.service_request_id IS 'Consuming request.'; COMMENT ON COLUMN subscription_entitlement_consumptions.expired_at IS 'Expiry time.';
COMMENT ON COLUMN subscription_pauses.subscription_pause_id IS 'Primary key.'; COMMENT ON COLUMN subscription_pauses.customer_subscription_id IS 'Subscription.'; COMMENT ON COLUMN subscription_pauses.pause_start IS 'Pause start.'; COMMENT ON COLUMN subscription_pauses.pause_end IS 'Pause end.'; COMMENT ON COLUMN subscription_pauses.reason IS 'Pause reason.'; COMMENT ON COLUMN subscription_pauses.status IS 'Pause state.'; COMMENT ON COLUMN subscription_pauses.approved_by IS 'Approver.';
COMMENT ON COLUMN monitoring_schedules.monitoring_schedule_id IS 'Primary key.'; COMMENT ON COLUMN monitoring_schedules.customer_subscription_id IS 'Subscription.'; COMMENT ON COLUMN monitoring_schedules.property_id IS 'Monitored property.'; COMMENT ON COLUMN monitoring_schedules.service_id IS 'Recurring service.'; COMMENT ON COLUMN monitoring_schedules.frequency IS 'Recurrence.'; COMMENT ON COLUMN monitoring_schedules.next_due_at IS 'Next due time.'; COMMENT ON COLUMN monitoring_schedules.status IS 'Schedule state.'; COMMENT ON COLUMN monitoring_schedules.last_generated_at IS 'Last generated time.';
