# PropertyPilot Final Repository Health Report

## Purpose and Assessment Basis

This report is the final read-only health assessment of the PropertyPilot repository as of 2026-08-31. It evaluates documentation, architecture, contracts, data design, security, integrations, delivery assets, and operating readiness.

**Validation scope:** 189 Markdown/OpenAPI documentation artefacts under `docs/`; implementation directories (`backend`, `admin-portal`, `infrastructure`, and `database`); and six initial SQL migrations. No existing document, implementation asset, or configuration was changed by this assessment.

## Executive Result

PropertyPilot is **documentation-rich but not implementation-ready for broad development or production**. The repository has strong product/governance intent and substantial operating design. Its authoritative contracts and executable baseline are not yet reconciled: OpenAPI is incomplete and divergent, the physical data model has core tables without fields, migrations diverge from the data/state model, all external providers remain unselected, and no application, infrastructure-as-code, or CI/CD implementation is present.

## Completeness Scores

| Measure | Score | Basis |
|---|---:|---|
| Documentation Completeness | **78%** | Broad coverage exists across product, architecture, data, API, security, operations, governance, webhooks, integrations, KYC, and privacy. Coverage is reduced by duplicate/stale artefacts, incomplete traceability, and several missing end-to-end specifications. |
| Architecture Completeness | **63%** | Architecture domains, standards, security, data, operations, and integration patterns are documented. Executable topology, selected technologies/providers, final domain boundaries, event implementation, and measurable NFR/SLO decisions remain incomplete. |
| Implementation Readiness | **46%** | Consistent with the implementation readiness assessment: a controlled foundation phase can start after P0 reconciliation, but broad development would create material rework. |
| Production Readiness | **18%** | No implemented services, IaC, CI/CD, test evidence, provider contracts, production environment, monitoring, backup/restore exercise, or security validation is evidenced. |

## Documentation Validation

### Strengths

- The repository contains canonical cross-cutting rules, SRS/product catalogue material, customer journeys, screens/flows, state/event/notification catalogues, data models, API governance, security, KYC, privacy, webhook, integration, runbook, and readiness artefacts.
- [Documentation Control Register](Documentation_Control_Register.md) defines ownership, status, canonical authority, review cadence, and deprecation controls.
- [Cross-Cutting Requirements](Cross_Cutting_Requirements.md) resolves platform-wide conflicts and establishes security, privacy, retention, API, tenancy, and lifecycle principles.
- [Traceability Matrix](Traceability_Matrix.md) has already identified several critical cross-artifact gaps and conflicts.

### Duplicate Documents and Duplicate Content

| ID | Duplicate / overlapping artefact | Risk | Required disposition |
|---|---|---|---|
| D-01 | `ARCHITECTURE_SUMMARY.md` and `Technical_Architecture.md` | Divergent architecture decisions can be implemented. | Keep Technical Architecture canonical; retain summary only as labelled overview or archive. |
| D-02 | `Database_Design.md`, `Database_Design_v2.md`, and `04_Data/Database_Physical_Model.md` | Multiple schema authorities create migration drift. | Use Database Physical Model as canonical; migrate unique content and deprecate legacy designs. |
| D-03 | Root `Project_Master_Specification.md` and `07_Planning/Project_Master_Specification.md` | Planning scope/version can diverge. | Keep planning-path file canonical; merge unique history then archive root copy. |
| D-04 | `API_Catalog.md` detailed contracts and appended endpoint inventories | Bare route lists may drift from OpenAPI and detailed definitions. | Treat appended inventories as deprecated; generate/reconcile catalog from OpenAPI. |
| D-05 | Legacy lifecycle sections in `Subscription_Management.md` and `State_Model_Catalog.md` | Different subscription status vocabulary and transitions. | State Model Catalog is canonical; map/remove legacy terms. |
| D-06 | `agent_assignments` migration representation, `service_assignments`, and `vendor_assignments` | Assignment ownership, SLA, reporting, and lifecycle can split. | Define one canonical assignment aggregate and migration path. |
| D-07 | `Business_Process_Catalog.md` duplicate second catalog segment | Duplicate process ownership/requirements. | Remove the duplicate segment in a controlled documentation change. |

## Conflicting Requirements

