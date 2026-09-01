CREATE TABLE user_roles (
    user_role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles (role_id) ON DELETE RESTRICT,
    effective_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_user_roles_effective_period CHECK (effective_to IS NULL OR effective_to > effective_from),
    CONSTRAINT ck_user_roles_audit_timestamps CHECK (updated_at >= created_at),
    CONSTRAINT ck_user_roles_version CHECK (version >= 0)
);

CREATE UNIQUE INDEX uq_user_roles_active_assignment ON user_roles (user_id, role_id) WHERE effective_to IS NULL;
CREATE INDEX idx_user_roles_user_effective ON user_roles (user_id, effective_to);
CREATE INDEX idx_user_roles_role_effective ON user_roles (role_id, effective_to);
CREATE INDEX idx_user_roles_created_by ON user_roles (created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_user_roles_updated_by ON user_roles (updated_by) WHERE updated_by IS NOT NULL;

CREATE OR REPLACE FUNCTION validate_active_role_assignment()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE role_id = NEW.role_id
          AND status = 'ACTIVE'
    ) THEN
        RAISE EXCEPTION 'Only active roles may be assigned';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_user_roles_validate_active_role
BEFORE INSERT OR UPDATE OF role_id, effective_to ON user_roles
FOR EACH ROW
WHEN (NEW.effective_to IS NULL)
EXECUTE FUNCTION validate_active_role_assignment();

CREATE TRIGGER trg_user_roles_set_audit_columns
BEFORE UPDATE ON user_roles
FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

CREATE TABLE customers (
    customer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users (user_id) ON DELETE RESTRICT,
    customer_type VARCHAR(30) NOT NULL DEFAULT 'INDIVIDUAL',
    preferred_location VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_customers_type CHECK (customer_type IN ('INDIVIDUAL', 'BUSINESS', 'NRI')),
    CONSTRAINT ck_customers_status CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT ck_customers_audit_timestamps CHECK (updated_at >= created_at),
    CONSTRAINT ck_customers_version CHECK (version >= 0)
);

CREATE INDEX idx_customers_status_created_at ON customers (status, created_at DESC);
CREATE INDEX idx_customers_created_by ON customers (created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_customers_updated_by ON customers (updated_by) WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_customers_set_audit_columns
BEFORE UPDATE ON customers
FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

CREATE TABLE leads (
    lead_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID REFERENCES customers (customer_id) ON DELETE SET NULL,
    source VARCHAR(80) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    budget_min NUMERIC(15,2),
    budget_max NUMERIC(15,2),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users (user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_leads_source_not_blank CHECK (length(btrim(source)) > 0),
    CONSTRAINT ck_leads_status CHECK (status IN ('NEW', 'CONTACTED', 'QUALIFIED', 'CONVERTED', 'LOST', 'CLOSED')),
    CONSTRAINT ck_leads_budget_min CHECK (budget_min IS NULL OR budget_min >= 0),
    CONSTRAINT ck_leads_budget_max CHECK (budget_max IS NULL OR budget_max >= 0),
    CONSTRAINT ck_leads_budget_range CHECK (budget_min IS NULL OR budget_max IS NULL OR budget_max >= budget_min),
    CONSTRAINT ck_leads_audit_timestamps CHECK (updated_at >= created_at),
    CONSTRAINT ck_leads_version CHECK (version >= 0)
);

CREATE INDEX idx_leads_customer_id ON leads (customer_id) WHERE customer_id IS NOT NULL;
CREATE INDEX idx_leads_status_created_at ON leads (status, created_at DESC);
CREATE INDEX idx_leads_source ON leads (source);
CREATE INDEX idx_leads_created_by ON leads (created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_leads_updated_by ON leads (updated_by) WHERE updated_by IS NOT NULL;

CREATE TRIGGER trg_leads_set_audit_columns
BEFORE UPDATE ON leads
FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE user_roles IS 'Stores effective-dated role assignments for users.';
COMMENT ON COLUMN user_roles.user_role_id IS 'System-generated UUID primary key for the assignment.';
COMMENT ON COLUMN user_roles.user_id IS 'User receiving the role.';
COMMENT ON COLUMN user_roles.role_id IS 'Role assigned to the user.';
COMMENT ON COLUMN user_roles.effective_from IS 'Inclusive timestamp from which the assignment is active.';
COMMENT ON COLUMN user_roles.effective_to IS 'Exclusive timestamp at which the assignment stops being active.';
COMMENT ON COLUMN user_roles.created_at IS 'Timestamp when the assignment was created.';
COMMENT ON COLUMN user_roles.updated_at IS 'Timestamp when the assignment was last updated.';
COMMENT ON COLUMN user_roles.created_by IS 'User that created the assignment.';
COMMENT ON COLUMN user_roles.updated_by IS 'User that last updated the assignment.';
COMMENT ON COLUMN user_roles.version IS 'Monotonically increasing optimistic-lock version.';
COMMENT ON FUNCTION validate_active_role_assignment() IS 'Prevents creation of active assignments for inactive roles.';

COMMENT ON TABLE customers IS 'Stores the customer profile associated with an authenticated user.';
COMMENT ON COLUMN customers.customer_id IS 'System-generated UUID primary key for the customer.';
COMMENT ON COLUMN customers.user_id IS 'Unique authenticated user owning the customer profile.';
COMMENT ON COLUMN customers.customer_type IS 'Customer classification used by service and compliance workflows.';
COMMENT ON COLUMN customers.preferred_location IS 'Free-text preferred service location pending structured selection.';
COMMENT ON COLUMN customers.status IS 'Current customer lifecycle state.';
COMMENT ON COLUMN customers.created_at IS 'Timestamp when the customer profile was created.';
COMMENT ON COLUMN customers.updated_at IS 'Timestamp when the customer profile was last updated.';
COMMENT ON COLUMN customers.created_by IS 'User that created the customer profile.';
COMMENT ON COLUMN customers.updated_by IS 'User that last updated the customer profile.';
COMMENT ON COLUMN customers.version IS 'Monotonically increasing optimistic-lock version.';

COMMENT ON TABLE leads IS 'Stores customer acquisition opportunities before or during conversion to an active customer.';
COMMENT ON COLUMN leads.lead_id IS 'System-generated UUID primary key for the lead.';
COMMENT ON COLUMN leads.customer_id IS 'Customer associated with the lead when known.';
COMMENT ON COLUMN leads.source IS 'Acquisition channel or source code for the lead.';
COMMENT ON COLUMN leads.status IS 'Current lead lifecycle state.';
COMMENT ON COLUMN leads.budget_min IS 'Optional minimum customer budget.';
COMMENT ON COLUMN leads.budget_max IS 'Optional maximum customer budget.';
COMMENT ON COLUMN leads.notes IS 'Operational notes about the lead.';
COMMENT ON COLUMN leads.created_at IS 'Timestamp when the lead was created.';
COMMENT ON COLUMN leads.updated_at IS 'Timestamp when the lead was last updated.';
COMMENT ON COLUMN leads.created_by IS 'User that created the lead.';
COMMENT ON COLUMN leads.updated_by IS 'User that last updated the lead.';
COMMENT ON COLUMN leads.version IS 'Monotonically increasing optimistic-lock version.';
