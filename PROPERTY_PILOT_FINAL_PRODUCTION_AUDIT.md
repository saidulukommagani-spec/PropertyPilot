# PropertyPilot Final Production Audit

**Audit date:** 2026-10-06  
**Scope:** Current repository source, configuration, migrations, tests, and client applications. This is an independent current-state audit, not a code change. The four requested truth/scope documents were used to frame expected behavior; findings below were verified against implementation evidence.

## Executive assessment

**Launch recommendation: NOT READY for customer production or a paid pilot.** The repository has substantial backend foundations, and some key paths have recently become more complete: service configuration flags exist, estimates persist calculation snapshots and line items, quotes can be accepted/rejected/expired, accepted quotes are linked to service requests, and assignment/visit/evidence completion has validation. Those components do not yet form a secure, complete customer transaction.

The primary launch blockers are: payment is recorded as successful from an authenticated API request without provider verification; the service request workflow does not create an invoice from the accepted quote or gate fulfillment on settled payment; the customer and staff web entry points are scaffold text rather than a runnable application; Monitoring and Property Health have no end-to-end execution workflow; evidence stores caller-supplied URLs without a controlled upload/download boundary; subscription renewal can grant a new paid period without payment; and production deployment/recovery controls have not been demonstrated.

The service remains organized as a single Spring application with broad shared persistence access. PropertyPilot’s non-marketplace ownership model is partly reflected in the service/quote/request flow, but marketplace/vendor-facing code and tables remain in the repository and frontend API surface. Their presence is inconsistent with the stated model and creates avoidable exposure and maintenance risk.

## Readiness scores

Scores measure demonstrated, end-to-end readiness for the Release V1 launch assumptions, not the fraction of source files present. A capability scores only when its relevant rules, authorization, persistence, customer/operations path, and operational behavior are demonstrated in code. Scores are conservative audit judgments, not test coverage percentages.

| Area | Score | Evidence-based assessment |
|---|---:|---|
| Business compliance | **48%** | Customer/property, lead/deal, configured services, quote, request, assignment, visit and evidence concepts exist. A direct paid customer flow, Monitoring service, Property Health service, and usable customer channel are not complete. Marketplace artifacts conflict with business truth. |
| Architecture compliance | **46%** | Domain types and service interfaces exist, but cross-domain orchestration directly accesses multiple repositories; there is no durable outbox/provider adapter boundary demonstrated; separate Monitoring/Health workflows are absent. |
| Security | **42%** | JWT secret length is validated and production requires an external secret; several ownership and assignment helpers exist. Payment authorization is insufficient because success is caller asserted, not provider verified; evidence is an arbitrary URL; role/resource enforcement is inconsistent across legacy route families. |
| Database | **57%** | PostgreSQL/Flyway, relational FKs, audit columns and many domain tables exist. Release-critical workflows are not represented consistently (no observed invoice-to-quote/request immutable billing path, no health assessment lifecycle, little provider settlement/idempotency evidence); migration deployment and restore have not been exercised here. |
| Service workflows | **55%** | Service configuration and request/assignment/visit completion logic are present. Completion checks are simplified: evidence rule codes are hardcoded, evidence review/approval is absent, required evidence may be attached at request level, and completed visits accept any historical verified GPS record. |
| Monitoring workflows | **10%** | Schedule/alert entities, repositories and dashboard counts exist; there is no Monitoring application service, controller, execution scheduler, alert creation/review, or service-request generation flow in current source. |
| Property Health workflows | **5%** | No Property Health assessment entity/service/controller or health-specific workflow was found. The generic catalog could host an offer, but it does not implement assessment composition or report output. |
| Pricing and quote lifecycle | **67%** | Server-side service price selection, rule/profile calculation, discounts, snapshot, line items and quote decisions exist. Customer quote generation is absent from the public quote path because estimate calculation is restricted to operations/admin; estimate creation and quote decision concurrency/idempotency are not fully evidenced. |
| Fulfillment lifecycle | **62%** | Assignment delegation, multi-visit creation, visit state, evidence records, GPS distance check, and request auto-completion hooks exist. Assignment creation is admin-only in the service, policy validation is shallow, and evidence/GPS trust and completion criteria have gaps. |
| Billing and payment lifecycle | **18%** | Invoice persistence and some internal creation calls exist. No verified payment-provider adapter/webhook/reconciliation path was found; payment API immediately persists `SUCCESS` and marks invoice `PAID`. Service-request invoices are not created from accepted quote totals. |
| Operations and reporting | **37%** | Admin operations dashboard, monitoring counts, status-history page and SLA report endpoints exist. Audit covers request status history only; SLA loads all requests into memory and applies a fixed four-hour at-risk threshold; monitoring is a count of dormant tables; revenue is lifetime aggregates, not a time-series operational finance report. |
| Observability | **25%** | Actuator health/info and structured application logging configuration exist. No error tracking, metrics/tracing, alert delivery, payment reconciliation alerting, or demonstrated operational runbook is present in the deployment artifacts inspected. |
| Performance | **40%** | Hikari pool limits and repository aggregate methods exist. SLA report calls `findAll()` and computes in memory; several aggregate/dashboard paths and large CRUD endpoints lack demonstrated pagination or bounded access; no load/performance evidence is present. |
| Deployment readiness | **20%** | Docker image uses a non-root runtime and Flyway plus Hibernate validate are configured. Compose is explicitly a dev profile with literal local PostgreSQL credentials; no production deployment manifest/CI/release/rollback/backup restore proof was found. |
| Testing readiness | **43%** | Backend has 84 test source files across controller/service tests, including new pricing/quote/assignment/fulfillment/report tests. The inspected suite is primarily unit/controller level; no frontend component tests were found, and PostgreSQL/Flyway, provider callback, full payment-to-service, Monitoring/Health, backup/restore and production profile integration gates are not evidenced. |

