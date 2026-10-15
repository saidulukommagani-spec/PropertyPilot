# PropertyPilot Deployment Readiness Checklist

Status is based on checked-in evidence. **Not evidenced** means this repository does not establish completion; verify operationally before go-live.

## Release decision

- [ ] Resolve all CRITICAL gaps in `PROPERTY_PILOT_GAP_MATRIX_V5.md`.
- [ ] Obtain security, finance, product and operations sign-off.
- [ ] Keep production launch blocked until payment settlement, authorization, migration and recovery checks pass.

## Application security

- [ ] Remove `security.jwt.secret` literal from `propertypilot-backend/src/main/resources/application.yml`; inject a rotated secret from a secret manager.
- [ ] Verify production startup fails if JWT/DB secrets are absent or too weak.
- [ ] Ensure production inherits no dev credentials, SQL bind logging, security DEBUG, test endpoints or permissive CORS.
- [ ] Verify role/resource authorization for each controller route, including ownership of properties, requests, visits, evidence, invoices, payments, subscriptions and documents.
- [ ] Verify registration, password reset, account verification, token expiration/revocation, rate limits and brute-force protection.
- [ ] Review uploaded files, signed URLs, retention, encryption at rest/in transit and malware/content handling.
- [ ] Review dependency vulnerabilities and container base image updates.

## Financial correctness

- [ ] Payment success can only follow a verified processor response/webhook; webhook signature/replay/idempotency behavior is proven.
- [ ] Invoice state is atomic with confirmed settlement and reconciliation; refunds/chargebacks are handled.
- [ ] Subscription renewal charges before entitlements extend; retries and concurrent scheduler runs cannot duplicate charge/renewal.
- [ ] Quotes include configured discounts and tax; accepted immutable quote snapshot is the source for service request and invoice totals.
- [ ] Customer response hides internal vendor, travel, DA, overhead and margin components where required.
- [ ] Invoice numbers have database-enforced uniqueness; currency, tax and rounding policy is approved.

## Database and migrations

- [ ] Production `spring.jpa.hibernate.ddl-auto` is `validate` or disabled; Flyway is sole schema authority.
- [ ] Confirm the production migration lineage is `propertypilot-backend/src/main/resources/db/migration`; classify the separate migration project and docs SQL as non-runtime/historical.
- [ ] Apply migrations to an empty production-like PostgreSQL instance and upgrade a copy of current production data.
- [ ] Verify constraints, indexes, FK delete behavior and uniqueness for payment idempotency, invoice numbers and job dedupe.
- [ ] Capture backup and complete restore drill; document RPO/RTO and retention.
- [ ] Establish migration rollback/forward-fix plan and prohibit destructive Flyway clean.

## Testing and quality gates

- [ ] CI builds frontend/backend and executes all unit/service/controller tests; record actual pass result and coverage report.
- [ ] Add PostgreSQL integration tests for Flyway, transaction boundaries, constraints, pricing and request/payment consistency.
- [ ] Add authorization matrix tests proving cross-customer/agent access is denied.
- [ ] Add gateway webhook/idempotency, renewal retry/concurrency, and evidence completion tests.
- [ ] Run API contract tests against OpenAPI and each supported client.
- [ ] Run load tests for target scale and verify pagination/query plans for list and completion paths.
- [ ] Complete security review/penetration test for auth, object access, file upload and payment callbacks.

## Deployment and operations

- [ ] Build immutable production image from `propertypilot-backend/Dockerfile`; scan image and run as non-root.
- [ ] Do not use `propertypilot-backend/docker-compose.yml` as production config; it activates `dev` and contains local DB credentials.
- [ ] Configure production DB URL/credentials, TLS, pool limits, migrations and connection monitoring.
- [ ] Configure readiness/liveness probes, graceful termination, CPU/memory limits and autoscaling bounds.
- [ ] Configure central structured logs, metrics, traces, error reporting and alerts for API failures, payment/renewal failures, scheduler lag, DB health and queue/outbox backlog.
- [ ] Define deployment rollback, incident response, data breach response and payment reconciliation runbooks.
- [ ] Prove backup recovery, application rollback and forward migration under release conditions.
- [ ] Set privacy retention, data deletion/export and audit-log access policy.
- [ ] Confirm notification delivery channel, retries and customer support escalation path.

## Client readiness

- [ ] Rebuild the React client: 143 of 154 current `src` files are ≤150 bytes and sampled routes/API/pages contain scaffold prose; `package.json`, Vite/TypeScript configs are prose, not valid build configuration. Add lockfile and reproducible CI build.
- [ ] Align frontend route/API modules with backend OpenAPI and current `/api` vs `/api/v1` paths.
- [ ] Decide whether Flutter and a separate admin portal are in initial launch scope; if yes, implement and test actual flows (mobile currently demo; admin currently scaffold).
- [ ] Verify responsive/accessibility behavior, auth expiry, validation, error handling and privacy-safe analytics.

## Go-live gate

- [ ] Product confirms MVP scope and unsupported features are not marketed.
- [ ] All critical findings closed and high-risk exceptions explicitly accepted.
- [ ] Migration, backup/restore, monitoring, security and financial integration evidence is attached to release record.
- [ ] Support/on-call owners and customer communication channels are staffed.
- [ ] Pilot with controlled traffic and reconcile all payments/renewals before broad rollout.

