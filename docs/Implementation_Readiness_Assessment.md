# PropertyPilot Implementation Readiness Assessment

## Purpose

This assessment evaluates whether the current PropertyPilot documentation and repository baseline are ready to support a controlled implementation. It measures evidence of an implementable, testable, and operable product—not the quantity of documents.

**Assessment date:** 2026-08-31  
**Overall readiness:** **46 / 100 — not ready to begin broad parallel development**

The documentation establishes substantial product intent and governance. However, canonical contracts, executable data design, delivery automation, and several release-critical decisions remain incomplete or inconsistent. A small, tightly controlled foundation phase may begin only after the “Must Fix Before Development” items are closed.

## Scoring Method

| Score | Meaning |
|---|---|
| 0–24 | Not ready; critical foundations are absent. |
| 25–49 | Partially defined; implementation would create material rework or uncontrolled risk. |
| 50–69 | Conditionally ready; bounded implementation can proceed with tracked blockers. |
| 70–84 | Ready for a controlled beta, subject to validation evidence. |
| 85–100 | Production ready; controls are implemented, tested, and operating. |

Scores reflect documentation completeness, internal consistency, executable specifications and assets, testability, and operating evidence. They do not certify compliance, security, or production suitability.

## Readiness Summary

| Area | Score | Readiness | Primary evidence | Key constraint |
|---|---:|---|---|---|
| Product Readiness | 68 | Conditionally ready | SRS, feature/service catalogues, journeys, screen catalogue/flows, MVP plan, state/event/notification catalogues | Several capability, workflow, and canonical-state gaps remain. |
| Architecture Readiness | 61 | Conditionally ready | Technical Architecture, domain boundaries, integration, event, observability, multi-tenancy, and architecture governance documents | Reference architecture is descriptive; deployment topology and implementation decisions are not yet executable. |
| API Readiness | 34 | Not ready | API Catalog, API versioning policy, OpenAPI specification | Canonical OpenAPI covers only a small subset and conflicts with required base path and model naming. |
| Database Readiness | 43 | Partially defined | Physical model, canonical dictionary, domain models, six SQL migrations | Many named tables have no physical fields; migrations materially diverge from the physical model. |
| Security Readiness | 59 | Conditionally ready | Security Design, IAM, Cross-Cutting Requirements, Security Controls Catalog | Controls are specified, but threat-model evidence, configuration, testing, and operational enforcement are absent. |
| DevOps Readiness | 28 | Not ready | DevOps Architecture, environment/release/capacity plans | Infrastructure and service directories are placeholders; no executable CI/CD or IaC is present. |
| Operations Readiness | 52 | Partially defined | Operational Runbooks, Platform Operations, DR/BCP, SLA, notifications | Runbooks exist, but ownership, tooling, SLOs, alert configuration, and recovery exercises are not evidenced. |

## Area Assessments

### Product Readiness — 68 / 100

**Strengths**

- The product is well described through the [SRS](PropertyPilot_SRS.md), feature and service catalogues, customer journeys, and UI artefacts.
- Lifecycle governance is materially improved by the [State Model Catalog](State_Model_Catalog.md), [Event Catalog](Event_Catalog.md), [Notification Catalog](Notification_Catalog.md), and [Cross-Cutting Requirements](Cross_Cutting_Requirements.md).
- MVP scope and prioritised backlog artefacts exist.

**Readiness gaps**

- The traceability review identifies missing product scope for subscription pause/cancellation/auto-renewal controls, construction project execution, and privacy rights/consent.
- Required workflows remain incomplete for payment disputes, collections/grace handling, vendor performance/onboarding, privacy requests, and construction milestones.
- The SRS contains stale source references, including a statement that the pricing strategy is absent despite the current document existing.
- Several duplicate or legacy lifecycle definitions can drive different implementations unless the State Model Catalog is enforced as canonical.

**Conclusion:** Begin only the agreed MVP foundation after consolidating canonical requirements. Do not start all catalogued domains in parallel.

### Architecture Readiness — 61 / 100

**Strengths**

- The architecture set covers service boundaries, security, integration, events, platform operations, observability, data, and multi-tenancy at a conceptual level.
- Governance, ADR, standards, and cross-cutting rules establish useful controls for implementation decisions.

**Readiness gaps**

- The architecture does not yet yield a single executable deployment/reference topology with selected cloud services, network boundaries, runtime sizing, failure domains, and environment-specific configurations.
- Domain ownership and assignment modelling are duplicated across `agent_assignments`, `service_assignments`, and `vendor_assignments`; one canonical aggregate and transition model is required.
- The documented asynchronous architecture lacks final broker, outbox, schema-registry, dead-letter, replay, and observability implementation decisions.
- Non-functional requirements need measurable service-level objectives, load profiles, capacity assumptions, and acceptance thresholds mapped to MVP services.

