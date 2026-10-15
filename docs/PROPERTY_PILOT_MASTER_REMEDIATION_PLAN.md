# PropertyPilot Master Remediation Plan

Effort in engineering days, rough ±50%. Sequence within each phase respects dependencies. Any production/customer launch is blocked until Phases A–C and launch-scope integration testing are complete.

## Phase A — Critical Production Blockers

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P0 | Remove hardcoded JWT secret; require injected secret; rotate key; suppress SQL/bind logging in every production profile. (`application.yml`, `application-prod.yml`, `JwtService.java`) | 1–2d | Secret store/config pipeline | Exposed key permits token forgery; log may expose sensitive binds. |
| P0 | Establish production configuration and migration authority: prod DDL validate, Flyway sole schema writer; clean/upgrade PostgreSQL migration test; discard credentials/dev profile from deployment. | 3–6d | Environment and PostgreSQL test service | Startup or data-schema drift can cause outage/data corruption. |
| P0 | Build authorization inventory and close BOLA: requests, invoices/payments, evidence/GPS, visits, subscriptions and customer data. Bind principal identities and explicit role/ownership policy. | 5–10d | Role definitions, API contract | Cross-customer disclosure or mutation. |
| P0 | Payment provider architecture decision, verified webhook settlement, stable idempotency and reconciliation; invoice state changes only on confirmed settlement. | 7–14d | Provider account, legal/compliance decisions | Financial loss and false receipts. |
| P0 | Production recovery/deployment controls: secret delivery, backups, restore drill, health/readiness, rollback and on-call alert path. | 4–8d | Deployment platform/DB ownership | Irrecoverable data loss or prolonged outage. |

## Phase B — Security

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P1 | Normalize `/api/v1` route map, `hasRole`/`hasAuthority` semantics, public registration rules and deny-by-default authorization. | 3–6d | Phase A authorization policy | Endpoint accidentally public or unusable. |
| P1 | Enforce identity/resource checks at service boundaries for service requests, evidence, visits, payments, subscriptions, and customer operations. Reuse `SecurityService` patterns already applied to properties/docs/leads/deals. | 3–6d | P0 identity map | IDOR exposure. |
| P1 | Move evidence to controlled object storage; validate content/type/size and uploader ownership; prevent arbitrary URL evidence submission. | 5–10d | Storage provider and access model | Forged evidence, malware, privacy leakage. |
| P1 | Harden registration/reset/MFA lifecycle; connect existing OTP/session entities and expire/revoke tokens if required by launch policy. | 4–8d | Auth UX/client | Account takeover/recovery weakness. |

## Phase C — Financial Integrity

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P0 | Complete pricing rule pipeline: cluster, ETA, coupon, subscription, promotional discounts; enforce order/caps/eligibility and round consistently. | 6–12d | Discount policy and Phase A payment model | Wrong quotes or margin. |
| P0 | Persist quote snapshots/line items using existing estimate and quote entities; bind service request and invoice to accepted snapshot. Reject request-supplied authoritative price. | 4–8d | Pricing pipeline | Price drift and disputes. |
| P0 | Replace subscription auto-renew “SUCCESS” with charge-confirmed idempotent renewal; concurrency-safe job, failure/retry/notification. | 5–10d | Payment provider/webhook | Free renewals or duplicate charges. |
| P1 | Invoice tax/currency policy, line items, unique invoice numbering, refunds/credit notes and reconciliation. | 4–8d | Finance policy, pricing snapshot | Tax/accounting misstatement. |

## Phase D — Workflow Completion

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P1 | Enforce visitMandatory/maxVisitsAllowed; support multiple visits and required evidence policy by service; ensure completion validates required state. | 3–6d | Product rules for max/per-visit evidence | Service incorrectly marked complete. |
| P1 | Deliver Property Health Check as configurable composed assessment/report; keep independent physical and document services purchasable. | 6–12d | Checklist/report format | Core business offer not deliverable. |
| P1 | Implement notification outbox/provider/retries. Replace reminder scheduler logging with deduped customer delivery and observability. | 4–8d | Notification channel provider | Missed customer commitments. |
| P2 | Decide monitoring domain and implement schedule/alert flow or explicitly defer/retire persistence-only monitoring module. | 5–12d | Product definition | Misleading feature claims and unused data. |
| P2 | Consolidate agent assignment lifecycle; remove unused throwing methods and unmatched workflow service interface after caller/API review. | 2–4d | Workflow contract | Divergent state transitions. |
| P2 | Resolve marketplace/vendor pages: remove marketplace customer-facing concept; retain internal fulfillment integration only where needed. | 2–6d | Product decision | Business model confusion. |

## Phase E — Performance and reliability

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P2 | Add pagination and filters for customer/admin collections; replace unbounded `findAll`; add indexes from query plans. | 4–8d | API contracts | Memory/latency growth. |
| P2 | Batch evidence/GPS completion validation and verify generated SQL with PostgreSQL plans. | 2–4d | Phase D workflow | DB load grows with visit/evidence volume. |
| P2 | Add structured logs, metrics, traces, alert thresholds and operational dashboards for API errors, job outcomes and payments. | 4–8d | Monitoring stack | Failures remain undetected. |

## Phase F — Nice To Have / enterprise expansion

| Priority | Item | Effort | Dependencies | Risk |
|---|---|---:|---|---|
| P3 | Rebuild valid React package/build config and align UI to actual OpenAPI routes; decide if mobile/admin portals are in launch scope. | 8–20d | Stable backend contract | Client integration slips. |
| P3 | Evaluate persistence-only entities/repositories and retire unused schema after checking real data, migration history and roadmap. | 2–8d | Usage/data audit | Premature removal can lose data. |
| P3 | Add customer export, operational analytics and multi-city capacity optimizations after baseline load measurement. | 5–15d | Production metrics | Premature optimization. |

## Recommended sprint sequence

1. Sprint 1: Phase A secret/config/migration baseline and resource authorization design; implement highest-risk service request/payment/evidence access controls.
2. Sprint 2: Payment provider settlement/idempotency and price snapshot linkage; add PostgreSQL integration tests and authorization tests.
3. Sprint 3: Renewal collection workflow, invoice/tax policy, quote discounts and financial reconciliation.
4. Sprint 4: Visit/evidence completion rules, notification delivery and Health Check workflow.
5. Sprint 5: production deployment/recovery drill, observability, frontend contract alignment and limited pilot gate review.

