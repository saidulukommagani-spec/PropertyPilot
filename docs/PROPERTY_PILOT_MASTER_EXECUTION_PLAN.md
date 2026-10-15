# PropertyPilot Master Execution Plan

**Planning basis:** Business Truth is the business authority; Architecture Truth defines the target domain structure. Code inventories describe the current repository. Gap Matrix V4, Production Readiness V2, Dead Code Report and Production Blockers are treated as claims to revalidate, not as authorities. Effort is in engineer-days and is a rough planning range (approximately ±50%).

## Reconciliation findings

### Validated findings retained

- The base `application.yml` contains a literal JWT key, enables Hibernate `ddl-auto: update`, and enables SQL/security DEBUG logging. `application-prod.yml` overrides datasource and selected logging settings, but inherits the base signing key and DDL mode. These are production blockers.
- The payment recording path in `PaymentServiceImpl` sets `PaymentStatus.SUCCESS` and invoice `PAID` without a verified processor event. Its idempotency key is generated randomly for each request, so it does not prevent duplicate client submissions.
- `SubscriptionAutoRenewScheduler` creates an active subscription and a renewal with status `SUCCESS` without invoking payment collection. This can grant a paid period without collection.
- `ServiceRequestServiceImpl.createRequest` accepts customer, property, service and amount IDs/values from the request; it stores the supplied amount and creates an invoice from it. `updateRequest` writes scheduling, completion and assignment fields directly without the transition/history path. `ServiceRequestController` does not apply `@Valid` to those request bodies or derive actor/customer identity from the principal.
- `EvidenceServiceImpl` stores a request-supplied `fileUrl`; it checks visit state and duplicate type, but no controlled upload/storage or evidence review pipeline is present in the inspected service. Evidence and visit access are not consistently scoped to the authenticated customer/assigned agent.
- `PricingRuleEngineServiceImpl` computes core cost inputs and GST, but returns zero cluster/ETA discounts. The calculation path does not apply coupon/subscription discounts or persist an accepted immutable estimate/line-item snapshot.
- `BillingServiceImpl` fixes tax at zero, fixes currency to INR, generates invoice numbers from milliseconds, and exposes an unpaged `findAll` path. Tax configuration and uniqueness are not established by this implementation.
- Service fields include `requiresVisit`, `visitMandatory`, `maxVisitsAllowed`, GPS and evidence flags. Completion uses several fields, but visit creation/completion do not enforce `visitMandatory` or maximum visit count. The visit service also restricts one visit per assignment.
- The reminder scheduler logs subscriptions instead of sending notifications. Generic notification/template persistence exists, but the inspected flow does not establish template-driven email/SMS delivery, retries or deduplication.
- Monitoring and complaint entities/repositories are present, without corresponding end-to-end service/controller workflows in the implementation inventory. The Business Truth defines both as service/operations capabilities.
- `CustomUserDetailsService` emits `ROLE_<code>`; `BillingController` checks `hasAuthority('ADMIN')`. Under that mapping, normal `ROLE_ADMIN` users fail the billing method guard. Other controllers use mixed `/api` and `/api/v1` route bases. The fallback rule requires authentication, so the prefix mismatch is a contract/policy inconsistency, not by itself proof of anonymous access.
- The React tree is not a working client: 143 of 154 `src` files are at most 150 bytes and sampled route/API/page files contain scaffold prose. `package.json`, Vite and TypeScript configuration files also contain prose. `mobile_app/lib/main.dart` is the Flutter counter demo; `admin-portal` is a README and empty source tree.
- The code has genuine ownership checks for property/document/lead/deal operations through `SecurityService`. The earlier broad phrasing that all property ownership was missing is therefore not retained. The gap is that equivalent resource and assigned-agent checks are not consistently applied to requests, visits, evidence, invoices and payments.

### V4/V2 findings removed or narrowed

