ALTER TABLE service_price_rules
    DROP CONSTRAINT IF EXISTS ck_price_rule_status;

ALTER TABLE service_price_rules
    ADD CONSTRAINT ck_price_rule_status
        CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'RETIRED'));
