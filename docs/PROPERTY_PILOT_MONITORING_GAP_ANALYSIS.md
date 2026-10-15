# PropertyPilot Monitoring and Property Health Gap Analysis

**Audit date:** 2026-10-06 (updated for final business decisions)  
**Scope:** Read-only comparison of the current repository against the supplied `PROPERTY_PILOT_MONITORING_AND_HEALTH_TRUTH.md` (including subsequent final decisions), `docs/Business/PROPERTY_PILOT_BUSINESS_TRUTH.md`, and `PROPERTY_PILOT_ARCHITECTURE_TRUTH.md`. The Monitoring and Health Truth was supplied as an IDE attachment; its contents and subsequent final decisions were read for this analysis. No code was changed.

## Executive summary

Monitoring and Property Health are core PropertyPilot products in the supplied truth documents. The codebase has useful generic fulfillment primitives and two Monitoring persistence entities, but neither product currently has an end-to-end workflow.

The most reusable path is:

**One-time request or active monitoring plan → configured monitoring service → scheduled visit/run → evidence and agent observations → report → health assessment → findings/recommendations/alerts → customer communication → accepted recommendation → quote and service request.**

The current repository does not implement the orchestration around that path. `MonitoringScheduleEntity` and `MonitoringAlertEntity` are the only Monitoring domain entities found. Their repositories are used for dashboard counts, not for creating or advancing schedules/alerts. No health assessment, health dimension, finding, score, or recommendation entity/service/controller was found. Existing visit/evidence/report/request facilities can carry much of the execution, but Monitoring run identity/history and Property Health’s immutable assessment history need explicit persistence.

This gap analysis does not propose a separate platform or a duplicate fulfillment system. Keep Monitoring and Health as PropertyPilot services using the service catalog and request model, with minimal domain records for recurring schedule/run state and immutable health results.

## 1. Existing capabilities

| Capability | What exists in code | Current limit against Monitoring and Health Truth |
|---|---|---|
| Service configuration | `ServiceEntity` and `ServiceCatalogConfigurationServiceImpl` expose billable, visit, GPS, photo, signature, document and remote-service flags. | No Monitoring plan/checklist/report/notification configuration or property-type-specific rules are represented as an executable ruleset. Evidence completion maps hardcoded service flags to fixed codes. |
| Subscription plans and entitlements | Subscription plans, plan versions, customer subscriptions, benefits and entitlement-consumption services/controllers/entities are present. `CustomerSubscriptionServiceImpl` creates customer subscriptions. | Plan configuration does not generate monitoring schedules or visits. Plan benefits are not a demonstrated Monitoring plan with versioned frequency/checklist/report/notification rules. Auto-renew currently has separate payment-integrity risks; do not rely on it for Monitoring entitlement until corrected. |
| Monitoring schedule persistence | `MonitoringScheduleEntity` stores customer subscription, property, service, frequency, next due time, status and last-generated timestamp. Migration `V013__create_subscriptions.sql` creates the table and due index. | No application service, controller or scheduler creates schedules, finds and processes due rows, generates work, advances next due time or records run failures. The mandatory `customerSubscription` prevents direct one-time schedule linkage. Frequency is free-form text, but no administrator configuration or calculation supports custom intervals. |
| Monitoring alert persistence | `MonitoringAlertEntity` stores property, optional schedule/request/report/recommended service, acknowledged user, type, severity, title/description, detected time and acknowledgement/resolution timestamps/status. Migrations `V018` and `V019` create/extend the table. | No application flow creates, acknowledges or resolves alerts. No alert history/transition API or notification delivery is connected. Alert persistence is not the same as alert generation or management. |
| Operations monitoring dashboard | `OperationsReportingServiceImpl.getMonitoringDashboard` counts active schedules, due schedules, unresolved alerts and critical unresolved alerts. `AdminOperationsController` exposes it. | It reads aggregates only. It provides no run history, overdue age, execution outcome, property/customer drilldown or schedule controls. Empty/dormant tables can produce plausible zero metrics. |
| Service request | `ServiceRequestEntity` and `ServiceRequestServiceImpl` link accepted quote, customer, property, service, status, assigned agent and request status history. Eligibility and property/customer consistency checks exist. | No Monitoring scheduler or alert calls the service request workflow. No Monitoring schedule/run linkage is persisted on a generated request. |
| Assignment and visit | Assignment and visit services support lifecycle operations and multiple visits; visit behavior checks service visit/remote configuration and completion hooks. | No Monitoring run creates a visit/request. A monitoring visit is not tied to a plan cycle or checklist version. |
| Evidence and GPS | Evidence records link to request and/or visit, store type, URL, media type, checksum and capture time. GPS records capture coordinates, distance and a verified/rejected result. | No agent notes/customer inputs checklist model, review/acceptance lifecycle, or Monitoring-specific evidence summary. The evidence URL is supplied by the API caller; GPS coordinates are also caller supplied, limiting evidentiary trust. |
| Reports | `ReportServiceImpl` creates DRAFT reports against requests and permits submit/approve/deliver transitions. `ReportEntity` stores type, summary and document URL. | Report is generic text/document metadata, not a generated Monitoring report with checklist responses, evidence index, findings, health score and recommendations. No report is generated from a Monitoring run. |
| Notifications | Notification and template persistence/services exist; subscription notification reads and subscription schedulers exist. | No Monitoring event path creates customer notification records for scheduled/completed visits, score changes or critical alerts; outbound channel delivery/retry was not found as a Monitoring workflow. |
| Property Health | Generic service catalog, request, subscription and report foundations could host one-time or subscription-based assessments. | No health score calculation, 0–100 weighted model, configurable dimensions/weights, property-type applicability, findings, recommendations or immutable assessment history was found. |
| History/audit | Shared auditable base entity and service request status history exist. Monitoring schedule/alert entities inherit audit metadata and use optimistic version fields. | No immutable per-run history or health-assessment history. Mutable current schedule/alert rows do not alone satisfy “history must never be deleted”; no Monitoring/Health transition audit path exists. |