**Overall production readiness: 31%.** Weighted toward security, revenue integrity, business completion, deployment and operational recoverability. Passing isolated unit tests would not remove the identified end-to-end launch blockers.

## Detailed findings

Severity definitions: **CRITICAL** means customer money/data or the core paid service outcome cannot be trusted; **HIGH** means a required V1 workflow or meaningful production safety control is absent; **MEDIUM** means the function exists but has material completeness, scale or operating limitations; **LOW** means cleanup or non-launch scope.

### 1. Business compliance and architecture

#### CRITICAL — The product cannot complete a direct paid V1 service transaction

`PricingController` exposes estimate calculation to ADMIN/OPERATIONS only. `QuoteController` exposes decisions and customer quote reads, but the customer-facing workflow has no customer-accessible estimate/quote creation operation. `ServiceRequestServiceImpl.createRequest` correctly requires an accepted quote and links the request to its estimate, but the path does not create an invoice or require settled payment before assignment/fulfillment. `BillingService.createInvoice` is called from deal and subscription code, not from the accepted quote/service-request path. As implemented, there is no proven Customer → quote → accepted request → invoice → confirmed payment → fulfillment chain.

**Business impact:** Customers cannot complete the central revenue transaction safely; operations may fulfill unbilled/unpaid work or issue a price not derived from the accepted quote.  
**Files:** `web/controller/PricingController.java`, `web/controller/QuoteController.java`, `application/service/impl/PricingEngineServiceImpl.java`, `application/service/impl/ServiceRequestServiceImpl.java`, `application/service/impl/BillingServiceImpl.java`, `application/service/impl/DealServiceImpl.java`.

#### HIGH — Monitoring is represented by schema and dashboard counts, not a customer service

`MonitoringScheduleEntity`, `MonitoringAlertEntity`, repositories and migrations exist. There is no Monitoring service/controller or scheduler to create/advance a schedule, execute an observation, produce evidence, open/resolve an alert, notify a customer, or create a follow-on service request. `OperationsReportingServiceImpl.getMonitoringDashboard` only counts ACTIVE schedules, due schedules and unresolved alerts. Thus the dashboard can report rows without any product workflow that owns those rows.

