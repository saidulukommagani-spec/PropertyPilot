# PropertyPilot Monitoring and Property Health Implementation Plan

**Status:** Proposed execution plan; no code changes have been made.  
**Business inputs:** `PROPERTY_PILOT_MONITORING_AND_HEALTH_TRUTH.md` (including Final Business Decisions), `PROPERTY_PILOT_MONITORING_GAP_ANALYSIS.md`, and `PROPERTY_PILOT_ARCHITECTURE_TRUTH.md`.  
**Planning principle:** Keep Monitoring and Property Health inside the existing PropertyPilot service/request architecture. Add persistence only where schedule execution, immutable outcomes, or customer recommendation conversion cannot be represented by existing records.

## Target workflow

**Monitoring:** one-time service request or subscription plan → property/service schedule when recurring → idempotent monitoring run → normal service request/assignment/visit → configurable checklist and evidence → report → optional Health assessment, findings, recommendations and alerts → customer notice → accepted recommendation creates a quote and/or service request.

**Property Health:** one-time service request or subscription entitlement → normal request/fulfillment (where required) → evidence and observations → immutable weighted assessment snapshot → findings/recommendations → report and customer history. No AI scoring in Release V1.

An individual Monitoring purchase is not required to create a subscription. A one-time, non-recurring job can use only the normal service request and visit flow. A recurring individual purchase can own a schedule through its originating purchase/request. Subscription-backed schedules use the existing subscription and entitlement records. Distinct monitoring services for one property remain independent schedules.

## Existing components to preserve

- Service catalog/configuration: `ServiceEntity`, `ServiceCatalogConfigurationService`, `ServiceCatalogConfigurationServiceImpl`.
- Customer, property, service request and audit lifecycle: `CustomerEntity`, `Property`, `ServiceRequestEntity`, `ServiceRequestServiceImpl`, `ServiceRequestStatusHistoryEntity`.
- Subscription plans, versions, customer subscriptions and entitlement ledger: existing `SubscriptionPlan*`, `CustomerSubscriptionEntity`, `SubscriptionEntitlementConsumptionEntity` and services.
- Current monitoring persistence: `MonitoringScheduleEntity`, `MonitoringScheduleRepository`, `MonitoringAlertEntity`, `MonitoringAlertRepository`.
- Assignment, visits, GPS and evidence: `AgentAssignmentServiceImpl`, `VisitServiceImpl`, `GpsVerificationServiceImpl`, `EvidenceServiceImpl` and their existing records.
- Reports and notifications: `ReportServiceImpl`, `ReportEntity`, existing notification templates/records and delivery services where available.
- Append-only business audit: `audit_log` from `V008__create_reference_data_and_audit_log.sql`, whose DB triggers reject update and delete. Use it for material transitions; do not treat mutable audit columns as history.
- Existing quote and pricing path for customer recommendation conversion: `PricingEngineServiceImpl`, `QuoteServiceImpl`, `QuoteController`, `ServiceRequestServiceImpl`. Reconcile authorization and quote origination before customer acceptance uses it.

## Phase 0 — Contracts and configuration fit

**Goal:** Settle the exact persisted contract and reuse current service/plan configuration before adding tables. No schema migration should be needed in this phase.

1. Map all Monitoring and Health requirements against `ServiceEntity`, its configuration DTOs, subscription plan version benefits, existing property type, evidence type, service visit configuration and report lifecycle.
2. Define the versioned configuration keys/shape for configurable recurrence (including day intervals/custom frequency), property-type checklists, evidence/GPS requirements, report contents, notification events, Health dimensions/weights and score bands.
3. Define acquisition records: one-time Monitoring request, recurring individually purchased Monitoring, subscription schedule, one-time Health request, and subscription-based Health entitlement. Define how accepted quote/payment/authorized entitlement activates each.
4. Define monitoring run state transitions, pause/resume semantics, missed-visit outcomes (reschedule policy is optional; administrator notification and immutable audit are required), and independent schedule identity by property + service + purchase/plan.
5. Define recommendation customer actions and whether acceptance creates quote only or quote plus request. In either case preserve the source finding and assessment; do not open vendor selection.

