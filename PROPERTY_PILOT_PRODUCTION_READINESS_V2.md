# PropertyPilot Production Readiness V2

Static code review scores. A score estimates demonstrated implementation, not intended design. No tests, build, deployment, penetration test, or live database validation were executed.

| Dimension | Score | Evidence-based rationale |
|---|---:|---|
| Business completeness | **57%** | Customer/property/lead/deal/service request/visit/evidence/pricing/subscription foundations exist. Health check is not composed; configured discounts omitted; monitoring tables lack workflow; notification reminder only logs; multi-visit constraints ignored; marketplace work conflicts with business positioning. |
| Architecture | **54%** | Layered Spring package structure exists, property/document ownership patterns and transactional services exist. Inconsistent API prefixes, duplicate repositories/workflow ownership, unmatched workflow interface, many persistence-only domains and legacy client roots reduce confidence. |
| Security | **30%** | JWT and ownership checks are real for property/document/lead/deal operations. But secret is hardcoded in base config and inherited by prod; service request/payment/evidence paths lack equivalent ownership validation; payment role checks permit broad customer access; broad auth fallback and inconsistent role naming. `BillingController` requires `ADMIN` authority while user details emits `ROLE_ADMIN`, so admin invoice operations are denied under the configured mapping. |
| Database | **52%** | Flyway migrations, PostgreSQL, constraints/indexes and optimistic versions present. Production profile exists and uses env DB credentials, but inherits `ddl-auto:update`; separate migration histories/docs SQL add ambiguity; no verified production backup/restore evidence. |
| Testing | **48%** | 78 Java test files include many service tests, controller tests, scheduler tests, auth/security/config tests. Static evidence suggests substantial Mockito/unit coverage but no proven full PostgreSQL integration, payment-provider contract tests, coverage gate or successful execution in this audit. |
| Performance | **40%** | `open-in-view:false`, Hikari configuration and some indexes/version fields are positive. Collection APIs frequently return full lists; billing `findAll`; completion validation does repeated existence calls over visits; no load results or capacity controls. |
| Deployment readiness | **28%** | A non-root Docker image, production profile, graceful shutdown and actuator health exist. Compose forces dev with literal DB credentials; base secret/schema/logging settings leak into prod; no checked-in orchestrator, CI release, backups/restore, rollback or alert runbooks evidenced. |

## Final score and verdict

**Production readiness: 41%** (weighted judgment across the seven dimensions; security and financial integrity are gating factors). **Recommendation: NOT READY for customer production or enterprise claims.**

**Business functionality estimate: 57%.** **Frontend readiness estimate: 5%** (143/154 React `src` files are at most 150 bytes; inspected route/API/page files contain scaffold prose, package/build configuration is also prose; mobile is a counter demo; admin portal is a README and empty source tree). Scores are conservative and do not count schema/entity presence as completed workflows.

### Critical blockers

1. Production JWT key is embedded in `propertypilot-backend/src/main/resources/application.yml` and inherited by `application-prod.yml`.
2. `PaymentServiceImpl.recordPayment` sets payment SUCCESS and invoice PAID without processor verification.
3. Service-request/payment/evidence resources lack consistent customer/agent ownership enforcement; authenticated callers can submit target IDs.
4. Auto-renew creates a new active subscription and SUCCESS renewal without payment confirmation.
5. Production profile inherits Hibernate `ddl-auto: update`, creating schema drift risk despite Flyway.
6. Price/quote/invoice integrity is incomplete: discounts omitted, customer-supplied amount accepted, fixed zero tax and weak invoice number generation.
7. Frontend build artifacts/configuration are not valid/complete, and mobile/admin clients are absent.
8. Production deployment, recovery and observability proof (backup restore, release/rollback, alerts) is absent from checked-in deployment configuration.
9. `BillingController`'s `hasAuthority('ADMIN')` is incompatible with `CustomUserDetailsService` authorities (`ROLE_ADMIN`), blocking configured admins from the billing endpoints.

### Effort to a controlled production deployment

Estimated **45–80 engineer-days for a production API/backend pilot with a separate already-working client**, excluding product design, provider onboarding/legal reviews, data migration, and external security assessment. PropertyPilot’s current React client is not implemented; include **another 20–40 days** to build a usable web client (about **65–120 days total**). Add roughly 10–25 days if a mobile or separate admin client is also a launch requirement. Payment, access control, production config, migrations, and tests remain non-negotiable.

