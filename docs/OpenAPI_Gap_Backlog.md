# OpenAPI Gap Backlog

## 1. Gap Summary

This backlog identifies API operations that are referenced by product journeys, screens, workflows, pricing, subscription logic, and integration requirements but are not represented in the current OpenAPI specification.

Total missing APIs identified: 32

| Category | Count | Priority |
|---|---:|---|
| Subscription APIs | 7 | P0 |
| Payment APIs | 5 | P0 |
| KYC APIs | 4 | P0 |
| Notification APIs | 3 | P1 |
| Marketplace APIs | 4 | P1 |
| Construction APIs | 5 | P1 |
| Privacy APIs | 4 | P0 |
| Webhook APIs | 3 | P0 |
| Other supporting APIs | 2 | P2 |

Summary findings:
- The largest gap is in subscription lifecycle, payment lifecycle, and compliance/privacy operations.
- Webhook contracts exist in other documents but are missing from OpenAPI contract coverage.
- Several documented customer journeys imply customer self-service actions that are not modeled in the API spec.
- Several internal operational APIs (milestone approval, change order, legal hold) are referenced but absent from the spec.

---

## 2. Missing API Inventory

## 2.1 Subscription APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Subscription | /api/v1/subscriptions/{subscriptionId}/upgrade | POST | Upgrade an active subscription to a different plan | Subscription plans, customer self-service flow | P0 | `SubscriptionUpgradeRequest { subscriptionId, targetPlanId, effectiveDate, reason?, paymentMethodId? }` | `SubscriptionResponse { subscriptionId, planId, status, effectiveDate, renewalDate, billingSummary }` | JWT + customer authorization | Requires billing recalculation and proration logic |
| Subscription | /api/v1/subscriptions/{subscriptionId}/downgrade | POST | Downgrade a plan | Subscription plans, cancellation flow | P0 | `SubscriptionDowngradeRequest { subscriptionId, targetPlanId, effectiveDate, reason? }` | `SubscriptionResponse { ... }` | JWT + customer authorization | Must preserve entitlement and billing consequences |
| Subscription | /api/v1/subscriptions/{subscriptionId}/pause | POST | Pause a subscription | Subscription management screens | P0 | `SubscriptionPauseRequest { subscriptionId, pauseUntil, reason? }` | `SubscriptionResponse { ... }` | JWT + customer/admin authorization | Requires pause policy enforcement |
| Subscription | /api/v1/subscriptions/{subscriptionId}/resume | POST | Resume a paused subscription | User account flows | P0 | `SubscriptionResumeRequest { subscriptionId }` | `SubscriptionResponse { ... }` | JWT + customer authorization | Must maintain existing renewal state |
| Subscription | /api/v1/subscriptions/{subscriptionId}/cancel | POST | Cancel a subscription | Cancellation journey, self-service account | P0 | `SubscriptionCancelRequest { subscriptionId, cancelAt, reason, refundEligible }` | `SubscriptionCancelResponse { subscriptionId, status, cancelAt, refundStatus }` | JWT + customer authorization | Must support legal retention and final billing |
| Subscription | /api/v1/subscriptions/{subscriptionId}/renew | POST | Manually renew or trigger renewal | Billing workflow | P0 | `SubscriptionRenewRequest { subscriptionId, billingCycle, paymentMethodId? }` | `SubscriptionRenewalResponse { renewalId, status, amount, invoiceId }` | JWT + customer/admin authorization | Must be idempotent |
| Subscription | /api/v1/subscriptions/{subscriptionId}/auto-renewal | PATCH | Update auto-renewal preference | Account settings, billing consent | P0 | `AutoRenewalConsentRequest { subscriptionId, enabled, consentTimestamp, consentSource }` | `AutoRenewalConsentResponse { subscriptionId, enabled, updatedAt }` | JWT + customer authorization | Required for consent capture and compliance |

## 2.2 Payment APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Payment | /api/v1/payments/{paymentId}/refunds | POST | Request a refund for a payment | Payment history, billing disputes | P0 | `RefundRequest { paymentId, amount, currency, reason, notes?, idempotencyKey }` | `RefundResponse { refundId, paymentId, status, amount, createdAt }` | JWT + customer/admin authorization + idempotency key | Must support financial reconciliation |
| Payment | /api/v1/payments/{paymentId}/refunds/{refundId} | GET | Fetch refund status | Payment dashboard, customer support | P0 | none | `RefundStatusResponse { refundId, paymentId, status, processedAt, failureReason? }` | JWT + authorization | Required for dispute and support workflows |
| Payment | /api/v1/payments/chargebacks | POST | Receive or create chargeback notification workflow | Finance and support operations | P0 | `ChargebackNotificationRequest { paymentId, chargebackId, reasonCode, amount, occurredAt }` | `ChargebackNotificationResponse { accepted, status, correlationId }` | JWT/internal service auth + webhook verification | may also be in webhook spec but needs API contract for internal processing |
| Payment | /api/v1/payments/reconciliation | POST | Trigger reconciliation process | Finance operations, month-end processing | P0 | `ReconciliationRequest { fromDate, toDate, provider, currency, includeUnmatched }` | `ReconciliationResponse { jobId, status, totals, recordsProcessed }` | JWT + admin authorization | Async process with job status endpoint |
| Payment | /api/v1/invoices/{invoiceId}/download | GET | Download invoice | Billing screens, customer self-service | P0 | none | `InvoiceDownloadResponse { invoiceId, fileUrl, contentType, expiresAt }` | JWT + customer authorization | Must support signed URLs and retrieval security |

