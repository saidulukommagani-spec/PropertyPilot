CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255),
    mobile_number VARCHAR(20),
    password_hash VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified_at TIMESTAMPTZ,
    mobile_verified_at TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_users_full_name_not_blank CHECK (length(btrim(full_name)) > 0),
    CONSTRAINT ck_users_login_identifier CHECK (email IS NOT NULL OR mobile_number IS NOT NULL),
    CONSTRAINT ck_users_email_normalized CHECK (email IS NULL OR email = lower(btrim(email))),
    CONSTRAINT ck_users_mobile_format CHECK (mobile_number IS NULL OR mobile_number ~ '^\+[1-9][0-9]{7,14}$'),
    CONSTRAINT ck_users_status CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'LOCKED', 'CLOSED')),
    CONSTRAINT ck_users_email_verification CHECK (email_verified_at IS NULL OR email IS NOT NULL),
    CONSTRAINT ck_users_mobile_verification CHECK (mobile_verified_at IS NULL OR mobile_number IS NOT NULL),
    CONSTRAINT ck_users_audit_timestamps CHECK (updated_at >= created_at),
    CONSTRAINT ck_users_version CHECK (version >= 0)
);

ALTER TABLE users
    ADD CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (user_id) ON DELETE SET NULL;

ALTER TABLE roles
    ADD CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users (user_id) ON DELETE SET NULL;

CREATE UNIQUE INDEX uq_users_email ON users (email) WHERE email IS NOT NULL;
CREATE UNIQUE INDEX uq_users_mobile_number ON users (mobile_number) WHERE mobile_number IS NOT NULL;
CREATE INDEX idx_users_status_created_at ON users (status, created_at DESC);
CREATE INDEX idx_users_created_by ON users (created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_users_updated_by ON users (updated_by) WHERE updated_by IS NOT NULL;
CREATE INDEX idx_roles_created_by ON roles (created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_roles_updated_by ON roles (updated_by) WHERE updated_by IS NOT NULL;

CREATE OR REPLACE FUNCTION set_audit_columns()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    NEW.version := OLD.version + 1;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_users_set_audit_columns
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE users IS 'Stores authenticated PropertyPilot user identities shared by customer, agent, partner, vendor, support, operations, and administrator profiles.';
COMMENT ON COLUMN users.user_id IS 'System-generated UUID primary key for the user.';
COMMENT ON COLUMN users.full_name IS 'Current human-readable full name of the user.';
COMMENT ON COLUMN users.email IS 'Normalized lowercase email login identifier when provided.';
COMMENT ON COLUMN users.mobile_number IS 'E.164 mobile login identifier when provided.';
COMMENT ON COLUMN users.password_hash IS 'One-way password hash when password authentication is explicitly enabled.';
COMMENT ON COLUMN users.status IS 'Current account lifecycle state.';
COMMENT ON COLUMN users.email_verified_at IS 'Timestamp at which ownership of the email address was verified.';
COMMENT ON COLUMN users.mobile_verified_at IS 'Timestamp at which ownership of the mobile number was verified.';
COMMENT ON COLUMN users.last_login_at IS 'Timestamp of the most recent successful authentication.';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user record was created.';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user record was last updated.';
COMMENT ON COLUMN users.created_by IS 'User that created this record; nullable for system bootstrap.';
COMMENT ON COLUMN users.updated_by IS 'User that last updated this record; nullable for system bootstrap.';
COMMENT ON COLUMN users.version IS 'Monotonically increasing optimistic-lock version.';
COMMENT ON FUNCTION set_audit_columns() IS 'Maintains updated_at and optimistic-lock version for audited tables.';
