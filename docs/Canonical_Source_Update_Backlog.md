# Canonical Source Update Backlog

Document Type: Governance and Documentation Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This backlog identifies every existing PropertyPilot document that must be updated to align with the newly established governance, architecture, API, data, workflow, and traceability baseline.

It is intended to:
- close documentation drift
- align active documents to canonical sources
- remove duplicate or conflicting definitions
- prioritize backlog updates by dependency
- prepare the documentation set for production readiness and release governance

---

## 1. Document Update Matrix

| Document | Reason For Update | Required Changes | Priority | Owner | Status |
|---|---|---|---|---|---|
| PropertyPilot_SRS.md | Source baseline must remain authoritative and aligned to product scope | Confirm requirement ownership, add missing functional requirements, align status model, reconcile scope drift | Critical | Product Owner | Planned |
| Feature_Catalog.md | Must align with SRS and screen/API traceability | Add missing features, standardize naming, map to journeys and workflows | Critical | Product Owner | Planned |
| Service_Catalog.md | Service inventory must match actual implementation model | Add service-level ownership, lifecycle states, dependencies, SLAs | High | Product Owner | Planned |
| Customer_Journeys.md | Journey detail must be consistent with screens and APIs | Add missing journeys, reconcile task flow names, align with service catalog | Critical | Product Owner | Planned |
| Screen_Catalog.md | Screen inventory must map to APIs and workflows | Add missing screens, mark orphan screens, align IDs to routes and ownership | High | UX Lead | Planned |
| Screen_Flows.md | Flow names and sequence definitions require consistency | Normalize nomenclature, add missing transitions, confirm screen-to-workflow mapping | High | UX Lead | Planned |
| API_Catalog.md | API inventory is out of sync with live spec and governance | Remove stale entries, map all APIs to screens and tables, mark deprecated endpoints | Critical | Engineering Lead | Planned |
| OpenAPI_Specification.yaml | Canonical API contract must reflect all active workloads | Add missing endpoints, align route versions, standardize response schemas, fix auth metadata | Critical | API Lead | Planned |
| Database_Physical_Model.md | Physical model must match canonical data dictionary and backlog | Add missing tables, normalize naming, add schema annotations, confirm PK/FK integrity | Critical | Data Architect | Planned |
| Canonical_Data_Dictionary.md | Canonical field-level definitions must be shared across APIs and DB | Add missing fields and enums, document status codes, relationships, constraints | Critical | Data Architect | Planned |
| Cross_Cutting_Requirements.md | Governing policy must align with privacy, security, and architecture | Add explicit precedence rules, standardize shared requirements, resolve conflicts | Critical | Enterprise Architect | Planned |
| Technical_Architecture.md | Architecture should match the system runtime and event model | Add event-driven pattern details, service boundaries, tenant boundaries, integration flows | Critical | Architect | Planned |
| Security_Design.md | Security baseline must align with privacy and API model | Add role mapping, token lifecycles, PII handling, event security, integration controls | Critical | Security Lead | Planned |
| Subscription_Plans.md | Commercial plans must align with subscription lifecycle and billing logic | Add lifecycle states, plan code mapping, renewal events, entitlement rules | High | Product Owner | Planned |
| Pricing_Strategy.md | Pricing logic must align with billing and entitlement model | Add pricing rules, plan changes, edge cases, tax handling, exceptions | High | Product Owner | Planned |
| Privacy_Rights_Operating_Procedure.md | Privacy workflows must align with current privacy APIs | Add legal hold exceptions, consent state mapping, export packages, traceability rules | Critical | Privacy Officer | Planned |
| KYC_Operations_Playbook.md | KYC process must align with actual API and status model | Add fraud escalation workflow, statuses, reviewer queue, expiry/rework rules | Critical | KYC Operations Lead | Planned |
| Webhook_Contract_Catalog.md | Callback contracts must align with API and integration register | Add provider payload schemas, retry semantics, signature validation, event versioning | High | Integration Architect | Planned |
| Integration_Contract_Register.md | External integrations need canonical owner and lifecycle mapping | Add provider matrix, auth flow, SLA, failure handling, maintenance window dependencies | High | Integration Architect | Planned |
| Construction_Project_Lifecycle_Specification.md | Construction lifecycle must align with project workflows and events | Add project state transitions, ownership, milestone approvals, warranty events | High | Product Owner | Planned |
| Traceability_Matrix_v2.md | Must remain the release-readiness governing map | Add missing trace links, confirm gaps closed, align coverage percentages | Critical | PMO | Planned |
| Documentation_Control_Register.md | Must govern document ownership and source-of-truth rules | Finalize canonical classification, owner matrix, review cadence | Critical | PMO | Planned |
| Documentation_Consolidation_Plan.md | Should be reconciled with final backlog outcome | Mark completed actions, remove stale recommendations | Medium | PMO | Planned |
| Documentation_Conflict_Resolution.md | Must reflect final conflict outcomes | Update resolved conflicts, remove unresolved items, add owner assignments | High | PMO | Planned |
| Production_Cutover_and_GoLive_Checklist.md | Cutover checklist must align with release governance | Add change control approvals, deployment gating, rollback validation, smoke matrix | High | Operations Lead | Planned |
| Environment_Management.md | Environment baselines must match deployment model | Confirm environment matrix, approvals, runtime parity, config drift checks | High | Platform Lead | Planned |
| Release_Management.md | Release process must match operational checklist | Align stage gates, approvals, release windows, signoff criteria | High | PMO | Planned |
| MVP_Release_Plan.md | Release plan needs to reflect final backlog sequencing | Align milestone dates, scope acceptance, dependencies | Medium | PMO | Planned |
| State_Model_Catalog.md | State models must align with service, subscription, and project lifecycles | Add missing states, transitions, business rules | High | Enterprise Architect | Planned |
| Event_Catalog.md | Events must support integrations and notifications | Add missing event names, payloads, emitters, consumers | High | Integration Architect | Planned |
| Notification_Catalog.md | Notification mapping must be aligned to events and screens | Add missing notifications, preference triggers, delivery statuses | Medium | Customer Operations | Planned |