## 2.3 KYC APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| KYC | /api/v1/kyc | POST | Submit KYC application | Customer onboarding, verification journeys | P0 | `KycSubmissionRequest { customerId, documentType, documentFileUrl, selfieFileUrl?, consentAccepted }` | `KycSubmissionResponse { kycId, status, submittedAt, reviewerQueueId? }` | JWT + customer authorization | Must support document verification and consent |
| KYC | /api/v1/kyc/{kycId}/review | POST | Review a KYC submission | Internal verification workflow | P0 | `KycReviewRequest { kycId, decision, reviewerId, notes, riskScore? }` | `KycReviewResponse { kycId, status, reviewerId, decision, reviewedAt }` | JWT + admin/operations authorization | Required for compliance workflow |
| KYC | /api/v1/kyc/{kycId}/reject | POST | Reject KYC application | Compliance and onboarding flow | P0 | `KycRejectRequest { kycId, reasonCode, reasonText, requestedResubmission }` | `KycRejectResponse { kycId, status, reasonCode, rejectedAt }` | JWT + admin authorization | Must capture audit trail |
| KYC | /api/v1/kyc/{kycId}/rework | POST | Return KYC to rework queue | Operations and customer remediation | P0 | `KycReworkRequest { kycId, reasonCode, reworkInstructions, dueBy }` | `KycReworkResponse { kycId, status, reworkQueue, dueBy }` | JWT + admin/operations authorization | Required for onboarding remediation |

## 2.4 Notification APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Notification | /api/v1/notifications/{notificationId}/delivery-status | GET | Fetch notification delivery state | notification dashboard, support | P1 | none | `DeliveryStatusResponse { notificationId, channel, status, providerStatus, deliveredAt?, failedReason? }` | JWT + customer/admin authorization | Required for operational support |
| Notification | /api/v1/notifications/{notificationId}/retry | POST | Retry failed notification delivery | support tools, operations | P1 | `NotificationRetryRequest { notificationId, channel?, reason? }` | `NotificationRetryResponse { notificationId, status, retryCount, nextAttemptAt }` | JWT + admin/operations authorization | Must be idempotent and auditable |
| Notification | /api/v1/users/{userId}/notification-preferences | GET/PATCH | Get and update notification preferences | customer settings, app screens | P1 | `NotificationPreferencesRequest { channels, frequency, optIn, quietHours }` | `NotificationPreferencesResponse { userId, channels, frequency, optIn, quietHours }` | JWT + user authorization | Required for consent governance |

## 2.5 Marketplace APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Marketplace | /api/v1/marketplace/quotes/requests | POST | Request quote from vendor(s) | vendor marketplace flow | P1 | `QuoteRequest { propertyId, serviceId, requestId, requirements, budget?, deadline }` | `QuoteRequestResponse { requestId, quotedVendorCount, status, expiresAt }` | JWT + customer/agent authorization | Required for vendors and comparison flow |
| Marketplace | /api/v1/marketplace/quotes/compare | POST | Compare vendor quotes | quote selection screens | P1 | `QuoteComparisonRequest { quoteIds, comparisonCriteria }` | `QuoteComparisonResponse { quoteIds, rankedQuotes, scoringSummary }` | JWT + customer/agent authorization | Must support scoring logic |
| Marketplace | /api/v1/vendors/{vendorId}/approve | POST | Approve vendor for active engagement | vendor review flow | P1 | `VendorApprovalRequest { vendorId, approvedBy, decisionReason, approvalWindow }` | `VendorApprovalResponse { vendorId, status, approvedAt }` | JWT + admin authorization | Required for vendor compliance / onboarding |
| Marketplace | /api/v1/service-requests/{serviceRequestId}/vendor-assignment | POST | Assign approved vendor to service request | operations workflow | P1 | `VendorAssignmentRequest { serviceRequestId, vendorId, assignmentReason, priority, scheduledAt }` | `VendorAssignmentResponse { assignmentId, serviceRequestId, vendorId, status }` | JWT + operations/admin authorization | Must support audit and SLA tracking |