**Business impact:** The V1 Monitoring offer required by Release Scope cannot be sold and delivered with a defined outcome.  
**Files:** `infrastructure/persistence/entity/MonitoringScheduleEntity.java`, `MonitoringAlertEntity.java`, `repository/MonitoringScheduleRepository.java`, `MonitoringAlertRepository.java`, `application/service/impl/OperationsReportingServiceImpl.java`, `db/migration/V013__create_subscriptions.sql`, `V018__create_monitoring_integrations_and_refunds.sql`.

#### HIGH — No Property Health assessment workflow exists

No health assessment entity, controller, application service, or specific report composition was found. Existing service catalog/request/report foundations do not calculate or persist health dimensions, severity, findings, recommendations, or assessment history. The Business and Architecture Truth define Health Check as a service executed through the common request model; that model alone does not provide the assessment result.

**Business impact:** Release V1’s promised Property Health Check service cannot be fulfilled or evidenced.  
**Files inspected:** `infrastructure/persistence/entity/`, `application/service/`, `web/controller/`, and Flyway migrations under `propertypilot-backend/src/main` (no health workflow type/path found).

#### HIGH — Customer and operations clients are not runnable product applications

`propertypilot-frontend/src/App.tsx`, `src/main.tsx`, and `src/app/routes/AppRoutes.tsx` contain directory-outline text rather than React application code; `package.json` is an install instruction rather than a valid project manifest. Feature component files exist but are not wired into a runnable app. `admin-portal/README.md` is a suggested structure and install instruction only. `mobile_app/lib/main.dart` is the Flutter counter demo. The intended browser-first V1 needs a usable customer and internal operations channel.

**Business impact:** Staff/customers have no deployable end-to-end channel, regardless of backend endpoint count.  
**Files:** `propertypilot-frontend/src/App.tsx`, `src/main.tsx`, `src/app/routes/AppRoutes.tsx`, `propertypilot-frontend/package.json`, `admin-portal/README.md`, `mobile_app/lib/main.dart`.

#### HIGH — Marketplace-oriented legacy surfaces remain

The backend includes `/api/v1/marketplace-listings/**` authorization and marketplace listing entities/tables, and frontend `src/api/marketplace.ts` plus vendor and quotation pages. The Business Truth explicitly says PropertyPilot is not a marketplace and vendors/agents are internal fulfillment partners. No evidence shows these interfaces are the customer service offer path, but their presence creates a conflicting API surface and operational concept.

**Business impact:** Customer expectations, access policy and internal product language can drift toward vendor selection/marketplace transactions.  
**Files:** `config/SecurityConfig.java`, marketplace controller/entity/repository package files, `propertypilot-frontend/src/api/marketplace.ts`, `propertypilot-frontend/src/features/marketplace/**`, and migration `V016__create_operations_and_marketplace.sql`.

#### MEDIUM — Cross-domain service code directly coordinates persistence

`PricingEngineServiceImpl` injects customer, property, service, pricing, coupon, redemption, subscription, cluster and estimate repositories. `ServiceRequestServiceImpl` coordinates quote, property/service eligibility, assignment, history and completion by accessing repositories/services across domains. This is a monolith, which is acceptable for V1, but the Architecture Truth calls for domain-owned application interfaces; direct cross-domain repository access makes rule ownership and transactional evolution harder. No durable outbox/event boundary was found for external side effects.

**Files:** `application/service/impl/PricingEngineServiceImpl.java`, `ServiceRequestServiceImpl.java`, `DealServiceImpl.java`, and persistence repository interfaces.

### 2. Security and evidence

#### CRITICAL — Payment API trusts caller-supplied payment details and marks invoices paid

