CREATE TABLE pricing_parameter_history
(
    pricing_parameter_history_id UUID PRIMARY KEY,

    pricing_parameter_id UUID NOT NULL,

    parameter_code VARCHAR(100) NOT NULL,

    old_value VARCHAR(500),

    new_value VARCHAR(500),

    change_reason VARCHAR(500),

    changed_at TIMESTAMPTZ NOT NULL,

    changed_by UUID,

    CONSTRAINT fk_pricing_parameter_history
        FOREIGN KEY (pricing_parameter_id)
        REFERENCES pricing_profile_parameters(
            pricing_parameter_id
        )
);