## 2.6 Construction APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Construction | /api/v1/projects | POST | Create a construction project | project management workflows | P1 | `ProjectCreateRequest { projectName, propertyId, ownerId, startDate, budget, status }` | `ProjectResponse { projectId, projectName, propertyId, status, createdAt }` | JWT + operations/admin authorization | Required for project lifecycle |
| Construction | /api/v1/projects/{projectId}/milestones | POST | Create project milestone | milestone planning | P1 | `ProjectMilestoneCreateRequest { projectId, name, dueDate, amount, status }` | `ProjectMilestoneResponse { milestoneId, projectId, status, dueDate }` | JWT + operations authorization | Needed for construction milestone approvals |
| Construction | /api/v1/projects/{projectId}/milestones/{milestoneId}/approve | POST | Approve milestone | construction approval workflow | P1 | `MilestoneApprovalRequest { projectId, milestoneId, approvedBy, approvalNotes }` | `MilestoneApprovalResponse { milestoneId, status, approvedAt }` | JWT + admin/operations authorization | Must support evidence and approval trace |
| Construction | /api/v1/projects/{projectId}/change-orders | POST | Create change order | construction variation workflow | P1 | `ChangeOrderCreateRequest { projectId, reason, amountDelta, approvedBy, effectiveDate }` | `ChangeOrderResponse { changeOrderId, projectId, amountDelta, status }` | JWT + operations/admin authorization | Requires finance and approval trace |
| Construction | /api/v1/projects/{projectId}/warranty-claims | POST | Create warranty claim | warranty management | P1 | `WarrantyClaimRequest { projectId, claimType, issueDescription, evidenceIds, claimedAmount }` | `WarrantyClaimResponse { claimId, projectId, status, submittedAt }` | JWT + customer/operations authorization | Must connect to evidence evidence set |

## 2.7 Privacy APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Privacy | /api/v1/privacy/data-export | POST | Export user data | GDPR, privacy settings flow | P0 | `DataExportRequest { userId, dataScope, format, includeAuditLogs }` | `DataExportResponse { exportJobId, status, downloadUrl?, expiresAt }` | JWT + user authorization | Must support legal retention and redaction |
| Privacy | /api/v1/privacy/data-delete | POST | Delete user data | privacy account deletion flow | P0 | `DataDeleteRequest { userId, reason, confirm, legalHoldApplied }` | `DataDeleteResponse { deletionJobId, status, startedAt }` | JWT + user/admin authorization | Must support retention exception handling |
| Privacy | /api/v1/privacy/consent-withdrawal | POST | Withdraw consent | consent management | P0 | `ConsentWithdrawalRequest { subjectType, subjectId, consentType, reason, effectiveAt }` | `ConsentWithdrawalResponse { consentId, status, withdrawnAt }` | JWT + user/admin authorization | Required for compliance and rights processing |
| Privacy | /api/v1/privacy/legal-hold | POST | Apply legal hold | compliance and litigation hold | P0 | `LegalHoldRequest { subjectType, subjectId, reason, holdType, effectiveFrom, expiresAt? }` | `LegalHoldResponse { holdId, status, effectiveFrom, expiresAt }` | JWT + admin/legal authorization | Must be immutable with audit logging |

## 2.8 Webhook APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Webhook | /api/v1/webhooks/payments | POST | Receive payment gateway callback events | payment provider integrations | P0 | `PaymentWebhookEvent { eventId, eventType, provider, paymentId, amount, currency, status, occurredAt, signature }` | `WebhookAcknowledgementResponse { received, eventId, status }` | HMAC + timestamp + idempotency | Must be in OpenAPI and API governance |
| Webhook | /api/v1/webhooks/sms | POST | Receive SMS delivery status events | SMS provider integration | P0 | `SmsWebhookEvent { eventId, eventType, provider, messageId, status, recipient, occurredAt }` | `WebhookAcknowledgementResponse { ... }` | HMAC + timestamp + replay validation | Must align with notification provider contract |
| Webhook | /api/v1/webhooks/whatsapp | POST | Receive WhatsApp delivery status events | WhatsApp integration | P0 | `WhatsAppWebhookEvent { eventId, eventType, provider, messageId, status, recipient, occurredAt }` | `WebhookAcknowledgementResponse { ... }` | HMAC + timestamp + replay validation | Should be versioned and signed |

## 2.9 Additional Missing Support APIs

