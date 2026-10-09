-- A Service Request may consume an entitlement only once in an open period.
-- Each consumption row remains attributable to its source request.
CREATE UNIQUE INDEX uq_active_consumption_service_request
    ON subscription_entitlement_consumptions(service_request_id)
    WHERE service_request_id IS NOT NULL
      AND period_end IS NULL;
