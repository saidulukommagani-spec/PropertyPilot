# PropertyPilot Execution Roadmap V1

**Planning baseline:** 2026-10-06  
**Target:** Limited, paid Pilot Launch  
**Planning assumption:** Small dedicated team: 2 backend engineers, 1 frontend engineer, shared QA/DevOps support. Estimates change if staffing or payment-provider onboarding differs.

## 1. Current Position

### Completed foundations

- Backend foundations exist for login/roles, customers and properties, service catalog, pricing estimates, quote decisions and expiry, quote-backed service requests, assignment, visits, evidence/GPS validation, subscriptions, invoices/payments, reports, and operations summaries.
- Monitoring Phase 1 is implemented: schedule acquisition from subscription or a purchased request, run history, due scheduler, and linkage to the canonical Service Request flow (MonitoringRunEntity, MonitoringRunServiceImpl, V033).
- Production profile settings use external DB/JWT environment variables, Flyway, Hibernate validation, health probes, and application logging.

### Partially complete

- Core workflows have unit/controller tests, but there is no verified end-to-end PostgreSQL journey covering purchase through settlement and fulfillment.
- Security and operational APIs exist, but resource/role behavior needs to be made consistent before frontend integration.
- Billing and payment records exist, but payment settlement is currently application asserted; subscription renewal/entitlement paths should stay disabled for pilot.
- Frontend feature directories and API names exist, but the current React tree is placeholder material, not a runnable client.

### Not started for customer-ready release

- Runnable customer/staff frontend.
- Provider-verified payments and a complete quote → request → invoice → payment transaction.
- Property Health assessment, findings, recommendation acceptance/conversion.
- Monitoring operations beyond Foundation: pause/resume, missed-run handling, customer findings/alerts/reporting.
- Production-like migration, restore, alerting, and operating-procedure proof.

### Pilot blockers

1. Authorization must prevent cross-customer/resource access and give OPERATIONS the intended access without granting it accidentally elsewhere.
2. Payment SUCCESS and invoice PAID must depend on verified provider settlement, not request data.
3. A usable client and a complete, tested direct-service money/fulfillment flow must exist.
4. PostgreSQL migration, concurrency, backup/restore, and deployment must be rehearsed.
5. Any Health or Monitoring offer must be operationally deliverable. The recommended first cohort excludes these offers unless their limited customer outcome flows are completed.

## 2. Workstreams

| Workstream | Owner focus | Dependencies | Can run in parallel with |
|---|---|---|---|
| **A — Backend Core** | Stabilize service catalog → quote → accepted quote → request → assignment/visit/evidence/completion contracts. Keep one source of truth per lifecycle. | Security rules from D; payment contract from E where request/invoice transitions meet. | B frontend shell and F deployment baseline. |
| **B — Frontend Development** | Build the executable React customer and internal operations web client, then integrate the narrow pilot journey. | Stable contracts from A, D, and E. Can build shell/screens against documented DTOs and mocks before integration gates pass. | D, E, F backend work; C design/scoping. |
| **C — Monitoring & Property Health** | Decide offer scope; complete only the bounded Health/Monitoring workflows that will be marketed. Reuse Service Request, Visit, Evidence, Report, and notification paths. | A fulfillment; D ownership; E if the offer is paid; F persistence/operations. | B shell and D/E. If excluded from the first cohort, defer most implementation. |
| **D — Security & Authorization** | Reconcile URL filters, controller roles, service authorization, customer/agent ownership, JWT actor use, and resource-level tests. | None; start first. | All other workstreams. |
| **E — Payments & Billing** | Implement one provider integration, verified settlement, idempotency, invoice policy, refunds/reconciliation, and quote/request linkage. | Pricing/quote and request contracts from A. | D, B shell, F deployment setup. This lies on the paid pilot critical path. |
| **F — Deployment & Operations** | PostgreSQL migration pipeline, runtime secrets, health/metrics/alerts, backups/restore, deployment/rollback, support and reconciliation procedures. | Current schema and provider sandbox configuration; final acceptance depends on A/D/E/B. | Start baseline immediately; final rehearsal follows integration. |