| Domain | Endpoint | Method | Purpose | Referenced By | Priority | Required Request Model | Required Response Model | Security Requirement | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Report | /api/v1/reports/{reportId}/export | POST | Trigger export for report generation | reporting workflows | P2 | `ReportExportRequest { reportId, format, filters, includeMetadata }` | `ReportExportResponse { jobId, status, downloadUrl?, expiresAt }` | JWT + role-based access | Needed for report pipeline orchestration |
| Service | /api/v1/service-requests/{serviceRequestId}/escalate | POST | Escalate a service request | service operations, SLA management | P2 | `ServiceEscalationRequest { serviceRequestId, reason, escalatedBy, priority, dueBy }` | `ServiceEscalationResponse { escalationId, serviceRequestId, status, escalatedAt }` | JWT + operations authorization | Required for SLA governance |

---

## 3. OpenAPI Implementation Backlog

### Priority P0: must be in next OpenAPI release
1. Subscription lifecycle APIs
   - upgrade, downgrade, pause, resume, cancel, renew, auto-renewal
2. Payment lifecycle and compliance APIs
   - refund request and status
   - chargeback notification
   - reconciliation
   - invoice download
3. KYC lifecycle APIs
   - submit, review, reject, rework
4. Privacy lifecycle APIs
   - data export, data delete, consent withdrawal, legal hold
5. Webhook callback APIs
   - payment, SMS, WhatsApp

### Priority P1: required before production operational readiness
1. Notification delivery APIs
   - delivery status
   - retry
   - notification preferences
2. Marketplace vendor APIs
   - request quote
   - compare quotes
   - approve vendor
   - vendor assignment
3. Construction workflow APIs
   - project, milestone, approval, change order, warranty claim

### Priority P2: required for full platform completeness
1. Report export APIs
2. Service escalation APIs
3. Additional UI-driven operational actions discovered during screen and journey analysis

### Implementation backlog standards
- Every endpoint must include:
  - operationId
  - summary and description
  - tags
  - security scheme
  - request schema
  - response schema
  - error responses
  - idempotency metadata
  - correlation ID examples
- All financial and consent operations must include idempotency keys.
- All callbacks must include webhook security and replay handling.
- All customer-facing APIs must include auth policy and scope.

---

## 4. Recommended Release Sequence

### Release 1: Critical Business and Compliance APIs
Focus: customer trust, billing correctness, legal compliance

- Subscription lifecycle APIs
- Payment refund APIs
- Invoice download
- KYC lifecycle APIs
- Privacy APIs
- Payment webhook callbacks

Rationale:
- These APIs affect customer lifecycle, billing integrity, legal obligations, and onboarding compliance.
- They are P0 and directly affect core risk, revenue, and compliance treatment.

### Release 2: Operational and Communication APIs
Focus: service operations and customer communication

- Notification delivery status and retry
- Notification preferences
- SMS and WhatsApp webhooks
- Service escalation
- Report export APIs

Rationale:
- These APIs support day-to-day operations, SLA management, report consumption, and vendor/customer communication.

### Release 3: Marketplace and Construction APIs
Focus: extended business workflows and partner operations

- marketplace quote request and comparison
- vendor approval and assignment
- project creation and milestone workflow
- change order and warranty claim

Rationale:
- These APIs support growth workflows and operational project execution but do not directly block initial product operations.

### Release 4: Final API Completeness Sweep
Focus: completeness, cleanup, and deprecation alignment

- finalize missing UI-driven endpoints
- align all descriptions and examples with canonical data dictionary
- validate against Traceability Matrix
- remove duplicate or legacy endpoint definitions
- review for versioned contract parity with API governance rules

---

## 5. Recommended Backlog Ownership

| Area | Suggested Owner |
|---|---|
| Subscription APIs | Product + Billing Platform |
| Payment APIs | Finance Platform + API Governance |
| KYC APIs | Verification Platform + Compliance |
| Notification APIs | Notification Platform |
| Marketplace APIs | Operations + Vendor Management |
| Construction APIs | Project Operations + Architecture |
| Privacy APIs | Security + Compliance |
| Webhook APIs | Integration Platform + Security |

---

## 6. Acceptance Criteria for Closure

The OpenAPI gap backlog is considered closed when:
- Every listed API exists in OpenAPI_Specification.yaml
- Every endpoint has request/response schema definitions
- Every endpoint has security requirements
- Every operation is linked to a source customer journey, screen, or workflow
- Idempotency is specified for financial and webhook operations
- Error responses are defined consistently
- Versioning and path conventions match /api/v1
- The spec passes governance review and integration validation

---

## 7. Final Governance Note

This backlog is derived from the present documentation set and is intended to close the current OpenAPI contract gap between the documented product requirements and the canonical API specification. It should be treated as a required implementation backlog for the next API alignment milestone and should be reviewed alongside:
- API_Contract_Governance.md
- Cross_Cutting_Requirements.md
- Traceability_Matrix.md
- Screen_Catalog.md
- Screen_Flows.md
- Customer_Journeys.md