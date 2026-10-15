# PropertyPilot Pilot Release Plan

**Assessment date:** 2026-10-06  
**Scope:** Current repository source, tests, migrations, and frontend files, checked against Business Truth, Architecture Truth, Release Scope V1, Master Execution Plan, Monitoring Gap Analysis, and Monitoring Implementation Plan.  
**Code changes:** None.

## 1. Executive Summary

PropertyPilot has substantial backend foundations for a service business: authentication, property and customer records, service configuration, pricing estimates, quote decisions, request lifecycle, assignment, visits, evidence/GPS, billing records, subscriptions, and operations reporting. It is not yet a safe pilot system for taking real customer money. The most material gaps are payment settlement integrity, inconsistent authorization enforcement, a non-runnable frontend, and missing production evidence for database migrations and end-to-end workflows.

The Monitoring Foundation now creates durable schedule/run/request links and reuses the ordinary Service Request model. It does not implement the customer value layers described in the Monitoring and Health documents: pause/resume, operational alert handling, assessed findings, recommendations, or Property Health assessments. There are no Health assessment or recommendation controllers/services/entities in the backend source.

**Frontend development should start now**, because backend APIs already support useful slices and the customer experience needs work. Start with making the React workspace executable, then integrate one thin, end-to-end direct-service journey. Do not expose payment, subscription checkout, or Health/Monitoring promises to customers until their server-side flows are safe and complete. The current frontend directory is not a functioning application: `package.json`, `src/App.tsx`, route files, and several screen files contain setup notes or directory outlines instead of a valid app; `package.json` fails JSON parsing.

### Current recommendation

**No-go for a real-customer pilot that accepts payment today.** Proceed with frontend implementation and backend stabilization in parallel. A staff-assisted, limited pilot can begin after the critical gates below pass: access isolation, provider-verified payment, quote-to-request-to-invoice correctness, a functioning customer/operations web client, and production-like database/deployment verification.

The pilot should remain a PropertyPilot-owned service operation. Agents and vendors remain internal fulfillment resources. Do not activate marketplace listing/contact flows.

## 2. Current Project Status

| Area | Status today | Code evidence |
|---|---|---|
| Backend | Broad domain foundation; several workflows are implemented, but money, resource access, and some customer/ops paths need release verification. | Controllers and services under `propertypilot-backend/src/main/java/com/propertypilot`; Flyway migrations through `V033__monitoring_run_foundation.sql`. |
| Frontend | Not runnable as checked in; feature directories contain placeholder/instructional text and package metadata is invalid. | `propertypilot-frontend/package.json`, `src/App.tsx`, `src/app/routes/AppRoutes.tsx`, and feature pages. |
| Mobile/admin portal | No separate production-ready mobile application or separate admin portal was found in the inspected repository. The current client tree is under `propertypilot-frontend`. | Repository file inventory; React feature folders include customer/admin/operations page names, but do not constitute a runnable app. |
| Monitoring | Phase 1 foundation: schedule, immutable run ledger, scheduler, and canonical Service Request link. | `MonitoringScheduleEntity`, `MonitoringRunEntity`, `MonitoringScheduleServiceImpl`, `MonitoringRunServiceImpl`, `MonitoringScheduleController`, `MonitoringScheduleScheduler`, `V033`. |
| Property Health | Missing as an executable backend workflow. | No Health assessment, scoring, finding, or recommendation entity/service/controller implementation found under backend main source. |
| Tests | Latest generated Surefire reports show 586 tests, 85 suites, 0 failures/errors/skips. Monitoring has 5 unit tests; its tests use mocks. No monitoring-specific integration/controller tests or verified coverage percentage. | `propertypilot-backend/target/surefire-reports`; monitoring tests under `src/test/java`. |
| Production operations | Baseline profile, Flyway, Hibernate validation, health probes, and INFO logging exist. Operational proof for migration upgrade, restore, alerting, and end-to-end pilot transactions is not established by source/tests inspected. | `application.yml`, `application-prod.yml`, `SecurityConfig`, test reports. |

## 3. Capability Assessment Matrix