- **Removed as an architectural recommendation:** a separate Property Health Check platform module. Business Truth explicitly makes it a service category. The implementation work is to model its configured document, compliance, physical and risk checks and produce linked findings/report results within the existing service/request/document/evidence/report domains.
- **Removed as a production migration conflict:** the separate `my-database-migration-project` is not configured as the backend runtime migration tool. Runtime Flyway is configured under `propertypilot-backend`. Keep the extra migration/document SQL trees classified as historical/planning material; the live production risk is Hibernate schema update running alongside Flyway.
- **Narrowed marketplace finding:** a customer-facing marketplace is contrary to Business Truth and must not be implemented as a product surface. Current backend inventory shows no marketplace controller/service flow, so dormant marketplace persistence is a cleanup candidate, not a currently operating marketplace blocker. Preserve vendor/partner fulfillment records only where they support PropertyPilot-managed delivery.
- **Narrowed test finding:** the backend has 78 Java test sources, including many service implementation tests, controller tests, security/config tests and scheduler tests. “Mostly controller tests” is inaccurate. The suite’s pass state, PostgreSQL integration coverage, external payment integration and release-gate use were not verified in this audit.
- **No blanket microservices finding:** Architecture Truth allows domain ownership in a modular application; the current Spring application is not disqualified merely for being a monolith. The relevant mismatch is that lifecycle ownership, configuration, durable events and authorization are not consistently enforced at domain boundaries.
- **No removal based only on class counts:** declaration-only repositories and entity candidates from the Dead Code Report require reference, data, migration and intended-release checks before deletion. Dead-code work is limited to confirmed throwing stubs, unmatched abstractions, duplicate ownership, and explicitly out-of-scope persistence after verification.

## Execution order and priorities

Recommended order is shown by item IDs. Complete P0 before accepting real customer data or money. Work may proceed in parallel after the named dependencies are settled.

## P0 — Production Blockers

### P0.1 — Remove unsafe inherited production configuration

- **Description:** Remove the committed JWT key, require an injected production secret, rotate the existing value, disable SQL/bind/security DEBUG logging in production, set Hibernate schema mode to validation (Flyway remains the schema writer), and make development credentials/profile impossible to select accidentally in a production deployment. Evidence: `propertypilot-backend/src/main/resources/application.yml`, `application-prod.yml`, `application-dev.yml`, `JwtService.java`.
- **Business impact:** Protects customer identities, property records, documents and financial activity from token compromise or log exposure.
- **Technical impact:** Makes production startup configuration explicit and prevents Hibernate/Flyway schema drift.
- **Dependencies:** Production secret store and environment ownership.
- **Estimated effort:** 2–4 days.
- **Recommended implementation order:** First. Establish deployment profile and secret contract before other release work.

### P0.2 — Enforce identity, role and resource authorization

- **Description:** Define a route/resource policy and apply it at the service boundary for request, visit, evidence, invoice, payment, subscription and customer data. Resolve customer/agent/admin identity from the authenticated principal; validate property ownership/access and agent assignment. Normalize `/api` versus `/api/v1` route policy and `ROLE_` checks. Correct the `BillingController` authority mismatch. Reuse the already-present property/document/lead/deal checks in `SecurityService` where appropriate. Evidence: `SecurityConfig.java`, `SecurityService.java`, `CustomUserDetailsService.java`, `ServiceRequestController.java`, `EvidenceController.java`, `PaymentController.java`, `BillingController.java` and corresponding services.
- **Business impact:** Keeps customer/property information private and ensures only assigned operational resources can act on work.
- **Technical impact:** Closes broken object-level access paths and removes caller-supplied actor/customer identity as an authorization source.
- **Dependencies:** Agreed role matrix and definition of shared-property owner/access policy.
- **Estimated effort:** 6–12 days.
- **Recommended implementation order:** After P0.1; before exposing customer APIs or file access.

### P0.3 — Make settlement provider-verified and idempotent

- **Description:** Replace `recordPayment` success-by-request with gateway intent/authorization/capture and signed, idempotent provider callbacks. Persist stable idempotency and provider references; reconcile invoice balance from confirmed settlement. Implement full/partial refund states and audit linkage before enabling refunds. Evidence: `PaymentServiceImpl.java`, `PaymentController.java`, `PaymentEntity.java`, `InvoiceEntity.java`, `RefundEntity.java`.
- **Business impact:** Prevents false payment confirmation, duplicate collection, uncollected service revenue and unauditable refunds.
- **Technical impact:** Introduces a correct financial state machine and processor boundary.
- **Dependencies:** Payment provider selection, credentials, webhook endpoint/secret and finance-approved settlement/refund policies; P0.1 and P0.2.
- **Estimated effort:** 8–15 days.
- **Recommended implementation order:** Start provider onboarding in parallel with P0.1; implement after P0.2 identity/authorization contract.

### P0.4 — Secure document and evidence object access

- **Description:** Replace arbitrary persisted `fileUrl`/`filePath` values with controlled object references and scoped upload/download. Record uploader, checksum, content metadata and validation outcome; enforce request/property/visit ownership, access logging and retention. Evidence acceptance must have a validation state. Evidence: `EvidenceServiceImpl.java`, `CreateEvidenceRequest.java`, `EvidenceEntity.java`, `DocumentServiceImpl.java`, `DocumentVersion.java`.
- **Business impact:** Protects property documents and preserves trustworthy proof of service work.
- **Technical impact:** Prevents unauthorized document exposure and makes evidence integrity and provenance enforceable.
- **Dependencies:** Object-storage provider/policy; P0.2 authorization; agree retention and accepted evidence formats.
- **Estimated effort:** 6–11 days.
- **Recommended implementation order:** Begin design with P0.2; deliver before launching services that collect customer documents or field evidence.

