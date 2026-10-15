# PropertyPilot Release Scope V1

**Purpose:** Define a practical, revenue-first production release scope. Business Truth defines the business; Architecture Truth defines the target domain shape. Inventories and prior readiness reports describe the current code and are revalidated here. This is a launch scope, not a complete future platform design.

Effort estimates are engineer-days (one engineer working one day), rounded planning ranges. They exclude vendor onboarding lead time and unknown data migration. Scores below measure readiness for this defined release, not the percentage of all future Business Truth capabilities.

## Executive Summary

### Recommended launch strategy

Launch a **web-first, single-coverage-area PropertyPilot service operation** selling a short, explicitly configured catalog of directly chargeable services. The launch must support the full customer transaction from PropertyPilot-branded offer and quote through confirmed payment, fulfillment, evidence/report delivery and customer communication. Include one Property Health Check offer and one bounded Monitoring offer as service catalog entries, using the same quote, request, fulfillment and reporting model. Keep monitoring rules and health assessment definitions narrow and operationally supportable at launch.

Use a limited set of services that can be fulfilled with current staff and selected agents/partners. Agents and vendors remain internal fulfillment resources; customers contract with and communicate with PropertyPilot. Leads and deals provide a basic sales-to-service-request path. Direct service revenue is the launch revenue foundation. Subscription plans, bundles and renewals should not be sold until payment-backed entitlement handling is complete.

The repository currently contains useful backend foundations, but the supplied inventories describe core workflows as partial. The React client/configuration is scaffold content, the mobile app is a counter demo, and the admin portal is not an implemented client. Several high-risk server paths accept caller supplied identities or prices, payment success can be recorded without provider confirmation, and production configuration inherits unsafe defaults. Consequently, the current code is not ready for a customer launch.

### Launch assumptions

- Release V1 is one country/currency/tax policy and one operational coverage area, with explicit service and agent capacity. Expansion is driven by observed service quality and unit economics.
- A supported browser client can serve customers and internal operations. No native mobile app is required; field staff may use responsive web screens or the internal operations workflow.
- V1 offers only services with approved price, eligibility, SLA, visit, evidence, completion and report rules. Some services require a visit; others, including document/compliance services, do not.
- Property Health is a configured service offer. Its first release uses a bounded checklist, documented findings, evidence and a customer report; it does not require an independent health platform.
- Monitoring is a configured recurring or scheduled service. V1 launches one defined monitoring type and service area, with human review of observations/alerts where automation is not yet reliable.
- Customers can pay per service through a real payment provider. Subscriptions, auto-renewals, coupons and complex discount stacking remain disabled until their complete financial lifecycle is supported.
- Leads can be captured and worked by staff. Deals are internal revenue opportunities and accepted deals can create service requests; no public vendor listings, bidding or vendor contact marketplace is in scope.
- Required compliance, tax, privacy, refund, retention and payment policies are confirmed for the launch jurisdiction before taking money or documents.

### Major risks

- Unsafe production defaults and inconsistent resource authorization could expose accounts, property records, evidence or money.
- Payment and invoice state are not currently tied to provider-confirmed settlement; subscription renewal can mark success without collecting payment.
- Customer supplied request amounts are trusted, and current quote calculation is not a persisted accepted-price contract.
- The current customer, operations and admin clients are not usable production applications.
- Evidence/document URLs lack a demonstrated controlled storage and access lifecycle.
- The chosen Monitoring and Health offers need narrow definitions. Selling broad claims without reliable outputs, alert ownership and support capacity would create service-quality and trust risks.
- No production restore, rollback, alerting, or passing production-path integration evidence is established by the reviewed documents.

## Release V1 Mandatory Modules

All listed modules are evaluated. “Launch blocker?” means whether the module or its minimum launch slice must work before accepting V1 customers. A minimal slice can be manual or narrow when stated; the business rule must still be enforced and auditable.

