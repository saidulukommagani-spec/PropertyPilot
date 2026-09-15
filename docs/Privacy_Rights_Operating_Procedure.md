# Privacy Rights Operating Procedure

Document Type: Privacy Operations Procedure  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This procedure defines how PropertyPilot handles customer privacy rights requests and regulatory compliance. It establishes the operational steps for verifying, approving, fulfilling, and auditing privacy-related requests in a consistent, secure, and legally defensible manner.

This procedure applies to all personal data processed by PropertyPilot across customer, property, payment, subscription, notification, reporting, service, KYC, audit, and partner workflows.

This document supports compliance with applicable privacy laws, contract obligations, internal security standards, and platform governance requirements.

---

## Scope

This procedure applies to:
- Customers
- Property Owners
- NRI Customers
- Agents
- Vendors
- Partners

This procedure covers:
- right to access data
- right to correct data
- right to delete data
- right to withdraw consent
- right to restrict processing
- right to object
- right to data portability
- legal hold processing
- retention enforcement
- privacy request auditing

The procedure applies to data processed in:
- customer and profile systems
- property and document systems
- payment and subscription systems
- service and workflow systems
- notifications and communications
- analytics and reports
- evidence and audit systems
- external partner and provider integrations

---

## Privacy Principles

PropertyPilot follows these principles for all personal data processing:

- Transparency
  - Customers must be informed about what data is collected, why it is used, and with whom it is shared.
- Consent
  - Optional processing and communications require valid, recorded consent and clear withdrawal mechanisms.
- Data Minimization
  - Only data necessary for the stated purpose may be collected and processed.
- Purpose Limitation
  - Data must be used only for approved processing purposes.
- Data Accuracy
  - Personal data must be kept correct, complete, and current.
- Security
  - Data must be protected through encryption, access control, masking, and secure delivery.
- Accountability
  - PropertyPilot must be able to demonstrate how privacy requests are handled, reviewed, and recorded.

---

## Customer Rights

### Right to Access Data

Flow:
Customer Request
→ Validation
→ Data Compilation
→ Export Generation
→ Delivery

SLA:
30 Days

Process:
1. Customer submits access request through verified channel.
2. Support or Privacy Operations validates identity and authority.
3. Data owners identify all required personal data sources.
4. Data is compiled, redacted where legally restricted, and assembled into a secure export.
5. Export is generated in approved format.
6. Export is securely delivered to the verified requester.
7. Delivery is logged and closed with evidence.

### Right to Correct Data

Flow:
Request
→ Verification
→ Update
→ Audit Log

Process:
1. Customer submits correction request with identifying information.
2. Identity and authority are verified.
3. Data owner validates the requested correction.
4. Corrected field values are updated in the system of record.
5. Original value, reason, and correction time are preserved in audit logs.
6. Customer receives confirmation of outcome.

### Right to Delete Data

Flow:
Request
→ Eligibility Validation
→ Legal Hold Check
→ Approval
→ Deletion
→ Confirmation

Process:
1. Customer submits deletion request.
2. System verifies identity and scope.
3. Request is checked against legal hold, retention obligations, and active service dependency rules.
4. If eligible, deletion is approved and executed.
5. If partial deletion is required, non-eligible data is retained with documented exception.
6. Confirmation is sent after completion and audit evidence is retained.

### Right to Withdraw Consent

Flow:
Request
→ Consent Revocation
→ System Update
→ Confirmation

Process:
1. Customer requests withdrawal of consent for a purpose or channel.
2. Consent record is validated and revoked.
3. Associated systems are updated to stop future processing for the affected purpose.
4. Transactional and legal processing may continue where required by law or contract.
5. Confirmation is sent to the customer.

### Right to Restrict Processing

Process:
1. Customer requests processing restriction where accuracy, legality, or objection is disputed.
2. Privacy Operations checks whether the request is valid.
3. Relevant systems restrict processing for the designated subject and purpose.
4. Data remains protected and auditable.
5. Customer is informed of the outcome and any legal exception.

### Right to Object

Process:
1. Customer objects to processing based on legitimate interest, direct marketing, or profiling.
2. Privacy Operations validates objection and scope.
3. Objected processing is restricted or stopped unless required by legal or contractual obligations.
4. The decision is documented and recorded.

### Right to Data Portability

Process:
1. Customer requests a portable export of account data.
2. Eligible data is compiled in machine-readable formats.
3. Export is secured and delivered using verified access.
4. Exports are logged, time-limited, and protected against unauthorized use.

---

## Privacy Request Categories

The following request categories are handled under this procedure:

- Data Export
- Data Correction
- Data Deletion
- Consent Withdrawal
- Restriction
- Objection

Each request category must be tracked through:
- request ID
- request type
- source channel
- requester identity
- verification result
- assignment to owner
- status
- completion evidence
- audit trail

---

## Data Export Specification

The export package must contain eligible data for the relevant customer or authorized representative.

