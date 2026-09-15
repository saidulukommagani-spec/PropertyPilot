CREATE TABLE location_master
(
    location_id UUID PRIMARY KEY,

    country VARCHAR(100) NOT NULL,

    state VARCHAR(100) NOT NULL,

    district VARCHAR(100),

    mandal VARCHAR(100),

    village VARCHAR(100),

    pincode VARCHAR(20),

    latitude NUMERIC(10,8),

    longitude NUMERIC(11,8),

    location_type VARCHAR(30) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,

    updated_at TIMESTAMPTZ NOT NULL
);


CREATE TABLE pricing_rules
(
    pricing_rule_id UUID PRIMARY KEY,

    service_type VARCHAR(100) NOT NULL,

    location_id UUID
        REFERENCES location_master(location_id),

    base_price NUMERIC(12,2) NOT NULL,

    effective_from TIMESTAMPTZ NOT NULL,

    effective_to TIMESTAMPTZ,

    status VARCHAR(20) NOT NULL,

    version INTEGER NOT NULL
);

CREATE TABLE eta_rules
(
    eta_rule_id UUID PRIMARY KEY,

    service_type VARCHAR(100) NOT NULL,

    location_id UUID
        REFERENCES location_master(location_id),

    target_hours INTEGER NOT NULL,

    warning_hours INTEGER NOT NULL,

    critical_hours INTEGER NOT NULL,

    status VARCHAR(20) NOT NULL
);

CREATE TABLE cluster_rules
(
    cluster_rule_id UUID PRIMARY KEY,

    service_type VARCHAR(100) NOT NULL,

    radius_km NUMERIC(10,2) NOT NULL,

    minimum_requests INTEGER NOT NULL,

    discount_percentage NUMERIC(5,2),

    status VARCHAR(20) NOT NULL
);

CREATE TABLE subscription_benefits
(
    benefit_id UUID PRIMARY KEY,

    plan_id UUID NOT NULL,

    benefit_type VARCHAR(100) NOT NULL,

    benefit_value VARCHAR(255),

    status VARCHAR(20) NOT NULL
);