**READY FOR PILOT** means the current implementation appears usable for a narrow pilot slice after normal release verification. **PARTIAL** means meaningful code exists but the full business workflow or required controls are incomplete. **MISSING** means no usable end-to-end capability was found for the described business need. A status describes implementation, not merely the existence of a table or controller.

| # | Capability | Status | Evidence and present boundary |
|---:|---|---|---|
| 1 | Authentication & Security | PARTIAL | JWT login, roles, `SecurityService`, resource checks, and security filters exist. Authorization is inconsistent across APIs; an `/api/admin/**` filter rule admits only ADMIN even where controller policy also permits OPERATIONS. All endpoints need a role/resource pass before live data. |
| 2 | Customer Management | PARTIAL | Customer/user/profile, address, verification, and preferences exist. Onboarding is fragmented across user creation, customer linkage, and verification; a cohesive customer journey is not proven. |
| 3 | Property Management | PARTIAL | CRUD/archive/restore endpoints and owner/property entities exist. Service logic derives new property customer from authenticated customer and checks ownership on ID operations, but shared ownership/access behavior needs end-to-end validation. |
| 4 | Property Ownership | PARTIAL | `PropertyOwnerEntity`, `PropertyOwnerController`, and ownership checks exist. Primary property ownership is also represented through `Property.customer`; full multi-owner access semantics are not established throughout dependent workflows. |
| 5 | Lead Management | PARTIAL | Lead creation, read, status, assignment and activity services exist. Lead-to-customer/deal conversion and access boundaries need a verified complete sales path. |
| 6 | Deal Management | PARTIAL | Deal, participant, milestone/activity, status, finalize/cancel, request links, and billing code exist. Controller contains several broad operations; a validated, idempotent Deal → one or more priced requests path is not proven end to end. |
| 7 | Service Catalog | PARTIAL | `ServiceEntity` configuration and admin create/update/read APIs exist, including visit/evidence/GPS/remote/billable fields and eligibility. Versioned snapshots of every policy applied to an in-flight request are not established. |
| 8 | Pricing Engine | PARTIAL | Pricing rules/profiles/parameters, estimate persistence, line items, and breakdown services exist. Calculation endpoint is restricted to ADMIN/OPERATIONS, so a customer purchase journey must have a deliberate staff-assisted or customer-safe quote-creation path. Verify each configured launch price and discount result before selling. |
| 9 | Quote Lifecycle | PARTIAL | `QuoteServiceImpl` supports customer list/read/accept/reject/expiry; snapshot/line items are persisted. Quote creation is routed through operations/admin pricing calculation, and the complete customer quote-to-invoice experience is not wired as a frontend. |
| 10 | Subscription Management | PARTIAL | Plan/version/benefit/entitlement/consumption, renewals, pause, add-ons and schedulers exist. Do not sell subscriptions in the pilot: provider-backed collection and entitlement integrity are not demonstrated; renewal/payment behavior needs review. |
| 11 | Service Request Lifecycle | PARTIAL | Request creation requires an accepted quote and rejects duplicates; status history and completion validation exist. The entire request lifecycle, actor controls, cancellation/refund behavior, and billing handoff need release tests. |
| 12 | Agent Assignment | PARTIAL | Assignment services/entities, request assignment actions and agent dashboard exist. Manual operations assignment can support a narrow pilot after verifying one authoritative lifecycle and agent ownership checks. |
| 13 | Visit Management | PARTIAL | Visit create/read/start/complete/cancel services and controller exist. Configurable no-visit and multi-visit behavior needs verification against completion rules and role/resource access. |
| 14 | Evidence Management | PARTIAL | Evidence type, storage reference, checksum, captured time, and service-driven completion validation exist. `EvidenceServiceImpl` accepts a caller-supplied `fileUrl`; controlled upload/download, review, retention and signed resource access are not established. |
| 15 | Monitoring Services | PARTIAL | V033 and Phase 1 code support subscription or one-time acquisition, schedule/run linkage, scheduler, and ordinary requests. No pause/resume command, missed-visit notifications/rescheduling, monitoring finding/alert customer workflow, or reporting flow is implemented here. |
| 16 | Property Health | MISSING | No assessment entity/service/controller, weighted score, checklist result, or customer report workflow found. A service catalog row alone does not implement Property Health. |
| 17 | Recommendations | MISSING | No recommendation domain or customer accept/decline and conversion workflow found. No finding → recommendation → quote/request revenue path. |
| 18 | Notifications | PARTIAL | Notification and template entities exist; subscription notification read service and reminder/expiry schedulers exist. General service/assignment/visit/payment notification delivery, channel adapters, delivery retry and failure visibility are not established. |
| 19 | Customer Dashboard APIs | PARTIAL | `CustomerDashboardController` and `CustomerDashboardServiceImpl` return basic property/request/subscription/invoice/payment counts and validate customer ownership. It does not provide the full Business Truth property timeline, visits/evidence, monitoring/health, recommendations, and savings surface. |
| 20 | Admin Operations APIs | PARTIAL | Operations dashboard, monitoring counts, service-request audit and SLA endpoints exist. Filter-level ADMIN-only rule conflicts with controller’s ADMIN/OPERATIONS declaration under `/api/admin/**`; operational coverage is limited. |
| 21 | Billing & Invoicing | PARTIAL | Invoice creation/list/read and deal/subscription billing paths exist. `BillingServiceImpl` fixes tax to zero, generates invoice numbers from current time, and the controller exposes customer invoice lists only to ADMIN. Service request quote billing is not demonstrated as a complete customer path. |
| 22 | Payments | PARTIAL | Payment and invoice records, customer ownership checks, and endpoint exist. `PaymentServiceImpl.recordPayment()` accepts request fields, marks the payment `SUCCESS`, and marks invoice `PAID` without provider verification or webhook; not safe for real customer collection. |
| 23 | Reporting | PARTIAL | Report/review services and operations/audit/SLA reports exist. Consistent delivered reports for all V1 services and accessible customer outcome artifacts are not proven. |
| 24 | Audit & Compliance | PARTIAL | `audit_log`, status histories, review records and monitoring-run audit insertion exist. Coverage is not universal for all business mutations, and system-run actor/correlation attribution is limited. |
| 25 | File Management | PARTIAL | Document, document version/type, property document and evidence records and APIs exist. Controlled object storage, scoped upload/download authorization, retention, and production access lifecycle are not demonstrated. |
| 26 | Geo / GPS Validation | PARTIAL | Property location, GPS verification and configurable service GPS radius exist; completion checks GPS where required. Coverage/zone/agent territory and travel pricing are not proven as one enforced pilot path. |
| 27 | Scheduling | PARTIAL | Visit scheduling and monitoring due-cycle scheduler exist; subscription renewal/reminder and quote expiry schedulers also exist. Time-zone policy, pause/missed execution controls, alerting, bounded batches, and multi-instance operation tests are absent from monitoring tests. |
| 28 | Deployment Readiness | PARTIAL | `application-prod.yml` uses external DB/JWT secrets; Hibernate validates schema and Flyway is enabled; health probes and graceful shutdown are configured. Deployment/rollback/backup restore and PostgreSQL migration verification are not evidenced. |
| 29 | Observability | PARTIAL | INFO logs, Actuator health/info, and error logs exist. No demonstrated metrics/alerts, trace/correlation scheme, scheduler backlog/failure alert, or delivery/payment operational dashboards. |
| 30 | Production Operations | MISSING | Operational screens are currently placeholder text; no verified go-live runbook, support/escalation process, restore drill, payment reconciliation process, or production incident evidence in executable code/tests. |

