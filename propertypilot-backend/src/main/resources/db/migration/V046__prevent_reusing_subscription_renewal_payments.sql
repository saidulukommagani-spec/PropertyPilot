-- A confirmed payment can fund at most one subscription renewal.
-- Keep NULL payment IDs available for historical/non-payment renewal rows.
CREATE UNIQUE INDEX uq_subscription_renewals_payment_id
    ON subscription_renewals(payment_id)
    WHERE payment_id IS NOT NULL;