### Critical path

**D security contract → A/E direct-service transaction → B integrated customer and staff journey → PostgreSQL end-to-end/security tests → F production rehearsal → pilot go/no-go.**

The first launch does not need the complete Subscription or Health/Monitoring roadmap if those offers are explicitly excluded. If the team decides they are launch offers, C becomes a critical-path dependency and the date moves accordingly.

### Parallel work

- Start B’s application shell and F’s deployment/observability baseline while D closes security policy.
- E can build provider sandbox and webhook handling while A stabilizes quote/request DTOs.
- C can define and prototype the smallest operational Health/Monitoring offer while the core revenue flow is built; avoid production promises until the underlying workflow is verified.
- QA can prepare role/resource test matrices and PostgreSQL fixtures before implementation merges.

## 3. Frontend Strategy

### Can frontend start immediately?

**Yes.** First make the client buildable: propertypilot-frontend/package.json is not valid JSON and the current app/route/screen files contain outlines or instructional text. Work can begin without changing backend architecture. Do not connect money or sensitive file flows until E and D exit their gates.

### API readiness and screen order

| Phase | Screens | API readiness | Work / dependency |
|---|---|---|---|
| **F1 — Client foundation** | App shell, login, session expiry, role-aware routing, API error handling | Login API exists at /api/v1/auth/login. | Replace placeholders with a valid React build/router and test login/unauthorized behavior. D supplies final role names. |
| **F2 — Customer basics** | Customer dashboard, properties, property details/ownership | Property CRUD and basic dashboard endpoints exist; dashboard ownership check exists. | Bind customer context to authenticated identity; do not accept arbitrary customer ID as client authority. Validate ownership API behavior with D. |
| **F3 — Revenue path** | Service catalog, quote list/detail, accept/reject, service request creation/status | Catalog, quote decision, and accepted-quote request APIs exist. Pricing calculation is staff-only today. | Build staff quote preparation with customer quote decision first. Customer self-service pricing can follow only if contract and access policy are approved. E defines invoice/payment UI contract. |
| **F4 — Fulfillment** | Lead/deal staff views, request queue, assignment board, visit scheduling/detail | Lead/deal/request/visit APIs exist, but workflows and permissions need D/A tests. | Start with internal users and manual assignment; defer unverified deal conversion actions. |
| **F5 — Completion and records** | Evidence, documents, completion report, customer request history | APIs/persistence exist, but upload URL is not a controlled storage contract and report delivery is incomplete. | Integrate after secure scoped upload/download, access checks, and report lifecycle are stable. |
| **F6 — Monitoring / Admin** | Monitoring schedule/run read view, service/pricing admin, operations dashboard | Limited monitoring read endpoints and admin operations/config endpoints exist. | Read-only Monitoring view can start after F1; admin integration waits on role reconciliation. Pause/alerts/Health screens depend on C APIs. |
| **F7 — Money and deferred offers** | Checkout, payment detail, invoice access, subscription purchase, Health/recommendations | Payment records exist but no verified provider settlement; Health APIs are absent. | Payment UI only after E. Keep subscription checkout and Health/recommendations hidden until their specific flows are ready. |

### APIs sufficient to begin

- Authentication login; property CRUD; basic customer dashboard.
- Service catalog read; customer quote list/read/accept/reject; quote-backed request creation.
- Staff lead/deal/request/assignment/visit views, provided the UI uses only confirmed role actions.
- Monitoring schedule/run history as a limited read-only feature.

“Sufficient to begin” means implement and test a client slice; it does not certify production readiness. Contract tests and D’s access gate are required before live customer data integration.

## 4. Backend Strategy

Estimates are engineer-days, rounded ranges. They assume reuse of the current model and a narrow pilot. Provider onboarding and jurisdiction-specific decisions are external schedule risks.

