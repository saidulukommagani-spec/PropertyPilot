# PropertyPilot State Model Catalog

## Version

1.0

## Purpose

This catalog defines the canonical lifecycle states for major PropertyPilot entities. It applies to all APIs, workflows, event consumers, database constraints, screens, and integrations.

[Cross_Cutting_Requirements.md](Cross_Cutting_Requirements.md) is the governing source when another document defines a conflicting state, transition, or transition rule.

---

# Global Transition Rules

1. State transitions shall be atomic, idempotent, authorization checked, and recorded in an entity-specific status-history record.
2. A request may transition only from its persisted current state. Invalid transitions return HTTP 409 with a stable state-conflict code.
3. Terminal states cannot transition further unless an explicitly documented recovery transition is listed in this catalog.
4. Each transition shall record actor, role, timestamp, reason, correlation ID, and source channel; regulated or financial transitions shall also record required approval evidence.
5. Asynchronous consumers shall validate transition order and ignore a duplicate or stale event without repeating side effects.

---

# Customer States

## States

`PENDING_VERIFICATION`, `ACTIVE`, `SUSPENDED`, `DEACTIVATED`, `DELETED`

## Valid Transitions

| From | To |
|---|---|
| `PENDING_VERIFICATION` | `ACTIVE`, `DEACTIVATED` |
| `ACTIVE` | `SUSPENDED`, `DEACTIVATED` |
| `SUSPENDED` | `ACTIVE`, `DEACTIVATED` |
| `DEACTIVATED` | `ACTIVE`, `DELETED` |

## Terminal States

`DELETED`

## Transition Rules

- `ACTIVE` requires required registration and verification checks.
- `SUSPENDED` requires a security, fraud, policy, or authorised administrative reason.
- `DEACTIVATED` blocks new authenticated business activity but retains data according to retention policy.
- `DELETED` represents completed authorised deletion or anonymisation; legal, financial, audit, and legal-hold records remain retained when required.

---

# Property States

## States

`DRAFT`, `PENDING_VERIFICATION`, `ACTIVE`, `INACTIVE`, `REJECTED`, `ARCHIVED`

## Valid Transitions

| From | To |
|---|---|
| `DRAFT` | `PENDING_VERIFICATION`, `ARCHIVED` |
| `PENDING_VERIFICATION` | `ACTIVE`, `DRAFT`, `REJECTED`, `ARCHIVED` |
| `ACTIVE` | `INACTIVE`, `ARCHIVED` |
| `INACTIVE` | `ACTIVE`, `ARCHIVED` |
| `REJECTED` | `DRAFT`, `ARCHIVED` |

## Terminal States

`ARCHIVED`

## Transition Rules

- A property may become `ACTIVE` only when required property, coverage, and ownership validation succeeds.
- Property lifecycle is independent of marketplace-listing status.
- Ownership, category, and lifecycle changes are versioned and audit logged.
- `INACTIVE`, `REJECTED`, and `ARCHIVED` properties cannot create new services unless reactivated through a permitted transition.

---

# Service Request States

## States

`NEW`, `PENDING_PAYMENT`, `PAYMENT_COMPLETED`, `PENDING_ASSIGNMENT`, `ASSIGNED`, `ACCEPTED`, `IN_PROGRESS`, `REPORT_SUBMITTED`, `UNDER_REVIEW`, `COMPLETED`, `CANCELLED`, `FAILED`, `ESCALATED`

## Valid Transitions

| From | To |
|---|---|
| `NEW` | `PENDING_PAYMENT`, `FAILED` |
| `PENDING_PAYMENT` | `PAYMENT_COMPLETED`, `CANCELLED` |
| `PAYMENT_COMPLETED` | `PENDING_ASSIGNMENT` |
| `PENDING_ASSIGNMENT` | `ASSIGNED`, `ESCALATED` |
| `ASSIGNED` | `ACCEPTED`, `PENDING_ASSIGNMENT`, `ESCALATED` |
| `ACCEPTED` | `IN_PROGRESS`, `ESCALATED` |
| `IN_PROGRESS` | `REPORT_SUBMITTED`, `ESCALATED` |
| `REPORT_SUBMITTED` | `UNDER_REVIEW`, `ESCALATED` |
| `UNDER_REVIEW` | `COMPLETED`, `IN_PROGRESS`, `ESCALATED` |
| Any non-terminal state | `ESCALATED`, `CANCELLED` when authorised by policy |

