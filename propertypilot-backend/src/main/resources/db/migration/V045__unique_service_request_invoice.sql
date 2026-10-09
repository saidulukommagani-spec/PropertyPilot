CREATE UNIQUE INDEX uq_invoices_service_request_entity
    ON invoices (entity_id)
    WHERE entity_type = 'SERVICE_REQUEST';

COMMENT ON INDEX uq_invoices_service_request_entity IS
    'A completed billable service request has at most one originating invoice.';
