# KYC Operations Playbook

Document Type: KYC Operations Procedure  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This playbook defines the complete Know Your Customer (KYC) operational process for PropertyPilot. It establishes the steps, responsibilities, controls, and standards used to verify identity, reduce fraud risk, maintain regulatory compliance, and support trust across all customer, agent, vendor, and partner categories.

This playbook aligns with PropertyPilot’s customer journey, customer management, security requirements, data governance, and legal/regulatory obligations.

---

## Scope

This playbook applies to:
- Customers
- NRI Customers
- Property Owners
- Agents
- Vendors
- Partners

This playbook covers:
- onboarding and verification
- customer identity validation
- document submission
- automated checks
- manual review
- approval and rejection
- rework and resubmission
- expiry management
- fraud detection
- audit and reporting
- operational SLA and escalation

This playbook applies to KYC workflows across:
- customer registration
- property onboarding
- agent onboarding
- vendor onboarding
- partner verification
- periodic re-verification

---

## KYC Objectives

PropertyPilot’s KYC process is designed to achieve the following objectives:

- Identity Verification
  - Confirm that the person or entity is who they claim to be.
- Fraud Prevention
  - Detect and prevent duplicate, fake, or manipulated identity submissions.
- Regulatory Compliance
  - Meet legal, compliance, and audit obligations for identity verification.
- Trust Establishment
  - Enable confident onboarding and protected customer service flows.

---

## KYC Levels

### Basic KYC

Requirements:
- Mobile Verification
- Email Verification

Use cases:
- low-risk onboarding
- basic customer profiles
- informational or non-financial account access

### Standard KYC

Requirements:
- PAN
- Aadhaar
- Selfie Verification

Use cases:
- customer property engagement
- service and subscription activity
- moderate-risk user onboarding

### Enhanced KYC

Requirements:
- Address Proof
- Video Verification
- Additional Documents

Use cases:
- high-risk profiles
- NRI verification
- suspicious or flagged users
- high-value transactions or regulated scenarios

---

## Customer KYC Workflow

Registration
→ KYC Initiated
→ Document Upload
→ Automated Validation
→ Manual Review
→ Approval / Rejection
→ Activation

Detailed flow:
1. Customer registers account.
2. Customer is prompted to complete KYC.
3. Customer uploads required identity documents.
4. System checks document quality, format, and metadata.
5. Automated validation performs:
   - format checks
   - OCR validation
   - face-match or selfie match
   - duplicate record detection
   - sanction/risk screening
6. If checks pass, case is routed to manual review.
7. Reviewer approves or rejects the case.
8. If approved, account is activated or business access is granted.
9. If rejected, user receives rejection reasons and possible rework path.
10. KYC status is updated and logged.

---

## NRI KYC Workflow

Registration
→ Passport Upload
→ Overseas Address Proof
→ Video Verification
→ Review
→ Approval

Detailed flow:
1. NRI user registers and selects NRI profile.
2. Passport and overseas address proof are uploaded.
3. Video verification is initiated.
4. System performs enhanced validation.
5. Manual reviewer validates identity consistency.
6. Approval allows onboarding and access to eligible services.
7. If rejected, rework instructions are issued.

---

## Agent KYC Workflow

Application
→ Document Upload
→ Background Verification
→ Review
→ Training Validation
→ Activation

Detailed flow:
1. Agent submits application.
2. Agent provides identity and supporting documents.
3. System validates PAN, Aadhaar, and background check status.
4. Review team validates completeness and authenticity.
5. Training completion or skill validation is checked.
6. Agent is approved and activated.
7. If rejected or incomplete, agent is placed into rework or suspended status.

---

## Vendor KYC Workflow

Registration
→ Business Verification
→ GST Validation
→ Bank Verification
→ Approval

Detailed flow:
1. Vendor submits business registration.
2. GST and tax identifiers are validated.
3. Bank proof and business documents are reviewed.
4. Business entity and person identity are verified.
5. Approval grants vendor account access.
6. Rejected vendors can submit updated documents.

---

## Documents Supported

### Customer
- Aadhaar
- PAN
- Passport
- Driving License

### NRI
- Passport
- Visa
- Overseas Address Proof

### Agent
- Aadhaar
- PAN
- Police Verification

### Vendor
- GST Certificate
- PAN
- Bank Proof

### Document Rules
- file format must be supported and valid
- documents must be legible and complete
- multiple documents may be required by level
- all uploads must be stored securely and checksum-verified

---

## Review Process

### Reviewer Assignment
- KYC cases are assigned based on:
  - risk score
  - KYC level
  - document category
  - queue type
  - reviewer specialization
- high-risk or anomalous cases are assigned to senior review queues.

### Checklist
Reviewers validate:
- document authenticity
- personal data consistency
- selfie to ID match
- name and date of birth consistency
- address compliance
- duplicate identity checks
- sanctions or blacklist status
- completeness of required fields

### Approval Rules
A KYC case is approved only when:
- all required documents are present
- all required verifications are complete
- no fraud indicators are detected
- manual review is consistent with policy
- required compliance checks are successful

### Rejection Rules
A KYC case is rejected when:
- document is invalid or unreadable
- mismatch is detected across identity data
- duplicate or suspicious pattern is found
- required document is absent
- verification cannot be completed at a satisfactory confidence level

### Escalation Rules
Escalation is required when:
- risk exceeds policy thresholds
- duplicate identity is suspected
- fraud indicators are found
- documentary inconsistencies remain unresolved
- high-value financial or property onboarding is involved

---

## Rework Workflow

Reject
→ Feedback
→ Resubmission
→ Re-review

