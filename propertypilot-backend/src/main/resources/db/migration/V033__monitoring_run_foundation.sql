ALTER TABLE monitoring_schedules
    ALTER COLUMN customer_subscription_id DROP NOT NULL,
    ADD COLUMN source_service_request_id UUID REFERENCES service_requests(service_request_id) ON DELETE RESTRICT;

ALTER TABLE monitoring_schedules
    DROP CONSTRAINT ck_schedule_frequency,
    ADD CONSTRAINT ck_monitoring_schedule_acquisition CHECK (
        (customer_subscription_id IS NOT NULL AND source_service_request_id IS NULL)
        OR (customer_subscription_id IS NULL AND source_service_request_id IS NOT NULL)
    );

ALTER TABLE monitoring_schedules
    DROP CONSTRAINT monitoring_schedules_customer_subscription_id_fkey,
    ADD CONSTRAINT fk_monitoring_schedule_subscription
        FOREIGN KEY (customer_subscription_id) REFERENCES customer_subscriptions(customer_subscription_id) ON DELETE RESTRICT,
    DROP CONSTRAINT monitoring_schedules_property_id_fkey,
    ADD CONSTRAINT fk_monitoring_schedule_property
        FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE RESTRICT;

CREATE UNIQUE INDEX uq_monitoring_schedule_purchase_request
    ON monitoring_schedules(source_service_request_id) WHERE source_service_request_id IS NOT NULL;

CREATE TABLE monitoring_runs (
    monitoring_run_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    monitoring_schedule_id UUID NOT NULL REFERENCES monitoring_schedules(monitoring_schedule_id) ON DELETE RESTRICT,
    service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE RESTRICT,
    run_number INTEGER NOT NULL,
    cycle_key VARCHAR(120) NOT NULL,
    scheduled_for TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    run_status VARCHAR(30) NOT NULL DEFAULT 'SERVICE_REQUEST_LINKED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_monitoring_run_number UNIQUE (monitoring_schedule_id, run_number),
    CONSTRAINT uq_monitoring_run_cycle UNIQUE (monitoring_schedule_id, cycle_key),
    CONSTRAINT uq_monitoring_run_request UNIQUE (service_request_id),
    CONSTRAINT ck_monitoring_run_number CHECK (run_number > 0),
    CONSTRAINT ck_monitoring_run_status CHECK (run_status = 'SERVICE_REQUEST_LINKED'),
    CONSTRAINT ck_monitoring_run_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_monitoring_runs_schedule_history
    ON monitoring_runs(monitoring_schedule_id, run_number);
CREATE INDEX idx_monitoring_runs_request
    ON monitoring_runs(service_request_id);

CREATE OR REPLACE FUNCTION prevent_monitoring_run_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'monitoring_runs are immutable history';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_monitoring_runs_immutable
    BEFORE UPDATE OR DELETE ON monitoring_runs
    FOR EACH ROW EXECUTE FUNCTION prevent_monitoring_run_mutation();

COMMENT ON TABLE monitoring_runs IS 'Immutable record of a Monitoring cycle linked to the canonical service request that fulfills it.';
COMMENT ON COLUMN monitoring_schedules.source_service_request_id IS 'Originating one-time purchased Monitoring request; mutually exclusive with subscription linkage.';
COMMENT ON COLUMN monitoring_runs.cycle_key IS 'Stable per-schedule identifier used to make run creation idempotent.';