---

## 2. API Documentation Updates

### 2.1 API Catalog Changes
Required updates:
- Remove stale or duplicate endpoints not in the canonical contract
- Add all missing service APIs from the operational backlog
- Add lifecycle metadata:
  - status
  - owner
  - version
  - deprecation date
  - replacement endpoint
- Classify endpoints by domain:
  - customer
  - property
  - subscription
  - payment
  - KYC
  - privacy
  - notifications
  - marketplace
  - construction
  - webhooks
- Add screen-to-API mapping for all active product screens
- Add data ownership tags for each endpoint
- Add auth and role metadata

Required API additions:
- GET /api/v1/kyc/{id}/status
- GET /api/v1/privacy/requests/{id}
- GET /api/v1/reports/{id}/download or export variants
- /api/v1/payments/chargebacks
- /api/v1/notifications/{id}/retry
- /api/v1/projects/{projectId}/status
- /api/v1/vendors/{id}/status
- /api/v1/marketplace/quotes/{id}/status

### 2.2 OpenAPI Changes
Required changes:
- Unify route versioning to /api/v1
- Add missing schemas and response models
- Add pagination metadata for list endpoints
- Add explicit error payload schema for all endpoints
- Add retryable and non-retryable error codes
- Add webhook and callback auth schemas
- Add request and response examples for key flows:
  - subscription lifecycle
  - KYC review
  - privacy request processing
  - payment reconciliation
  - refund workflow
- Add `x-` extension metadata for:
  - owner
  - domain
  - deprecation policy
  - support SLA
- Standardize HTTP semantics:
  - GET for reads
  - POST for create/action
  - PATCH for partial update
  - DELETE for revoke/delete
- Ensure OpenAPI is the authoritative source of contract behavior

### 2.3 Webhook Changes
Required updates:
- Add explicit provider-level payload contracts for:
  - payment
  - SMS
  - WhatsApp
  - email
  - storage callbacks
- Add event-idempotency rules
- Add signature validation requirements
- Add retry and dead-letter handling
- Add event versioning strategy:
  - v1, v2 migration rules
  - consumer compatibility expectation
- Add webhook consumer status tracking

### 2.4 Versioning Impacts
Required updates:
- Standardize all public API route paths to versioned form
- Add deprecation markers for old endpoints
- Document migration windows and sunset dates
- Ensure OpenAPI and API catalog remain synchronized
- Add compatibility rules for:
  - breaking changes
  - major vs minor versioning
  - backwards compatibility matrix

---

## 3. Data Documentation Updates

### 3.1 New Entities
Add the following entities if not already present:
- kyc_fraud_alert
- consent_record
- legal_hold_scope
- quote_offer
- reconciliation_batch
- report_job
- webhook_event
- notification_delivery
- assignment
- milestone_approval

