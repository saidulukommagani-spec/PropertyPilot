-- An open consumption row represents the current entitlement balance.
-- The service looks it up with period_end IS NULL and closes it only when
-- the usage period is explicitly finalized.
ALTER TABLE subscription_entitlement_consumptions
    ALTER COLUMN period_end DROP NOT NULL;