| Module | Why required | Current implementation status | Remaining work for V1 | Launch blocker? |
|---|---|---|---|---|
| Authentication & Security | Protect customer/property data, enforce customer, agent and admin roles, and derive actors from authenticated identity. | Partial: JWT, roles and some ownership checks exist; secret/configuration and resource-level enforcement are inconsistent, and billing authority naming conflicts. | Externalize/rotate secrets; use safe production schema/log settings; enforce role and resource ownership across request, visit, evidence, invoice, payment and subscription APIs; normalize authority checks; verify access-denial paths. | Yes |
| Customer Management | PropertyPilot owns each customer relationship and customer-facing service experience. | Partial: profile and related services exist; onboarding/conversion paths are fragmented. | Deliver a usable registration/login and profile flow, verified contact details, admin-created customer support and authorized customer views. Keep identity separate from customer ownership in API checks. | Yes |
| Property Management | Services, history and documents center on customer properties. | Foundation implemented: property CRUD, owner relationships and ownership checks exist. | Complete customer property create/view/update through the client; verify shared-owner access and request-time property/customer relationship; ensure property context on all service records. | Yes |
| Service Catalog | All customer-facing work is a PropertyPilot service with controlled availability, configuration and customer price. | Partial: `ServiceEntity` has visit/evidence/GPS fields and eligibility support; coherent catalog administration/versioning is not established. | Configure a small launch catalog with price/eligibility/SLA, visit optionality, evidence, GPS and completion rules; expose controlled admin operations; snapshot applicable rule version to work. | Yes |
| Service Request | Represents the customer’s paid work order and links property, offer, quote and fulfillment. | Partial: lifecycle and status history exist, but create trusts submitted IDs/amount and generic update can bypass transitions. | Create from authorized property and accepted quote; validate service eligibility; enforce transitions and actor history; support cancellation/hold/rejection; prevent caller-set price/state/actor. | Yes |
| Visit Management | Supports field services while allowing services that need no visit. | Partial: schedule/start/complete/cancel paths exist; configured visit requirement/count is not enforced and one visit per assignment is restrictive. | Enforce the service’s frozen visit policy; support no-visit completion and bounded repeat visits where configured; customer/agent access, schedule changes and audit history. | Yes, for visit-required offers; no-visit offers still need a valid completion path. |
| Evidence Management | Proof is needed to establish work completion and customer trust. | Partial: evidence/GPS persistence and checks exist; evidence accepts supplied URL and has no proven controlled upload/review boundary. | Implement scoped upload/download using controlled storage; validate type, size and ownership; capture uploader/time/checksum; apply configured per-service/per-visit requirements and review status; deny access across customers. | Yes |
| Agent Assignment | Staff/agents fulfill work under PropertyPilot ownership. | Partial and duplicated: assignment lifecycle overlaps request/visit services; some standalone lifecycle methods throw. | Provide a reliable internal manual assignment, accept/reject and reassignment path with agent identity checks and notifications. Select one lifecycle owner; do not expose vendor marketplace behavior. | Yes |
| Deal Management | Tracks revenue opportunities and carries accepted opportunities into service delivery. | Partial: deal lifecycle, billing and participant operations exist; deal-to-request link has no demonstrated end-to-end handoff. | Support internal deal creation/acceptance and an idempotent action to create one or more priced service requests with source-deal traceability. Omit unnecessary negotiation automation. | Yes, at least minimal conversion for deals used to sell work. |
| Lead Management | Captures prospects from customer, staff, referral and other approved sources. | Partial: capture, assignment and activity exist; source normalization/dedupe and conversion are incomplete. | Provide lead intake, owner/status/activity and conversion to customer/deal; permit unconverted prospects; validate access and prevent duplicate conversion. | Yes, minimal sales intake. |
| Pricing Engine | PropertyPilot controls the customer price and every service may be chargeable. | Partial: core vendor/travel/agent/overhead/margin/GST calculation exists; cluster/ETA return zero and discount/configuration coverage is incomplete. | Configure and version the V1 price inputs, tax and rounding; price selected service area and offerings consistently; show customer price and benefit breakdown without exposing internal vendor cost. No hardcoded launch price logic. | Yes |
| Quote Management | Customer must accept the price before paid work begins. | Incomplete: calculation exists but no demonstrated immutable, accepted quote snapshot tied to request/invoice. | Persist quote and line items with pricing-rule version, expiry and customer acceptance; create request and invoice from the accepted quote; support quote rejection/expiry. | Yes |
| Billing / Invoice | Records what is due, paid, refunded and linked to delivered services. | Partial: invoice generation exists; tax is fixed at zero, numbering is weak, and list APIs are unbounded. | Use unique invoice numbers, jurisdiction-approved tax/currency, quote-backed lines, due/balance state and auditable adjustments/refunds. Restrict invoice visibility and paginate operational views. | Yes |
| Payment Integration | Collects service revenue and provides authoritative settlement state. | Incomplete for production: recording a request can mark payment SUCCESS and invoice PAID without provider verification; idempotency is not stable. | Integrate a selected provider using verified signatures, stable idempotency, provider references, failure/pending/success/refund states and reconciliation. Do not mark paid from a client assertion. | Yes |
| Notifications | PropertyPilot, not agents/vendors, owns customer status communication. | Partial: notification persistence exists; reminder scheduler logs IDs and delivery/retry/template behavior is not established. | Deliver template-driven in-app, email and SMS for request/payment/assignment/visit/completion/monitoring and operational failures; record delivery/retry outcome. | Yes, minimum transaction and service-status messages. |
| Geo Intelligence | Defines sellable coverage, assignment and configured travel/GPS rules. | Partial: zones, clusters, locations and GPS persistence exist; coherent admin configuration and price/eligibility use are incomplete. | Define one launch coverage boundary; validate property eligibility and agent territory; apply required GPS rules and travel policy. Defer sophisticated clustering/route optimization. | Yes, minimum coverage and any GPS-required offer. |
| Subscription Management | Subscription is a business revenue model with benefits and entitlements. | Partial: plan/benefit/consumption structures exist; renewal marks success without payment and quote entitlement application is incomplete. | **V1 scope:** keep subscription purchase, auto-renew, benefits and subscription discounts disabled. Remove/guard the unsafe renewal success path and avoid representing unpaid subscriptions as active. Re-enable only with payment-backed entitlement flows. | No for the direct-service V1; unsafe existing endpoints/jobs must be disabled or corrected before launch. |
| Monitoring | Monitoring is a paid service that can produce alerts and follow-on requests. | Persistence-only/partial: schedule and alert entities exist, but end-to-end run, review, alert and renewal workflow is not identified. | Launch one bounded monitoring SKU with explicit schedule, observation/evidence record, human review where needed, customer alert, follow-up service request and pause/expiry handling. Reuse catalog/request/payment/reporting. | Yes for the assumed V1 Monitoring offer; broad automated monitoring is deferred. |
| Property Health | Health Check is a service category that produces findings and scores/recommendations. | Incomplete: no end-to-end assessment, findings/score or report flow is demonstrated. | Launch one configured checklist service with defined documentation/compliance/physical checks appropriate to the offer, evidence, explainable score/risk result, findings and customer report. Do not build a separate platform module. | Yes for the assumed V1 Health offer; broad assessment coverage is deferred. |
| Document Management | Stores property, service and evidence records with access control and history. | Partial: document/version entities and services exist; controlled storage/access/retention are not demonstrated end-to-end. | Secure upload/download and ownership; connect documents to property/request/assessment; preserve versions, access audit and retention policy. Limit accepted document formats and launch storage scope. | Yes for any service accepting customer documents or evidence. |
| Reporting | Delivers the service outcome and customer transparency. | Partial: report/completion services exist; consistent report output, evidence gating and customer channel are incomplete. | Generate a service completion report, Health Check report and Monitoring observation/alert history; expose only authorized customer records; ensure completion is blocked until configured deliverables exist. | Yes |

