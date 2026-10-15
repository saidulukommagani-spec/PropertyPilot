ALTER TABLE subscription_renewals
    ADD COLUMN new_customer_subscription_id UUID
        REFERENCES customer_subscriptions (customer_subscription_id) ON DELETE SET NULL,
    ADD COLUMN renewal_type VARCHAR(30),
    ADD COLUMN renewal_amount NUMERIC(12,2),
    ADD COLUMN remarks VARCHAR(1000);