### P0.5 — Establish production deployment and recovery controls

- **Description:** Use the production profile and external secrets, a production PostgreSQL configuration, least-privilege DB credentials, bounded resource/pool settings, liveness/readiness probes, release/rollback procedure, backup retention and a proven restore process. `docker-compose.yml` is development-only and contains local credentials. Evidence: backend `Dockerfile`, `docker-compose.yml`, application profiles, Actuator configuration; no repository proof of a production orchestrator or recovery runbook.
- **Business impact:** Reduces outage and data-loss risk for property histories, documents, payments and service evidence.
- **Technical impact:** Establishes repeatable deployment, recoverability and operational health signals.
- **Dependencies:** Chosen hosting/database platform, RPO/RTO, secret management and operations owner.
- **Estimated effort:** 5–9 days, excluding cloud account/procurement setup.
- **Recommended implementation order:** Start in parallel with P0.1; prove restore and rollback before pilot approval.

### P0.6 — Add production-path integration and authorization tests

- **Description:** Add and require tests for PostgreSQL/Flyway clean install and upgrade, cross-customer/agent access denial, evidence access, provider callback signatures/idempotency, invoice/payment consistency, renewal failure, configured completion, and transaction rollback. Existing 78 test files are not proof of these release paths; no suite execution result is part of the supplied audit evidence.
- **Business impact:** Prevents customer data leakage, false settlement and invalid completion from reaching production.
- **Technical impact:** Validates database constraints and multi-component behavior beyond mocks.
- **Dependencies:** P0.1–P0.4 contracts; PostgreSQL test environment and payment-provider sandbox.
- **Estimated effort:** 8–14 days.
- **Recommended implementation order:** Add tests alongside P0.2–P0.4, then make them mandatory before deployment sign-off.

### P0.7 — Deliver a minimum supported customer and operations channel

- **Description:** For a web-first pilot, replace the scaffold React files/configuration with a buildable customer channel for property, service selection/request, quote/benefits, service status, visit/evidence/report and payment, plus the minimum authenticated internal operations/field screens needed to configure and fulfill those services. A separately maintained existing client could satisfy this requirement, but none is evidenced in this repository. Evidence: `propertypilot-frontend/package.json`, `vite.config.ts`, `src/**`, `mobile_app/lib/main.dart`, `admin-portal/**`.
- **Business impact:** Gives customers the PropertyPilot-owned service experience and staff the means to deliver it without exposing vendors as the interface.
- **Technical impact:** Validates real API contracts and provides an operable launch channel; current UI files are predominantly non-executable scaffold prose.
- **Dependencies:** Stable API/security/quote contracts from P0.2 and P1.1–P1.2; launch service scope.
- **Estimated effort:** 20–40 days for a lean web-first channel; a native mobile app is excluded.
- **Recommended implementation order:** Contract/design in parallel; implement after API contracts stabilize and before customer pilot.

## P1 — Revenue Critical

### P1.1 — Complete rule-driven price calculation and quote snapshots

- **Description:** Apply versioned service/profile rules for travel, agent DA, overhead, margin and tax plus subscription, coupon, cluster, ETA and promotional benefits. Persist a quote snapshot and line items using existing pricing estimate/quote-line entities; show standard price, customer price and savings while keeping operational cost components internal. Evidence: `PricingRuleEngineServiceImpl.java`, `PricingCalculatorServiceImpl.java`, pricing estimate/rule/discount/quote-line entities and repositories.
- **Business impact:** Establishes accurate, transparent customer prices and realizes discounts/entitlements promised by Business Truth.
- **Technical impact:** Makes quote-to-invoice calculations reproducible and prevents later rule changes from repricing accepted work.
- **Dependencies:** Approved discount ordering/stacking, rounding, tax and eligibility rules; P0.1 and P0.6 test harness.
- **Estimated effort:** 8–14 days.
- **Recommended implementation order:** First in P1; precedes paid service request flow.

### P1.2 — Bind service request and invoice to accepted price or entitlement

