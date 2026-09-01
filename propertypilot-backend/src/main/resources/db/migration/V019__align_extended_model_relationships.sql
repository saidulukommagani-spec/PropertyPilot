ALTER TABLE leads
    ADD COLUMN property_id UUID REFERENCES properties(property_id) ON DELETE SET NULL,
    ADD COLUMN lead_type VARCHAR(50) NOT NULL DEFAULT 'SERVICE',
    ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    ADD COLUMN assigned_agent_id UUID REFERENCES agents(agent_id) ON DELETE SET NULL,
    ADD COLUMN converted_at TIMESTAMPTZ,
    ADD CONSTRAINT ck_leads_type CHECK(lead_type IN('SERVICE','SUBSCRIPTION','MARKETPLACE','REFERRAL')),
    ADD CONSTRAINT ck_leads_priority CHECK(priority IN('LOW','MEDIUM','HIGH','URGENT')),
    ADD CONSTRAINT ck_leads_conversion_time CHECK(converted_at IS NULL OR converted_at>=created_at);
CREATE INDEX idx_leads_status_assignee_created ON leads(status,assigned_agent_id,created_at DESC);
CREATE INDEX idx_leads_source_type_created ON leads(source,lead_type,created_at DESC);
COMMENT ON COLUMN leads.property_id IS 'Optional property context for the lead.'; COMMENT ON COLUMN leads.lead_type IS 'Lead business category.'; COMMENT ON COLUMN leads.priority IS 'Follow-up priority.'; COMMENT ON COLUMN leads.assigned_agent_id IS 'Assigned sales or field agent.'; COMMENT ON COLUMN leads.converted_at IS 'Conversion timestamp.';

ALTER TABLE customer_verifications
    ADD COLUMN provider_reference VARCHAR(255),
    ADD COLUMN expires_at TIMESTAMPTZ,
    ADD CONSTRAINT ck_customer_verification_expiry CHECK(expires_at IS NULL OR expires_at>created_at);
CREATE INDEX idx_customer_verifications_expiry ON customer_verifications(customer_id,status,expires_at);
COMMENT ON COLUMN customer_verifications.provider_reference IS 'External verification-provider reference.'; COMMENT ON COLUMN customer_verifications.expires_at IS 'Verification expiry time.';

ALTER TABLE pricing_estimates ADD COLUMN subscription_plan_id UUID REFERENCES subscription_plans(subscription_plan_id) ON DELETE RESTRICT;
CREATE INDEX idx_estimates_plan ON pricing_estimates(subscription_plan_id) WHERE subscription_plan_id IS NOT NULL;
COMMENT ON COLUMN pricing_estimates.subscription_plan_id IS 'Optional plan used during pricing.';

ALTER TABLE service_assignments
    ADD COLUMN scheduled_start TIMESTAMPTZ,
    ADD COLUMN scheduled_end TIMESTAMPTZ,
    ADD COLUMN accepted_at TIMESTAMPTZ,
    ADD CONSTRAINT ck_service_assignment_schedule CHECK(scheduled_end IS NULL OR scheduled_start IS NOT NULL AND scheduled_end>scheduled_start),
    ADD CONSTRAINT ck_service_assignment_acceptance CHECK(accepted_at IS NULL OR accepted_at>=assigned_at);
CREATE INDEX idx_service_assignments_assignee_schedule ON service_assignments(agent_id,vendor_id,status,scheduled_start);
COMMENT ON COLUMN service_assignments.scheduled_start IS 'Scheduled work start.'; COMMENT ON COLUMN service_assignments.scheduled_end IS 'Scheduled work end.'; COMMENT ON COLUMN service_assignments.accepted_at IS 'Assignee acceptance time.';

ALTER TABLE monitoring_alerts
    ADD COLUMN report_id UUID REFERENCES reports(report_id) ON DELETE SET NULL,
    ADD COLUMN recommended_service_id UUID REFERENCES services(service_id) ON DELETE SET NULL;
CREATE INDEX idx_monitoring_alerts_report ON monitoring_alerts(report_id) WHERE report_id IS NOT NULL;
COMMENT ON COLUMN monitoring_alerts.report_id IS 'Report containing the triggering finding.'; COMMENT ON COLUMN monitoring_alerts.recommended_service_id IS 'Recommended remediation service.';

ALTER TABLE service_ratings
    ADD COLUMN agent_id UUID REFERENCES agents(agent_id) ON DELETE SET NULL,
    ADD COLUMN vendor_id UUID REFERENCES vendors(vendor_id) ON DELETE SET NULL,
    ADD COLUMN submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD CONSTRAINT ck_rating_provider CHECK(NOT(agent_id IS NOT NULL AND vendor_id IS NOT NULL));
CREATE INDEX idx_service_ratings_request_submitted ON service_ratings(service_request_id,submitted_at DESC);
COMMENT ON COLUMN service_ratings.agent_id IS 'Rated agent when applicable.'; COMMENT ON COLUMN service_ratings.vendor_id IS 'Rated vendor when applicable.'; COMMENT ON COLUMN service_ratings.submitted_at IS 'Customer submission time.';

ALTER TABLE vendor_service_mappings
    ADD COLUMN coverage_scope JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN required_verification_level VARCHAR(30),
    ADD COLUMN capacity_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    ADD CONSTRAINT ck_vendor_coverage_scope CHECK(jsonb_typeof(coverage_scope)='object'),
    ADD CONSTRAINT ck_vendor_capacity_status CHECK(capacity_status IN('AVAILABLE','LIMITED','UNAVAILABLE'));
CREATE INDEX idx_vendor_service_verification ON vendor_service_mappings(service_id,status,required_verification_level);
COMMENT ON COLUMN vendor_service_mappings.coverage_scope IS 'Structured geographic eligibility beyond a single zone.'; COMMENT ON COLUMN vendor_service_mappings.required_verification_level IS 'Required vendor verification tier.'; COMMENT ON COLUMN vendor_service_mappings.capacity_status IS 'Current fulfillment capacity.';

ALTER TABLE marketplace_inquiries ADD COLUMN lead_id UUID REFERENCES leads(lead_id) ON DELETE SET NULL;
CREATE INDEX idx_marketplace_inquiries_lead ON marketplace_inquiries(lead_id) WHERE lead_id IS NOT NULL;
COMMENT ON COLUMN marketplace_inquiries.lead_id IS 'Protected CRM lead derived from the inquiry.';

ALTER TABLE marketplace_commissions
    ADD COLUMN vendor_id UUID REFERENCES vendors(vendor_id) ON DELETE SET NULL,
    ADD COLUMN settled_at TIMESTAMPTZ;
CREATE INDEX idx_marketplace_commissions_vendor ON marketplace_commissions(vendor_id,status) WHERE vendor_id IS NOT NULL;
COMMENT ON COLUMN marketplace_commissions.vendor_id IS 'Vendor involved in the commercial outcome.'; COMMENT ON COLUMN marketplace_commissions.settled_at IS 'Commission settlement time.';

ALTER TABLE nri_relationship_assignments ADD COLUMN property_id UUID REFERENCES properties(property_id) ON DELETE CASCADE;
CREATE INDEX idx_nri_assignments_property ON nri_relationship_assignments(property_id,status) WHERE property_id IS NOT NULL;
COMMENT ON COLUMN nri_relationship_assignments.property_id IS 'Optional property-specific relationship scope.';