| Required layer | Plan |
|---|---|
| Entities | Reuse `ServiceEntity`, `SubscriptionPlanVersionEntity`, `CustomerSubscriptionEntity`, `MonitoringScheduleEntity`, `MonitoringAlertEntity`; no new entity pending configuration review. |
| Repositories | Reuse existing service, subscription, schedule, alert, property and audit repositories. |
| Services | Reuse `ServiceCatalogConfigurationService`, subscription/entitlement services, pricing/quote services, and `SecurityService`. |
| Controllers | Existing admin service/subscription configuration endpoints; existing customer quote/request endpoints. Identify contract additions only after role/resource review. |
| DTOs | Existing service configuration, plan/version, quote and request DTOs; define JSON/API compatibility contract before new request/response types. |
| Database changes | None. |
| Reusable components | Existing entities, `audit_log`, service request flow, quote acceptance and report workflow. |
| Migration strategy | Inspect real deployed schema and migration history; never edit an already applied migration. Freeze versioned config semantics before adding schema. |
| Risks | Reusing unversioned JSON without an immutable request/run snapshot would make past scores and checklists change when admins edit configuration. Subscription auto-renew currently must not be treated as paid entitlement until its payment lifecycle is corrected. |

## Phase 1 — Versioned service policy and minimal persistence

**Goal:** Store policy versions and execution cycles so configuration can change safely and scheduler retries cannot duplicate work.

1. Add a single configuration source for monitoring policy and Health scoring policy. Prefer existing service/plan JSON configuration if it is already persisted, validated and versioned. If not, add only the necessary JSONB configuration and version/effective metadata rather than a separate plan/catalog hierarchy.
2. Extend `MonitoringScheduleEntity` to represent either subscription-backed schedule or an individually purchased recurring service. Preserve property and service links. Add purchase/request provenance where required and a database constraint that requires exactly one valid acquisition source. A one-time non-recurring service remains just a `ServiceRequestEntity`.
3. Add an immutable `MonitoringRunEntity` with schedule, unique cycle key, policy version/snapshot, due/start/end, state/outcome, request/visit/report references, next action, and failure/retry information. Unique `(schedule_id, cycle_key)` prevents duplicate runs on concurrent ticks or retry.
4. Add checklist response persistence only if the existing evidence/service metadata cannot capture typed answers and notes. Responses must link to run/visit/checklist item version and actor/time; snapshots retain property-type-specific checklist used.

| Required layer | Plan |
|---|---|
| Entities | Update `MonitoringScheduleEntity`; add proposed `MonitoringRunEntity`; add checklist response entity only if compact versioned JSON cannot support required query/audit needs. |
| Repositories | Extend `MonitoringScheduleRepository` for due/paused/claim operations and locking; add `MonitoringRunRepository` with unique cycle lookup; add checklist repository only if normalized. |
| Services | Add `MonitoringConfigurationService` (admin validation/version activation) and `MonitoringScheduleService` (activate, pause, resume, cancel and acquisition checks). Keep subscription entitlements and request creation behind existing services. |
| Controllers | Add an admin monitoring configuration/schedule controller and customer schedule read/command endpoints with explicit role and property ownership enforcement. Do not expose persistence entities. |
| DTOs | Add configuration create/version DTOs, schedule create/read/pause/resume DTOs, and run/checklist summary DTOs. Validate interval bounds, effective dates, property/service ownership and allowed acquisition mode. |
| Database changes | Add JSONB/version/effective fields only if absent; make subscription FK optional while adding exactly-one source linkage for recurring schedules; add monitoring run table and uniqueness/index on schedule/cycle; checklist response table only if necessary. |
| Reusable components | `ServiceEntity`, subscription plan versions, property type, `MonitoringScheduleEntity`, `audit_log`, `SecurityService`, shared validation/error handling. |
| Migration strategy | Add the next unused Flyway version after the repository’s latest migration (currently V032; verify on branch at execution time). Use additive migrations: expand nullable/source columns, backfill existing schedule rows as subscription-sourced, add run/config tables and indexes, then validate constraints. Never rewrite V013/V018/V019. |
| Risks | Existing schedule rows must be correctly classified during backfill. Invalid or ambiguous legacy frequency strings can break recurrence; seed/validate known values and quarantine unknown values instead of silently defaulting. Scheduler row locking differs by DB and must be proven on PostgreSQL. |

## Phase 2 — Schedule execution and fulfillment reuse