- **Description:** Do not accept `amount` as an authoritative create-request field. Resolve customer/property/service/eligibility, require acceptance of a versioned quote or explicit entitlement/discount, and create invoice lines from that accepted record. Add database-enforced unique invoice number and configured tax rather than fixed zero. Evidence: `ServiceRequestServiceImpl.createRequest`, `CreateServiceRequestRequest.java`, `BillingServiceImpl.java`, `PricingEstimateEntity.java`, `InvoiceEntity.java`.
- **Business impact:** Prevents undercharging, overcharging and disagreement between displayed quote and collected amount.
- **Technical impact:** Establishes one auditable price source from selection through invoice and payment.
- **Dependencies:** P1.1, P0.3; finance tax/currency policy.
- **Estimated effort:** 5–9 days.
- **Recommended implementation order:** Immediately after P1.1; before exposing checkout.

### P1.3 — Make subscriptions, benefits, add-ons and renewals financially consistent

- **Description:** Activate/renew only after confirmed collection or explicit zero-pay benefit; apply entitlement consumption to eligible requests; model failed charge/retry/cancel outcomes idempotently. Connect the existing plan/version/consumption/add-on structures to quote and payment flows. Remove the current auto-renew success-without-payment path. Evidence: `SubscriptionAutoRenewScheduler.java`, `CustomerSubscriptionServiceImpl.java`, renewal/entitlement services and entities.
- **Business impact:** Delivers paid plans, service bundles, discounts, add-on revenue and priority benefits without granting unpaid periods.
- **Technical impact:** Coordinates billing, lifecycle state and entitlement ledger safely across scheduler retries.
- **Dependencies:** P0.3, P1.1–P1.2, plan/benefit/renewal policies.
- **Estimated effort:** 5–10 days.
- **Recommended implementation order:** After P0.3/P1.1; before selling auto-renewing subscriptions.

### P1.4 — Complete Deal-to-Service Request handoff

- **Description:** Implement and persist the Business Truth relationship where an accepted deal may create multiple service requests. Current inventory contains `DealServiceRequestLinkEntity`, but static code search found no direct usage; `DealServiceImpl` handles deal lifecycle/billing without an evident service-request creation path. Define whether deal conversion is admin-triggered and idempotent and retain source deal references on resulting requests.
- **Business impact:** Carries qualified opportunity revenue into actual PropertyPilot service delivery.
- **Technical impact:** Prevents lost or duplicate work orders and preserves attribution/history.
- **Dependencies:** P1.2 request/quote contract; deal acceptance policy.
- **Estimated effort:** 3–6 days.
- **Recommended implementation order:** After P1.2; before using deals as the primary sales-to-delivery handoff.

### P1.5 — Deliver Property Health Check as a configured service offering

- **Description:** Add service configurations/checklists and assessment results for documentation, compliance, physical condition and risk, with linked findings, evidence and report output. Use the existing service request, document, evidence and report domains. Do not create a separate Property Health platform/module. Health/risk score calculation must be versioned and explainable.
- **Business impact:** Delivers the Property Health Check service and its findings/recommendations promised by Business Truth.
- **Technical impact:** Connects independent checks into a traceable customer deliverable without duplicating request/payment lifecycles.
- **Dependencies:** P1.1–P1.2 pricing/request flow; P0.4 evidence/document controls; product-approved score/check definitions.
- **Estimated effort:** 6–12 days for the initial configured service and report.
- **Recommended implementation order:** After shared request, evidence and quote foundations; before advertising a scored assessment.

## P2 — Operational Critical

### P2.1 — Make service catalog and ordinary business configuration operable

- **Description:** Provide administrative operations for service definitions and versioned visit, evidence, GPS, eligibility, SLA and completion policy. Current `ServiceEntity` stores several configuration flags, but the inventory identifies no coherent service-catalog administration controller/service. Ordinary service-rule changes should not require code deployment.
- **Business impact:** Operations can launch and tune chargeable service categories consistently across cities and properties.
- **Technical impact:** Moves service behavior from scattered code branches to versioned configuration used by requests and completion validation.
- **Dependencies:** P0.2 role policy; P1.1 pricing version model.
- **Estimated effort:** 5–9 days.
- **Recommended implementation order:** Before broadening the service catalog; define policy versions before migrated data/configuration.

### P2.2 — Complete request and assignment lifecycle ownership

- **Description:** Keep request transitions in one workflow owner, remove direct state/assignment mutations from generic update, record actor/time/reason on each transition, and consistently handle rejection, hold, reassignment and cancellation. `AgentAssignmentServiceImpl` has lifecycle methods that throw; the active transitions also exist in `ServiceRequestServiceImpl`/`VisitServiceImpl`. `ServiceRequestWorkflowService` has no implementation. Retain useful assignment functions only where an actual caller exists.
- **Business impact:** Staff and agents see truthful work status and every action has an accountable owner.
- **Technical impact:** Prevents invalid state updates and split-brain lifecycle behavior.
- **Dependencies:** P0.2 actor/authorization model; agree exact request/assignment states.
- **Estimated effort:** 4–8 days.
- **Recommended implementation order:** After P0.2; before operational volume increases.

