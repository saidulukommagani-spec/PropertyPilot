ALTER TABLE deals
    ADD COLUMN pricing_estimate_id UUID;

ALTER TABLE deals
    ADD CONSTRAINT fk_deals_pricing_estimate
        FOREIGN KEY (pricing_estimate_id)
        REFERENCES pricing_estimates (pricing_estimate_id)
        ON DELETE RESTRICT;

CREATE UNIQUE INDEX uq_deals_pricing_estimate
    ON deals (pricing_estimate_id)
    WHERE pricing_estimate_id IS NOT NULL;

COMMENT ON COLUMN deals.pricing_estimate_id IS
    'Pricing estimate that produced the commercial terms for this deal.';