`PaymentServiceImpl.recordPayment` accepts the caller’s provider, provider payment ID, method and amount; if amount equals invoice total, it creates a `PaymentEntity` with `PaymentStatus.SUCCESS` and sets invoice `PAID`. It generates a random idempotency key rather than using a stable client/provider key. There is no provider signature verification, webhook, settlement query, replay protection or reconciliation in the payment code. `PaymentController` permits CUSTOMER and ADMIN, so an authenticated customer can assert that a payment succeeded.

**Business impact:** Fraudulent payment claims can unlock paid service without funds; replay/race behavior can create inconsistent finance records.  
**Production impact:** Direct revenue loss, financial ledger corruption and inability to reconcile provider settlement.  
**Files:** `web/controller/PaymentController.java`, `application/service/impl/PaymentServiceImpl.java`, `application/dto/CreatePaymentRequest.java`, `infrastructure/persistence/entity/PaymentEntity.java`, `PaymentRepository.java`.

#### HIGH — Evidence is persisted as an arbitrary URL with caller-declared metadata

`EvidenceServiceImpl.createEvidence` validates parent request/visit access and status, but stores `fileUrl`, `mediaType` and checksum directly from `CreateEvidenceRequest`. The code has no upload validation, storage adapter, ownership-bound object key, malware/content check, safe download authorization or immutable provenance check. Completion validation treats existence of an evidence type row as fulfillment, without review/acceptance state.

**Business impact:** Required proof can be missing, replaced or point to attacker-controlled/external content; sensitive documents can be exposed.  
**Files:** `application/service/impl/EvidenceServiceImpl.java`, `application/dto/CreateEvidenceRequest.java`, `infrastructure/persistence/entity/EvidenceEntity.java`, `web/controller/EvidenceController.java`, `CompletionValidationServiceImpl.java`.

#### HIGH — JWT route authentication is not a substitute for consistent resource authorization

`SecurityConfig` has a large mix of `/api/v1/**` and legacy `/api/**` route families, while the fallback is simply authenticated. Several controllers have role annotations, and `SecurityService` now includes customer, assignment, visit, evidence and invoice access methods; however the audit found critical trust flaws in payments and arbitrary evidence URLs and did not find a single uniformly applied policy at each controller/service boundary. In `BillingServiceImpl.createInvoice`, invoice creation accepts a customer UUID and arbitrary entity/amount without an access check; current callers include deal/subscription operations and rely on upstream assumptions. The code should not rely on UUID opacity or route-level authentication.

**Files:** `config/SecurityConfig.java`, `security/SecurityService.java`, `web/controller/**`, `application/service/impl/BillingServiceImpl.java`.

JWT configuration itself has improved: `JwtService` validates a nonblank secret of at least 32 UTF-8 bytes, and the prod profile sources `JWT_SECRET`; the base profile does not supply a signing default. `JwtAuthenticationFilter` parses signed JWTs and reloads user authorities from the user-details service. These are positive controls, but they do not address resource-level policy or provider-trust gaps.

### 3. Database and financial integrity

#### CRITICAL — Request pricing is not shown to become an immutable invoice balance

An accepted quote snapshot is stored on `PricingEstimateEntity` and linked through `ServiceRequestEntity`; this preserves the quote/request association. However `ServiceRequestServiceImpl.createRequest` does not issue an invoice from that snapshot, and invoice creation takes a free-form amount and entity reference. `BillingServiceImpl.createInvoice` sets tax to zero and hardcodes INR. This breaks the invariant that the amount billed is the accepted customer amount and tax breakdown.

**Files:** `PricingEngineServiceImpl.java`, `QuoteServiceImpl.java`, `ServiceRequestServiceImpl.java`, `BillingServiceImpl.java`, `InvoiceEntity.java`, `PricingEstimateEntity.java`, migrations `V031__formal_quote_lifecycle.sql`, `V032__link_service_requests_to_quotes.sql`.

#### HIGH — Subscription renewal scheduler grants entitlements without payment