### P2.3 — Enforce configurable visit count and completion requirements

- **Description:** Enforce `visitMandatory`, `maxVisitsAllowed`, and evidence/GPS requirements from the request’s frozen service configuration; support multiple visits under that maximum; define whether evidence is required per request or per visit. Existing completion validator checks a required completed visit, evidence-type existence across any visit and any validated GPS record.
- **Business impact:** No-visit services remain possible while field services cannot bypass their configured rules.
- **Technical impact:** Removes mismatch between service configuration and execution state and bounds repeat visit creation.
- **Dependencies:** P2.1 configuration version; P0.4 accepted-evidence state.
- **Estimated effort:** 3–6 days.
- **Recommended implementation order:** After P2.1 and P0.4; before enabling services with multi-visit requirements.

### P2.4 — Implement customer property monitoring service lifecycle

- **Description:** Use existing `MonitoringScheduleEntity`/`MonitoringAlertEntity` persistence as appropriate to implement service activation, scheduled/event-driven runs, observations/evidence, customer alerts, linked follow-on service requests, acknowledgment/resolution, pause/expiry and idempotent repeat execution. No monitoring service/controller/scheduler workflow is identified in the inventory.
- **Business impact:** Enables paid recurring/event-driven property, encroachment, tax, compliance and maintenance monitoring.
- **Technical impact:** Turns persistence-only tables into an auditable scheduled service and avoids duplicate alerts/requests.
- **Dependencies:** P2.1 catalog configuration, P0.4 evidence, P2.5 notifications and a defined initial monitoring product.
- **Estimated effort:** 8–15 days for one initial monitoring type and shared lifecycle.
- **Recommended implementation order:** After the service/evidence/notification foundations; launch monitoring types incrementally.

### P2.5 — Deliver template-driven operational and customer notifications

- **Description:** Implement durable event-triggered notifications for request, assignment, visit, completion, subscription, monitoring, health, payment and operational failure events; use templates, recipient/channel policy, retries, provider outcome and deduplication. The subscription reminder job currently logs IDs only; the notification service primarily reads stored subscription notifications.
- **Business impact:** Keeps customers informed by PropertyPilot rather than relying on agents/vendors to relay status.
- **Technical impact:** Makes external delivery observable and retry-safe without making notifications authoritative for domain state.
- **Dependencies:** Event definitions from P2.2/P2.4; email/SMS/in-app provider choice; P0.5 operational monitoring.
- **Estimated effort:** 5–9 days for in-app + one external channel and reusable delivery state.
- **Recommended implementation order:** Deliver base outbox/deduplication with P0.6; finish channels before monitoring/renewal launch.

### P2.6 — Provide geo coverage, cluster and territory operations

- **Description:** Connect the existing coverage-zone, cluster, location and agent records to service eligibility, assignment, travel-cost calculation, SLA coverage and GPS policies. Current inventory has geography/cluster/GPS persistence but no coherent admin API for coverage/cluster configuration.
- **Business impact:** Controls where service can be sold and enables geographically sound assignment and pricing as cities expand.
- **Technical impact:** Replaces loosely related geo records with validated domain use across catalog, request, pricing and field execution.
- **Dependencies:** P2.1 service configuration; pricing/assignment policy; map/distance provider choice if external distance data is required.
- **Estimated effort:** 5–10 days for one-city coverage and assignment/price integration.
- **Recommended implementation order:** Before selling outside currently validated coverage; then expand city by city.

### P2.7 — Complete customer transparency and report access

- **Description:** Connect customer dashboard/report operations to property, service request, visit history, validated evidence, health reports, subscription benefits, savings and monitoring alerts with resource-level access checks. Backend dashboard/report classes exist, but there is no working customer channel in this repository.
- **Business impact:** Provides the property history and work proof promised as the customer’s single view.
- **Technical impact:** Builds authorized read models and avoids unbounded cross-domain reads in customer-facing pages.
- **Dependencies:** P0.2 ownership policy; P0.4 evidence access; P0.7 client.
- **Estimated effort:** 4–8 backend days plus client work included in P0.7.
- **Recommended implementation order:** After request/evidence/report APIs stabilize; deliver with P0.7.

### P2.8 — Implement complaint and escalation service flow