**Conclusion:** A foundation architecture can be implemented after key ADRs are approved. Production architecture is not ready.

### API Readiness — 34 / 100

**Strengths**

- The API Catalog is broad, and the [API Versioning and Deprecation Policy](API_Versioning_and_Deprecation_Policy.md) now defines compatibility and lifecycle governance.
- Cross-cutting error, pagination, idempotency, webhook, and security rules are documented.

**Readiness gaps**

- The canonical OpenAPI file defines only customers, properties, service requests, and reports; it lacks complete operations, security schemes, reusable errors, pagination, request/response coverage, callbacks/webhooks, and the majority of catalogued APIs.
- The OpenAPI server uses `/v1`, while Cross-Cutting Requirements mandate `/api/v1`; API Catalog examples also omit the canonical prefix.
- The OpenAPI and physical-model/API naming differ, for example `service_request_id`/`request_type` versus `request_id`/`service_id`.
- The Traceability Matrix identifies absent contracts for KYC administration, subscription changes, payment webhooks/refunds, offline sync, project lifecycle, and privacy rights.
- No implementation or CI evidence exists for OpenAPI linting, compatibility checks, provider tests, consumer contract tests, or generated SDKs.

**Conclusion:** API implementation must be limited to contracts completed and approved in OpenAPI. Broad API development must not start until the v1 contract baseline is reconciled.

### Database Readiness — 43 / 100

**Strengths**

- The physical model, domain data models, data governance material, and [Canonical Data Dictionary](Canonical_Data_Dictionary.md) give meaningful domain context.
- Initial SQL migrations cover customer, property, service request, visit, evidence, and report foundations.

**Readiness gaps**

- The physical model names key tables—subscription, payment, invoice, refund, vendor, quotation, complaint, and notification—but does not define their fields.
- Required persistence is still absent for `tasks`, service-request cancellations, and vendor invoice/settlement linkage.
- The existing SQL migrations diverge from the physical model: customer/account shape, service-request identifier and status vocabulary, assignment representation, and audit conventions differ.
- Common audit fields lack fully specified physical types, nullability, defaults, and migration enforcement; the dictionary correctly records this as unspecified.
- There is no evidence of migration runner configuration, schema validation, seed/reference data, rollback policy, performance indexing tests, backup/restore test, or data retention execution.

**Conclusion:** Implement only after selecting the physical model and migration set as one reconciled baseline. The current SQL scripts must not be treated as production schema authority.

### Security Readiness — 59 / 100

**Strengths**

- Security requirements are extensive: Zero Trust, IAM, encryption, secrets, key rotation, PII handling, audit logging, abuse protection, webhook security, GDPR principles, backup protection, and incident response are covered.
- Cross-cutting rules provide a conflict-resolution authority, data classifications, retention controls, and tenant/property access principles.

**Readiness gaps**

- No threat model, data-flow threat review, risk acceptance register, or security architecture review is evidenced for the MVP flows and third-party providers.
- No implementation evidence exists for a secrets manager, key-management configuration, token/OTP handling, WAF/rate limits, audit sink immutability, field-level encryption/tokenisation, or webhook signature verification.
- Privacy obligations exist, but consent, export, deletion, legal-hold, and breach-notification interfaces/data stores are not fully designed end-to-end.
- No SAST, dependency scan, DAST, penetration-test plan execution, or production security-monitoring evidence is present.

**Conclusion:** Security design is credible but unproven. It is sufficient for guarded foundation development after threat modelling; it is insufficient for beta with real personal or payment data.

### DevOps Readiness — 28 / 100

**Strengths**

- DevOps, environment, capacity, release-management, and testing documents define intended practices.

**Readiness gaps**

- `backend`, `infrastructure`, and `admin-portal` contain README placeholders rather than service code, containers, deployment manifests, infrastructure-as-code, or pipeline definitions.
- No source control protections, CI workflows, image registry configuration, artifact provenance, environment promotion, secret injection, or deployment rollback automation is evidenced.
- Environment catalogue ownership/values remain `TBD` for DEV through DR.
- No observability implementation, log/metric/trace standards enforcement, alert routing, dashboards, SLO burn alerts, or release-quality gates is present.

**Conclusion:** DevOps is a blocking foundation. Establish the delivery platform before multi-team feature development.

### Operations Readiness — 52 / 100

**Strengths**

- Runbooks cover incidents, service/API/database failures, backup recovery, DR, monitoring, security incidents, and business continuity.
- Operations, SLA, capacity, notification, and support documentation provide an operating model.

**Readiness gaps**