| Phase | Purpose | Effort | Dependencies | Pilot impact |
|---|---|---:|---|---|
| **B1 — Security & authorization** | Establish one role/resource policy across customer, property, lead/deal, request, agent, visit, evidence, invoice/payment and admin/operations endpoints; test cross-owner IDs and actor attribution. | 8–12 days | None; start first. | Hard gate: protects customer data and operations. |
| **B2 — Direct-service quote/order integrity** | Verify quote creation snapshot/acceptance, accepted-quote-only request, duplicate prevention, frozen price transfer, and a reliable request-to-invoice handoff. | 6–10 days | B1 role boundaries; current pricing/quote code. | Hard gate: each sold service has approved customer price and one work order. |
| **B3 — Payments & billing** | Implement provider checkout/confirmation, signed webhook, idempotent state transitions, server-side amount checks, invoice number/tax policy, failure/refund and reconciliation. | 12–20 days | B2; provider and tax/currency decision. | Hard gate for a paid pilot. |
| **B4 — Pilot fulfillment hardening** | Prove manual assignment, visit-required and no-visit services, configured evidence/GPS/completion rules, secure document/evidence storage, and customer result/report. | 10–16 days | B1/B2; chosen pilot catalog. | Hard gate for delivery and trust. |
| **B5 — Notifications and operational controls** | Minimum customer transaction/status notices or staffed manual equivalent; overdue/stuck/payment-failure queues; basic support escalation and audit. | 6–10 days | B1 and B2/B3 event points. | Strong pilot condition; manual controls can reduce automation if documented and staffed. |
| **B6 — Monitoring V1 offer (conditional)** | Turn Foundation into a bounded customer service: one selected frequency, one human-reviewed result, pause/expiry/admin exception process, audit, customer outcome. | 8–14 days | B1/B4; notification/ops path B5. | Required only if Monitoring is advertised in first cohort; otherwise defer. |
| **B7 — Property Health and recommendation revenue path (conditional)** | Deliver bounded checklist assessment/report, findings, simple weighted score if promised, recommendations and idempotent conversion to quote/request. | 15–25 days | B1/B2/B4; report/storage path. | Required only if Health is advertised; absent today, so including it expands the critical path. |
| **B8 — PostgreSQL integration and production proof** | Fresh install/upgrade Flyway, Hibernate validate, transaction/idempotency/concurrency/security tests, restore/deploy/rollback rehearsal. | 8–12 days | Schema-affecting B2–B7 work; harness can start early. | Hard gate before pilot data and payments. |

### Implementation sequence

1. B1 authorization policy and tests.
2. B2 direct quote/request/invoice contract while frontend shell begins.
3. B3 provider payment and money-state integrity.
4. B4 fulfillment and file boundary for selected services.
5. B5 customer/staff operational minimum.
6. B8 PostgreSQL end-to-end and recovery proof (test harness begins earlier; final sign-off here).
7. B6/B7 only if the product owner retains Monitoring/Health as first-cohort offers; prioritize one narrow offer at a time.

## 5. Pilot Launch Plan

### Recommended scope

| Decision | Recommendation |
|---|---|
| Geography | One contiguous coverage area in the city/region where PropertyPilot can staff field work and verify travel/GPS. Choose the actual area from agent capacity; no repository evidence supports selecting a particular city. |
| Services | Start with 3–5 clear, low-complexity service SKUs. Include at least one no-visit and one visit-required service only after each price, eligibility, SLA, evidence and completion policy is tested. Avoid legal/tax promises until delivery and jurisdiction requirements are approved. |
| Customer count | Cap the first cohort at about 20–30 customer accounts, with an explicit limit on active properties/work orders. This is an operating recommendation, not a current system limit. |
| Internal operations model | PropertyPilot owns quoting, customer communication, triage, assignment, service quality, report delivery and payment reconciliation. Use a named operations owner and manual queues where automation is not ready. |
| Agent model | Small, vetted internal fulfillment pool (for example, 3–5 agents sized to the chosen coverage area); manually assign and track acceptance. Customers contract with PropertyPilot, not agents/vendors. |
| Monitoring scope | Recommended: defer from the first paid cohort. If retained, sell one explicitly bounded human-reviewed monitoring service only after pause/expiry, missed-run escalation, customer output, and payment treatment are operational. Current Phase 1 is a schedule/run/request foundation, not the full offer. |
| Property Health scope | Recommended: defer from the first paid cohort unless a bounded checklist/report can be delivered reliably. Do not market a health score, findings, or recommendations before those workflows exist. If retained, constrain the checklist, evidence, scoring explanation, and report to one property type/service. |
| Subscriptions | Do not sell in first cohort; use one-time direct-service purchases until collection and entitlement consumption are payment-backed and verified. |