## 4. Frontend Readiness Matrix

Here **READY FOR FRONTEND** means backend contracts exist for beginning the corresponding client work. It does not mean a finished screen exists. The present React directory is not runnable or production-integrated; restore the app shell/build first. The existing feature names and API files should be treated as scaffolding until replaced with real components and verified against these controllers.

| Frontend area | Backend status for starting UI | Integration condition |
|---|---|---|
| Login | READY FOR FRONTEND | `POST /api/v1/auth/login` exists. Implement token/session handling, expiry, unauthorized states and role navigation. |
| Customer Dashboard | READY FOR FRONTEND | `GET /api/customer-dashboard/{customerId}` exists with ownership validation; use current identity rather than allowing arbitrary customer ID selection. |
| Property Screens | READY FOR FRONTEND | Property CRUD APIs and ownership logic exist; verify owner access contract and consistent response validation. |
| Lead Screens | READY FOR FRONTEND | Lead APIs exist; constrain sales/admin views and verify conversion path before presenting conversion actions. |
| Deal Screens | READY FOR FRONTEND | Deal APIs exist; limit to internal users until each action’s authorization and request conversion are verified. |
| Service Request Screens | READY FOR FRONTEND | Quote-based request creation and lifecycle APIs exist; use accepted quote, never client-supplied amount/status/actor. |
| Quote Screens | READY FOR FRONTEND | Customer quote list/read/accept/reject exists; quote creation/pricing API currently requires operations/admin access. |
| Subscription Screens | READY FOR FRONTEND (read only / mock) | Plan and subscription APIs exist, but hide purchase/renewal until payment-backed entitlement is safe. |
| Visit Screens | READY FOR FRONTEND | Visit controller/service supports execution operations; agent/customer access and status transitions require integrated authorization tests. |
| Evidence Screens | NOT READY FOR FRONTEND integration | Evidence APIs exist, but the file URL boundary is not a secure upload/download contract. Build UI against a temporary mock only until scoped storage is available. |
| Monitoring Screens | READY FOR FRONTEND (limited) | Customer schedule/run reads exist. Show history/status only; creation is staff-only and there is no pause/resume or alert workflow. |
| Property Health screens | NOT READY FOR FRONTEND | No Health API workflow exists. |
| Admin Screens | READY FOR FRONTEND (limited) | Catalog/pricing/operations APIs exist, but role mismatch and access verification must be fixed before integration. |
| Payment / Invoice Screens | NOT READY FOR payment integration | Invoice/payment records can be read in restricted ways, but no verified gateway contract exists; do not ship collect/paid UI against manual `SUCCESS` behavior. |
| Reports / Notifications Screens | PARTIAL | Report and subscription notification endpoints exist; full delivery and customer outcome coverage are incomplete. |