Process:
1. Request is rejected with clear reasons.
2. User or business owner receives notification and feedback.
3. Corrected documentation is re-uploaded.
4. Request is requeued for review.
5. Case is re-evaluated under the same or stricter validation standards.
6. Final decision is approved, rejected, or escalated.

---

## Expiry Management

### Expiry Detection
KYC records must be monitored for:
- document expiry
- review expiry
- periodic re-verification deadlines
- account-level KYC refresh

### Renewal Request
When KYC expires:
1. System triggers renewal request.
2. Customer or partner is notified.
3. Required documents are re-submitted.
4. Re-verification flow is executed.

### Re-verification
Re-verification is required for:
- expired KYC
- major profile changes
- suspicious account activity
- risk reclassification
- policy changes or audit findings

---

## Fraud Detection

Examples:
- Duplicate Identity
- Fake Documents
- Blacklisted User

### Fraud Indicators
- repeated document uploads using the same metadata
- mismatched selfie and document data
- sudden profile inconsistency
- use of blacklisted or sanctioned identity
- document tampering or image manipulation
- provider-reported suspicious activity

### Escalation Workflow
1. Fraud signal is generated.
2. Risk case is flagged for manual review.
3. Compliance or security team is notified if threshold is exceeded.
4. Account may be suspended pending investigation.
5. Decision is logged and auditable.

---

## KYC Status Model

| Status | Meaning |
|---|---|
| Draft | user started but did not complete form or upload |
| Submitted | KYC submitted for verification |
| Under Review | manual or automated review is active |
| Approved | verification passed |
| Rejected | verification failed |
| Rework Required | user must correct or re-submit documents |
| Expired | verification validity period ended |
| Suspended | account temporarily restricted pending review |

---

## SLA Matrix

| Process | SLA |
|---|---|
| Submission Review SLA | 24–72 Hours for standard cases |
| Rework SLA | 3–5 Business Days |
| Escalation SLA | 1 Business Day for risk-based escalation |
| NRI Review SLA | 48–96 Hours depending on risk |
| Agent Review SLA | 3 Business Days |
| Vendor Review SLA | 3–5 Business Days |

SLA may vary based on:
- risk category
- regulatory requirement
- volume load
- document verification provider delays

---

## Notifications

PropertyPilot must send notifications for:
- Submission Received
- Approved
- Rejected
- Rework Required
- Expiry Warning

Notification methods:
- SMS
- email
- WhatsApp
- in-app message

Notification content must include:
- status
- reason if rejected
- next action required
- deadline if rework or expiry is pending

---

## Required APIs

- Submit KYC
- Get KYC Status
- Approve KYC
- Reject KYC
- Upload Documents

Additional APIs may be created for:
- rework submission
- reviewer assignment
- document validation status
- fraud escalation
- expiry reminder

---

## Required Screens

- Customer KYC
- KYC Status
- KYC Review
- KYC Rework

Support screens may include:
- NRI KYC
- Agent KYC
- Vendor KYC
- KYC dashboard
- compliance queue

---

## Database Requirements

### Tables
- kyc_profile
- kyc_document
- kyc_verification_result
- kyc_review_case
- kyc_rework_request
- kyc_fraud_alert
- kyc_audit_log

### Relationships
- Customer to KYC profile
- User to document submissions
- KYC profile to review cases
- KYC profile to fraud alerts
- Agent or vendor to assigned KYC records
- Document to verification result

### Audit Requirements
Every KYC action must be auditable:
- submission timestamp
- submitted by
- document metadata
- reviewer identity
- decision and reason code
- escalation actions
- fraud alert actions
- approval or rejection timestamps

---

## Security Requirements

### Encryption
- all KYC documents must be encrypted at rest
- all transfers must use TLS
- temporary storage must be restricted and short-lived
- backups must be encrypted and access-controlled

### Access Control
- only authorized reviewers and compliance roles may access KYC details
- data access must be role-based and auditable
- support teams should have limited access to masked data

### PII Protection
- PII must be masked in logs and non-essential views
- external systems must receive minimum necessary data
- KYC material must not be exposed to unapproved roles

### Document Security
- document checksums must be stored
- file integrity must be validated
- files must be versioned or retained with audit references
- document access must be logged

---

## Reporting Requirements

The KYC process must generate metrics for:
- Approval Rate
- Rejection Rate
- Average Review Time
- Fraud Detection Rate

Additional reports:
- KYC pending count
- rework volume by reason
- NRI vs domestic approval rate
- agent and vendor onboarding completion
- expiry warnings by date
- reviewer productivity and queue distribution

---

## Traceability Matrix

| Journey | Screen | API | Database |
|---|---|---|---|
| Customer Registration | Customer KYC | Submit KYC | kyc_profile, kyc_document |
| Customer Verification | KYC Status | Get KYC Status | kyc_profile, kyc_verification_result |
| Manual Review | KYC Review | Approve KYC / Reject KYC | kyc_review_case, kyc_audit_log |
| Rework | KYC Rework | Upload Documents | kyc_document, kyc_rework_request |
| NRI Verification | NRI KYC | Submit KYC | kyc_profile, kyc_document |
| Agent Onboarding | Agent KYC | Submit KYC | kyc_profile, kyc_review_case |
| Vendor Onboarding | Vendor KYC | Submit KYC | kyc_profile, kyc_document |
| Fraud Detection | KYC Review | Reject KYC / Escalate | kyc_fraud_alert, kyc_audit_log |

---

## Summary

The KYC process is a business-critical compliance and trust function. It must be implemented consistently across customer, NRI, agent, and vendor flows using secure, auditable, and measurable controls. The process must protect personal data, minimize fraud risk, and ensure that onboarding is aligned with legal and operational policy.

This playbook must be used alongside the customer journey, security design, data and API standards, and compliance governance documentation.