`SubscriptionAutoRenewScheduler.processRenewal` creates a new ACTIVE subscription for a year, records renewal status `SUCCESS` and closes the old subscription without calling billing or payment services. Subscription reminder/expiry/renewal schedulers are enabled globally by `@EnableScheduling`. Release Scope says subscriptions should not be sold in V1 until payment-backed entitlement handling is complete; current renewal behavior violates that safety assumption if auto-renew subscriptions exist.

**Files:** `application/scheduler/SubscriptionAutoRenewScheduler.java`, `PropertyPilotApplication.java`, `application/service/impl/CustomerSubscriptionServiceImpl.java`, migrations `V013__create_subscriptions.sql` and subscription extensions.

#### MEDIUM — Invoices have weak uniqueness and lifecycle evidence

`BillingServiceImpl.generateInvoiceNumber` uses current epoch milliseconds; concurrent calls can collide, and there is no observed stable idempotency constraint for one invoice per request/quote. Invoice creation permits arbitrary entity type/id and amount, uses zero tax and fixed INR. Refund entity/repository/migration exists, but payment-provider refund/reconciliation lifecycle was not found.

**Files:** `application/service/impl/BillingServiceImpl.java`, `infrastructure/persistence/entity/InvoiceEntity.java`, `PaymentEntity.java`, `RefundEntity.java`, migration `V009__create_contracts_invoices_and_payments.sql`, `V018__create_monitoring_integrations_and_refunds.sql`.

#### MEDIUM — Schema is broad relative to active workflows

Flyway creates many domains (subscription, monitoring, offline sync, NRI relationship, marketplace, refund, customer engagement). Current Monitoring/Health workflows are absent and the marketplace model conflicts with business truth. Hibernate uses `ddl-auto: validate`, Flyway is enabled and validation-on-migrate is true, which avoids an active Hibernate/Flyway schema-writer conflict. This audit did not apply migrations to a clean PostgreSQL instance, so migration ordering/model validation remains unproven.

**Files:** `application.yml`, `application-prod.yml`, `src/main/resources/db/migration/V001...V032`.

### 4. Service and fulfillment workflows

#### HIGH — Configured completion requirements are only partly configurable and can be satisfied by stale proof

`CompletionValidationServiceImpl` hardcodes evidence codes `BEFORE_PHOTO`, `AFTER_PHOTO`, `SIGNATURE`, and `DOCUMENT` from service booleans; it does not evaluate an ordered/versioned evidence rule set, required counts, per-visit evidence rules, or human review. For GPS it calls `existsByVisit_VisitIdAndValidationStatus(..., "VERIFIED")`, so an earlier verified record remains sufficient even if a newer capture is rejected. Required evidence can be attached to the overall request rather than the visit. For visit-required services it only requires at least one completed visit; it does not enforce a configured minimum/expected visit count. These shortcuts do not meet rule-driven completion for a varied launch catalog.

**Files:** `application/service/impl/CompletionValidationServiceImpl.java`, `EvidenceServiceImpl.java`, `GpsVerificationServiceImpl.java`, `ServiceEntity.java`, evidence/GPS repositories.

#### HIGH — GPS “verification” relies on untrusted submitted coordinates

`GpsVerificationServiceImpl` compares request-supplied latitude/longitude to stored property coordinates and records VERIFIED based on distance. It does not bind coordinates to device/location attestation, trusted capture metadata or anti-replay proof. A client can submit the property’s coordinates. Remote-service GPS rejection and configurable radius are implemented; the evidence source itself is not trustworthy enough for fraud-sensitive verification.

**Files:** `application/service/impl/GpsVerificationServiceImpl.java`, `application/dto/CreateGpsVerificationRequest.java`, `GpsVerificationEntity.java`.

#### MEDIUM — Assignment lifecycle is split and assignment creation is narrower than operations policy

