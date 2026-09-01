CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_code VARCHAR(80) NOT NULL,
    role_name VARCHAR(120) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_roles_role_code UNIQUE (role_code),
    CONSTRAINT ck_roles_role_code_format
        CHECK (role_code ~ '^[A-Z][A-Z0-9_]{1,79}$'),
    CONSTRAINT ck_roles_role_name_not_blank
        CHECK (length(btrim(role_name)) > 0),
    CONSTRAINT ck_roles_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_roles_audit_timestamps
        CHECK (updated_at >= created_at),
    CONSTRAINT ck_roles_version
        CHECK (version >= 0)
);

CREATE INDEX idx_roles_status ON roles (status);

CREATE OR REPLACE FUNCTION roles_before_update()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.role_code IS DISTINCT FROM OLD.role_code THEN
        RAISE EXCEPTION 'role_code is immutable';
    END IF;

    NEW.updated_at := CURRENT_TIMESTAMP;
    NEW.version := OLD.version + 1;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_roles_before_update
BEFORE UPDATE ON roles
FOR EACH ROW
EXECUTE FUNCTION roles_before_update();

COMMENT ON TABLE roles IS
    'Defines stable authorization roles assignable to PropertyPilot users.';

COMMENT ON COLUMN roles.role_id IS
    'System-generated UUID primary key for the role.';
COMMENT ON COLUMN roles.role_code IS
    'Immutable uppercase machine identifier for the role.';
COMMENT ON COLUMN roles.role_name IS
    'Human-readable display name for the role.';
COMMENT ON COLUMN roles.status IS
    'Lifecycle state controlling whether the role may be newly assigned.';
COMMENT ON COLUMN roles.created_at IS
    'Timestamp when the role record was created.';
COMMENT ON COLUMN roles.updated_at IS
    'Timestamp when the role record was last updated.';
COMMENT ON COLUMN roles.created_by IS
    'UUID of the actor that created the role; nullable for system bootstrap.';
COMMENT ON COLUMN roles.updated_by IS
    'UUID of the actor that last updated the role; nullable for system bootstrap.';
COMMENT ON COLUMN roles.version IS
    'Monotonically increasing optimistic-lock version.';

COMMENT ON FUNCTION roles_before_update() IS
    'Preserves immutable role codes and maintains role audit metadata on update.';