- **Description:** Use existing complaint/comment and SLA persistence to support open, in-progress, escalated, resolved and closed states, assignment, SLA timers, resolution evidence and linked refund/remediation. The Business Truth includes this capability; no backend complaint service/controller lifecycle is identified.
- **Business impact:** Gives customers a PropertyPilot-owned path to resolve service quality problems and protects trust.
- **Technical impact:** Connects complaint ownership, SLA measurement, evidence and financial remediation in an auditable workflow.
- **Dependencies:** P0.2 resource access, P0.4 evidence and P2.5 notifications; approved SLA/escalation policy.
- **Estimated effort:** 5–10 days for intake, assignment, escalation and closure.
- **Recommended implementation order:** Before expanding customer volume; integrate after notification and audit foundations.

### P2.9 — Standardize audit actor and reason across business transitions

- **Description:** Reconcile existing `AuditableEntity`, service-request status history and lead/deal activity into a consistent record for actor, time, prior/new values and reason on important service, visit, evidence, pricing, subscription, payment, refund and admin configuration actions. The current implementation has audit foundations but accepts caller actor IDs in some request operations and does not demonstrate a single action trail across domains.
- **Business impact:** Makes service quality, customer disputes and operational accountability traceable.
- **Technical impact:** Establishes consistent audit semantics and protects against untraceable state changes.
- **Dependencies:** P0.2 principal identity; lifecycle owners from P2.2; P1 financial transactions.
- **Estimated effort:** 5–9 days for shared audit contract and high-value flows.
- **Recommended implementation order:** Define with P0.2; implement alongside P2.2 and P1.3/P1.4.

## P3 — Technical Debt

### P3.1 — Retire confirmed throwing or unmatched workflow abstractions

- **Description:** Consolidate assignment lifecycle into its active owner; remove/replace the throwing lifecycle methods in `AgentAssignmentServiceImpl` and the unmatched `ServiceRequestWorkflowService` only after confirming no external consumers. Preserve active create/get behavior if it has a caller.
- **Business impact:** No direct customer feature loss; reduces confusion in service operations.
- **Technical impact:** Removes unusable methods and duplicate workflow ownership.
- **Dependencies:** P2.2 lifecycle decision and consumer/reference verification.
- **Estimated effort:** 2–4 days.
- **Recommended implementation order:** After P2.2 so removal follows the canonical workflow.

### P3.2 — Consolidate duplicate repository interfaces

- **Description:** Review `LeadJpaRepository`/`LeadRepository`, `RoleJpaRepository`/`RoleRepository`, `UserRoleJpaRepository`/`UserRoleRepository`, and customer repository pairs; retain query methods and remove duplicates only after all injection points are moved.
- **Business impact:** No direct change to service scope.
- **Technical impact:** Reduces duplicate access abstractions and maintenance drift.
- **Dependencies:** Reference/use audit; tests for moved query behavior.
- **Estimated effort:** 1–3 days.
- **Recommended implementation order:** After P0/P2 workflow changes settle; low-risk cleanup.

### P3.3 — Verify persistence-only code and remove only confirmed unused scope

- **Description:** The Dead Code Report lists several dozen repository interfaces with declaration-only references, plus direct-reference candidates such as `DealActivityEntity`, `DealMilestoneEntity` and `DealServiceRequestLinkEntity`. Audit Java references, JPA relationships, migration/table use, live data, external consumers and release scope. Retire legacy customer marketplace entities/screens that have no internal fulfillment use; retain monitoring, complaints, refunds and evidence models because Business Truth calls for those capabilities and P2 implements them.
- **Business impact:** Keeps the product focused on PropertyPilot-managed services and avoids accidental exposure of vendor-marketplace behavior.
- **Technical impact:** Reduces persistence surface without deleting required business capabilities or live data.
- **Dependencies:** P1.4 deal handoff; P2.4/P2.8 capability decisions; data inventory and migration review.
- **Estimated effort:** 3–8 days for analysis and safe code/schema retirement; excludes data migration complexity.
- **Recommended implementation order:** Classify before cleanup; remove only after all P1/P2 consumers are known.

### P3.4 — Bound list APIs and optimize completion queries

- **Description:** Add pagination to administrative/customer collection APIs such as `BillingServiceImpl.getAllInvoices` and other unbounded list paths; batch evidence/GPS completion checks rather than issuing repeated existence queries per visit; add query-plan-based indexes.
- **Business impact:** Preserves service availability and response times as customers/properties/visits grow.
- **Technical impact:** Bounds memory and database work; uses evidence from measured query plans rather than speculative caching.
- **Dependencies:** API contract versions and representative PostgreSQL query/load data.
- **Estimated effort:** 4–8 days.
- **Recommended implementation order:** After P0 authorization and P2 customer/admin contract stabilization; complete before scale targets are exceeded.

### P3.5 — Normalize API validation, error and status contracts