| ID | Conflict | Evidence | Required resolution |
|---|---|---|---|
| C-01 | API base URL is inconsistent. | Cross-Cutting Requirements mandates `/api/v1`; API Catalog examples omit it; OpenAPI server is `/v1`. | Approve `/api/v1` as v1 standard and regenerate OpenAPI/API Catalog/client routes. |
| C-02 | Authentication scope is inconsistent. | Cross-Cutting Requirements makes mobile OTP the MVP customer/agent method; API Catalog exposes password login/reset and MFA as active. | Mark non-OTP routes future/privileged-only or amend approved MVP policy. |
| C-03 | Property and marketplace semantics are conflated. | Physical model treats `properties` as customer-owned; API Catalog labels `POST /properties` as listing creation. | Separate managed-property and marketplace-listing resources/contracts. |
| C-04 | Service lifecycle names differ. | State/Cross-Cutting model uses `NEW` and governed transitions; migration defaults to `created`. | Align API enums, migrations, events, status history, and State Model Catalog. |
| C-05 | Subscription status vocabulary differs. | Canonical state rules use `PENDING`, `PAST_DUE`, `SUSPENDED`; legacy document uses `DRAFT`, `PENDING_PAYMENT`, `RENEWAL_DUE`. | Publish mapping/deprecation and migrate all consumers to canonical vocabulary. |
| C-06 | Initial pricing distance source differs. | Pricing Engine uses agent/property coordinates; Cross-Cutting Requirements requires property/coverage coordinates for initial quotes. | Apply Cross-Cutting rule and amend pricing contract/engine specification. |
| C-07 | SRS source references are stale. | SRS says Pricing Strategy is absent, but the file exists and is referenced elsewhere. | Update SRS source-of-truth and pricing acceptance references. |

## Missing APIs

The canonical [OpenAPI Specification](05_APIs/OpenAPI_Specification.yaml) presently covers only a small subset of the documented platform and differs from the API Catalog/data model. The following capabilities require complete request/response/error/security contracts before implementation.

| ID | Missing API capability | Minimum operations |
|---|---|---|
| A-01 | KYC administration | Queue/status, approve, reject, rework, expiry, reverification, fraud escalation. |
| A-02 | Subscription lifecycle control | Upgrade, downgrade, pause/cancel, auto-renew consent, grace/`PAST_DUE`/suspension, lifecycle history. |
| A-03 | Payment webhook and refund workflow | Signed provider intake, refund approval/status/reversal, reconciliation exceptions. |
| A-04 | Offline agent synchronisation | Submit/retry queued operations, retrieve conflict state, resolve/replay. |
| A-05 | Construction/project execution | Create from quotation; milestones, scope changes, acceptance, warranty, settlement. |
| A-06 | Privacy rights | Export, correction, deletion, consent, legal-hold-aware request status/outcome. |
| A-07 | Agent/vendor task management | Task list/detail, assignment, due date, work state, escalation. |
| A-08 | Vendor invoice and settlement | Invoice, approval, payout/settlement, reconciliation, assignment/quotation linkage. |
| A-09 | Complete API platform conventions | Standard errors, OAuth/security schemes, pagination, filters, idempotency, webhook callbacks, versioning and deprecation metadata in OpenAPI. |

## Missing Screens

| ID | Missing screen | Required role/action |
|---|---|---|
| U-01 | KYC Review and Exception Queue | Admin/operations queue, evidence review, approve/reject/rework, expiry/fraud actions. |
| U-02 | Subscription Change Management | Customer control for pause, cancellation, auto-renewal, downgrade timing, grace/suspension status. |
| U-03 | Construction Project Milestones | Customer/vendor/operations view of milestone, change order, evidence, acceptance, warranty. |
| U-04 | Privacy and Consent Centre | Preferences, consent, export, correction/deletion request, legal-hold-aware status. |
| U-05 | Compliance and document-expiry review | Operations matrix for expired/missing KYC, property, vendor, and contractual documents. |
| U-06 | Tenant move-out/exit workflow | Operations workflow for exit, check-out, deposit/security and closure. |
| U-07 | Payment dispute and reconciliation workbench | Finance review for chargeback, reversal, adjustment, refund exception, and evidence. |
| U-08 | Operations incident/audit response console | Controlled investigation, remediation, evidence, and closure for security/audit incidents. |

## Missing Database Tables and Physical Definitions