### 3.2 New Fields
Add or validate:
- customer.status_reason
- customer.kyc_status
- customer.privacy_status
- property.ownership_status
- property.marketplace_status
- subscription.plan_version
- subscription.auto_renewal_consent
- payment.payment_channel
- payment.reconciliation_status
- invoice.tax_amount
- refund.reason_code
- kyc_profile.risk_score
- kyc_profile.expiry_at
- kyc_review_case.review_queue
- privacy_request.legal_hold_flag
- privacy_request.source_channel
- report_job.export_format
- webhook_event.signature_valid
- webhook_event.retry_count

### 3.3 New Enums
Add or standardize:
- customer.status
- property.status
- service_request.status
- subscription.status
- payment.status
- refund.status
- kyc_profile.status
- kyc_review_case.outcome
- privacy_request.type
- privacy_request.status
- notification.channel
- notification.delivery_status
- project.status
- project_milestone.status
- change_order.status
- vendor.status

### 3.4 New Relationships
Add or validate:
- customer -> subscription
- customer -> payment
- customer -> kyc_profile
- customer -> privacy_request
- property -> owner
- property -> service_request
- subscription -> plan
- subscription -> consent_record
- payment -> invoice
- payment -> refund
- project -> project_milestone
- project -> change_order
- vendor -> assignment
- kyc_profile -> kyc_document
- kyc_profile -> kyc_review_case
- privacy_request -> data_export_job / data_deletion_job
- webhook_event -> notification_delivery
- report_job -> report_definition

---

## 4. Architecture Documentation Updates

### 4.1 Event-Driven Architecture Changes
Required updates:
- Define event emitters for:
  - customer.created
  - kyc.submitted
  - kyc.reviewed
  - payment.authorized
  - subscription.created
  - subscription.changed
  - notification.delivered
  - legal_hold.created
  - webhook.received
  - report.generated
- Document event consumers and orchestration rules
- Define event retention, retry, and deduplication strategy
- Add event versioning and compatibility policy

### 4.2 Integration Changes
Required updates:
- Map all provider integrations:
  - payment gateway
  - SMS
  - WhatsApp
  - email
  - maps
  - storage
  - analytics
- Add provider contract owner, SLA, and failure mode
- Add callback routes and replay behavior
- Define integration error handling and status propagation

### 4.3 Security Impacts
Required updates:
- Add identity and access model by domain
- Add PII handling for KYC and privacy flows
- Update auth patterns:
  - customer auth
  - support auth
  - partner auth
  - service-to-service auth
- Add secure delivery rules for exports and notifications
- Confirm encryption and secret management policy for all integrations

### 4.4 Multi-Tenancy Impacts
Required updates:
- Document tenant separation rules for:
  - customer data
  - vendor data
  - admin contexts
  - partner integrations
- Add per-tenant access control model
- Document tenant-scoped configuration and environment separation
- Add quotas, isolation rules, and data residency requirements

---

## 5. Product Documentation Updates

### 5.1 SRS Updates
Required changes:
- Add missing requirements for:
  - KYC expiry and re-verification
  - privacy export and deletion
  - legal hold handling
  - webhook processing
  - construction lifecycle
  - marketplace vendor approval
- Add explicit non-functional requirements for:
  - API availability
  - notification reliability
  - privacy compliance
  - performance and security
- Add acceptance criteria for each critical workflow

### 5.2 Feature Catalog Updates
Required changes:
- Add missing product features:
  - consent management
  - privacy portal
  - report export
  - legal hold
  - construction milestones
  - marketplace compare and assignment
  - webhook management
- Add owner, priority, release tag, and lifecycle status

### 5.3 Customer Journey Updates
Required changes:
- Add missing journey variants:
  - NRI customer
  - property owner
  - agent
  - vendor
  - partner
- Add journey exit and re-entry conditions
- Align journeys with notification and status mechanics

### 5.4 Screen Catalog Updates
Required changes:
- Add missing screens:
  - privacy request tracker
  - NRI KYC
  - legal hold dashboard
  - reconciliation queue
  - vendor approval queue
  - project milestone board
- Add screen ownership, APIs, and workflow mapping

---

## 6. Workflow Documentation Updates

### 6.1 Subscription Lifecycle Changes
Required changes:
- Standardize life cycle states:
  - Draft
  - Active
  - Paused
  - Cancelled
  - Expired
  - Pending Renewal
- Add transitions for:
  - upgrade
  - downgrade
  - pause/resume
  - cancellation
  - renewals
- Add event triggers and notification requirements

### 6.2 KYC Workflows
Required changes:
- Standardize status set:
  - Draft
  - Submitted
  - Under Review
  - Approved
  - Rejected
  - Rework Required
  - Expired
  - Suspended