- **Description:** Add bean validation consistently to inbound DTOs, standardize state-transition conflicts and error response shape, and align HTTP status semantics/OpenAPI with actual controller behavior. Prioritize service request/payment/evidence operations. Existing controllers vary in validation and use mixed route prefixes.
- **Business impact:** Reduces invalid bookings and makes customer/operations behavior predictable.
- **Technical impact:** Stabilizes API contracts and improves safe client integration.
- **Dependencies:** P0.2 endpoint and role map; P0.7 client API contract.
- **Estimated effort:** 4–8 days.
- **Recommended implementation order:** Stabilize critical request/payment contracts before P0.7 integration; finish remaining endpoints in sequence.

### P3.6 — Keep migration lineage unambiguous

- **Description:** Document that runtime migrations are Flyway under `propertypilot-backend/src/main/resources/db/migration`; classify `my-database-migration-project` and `docs/08_Implementation/database` as historical/planning unless explicitly adopted. There is no evidence the second migration tool is wired into runtime.
- **Business impact:** Avoids deployment operators applying unrelated schema scripts.
- **Technical impact:** Keeps production schema evolution reproducible; unlike the original V4 wording, this is not currently a proven competing runtime migration engine.
- **Dependencies:** P0.1 DDL/Flyway validation and repository ownership decision.
- **Estimated effort:** 1–2 days.
- **Recommended implementation order:** Document with P0.1; no separate runtime redesign needed.

## P4 — Future Enhancements

### P4.1 — Native field/mobile client and offline synchronization

- **Description:** Build a native agent app with authenticated assignment, scheduled visits, offline capture, GPS/photo/video/signature upload, safe retry/synchronization and conflict resolution. Current Flutter code is the default counter demo; offline-sync persistence alone is not an implemented mobile workflow.
- **Business impact:** Improves field productivity and supports remote/property-site operations in unreliable connectivity.
- **Technical impact:** Adds a client and synchronization protocol beyond the web-first pilot.
- **Dependencies:** Stable P0/P2 APIs and evidence object lifecycle.
- **Estimated effort:** 15–30 days for an initial field application, depending on platform and offline scope.
- **Recommended implementation order:** After web-first service workflows and evidence APIs stabilize.

### P4.2 — Proactive AI recommendations

- **Description:** Add recommendations such as tax due, declining health score or inspection need only after property history, health assessments and monitoring observations are reliable. Business Truth explicitly labels this future work; no recommendation implementation is required for the initial production service set.
- **Business impact:** Can create relevant service opportunities and improve property outcomes.
- **Technical impact:** Requires trusted history, explainable rules/models, consent and customer-safe presentation.
- **Dependencies:** P1.5 property-health results, P2.4 monitoring, quality data and product policy.
- **Estimated effort:** 10–25 days for a narrow rule-based first release; model/ML work excluded.
- **Recommended implementation order:** After P2 core data and workflows; not a production blocker.

### P4.3 — Expand service catalog and enterprise offerings

- **Description:** Add further property service categories and future builder, investor and corporate property-management packages through the same service, quote, entitlement, request, evidence and reporting model.
- **Business impact:** Extends property lifecycle coverage and future revenue without changing customer ownership.
- **Technical impact:** Exercises configuration-driven extension after core domain policy is stable.
- **Dependencies:** P1/P2 domain foundations and validated city/service operating metrics.
- **Estimated effort:** 5–15 days per bounded service family, varying by provider/legal integrations.
- **Recommended implementation order:** Add one service family at a time after production pilot feedback.

## Applicability and architecture assessment

### Architecture mismatches

1. **Service configuration ownership:** Requirements are partly stored on `ServiceEntity`, but a complete versioned service/evidence/visit/GPS/SLA administration path is not identified. This is a mismatch with configuration-first behavior.
2. **Workflow boundaries:** Request, assignment and visit state mutations span `ServiceRequestServiceImpl`, `VisitServiceImpl` and a partially throwing `AgentAssignmentServiceImpl`. This conflicts with Architecture Truth’s single accountable workflow owner per lifecycle.
3. **Quote-to-fulfillment consistency:** Pricing calculation, request amount and invoice creation are not tied to one accepted immutable quote. This breaks the intended domain relationship between pricing and a service request.
4. **Evidence/document trust boundary:** The current model stores paths/URLs and evidence metadata, but does not establish scoped object delivery and a validated acceptance lifecycle. This conflicts with evidence-first completion and the document-vault rules.
5. **Customer and operational channels:** The backend has API controllers, but current React files are stubs and mobile/admin roots are not implemented product clients. The codebase does not currently realize the customer transparency/operations experience.
6. **Property-first records:** Core property and owner links exist. New monitoring, health findings, report, payment and evidence flows must preserve direct property/request context rather than introduce property-independent customer work except lead qualification.
7. **Marketplace persistence:** Marketplace listing/inquiry/commission/contact-disclosure tables and frontend marketplace placeholders do not align with Business Truth as customer product functionality. Internal vendor/partner fulfillment remains applicable; a customer marketplace is not.
8. **Deployment topology:** A layered Spring modular application is compatible with Architecture Truth; there is no requirement here to split into microservices. Durable state, idempotent external callbacks and clear domain boundaries are the relevant completeness measures.

