CREATE TABLE services (
    service_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_code VARCHAR(80) NOT NULL UNIQUE, service_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL, base_price NUMERIC(15,2) NOT NULL DEFAULT 0, active_flag BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_services_code CHECK(service_code~'^[A-Z][A-Z0-9_]{1,79}$'), CONSTRAINT ck_services_name CHECK(length(btrim(service_name))>0),
    CONSTRAINT ck_services_category CHECK(length(btrim(category))>0), CONSTRAINT ck_services_price CHECK(base_price>=0), CONSTRAINT ck_services_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE INDEX idx_services_category_active ON services(category,active_flag);
CREATE TRIGGER trg_services_audit BEFORE UPDATE ON services FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

ALTER TABLE service_requests ADD COLUMN service_id UUID REFERENCES services(service_id) ON DELETE RESTRICT;
CREATE INDEX idx_service_requests_service_status ON service_requests(service_id,status) WHERE service_id IS NOT NULL;
COMMENT ON COLUMN service_requests.service_id IS 'Catalog service requested by the customer.';

CREATE TABLE agent_skills (
    skill_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), agent_id UUID NOT NULL REFERENCES agents(agent_id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(service_id) ON DELETE CASCADE, skill_name VARCHAR(100) NOT NULL, skill_level INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_agent_skills UNIQUE(agent_id,skill_name), CONSTRAINT ck_agent_skills_name CHECK(length(btrim(skill_name))>0),
    CONSTRAINT ck_agent_skills_level CHECK(skill_level BETWEEN 1 AND 5), CONSTRAINT ck_agent_skills_status CHECK(status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT ck_agent_skills_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE INDEX idx_agent_skills_service_status ON agent_skills(service_id,status) WHERE service_id IS NOT NULL;
CREATE TRIGGER trg_agent_skills_audit BEFORE UPDATE ON agent_skills FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

CREATE TABLE partner_services (
    partner_service_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), partner_id UUID NOT NULL REFERENCES partners(partner_id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(service_id) ON DELETE CASCADE, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_partner_services UNIQUE(partner_id,service_id), CONSTRAINT ck_partner_services_status CHECK(status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT ck_partner_services_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE INDEX idx_partner_services_service_status ON partner_services(service_id,status);
CREATE TRIGGER trg_partner_services_audit BEFORE UPDATE ON partner_services FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE services IS 'Canonical catalog of PropertyPilot services.'; COMMENT ON TABLE agent_skills IS 'Maps agent capabilities to service skills.'; COMMENT ON TABLE partner_services IS 'Maps partners to authorized services.';
COMMENT ON COLUMN services.service_id IS 'Primary key.'; COMMENT ON COLUMN services.service_code IS 'Stable service code.'; COMMENT ON COLUMN services.service_name IS 'Customer-visible name.'; COMMENT ON COLUMN services.category IS 'Service category.'; COMMENT ON COLUMN services.base_price IS 'Non-negative base price.'; COMMENT ON COLUMN services.active_flag IS 'Whether service is offered.';
COMMENT ON COLUMN agent_skills.skill_id IS 'Primary key.'; COMMENT ON COLUMN agent_skills.agent_id IS 'Skilled agent.'; COMMENT ON COLUMN agent_skills.service_id IS 'Related catalog service.'; COMMENT ON COLUMN agent_skills.skill_name IS 'Skill name.'; COMMENT ON COLUMN agent_skills.skill_level IS 'Proficiency from one to five.'; COMMENT ON COLUMN agent_skills.status IS 'Skill lifecycle state.';
COMMENT ON COLUMN partner_services.partner_service_id IS 'Primary key.'; COMMENT ON COLUMN partner_services.partner_id IS 'Partner.'; COMMENT ON COLUMN partner_services.service_id IS 'Authorized service.'; COMMENT ON COLUMN partner_services.status IS 'Mapping lifecycle state.';
COMMENT ON COLUMN services.created_at IS 'Creation time.'; COMMENT ON COLUMN services.updated_at IS 'Update time.'; COMMENT ON COLUMN services.created_by IS 'Creating user.'; COMMENT ON COLUMN services.updated_by IS 'Updating user.'; COMMENT ON COLUMN services.version IS 'Lock version.';
COMMENT ON COLUMN agent_skills.created_at IS 'Creation time.'; COMMENT ON COLUMN agent_skills.updated_at IS 'Update time.'; COMMENT ON COLUMN agent_skills.created_by IS 'Creating user.'; COMMENT ON COLUMN agent_skills.updated_by IS 'Updating user.'; COMMENT ON COLUMN agent_skills.version IS 'Lock version.';
COMMENT ON COLUMN partner_services.created_at IS 'Creation time.'; COMMENT ON COLUMN partner_services.updated_at IS 'Update time.'; COMMENT ON COLUMN partner_services.created_by IS 'Creating user.'; COMMENT ON COLUMN partner_services.updated_by IS 'Updating user.'; COMMENT ON COLUMN partner_services.version IS 'Lock version.';