### Included Data Categories

- Profile Data
  - customer_id
  - first_name
  - last_name
  - email
  - mobile_number
  - status
  - created_at
  - updated_at
- Properties
  - property identifiers
  - names
  - ownership type
  - status
  - location metadata
  - associated documents
- Documents
  - property documents
  - customer documents
  - document types
  - file references
  - upload metadata
- Reports
  - generated reports
  - report metadata
  - report dates
  - access history
- Subscriptions
  - plan details
  - billing cycle
  - renewal status
  - invoice history
- Payments
  - payment IDs
  - amounts
  - status
  - invoice references
  - refund history
- Notifications
  - notification history
  - template usage
  - delivery status
  - preferences

### Export Formats

- JSON
  - machine-readable structured data export
- PDF
  - human-readable customer summary export
- CSV
  - tabular structured data for spreadsheet use

### Export Rules
- Exports must be generated only after verification and approval.
- Exports must exclude third-party data not owned by the requester.
- Exports must be encrypted at rest and in transit.
- Exports must include a manifest and redaction summary.
- Downloads must expire after a short, approved time window.

---

## Legal Hold Process

### Purpose

A legal hold preserves specific data and prevents deletion, purge, or modification while a legal, regulatory, compliance, or security matter is active.

### Trigger Conditions

A legal hold is created when:
- litigation or investigation is initiated
- regulatory inquiry is active
- compliance review is ongoing
- fraud or security incident requires preservation
- contractual dispute or audit requires retention
- a court or regulator issues an instruction

### Approval Workflow

1. Legal, Compliance, or Security requests legal hold.
2. Hold owner provides:
   - scope
   - subject(s)
   - reason
   - system(s)
   - date range
   - owner
   - release criteria
3. Privacy Officer validates authority.
4. Hold is created in the privacy system.
5. Data owners and engineering are notified.
6. Purge, deletion, and archival processes are suspended for in-scope data.

### Release Workflow

1. Hold owner submits release request.
2. Privacy Officer validates authority and documentation.
3. Scope is reviewed.
4. Hold is released in the system.
5. Normal retention and deletion workflows resume.
6. Release is logged as a controlled audit event.

Legal holds must be:
- scoped to minimum necessary data
- time-bound where possible
- auditable
- reviewed periodically
- enforceable at the system and process level

---

## Data Retention Matrix

| Data Category | Retention Period | Archive Policy | Purge Policy |
|---|---|---|---|
| Customers | 7 years after account closure | archive and restrict access | purge after legal review |
| Properties | 10 years after retirement or closure | retain in archive | purge with legal approval |
| Reports | 2–5 years depending on use | archive for operational history | purge after retention expiry |
| Evidence | 5–7 years depending on case type | archive in immutable storage | purge only following legal review |
| Payments | 7–10 years depending on regulation | archive in financial system | purge under finance/compliance approval |
| Notifications | 1–2 years depending on purpose | archive delivery metadata | purge after expiry |
| Audit Logs | 7 years minimum | immutable archive | purge only under legal approval |

Retention and deletion are subject to:
- legal hold
- compliance obligations
- security investigation
- active dispute or contractual obligation

---

## Roles and Responsibilities

### Customer
- Submit valid request
- Provide required identity or representative verification
- Follow secure delivery instructions
- Use approved communication channels

### Support Team
- Receive and log the initial request
- Validate basic identity and source channel
- Route to Privacy Operations or appropriate owner
- Escalate risk, legal hold, or security concerns

### Privacy Officer
- Review privacy requests for policy compliance
- Approve legal hold, exception, and high-risk request decisions
- Ensure regulatory and contractual compliance
- Oversee privacy metrics and escalations

### Operations Team
- Compile and validate data from operational systems
- Execute approved deletion, correction, or restriction actions
- Monitor performance against SLA
- Coordinate with engineering and compliance

### Engineering Team
- Implement verified data retrieval, deletion, and masking controls
- Support secure exports and record retention automation
- Ensure logs and system events are privacy-safe
- Maintain auditability of changes and deletes

### Compliance Team
- Review legal obligations and retention schedule
- Validate escalation and exception decisions
- Support audits and regulator inquiries
- Approve high-risk or cross-border processing adjustments

---

## Audit Requirements

### Logging Requirements
Every privacy request must log:
- request_id
- request_type
- request_channel
- requester identity
- verification result
- assigned owner
- source system(s)
- status changes
- approval/rejection reason
- date and time
- legal hold or retention exceptions
- delivery result
- closure evidence

### Evidence Requirements
The system must retain:
- identity verification evidence
- request source and timestamps
- data set used for the decision
- applicable legal or policy basis
- correction or deletion outcome
- exported package manifest
- delivery confirmation
- review and approval records

### Review Requirements
Privacy requests must be reviewed:
- at request closure
- when an exception is applied
- when a legal hold or retention exception is triggered
- during periodic operational review
- during compliance/audit review