**Goal:** Turn each due recurring schedule into one traceable work cycle, while keeping ordinary one-time requests on the standard service path.

1. Add a scheduler/runner which claims due ACTIVE schedules, checks active purchase or subscription entitlement, skips PAUSED schedules, and writes one `MonitoringRun` before creating work.
2. Generate an ordinary service request through `ServiceRequestService` using the schedule’s property, service and frozen policy version. Bind the request to its monitoring run and avoid charging twice for a subscription-included cycle. One-time single-visit acquisitions use the existing quote → accepted quote → service request path and do not get an extra recurring schedule.
3. Reuse assignment and visit services for internal agent fulfillment. Attach checklist responses, typed evidence, GPS and agent notes to the run and visit; keep configured no-visit services valid.
4. Advance next due time from administrator-configured recurrence only after a durable run outcome. Make retry, failure, missed/no-show and optional reschedule explicit states. Every missed visit must create an administrator notification and append audit history.
5. Support multiple schedules for the same property when service identity differs; support multiple properties and plans without merging their histories.

| Required layer | Plan |
|---|---|
| Entities | `MonitoringScheduleEntity`, `MonitoringRunEntity`, existing `ServiceRequestEntity`, assignment/visit/evidence/GPS entities, optional checklist response entity. |
| Repositories | Schedule repository due query with claim/lock; run repository idempotency lookup; existing service request/assignment/visit/evidence/GPS repositories. |
| Services | Add `MonitoringExecutionService` and scheduler. Call `ServiceRequestService`, assignment/visit/evidence services and subscription entitlement service; do not write those domains through their repositories. Add recurrence calculator isolated behind policy configuration. |
| Controllers | No public run-creation endpoint. Add admin run inspection/retry and schedule management; customer can read schedule/run/visit status. Agent actions continue through existing assignment/visit APIs. |
| DTOs | Due run, run detail, missed visit/reschedule, checklist answer, run failure/retry and customer schedule/history DTOs. Client must not submit trusted execution status, due time or settlement. |
| Database changes | Run state/reference columns and unique cycle key; optional schedule acquisition-source FK; indexes for due ACTIVE schedules and run status; append missed/rescheduled actions to `audit_log`. |
| Reusable components | Existing transactional service request/assignment/visit lifecycle, evidence and GPS service, subscription consumption, notifications, audit_log. |
| Migration strategy | Deploy schema before scheduler code. Initially deploy scheduler disabled/feature-flagged; verify backfill and uniqueness, then enable for selected services. Use idempotent scheduling and database locking; avoid double-running during multi-instance rollout. |
| Risks | Duplicate work or entitlement consumption if run creation, request creation and schedule advancement are not transactionally linked/idempotent. External notification failure must not roll back recorded work; persist an event/outbox or retryable delivery record. Overlapping plans need explicit entitlement and duplicate-service policy. |

## Phase 3 — Property Health, findings and recommendation revenue path

**Goal:** Derive a deterministic, customer-auditable health result from one-time or subscription-backed assessments and convert accepted recommendations into revenue workflows.

1. Implement a lightweight configurable weighted score (0–100) using versioned dimension weights and property-type applicability. Validate all configured weights sum to 100; define how not-applicable and unavailable data are handled. Do not introduce AI/opaque scoring.
2. Create a new immutable `PropertyHealthAssessment` snapshot linked to property, service request and source run(s). Persist policy version, assessed time, total score/category, dimension scores/weights, input provenance and report linkage. Each reassessment creates a new row.
3. Persist findings with description, dimension, source observation/evidence and status/history. Release V1 does not require severity classification.
4. Persist recommendations with description, proposed service, source finding/assessment, customer decision/time and conversion references. On acceptance, create a quote and/or service request through existing pricing/quote/request services; enforce customer ownership and idempotent acceptance/conversion.
5. Health assessments can originate from a one-time Property Health service request or an eligible subscription entitlement. For recurring plan assessments, use the same schedule/run mechanism rather than another scheduler/state machine.

