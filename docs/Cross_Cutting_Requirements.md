# PropertyPilot Cross-Cutting Requirements

## Version

1.0

## Status

MVP baseline

## Purpose

This document is the implementation baseline for concerns shared by the service, property, marketplace, financial, reporting, and operations modules. Module documents define domain behavior; this document defines the common contracts they must use.

## Service State Machine

Allowed transitions are:

| From | To | Required actor and condition |
|---|---|---|
| `NEW` | `PENDING_PAYMENT` | System; coverage and eligibility pass |
| `NEW` | `FAILED` | System; validation fails with reason |
| `PENDING_PAYMENT` | `PAYMENT_COMPLETED` | Payment service; verified provider event |
| `PENDING_PAYMENT` | `CANCELLED` | Customer or admin; payment window expires or cancellation is approved |
| `PAYMENT_COMPLETED` | `PENDING_ASSIGNMENT` | System; payment is captured |
| `PENDING_ASSIGNMENT` | `ASSIGNED` | Operations; eligible agent selected |
| `ASSIGNED` | `ACCEPTED` | Agent; acceptance deadline not exceeded |
| `ASSIGNED` | `PENDING_ASSIGNMENT` | System or operations; rejection or timeout |
| `ACCEPTED` | `IN_PROGRESS` | Agent; arrival and GPS validation pass |
| `IN_PROGRESS` | `REPORT_SUBMITTED` | Agent; required evidence and report pass validation |
| `REPORT_SUBMITTED` | `UNDER_REVIEW` | System; report is persisted |
| `UNDER_REVIEW` | `COMPLETED` | Quality team or auto-approval; report passes checks |
| `UNDER_REVIEW` | `IN_PROGRESS` | Quality team; corrections are required |
| Any non-terminal state | `ESCALATED` | System or operations; SLA breach or dispute |

Every transition is atomic, idempotent, authorization-checked, and recorded in `service_status_history`. Invalid transitions return a conflict error and do not partially apply side effects.

## Financial Controls

- A payment creates an idempotent payment intent keyed by `service_request_id` and client idempotency key.
- Provider webhooks are signature-verified, deduplicated by provider event ID, and may be retried safely.
- Captured payments create immutable transactions and an invoice with currency, tax, rounding, fee, and line-item details.
- Refunds support full and partial amounts, require an actor and reason, and cannot exceed the captured amount less prior refunds.
- Chargebacks and failed settlements create separate immutable events; records are never deleted or overwritten.
- Reconciliation compares provider settlement data with internal transactions daily and raises an exception for unmatched, duplicated, or amount-mismatched records.
- Customer price is frozen at payment. Reassignment or route changes cannot change it unless the customer explicitly approves a new quote.

## API Contract

All APIs use `/api/v1`, bearer authentication, JSON, UUID identifiers, and UTC timestamps. List endpoints support `page`, `page_size` (maximum 100), stable sorting, and documented filters. Mutating requests accept an `Idempotency-Key` where retries can create financial or operational side effects.

Errors use:

```json
{
  "code": "SERVICE_STATE_CONFLICT",
  "message": "The service cannot be moved from its current state.",
  "request_id": "uuid",
  "details": {}
}
```

The API shall expose service transitions, assignments, payments, refunds, invoices, reports, complaints, reviews, subscriptions, and admin configuration as versioned resources. Webhooks are signed, include an event ID and schema version, and are retried with exponential backoff.

## Authorization and Privacy

- Tenant and resource ownership checks apply after role checks; a role alone does not grant access to every resource.
- Customers can access only their properties, service requests, reports, invoices, and approved marketplace conversations.
- Agents can access only assigned jobs and the minimum location/property data required to execute them.
- Exact property coordinates are hidden from marketplace users and disclosed to an assigned agent only for the active assignment.
- OTP values are never stored in plaintext. Store a salted hash, expiry, attempt count, purpose, delivery channel, and verification timestamp.
- Evidence and reports use short-lived signed URLs. Access, download, export, deletion, and administrative overrides are audited.
- Data export, account deletion, retention expiry, legal hold, and consent withdrawal are explicit workflows; legal and financial records are retained when required.

## Property Ownership and Taxonomy

- Canonical MVP property categories are `OPEN_PLOT`, `RESIDENTIAL_HOUSE`, `FLAT`, `AGRICULTURAL_LAND`, and `COMMERCIAL_PROPERTY`.
- Legal ownership is separate from user identity. A legal owner may be an individual or an organization, and user accounts receive representative access through a dated authorization record.
- Ownership records store percentage, verification status, effective dates, and source documents. Active ownership percentages must total 100% before a property can request a service.
- Property category changes are versioned and audited; display labels may change without changing the stable category code.

## Quote-Time Distance Pricing

Pricing occurs before assignment and therefore shall use the property's coordinates and the configured coverage-zone or cluster pricing rule. Agent coordinates are not used for the initial customer quote. The quoted price is frozen at payment; the assignment engine may use agent distance for operational optimization only.

## Evidence Lifecycle

Evidence states are `PENDING_UPLOAD`, `UPLOADING`, `AVAILABLE`, `QUARANTINED`, `REJECTED`, and `DELETED`. Uploads require a checksum, media type, capture timestamp, source device ID, and optional GPS accuracy. The system scans uploads for malware, preserves immutable versions, supports resumable retries, strips sensitive EXIF fields before sharing, and records a tamper-evident hash for the delivered artifact.

## Project and Quotation Lifecycle

Approved quotations create a project snapshot containing scope, BOQ version, milestones, assigned vendors, deposits, and accepted price. Projects use `DRAFT`, `APPROVED`, `IN_PROGRESS`, `BLOCKED`, `COMPLETED`, `CANCELLED`, and `WARRANTY` states. Scope changes require a versioned change order and customer approval. Milestone evidence and acceptance are prerequisites for milestone settlement; defects remain linked to the project during the warranty period.

## Subscription Rules

Subscriptions store plan version, billing anchor, timezone, payment authorization reference, and next service date. Renewal retries are idempotent; failed payment enters `PAST_DUE`, retries according to configured policy, then enters `SUSPENDED`. Pause, resume, upgrade, downgrade, proration, missed visits, service credits, and cancellation effective dates are recorded as events. Schedule generation uses a unique key on subscription and occurrence date.

## Operational Baseline

- Target availability: 99.5% monthly for customer and agent APIs.
- Target API latency: P95 under 500 ms for reads and under 1 second for writes, excluding media transfer and external providers.
- Logs, metrics, traces, queue retries, dead-letter items, provider failures, and audit events are monitored with request and correlation IDs.
- Backups are encrypted, tested monthly, and support an RPO of 24 hours and RTO of 8 hours for MVP.
- Deployments support rollback; schema migrations are backward-compatible during rollout.
- Provider outages degrade gracefully: queued notifications, retryable payments, and visible operational incidents are preferred to silent data loss.

## Recorded Product Decisions

- **MVP authentication:** mobile-number OTP is the primary customer and agent login method. Email OTP and password authentication remain future options until product and security owners approve them.
- **Service closure:** quality approval and successful report delivery complete the service. Customer feedback is requested after completion and does not block closure.
- **Geography:** MVP uses coverage zones and clusters. A sub-cluster is not a separate hierarchy level unless introduced by a future migration and API version.
- **ETA units:** ETA rules use calendar days; "one week" and "seven days" are equivalent in MVP configuration.
- **Pricing labels:** customer charges and agent payouts are separate ledgers and every example must identify which ledger it belongs to.