`AgentAssignmentServiceImpl` delegates lifecycle state transitions to `ServiceRequestServiceImpl`, which is good reuse and avoids duplicate state machines. Yet `createAssignment` calls `validateAdminAccess`, while business fulfillment may be managed by OPERATIONS staff. The assignment API also passes distance/ETA values from the request and stores them without demonstrated server-side recomputation. Rejected/ended assignments and reassignment consistency should be checked against all existing rows before launch; request and assignment statuses are separate persisted representations.

**Files:** `application/service/impl/AgentAssignmentServiceImpl.java`, `ServiceRequestServiceImpl.java`, `AgentAssignmentEntity.java`, `ServiceRequestController.java`, `AgentAssignmentController.java` if present.

#### MEDIUM — Request completion does not enforce payment/report/customer-delivery gates

Completion is driven by visit/evidence/GPS readiness, then request status changes. The completion validator does not check invoice settlement, subscription entitlement, customer approval/report generation, or required document review. `ReportServiceImpl` is a separate report path and does not establish a completion transaction with payment and evidence.

**Files:** `CompletionValidationServiceImpl.java`, `ServiceRequestServiceImpl.java`, `ReportServiceImpl.java`, `BillingServiceImpl.java`.

### 5. Pricing, leads, deals, notifications and operations

#### HIGH — Pricing is back-office initiated and therefore not a customer purchase path

`PricingEngineServiceImpl` derives base cost from active rules/service price and persists standard/customer breakdown, discounts, line items and snapshot. But `PricingController.calculatePricing` is ADMIN/OPERATIONS only and accepts a DTO containing customer/property/service identifiers. `QuoteServiceImpl` manages quote decisions but does not originate the estimate. Thus pricing logic exists but is disconnected from self-service acquisition; quote expiry is covered by a scheduler and status string `ACTIVE` rather than a typed enum.

**Files:** `PricingController.java`, `PricingEngineServiceImpl.java`, `QuoteServiceImpl.java`, `QuoteExpiryScheduler.java`, `PricingEstimateEntity.java`.

#### MEDIUM — Deal flows coexist with quote/request flow and retain legacy invoice generation

`DealServiceImpl` and `DealBillingServiceImpl` both call the shared `BillingService.createInvoice`; `DealServiceImpl` contains multiple invoice creation sites. The business truth allows deals to generate service requests, but deal billing is not proven to preserve an accepted quote snapshot or converge on the canonical paid service-request transaction. This is a duplicate/parallel financial path that risks inconsistent customer amounts and repeated invoice creation.

**Files:** `application/service/impl/DealServiceImpl.java`, `DealBillingServiceImpl.java`, `BillingServiceImpl.java`.

#### MEDIUM — Notifications are stored/read but delivery is not operationally complete

Notification entities/templates/repositories and subscription notification reads exist. No outbound email/SMS/push provider adapter or durable retry/failed-delivery operational flow was found in current implementation. Customer-facing transaction and schedule/alert communication therefore lacks proven delivery. This is an operational risk, even if the V1 pilot initially uses manual contact.

**Files:** notification entities/repositories/services and `SubscriptionNotificationServiceImpl.java`; no provider implementation was located in `application/service/impl`.

#### MEDIUM — Reporting endpoints have unbounded or narrow definitions

`OperationsReportingServiceImpl.getSlaReport` loads `requests.findAll()` and computes every SLA row in memory; the fixed four-hour risk window is not a configured policy. Audit reporting exposes only service-request status history and the current actor field; it is not a cross-domain audit report. Monitoring dashboard reports record counts, not executions or outcomes. `RevenueDashboardServiceImpl` uses repository aggregates but reports lifetime totals and broad invoice sums, without a dated cash/reconciliation view.

**Files:** `application/service/impl/OperationsReportingServiceImpl.java`, `RevenueDashboardServiceImpl.java`, `web/controller/AdminOperationsController.java`, SLA/history/revenue repositories.

### 6. Production configuration, deployment and test controls

#### HIGH — No production release, migration, rollback or recovery path is evidenced