### Over-engineered or excess for initial release

- No active business module is classified as over-engineered solely because it has many files. Subscription versions, benefits, consumption, add-ons and renewals correspond to explicit subscription requirements.
- The **dormant marketplace persistence/UI scaffolding** is excess for PropertyPilot’s customer offering. Keep only partner/vendor fulfillment data that a real internal flow uses.
- **Declaration-only repository families** and relationship-only entity candidates are unproven release scope. They are cleanup candidates, not safe deletions until external references and data are checked.
- **Duplicate repository abstractions** and the second unused migration/documentation catalog add maintenance ambiguity without adding active runtime capability. Runtime uses Flyway; this is not evidence of two active migration engines.

### Under-engineered modules

- Payment settlement, refunds and subscription auto-renew are not integrated with confirmed payment state.
- Service quote calculation is missing required configured discount paths and immutable customer acceptance.
- Service catalog configuration, multi-visit policy, evidence acceptance and geo coverage do not yet form a fully driven execution pipeline.
- Monitoring, notification delivery and complaint/escalation are represented mainly by persistence or partial read/logging code.
- Property Health Check has no end-to-end assessment/result/report flow, while remaining a service category rather than a separate module.
- Cross-domain audit actor/reason, client channels, and production recovery/observability are incomplete for a customer operation.

## Readiness estimates

These are conservative planning judgments, not test coverage measurements. Class and test file counts are not counted as completed workflows. No tests, builds, deployment, database migration or penetration test were run as part of this plan.

| Measure | Estimate | Basis |
|---|---:|---|
| Business completeness | **54%** | Core customer/property/lead/deal/request/visit/evidence/pricing/subscription building blocks exist. Price discounts, payment-backed subscription, monitoring, complaint, health assessment outputs, notification delivery and consistent configured execution remain incomplete. Property Health is evaluated as a service category, not a missing platform module. |
| Architecture completeness | **48%** | Domain packages and reusable core entities/services exist, but configured domain ownership, quote/request linkage, workflow boundaries, evidence storage, audit semantics and working client channels are incomplete. The modular monolith itself is acceptable. |
| Backend completeness | **55%** | Many CRUD and service paths have implementation and unit/controller tests. Several critical financial and operational workflows are partial or persistence-only; no inference of completeness is made from the 496 Java source files. |
| Production readiness | **36%** | Hardcoded/inherited signing key and schema-update mode, broad resource access gaps, simulated payment success, unpaid renewal, absent recovery evidence and unverified integration tests are release gates. |
| Testing readiness | **44%** | 78 Java test sources span services, controllers, security/config and schedulers. Production-path PostgreSQL, access-control, payment-provider, idempotency, renewal and end-to-end evidence tests are not established as release gates; test pass state was not verified. |

## Estimated effort to production deployment

Estimated **95–170 engineer-days** for a web-first production pilot with a limited, explicitly selected service catalog, a working customer/operations browser channel, real payment provider, verified authz, production deployment/recovery and core request/evidence/quote tests. This is a person-day range, not elapsed calendar duration; parallel teams may shorten elapsed time after provider/config decisions are made.

The estimate excludes native mobile, broad enterprise expansion, AI, multi-city rollout beyond the first validated coverage configuration, vendor-specific integrations, provider/legal onboarding lead time, migration of unknown live data, and independent penetration-test remediation. Completing all P2 operational domains (including monitoring and complaint handling) across multiple service types may add approximately **20–35 engineer-days** beyond the narrow pilot. Mobile adds a separate **15–30 days**.

## Recommended first sprint sequence

1. P0.1 production configuration and migration policy; P0.2 access policy inventory and critical ownership enforcement.
2. In parallel, select payment provider and storage approach; implement P0.3 settlement and P0.4 evidence/document controls.
3. Add P0.6 PostgreSQL, authz, pricing/payment/renewal and completion tests as each behavior lands.
4. Complete P1.1/P1.2 quote-to-invoice consistency, then P1.3 subscription payment/entitlements and P1.4 deal conversion.
5. Deliver P0.5 restore/deployment proof, P0.7 usable web/operations channel, and chosen launch service set.
6. Before widening the catalog, complete P2.1–P2.5 configuration-driven execution, monitoring, notifications and Health Check results as applicable to the services actually sold.

