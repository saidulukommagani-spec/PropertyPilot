# Schema Completion Backlog

Document Type: Data Model Completion Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for schema and API review

This backlog is derived from:
- Database_Physical_Model.md
- Canonical_Data_Dictionary.md
- Cross_Cutting_Requirements.md

The purpose is to identify database tables that are required to satisfy the documented product, operational, security, and compliance requirements but are not yet represented in the current physical schema.

---

## 1. Scope and Method

The current physical schema is strong in core operational tables, but it does not yet fully cover:
- identity and consent governance
- immutable audit and security event recording
- payment exception handling
- vendor lifecycle and quotation workflows
- service-level tracking and escalation records
- notification delivery and retry status
- data retention and archival policy controls
- bulk import/export management
- report delivery governance
- verification and compliance task tracking

This backlog enumerates only missing tables, not currently modeled tables.

---

## 2. Missing Tables by Domain

## 2.1 Identity, Access, and Consent

### Table: consent_record
Purpose:
Captures consent and privacy preferences for users, tenants, organizations, and document processing events.

Columns:
- consent_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- user_id (UUID, FK nullable)
- consent_type (VARCHAR)
- consent_scope (VARCHAR)
- granted_at (TIMESTAMP)
- revoked_at (TIMESTAMP nullable)
- consent_status (VARCHAR)
- source_channel (VARCHAR)
- source_ip (VARCHAR nullable)
- metadata_json (JSONB nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
- created_by (UUID nullable)
- updated_by (UUID nullable)

PK:
- consent_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- user_id -> app_user.user_id

Indexes:
- idx_consent_org_tenant
- idx_consent_user_status
- idx_consent_type_status
- idx_consent_granted_at

Retention:
- 7 years or per legal/privacy retention policy, whichever is longer

Related APIs:
- POST /consents
- GET /consents/{consentId}
- GET /consents?userId=&tenantId=&orgId=
- PATCH /consents/{consentId}/revoke

---

### Table: access_review
Purpose:
Tracks periodic access reviews and attestation for privileged accounts, roles, and organizational access.

Columns:
- access_review_id (UUID, PK)
- org_id (UUID, FK)
- reviewed_by (UUID, FK)
- review_period_start (TIMESTAMP)
- review_period_end (TIMESTAMP)
- review_status (VARCHAR)
- reviewer_comments (TEXT nullable)
- approved_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- access_review_id

FK:
- org_id -> organization.org_id
- reviewed_by -> app_user.user_id

Indexes:
- idx_access_review_org_status
- idx_access_review_review_period
- idx_access_review_reviewed_by

Retention:
- 5 years

Related APIs:
- POST /access-reviews
- GET /access-reviews
- PATCH /access-reviews/{reviewId}
- GET /access-reviews/{reviewId}/results

---

## 2.2 Audit and Security Events

### Table: immutable_audit_event
Purpose:
Stores tamper-resistant audit events for security, governance, and regulated business actions.

Columns:
- audit_event_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- user_id (UUID, FK nullable)
- entity_type (VARCHAR)
- entity_id (UUID)
- event_type (VARCHAR)
- event_action (VARCHAR)
- event_source (VARCHAR)
- occurred_at (TIMESTAMP)
- request_id (UUID nullable)
- actor_ip (VARCHAR nullable)
- actor_device (VARCHAR nullable)
- old_value_json (JSONB nullable)
- new_value_json (JSONB nullable)
- hash_value (VARCHAR)
- previous_hash (VARCHAR nullable)
- created_at (TIMESTAMP)

PK:
- audit_event_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- user_id -> app_user.user_id

Indexes:
- idx_audit_event_org_time
- idx_audit_event_entity
- idx_audit_event_event_type
- idx_audit_event_hash

Retention:
- 7 years or longer for compliance requirements

Related APIs:
- POST /audit-events
- GET /audit-events
- GET /audit-events?entityType=&entityId=&userId=
- GET /audit-events/export

---

### Table: security_incident
Purpose:
Tracks security incidents, triage status, owner, remediation steps, and closure evidence.

Columns:
- incident_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- incident_type (VARCHAR)
- severity (VARCHAR)
- status (VARCHAR)
- opened_at (TIMESTAMP)
- assigned_to (UUID, FK nullable)
- detected_by (VARCHAR)
- summary (TEXT)
- root_cause (TEXT nullable)
- remediation_plan (TEXT nullable)
- closed_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- incident_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- assigned_to -> app_user.user_id

Indexes:
- idx_incident_org_status
- idx_incident_severity_status
- idx_incident_opened_at

Retention:
- 5 years

Related APIs:
- POST /security-incidents
- GET /security-incidents
- PATCH /security-incidents/{incidentId}
- POST /security-incidents/{incidentId}/close

---

## 2.3 Billing, Finance, and Collections

### Table: payment_adjustment
Purpose:
Captures manual payment adjustments, write-offs, and approved exceptions.

Columns:
- payment_adjustment_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- invoice_id (UUID, FK nullable)
- payment_id (UUID, FK nullable)
- adjustment_type (VARCHAR)
- amount (DECIMAL(18,2))
- reason_code (VARCHAR)
- reason_description (TEXT nullable)
- approved_by (UUID, FK nullable)
- approved_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- payment_adjustment_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- invoice_id -> invoice.invoice_id
- payment_id -> payment.payment_id
- approved_by -> app_user.user_id

Indexes:
- idx_payment_adjustment_invoice
- idx_payment_adjustment_org_status
- idx_payment_adjustment_created_at

Retention:
- 7 years

Related APIs:
- POST /payment-adjustments
- GET /payment-adjustments
- PATCH /payment-adjustments/{adjustmentId}/approve
- GET /payment-adjustments?invoiceId=

---

### Table: payment_dispute
Purpose:
Tracks tenant or finance disputes related to invoices, payment status, or settlement issues.

Columns:
- payment_dispute_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- invoice_id (UUID, FK nullable)
- payment_id (UUID, FK nullable)
- dispute_status (VARCHAR)
- dispute_reason (VARCHAR)
- dispute_amount (DECIMAL(18,2))
- initiated_at (TIMESTAMP)
- resolved_at (TIMESTAMP nullable)
- resolved_by (UUID, FK nullable)
- notes (TEXT nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- payment_dispute_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- invoice_id -> invoice.invoice_id
- payment_id -> payment.payment_id
- resolved_by -> app_user.user_id

Indexes:
- idx_payment_dispute_org_status
- idx_payment_dispute_tenant
- idx_payment_dispute_invoice

Retention:
- 7 years

Related APIs:
- POST /payment-disputes
- GET /payment-disputes
- PATCH /payment-disputes/{disputeId}
- GET /payment-disputes/{disputeId}/history

---

### Table: payment_retry_log
Purpose:
Tracks payment retry attempts, retry policies, and status outcomes.

Columns:
- payment_retry_log_id (UUID, PK)
- org_id (UUID, FK)
- payment_id (UUID, FK)
- retry_count (INT)
- retry_status (VARCHAR)
- retry_reason (VARCHAR)
- next_retry_at (TIMESTAMP nullable)
- attempted_at (TIMESTAMP)
- response_code (VARCHAR nullable)
- response_message (TEXT nullable)
- created_at (TIMESTAMP)

PK:
- payment_retry_log_id

FK:
- org_id -> organization.org_id
- payment_id -> payment.payment_id

Indexes:
- idx_payment_retry_payment
- idx_payment_retry_status_next_retry
- idx_payment_retry_attempted_at

Retention:
- 5 years

Related APIs:
- POST /payments/{paymentId}/retries
- GET /payments/{paymentId}/retries
- GET /payments/retries?status=

---

### Table: billing_proration
Purpose:
Stores plan and invoice proration entries for subscription or billing changes.

Columns:
- proration_id (UUID, PK)
- org_id (UUID, FK)
- subscription_id (UUID, FK)
- invoice_id (UUID, FK nullable)
- proration_type (VARCHAR)
- proration_amount (DECIMAL(18,2))
- from_period (TIMESTAMP)
- to_period (TIMESTAMP)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- proration_id

FK:
- org_id -> organization.org_id
- subscription_id -> subscription.subscription_id
- invoice_id -> invoice.invoice_id

Indexes:
- idx_proration_subscription
- idx_proration_invoice
- idx_proration_period

Retention:
- 7 years

Related APIs:
- POST /subscriptions/{subscriptionId}/prorations
- GET /subscriptions/{subscriptionId}/prorations
- GET /invoices/{invoiceId}/prorations

---

## 2.4 Service Management and SLA Tracking

### Table: service_level_agreement
Purpose:
Defines service level expectations, thresholds, and escalation triggers for maintenance and support work.

Columns:
- sla_id (UUID, PK)
- org_id (UUID, FK)
- property_id (UUID, FK nullable)
- service_type (VARCHAR)
- response_time_minutes (INT)
- resolution_time_minutes (INT)
- escalation_threshold_minutes (INT)
- sla_status (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- sla_id

FK:
- org_id -> organization.org_id
- property_id -> property.property_id

Indexes:
- idx_sla_org_service_type
- idx_sla_property
- idx_sla_status

Retention:
- 5 years

Related APIs:
- POST /slas
- GET /slas
- PATCH /slas/{slaId}
- GET /service-requests/metrics/sla

---

### Table: service_escalation
Purpose:
Tracks incidents where a service or maintenance request exceeds SLA threshold and is escalated.

Columns:
- escalation_id (UUID, PK)
- org_id (UUID, FK)
- service_request_id (UUID, FK)
- escalation_level (INT)
- escalated_by (UUID, FK)
- escalated_at (TIMESTAMP)
- reason_code (VARCHAR)
- notes (TEXT nullable)
- resolved_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)

PK:
- escalation_id

FK:
- org_id -> organization.org_id
- service_request_id -> service_request.service_request_id
- escalated_by -> app_user.user_id

Indexes:
- idx_escalation_service_request
- idx_escalation_org_level
- idx_escalation_escalated_at

Retention:
- 5 years

Related APIs:
- POST /service-requests/{requestId}/escalations
- GET /service-requests/{requestId}/escalations
- GET /escalations?status=

---

### Table: work_order_cost
Purpose:
Stores labor, materials, vendor, and service cost associated with a work order.

Columns:
- work_order_cost_id (UUID, PK)
- org_id (UUID, FK)
- work_order_id (UUID, FK)
- cost_type (VARCHAR)
- vendor_id (UUID, FK nullable)
- labor_cost (DECIMAL(18,2))
- material_cost (DECIMAL(18,2))
- tax_amount (DECIMAL(18,2))
- total_cost (DECIMAL(18,2))
- currency_code (VARCHAR)
- approved_by (UUID, FK nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- work_order_cost_id

FK:
- org_id -> organization.org_id
- work_order_id -> work_order.work_order_id
- vendor_id -> vendor.vendor_id
- approved_by -> app_user.user_id

Indexes:
- idx_work_order_cost_work_order
- idx_work_order_cost_vendor
- idx_work_order_cost_created_at

Retention:
- 5 years

Related APIs:
- POST /work-orders/{workOrderId}/costs
- GET /work-orders/{workOrderId}/costs
- PATCH /work-orders/{workOrderId}/costs/{costId}

---

## 2.5 Vendor and Partner Governance

### Table: vendor
Purpose:
Stores vendor profile, onboarding state, qualifications, and partner linkage.

Columns:
- vendor_id (UUID, PK)
- org_id (UUID, FK)
- vendor_name (VARCHAR)
- vendor_type (VARCHAR)
- onboarding_status (VARCHAR)
- verification_status (VARCHAR)
- approved_by (UUID, FK nullable)
- approved_at (TIMESTAMP nullable)
- rating (DECIMAL(5,2) nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- vendor_id

FK:
- org_id -> organization.org_id
- approved_by -> app_user.user_id

Indexes:
- idx_vendor_org_status
- idx_vendor_name
- idx_vendor_verification_status

Retention:
- 7 years

Related APIs:
- POST /vendors
- GET /vendors
- PATCH /vendors/{vendorId}
- GET /vendors/{vendorId}/performance

---

### Table: vendor_qualification
Purpose:
Stores vendor certifications, skill sets, service categories, and compliance evidence.

Columns:
- vendor_qualification_id (UUID, PK)
- vendor_id (UUID, FK)
- qualification_type (VARCHAR)
- qualification_value (VARCHAR)
- expiry_date (TIMESTAMP nullable)
- verification_status (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- vendor_qualification_id

FK:
- vendor_id -> vendor.vendor_id

Indexes:
- idx_vendor_qualification_vendor
- idx_vendor_qualification_expiry
- idx_vendor_qualification_status

Retention:
- 5 years

Related APIs:
- POST /vendors/{vendorId}/qualifications
- GET /vendors/{vendorId}/qualifications
- PATCH /vendors/{vendorId}/qualifications/{qualificationId}

---

### Table: vendor_quote
Purpose:
Captures vendor quotes, pricing, and approval status for service jobs.

Columns:
- vendor_quote_id (UUID, PK)
- org_id (UUID, FK)
- vendor_id (UUID, FK)
- work_order_id (UUID, FK nullable)
- quote_number (VARCHAR)
- quote_status (VARCHAR)
- quote_amount (DECIMAL(18,2))
- currency_code (VARCHAR)
- submitted_at (TIMESTAMP)
- approved_at (TIMESTAMP nullable)
- approved_by (UUID, FK nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- vendor_quote_id

FK:
- org_id -> organization.org_id
- vendor_id -> vendor.vendor_id
- work_order_id -> work_order.work_order_id
- approved_by -> app_user.user_id

Indexes:
- idx_vendor_quote_vendor
- idx_vendor_quote_work_order
- idx_vendor_quote_status

Retention:
- 5 years

Related APIs:
- POST /vendors/{vendorId}/quotes
- GET /vendors/{vendorId}/quotes
- PATCH /vendor-quotes/{quoteId}/approve
- GET /work-orders/{workOrderId}/quotes

---

## 2.6 Notifications and Delivery

### Table: notification_delivery
Purpose:
Tracks message delivery status and outcome for email, SMS, and WhatsApp events.

Columns:
- notification_delivery_id (UUID, PK)
- org_id (UUID, FK)
- notification_event_id (UUID, FK)
- channel_type (VARCHAR)
- recipient_user_id (UUID, FK nullable)
- recipient_phone (VARCHAR nullable)
- recipient_email (VARCHAR nullable)
- delivery_status (VARCHAR)
- status_code (VARCHAR nullable)
- provider_message_id (VARCHAR nullable)
- sent_at (TIMESTAMP nullable)
- delivered_at (TIMESTAMP nullable)
- failed_at (TIMESTAMP nullable)
- failure_reason (TEXT nullable)
- retry_count (INT)
- created_at (TIMESTAMP)

PK:
- notification_delivery_id

FK:
- org_id -> organization.org_id
- notification_event_id -> notification_event.notification_event_id
- recipient_user_id -> app_user.user_id

Indexes:
- idx_notification_delivery_status
- idx_notification_delivery_channel
- idx_notification_delivery_user
- idx_notification_delivery_sent_at

Retention:
- 2 years

Related APIs:
- POST /notifications/deliveries
- GET /notifications/deliveries
- GET /notifications/{eventId}/deliveries
- PATCH /notifications/deliveries/{deliveryId}/retry

---

### Table: notification_retry
Purpose:
Tracks retry actions for failed notification dispatch attempts.

Columns:
- notification_retry_id (UUID, PK)
- org_id (UUID, FK)
- notification_delivery_id (UUID, FK)
- retry_no (INT)
- attempted_at (TIMESTAMP)
- attempt_result (VARCHAR)
- response_code (VARCHAR nullable)
- created_at (TIMESTAMP)

PK:
- notification_retry_id

FK:
- org_id -> organization.org_id
- notification_delivery_id -> notification_delivery.notification_delivery_id

Indexes:
- idx_notification_retry_delivery
- idx_notification_retry_attempted_at

Retention:
- 2 years

Related APIs:
- POST /notifications/deliveries/{deliveryId}/retries
- GET /notifications/deliveries/{deliveryId}/retries

---

## 2.7 Reporting and Delivery Governance

### Table: report_subscription
Purpose:
Tracks scheduled, subscribed, or shared report delivery configurations.

Columns:
- report_subscription_id (UUID, PK)
- org_id (UUID, FK)
- user_id (UUID, FK)
- report_definition_id (UUID, FK)
- schedule_type (VARCHAR)
- cron_expression (VARCHAR nullable)
- delivery_channel (VARCHAR)
- next_run_at (TIMESTAMP nullable)
- last_run_at (TIMESTAMP nullable)
- active_flag (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- report_subscription_id

FK:
- org_id -> organization.org_id
- user_id -> app_user.user_id
- report_definition_id -> report_definition.report_definition_id

Indexes:
- idx_report_subscription_active
- idx_report_subscription_user
- idx_report_subscription_next_run

Retention:
- 2 years

Related APIs:
- POST /reports/subscriptions
- GET /reports/subscriptions
- PATCH /reports/subscriptions/{subscriptionId}
- DELETE /reports/subscriptions/{subscriptionId}

---

### Table: report_definition
Purpose:
Stores report metadata, ownership, filters, and scheduling metadata.

Columns:
- report_definition_id (UUID, PK)
- org_id (UUID, FK)
- report_name (VARCHAR)
- report_category (VARCHAR)
- report_description (TEXT nullable)
- owner_user_id (UUID, FK)
- query_template (TEXT nullable)
- allowed_roles (JSONB nullable)
- is_system_report (BOOLEAN)
- active_flag (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- report_definition_id

FK:
- org_id -> organization.org_id
- owner_user_id -> app_user.user_id

Indexes:
- idx_report_definition_org_category
- idx_report_definition_owner
- idx_report_definition_active

Retention:
- 5 years

Related APIs:
- POST /reports/definitions
- GET /reports/definitions
- PATCH /reports/definitions/{definitionId}
- GET /reports/definitions/{definitionId}/metadata

---

## 2.8 Verification and Compliance Tasks

### Table: verification_task
Purpose:
Tracks required checks, reviewer assignments, and approval or rejection outcomes for identity, document, or compliance verification.

Columns:
- verification_task_id (UUID, PK)
- org_id (UUID, FK)
- tenant_id (UUID, FK nullable)
- applicant_id (UUID, FK nullable)
- task_type (VARCHAR)
- task_status (VARCHAR)
- priority (VARCHAR)
- assigned_to (UUID, FK nullable)
- due_at (TIMESTAMP nullable)
- reviewed_at (TIMESTAMP nullable)
- reviewed_by (UUID, FK nullable)
- outcome_code (VARCHAR nullable)
- notes (TEXT nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- verification_task_id

FK:
- org_id -> organization.org_id
- tenant_id -> tenant.tenant_id
- applicant_id -> applicant.applicant_id
- assigned_to -> app_user.user_id
- reviewed_by -> app_user.user_id

Indexes:
- idx_verification_task_org_status
- idx_verification_task_tenant
- idx_verification_task_assigned_to
- idx_verification_task_due_at

Retention:
- 5 years

Related APIs:
- POST /verification-tasks
- GET /verification-tasks
- PATCH /verification-tasks/{taskId}
- GET /verification-tasks/{taskId}/history

---

### Table: document_verification_result
Purpose:
Stores outcome for verification of uploaded or required identity and compliance documentation.

Columns:
- doc_verification_result_id (UUID, PK)
- org_id (UUID, FK)
- verification_task_id (UUID, FK)
- document_id (UUID, FK)
- verification_status (VARCHAR)
- verification_rule_code (VARCHAR)
- matched_value (VARCHAR nullable)
- notes (TEXT nullable)
- verified_by (UUID, FK nullable)
- verified_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)

PK:
- doc_verification_result_id

FK:
- org_id -> organization.org_id
- verification_task_id -> verification_task.verification_task_id
- document_id -> document.document_id
- verified_by -> app_user.user_id

Indexes:
- idx_doc_verification_result_task
- idx_doc_verification_result_status
- idx_doc_verification_result_document

Retention:
- 5 years

Related APIs:
- POST /verification-tasks/{taskId}/documents/{documentId}/results
- GET /verification-tasks/{taskId}/results
- PATCH /verification-tasks/{taskId}/results/{resultId}

---

## 2.9 Data Lifecycle and Retention Governance

### Table: retention_policy
Purpose:
Defines retention rules for entity categories, legal hold overrides, and archival actions.

Columns:
- retention_policy_id (UUID, PK)
- org_id (UUID, FK)
- entity_type (VARCHAR)
- retention_period_days (INT)
- archival_required (BOOLEAN)
- legal_hold_flag (BOOLEAN)
- policy_version (VARCHAR)
- effective_from (TIMESTAMP)
- effective_to (TIMESTAMP nullable)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

PK:
- retention_policy_id

FK:
- org_id -> organization.org_id

Indexes:
- idx_retention_policy_org_entity
- idx_retention_policy_effective_dates

Retention:
- 10 years or according to legal policy

Related APIs:
- POST /retention-policies
- GET /retention-policies
- PATCH /retention-policies/{policyId}
- POST /retention-policies/{policyId}/apply

---

### Table: data_archive_job
Purpose:
Tracks archival and purging jobs to ensure retention policy execution.

Columns:
- archive_job_id (UUID, PK)
- org_id (UUID, FK)
- entity_type (VARCHAR)
- archive_status (VARCHAR)
- started_at (TIMESTAMP)
- completed_at (TIMESTAMP nullable)
- records_processed (INT)
- records_archived (INT)
- records_deleted (INT)
- error_message (TEXT nullable)
- created_at (TIMESTAMP)

PK:
- archive_job_id

FK:
- org_id -> organization.org_id

Indexes:
- idx_archive_job_org_status
- idx_archive_job_started_at

Retention:
- 5 years

Related APIs:
- POST /data-archive-jobs
- GET /data-archive-jobs
- PATCH /data-archive-jobs/{jobId}

---

## 2.10 Bulk Import / Export

### Table: import_batch
Purpose:
Tracks structured data imports and validation results across entities like tenants, properties, and documents.

Columns:
- import_batch_id (UUID, PK)
- org_id (UUID, FK)
- import_type (VARCHAR)
- source_name (VARCHAR)
- status (VARCHAR)
- file_name (VARCHAR)
- records_total (INT)
- records_processed (INT)
- records_failed (INT)
- initiated_by (UUID, FK)
- started_at (TIMESTAMP)
- completed_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)

PK:
- import_batch_id

FK:
- org_id -> organization.org_id
- initiated_by -> app_user.user_id

Indexes:
- idx_import_batch_org_status
- idx_import_batch_started_at
- idx_import_batch_type

Retention:
- 2 years

Related APIs:
- POST /imports
- GET /imports
- PATCH /imports/{batchId}
- GET /imports/{batchId}/results

---

### Table: export_batch
Purpose:
Tracks data export jobs including filters, file generation, and download status.

Columns:
- export_batch_id (UUID, PK)
- org_id (UUID, FK)
- export_type (VARCHAR)
- requested_by (UUID, FK)
- format_type (VARCHAR)
- status (VARCHAR)
- filter_json (JSONB nullable)
- file_key (VARCHAR nullable)
- result_count (INT)
- started_at (TIMESTAMP)
- completed_at (TIMESTAMP nullable)
- created_at (TIMESTAMP)

PK:
- export_batch_id

FK:
- org_id -> organization.org_id
- requested_by -> app_user.user_id

Indexes:
- idx_export_batch_org_status
- idx_export_batch_requested_by
- idx_export_batch_started_at

Retention:
- 2 years

Related APIs:
- POST /exports
- GET /exports
- PATCH /exports/{batchId}
- GET /exports/{batchId}/download

---

## 3. Summary of Missing Tables

The schema gap includes the following missing table clusters:

1. Consent and access governance
2. Immutable audit and security incident tracking
3. Payment disputes and adjustments
4. Billing proration and retry logic
5. Service-level agreements and escalations
6. Vendor lifecycle, qualification, and quotation
7. Notification delivery and retry tracking
8. Report definition and report subscription scheduling
9. Verification task and document validation outcomes
10. Retention policy and archive process records
11. Import and export batch management

---

## 4. Recommended Next Steps

1. Add the missing tables to the physical schema backlog.
2. Prioritize the tables needed for MVP:
   - consent_record
   - immutable_audit_event
   - payment_adjustment
   - payment_dispute
   - payment_retry_log
   - service_level_agreement
   - service_escalation
   - verification_task
   - retention_policy
3. Complete API contracts for these tables before implementation.
4. Validate all retention, security, and audit rules against Cross_Cutting_Requirements.md before schema lock.

This backlog is intentionally scoped to missing tables only and does not duplicate tables already present in the current physical model.