## 2. Missing capabilities

### Monitoring plan and schedule

1. **Acquisition and plan definition/versioning:** Monitoring must support individual service requests and subscription plans; Property Health must support one-time and subscription-based service. No plan/configuration unifies frequency, included services, evidence/checklist requirements, reporting and notifications. Subscription plan versions and benefits JSON are reusable, but are not a Monitoring plan contract.
2. **One-time activation:** Schedule requires a `CustomerSubscriptionEntity`. No code creates a monitoring schedule directly from a one-time accepted request or represents its purchased recurrence/visit scope without a subscription. One-time, non-recurring work can reuse the ordinary request/visit flow.
3. **Frequency administration:** Frequency is stored as free-form text, but no administrator configuration or recurrence calculation supports custom frequencies such as every 15 or 45 days. A text column alone does not provide configurable behavior.
4. **Multiple property/plan/service orchestration:** The data model can hold multiple schedules, but no service validates multi-property customers, multiple active plans, independent simultaneous services (for example agricultural, water, security and boundary monitoring), duplicate schedules or property/service entitlements.
5. **Due cycle generation:** No Monitoring scheduler queries due schedules, creates an idempotent run, advances due dates, handles missed visits, notifies administrators or records failed/retried runs. Existing `Subscription*Scheduler` classes are not Monitoring schedulers.
6. **Pause and resume:** A status string could represent pause, but no lifecycle API or scheduler gate enforces pause/resume, preserves history or prevents new activity generation while paused.
7. **Monitoring visit workflow:** No cycle-to-request/visit linkage, visit scheduling, rescheduling/no-show outcome or completion policy exists. Existing service request and visit services are reusable.
8. **Configurable checklist:** No property-type-specific checklist definition/version, checklist response, agent/customer notes or required-answer validation exists.
9. **Run idempotency and immutable history:** `lastGeneratedAt` is a timestamp only. There is no durable unique cycle key/run record or immutable observation history to prevent duplicate work on retry and reconstruct past execution.

### Evidence, reports, health, alerts and recommendations

