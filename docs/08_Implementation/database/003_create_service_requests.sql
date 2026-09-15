CREATE TABLE service_requests (
    service_request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE RESTRICT,
    request_type VARCHAR(100) NOT NULL,
    priority VARCHAR(30) NOT NULL DEFAULT 'medium',
    status VARCHAR(30) NOT NULL DEFAULT 'created',
    coverage_zone_id UUID REFERENCES coverage_zones(coverage_zone_id) ON DELETE SET NULL,
    cluster_id UUID REFERENCES clusters(cluster_id) ON DELETE SET NULL,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    scheduled_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    amount DECIMAL(15,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE service_request_status_history (
    status_history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(150),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