### Must Have / Should Have / Nice To Have

| Must Have before paid cohort | Should Have for pilot quality | Nice To Have / after first cohort |
|---|---|---|
| Customer and staff login; resource authorization; supported onboarding; owned property records. | Automated service status notices; richer customer dashboard; deal conversion conveniences. | Native mobile client; multi-city support; routing optimization. |
| Small service catalog with approved price/eligibility/visit/evidence/completion rules. | Monitoring or Health, but only if limited service outcomes are ready. | Broad Monitoring catalog and automated event detection. |
| Accepted quote → one request → invoice → provider-confirmed payment with idempotency. | Complaint/support workflow and customer satisfaction capture. | Subscription bundles, auto-renew, discounts and add-ons. |
| Internal assignment and supported service fulfillment with secure evidence/document access. | Operations SLA, backlog and payment reconciliation views. | Advanced analytics, AI recommendations and forecasting. |
| Audit trail, PostgreSQL migration/backup/restore evidence, deployment and rollback procedure. | Email/in-app status notification delivery tracking. | Complex lead automation and external CRM integrations. |

## 6. 90-Day Roadmap

Weeks below assume kickoff on **2026-10-07** and a staffed team as stated above. Workstreams overlap; each checkpoint is a gate, not a promise that unfinished dependencies are skipped.

| Window | Backend & security | Frontend | Testing, deployment & pilot preparation |
|---|---|---|---|
| **Week 1–2** | B1 role/resource matrix; fix admin/operations policy mismatch; prioritize customer/property/request/file/payment IDOR cases. Freeze pilot service list, price/tax/provider decisions. | F1 replace placeholders; valid package/build/router; login, token expiry, role shell. | QA prepares test accounts/fixtures and API contract matrix. DevOps makes a repeatable local PostgreSQL + Flyway run. |
| **Week 3–4** | Complete B1 tests; B2 quote snapshot, accepted request and idempotent invoice linkage; choose payment provider and implement sandbox creation/return/webhook skeleton. | F2 customer dashboard/property screens; F3 catalog and quote display/decision. | Run endpoint security tests and fresh Flyway startup. Approve service catalog, geography and operational capacity. |
| **Week 5–6** | B3 verified payment webhook, stable idempotency and invoice settlement; tax/number policy; B4 secure evidence/document storage and access. | F3 request creation/status; begin internal quote and request queue. Payment UI remains sandbox-only until webhook tests pass. | PostgreSQL transaction and webhook replay tests. Draft support, refund and reconciliation runbooks. |
| **Week 7–8** | B4 assignment, visit/no-visit and evidence/completion gates on selected services; B5 minimum overdue/payment failure visibility and communication. | F4 operations assignment/visit screens; F5 evidence and report view against controlled storage. | Cross-role end-to-end flow from quote acceptance to completed work and provider-confirmed payment. Resolve defects before additional modules. |
| **Week 9–10** | Stabilize core path; optionally implement B6 or bounded B7 only if the product owner requires that offer in cohort one. Do not parallelize both at the cost of payment/security. | Complete core customer/staff screens, empty/error/loading states, accessibility and responsive layout. Add Monitoring read-only view only if useful. | Load/concurrency checks for invoice/payment idempotency and monitoring scheduler if enabled; review security and data-retention policies. |
| **Week 11–12** | Production configuration and migration fixes from rehearsal; incident-safe retries and operations queue polish. | UAT fixes and release build; remove mock-only purchase/status paths from production routes. | Rehearse fresh deploy, upgrade, rollback/forward recovery, backup restore, payment reconciliation, support escalation. Train operations/agents and select cohort. |