## Terminal States

`COMPLETED`, `CANCELLED`, `FAILED`

## Transition Rules

- `NEW` to `PENDING_PAYMENT` requires coverage and service-eligibility validation.
- `PAYMENT_COMPLETED` requires a verified, idempotent payment-provider event.
- `ASSIGNED` requires an active and eligible agent or vendor; rejection or timeout returns the request to `PENDING_ASSIGNMENT`.
- `IN_PROGRESS` requires required start checks, including GPS where applicable.
- `REPORT_SUBMITTED` requires the configured evidence and report validation; `COMPLETED` requires quality approval or approved automation.
- `ESCALATED` is a non-terminal overlay for SLA breach, dispute, or risk and must retain the underlying operational context until resolution.

---

# Visit States

## States

`SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, `FAILED`

## Valid Transitions

| From | To |
|---|---|
| `SCHEDULED` | `IN_PROGRESS`, `CANCELLED`, `FAILED` |
| `IN_PROGRESS` | `COMPLETED`, `FAILED`, `CANCELLED` |

## Terminal States

`COMPLETED`, `CANCELLED`, `FAILED`

## Transition Rules

- A visit requires an active, accepted assignment and an eligible service request.
- `IN_PROGRESS` requires an authorised assignee and required start evidence or location validation where configured.
- `COMPLETED` requires configured completion evidence, timestamps, and observations; it does not itself consume a subscription entitlement until service-completion acceptance rules are met.
- `CANCELLED` and `FAILED` require reason, actor, financial/entitlement effect, and rescheduling decision where applicable.

---

# Report States

## States

`DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `DELIVERED`, `REJECTED`, `ARCHIVED`

## Valid Transitions

| From | To |
|---|---|
| `DRAFT` | `SUBMITTED`, `ARCHIVED` |
| `SUBMITTED` | `UNDER_REVIEW`, `REJECTED` |
| `UNDER_REVIEW` | `APPROVED`, `REJECTED` |
| `REJECTED` | `DRAFT`, `ARCHIVED` |
| `APPROVED` | `DELIVERED`, `ARCHIVED` |
| `DELIVERED` | `ARCHIVED` |

## Terminal States

`ARCHIVED`

## Transition Rules

- `SUBMITTED` requires the report's configured evidence, source request, author, and report type.
- `APPROVED` requires an authorised reviewer or approved automated control.
- `DELIVERED` requires access-controlled publication to authorised recipients.
- Corrections create a new report version; prior delivered versions remain retained and auditable.

---

# Subscription States

## States

`PENDING`, `ACTIVE`, `PAUSED`, `PAST_DUE`, `SUSPENDED`, `EXPIRED`, `CANCELLED`

## Valid Transitions

| From | To |
|---|---|
| `PENDING` | `ACTIVE`, `CANCELLED` |
| `ACTIVE` | `PAUSED`, `PAST_DUE`, `SUSPENDED`, `EXPIRED`, `CANCELLED` |
| `PAUSED` | `ACTIVE`, `SUSPENDED`, `EXPIRED`, `CANCELLED` |
| `PAST_DUE` | `ACTIVE`, `SUSPENDED`, `EXPIRED`, `CANCELLED` |
| `SUSPENDED` | `ACTIVE`, `EXPIRED`, `CANCELLED` |
| `EXPIRED` | `ACTIVE`, `CANCELLED` |

## Terminal States

`CANCELLED`

## Transition Rules

- `ACTIVE` requires payment confirmation, activation validation, and a retained plan version.
- Only `ACTIVE` subscriptions generate new recurring service requests.
- Failed renewal enters `PAST_DUE`; retry, grace, and suspension rules are configured and audit logged.
- A pause blocks new schedule generation but preserves history; downgrade, cancellation, and expiry retain effective dates and entitlement effects.
- Renewal from `EXPIRED` creates an auditable renewal event and does not duplicate schedule occurrences.

---

# Payment States

## States

`PENDING`, `AUTHORIZED`, `PROCESSING`, `SUCCESS`, `FAILED`, `PARTIAL_REFUND`, `REFUNDED`, `CANCELLED`, `EXPIRED`