### Screens that can start immediately

1. App shell, login, session handling, role-aware navigation, shared API error/loading handling.
2. Customer property list/detail/create/edit, using authenticated customer context.
3. Customer dashboard summary.
4. Service catalog browse and quote list/detail/accept/reject.
5. Internal lead/deal list/detail and operations request/assignment views, initially behind staff roles.
6. Visit schedule/list/detail for internal staff/agents once endpoint authorization is confirmed.

### Screens blocked from live integration

- Checkout/payment success, paid invoice state, and subscription purchase/renewal.
- Evidence upload/download until controlled storage and signed access are exposed.
- Health score, findings, recommendations and recommendation acceptance.
- Monitoring pause/resume, missed-run response, alerting and customer reports.
- Any admin/operations route protected inconsistently by controller and HTTP security filter.

### Recommended frontend order

1. Replace placeholder files with a valid React build, route tree, auth/session/API layer and error handling.
2. Login and customer account/property screens.
3. Catalog → quote view/decision → accepted quote → service request status.
4. Staff operations request queue → assignment → visit scheduling and status.
5. Secure evidence and customer completion/report view after backend upload and visibility boundaries are defined.
6. Invoice/payment UI only after provider-confirmed settlement; keep subscription checkout hidden.
7. Monitoring read-only schedule/run view; add Health screens only after those APIs exist.
8. Lead/deal/admin configuration and reporting screens according to verified role contracts.

## 5. Pilot Readiness Matrix