### Exit gates

- **G1 (end Week 2):** Client builds; auth/role matrix and pilot services are agreed.
- **G2 (end Week 4):** Authorization tests pass for scoped endpoints; customer can view property, catalog and quote in a real client.
- **G3 (end Week 6):** Provider sandbox confirms success/failure/replay behavior; invoice and request linkage are deterministic; file boundary is controlled.
- **G4 (end Week 8):** A complete paid request can be assigned, fulfilled, evidenced, completed and shown to its customer without cross-owner access.
- **G5 (end Week 10):** Pilot service quality and operational workload accepted; any included Monitoring/Health offer passes its own acceptance criteria.
- **G6 (end Week 12):** PostgreSQL upgrade, restore, production deployment, support and financial reconciliation rehearsed; go/no-go review.

## 7. Final Recommendation

1. **Next task tomorrow morning:** Establish the endpoint/resource authorization matrix from actual controller routes and SecurityService checks, then add failing cross-role/cross-customer tests for pilot endpoints. Resolve the /api/admin/** ADMIN-only filter versus OPERATIONS controller policy as part of that work.
2. **Frontend start point:** Make propertypilot-frontend installable and runnable, then deliver login and customer property screens. Keep the API layer aligned to controller DTOs and avoid fake paid/complete states.
3. **Backend priority:** B1 Security first, then B2 quote → request → invoice integrity and B3 provider-confirmed payment. Run B4 fulfillment and F deployment harness in parallel as soon as contracts are clear.
4. **Pilot date readiness estimate:** With the stated staffed team and strict scope, target **12–14 weeks from 2026-10-07 (late December 2026 to early January 2027)**. A solo developer should plan roughly **20–28 weeks**. Provider onboarding, tax decisions, discovered authorization defects, or retaining Health and Monitoring offers can extend this. Date is conditional on gates, not calendar-only.
5. **Biggest risks:** False paid states or duplicated charges; privacy/authorization failures; an unusable client; sensitive files exposed by URL; and marketing a Health/Monitoring outcome the current code does not produce.
6. **Biggest opportunities:** Use the existing accepted-quote/request and fulfillment foundation to sell a very small service catalog; learn real pricing and delivery economics in one area; add recurring Monitoring/Health recommendations after the direct-service revenue path and operations are trustworthy.

**Decision:** Start frontend and backend work now in parallel. Do not start a paid pilot until G6 passes and all critical gates are signed off. Keep the first cohort to direct one-time services; defer subscriptions and Health/Monitoring offers unless their customer outcomes are deliberately added to the critical path.

## Source references

- Business and architecture authority: docs/Business/PROPERTY_PILOT_BUSINESS_TRUTH.md, PROPERTY_PILOT_ARCHITECTURE_TRUTH.md.
- Release and execution inputs: PROPERTY_PILOT_RELEASE_SCOPE_V1.md, PROPERTY_PILOT_MASTER_EXECUTION_PLAN.md, PROPERTY_PILOT_IMPLEMENTATION_SEQUENCE.md, PROPERTY_PILOT_PILOT_RELEASE_PLAN.md.
- Monitoring inputs and Phase 1 status: PROPERTY_PILOT_MONITORING_GAP_ANALYSIS.md, PROPERTY_PILOT_MONITORING_IMPLEMENTATION_PLAN.md, PROPERTY_PILOT_PHASE_1_MONITORING_FOUNDATION_REVIEW.md.
- Verified code touchpoints: propertypilot-backend/src/main/java/com/propertypilot/config/SecurityConfig.java; application/service/impl/PaymentServiceImpl.java; application/service/impl/QuoteServiceImpl.java; application/service/impl/ServiceRequestServiceImpl.java; application/service/impl/MonitoringRunServiceImpl.java; propertypilot-backend/src/main/resources/db/migration/V033__monitoring_run_foundation.sql; propertypilot-frontend/package.json.
