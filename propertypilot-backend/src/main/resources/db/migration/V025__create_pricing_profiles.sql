CREATE TABLE pricing_profiles
(
    pricing_profile_id UUID PRIMARY KEY,

    profile_code VARCHAR(50) NOT NULL UNIQUE,

    profile_name VARCHAR(100) NOT NULL,

    description VARCHAR(500),

    active_flag BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    created_by UUID,
    updated_by UUID,

    version BIGINT NOT NULL DEFAULT 0
);