| Priority | Remaining gap | Why it matters for the pilot | Minimum exit condition |
|---|---|---|---|
| CRITICAL | Resource authorization and role policy reconciliation | A pilot must not expose customer, property, request, evidence, invoice, or payment records across users. Operations endpoints also have ADMIN/OPERATIONS policy mismatch. | Endpoint-by-endpoint tests using CUSTOMER, AGENT, OPERATIONS, and ADMIN identities, including cross-customer IDs and forbidden operations. |
| CRITICAL | Real payment settlement | Current API can record client-submitted payment details and mark invoice paid without provider confirmation. This can create false paid orders and financial loss. | Provider integration with verified callback/webhook, stable idempotency, pending/failure/success/refund states, reconciliation, and server-controlled invoice settlement. |
| CRITICAL | Runnable frontend and customer transaction path | There is no usable client to sell/operate the service. Package metadata is invalid; feature files are scaffold text. | Clean install/build/start, browser-tested login and narrow customer purchase/status path, plus staff fulfillment path. |
| CRITICAL | Quote → request → invoice/payment linkage | Quote and request portions exist, but invoice issuance for service requests and payment completion are not proven as a coherent paid order. | Accepted quote snapshot produces one request and one correct invoice; payment success is provider-derived; repeated calls do not duplicate work or money. |
| CRITICAL | Production database and deployment verification | V033 includes DB-specific constraints/triggers and JPA locks; current unit mocks do not validate PostgreSQL behavior or migration upgrade. | Fresh install and upgrade migrations pass against PostgreSQL; backup/restore and deployment/rollback rehearsed; Hibernate validation passes. |
| CRITICAL if marketed in Pilot | Property Health offer | Business Truth treats Health Check as a service; no assessment, findings, score, recommendations or report workflow exists. | Either exclude Health from the actual pilot catalog/claims, or implement a bounded checklist/evidence/report offer before selling it. |
| CRITICAL if marketed in Pilot | Monitoring offer beyond Foundation | Phase 1 persists cycles and links requests, but cannot deliver findings/alerts, pause/resume, missed-visit notifications, or report value. | Either defer customer-facing Monitoring SKU, or deliver one bounded monitoring operation with human review, customer result, pause/expiry handling and admin alert process. |
| HIGH | Evidence and document storage security | A URL field is not a secure customer evidence/document vault; proof of work and property documents are sensitive. | Controlled storage, type/size validation, scoped upload/download, authorization, and audit of access. |
| HIGH | End-to-end service completion policy | Service configuration and completion validator exist, but no-visit/multi-visit/evidence/GPS combinations need integrated proof on launch catalog. | PostgreSQL-backed tests for each offered service policy; no manual bypass of required proof. |
| HIGH | Customer onboarding/verification | Account, customer, and verification code exists but journey is fragmented. Pilot support burden and incorrect ownership risk rise without a clear onboarding path. | One documented registration/admin-create/verification path that binds exactly one customer identity correctly. |
| HIGH | Operational notification and support | General service notifications are not fully delivered; staff need to know assignment, overdue service, payment failure and monitoring exceptions. | Minimum template-driven customer request/payment/status notices and admin failure/overdue queue or documented staff process. |
| HIGH | Invoice tax, numbering, and customer visibility | Tax is fixed zero, invoice IDs use timestamp-based numbering, and customer invoice list endpoint is admin-only. | Approve pilot tax/currency policy, unique invoice number generation, and authorized customer invoice access. |
| HIGH | Monitoring scheduler recovery and load | Runs are serialized per schedule and errors logged, but all due rows are loaded at once and no monitoring integration tests exist. | Bounded processing, retry/failure visibility, schedule lock and unique-key concurrency tests. |
| MEDIUM | Full subscription lifecycle | Can avoid subscription complexity for first direct-service revenue pilot; incomplete renewal/payment creates risk if left exposed. | Feature-disable or correct paid purchase/renewal and usage consumption before offering plans. |
| MEDIUM | Advanced Leads/Deals automation | Staff can run basic sales manually; complex forecasting and CRM automation are not required for a narrow pilot. | Basic assignment/activity and controlled accepted-deal service conversion only if used by the pilot. |
| MEDIUM | Full dashboards/reporting | Basic customer and operations summaries exist. More detailed analytics can follow if staff can run the narrow service using records and reports. | A daily operational queue and customer-visible completion artifact for each offered service. |
| LOW | Multi-city coverage, route optimization, broad service catalog, AI recommendations | Not needed to validate one geography and small service set. | Defer until unit economics and service execution are measured. |

### Critical gaps remaining in the backend