| Required layer | Plan |
|---|---|
| Entities | Add `PropertyHealthAssessmentEntity` (immutable snapshot), `HealthFindingEntity`, `HealthRecommendationEntity`. Store dimension score/weight snapshots in assessment JSONB unless query/report requirements justify normalized dimension rows. Severity is excluded from V1 required fields. |
| Repositories | Add assessment query by property/customer/time; finding/recommendation repositories with source and status lookups; unique constraints for recommendation conversion and request/quote references where appropriate. |
| Services | Add `PropertyHealthAssessmentService` and deterministic `HealthScoringService`; reuse `ReportService`, `PricingEngineService`, `QuoteService`, `ServiceRequestService`, `SecurityService`, and entitlement consumption. Add a recommendation acceptance service transaction with idempotency. |
| Controllers | Add customer-owned read/history and recommendation accept/decline endpoints; add operations/admin assessment creation/review/configuration endpoints. A customer must not submit score values or mark evidence accepted. |
| DTOs | Assessment summary/history, dimension score, finding, recommendation, decision request/response, report detail and configuration version DTOs. Keep public response separate from internal scoring provenance as appropriate. |
| Database changes | Add immutable assessment table; findings and recommendation tables; score/config snapshot JSONB; source links to property/request/run/report/evidence; conversion links to quote/request; append-only decision/history events in `audit_log`. |
| Reusable components | Existing service request/service catalog for one-time purchase; subscription plan/version/entitlements for recurring assessments; MonitoringRun, evidence, report, quote and request services. |
| Migration strategy | Add tables without changing existing health-unaware service requests. Seed no fabricated historical scores. Make new FK relationships nullable only where one-time/manual health review legitimately lacks a monitoring run; require property and source request/assessment provenance. |
| Risks | Scores become untrustworthy if configuration edits affect old records or evidence is unverified. Recommendation acceptance can duplicate paid work unless quote/request conversion is idempotent. Pricing quote generation currently has operations/admin restrictions that must be safely resolved before customer-driven conversion. |

## Phase 4 — Alerts, reports, customer and operations visibility

**Goal:** Deliver auditable customer value and operational control on top of persisted run and assessment results.

1. Generate/associate `MonitoringAlertEntity` from configured findings or run outcomes. Support customer/admin acknowledgement and resolution; write each state change to append-only `audit_log`. Severity fields remain optional for V1 because the final business decisions do not require finding severity.
2. Generate structured Monitoring/Health reports from immutable run, checklist, evidence, finding, score and recommendation snapshots. Reuse `ReportService` submission, approval and delivery states; report changes must not mutate the assessment snapshot.
3. Add notifications for visit scheduled/completed, missed visit to administrators, health score updated, recommendation generated and any configured critical/priority alert. Persist delivery attempts and retry; notification provider outage must not lose run/assessment state.
4. Expose customer history per authorized property: active plans/schedules, prior runs/visits, reports, evidence, assessments, findings, recommendations, alerts and decisions. Add operations view for due/overdue/failed cycles and plan utilization.

| Required layer | Plan |
|---|---|
| Entities | Reuse `MonitoringAlertEntity`, `ReportEntity`, notification entities and immutable assessment/run entities. Add alert transition table only if `audit_log` cannot provide stable queryable history. |
| Repositories | Extend alert queries by authorized property/status/date; report queries by request/property; notification attempt/delivery queries; paginated schedule/run/assessment reporting repositories. |
| Services | Add/extend `MonitoringAlertService`, `MonitoringReportService` or a cohesive monitoring application service; extend notification trigger/delivery and reporting services. Reuse `ReportService` lifecycle. |
| Controllers | Add customer alert/report/history routes, admin alert acknowledgement/resolution and operations run/SLA/overdue dashboards, all with customer/property ownership checks and role policies. |
| DTOs | Alert history/detail/action, report summary, property monitoring timeline, health timeline, operations queue and notification delivery status DTOs. |
| Database changes | Prefer append-only `audit_log` entries for alert state transitions; add only missing indexes and notification delivery-attempt fields/tables. Add indexes on property + event time and status + due time where query plans justify them. |
| Reusable components | Existing reports, alert records, notification templates, operations dashboard/controller, customer access helpers and `audit_log`. |
| Migration strategy | Additive index/table migrations after workflow schema. Use bounded page queries and verify query plans against representative data; no destructive retention cleanup for required histories. |
| Risks | Notification channels may be persisted without actual delivery adapters; dashboards can mislead if they count schedules but do not expose failed execution. Report URLs/evidence must use controlled storage and resource-scoped reads. |

## Phase 5 — Release verification and operational activation