### Minimum viable scope boundaries

- Offer a small list of services in one coverage area, including one Property Health service and one Monitoring service with explicitly bounded outputs. Choose additional direct service offers only where fulfillment capacity and price rules are validated.
- Support both visit-required and no-visit service paths. Avoid implying that every service follows one field workflow.
- Assign agents manually through internal operations for V1. Manual assignment is acceptable; untracked assignment or unauthorized agent access is not.
- Process leads and accepted deals with a simple staff workflow. Defer elaborate sales automation, multi-stage forecasting and external CRM integration.
- Support direct one-time payment for services. Keep subscription checkout and recurring billing unavailable until payment and entitlement integrity is complete.
- Provide an operational complaint/support intake and response process. Complex multi-tier complaint automation can follow in V2, but customers need a PropertyPilot-owned contact path at launch.
- Keep marketplace listing, vendor discovery, vendor bidding, vendor contact disclosure and vendor-to-customer contracting out of V1 and V2 product scope.

## Release V2 Deferred Modules

Deferral applies to advanced capability, not to data protection, basic customer support, or any feature needed by a V1 service offer.

| Module / capability deferred | Why it can be deferred | Business impact of deferral | Technical impact of deferral |
|---|---|---|---|
| Subscription plans, paid bundles, auto-renew and entitlement discounts | Direct one-time service sales can establish revenue first; current renewal/payment path is unsafe. | Delays recurring revenue, retention benefits and bundle discounts. Customers can still buy chargeable services directly. | Avoids exposing incomplete entitlement and renewal logic; plan schema can remain unused or be feature-disabled until integrated with quote/payment. |
| Multi-city geo intelligence, optimized clusters and route automation | A single bounded service area can validate operations and travel economics. | Slower geographic expansion and less travel/cluster discount optimization. | Avoids premature routing integrations and broader territory administration; retain simple configurable coverage and GPS checks needed by V1. |
| Broad monitoring catalog and fully automated event detection | One reviewed monitoring service is enough to test willingness to pay and operating cost. | Limits monitoring variety and scale; retains one paid monitoring offer. | Defers event-provider integrations, automated classification and high-volume alert orchestration; V1 still needs durable schedule/run/alert state. |
| Broad Property Health coverage and advanced scoring | One defined checklist/report can validate the service; scores must be explainable before expansion. | Customers receive a bounded assessment rather than every compliance, physical and financial risk dimension. | Defers complex scoring models and more integrations; V1 keeps versioned checks and explainable results. |
| Full complaint escalation automation | Basic customer intake, ownership and response can be operated by staff during a controlled launch. | Slower escalation and less automated SLA control; customer support remains available. | Manual triage adds operational load. Preserve complaint/audit records; expand timers, levels and remediation automation in V2. |
| Advanced subscription notification channels (WhatsApp, push, voice/IVR) | Business Truth places these in later phases; email/SMS/in-app cover V1. | Less channel choice and potentially lower engagement for some customers. | No effect on core service correctness; reduces provider and template integrations. |
| Native mobile apps and offline synchronization | Responsive browser workflows can support a web-first pilot. | Less convenient field operation under poor connectivity. | Defers mobile clients, synchronization, conflict handling and app-store operations; APIs and evidence upload must still be secure and usable. |
| AI recommendations | Explicitly future in Business Truth and depends on reliable property, monitoring and health history. | No proactive AI-generated upsell or risk suggestions. | Avoids model governance and data quality dependency; ordinary configured service notifications remain in V1. |
| Enterprise packages, expanded catalog and multi-tenant/large portfolio features | Not required to validate the first direct-service launch; Architecture Truth does not mandate a multi-tenant or microservice redesign. | Delays builder, investor and corporate offerings and high-volume account tools. | Avoids premature enterprise abstractions; preserve property/customer ownership and stable domain interfaces. |
| Advanced analytics, forecasting and performance dashboards | V1 can use operational reports and basic revenue/service measures. | Fewer management insights and manual reporting overhead. | Defers warehouse/BI workloads; transaction lists and reports still need pagination and access controls. |