Audit records must be:
- append-only
- access restricted
- tamper-resistant
- retained for the required policy period

---

## APIs Required

The following APIs are required for privacy operations:

- Export My Data
- Delete My Data
- Withdraw Consent
- Privacy Request Status

Additional supporting APIs may be added for:
- correction request status
- restriction request status
- legal hold status
- bulk privacy request processing
- privacy audit export

Each API must support:
- authentication and authorization
- request ID correlation
- idempotency
- audit logging
- secure delivery
- error handling
- rate limiting

---

## Database Requirements

### Required Tables

- privacy_request
- privacy_request_audit_log
- privacy_consent
- privacy_consent_history
- legal_hold
- legal_hold_scope
- data_export_job
- data_deletion_job
- privacy_exception
- retention_policy

### Required Fields

For privacy_request:
- privacy_request_id
- customer_id
- request_type
- status
- source_channel
- request_created_at
- verified_by
- verified_at
- approved_by
- approved_at
- completed_at
- actor_user_id
- legal_hold_flag
- notes

For privacy_consent:
- consent_id
- subject_id
- consent_type
- purpose
- channel
- consent_status
- granted_at
- withdrawn_at
- policy_version
- source

For legal_hold:
- legal_hold_id
- subject_type
- subject_id
- reason
- created_by
- created_at
- effective_from
- released_at
- status

### Required Audit Logs
- request actions
- verification outcomes
- reviewer decisions
- system deletion actions
- export generation events
- consent changes
- legal hold events
- exception approvals
- secure delivery events

---

## Security Requirements

### Encryption
- All personal data must be encrypted in transit and at rest.
- Export packages must be encrypted.
- Secure URLs must be short-lived and signed.
- Sensitive data must not be stored in plaintext logs.

### Access Control
- Only authorized users and systems may access privacy request data.
- Row-level or scoped access must be enforced for customer-specific data.
- High-risk operations require separate approval and step-up verification.

### Data Masking
- Mask or redact sensitive fields in logs, traces, and support views.
- Hide or minimize PII in notifications and export manifests.
- Restrict direct display of KYC, payment, and financial data unless explicitly approved.

### Secure Delivery
- Use verified account access, encrypted channels, or approved download links.
- Never send sensitive export content by email, SMS, or WhatsApp.
- Log every access to exported data and revoke access on expiry.

---

## Escalation Process

Escalate privacy requests when:
- identity cannot be verified
- high-risk data export is requested
- legal hold or regulatory instruction applies
- deletion would conflict with audit, retention, or compliance law
- consent withdrawal cannot be propagated to downstream systems
- security breach or anomalous privacy activity is detected

Escalation path:
- Support Team
→ Privacy Operations
→ Privacy Officer
→ Compliance Team / Legal Team / Security Team as required

High-risk or urgent cases must be resolved within the defined timeframe and documented in the privacy request record.

---

## SLA Matrix

| Activity | SLA |
|---|---|
| Acknowledge request | 1 Business Day |
| Identity verification | 3 Business Days |
| Consent withdrawal | 1 Business Day |
| Standard correction | 10 Business Days |
| Access request | 30 Days |
| Delete / restrict decision | 15 Business Days |
| Final fulfilment | 30 Calendar Days |
| Legal hold creation | 4 Business Hours |
| High-risk export review | Immediate with review within 1 Hour |

SLA pause conditions:
- missing or incomplete evidence from requester
- external processor dependency
- legal hold or compliance hold
- regulator or legal instruction

All pauses must be documented with reason and expected resume point.

---

## KPIs

Track and review the following metrics:

- Request Volume
- SLA Compliance
- Deletion Success Rate
- Export Completion Rate
- Correction Completion Rate
- Consent Withdrawal Completion Rate
- Legal Hold Count
- Review Rejection Rate
- Manual Escalation Rate
- Privacy Incident Count
- Data Quality Exceptions

---

## Traceability

This procedure must be traceable to the following:

- Screens
  - Account Settings
  - Profile Management
  - Privacy Center
  - Billing and Subscription
  - Notification Preferences
  - Support Request Portal
- APIs
  - Export My Data
  - Delete My Data
  - Withdraw Consent
  - Privacy Request Status
- Workflows
  - access request workflow
  - correction workflow
  - deletion workflow
  - legal hold workflow
  - consent withdrawal workflow
- Database Tables
  - privacy_request
  - privacy_consent
  - legal_hold
  - data_export_job
  - data_deletion_job
  - privacy_request_audit_log

---

## Summary

PropertyPilot must treat privacy requests as governed, secure, and auditable workflows. Every request must be validated, actioned, logged, and reviewed according to policy and applicable legal obligations. Customer trust depends on consistent implementation of privacy rights, secure processing, and accountable governance.

This procedure is operational guidance and must be interpreted alongside the applicable legal requirements, platform security standards, and regulatory obligations.