8. **Monitoring evidence summary:** Evidence table/API can hold common artifacts, but there is no type/count rule resolution by monitoring plan/checklist or run-level evidence manifest.
9. **Health assessment composition:** No assessment object links one or more monitoring observations/evidence/reports to a point-in-time health result.
10. **Score calculation:** No dimension values, configurable weights, score range validation, category labels, missing-data treatment or versioned scoring policy (0–100; Excellent/Good/Fair/Poor/Critical) exists.
11. **Property-type dimensions:** No mechanism defines dimensions applicable to residential, apartment, plot, agricultural, commercial or industrial property types.
12. **Finding lifecycle:** No finding records with dimension, evidence, observation source, status, actor, remediation state or history were found. Severity is explicitly not required for Release V1 and is not a launch blocker.
13. **Recommendation lifecycle and revenue conversion:** No recommendation records, reason/provenance, customer acceptance state, quote creation or request conversion exist. `MonitoringAlertEntity.recommendedService` is only a nullable service reference. Accepted recommendations must generate a quote and/or service request while preserving source finding/assessment linkage.
14. **Alert generation and handling:** Alert entity fields exist, but no rule evaluates findings into alerts; no customer access/listing, acknowledge, resolve, escalation or alert history flow exists.
15. **Customer notifications:** No event-to-template/channel path is connected to monitoring schedule/visit completion, score changes, critical findings or recommendations.
16. **Monitoring/Health report:** Generic report lifecycle exists but no report creation path aggregates property, plan/run, checklist, evidence, findings, score and recommendations; there is no auditable versioned report snapshot shown.
17. **Immutable tracking:** Neither schedule/alert current state nor generic report status supplies immutable visit/evidence/score/recommendation/alert history. A history-preserving event model or append-only result records are required.

## 3. Reusable components

Reuse these existing pieces to avoid a second work-order system:

- **Service catalog and configuration:** `ServiceEntity`, service configuration DTOs and `ServiceCatalogConfigurationServiceImpl` should identify Monitoring and Property Health offerings, chargeability, visit/GPS/evidence requirements and service eligibility. Extend configuration only for policy missing from current fields.
- **Subscriptions and entitlements:** Existing plan/version/subscription/benefit and consumption structures can authorize frequency/visit/service entitlements. Add a clear mapping from active plan version to Monitoring configuration; avoid duplicating subscription ownership. Subscription payment/renewal safeguards are prerequisite to paid recurring activation.
- **MonitoringScheduleEntity:** Reuse as the current schedule aggregate/root for property + service + subscription + frequency + due state. Add only fields or related records needed to freeze the plan version and link cycles/work. Its required subscription association needs an explicit decision if single-purchase monitoring is permitted.
- **ServiceRequestService and ServiceRequestEntity:** Use these for PropertyPilot-owned work, status history, customer/property/service association and operational assignment. A due monitoring cycle should create an ordinary internal service request under a unique cycle/run reference.
- **Assignment, Visit and GPS services:** Reuse for agent ownership, scheduled on-site inspection, multi-visit behavior and configurable location checks. Use an explicit monitoring checklist/visit result rather than creating a parallel visit model.
- **EvidenceService and EvidenceEntity:** Reuse evidence associations and common media metadata after establishing a controlled storage/access boundary and run/checklist linkage. Existing arbitrary URL storage is not adequate evidence custody.
- **ReportService and ReportEntity:** Reuse report draft/submit/approve/deliver lifecycle. Add a structured result snapshot or report sections so a Monitoring report is derived from run/checklist/evidence/finding/assessment data and is reproducible.
- **MonitoringAlertEntity:** Reuse for current actionable alert state and links to schedule, request, report and recommended service. Add an append-only transition/history mechanism if immutable alert history is required.
- **Notification entities/templates:** Reuse configured templates and existing delivery boundaries if implemented; add event triggers for Monitoring and Health milestones.
- **Admin operations reporting:** Extend the current dashboard with due/overdue runs and execution outcomes after actual run processing exists. Do not mistake dashboard counters for workflow implementation.

## 4. Recommended minimal architecture