## Launch Critical Production Blockers

These items block the scoped V1 customer launch. They do not include all known technical debt or all capabilities needed for enterprise scale.

| Blocker | Impact | Recommended fix | Estimated effort |
|---|---|---|---:|
| Unsafe production configuration and schema ownership | A committed JWT key, inherited schema update behavior and debug logging create account compromise, data leakage and schema drift risks. | Inject and rotate secrets; production-safe logging; Flyway-only schema change with Hibernate validation; explicit production profile and least-privilege DB credentials. | 2–4 days |
| Resource authorization and actor identity gaps | Cross-customer/property/agent access or forged actor/target identifiers can expose or mutate customer work. | Establish role/resource matrix; derive actor from principal; enforce ownership and assignment in service layer for customer, property, request, visit, evidence, document, quote, invoice and payment. Fix authority mismatch and test denial cases. | 6–12 days |
| Unverified payment settlement and unsafe renewal behavior | False paid invoices, duplicate collection or unpaid active subscriptions undermine revenue and customer trust. | Integrate provider-verified, idempotent payment for direct service invoices; reconcile provider callbacks; disable subscription checkout/renewal until payment-backed. | 8–15 days |
| No accepted-price-to-invoice chain | Client-supplied amounts and incomplete tax/discount pricing can cause wrong charges and disputes. | Version V1 price rules; persist accepted quote; derive request/invoice lines only from quote; apply approved tax/currency/rounding; unique invoice numbers. | 10–18 days |
| Evidence/document access lacks controlled object lifecycle | Arbitrary URLs and incomplete authorization/storage validation risk data exposure and untrustworthy completion proof. | Implement controlled object storage, scoped upload/download, file validation, uploader provenance, accepted/rejected review state, access audit and configured completion rules. | 8–14 days |
| Required customer/operations channel is not implemented | Current React configuration/pages are scaffold prose; mobile is a counter sample and admin portal has no usable app. Staff and customers lack the supported way to transact and deliver services. | Build a responsive web V1 for onboarding, properties, offer/quote, request/payment, status/report and minimum internal catalog, lead/deal, assignment, evidence and operations tasks. | 20–40 days |
| No production deployment/recovery evidence | Without controlled release, health monitoring, backup/restore and rollback, customer and payment records lack operational protection. | Configure production hosting/runtime/secrets, health/readiness, alert ownership, backup retention and demonstrate restore and rollback. | 5–9 days |
| Production-path tests are not established as release gates | Unit test counts do not prove database migration, authorization, payment, evidence or completion behavior. | Require PostgreSQL/Flyway install-upgrade and cross-resource authorization, quote/payment idempotency, evidence access, monitoring/health completion and rollback-path tests in CI; verify suite passes. | 8–14 days |
| V1 service configuration and outcomes are not operable end-to-end | The chosen services cannot be reliably sold if visit/evidence/geo rules, price, assignment and deliverables are not applied consistently. | Define and configure the small launch catalog; implement service rule snapshot, valid no-visit and visit execution, one monitoring lifecycle and one Health Check result/report; provide manual internal operations where automation is not essential. | 15–28 days |
| Customer communication and support path are incomplete | Customers may pay without receiving reliable status, reports or an owned route for service problems. | Implement templated in-app plus email/SMS notifications for transactional states and provide basic PropertyPilot support/complaint intake with named internal owner and response SLA. | 6–10 days |