## Valid Transitions

| From | To |
|---|---|
| `PENDING` | `AUTHORIZED`, `PROCESSING`, `FAILED`, `CANCELLED`, `EXPIRED` |
| `AUTHORIZED` | `PROCESSING`, `FAILED`, `CANCELLED`, `EXPIRED` |
| `PROCESSING` | `SUCCESS`, `FAILED`, `CANCELLED`, `EXPIRED` |
| `SUCCESS` | `PARTIAL_REFUND`, `REFUNDED` |
| `PARTIAL_REFUND` | `PARTIAL_REFUND`, `REFUNDED` |

## Terminal States

`FAILED`, `REFUNDED`, `CANCELLED`, `EXPIRED`

## Transition Rules

- Payment transitions require a verified provider event and an immutable provider reference.
- A payment intent and all financial mutations use idempotency controls; replay must not create a duplicate charge or refund.
- `SUCCESS` creates the approved invoice and downstream service or subscription activation activity.
- Refund transitions require authorised eligibility, reason, amount validation, and reconciliation to the original payment.
- A partial refund may repeat only while cumulative refunds do not exceed the captured amount.

---

# Complaint States

## States

`NEW`, `ASSIGNED`, `UNDER_REVIEW`, `INVESTIGATION`, `RESOLUTION_PROPOSED`, `RESOLVED`, `CLOSED`, `CANCELLED`

## Valid Transitions

| From | To |
|---|---|
| `NEW` | `ASSIGNED`, `CANCELLED` |
| `ASSIGNED` | `UNDER_REVIEW`, `CANCELLED` |
| `UNDER_REVIEW` | `INVESTIGATION`, `RESOLUTION_PROPOSED`, `CANCELLED` |
| `INVESTIGATION` | `RESOLUTION_PROPOSED`, `CANCELLED` |
| `RESOLUTION_PROPOSED` | `RESOLVED`, `INVESTIGATION`, `CANCELLED` |
| `RESOLVED` | `CLOSED`, `INVESTIGATION` |

## Terminal States

`CLOSED`, `CANCELLED`

## Transition Rules

- High and critical fraud complaints automatically escalate under the complaint policy.
- Assignment, investigation findings, proposed resolution, customer response where required, and closure reason are audit logged.
- Reopening a resolved complaint requires an authorised reason and returns it to `INVESTIGATION`; a closed complaint requires a new linked complaint unless policy permits reopening.
- Complaint state does not change the related service or payment state without the authorised workflow for that entity.

---

# Vendor Assignment States

## States

`ASSIGNED`, `ACCEPTED`, `IN_PROGRESS`, `PENDING_REVIEW`, `REWORK_REQUIRED`, `COMPLETED`, `CANCELLED`

## Valid Transitions

| From | To |
|---|---|
| `ASSIGNED` | `ACCEPTED`, `CANCELLED` |
| `ACCEPTED` | `IN_PROGRESS`, `CANCELLED` |
| `IN_PROGRESS` | `PENDING_REVIEW`, `CANCELLED` |
| `PENDING_REVIEW` | `COMPLETED`, `REWORK_REQUIRED`, `CANCELLED` |
| `REWORK_REQUIRED` | `IN_PROGRESS`, `CANCELLED` |

## Terminal States

`COMPLETED`, `CANCELLED`

## Transition Rules

- An assignment may be created only for an active vendor with valid agreement, verification, service mapping, coverage, availability, and capacity.
- `ACCEPTED` requires vendor acceptance within the configured response SLA.
- `IN_PROGRESS` requires required start evidence and timestamps where configured.
- `PENDING_REVIEW` requires completion evidence, deliverables, and invoice information where payment terms require it.
- `COMPLETED` requires operations or customer acceptance and quality, SLA, and commercial checks; `REWORK_REQUIRED` records reason, owner, and due date.
- `CANCELLED` records reason, financial effect, and rescheduling outcome.

---

# Related Documents

- Cross_Cutting_Requirements.md
- Subscription_Management.md
- Payment_Billing_Engine.md
- Complaint_Dispute_Management.md
- Vendor_Management.md
- Service_Workflow.md
- Workflow_Engine.md
