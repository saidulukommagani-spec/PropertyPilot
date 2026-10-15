ALTER TABLE subscription_entitlement_consumptions
    ADD COLUMN entitlement_id UUID;

UPDATE subscription_entitlement_consumptions c
SET entitlement_id = (
    SELECT e.entitlement_id
    FROM customer_subscriptions s
    JOIN subscription_plan_entitlements e
        ON e.plan_version_id = s.plan_version_id
       AND e.service_id = c.service_id
    WHERE s.customer_subscription_id = c.customer_subscription_id
)
WHERE (
    SELECT count(*)
    FROM customer_subscriptions s
    JOIN subscription_plan_entitlements e
        ON e.plan_version_id = s.plan_version_id
       AND e.service_id = c.service_id
    WHERE s.customer_subscription_id = c.customer_subscription_id
) = 1;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM subscription_entitlement_consumptions
        WHERE entitlement_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Cannot uniquely backfill entitlement_id for every subscription entitlement consumption';
    END IF;
END;
$$;

ALTER TABLE subscription_entitlement_consumptions
    ALTER COLUMN entitlement_id SET NOT NULL,
    ADD CONSTRAINT fk_consumptions_entitlement
        FOREIGN KEY (entitlement_id)
        REFERENCES subscription_plan_entitlements (entitlement_id)
        ON DELETE RESTRICT;

CREATE INDEX idx_consumptions_entitlement
    ON subscription_entitlement_consumptions (entitlement_id);