| ID | Missing table / definition | Required responsibility |
|---|---|---|
| DB-01 | Complete fields for `subscription_plans`, `customer_subscriptions`, `subscription_renewals` | Plan/version acceptance, lifecycle, billing anchor, entitlement, renewal and audit details. |
| DB-02 | Complete fields for `payments`, `invoices`, `refunds` | Provider reference, currency, tax, line items, intent/capture/refund/reconciliation state. |
| DB-03 | Complete fields for `vendors`, `quotations`, `vendor_assignments` | Profile/verification, quote version/cost/tax/validity, assignment and commercial linkage. |
| DB-04 | Complete fields for `complaints`, `complaint_comments` | Reporter, service/property context, severity, lifecycle, SLA, resolution, evidence/comments. |
| DB-05 | Complete fields for `notifications`, `notification_templates` | Event/recipient/channel/template version, consent decision, delivery, retry, provider reference. |
| DB-06 | `tasks` | Agent/vendor task identity, assignment, due date, state, service-request relation, audit history. |
| DB-07 | `service_request_cancellations` | Requester, reason, effective date, policy/refund decision, approval and outcome. |
| DB-08 | Vendor invoice/settlement linkage | Dedicated vendor invoice or explicit invoice/payment FKs to assignment and quotation. |
| DB-09 | Property ownership attributes | Ownership percentage, effective dates, verification/source-document reference, change history. |
| DB-10 | Privacy operations persistence | Request, consent, legal hold, export manifest, deletion/anonymisation outcome, processor confirmation, and retention-execution records. |
| DB-11 | KYC operations persistence | Case state history, reviewer/maker-checker, reason codes, evidence references, fraud escalation, expiry/reverification schedule. |

Additionally, the shared audit fields in the physical model still lack complete physical types, nullability, defaults, and enforced migration conventions. Existing SQL migrations conflict with physical-model entity shape and status nomenclature.

## Missing Workflows

| ID | Missing workflow | Why it is required |
|---|---|---|
| W-01 | Payment dispute, chargeback, adjustment, and collections escalation | Required to protect financial integrity and handle failed/contested payments. |
| W-02 | Subscription proration, grace, retry, arrears, suspension, reactivation, and downgrade policy | Required for an enforceable subscription lifecycle. |
| W-03 | Construction project lifecycle | Required for quotations, milestones, variation/change order, acceptance, warranty, and settlement. |
| W-04 | Vendor onboarding, performance remediation, agreement/compliance renewal | Required before reliable vendor assignment and settlement. |
| W-05 | KYC administration flow in product/API/data plane | The operating playbook exists, but screens/APIs/tables and integration execution remain incomplete. |
| W-06 | Privacy-rights fulfilment in product/API/data plane | The procedure exists, but required request/consent/legal-hold persistence, screens, and APIs are incomplete. |
| W-07 | Security incident and audit response workflow implementation | Policies/runbooks exist, but incident tooling, immutable evidence store, and response console are not evidenced. |
| W-08 | Retention, legal-hold, deletion/anonymisation execution | Rules exist, but jobs, processor propagation, backup handling, and control reporting are not evidenced. |

## Missing Integrations

The [Integration Contract Register](Integration_Contract_Register.md) describes expected controls but every listed provider remains `TBD`.

| ID | Missing integration decision or implementation | Impact |
|---|---|---|
| I-01 | Payment gateway selection, PCI/DPA review, adapter, signed callback, reconciliation and failover test. | Payments, subscriptions, refunds, invoices cannot operate. |
| I-02 | SMS, WhatsApp, and email provider selection/adapters, approved templates, consent/suppression, delivery callbacks, and failover. | OTP, transactional messages, alerts, and customer communication cannot operate. |
| I-03 | KYC provider selection, DPA, secure evidence-reference exchange, callback, manual fallback, and fraud escalation integration. | Required verification cannot be automated or governed end-to-end. |
| I-04 | Maps/coverage provider selection, quota controls, caching/failover, and price/eligibility integration. | Serviceability, ETA, location validation, and initial pricing remain unreliable. |
| I-05 | Object storage, KMS, malware scanning, immutable evidence retention, signed access, backup/restore. | Documents, evidence, reports, exports, and KYC cannot safely operate. |
| I-06 | Analytics/warehouse ingestion, pseudonymisation, retention, dashboard, and export controls. | KPI/reporting claims cannot be measured safely. |
| I-07 | Event broker/outbox/DLQ/schema registry implementation. | Event Catalog contracts have no durable operational transport. |

## Missing Security Controls or Security Evidence

Security design is comprehensive; the missing items are principally implementation and verification controls.