- Runbooks are procedural documentation only; no evidence exists of configured on-call ownership, incident tooling, escalation rosters, alert integration, ticket workflow, or service dashboards.
- RTO/RPO, SLO/error-budget targets, backup frequency, and recovery acceptance tests need a single approved operational baseline.
- No completed restoration, failover, load, or disaster-recovery exercise is recorded.
- Support and field operations require production-ready queues, permissions, training, reconciliation, and audit evidence before external beta.

**Conclusion:** Operational design can support internal development, but not a real-customer beta without tooling and exercises.

## Must Fix Before Development

These are prerequisites for starting shared implementation work. Until closed, parallel delivery will produce incompatible code and migration rework.

| ID | Priority | Required outcome | Primary owner | Evidence of completion |
|---|---|---|---|---|
| DEV-01 | P0 | Approve a single MVP scope, domain sequence, and canonical requirement hierarchy; remove or map stale/legacy lifecycle rules. | Product Owner / Architecture Lead | Signed MVP scope; State Model and Cross-Cutting rules referenced from SRS/backlog. |
| DEV-02 | P0 | Resolve the six traceability conflicts: API base path, authentication scope, property/listing semantics, pricing distance source, service status vocabulary, and subscription status vocabulary. | Architecture Lead | Updated authoritative artefacts and decision records. |
| DEV-03 | P0 | Establish a complete, reviewed `/api/v1` OpenAPI baseline for the first implementation slice, including errors, auth, pagination, idempotency, and state transitions. | API Lead | OpenAPI lint passes; catalog and OpenAPI diff are reconciled. |
| DEV-04 | P0 | Reconcile the Database Physical Model, Canonical Data Dictionary, and SQL migrations; define all fields for the MVP tables. | Data Architect | Approved ERD/DDL and a migration plan with no unresolved mismatch. |
| DEV-05 | P0 | Approve runtime/deployment ADRs: services, database, object storage, eventing, cache, identity, observability, and environment topology. | Architecture / Platform Lead | ADRs and target deployment diagram. |
| DEV-06 | P0 | Establish repository delivery controls: branching, protected reviews, CI build/test/lint/security gates, migration validation, and artifact versioning. | Platform Engineering | A working pipeline for the foundation service and database. |
| DEV-07 | P1 | Conduct an MVP threat model and third-party integration/security review. | Security Lead | Threat model, mitigations, and accepted residual risks. |

## Must Fix Before Beta

Beta may include real users only after these controls are implemented and verified in a beta-like environment.

| ID | Priority | Required outcome | Primary owner | Evidence of completion |
|---|---|---|---|---|
| BETA-01 | P0 | Implement all MVP journeys end-to-end with traceability from requirement to screen, API, database migration, state transition, event, notification, and test. | Engineering Lead | Passing traceability report and release test evidence. |
| BETA-02 | P0 | Complete OpenAPI contracts and implementations for KYC review, subscription changes, payment/refund webhook flow, offline sync, and the selected MVP privacy functions. | API / Domain Leads | Provider and consumer contract tests passing. |
| BETA-03 | P0 | Implement missing persistence for tasks, cancellation/refund decisioning, vendor settlement linkage, and all selected MVP physical tables. | Data / Domain Leads | Forward-only migrations and integration tests. |
| BETA-04 | P0 | Deploy DEV, QA, UAT, and beta environments through infrastructure-as-code with secrets management, least privilege, monitoring, and rollback. | Platform Engineering | Reproducible environment promotion and deployment run. |
| BETA-05 | P0 | Implement security controls for OTP/session lifecycle, RBAC, tenant/property access, PII masking, encryption, audit events, webhook validation, and rate limiting. | Security / Engineering | Security test results and access-control test evidence. |
| BETA-06 | P1 | Complete missing KYC, subscription-change, privacy/consent, and selected project/operations screens and flows for the beta scope. | Product / UX | Reviewed screens, accessibility acceptance, and UI tests. |
| BETA-07 | P1 | Establish UAT, regression, API, migration, performance smoke, and security test suites with quality gates. | QA Lead | Automated results and signed UAT acceptance. |
| BETA-08 | P1 | Configure on-call, incident intake, dashboards, alert routing, support queues, and beta feedback/reconciliation process. | Operations Lead | Drill and ownership roster. |

## Must Fix Before Production

Production is prohibited until these controls have been operated successfully and release evidence is approved.