`application-prod.yml` correctly requires external DB/JWT settings; `application.yml` uses Hibernate validate and Flyway, and production logging disables SQL/bind trace. `Dockerfile` runs the packaged app as a non-root user. These are sound foundations. However the only inspected Compose service activates `dev`, uses literal local PostgreSQL credentials, and exposes database/backend ports; no production deployment manifest, CI workflow, rollout/rollback policy, backup schedule, restore drill, or production-like acceptance result was found. No production profile startup/migration result is in repository evidence.

**Files:** `application.yml`, `application-prod.yml`, `application-dev.yml`, `propertypilot-backend/Dockerfile`, `docker-compose.yml`, and absence of deployment/CI artifacts.

#### MEDIUM — Health endpoints exist without operational telemetry

Actuator exposes health and info and hides details; graceful shutdown is enabled. This does not supply application metrics, trace correlation, alerting, dashboards for process/provider failures or SLOs. Admin operational reports are business aggregates and cannot detect a stalled scheduler, failed webhook or repeated outbound notification failures.

**Files:** `application.yml`, `application-prod.yml`, `PropertyPilotApplication.java`; absence of metrics/tracing/alert configuration.

#### MEDIUM — Automated tests are not a launch proof

The backend contains 84 test source files, including tests for security helpers, pricing, quote, service requests, visits, GPS, payments and reporting. The repository does not show a PostgreSQL migration/startup suite, payment-provider verification/replay integration test, complete direct-purchase-to-delivery test, Monitoring/Health workflow test, or restore/deploy gate. No frontend test files matching `.test.*`/`.spec.*` were found. This audit did not run builds or tests.

**Files:** `propertypilot-backend/src/test/java/**`, `propertypilot-frontend/src/testing/**`, `propertypilot-frontend/package.json`.

## Dead, duplicate and overbuilt areas

These are current-state observations, not a recommendation to delete code blindly:

- **Marketplace artifacts conflict with Business Truth:** marketplace listing API/model and frontend vendor/quotation screens have no place in the V1 branded service model. Confirm no external data dependency before retiring these surfaces.
- **Admin portal is documentation only:** `admin-portal/README.md` describes a possible React structure; it has no active application implementation. The React frontend also has many feature files but scaffold entry files and no valid package manifest, so those pages are effectively disconnected/dead at runtime.
- **Mobile app is demo code:** `mobile_app/lib/main.dart` is the Flutter counter sample; it is not a PropertyPilot field workflow. This is deferrable under web-first Release V1, but should not be represented as an implemented channel.
- **Parallel billing paths:** `DealServiceImpl` and `DealBillingServiceImpl` both create invoices through `BillingServiceImpl`, while the canonical accepted quote/request path does not. Preserve only the required business paths after financial invariants are defined.
- **Dormant schema:** monitoring tables have only repositories/dashboard reads and no execution service. Property Health has no schema/workflow found. Marketplace, NRI relationship and offline-sync tables appear broader than the bounded web V1 launch; confirm whether mobile/offline/NRI needs remain in scope.
- **Overbuilt relative to launch:** subscriptions include plans, versions, add-ons, pauses, entitlement consumption, renewal schedulers and notifications, while Release V1 defers paid subscriptions until payment-backed entitlement handling works. The breadth increases operational failure surface without supporting the V1 direct-service revenue path.
- **Duplicate pricing layers:** `PricingEngineServiceImpl`, `PricingRuleEngineServiceImpl`, `PricingCalculatorServiceImpl`, `PricingRuleServiceImpl`, `PricingConfigurationServiceImpl` and profile-loader/history services all exist. Their distinct call sites should be preserved where responsibilities are real, but unreferenced methods/classes need a usage-based dead-code review. No class is labeled wholly dead here without proving no injection/reflection/endpoint usage.

## Production blockers (must close before paid customer launch)

