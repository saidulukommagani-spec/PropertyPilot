CREATE TABLE service_requests (
    service_request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE RESTRICT,
    request_type VARCHAR(100) NOT NULL,
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    coverage_zone_id UUID REFERENCES coverage_zones(coverage_zone_id) ON DELETE SET NULL,
    cluster_id UUID REFERENCES clusters(cluster_id) ON DELETE SET NULL,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    amount NUMERIC(15,2),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_service_requests_type CHECK (length(btrim(request_type)) > 0),
    CONSTRAINT ck_service_requests_priority CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT')),
    CONSTRAINT ck_service_requests_status CHECK (status IN ('NEW','PENDING_PAYMENT','PAYMENT_COMPLETED','PENDING_ASSIGNMENT','ASSIGNED','ACCEPTED','IN_PROGRESS','REPORT_SUBMITTED','UNDER_REVIEW','COMPLETED','CANCELLED','FAILED','ESCALATED')),
    CONSTRAINT ck_service_requests_amount CHECK (amount IS NULL OR amount >= 0),
    CONSTRAINT ck_service_requests_schedule CHECK (scheduled_at IS NULL OR scheduled_at >= requested_at),
    CONSTRAINT ck_service_requests_completion CHECK (completed_at IS NULL OR completed_at >= requested_at),
    CONSTRAINT ck_service_requests_completed_state CHECK (status <> 'COMPLETED' OR completed_at IS NOT NULL),
    CONSTRAINT ck_service_requests_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE service_request_status_history (
    status_history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    change_reason TEXT,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_request_history_previous_status CHECK (previous_status IS NULL OR previous_status IN ('NEW','PENDING_PAYMENT','PAYMENT_COMPLETED','PENDING_ASSIGNMENT','ASSIGNED','ACCEPTED','IN_PROGRESS','REPORT_SUBMITTED','UNDER_REVIEW','COMPLETED','CANCELLED','FAILED','ESCALATED')),
    CONSTRAINT ck_request_history_new_status CHECK (new_status IN ('NEW','PENDING_PAYMENT','PAYMENT_COMPLETED','PENDING_ASSIGNMENT','ASSIGNED','ACCEPTED','IN_PROGRESS','REPORT_SUBMITTED','UNDER_REVIEW','COMPLETED','CANCELLED','FAILED','ESCALATED')),
    CONSTRAINT ck_request_history_actual_change CHECK (previous_status IS NULL OR previous_status <> new_status),
    CONSTRAINT ck_request_history_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_service_requests_customer_status ON service_requests(customer_id,status,requested_at DESC);
CREATE INDEX idx_service_requests_property_status ON service_requests(property_id,status,requested_at DESC);
CREATE INDEX idx_service_requests_zone_status ON service_requests(coverage_zone_id,status) WHERE coverage_zone_id IS NOT NULL;
CREATE INDEX idx_service_requests_cluster_status ON service_requests(cluster_id,status) WHERE cluster_id IS NOT NULL;
CREATE INDEX idx_service_requests_scheduled ON service_requests(scheduled_at) WHERE scheduled_at IS NOT NULL;
CREATE INDEX idx_service_requests_created_by ON service_requests(created_by) WHERE created_by IS NOT NULL;
CREATE INDEX idx_request_status_history_request_changed ON service_request_status_history(service_request_id,changed_at DESC);

CREATE TRIGGER trg_service_requests_audit BEFORE UPDATE ON service_requests FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_request_status_history_audit BEFORE UPDATE ON service_request_status_history FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE service_requests IS 'Tracks customer requests for PropertyPilot services from intake through completion.';
COMMENT ON TABLE service_request_status_history IS 'Immutable-oriented audit history of service-request status changes.';
COMMENT ON COLUMN service_requests.service_request_id IS 'System-generated UUID primary key.';
COMMENT ON COLUMN service_requests.customer_id IS 'Customer requesting the service.';
COMMENT ON COLUMN service_requests.property_id IS 'Property at which the service is requested.';
COMMENT ON COLUMN service_requests.request_type IS 'Business-defined requested service type.';
COMMENT ON COLUMN service_requests.priority IS 'Operational handling priority.';
COMMENT ON COLUMN service_requests.status IS 'Current governed service-request state.';
COMMENT ON COLUMN service_requests.coverage_zone_id IS 'Resolved coverage zone for fulfillment.';
COMMENT ON COLUMN service_requests.cluster_id IS 'Resolved operational cluster for fulfillment.';
COMMENT ON COLUMN service_requests.requested_at IS 'Timestamp when the request was submitted.';
COMMENT ON COLUMN service_requests.scheduled_at IS 'Current scheduled fulfillment timestamp.';
COMMENT ON COLUMN service_requests.completed_at IS 'Timestamp when the request reached completed state.';
COMMENT ON COLUMN service_requests.amount IS 'Quoted or charged request amount when applicable.';
COMMENT ON COLUMN service_requests.description IS 'Customer or operational description of the request.';
COMMENT ON COLUMN service_requests.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN service_requests.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN service_requests.created_by IS 'Creating user.'; COMMENT ON COLUMN service_requests.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN service_requests.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN service_request_status_history.status_history_id IS 'System-generated UUID primary key.';
COMMENT ON COLUMN service_request_status_history.service_request_id IS 'Service request whose state changed.';
COMMENT ON COLUMN service_request_status_history.previous_status IS 'State before the transition, null for initial history.';
COMMENT ON COLUMN service_request_status_history.new_status IS 'State after the transition.';
COMMENT ON COLUMN service_request_status_history.change_reason IS 'Reason or operational context for the transition.';
COMMENT ON COLUMN service_request_status_history.changed_at IS 'Business timestamp of the transition.';
COMMENT ON COLUMN service_request_status_history.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN service_request_status_history.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN service_request_status_history.created_by IS 'Creating user.'; COMMENT ON COLUMN service_request_status_history.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN service_request_status_history.version IS 'Optimistic-lock version.';