| ID | Priority | Required outcome | Primary owner | Evidence of completion |
|---|---|---|---|---|
| PROD-01 | P0 | Complete independent security assessment: SAST, dependency scanning, DAST, penetration testing, remediation, and production access review. | Security Lead | Signed security release assessment; no unaccepted critical/high findings. |
| PROD-02 | P0 | Prove backup/restore and DR against approved RTO/RPO; retain exercise records and corrective actions. | Platform / Operations Leads | Successful restoration and DR exercise. |
| PROD-03 | P0 | Prove performance, resilience, rate-limit, concurrency, and failure-recovery behaviour at approved production load. | Engineering / QA Leads | Load and chaos/failure test report meeting SLOs. |
| PROD-04 | P0 | Activate privacy, financial, audit, retention, legal-hold, refund/reconciliation, and evidence-integrity controls end-to-end. | Compliance / Finance / Security Leads | Control tests, reconciliation, and audit samples. |
| PROD-05 | P0 | Complete production operations readiness: 24/7 ownership, dashboards, paging, SLOs/error budgets, support escalation, change management, and incident communications. | Operations Lead | Production readiness review and live drill. |
| PROD-06 | P1 | Complete API consumer onboarding, OpenAPI publication, compatibility checks, webhook replay/verification, and deprecation communication process. | API Lead | Consumer contract and operational monitoring evidence. |
| PROD-07 | P1 | Establish data-quality monitoring, retention/anonymisation jobs, access recertification, and audit-log immutability verification. | Data Governance / Security | Scheduled control reports and exception workflow. |

## Prioritized Implementation Backlog

The backlog is sequenced to reduce rework. P0 items are critical path; P1 items can proceed after their dependent P0 foundations are approved; P2 items are post-beta optimisations.

| Order | ID | Priority | Backlog item | Depends on |
|---:|---|---|---|---|
| 1 | IR-01 | P0 | Create ADRs and reconcile canonical product, state, API-path, and ownership decisions. | DEV-01, DEV-02 |
| 2 | IR-02 | P0 | Define the MVP service slice: registration/OTP, customer/property, service request, assignment, visit/evidence, report, and selected payment/subscription behaviour. | IR-01 |
| 3 | IR-03 | P0 | Rebuild the OpenAPI `/api/v1` contract for the MVP slice; add linting and breaking-change checks. | IR-01, IR-02 |
| 4 | IR-04 | P0 | Reconcile physical model and create versioned, forward-only database migrations with reference data and schema tests. | IR-01, IR-02 |
| 5 | IR-05 | P0 | Build baseline platform: repository pipeline, containers, IaC, DEV/QA environments, secrets, central logs/metrics/traces, and database migration runner. | IR-03, IR-04 |
| 6 | IR-06 | P0 | Implement identity/RBAC/OTP/session, tenant/property authorisation, and audit foundations. | IR-03, IR-05 |
| 7 | IR-07 | P0 | Implement the MVP domain vertical slice with contract, integration, state-transition, and event tests. | IR-03 through IR-06 |
| 8 | IR-08 | P1 | Implement selected subscription, payment/refund, KYC, offline-sync, and notification contracts/data/workflows required for beta. | IR-03 through IR-07 |
| 9 | IR-09 | P1 | Add the missing beta screens, navigation flows, accessibility coverage, and role-based UAT scripts. | IR-07, IR-08 |
| 10 | IR-10 | P1 | Establish beta operations: alerting, support workflow, incident drills, backup restore, performance smoke, and release gates. | IR-05 through IR-09 |
| 11 | IR-11 | P1 | Add privacy-rights, consent, legal-hold, retention, and financial reconciliation capabilities. | IR-04 through IR-10 |
| 12 | IR-12 | P0 | Execute production security, DR, performance, and operational-readiness validation. | IR-10, IR-11 |
| 13 | IR-13 | P2 | Deliver construction project lifecycle, advanced CRM/analytics, and broader marketplace enhancements outside agreed MVP. | Stable beta baseline |

## Decision

PropertyPilot is **documentation-rich but implementation-foundation poor**. The immediate goal should be a six-to-eight-week foundation and MVP-contract phase focused on IR-01 through IR-06, not broad feature construction. Reassess readiness after the first vertical slice is deployed to QA with a reconciled OpenAPI contract, migration set, automated pipeline, and security controls.

## Evidence Base

- [Traceability Matrix](Traceability_Matrix.md)
- [PropertyPilot Gap Analysis](PropertyPilot_Gap_Analysis.md)
- [Technical Architecture](Technical_Architecture.md)
- [Database Physical Model](04_Data/Database_Physical_Model.md)
- [OpenAPI Specification](05_APIs/OpenAPI_Specification.yaml)
- [Security Design](03_Architecture/Security_Design.md)
- [DevOps Architecture](03_Architecture/DevOps_Architecture.md)
- [Operational Runbooks](08_Implementation/Operational_Runbooks.md)
- [MVP Release Plan](07_Planning/MVP_Release_Plan.md)