1. Replace caller-asserted payment success with provider-confirmed payment, signed webhook or trusted provider query, stable idempotency, replay-safe state transitions and reconciliation.
2. Create the invoice from the accepted quote/request immutable snapshot; enforce tax/currency/amount invariants and gate assignment/service execution on confirmed payment or explicit authorized no-charge entitlement.
3. Deliver a runnable customer and internal operations browser application for onboarding, property, quote acceptance, payment, request status/evidence/report, and minimum catalog/CRM/assignment operations.
4. Implement a bounded Monitoring service lifecycle (purchase/request linkage, due execution, observation/evidence, alert, review/notification and follow-on request as applicable) or remove Monitoring from the launch offer; current Release V1 explicitly includes it.
5. Implement a bounded Property Health assessment/report through the service/request lifecycle or remove it from the launch offer; current Release V1 explicitly includes it.
6. Replace arbitrary evidence URL intake with controlled storage, upload checks, authorized access/download, capture provenance and completion-time validation of the configured evidence requirements.
7. Review and close endpoint/resource authorization for customer, agent, operations and admin records; include unauthorized cross-customer/agent tests at service and web boundaries.
8. Disable subscription auto-renew/activation paths for V1 until renewal is provider-paid and idempotent; current scheduler creates active annual access and records `SUCCESS` without payment.
9. Establish production deployment settings/secrets, database least privilege, migrations, backup/restore and rollback procedures; verify a production-like startup against PostgreSQL.
10. Gate launch on production-like end-to-end tests for direct purchase, payment callback retry, invoice consistency, request completion, evidence authorization, Monitoring/Health output and denied cross-customer access.

## Business-functionality gaps vs. production-hardening gaps

**Business-functionality gaps:** customer quote origination; quote-derived invoice and paid request transition; complete direct-purchase workflow; Monitoring execution/alerts; Property Health assessment/report; channel for customers and internal fulfillment; consistent lead/deal-to-service conversion; configured evidence acceptance and outcomes.

**Production-hardening gaps:** provider authentication and payment reconciliation; resource authorization coverage; controlled evidence storage; renewal idempotency; migration/restore/release verification; observability and operational alerting; bounded/paginated reports; production integration and end-to-end tests.

Some findings span both groups. For example, evidence storage is a missing architecture capability and a data-security risk; payment confirmation is a missing revenue workflow and a financial control failure.

## First release sequence

1. Freeze V1 offers and disable unsupported subscription renewal/marketplace exposure.
2. Close role/resource authorization and controlled evidence storage, and make production configuration/DB migration validation reproducible.
3. Connect customer-accessible pricing and quote generation to accepted quote snapshots.
4. Generate one immutable invoice from each accepted quote/request; integrate a payment provider with verified settlement, idempotency and reconciliation; block fulfillment before settlement.
5. Complete assignment, configured visit/evidence and report delivery; test no-visit and multi-visit paths.
6. Implement the bounded Monitoring and Property Health offers on the service/request lifecycle.
7. Build and run the browser customer/operations applications; retire or hide conflicting marketplace UI/API.
8. Add PostgreSQL/provider end-to-end tests, operational metrics/alerts, backup-restore rehearsal, deployment and rollback acceptance; then begin a constrained pilot.

## Final verdict

**Actual production readiness: 31%.**  
**Recommendation: NOT READY.**

The codebase is a meaningful backend foundation, not an enterprise-grade production service today. The score reflects demonstrated workflows and trust boundaries rather than class or test counts. The immediate stop-ship issue is that the payment API can mark an invoice paid without provider confirmation, and the core accepted-quote path has no proven invoice/settlement gate. Missing runnable clients and absent Monitoring/Property Health workflows independently block the defined V1 release. Production configuration has improved—external production credentials, JWT key validation, Flyway and Hibernate validation, quiet production SQL logging, and a non-root container are present—but deployment, recovery and end-to-end operation remain unproven.

**Audit limitations:** Source and repository artifacts were inspected read-only. No build, tests, database migration, deployment, penetration test, load test or provider sandbox flow was run for this audit. Absence claims refer to the current repository search and implementation paths inspected; they should be rechecked if generated/external deployment artifacts or provider integrations live outside this repository.