Keep the existing modular Spring application. Add a small Monitoring application domain and a Property Health assessment capability composed through the existing service-request workflow.

### Monitoring domain

- Treat the existing `MonitoringScheduleEntity` as the active recurring schedule for a property and independent monitoring service. Associate it with a subscription when subscription-acquired, and support explicit one-time purchase linkage when individually acquired. One-time non-recurring Monitoring uses the ordinary service request/visit flow without an artificial recurring schedule.
- Add a **versioned monitoring plan/configuration** only if existing subscription plan version/benefits and service configuration cannot represent configurable frequency, checklist, evidence, report and notification rules. Keep the plan tied to PropertyPilot services and plan versions, not a vendor/marketplace concept.
- Add an append-only **MonitoringRun** (or equivalent durable cycle record) with schedule, cycle key, plan/configuration version, due/execution timestamps, status, generated request/visit reference, outcome and next-due decision. Enforce uniqueness on schedule + cycle key so retries do not duplicate a visit or charge.
- Have a scheduler claim due schedules transactionally or with locking, create one run, then create a service request through `ServiceRequestService`; assignment/visit/evidence/report continue through canonical existing workflows. Advance the schedule only with durable run state, and record failures/retries.
- Persist configurable checklist definitions and versioned checklist responses, either as compact configuration/JSON plus typed answer records or a small normalized set if querying reports requires it. Must include property-type applicability, evidence requirements, notes, actor/time and run/visit linkage.
- Keep `MonitoringAlertEntity` as a current-state alert record, link it to the run/finding/property, and append alert transitions for immutable history. Generate alerts from configured finding/threshold rules; use existing service request linking for remediation opportunities.

### Property Health capability

- Model health as a **point-in-time assessment** linked to property and source monitoring run(s)/service request(s), not as a separate customer booking or fulfillment silo.
- Persist immutable assessment snapshot: scoring-policy version, assessment time, overall 0–100 weighted score/category, dimension values/weights, source observations, findings and recommendations. Configurable policy selects dimensions/weights by property type and validates a 100% total. Keep V1 scoring deterministic and simple; AI scoring is out of scope.
- Persist findings/recommendations with dimension, evidence/source references, status/history and optional recommended service. Severity is not required for V1. Recommendation acceptance must lead to a quote and/or ordinary service request, linked back to recommendation and assessment.
- Generate a structured `ReportEntity` through the existing report lifecycle using the immutable assessment snapshot. A changed score creates a new assessment; do not overwrite a prior score.
- Expose customer-owned property history through access checks in `SecurityService`; expose administration for versioned plan, checklist and scoring configuration with audit metadata.

### Minimal schema direction

Current schema already covers property, service, subscription, schedule, alert, request, visit, evidence, report and entitlements. Prefer small additive migrations over parallel domain tables. Schema changes are expected for one-time purchase linkage, durable MonitoringRun/cycle idempotency, configurable frequency/checklist response if not safely representable with existing configuration, health assessment snapshots/dimensions, findings/recommendations and immutable alert/history transitions. First validate whether plan version JSON and service configuration can hold policy definitions before adding a standalone plan table. Retain immutable history rather than updating prior assessments in place. Severity is optional and not a V1 acceptance requirement.

## 5. Recommended implementation sequence

The sequence orders prerequisites and minimizes duplicate work. Each phase should add tests for state/authorization/idempotency with the implementation.

### Phase 0 — Resolve product and policy decisions

1. Model the now-settled acquisition modes explicitly: one-time Monitoring request or subscription plan; one-time Property Health assessment or subscription-based service.
2. Define how administrator-configured frequency maps to requests/visits and what constitutes cycle completion, pause/resume, missed visit, optional rescheduling, administrator notification and cancellation.
3. Define property-type checklist variants, evidence/GPS rules, customer notification events, health dimensions/weights, missing-data treatment and recommendation acceptance/conversion. Do not require finding severity for V1.
4. Define subscription-cycle entitlement consumption and verify active paid/authorized entitlement before subscription schedule activation; one-time purchases use the ordinary quote/payment path.

### Phase 1 — Versioned configuration and persistence

