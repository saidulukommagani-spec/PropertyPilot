# PropertyPilot Production Blockers

Issues that must be fixed before real customer production deployment:

1. **Remove committed credential defaults.** `propertypilot-backend/src/main/resources/application.yml` contains a literal JWT signing secret and verbose SQL/bind logging defaults. Use externally managed secrets, require strong production values at startup, rotate the exposed value, and disable sensitive SQL logging.
2. **Enforce object-level authorization.** Align `SecurityConfig` URL matchers with actual controller mappings; derive customer/agent/admin identity from the authenticated principal; verify ownership for customer, property, service request, invoice, and payment reads/mutations. Caller-provided IDs currently permit insecure direct object references.
3. **Replace simulated payment success.** `PaymentServiceImpl.recordPayment` marks arbitrary request-submitted payments successful and invoices paid without provider confirmation, with no effective idempotency. Integrate verified gateway settlement/webhooks and reconciliation before accepting real money.
4. **Make auto-renew financially real and safe.** `SubscriptionAutoRenewScheduler` records renewals as SUCCESS without payment. Add payment result handling, idempotent processing and concurrency protection before granting renewed entitlements.
5. **Make schema changes deterministic.** Disable Hibernate `ddl-auto: update` in production; designate and verify the migration lineage (Flyway vs separate `my-database-migration-project`); exercise clean install and upgrade migrations against production-like PostgreSQL.
6. **Correct customer quotes/invoices.** Pricing omits configured cluster, ETA, coupon and subscription discounts; service requests accept caller amounts; billing hard-codes zero tax and uses timestamp invoice numbers. Use validated immutable quote snapshots and configured tax/unique invoice numbering before charging customers.
7. **Constrain service evidence and completion.** Do not accept arbitrary file URLs as proof. Enforce uploader/resource access, controlled storage and evidence validation; honor configured visit limits and mandatory visit rules before completion.
8. **Prepare production deployment and data recovery.** `docker-compose.yml` sets dev mode and literal database credentials. Add a production profile and secret handling, tested backups/restores, deployment health/rollback and operational alerts before launch.
9. **Prove core financial and authorization behavior.** Add automated PostgreSQL integration tests for authorization boundaries, price-to-invoice consistency, gateway settlement/idempotency, renewal retries, and completion evidence requirements; require them in CI.

**Recommendation:** NOT READY for production deployment. A controlled internal/demo environment can be used with synthetic data only after securing its credentials.
