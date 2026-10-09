ALTER TABLE service_requests
    ADD COLUMN pricing_estimate_id UUID
        REFERENCES pricing_estimates(pricing_estimate_id) ON DELETE RESTRICT;

CREATE UNIQUE INDEX uq_service_requests_quote
    ON service_requests(pricing_estimate_id)
    WHERE pricing_estimate_id IS NOT NULL;

CREATE OR REPLACE FUNCTION prevent_service_request_price_mutation()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.amount IS DISTINCT FROM OLD.amount
       OR NEW.pricing_estimate_id IS DISTINCT FROM OLD.pricing_estimate_id THEN
        RAISE EXCEPTION 'Service request amount and accepted quote are immutable'
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_service_request_price_immutable
    BEFORE UPDATE ON service_requests
    FOR EACH ROW EXECUTE FUNCTION prevent_service_request_price_mutation();

COMMENT ON COLUMN service_requests.pricing_estimate_id IS
    'Accepted quote used as the immutable source for the request price.';