1. Map current `ServiceEntity`, subscription plan version and benefits JSON to the required configuration; add only missing versioned configuration fields/tables.
2. Add MonitoringRun/cycle persistence with uniqueness/idempotency and link to existing schedule, request and visit.
3. Add property-type checklist definitions and responses with run and evidence associations.
4. Add health assessment snapshot, dimension results, findings and recommendations; add alert transition/history persistence.
5. Add Flyway migrations and verify entity mappings/FKs on PostgreSQL before adding scheduler behavior.

### Phase 2 — Monitoring plan activation and due-cycle execution

1. Add activation/pause/resume/cancel operations from an eligible subscription plan or one-time purchase, supporting multi-property, multiple active plans and independent concurrent monitoring services.
2. Add administrator-configured recurrence rules and a scheduler/runner that skips paused schedules, claims each cycle once, persists run state, creates the ordinary service request and advances next due only with a durable outcome.
3. Add missed-visit outcomes: preserve prior visit/run audit, optionally reschedule under configured policy and notify administrators.
4. Add retry/failure handling and tests for concurrent scheduler invocation, duplicate cycle prevention, custom-frequency/time-zone edges, pause/resume and entitlement exhaustion.
5. Route assignment/visit/agent completion through existing fulfillment services; capture checklist responses, notes, configured evidence and GPS.

### Phase 3 — Health assessment, findings, alerts and reports

1. Calculate health dimensions and a simple weighted total from versioned rules and accepted run evidence/findings.
2. Persist an immutable snapshot for every one-time or subscription-based assessment; validate score bands and configurable weights.
3. Generate findings, recommendations and applicable alerts; severity is optional in V1. Support recommendation acceptance with source assessment/finding linkage.
4. Create a structured Monitoring/Health report with property, plan/run, visit, checklist, evidence, score, findings and recommendations; use existing report approval/delivery lifecycle.
5. Convert accepted recommendations into a quote and/or service request through the normal revenue flow, retaining traceability to the source assessment/finding.

### Phase 4 — Customer, admin and operations visibility

1. Add authorized customer views for active plans, next/previous visits, run history, evidence, reports, current and historical scores, findings, recommendations and alerts.
2. Add admin controls for plans, schedules, checklist/scoring versions, alert thresholds, and recovery/retry of failed runs; record actor and reason.
3. Extend operations reports with due/overdue runs, failures, cycle completion, critical alerts and plan utilization using bounded/paginated queries.
4. Connect the existing notification template/channel boundary for scheduled/completed visits, score updates, recommendations and critical alerts, with retry and delivery status.

### Phase 5 — Launch verification

1. Test supported residential, apartment, open plot, agricultural, commercial and industrial property profiles against versioned checklists and health dimensions.
2. Test multi-property customers, multiple active plans, overlapping schedules, missed/paused cycles, idempotent retry, entitlement exhaustion, and duplicate request prevention.
3. Test evidence/GPS authorization, approved evidence rules, alert lifecycle, immutable score/history and customer access isolation.
4. Run end-to-end PostgreSQL-backed tests from plan activation through visit, evidence, report, assessment, customer alert and optional follow-up request.
5. Validate production scheduling timezone/locking, scheduler observability, notification retry and backup/restore of monitoring/health history.

## Audit conclusion

Current status: **persistence foundations exist; Monitoring and Property Health business workflows are not implemented.** Both one-time and subscription acquisition modes are required, as is recommendation acceptance → quote/request conversion. The highest-value reuse is the canonical service-request → assignment → visit → evidence → report → quote/request path, plus existing subscription/service configuration and schedule/alert records. The minimum material additions are one-time/subscription linkage, administrator-configured recurrence, durable cycle execution with pause/missed-visit handling, configurable checklist responses, immutable weighted assessments/findings/recommendations, and secure customer/admin operations.

The architecture should remain PropertyPilot-owned and service-centric: recurring Monitoring schedules generate internal service work; agents fulfill it; Property Health consumes verified observations and produces auditable customer reports. There is no need for marketplace functionality or a second visit/request lifecycle.