Effort overlaps where work shares contracts or tests; do not add table ranges mechanically. Payment-provider onboarding, jurisdiction review, and hosting procurement can extend calendar time.

## Recommended Launch Sequence

### Phase 1 — Lock offer and operating policy

1. Select the one launch area, customer segment, service list and fulfillment capacity.
2. Define price/tax/refund policy, service eligibility, SLA, visit optionality, evidence and GPS requirements, Monitoring run/alert policy, and Health Check checklist/report.
3. Choose payment and object-storage providers; define notification channels, production hosting, retention, support owner and restore objectives.
4. Confirm that every V1 offer is sold by PropertyPilot with no customer marketplace surface.

**Exit condition:** Operations and finance can state exactly what is sold, to whom, at what price, with what evidence/output and fulfillment responsibility.

### Phase 2 — Secure transactional foundations

1. Fix production secrets, profile selection, logging, Flyway/Hibernate settings and database privileges.
2. Implement principal-derived identity and resource-level access policies.
3. Deliver controlled document/evidence storage and customer access.
4. Implement quote snapshot → accepted quote → service request → invoice → provider-confirmed payment.
5. Disable or guard unpaid subscription activation/renewal; define supported refunds.
6. Add PostgreSQL, authorization and payment contract tests as each path is implemented.

**Dependencies:** Phase 1 decisions. This phase must precede exposure of customer records or money.

### Phase 3 — Deliver the minimum service operation

1. Configure the narrow catalog and service policy versions; enforce both visit-required and no-visit paths.
2. Implement manual internal assignment, agent acceptance/access, evidence review and completion/report generation.
3. Complete the selected Monitoring service and Property Health Check service as catalog/request/report flows.
4. Implement lead intake and minimal deal acceptance-to-request conversion.
5. Add customer notifications, support intake and the customer/operations web channel.

**Dependencies:** Phase 2 identity, quote, storage and API contracts. Build the browser skeleton in parallel, but connect checkout only after the accepted quote and payment contracts are stable.

### Phase 4 — Prove and launch the controlled pilot

1. Run end-to-end tests for direct purchase, no-visit completion, visit/evidence completion, Monitoring alert/review, Health Check report, cancellation/refund and denied cross-customer access.
2. Exercise migration upgrade, backup restore, rollback, payment webhook retry and notification retry in a production-like environment.
3. Train staff and selected agents; rehearse manual assignment, support escalation and service recovery.
4. Launch to a limited customer cohort, monitor payment reconciliation, SLA, completion evidence, complaints and contribution margin; expand scope only after these measures hold.