**Goal:** Demonstrate correctness across PostgreSQL, concurrent schedules, customer isolation and payment/entitlement boundaries before turning on paid plans.

| Required layer | Plan |
|---|---|
| Entities | No new business entities unless tests expose a missing persisted state; use production-mapped schema in integration tests. |
| Repositories | Verify all new query/locking constraints with PostgreSQL and Flyway, not only mocks. |
| Services | Add tests for interval computation, paused schedules, missed/rescheduled visits, one-time and subscription acquisition, multiple services/properties/plans, evidence/GPS policy, weighted-score boundaries, immutable reassessment, and accepted recommendation conversion. |
| Controllers | Exercise unauthorized customer/property access, agent access, admin-only configuration and customer recommendation actions through HTTP security filters. |
| DTOs | Validate malformed intervals/weights/checklist answers, missing required evidence, oversized notes, invalid decisions and unsupported status transitions. |
| Database changes | No planned change. Capture query plans and migration/restore evidence for the final schema. |
| Reusable components | Existing backend tests plus PostgreSQL integration infrastructure, Actuator health, audit log, report and notification components. |
| Migration strategy | Test fresh installation and upgrade from the current supported schema; verify migrate/rollback approach (Flyway rollback may require forward repair), backup/restore and multi-instance scheduler locking. Enable schedules by service after acceptance. |
| Risks | Unit-only success will miss FK, locking, transaction and data migration errors. Enabling all schedules at once can create an operational burst; activate by configured service/cohort and monitor due backlog/failures. |

### Release acceptance gates

- One-time Monitoring and subscription Monitoring both create correct, separately traceable work; Health can be purchased one-time or through subscription entitlement.
- Administrators configure and version frequencies/checklists/weights without hardcoded interval logic; changes do not alter prior run or assessment records.
- Paused schedules generate no new activity. Missed visits preserve audit history, notify administrators and follow configured reschedule behavior.
- Multiple monitoring services can be active for one property and remain independent. Multi-property customers and multiple active plans are supported without duplicate run/entitlement use.
- Reports show property, visits, evidence summary, findings, Health score and recommendations. Assessment histories are append-only and weighted scores are reproducible.
- Accepted recommendations produce an idempotent quote and/or service request associated with the recommendation and source assessment.
- Customer, agent, operations and admin access is isolated; schedule runner, notifications and migrations pass production-like tests.

## Migration and deployment strategy summary

1. Confirm the deployed Flyway schema/version before planning actual version numbers; repository currently contains migrations through `V032` plus earlier `V20`–`V29` naming. Do not infer the production schema from filenames alone.
2. Keep all changes additive and append new migrations. Never edit existing applied migrations. Use expand → backfill → validate/constraint ordering for schedule source changes.
3. Prefer existing versioned subscription/service JSON only where it can be validated and snapshotted. Otherwise add a narrow config snapshot field/table, not a second generic catalog.
4. Add only durable records required for idempotent run execution and immutable health/finding/recommendation outcomes. Reuse `audit_log` for append-only schedule, alert and recommendation decisions when query needs permit.
5. Validate fresh install and upgrade paths against PostgreSQL with Hibernate `ddl-auto: validate`; run scheduler disabled until migration/backfill and run idempotency are confirmed.
6. Back up before production migration; rehearse restore and deployment rollback. For irreversibly applied schema changes, roll forward with a repair migration rather than assuming a Flyway down migration.

## Recommended build order

1. Contract/configuration mapping and entitlement/acquisition rules.
2. Versioned config and schedule source support for one-time vs subscription.
3. Monitoring run ledger, uniqueness and due-schedule repository locking.
4. Pause/resume, configurable recurrence, due-cycle scheduler and standard request generation.
5. Checklist capture on existing visit/evidence flow; missed visit handling, admin notification and audit.
6. Immutable weighted Health assessment, findings and recommendations.
7. Recommendation accept → quote/request conversion with ownership and idempotency.
8. Monitoring/Health reports, alerts, customer history and operations views.
9. PostgreSQL, security, concurrency, scheduler, notification, recovery and end-to-end release gates.

This sequence places durable lifecycle state before automation, reuses PropertyPilot’s established service fulfillment model, and establishes the recommendation-to-revenue path without making Monitoring dependent on subscription enrollment.