| ID | Missing control/evidence | Required outcome |
|---|---|---|
| S-01 | Threat model and provider/data-flow risk assessment for MVP. | Approved threats, mitigations, residual-risk acceptance, and review date. |
| S-02 | Central secrets, KMS, key rotation and revocation implementation. | Environment-scoped secrets, rotation tests, audit trail, no credentials in code. |
| S-03 | Identity/RBAC/OTP/session enforcement. | Step-up controls, least privilege, tenant/property access checks, session revocation, automated tests. |
| S-04 | Immutable/tamper-evident audit implementation. | Append-only audit store, restricted mutation, retention/hold support, monitored integrity failures. |
| S-05 | PII encryption/tokenisation and non-production masking. | Field/object encryption, data classification enforcement, masked test data, access logging. |
| S-06 | API/webhook abuse and signature protection. | Rate limiting, WAF/risk controls, request limits, signed callback/replay checks, DLQ monitoring. |
| S-07 | Security assurance evidence. | SAST, dependency/SBOM, DAST, penetration test, remediation and production access review. |
| S-08 | Privacy/retention execution controls. | Consent propagation, data-subject request system, legal hold, deletion/anonymisation, backup expiry and audit evidence. |

## Top 20 Remaining Gaps

| Rank | Priority | Gap | Release gate |
|---:|---|---|---|
| 1 | P0 | Reconcile canonical requirements and the seven documented conflicts. | Before development |
| 2 | P0 | Complete and approve a single `/api/v1` OpenAPI contract; align catalog and data naming. | Before development |
| 3 | P0 | Reconcile Database Physical Model, canonical dictionary, and migrations; define all core table fields. | Before development |
| 4 | P0 | Build CI/CD, IaC, environments, secrets injection, artifact/version control, and migration runner. | Before development |
| 5 | P0 | Approve target runtime, deployment, identity, storage, eventing, observability, and provider ADRs. | Before development |
| 6 | P0 | Implement identity/RBAC/OTP/session, tenancy/property authorisation, and immutable audit foundation. | Before beta |
| 7 | P0 | Select and integrate a payment gateway with signed webhooks, idempotency, reconciliation, and refunds. | Before beta |
| 8 | P0 | Implement core MVP vertical slice with traceable API, schema, state, event, notification, and tests. | Before beta |
| 9 | P0 | Define and implement missing payments/invoices/refunds, subscriptions, vendor, complaint, notification, task, and cancellation persistence. | Before beta |
| 10 | P0 | Select secure storage/KMS and implement evidence/document/report/backup controls. | Before beta |
| 11 | P0 | Implement KYC operations in product/API/data planes and select provider/manual fallback. | Before beta |
| 12 | P0 | Implement privacy request, consent, retention, legal-hold, and deletion/anonymisation mechanisms. | Before beta |
| 13 | P0 | Complete security testing and remediate critical/high results. | Before production |
| 14 | P0 | Prove restore/DR, performance/resilience, and incident response at production targets. | Before production |
| 15 | P1 | Add KYC, subscription-change, privacy/consent, compliance, payment-dispute, and project screens/flows. | Before beta |
| 16 | P1 | Implement subscription proration, grace, retry, suspension, reactivation, and auto-renewal rules. | Before beta |
| 17 | P1 | Implement vendor invoice/settlement and performance/remediation workflow. | Before beta |
| 18 | P1 | Select/integrate messaging, maps, analytics, and event platform with monitoring, DLQ, and failover. | Before beta |
| 19 | P1 | Configure monitoring/SLOs, dashboards, on-call, support queues, and operational drills. | Before beta |
| 20 | P2 | Consolidate duplicate/legacy documents and generate derivative API/data views from canonical sources. | Before production scale |

## Release Decision

| Gate | Decision | Condition |
|---|---|---|
| Broad development | **No-go** | Close Top 20 gaps 1–5 and approve the foundation baseline. |
| Controlled foundation work | **Conditional go** | A single team may execute ADR, contract, schema, pipeline, and identity foundation tasks with explicit change control. |
| External beta | **No-go** | Close P0 beta gaps; demonstrate security, data, integration, test, support, and operational evidence. |
| Production | **No-go** | Close all P0 production gaps and obtain formal security, DR, performance, privacy/financial, and operations sign-off. |

## Evidence Sources

- [Traceability Matrix](Traceability_Matrix.md)
- [Implementation Readiness Assessment](Implementation_Readiness_Assessment.md)
- [Documentation Control Register](Documentation_Control_Register.md)
- [Cross-Cutting Requirements](Cross_Cutting_Requirements.md)
- [Technical Architecture](Technical_Architecture.md)
- [Database Physical Model](04_Data/Database_Physical_Model.md)
- [OpenAPI Specification](05_APIs/OpenAPI_Specification.yaml)
- [Integration Contract Register](Integration_Contract_Register.md)
- [Security Design](03_Architecture/Security_Design.md)
