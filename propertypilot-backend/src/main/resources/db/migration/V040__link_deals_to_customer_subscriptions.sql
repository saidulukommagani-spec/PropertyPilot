ALTER TABLE deals
    ADD COLUMN customer_subscription_id UUID;

ALTER TABLE deals
    ADD CONSTRAINT fk_deals_customer_subscription
        FOREIGN KEY (customer_subscription_id)
        REFERENCES customer_subscriptions (customer_subscription_id)
        ON DELETE RESTRICT;

CREATE INDEX idx_deals_customer_subscription
    ON deals (customer_subscription_id)
    WHERE customer_subscription_id IS NOT NULL;

COMMENT ON COLUMN deals.customer_subscription_id IS
    'Subscription linked to this deal when the deal represents a subscription renewal.';