1. Payment service requires a real provider confirmation boundary; current success is application asserted.
2. Resolve endpoint-level and object-level authorization consistently. `SecurityConfig` protects `/api/admin/**` as ADMIN-only while some admin operations controllers allow OPERATIONS; several legacy controllers rely on service checks or only `.authenticated()`.
3. Make accepted quote, request, invoice, and payment a single recoverable, idempotent financial workflow; verify service request invoice issuance rather than assuming it from entity/controller existence.
4. Enable pilot tax/currency and invoice numbering policy; `BillingServiceImpl` currently writes zero tax and generates invoice numbers from system time.
5. Implement or remove from the marketed catalog any capability advertised as Property Health or Monitoring value beyond schedule/request creation.
6. Add secure document/evidence object storage boundary and prove access isolation.
7. Test migrations, locking, transactions and endpoint permissions against PostgreSQL and HTTP security filters.
8. Provide operational failure/overdue visibility and notification delivery or a formally supported manual runbook.

## 6. Recommended Frontend Start Point

Start with the customer/staff web client foundations in `propertypilot-frontend`:

- Create valid package metadata and a minimal working app entry point and router; current package metadata is not valid JSON and `App.tsx`/route files are directory/setup outlines.
- Implement login against `AuthController` and retain only the authenticated identity/token required by the API.
- Build the narrow customer journey: property → active service catalog → staff-generated quote display → quote decision → request tracking.
- Build a staff queue for quote preparation/requests and manual agent assignment.
- Use mock fixtures where a safe backend contract does not yet exist; label and isolate those screens so mock status cannot appear as a paid/fulfilled production state.

Frontend coding can proceed now. Live deployment integration should be staged behind the critical backend gates above.

## 7. Recommended Backend Priorities

1. **Close authorization holes and route-policy mismatches.** Inventory every controller method and service-level ownership check. Add role and resource tests, especially Customer/Agent/Operations/Admin and customer-to-customer IDOR cases.
2. **Make direct-service money trustworthy.** Implement one payment provider’s create/confirm/webhook/refund path; stable idempotency; server-authoritative amount; invoice state changes only after verified settlement; link accepted quote/request/invoice/payment.
3. **Stabilize narrow service fulfillment.** Select one or two services; verify catalog policy, accepted quote, eligibility, assignment, optional/required visits, evidence/GPS, completion and customer result.
4. **Secure file handling.** Replace arbitrary file URL trust with controlled upload/download and resource-scoped access.
5. **Prove database behavior.** Run fresh/upgrade Flyway and integration/security tests on PostgreSQL; verify uniqueness, locks, immutable run rows, and rollback/restore procedure.
6. **Establish operations basics.** Staff queue, assignment visibility, stuck/overdue request and payment failure process, customer transactional notifications or an explicit manual support process.
7. **Hide unfinished revenue paths.** Disable subscription checkout/auto-renew, Health and Monitoring catalog offers unless their limited pilot implementations are completed and supported.

## 8. Recommended Pilot Scope

A safe first pilot should use one geography, a small catalog, manual staff quote preparation/assignment where needed, and direct one-time services only. Each sold service must have a clear customer price, quote acceptance, property linkage, completion rules, evidence requirements, customer outcome, invoice, and provider-confirmed payment.

Keep the following out of the first paid cohort unless their gaps are closed:

- Subscription purchase, renewal, and entitlement promises.
- Property Health claims (score/findings/recommendations) because those workflows are absent.
- Monitoring claims that imply alert review, missed-visit response, pause/resume, reports, or recommendations; the current foundation only schedules and links runs to requests.
- Any vendor marketplace or customer-vendor contracting surface.

This narrows the current Release Scope V1 assumptions: that document lists one bounded Health and Monitoring offer, but current code does not deliver their customer-facing outcomes. The minimum pilot can still validate PropertyPilot’s service revenue model with other configured services; do not keep an offer in scope solely because its tables exist. If Health or Monitoring is a must-have for the pilot, treat the missing outcome workflows as launch blockers and build them before advertising those offers.

## 9. Go / No-Go Recommendation

### A. Should frontend development start now?

**Yes.** Start now with restoring a valid React application and building auth, property, catalog, quote, request, and staff operations slices. Existing frontend files are not a production frontend and need implementation rather than polish.

### B. Should backend development continue first?

**Backend hardening must proceed alongside frontend development and blocks live pilot integration.** Prioritize authorization, verified payment/invoice flow, PostgreSQL integration coverage, and secure file access. Building only backend first would delay UI feedback; integrating against unsafe financial/security paths would expose customers.

