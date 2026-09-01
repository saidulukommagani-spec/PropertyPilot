CREATE TABLE partners (
    partner_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID UNIQUE REFERENCES users(user_id) ON DELETE RESTRICT,
    company_name VARCHAR(180), partner_type VARCHAR(80) NOT NULL DEFAULT 'CHANNEL', status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_partners_identity CHECK (user_id IS NOT NULL OR COALESCE(length(btrim(company_name)), 0) > 0),
    CONSTRAINT ck_partners_type CHECK (length(btrim(partner_type)) > 0), CONSTRAINT ck_partners_status CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','TERMINATED')),
    CONSTRAINT ck_partners_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE vendors (
    vendor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID UNIQUE REFERENCES users(user_id) ON DELETE RESTRICT,
    company_name VARCHAR(180) NOT NULL, category VARCHAR(80), status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_vendors_company CHECK (length(btrim(company_name)) > 0), CONSTRAINT ck_vendors_status CHECK (status IN ('PENDING_VERIFICATION','ACTIVE','INACTIVE','SUSPENDED','TERMINATED')),
    CONSTRAINT ck_vendors_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE agents (
    agent_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE RESTRICT,
    agent_code VARCHAR(50) NOT NULL, region VARCHAR(120), commission_rate NUMERIC(5,2) NOT NULL DEFAULT 0, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_agents_code UNIQUE(agent_code), CONSTRAINT ck_agents_code CHECK (agent_code ~ '^[A-Z0-9][A-Z0-9_-]{2,49}$'),
    CONSTRAINT ck_agents_commission CHECK (commission_rate BETWEEN 0 AND 100), CONSTRAINT ck_agents_status CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','TERMINATED')),
    CONSTRAINT ck_agents_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE agent_assignments (
    assignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    agent_id UUID NOT NULL REFERENCES agents(agent_id) ON DELETE RESTRICT, assignment_status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED',
    distance_km NUMERIC(8,2), eta_minutes INTEGER, assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMPTZ, ended_at TIMESTAMPTZ, rejection_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_agent_assignments_status CHECK (assignment_status IN ('ASSIGNED','ACCEPTED','REJECTED','IN_PROGRESS','COMPLETED','CANCELLED','EXPIRED')),
    CONSTRAINT ck_agent_assignments_distance CHECK (distance_km IS NULL OR distance_km >= 0), CONSTRAINT ck_agent_assignments_eta CHECK (eta_minutes IS NULL OR eta_minutes >= 0),
    CONSTRAINT ck_agent_assignments_response CHECK (responded_at IS NULL OR responded_at >= assigned_at), CONSTRAINT ck_agent_assignments_end CHECK (ended_at IS NULL OR ended_at >= assigned_at),
    CONSTRAINT ck_agent_assignments_rejection CHECK (assignment_status <> 'REJECTED' OR length(btrim(rejection_reason)) > 0),
    CONSTRAINT ck_agent_assignments_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE UNIQUE INDEX uq_agent_assignments_active_request ON agent_assignments(service_request_id) WHERE assignment_status IN ('ASSIGNED','ACCEPTED','IN_PROGRESS');

CREATE TABLE visits (
    visit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    assignment_id UUID REFERENCES agent_assignments(assignment_id) ON DELETE SET NULL, agent_id UUID REFERENCES agents(agent_id) ON DELETE SET NULL,
    scheduled_at TIMESTAMPTZ NOT NULL, started_at TIMESTAMPTZ, ended_at TIMESTAMPTZ, status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', notes TEXT,
    cancellation_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_visits_status CHECK (status IN ('SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED','FAILED')),
    CONSTRAINT ck_visits_start CHECK (started_at IS NULL OR started_at >= scheduled_at), CONSTRAINT ck_visits_end CHECK (ended_at IS NULL OR (started_at IS NOT NULL AND ended_at >= started_at)),
    CONSTRAINT ck_visits_completion CHECK (status <> 'COMPLETED' OR ended_at IS NOT NULL),
    CONSTRAINT ck_visits_cancellation CHECK (status NOT IN ('CANCELLED','FAILED') OR length(btrim(cancellation_reason)) > 0),
    CONSTRAINT ck_visits_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE gps_verifications (
    gps_verification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), visit_id UUID NOT NULL REFERENCES visits(visit_id) ON DELETE CASCADE,
    property_location_id UUID REFERENCES property_locations(property_location_id) ON DELETE SET NULL,
    latitude NUMERIC(9,6) NOT NULL, longitude NUMERIC(9,6) NOT NULL, validation_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    distance_from_expected_km NUMERIC(8,3), captured_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, validated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_gps_latitude CHECK (latitude BETWEEN -90 AND 90), CONSTRAINT ck_gps_longitude CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT ck_gps_status CHECK (validation_status IN ('PENDING','VERIFIED','REJECTED')), CONSTRAINT ck_gps_distance CHECK (distance_from_expected_km IS NULL OR distance_from_expected_km >= 0),
    CONSTRAINT ck_gps_validation_time CHECK (validated_at IS NULL OR validated_at >= captured_at), CONSTRAINT ck_gps_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_partners_status ON partners(status); CREATE INDEX idx_vendors_status_category ON vendors(status,category); CREATE INDEX idx_agents_status_region ON agents(status,region);
CREATE INDEX idx_assignments_agent_status ON agent_assignments(agent_id,assignment_status); CREATE INDEX idx_assignments_request_created ON agent_assignments(service_request_id,created_at DESC);
CREATE INDEX idx_visits_request_scheduled ON visits(service_request_id,scheduled_at DESC); CREATE INDEX idx_visits_agent_status ON visits(agent_id,status) WHERE agent_id IS NOT NULL;
CREATE INDEX idx_gps_visit_captured ON gps_verifications(visit_id,captured_at DESC); CREATE INDEX idx_gps_status ON gps_verifications(validation_status);

CREATE TRIGGER trg_partners_audit BEFORE UPDATE ON partners FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_vendors_audit BEFORE UPDATE ON vendors FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_agents_audit BEFORE UPDATE ON agents FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_agent_assignments_audit BEFORE UPDATE ON agent_assignments FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_visits_audit BEFORE UPDATE ON visits FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_gps_verifications_audit BEFORE UPDATE ON gps_verifications FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE partners IS 'Stores organizations or individuals participating as PropertyPilot channel or operating partners.';
COMMENT ON TABLE vendors IS 'Stores third-party service-provider organizations.';
COMMENT ON TABLE agents IS 'Stores field-agent profiles linked to authenticated users.';
COMMENT ON TABLE agent_assignments IS 'Stores effective operational assignments of service requests to field agents.';
COMMENT ON TABLE visits IS 'Stores scheduled and executed property visits for service requests.';
COMMENT ON TABLE gps_verifications IS 'Stores captured coordinates and validation results for a visit.';

COMMENT ON COLUMN partners.partner_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN partners.user_id IS 'Optional authenticated user for the partner.'; COMMENT ON COLUMN partners.company_name IS 'Partner organization name.'; COMMENT ON COLUMN partners.partner_type IS 'Business-defined partner classification.'; COMMENT ON COLUMN partners.status IS 'Partner lifecycle state.';
COMMENT ON COLUMN vendors.vendor_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN vendors.user_id IS 'Optional authenticated vendor representative.'; COMMENT ON COLUMN vendors.company_name IS 'Legal or trading name of the vendor.'; COMMENT ON COLUMN vendors.category IS 'Primary service category.'; COMMENT ON COLUMN vendors.status IS 'Vendor lifecycle and verification state.';
COMMENT ON COLUMN agents.agent_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN agents.user_id IS 'Authenticated user owning the agent profile.'; COMMENT ON COLUMN agents.agent_code IS 'Immutable operational agent identifier.'; COMMENT ON COLUMN agents.region IS 'Primary operating region.'; COMMENT ON COLUMN agents.commission_rate IS 'Percentage commission rate.'; COMMENT ON COLUMN agents.status IS 'Agent lifecycle state.';
COMMENT ON COLUMN agent_assignments.assignment_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN agent_assignments.service_request_id IS 'Assigned service request.'; COMMENT ON COLUMN agent_assignments.agent_id IS 'Assigned field agent.'; COMMENT ON COLUMN agent_assignments.assignment_status IS 'Current assignment state.'; COMMENT ON COLUMN agent_assignments.distance_km IS 'Estimated distance to the property.'; COMMENT ON COLUMN agent_assignments.eta_minutes IS 'Estimated travel time in minutes.'; COMMENT ON COLUMN agent_assignments.assigned_at IS 'Assignment creation business timestamp.'; COMMENT ON COLUMN agent_assignments.responded_at IS 'Agent response timestamp.'; COMMENT ON COLUMN agent_assignments.ended_at IS 'Timestamp at which the assignment ended.'; COMMENT ON COLUMN agent_assignments.rejection_reason IS 'Required reason for a rejected assignment.';
COMMENT ON COLUMN visits.visit_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN visits.service_request_id IS 'Service request fulfilled by the visit.'; COMMENT ON COLUMN visits.assignment_id IS 'Assignment authorizing the visit.'; COMMENT ON COLUMN visits.agent_id IS 'Agent performing the visit.'; COMMENT ON COLUMN visits.scheduled_at IS 'Scheduled start timestamp.'; COMMENT ON COLUMN visits.started_at IS 'Actual start timestamp.'; COMMENT ON COLUMN visits.ended_at IS 'Actual end timestamp.'; COMMENT ON COLUMN visits.status IS 'Current governed visit state.'; COMMENT ON COLUMN visits.notes IS 'Visit observations.'; COMMENT ON COLUMN visits.cancellation_reason IS 'Reason for cancellation or failure.';
COMMENT ON COLUMN gps_verifications.gps_verification_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN gps_verifications.visit_id IS 'Visit being verified.'; COMMENT ON COLUMN gps_verifications.property_location_id IS 'Expected property location.'; COMMENT ON COLUMN gps_verifications.latitude IS 'Captured latitude.'; COMMENT ON COLUMN gps_verifications.longitude IS 'Captured longitude.'; COMMENT ON COLUMN gps_verifications.validation_status IS 'GPS verification state.'; COMMENT ON COLUMN gps_verifications.distance_from_expected_km IS 'Calculated distance from expected property location.'; COMMENT ON COLUMN gps_verifications.captured_at IS 'Coordinate capture timestamp.'; COMMENT ON COLUMN gps_verifications.validated_at IS 'Validation completion timestamp.';

COMMENT ON COLUMN partners.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN partners.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN partners.created_by IS 'Creating user.'; COMMENT ON COLUMN partners.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN partners.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN vendors.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN vendors.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN vendors.created_by IS 'Creating user.'; COMMENT ON COLUMN vendors.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN vendors.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN agents.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN agents.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN agents.created_by IS 'Creating user.'; COMMENT ON COLUMN agents.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN agents.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN agent_assignments.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN agent_assignments.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN agent_assignments.created_by IS 'Creating user.'; COMMENT ON COLUMN agent_assignments.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN agent_assignments.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN visits.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN visits.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN visits.created_by IS 'Creating user.'; COMMENT ON COLUMN visits.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN visits.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN gps_verifications.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN gps_verifications.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN gps_verifications.created_by IS 'Creating user.'; COMMENT ON COLUMN gps_verifications.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN gps_verifications.version IS 'Optimistic-lock version.';