**Dependencies:** Phases 1–3 complete and release gates passing. This is a limited production pilot, not a claim of enterprise readiness.

## Revised Readiness Assessment

Scores are current-state estimates for the defined V1 scope, based on implementation evidence in the inventories and execution/readiness plans. They are not code-volume or test-count percentages. The goal is to estimate how much of the launch capability is already demonstrated end-to-end.

| Measure | Readiness | Explanation |
|---|---:|---|
| Business Readiness | **42%** | Customer/property, lead/deal, request, visit, evidence and pricing foundations align with the model. Revenue-critical accepted quotes, provider settlement, configured service fulfillment, Monitoring and Health Check outputs remain partial or absent. This score includes V1’s one Monitoring and one Health offer, not every service in Business Truth. |
| Architecture Readiness | **49%** | A modular Spring application and many domain records/services provide a reasonable base. Lifecycle ownership, versioned configuration, quote-to-request linkage, controlled object storage and durable external workflows remain incomplete. No microservice split is needed for V1. |
| Backend Readiness | **51%** | Several CRUD and state paths exist with service/controller tests. Critical request authorization, accepted quote/invoice/payment, evidence validation, configured visit policy and Monitoring/Health workflows need implementation or completion. Presence of tables/classes is not counted as an operational workflow. |
| Operational Readiness | **31%** | Catalog configuration, staff/agent operations, customer notifications, complaint intake, deploy/recovery and service monitoring are not demonstrated end-to-end. A narrow pilot can use manual internal assignment and human review if recorded and permissioned. |
| Production Readiness | **27%** | Unsafe inherited configuration, resource-access gaps, unverified settlement, incomplete evidence boundary, unusable clients and no verified recovery/release evidence block customer launch. Existing Docker/Actuator foundations do not establish operational readiness. |

The previous Production Readiness V2 overall score of 41% described broad platform readiness. Its 57% business estimate and backend evidence do not mean a revenue transaction is production-ready. For this narrower but transaction-complete scope, launch readiness is lower because payment, price integrity, safe access, customer channel and the assumed Monitoring/Health offers are hard gates. Subscription revenue is excluded from V1 and must not be counted as a delivered capability.

## Launch Recommendation

### C. Needs Major Work Before Launch

The repository is not ready for a limited customer pilot today. Essential foundations exist, but the current implementation does not safely complete a customer-owned revenue workflow from authorized property and accepted quote through verified payment and evidence-backed service outcome. The launch clients are not usable, and production configuration and recovery have not been proven.

A limited pilot becomes appropriate after the Phase 1–4 gates pass for the chosen catalog, including one defined Monitoring service and one Property Health service. Keep customer cohort, service area, pricing rules and fulfillment capacity constrained; do not sell subscriptions or services whose configured fulfillment/report path is not tested.

## Estimated Effort Remaining

Ranges are person-days for a small experienced team. Shared work and parallel implementation affect elapsed calendar duration; external provider, compliance and procurement lead times are excluded.

| Estimate | Effort | Scope and confidence |
|---|---:|---|
| Minimum effort for Pilot Launch | **100–150 engineer-days** | A tightly constrained web pilot: one operating area, small catalog, one-time service payment, manually assigned internal fulfillment, one Monitoring service, one bounded Health Check, controlled evidence, minimum customer/operations web interface, basic notices/support and essential release tests. Assumes reusable backend foundations and prompt provider decisions. Low confidence until build/test and data migration status are verified. |
| Recommended effort for Stable Launch | **135–205 engineer-days** | Includes the pilot plus stronger automated PostgreSQL/payment/authz tests, retry/reconciliation, notification delivery monitoring, staff tools, customer transparency/report polish, refund/support procedures, recovery drills and operational dashboards for the selected services. |
| High-confidence production launch estimate | **165–250 engineer-days** | Stable launch plus contingency for integration defects, security review findings, migration/data cleanup, performance tuning at expected pilot load and repeated production-like rehearsals. Excludes native mobile, multi-city automation, full subscription product, AI, enterprise packages and major penetration-test remediation. |

These estimates reconcile the Master Execution Plan’s 95–170 engineer-day web-first pilot with its stated 20–35 day allowance for broader operational domains. This document explicitly includes a bounded Monitoring offer and a bounded Health Check in V1; scope reduction comes from limiting catalog breadth, geography, subscription complexity, mobile and automation—not from changing PropertyPilot into a marketplace or omitting those two service categories.