### C. What is the minimum scope required for Pilot Launch?

- Authenticated customer and staff accounts with verified role/resource isolation.
- Customer onboarding and owned property management.
- A small PropertyPilot service catalog and approved configurable prices.
- Quote creation, immutable customer-visible quote and acceptance/rejection/expiry.
- Idempotent service request creation from accepted quotes.
- Manual internal assignment, visit where configured, evidence/document handling, configured completion validation, and customer outcome.
- Correct invoice and confirmed provider payment/refund state.
- Minimal customer and operations screens, audit trail, support/escalation process, and production PostgreSQL/deployment/restore evidence.
- Do not sell subscriptions, Health or Monitoring unless their specific lifecycle/outcome gates are met. A narrow pilot may postpone these without becoming a marketplace or violating the service-first model.

### D. What is the recommended next 90-day roadmap?

| Window | Focus | Exit checkpoint |
|---|---|---|
| Days 1–30 | Make frontend runnable; resolve auth/role/ownership issues; select pilot services and define their price/visit/evidence rules; connect login, properties, catalog and quote screens. | Cross-role HTTP tests pass; frontend clean install/build; no unbounded customer access; approved service policy and price. |
| Days 31–60 | Complete accepted quote → request → invoice → real payment provider flow; secure files; build staff assignment/visit and customer status screens; test request completion for each pilot service. | Provider verified settlement and idempotency; PostgreSQL end-to-end direct service passes; staff can deliver and customer can see the result. |
| Days 61–90 | Production-like migration/deployment rehearsal; backup restore; operational queue/alerts and support process; limited cohort pilot; fix issues found in actual operations. | Fresh install/upgrade, restore, monitoring, reconciliation and incident runbooks demonstrated; pilot gates signed off. |

**Go/no-go today: NO-GO for taking real customer payments.** Go for frontend development and backend remediation now. Reassess for a limited paid pilot only after the critical gates pass. The largest scope decision is whether to include Health and Monitoring in the first pilot: code currently does not support their full customer promises, so defer those offers unless their outcomes are implemented and verified.

## Evidence references

- Business and target architecture: `docs/Business/PROPERTY_PILOT_BUSINESS_TRUTH.md`, `PROPERTY_PILOT_ARCHITECTURE_TRUTH.md`.
- Release and execution scope: `PROPERTY_PILOT_RELEASE_SCOPE_V1.md`, `PROPERTY_PILOT_MASTER_EXECUTION_PLAN.md`.
- Monitoring inputs and current design: `PROPERTY_PILOT_MONITORING_GAP_ANALYSIS.md`, `PROPERTY_PILOT_MONITORING_IMPLEMENTATION_PLAN.md`, `PROPERTY_PILOT_PHASE_1_MONITORING_FOUNDATION_REVIEW.md`.
- Backend security: `propertypilot-backend/src/main/java/com/propertypilot/config/SecurityConfig.java`, `security/SecurityService.java`.
- Customer/property/service transaction: `web/controller/PropertyController.java`, `application/service/impl/PropertyServiceImpl.java`, `web/controller/QuoteController.java`, `application/service/impl/QuoteServiceImpl.java`, `web/controller/ServiceRequestController.java`, `application/service/impl/ServiceRequestServiceImpl.java`.
- Payment/billing: `application/service/impl/PaymentServiceImpl.java`, `application/service/impl/BillingServiceImpl.java`, `web/controller/PaymentController.java`, `web/controller/BillingController.java`.
- Monitoring: `V033__monitoring_run_foundation.sql`, `infrastructure/persistence/entity/MonitoringRunEntity.java`, `application/service/impl/MonitoringRunServiceImpl.java`, `web/controller/MonitoringScheduleController.java`, `application/scheduler/MonitoringScheduleScheduler.java`.
- Frontend state: `propertypilot-frontend/package.json`, `src/App.tsx`, `src/app/routes/AppRoutes.tsx`, `src/features/auth/pages/LoginPage.tsx`.
- Test state: `propertypilot-backend/target/surefire-reports`; `src/test/java/com/propertypilot/application/service/impl/MonitoringConfigurationServiceImplTest.java`, `MonitoringRunServiceImplTest.java`.