- Add rework and fraud escalation path
- Add expiry detection and re-verification workflow
- Add reviewer assignment and queue logic

### 6.3 Refund Workflows
Required changes:
- Add refund decision path
- Add hold / validation / approval states
- Add notification and reversal tracking
- Add ledger impact and reconciliation mapping

### 6.4 Construction Workflows
Required changes:
- Add state model for project lifecycle
- Add milestone approval workflow
- Add change order approval path
- Add warranty claim triage workflow
- Add construction activity notifications

---

## 7. Canonical Source Decisions

| Duplicated Document | Canonical Source | Deprecated Source | Migration Approach |
|---|---|---|---|
| Duplicate architecture summaries | Technical_Architecture.md | ARCHITECTURE_SUMMARY.md and older summaries | Archive superseded docs, update links |
| Duplicate database designs | Database_Physical_Model.md | Database_Design.md and schema drafts | Merge schema, validate against canonical dictionary |
| Duplicate API drafts | OpenAPI_Specification.yaml + API_Catalog.md | stale API design docs and route variants | Replace with canonical route list |
| Duplicate security sections | Security_Design.md | scattered security fragments | Merge and archive duplicates |
| Duplicate product catalog variants | Feature_Catalog.md / Service_Catalog.md | older product summaries | Map to canonical features and service definitions |
| Duplicate gap analysis docs | Traceability_Matrix_v2.md | Architecture_Gap_Analysis.md / PropertyPilot_Gap_Analysis.md | Consolidate findings and archive duplicates |
| Duplicate privacy notes | Privacy_Rights_Operating_Procedure.md | older privacy summaries | Merge and update APIs and retention rules |
| Duplicate KYC wording and status definitions | KYC_Operations_Playbook.md | older onboarding docs | Adopt final KYC workflow and status model |
| Duplicate subscription lifecycle definitions | Subscription_Plans.md + Canonical_Data_Dictionary.md | older lifecycle notes | Merge state definitions and lifecycle transitions |
| Duplicate release notes and checklists | Production_Cutover_and_GoLive_Checklist.md | older runbook fragments | Archive older checklists after alignment |
| Duplicate webhooks and integration docs | Webhook_Contract_Catalog.md + Integration_Contract_Register.md | older provider notes | Merge into canonical integration register |

---

## 8. Recommended Update Sequence

### Phase 1: Governance and Source-of-Truth Stabilization
1. Finalize canonical source assignments:
   - PropertyPilot_SRS.md
   - Cross_Cutting_Requirements.md
   - Technical_Architecture.md
   - Canonical_Data_Model.md
   - Database_Physical_Model.md
   - OpenAPI_Specification.yaml
   - Security_Design.md
   - Traceability_Matrix_v2.md
2. Resolve remaining document conflicts.
3. Approve the documented owner matrix and review cadence.
4. Archive duplicate working and stale documents.
5. Update Documentation_Control_Register.md to reflect final state.

### Phase 2: Contract and Data Completion
1. Complete OpenAPI gaps.
2. Complete API_Catalog.md mapping and deprecation tags.
3. Complete Canonical_Data_Dictionary.md field and enum updates.
4. Add missing database tables and relationships.
5. Update Webhook_Contract_Catalog.md and Integration_Contract_Register.md.
6. Validate event and notification models.

### Phase 3: Product, Workflow, and Operational Readiness
1. Resolve SRS and feature catalog updates.
2. Update screen and journey mappings.
3. Update workflow documentation for KYC, subscriptions, refunds, and construction.
4. Finalize privacy and operational procedures.
5. Validate final traceability matrix and release readiness.
6. Confirm production cutover and go-live docs are aligned with final state.

---

## Actionable Backlog Summary

Critical first actions:
- finalize canonical sources
- complete API contract parity
- complete database and data dictionary parity
- resolve KYC, privacy, and subscription lifecycle conflicts
- close screen-to-API and screen-to-table gaps
- update traceability matrix and governance controls

High-risk items:
- privacy export and deletion flows
- KYC fraud and expiry flow
- payment and refund lifecycle
- webhook reliability and signature validation
- construction lifecycle specification and approval flow

---

## Final Recommendation

The documentation effort should proceed in dependency order. Governance and contracts must be stabilized first, then data and API models, then workflows and operational procedures. This order prevents the creation of new drift during implementation and ensures that the documentation set supports production readiness, release governance, and traceability.

This backlog should be treated as the implementation baseline for the next documentation remediation cycle.

