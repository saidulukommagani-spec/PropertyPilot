CREATE TABLE pricing_profile_parameters
(
    pricing_parameter_id UUID PRIMARY KEY,

    pricing_profile_id UUID NOT NULL,

    parameter_code VARCHAR(100) NOT NULL,

    parameter_name VARCHAR(200) NOT NULL,

    parameter_value VARCHAR(500) NOT NULL,

    parameter_type VARCHAR(50) NOT NULL,

    active_flag BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    created_by UUID,
    updated_by UUID,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_pricing_profile_parameter
        FOREIGN KEY (pricing_profile_id)
        REFERENCES pricing_profiles(pricing_profile_id)
);

CREATE UNIQUE INDEX uq_profile_parameter
ON pricing_profile_parameters
(
    pricing_profile_id,
    parameter_code
);