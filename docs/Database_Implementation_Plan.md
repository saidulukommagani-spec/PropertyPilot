````markdown
# Database Implementation Plan

Document Type: Database Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Database Architecture / Backend Architecture / Platform Engineering

---

# Purpose

This document converts the PropertyPilot database documentation set into an implementation-ready production database blueprint. It aligns the canonical data dictionary, physical model, schema backlog, traceability matrix, and cross-cutting requirements into a coherent implementation plan for PostgreSQL and supporting storage services.

The purpose is to:
- confirm the current database state
- identify missing and partial schema components
- resolve model conflicts and gaps
- define the entity implementation roadmap by domain
- establish Flyway migration sequencing
- create index, partition, and retention rules
- finalize data integrity and audit standards
- define a delivery roadmap ready for backend implementation and production deployment

---

# Current State Assessment

## Implemented Tables
The repository indicates a strong logical data model and a physical schema direction, with clear evidence of structured domain modeling. Based on the available documentation and design artifacts, the following are likely to be either already modeled or partially represented:

- users
- roles
- permissions
- customer profiles
- properties
- property ownership records
- service catalog items
- service requests
- service request history
- subscriptions
- payment records
- invoice records
- notification templates
- notifications
- complaints
- audit logs
- admin configuration records

These tables are typically found in an early-to-mid-stage implementation baseline and are suitable for a controlled production progression.

## Missing Tables
The following areas are likely under-modeled or not fully translated into SQL migrations:

- user sessions and refresh tokens
- property verification and approval chain
- evidence attachments and media metadata
- visit scheduling and onsite visit records
- agent assignment state and capacity records
- escalation records
- payment provider transaction metadata
- report jobs and exports
- KYC verification records
- vendor master data
- project management records
- construction activity records
- marketplace listing and inquiry tables
- notification delivery and preference tables
- archival tables for historical retention
- outbox tables for event reliability

## Partial Tables
The following domain objects are likely only partially complete in current design artifacts:
- payment states and retries
- workflow state transitions
- audit log detail payloads
- notification delivery status
- service evidence lifecycle
- complaint thread / resolution state
- user permission mappings
- dashboard/report data models
- configuration versioning

## Conflicting Models
Likely areas of tension between modeled and operational requirements:
- physical model vs. migration history may differ in naming conventions
- domain lifecycle states may be inconsistent across modules
- retention logic may be defined in business docs but absent in schema
- audit logging may be planned but not fully normalized
- soft delete conventions may be applied inconsistently
- reporting tables may not map cleanly to transactional schema

## Schema Gaps
The primary schema gaps are:
- multi-tenant and tenant ownership metadata
- soft delete and history tracking conventions
- outbox/event tables
- secure secret and credential storage design
- large-object or file metadata tables
- evidence retention and purge mechanisms
- KYC and compliance enforcement data
- reporting fact tables and summary/index tables
- cross-domain FK integrity and maturation

---

# Canonical Database Architecture

## PostgreSQL
PostgreSQL is the source of truth for all transactional data. It is the system of record for:
- customer and user data
- property and ownership records
- service lifecycle
- payment and subscription data
- complaint and audit state
- admin configuration
- operational records

The PostgreSQL design should emphasize:
- explicit PK/FK constraints
- timestamped lifecycle tracking
- index-backed search and lookup
- soft delete for business recovery
- versioning for critical entities
- dataclass and enum constraints

## Redis
Redis is not a source-of-truth database. It is used for:
- token/session validation
- caching hot read models
- IDs for deduplication
- rate-limit counters
- temporary workflow coordination
- notification queue short-term state
- report cache and dashboard cache

Redis should not store authoritative customer or property data unless explicitly treated as temporary caches with TTL.

## Object Storage
Object Storage should be used for:
- property evidence files
- agent-captured media
- complaint attachments
- invoice and report exports
- document archive
- support files
- large media payloads not suitable for PostgreSQL

Recommended object storage model:
- file metadata in PostgreSQL
- binary content in object storage
- object URL and checksum stored in DB
- signed URL access model for secure retrieval

## Search Index
A search index should be used for:
- property search
- marketplace listing search
- service request query
- audit search
- admin lookup
- customer lookup

Recommended strategy:
- Phase 1: PostgreSQL full-text and trigram search
- Phase 2: dedicated search cluster if product scale requires it
- optional OpenSearch/Elasticsearch for enterprise scale

---

# Entity Implementation Roadmap

## Customer Domain

Entities:
- users
- user_roles
- permissions
- customers
- customer_profiles
- customer_addresses
- customer_preferences
- user_sessions
- refresh_tokens

Implementation Notes:
- user and role tables are foundational
- customer_entity should reference user_id
- profile and preference tables should be 1:1 or 1:N as required
- user session and refresh token tables should be short-lived and audited

---

## Property Domain

Entities:
- properties
- property_addresses
- property_media
- property_documents
- property_status_history
- property_verifications
- property_ownership
- property_ownership_history

Implementation Notes:
- property_id should be the canonical identifier
- status history table should capture state transitions
- verification table should hold approval/rejection metadata
- ownership should support historical chain-of-title view

---

## Ownership Domain

Entities:
- ownership_records
- ownership_transfers
- ownership_verification_requests
- legal_party_records

Implementation Notes:
- ownership is a critical domain; avoid overwriting historical records
- support linear auditability and valid-date semantics
- verification requests should be associated with a property and user

---

## Service Domain

Entities:
- service_catalog
- service_request_types
- service_requests
- service_request_history
- service_request_assignments
- service_requirement_items
- service_request_evidence
- service_sla_rules

Implementation Notes:
- status transitions should be explicit and auditable
- assignment records should connect service request to agent/operations
- evidence should reference object storage metadata

---

## Agent Domain

Entities:
- agents
- agent_profiles
- agent_availability
- agent_assignments
- agent_performance_summary
- agent_worklog

Implementation Notes:
- assignment and availability tables should support queue operations
- agent performance summary may be derived and materialized
- worklog should support historical operations audit

---

## Visit Domain

Entities:
- visits
- visit_slots
- visit_checklists
- visit_outcomes
- visit_evidence
- visit_status_history

Implementation Notes:
- visit dates and location metadata should be explicit
- checklist items should support structured tracking
- outcomes should be linked to service request and agent

---

## Evidence Domain

Entities:
- evidence_records
- evidence_metadata
- evidence_file_links
- evidence_review_queue
- evidence_review_history

Implementation Notes:
- file metadata should be in DB
- actual content belongs in object storage
- review state should be explicit and queryable
- retention and legal hold states should be stored

---

## Report Domain

Entities:
- reports
- report_templates
- report_jobs
- report_exports
- report_schedules
- report_metrics

Implementation Notes:
- report generation should be async and job-driven
- export metadata should support retention and access tracking
- metrics may be materialized or computed from source data

---

## Subscription Domain

Entities:
- subscription_plans
- subscriptions
- subscription_events
- billing_cycles
- renewal_history
- plan_features

Implementation Notes:
- subscriptions should have clear lifecycle states
- renewal and billing tables should support invoice generation
- status transitions must be versioned

---

## Payment Domain

Entities:
- payments
- invoices
- payment_attempts
- refunds
- payment_providers
- payment_status_history
- payment_gateway_responses

Implementation Notes:
- idempotency keys are critical
- payment events should be historically immutable
- provider response payloads should be stored with traceability
- payment state transitions must be well-defined

---

## Marketplace Domain

Entities:
- marketplace_listings
- listing_categories
- listing_media
- listing_enquiries
- listing_preferences
- listing_review_queue

Implementation Notes:
- listings should include visibility and moderation status
- enquiry tables should explicitly capture lead conversion metadata
- moderation and lifecycle states should be versioned

---

## Vendor Domain

Entities:
- vendors
- vendor_profiles
- vendor_services
- vendor_contracts
- vendor_performance

Implementation Notes:
- vendors may be used for service providers or third-party agencies
- contracts should support legal and operational metadata
- performance metrics should be derived or aggregated

---

## Complaint Domain

Entities:
- complaints
- complaint_threads
- complaint_status_history
- complaint_resolution
- complaint_escalations

Implementation Notes:
- complaint lifecycle states must be traceable
- thread entries should preserve chronology and actor identity
- resolution detail should support evidence and closure trust

---

## Notification Domain

Entities:
- notification_templates
- notifications
- notification_channels
- notification_preferences
- notification_delivery_status
- notification_failures

Implementation Notes:
- notification data should not contain raw sensitive PII
- delivery status must be auditable and queryable
- templates should support localization and versioning

---

## Audit Domain

Entities:
- audit_log
- audit_log_events
- audit_log_payloads
- config_change_history
- privileged_action_logs

Implementation Notes:
- this should be append-only in production
- values should be stored in JSON or structured fields
- sensitive info should be hashed or masked appropriately

---

## KYC Domain

Entities:
- kyc_profiles
- kyc_documents
- kyc_verification_requests
- kyc_verification_results
- kyc_status_history

Implementation Notes:
- must support approval, rejection, pending, expired states
- document metadata should be stored in DB; actual files in object storage
- legal hold and PII handling should be strict

---

## Project Management Domain

Entities:
- projects
- project_members
- project_tasks
- project_status_history
- project_delivery_milestones

Implementation Notes:
- project metadata may support internal implementation operations
- tasks should capture assignee, status, and due dates
- milestones should support timeline reporting

---

## Construction Domain

Entities:
- construction_sites
- site_activities
- site_checklists
- site_materials
- site_progress_updates
- site_work_orders

Implementation Notes:
- maintain explicit relationship to property
- work orders and progress should support operational reporting
- material and site updates may be later phased but should be planned with schema structure

---

# Missing Tables to Implement

Below are the high-priority tables that should be implemented first to complete the schema baseline.

## 1. user_sessions
Purpose:
- store session metadata for authenticated users

Primary Key:
- session_id

Foreign Keys:
- user_id -> users.user_id

Important Columns:
- session_id
- user_id
- session_token_hash
- issued_at
- expires_at
- revoked_at
- device_info
- ip_address
- is_active

Indexes:
- idx_user_sessions_user_id
- idx_user_sessions_expires_at
- idx_user_sessions_status

Retention Rules:
- delete expired rows after 30-90 days
- purge on logout and security invalidation

---

## 2. refresh_tokens
Purpose:
- support JWT refresh token lifecycle

Primary Key:
- refresh_token_id

Foreign Keys:
- user_id -> users.user_id

Important Columns:
- refresh_token_hash
- user_id
- issued_at
- expires_at
- rotated_from
- revoked_at
- user_agent
- ip_address

Indexes:
- idx_refresh_tokens_user_id
- idx_refresh_tokens_expires_at
- idx_refresh_tokens_hash

Retention Rules:
- 7-30 day retention depending on platform policy
- revoke upon logout and password reset

---

## 3. property_verifications
Purpose:
- capture verification workflow for property ownership or status

Primary Key:
- property_verification_id

Foreign Keys:
- property_id -> properties.property_id
- verifier_user_id -> users.user_id

Important Columns:
- verification_type
- status
- reviewed_by
- reviewed_at
- remarks
- evidence_reference

Indexes:
- idx_property_verifications_property_id
- idx_property_verifications_status
- idx_property_verifications_reviewed_at

Retention Rules:
- retain for audit and compliance lifecycle
- purge only after legal and operational retention review

---

## 4. property_status_history
Purpose:
- record status changes over time

Primary Key:
- status_history_id

Foreign Keys:
- property_id -> properties.property_id

Important Columns:
- previous_status
- new_status
- changed_by
- changed_at
- reason_code

Indexes:
- idx_property_status_history_property_id
- idx_property_status_history_changed_at

Retention Rules:
- retain as operational history
- align with property life cycle retention

---

## 5. service_request_evidence
Purpose:
- link evidence files to service requests

Primary Key:
- evidence_id

Foreign Keys:
- service_request_id -> service_requests.service_request_id
- uploaded_by -> users.user_id

Important Columns:
- evidence_type
- file_key
- file_size
- mime_type
- checksum
- review_status

Indexes:
- idx_service_evidence_request_id
- idx_service_evidence_uploaded_by
- idx_service_evidence_review_status

Retention Rules:
- dependent on service request or case lifecycle
- purge only post-retention and legal review

---

## 6. visit_records
Purpose:
- represent an onsite or remote visit instance

Primary Key:
- visit_id

Foreign Keys:
- service_request_id -> service_requests.service_request_id
- agent_id -> agents.agent_id

Important Columns:
- scheduled_at
- started_at
- completed_at
- visit_status
- latitude
- longitude
- notes

Indexes:
- idx_visit_records_service_request_id
- idx_visit_records_agent_id
- idx_visit_records_status

Retention Rules:
- retain for operational and dispute review
- align with legal evidence retention

---

## 7. visit_evidence
Purpose:
- store evidence gathered during a visit

Primary Key:
- visit_evidence_id

Foreign Keys:
- visit_id -> visit_records.visit_id
- evidence_id -> evidence_records.evidence_id

Important Columns:
- evidence_type
- captured_at
- file_reference
- quality_score

Indexes:
- idx_visit_evidence_visit_id
- idx_visit_evidence_type

Retention Rules:
- same as evidence retention policy

---

## 8. complaint_threads
Purpose:
- support threaded conversations on complaints

Primary Key:
- thread_id

Foreign Keys:
- complaint_id -> complaints.complaint_id
- created_by -> users.user_id

Important Columns:
- message_body
- message_type
- created_at
- is_internal

Indexes:
- idx_complaint_threads_complaint_id
- idx_complaint_threads_created_at

Retention Rules:
- retain with complaint case lifecycle

---

## 9. complaint_resolution
Purpose:
- store final complaint closure decisions

Primary Key:
- complaint_resolution_id

Foreign Keys:
- complaint_id -> complaints.complaint_id
- resolved_by -> users.user_id

Important Columns:
- resolution_status
- resolution_summary
- closure_reason
- resolved_at

Indexes:
- idx_complaint_resolution_complaint_id
- idx_complaint_resolution_resolved_at

Retention Rules:
- retain for compliance and support records

---

## 10. payment_attempts
Purpose:
- capture provider attempts and retries

Primary Key:
- payment_attempt_id

Foreign Keys:
- payment_id -> payments.payment_id

Important Columns:
- provider_name
- provider_transaction_id
- status
- response_code
- request_payload_hash
- created_at

Indexes:
- idx_payment_attempts_payment_id
- idx_payment_attempts_provider_tx_id

Retention Rules:
- retain for reconciliation and fraud analysis

---

## 11. payment_status_history
Purpose:
- track payment lifecycle state changes

Primary Key:
- payment_status_history_id

Foreign Keys:
- payment_id -> payments.payment_id

Important Columns:
- previous_status
- new_status
- changed_at
- changed_by
- notes

Indexes:
- idx_payment_status_history_payment_id
- idx_payment_status_history_changed_at

Retention Rules:
- retain for dispute and audit resolution

---

## 12. notification_delivery_status
Purpose:
- record send and failure status for notification messages

Primary Key:
- notification_delivery_id

Foreign Keys:
- notification_id -> notifications.notification_id

Important Columns:
- channel
- status
- sent_at
- delivered_at
- failed_at
- provider_response

Indexes:
- idx_notification_delivery_notification_id
- idx_notification_delivery_status

Retention Rules:
- store for operational and support analysis
- subject to notification retention policy

---

## 13. report_jobs
Purpose:
- capture async generation pipelines

Primary Key:
- report_job_id

Foreign Keys:
- report_id -> reports.report_id
- requested_by -> users.user_id

Important Columns:
- job_status
- started_at
- completed_at
- parameters_json
- output_location

Indexes:
- idx_report_jobs_report_id
- idx_report_jobs_status
- idx_report_jobs_requested_by

Retention Rules:
- retain job metadata for operational analysis

---

## 14. kyc_verification_results
Purpose:
- store approval/rejection of KYC verification

Primary Key:
- kyc_verification_id

Foreign Keys:
- customer_id -> customers.customer_id
- reviewed_by -> users.user_id

Important Columns:
- verification_status
- verification_type
- result_reason
- processed_at

Indexes:
- idx_kyc_verification_customer_id
- idx_kyc_verification_status

Retention Rules:
- secure retention per KYC policy

---

## 15. project_tasks
Purpose:
- support project execution and delivery operations

Primary Key:
- project_task_id

Foreign Keys:
- project_id -> projects.project_id
- assignee_user_id -> users.user_id

Important Columns:
- task_name
- status
- due_date
- completion_date
- priority

Indexes:
- idx_project_tasks_project_id
- idx_project_tasks_assignee
- idx_project_tasks_status

Retention Rules:
- retain for operational lifecycle

---

## 16. outbox_events
Purpose:
- ensure reliable asynchronous event publication

Primary Key:
- outbox_event_id

Important Columns:
- aggregate_type
- aggregate_id
- event_type
- payload_json
- occurred_at
- processed_at
- status

Indexes:
- idx_outbox_events_status
- idx_outbox_events_occurred_at

Retention Rules:
- retain for operational event replay and recovery
- purge after safe replay window

---

## 17. webhook_events
Purpose:
- support payload delivery to external systems

Primary Key:
- webhook_event_id

Important Columns:
- endpoint_url
- payload_json
- status
- attempts
- last_attempt_at
- next_attempt_at

Indexes:
- idx_webhook_events_status
- idx_webhook_events_next_attempt_at

Retention Rules:
- retain until success or safe dead-lettering

---

## 18. audit_log_payloads
Purpose:
- centralize payload and change history for audit tables

Primary Key:
- audit_log_payload_id

Foreign Keys:
- audit_log_id -> audit_log.audit_log_id

Important Columns:
- old_value_json
- new_value_json
- diff_json

Indexes:
- idx_audit_log_payloads_audit_log_id

Retention Rules:
- retain for compliance and forensic investigation

---

# Schema Alignment Tasks

The following tasks are required to align the physical model with the business and traceability documents.

## 1. Resolve naming inconsistencies
- standardize singular vs plural naming
- align table names to the canonical dictionary
- standardize snake_case and foreign key naming

## 2. Resolve domain merge gaps
- unify customer, user, and customer profile models
- ensure service request and visit records align with state model
- align complaint and escalation ownership semantics
- unify audit and platform configuration definitions

## 3. Harmonize lifecycle state values
- create one authoritative enum/state table or controlled value set
- ensure all modules reference the same state transitions
- remove drift between SRS and implementation states

## 4. Align with traceability matrix
- match all SRS requirements to tables and fields
- ensure each critical feature has a data backing model
- verify no requirement exists without a persistence artifact

## 5. Confirm soft-deletion semantics
- define global soft delete column conventions
- ensure deletion logic is consistent across domain tables
- decide whether inactive records remain visible with filters

## 6. Align retention and compliance controls
- define data retention for files, logs, and customer records
- ensure PII handling and deletion rules are reflected in schema and API flow

---

# Flyway Migration Roadmap

Each migration should be versioned and transactional. The version order below is representative and should be refined during implementation.

## V001 - Foundation and Schema Bootstrapping
Purpose:
- create baseline tables and extension support

Tables:
- users
- roles
- permissions
- user_roles
- permission_role_map

Constraints:
- PK/FK integrity
- unique indexes for role and permission names

Indexes:
- idx_users_email_unique
- idx_roles_name_unique

Data Migration Impact:
- seed default roles and permissions
- set bootstrapped admin account data if required

---

## V002 - Customer and Profile Schema
Purpose:
- create customer domain tables

Tables:
- customers
- customer_profiles
- customer_addresses
- customer_preferences

Constraints:
- FK to users
- unique customer external identifiers if applicable

Indexes:
- idx_customers_user_id
- idx_customer_profiles_customer_id

Data Migration Impact:
- migrate legacy customer records into base domain

---

## V003 - Property Core Schema
Purpose:
- create property and address tables

Tables:
- properties
- property_addresses
- property_media
- property_documents

Constraints:
- property status constraints
- property owner FK integrity

Indexes:
- idx_properties_customer_id
- idx_property_addresses_property_id
- idx_properties_status

Data Migration Impact:
- load initial seed property records if any

---

## V004 - Ownership and Verification Schema
Purpose:
- implement property ownership and verification lifecycle

Tables:
- property_ownership
- property_ownership_history
- property_verifications
- property_status_history

Constraints:
- enforce legal ownership history model
- verification status values

Indexes:
- idx_property_ownership_property_id
- idx_property_verifications_property_id

Data Migration Impact:
- backfill existing property status history if available

---

## V005 - Service Catalog and Request Workflow
Purpose:
- create service domain foundations

Tables:
- service_catalog
- service_request_types
- service_requests
- service_request_history
- service_request_assignments
- service_requirement_items

Constraints:
- status transition rule support
- FK integrity for assignments

Indexes:
- idx_service_requests_customer_id
- idx_service_request_assignments_request_id

Data Migration Impact:
- seed catalog entries
- map legacy requests if present

---

## V006 - Agent and Visit Support
Purpose:
- enable agent work and visit tracking

Tables:
- agents
- agent_profiles
- agent_availability
- agent_assignments
- visits
- visit_status_history
- visit_evidence

Constraints:
- agent assignment invariants
- scheduled and completed visit constraints

Indexes:
- idx_agents_user_id
- idx_agent_assignments_request_id
- idx_visits_agent_id

Data Migration Impact:
- initialize agent records
- migrate assigned visits if any

---

## V007 - Evidence and File Metadata
Purpose:
- implement evidence metadata and object references

Tables:
- evidence_records
- evidence_metadata
- evidence_file_links
- evidence_review_queue

Constraints:
- FK to service and visit tables
- object storage path validation

Indexes:
- idx_evidence_records_request_id
- idx_evidence_records_review_status

Data Migration Impact:
- backfill media asset metadata if existing files exist

---

## V008 - Subscription and Billing
Purpose:
- implement subscriptions, billing cycle, and plan structures

Tables:
- subscription_plans
- subscriptions
- billing_cycles
- renewal_history
- plan_features

Constraints:
- status, plan validity, and renewal regulations

Indexes:
- idx_subscriptions_customer_id
- idx_billing_cycles_subscription_id

Data Migration Impact:
- seed plan catalog and existing subscriptions

---

## V009 - Payment Processing
Purpose:
- support payment lifecycle and provider integration

Tables:
- payments
- invoices
- payment_attempts
- refunds
- payment_status_history
- payment_gateway_responses

Constraints:
- unique provider transaction mapping
- status and amount validation

Indexes:
- idx_payments_customer_id
- idx_invoices_subscription_id
- idx_payments_status

Data Migration Impact:
- migrate existing payment records if available

---

## V010 - Notifications and Messaging
Purpose:
- support message templates and delivery outcomes

Tables:
- notification_templates
- notifications
- notification_preferences
- notification_channels
- notification_delivery_status
- notification_failures

Constraints:
- channel and status validation
- unique template code values if designed

Indexes:
- idx_notifications_user_id
- idx_notification_delivery_status_notification_id

Data Migration Impact:
- seed templates and default notification preferences

---

## V011 - Complaint and Escalation
Purpose:
- implement complaint handling and escalation schemas

Tables:
- complaints
- complaint_threads
- complaint_resolution
- complaint_escalations

Constraints:
- lifecycle constraints and actor tracking
- escalation policy links

Indexes:
- idx_complaints_customer_id
- idx_complaint_threads_complaint_id

Data Migration Impact:
- migrate existing complaint records if any

---

## V012 - Marketplace and Vendor
Purpose:
- create marketplace and vendor foundation

Tables:
- vendors
- vendor_profiles
- vendor_services
- marketplace_listings
- listing_enquiries
- listing_categories

Constraints:
- listing visibility state
- vendor service association rules

Indexes:
- idx_marketplace_listings_status
- idx_listing_enquiries_listing_id

Data Migration Impact:
- seed categories and vendor lists if required

---

## V013 - KYC and Compliance
Purpose:
- support KYC onboarding and verification

Tables:
- kyc_profiles
- kyc_documents
- kyc_verification_requests
- kyc_verification_results
- kyc_status_history

Constraints:
- controlled status values
- document relation restrictions

Indexes:
- idx_kyc_profiles_customer_id
- idx_kyc_verification_results_customer_id

Data Migration Impact:
- migrate verification status if legacy compliance data exists

---

## V014 - Audit, Administration, and Outbox
Purpose:
- finalize governance and operational reliability

Tables:
- audit_log
- audit_log_events
- audit_log_payloads
- admin_configurations
- feature_flags
- outbox_events
- webhook_events

Constraints:
- append-only audit log
- event processing status states

Indexes:
- idx_audit_log_user_id
- idx_outbox_events_status
- idx_webhook_events_status

Data Migration Impact:
- seed config values
- create default feature flags
- initialize admin settings

---

## V015 - Reporting and Analytics Schema
Purpose:
- support dashboard and reporting workloads

Tables:
- reports
- report_templates
- report_jobs
- report_exports
- report_schedules
- report_metrics

Constraints:
- report generation status
- export and reporting storage location constraints

Indexes:
- idx_report_jobs_status
- idx_report_exports_report_id

Data Migration Impact:
- seed report templates and admin-level report definitions

---

## V016 - Project and Construction Data
Purpose:
- support internal planning and site operations

Tables:
- projects
- project_members
- project_tasks
- project_milestones
- construction_sites
- site_activities
- site_checklists

Constraints:
- project ownership and task assignment rules

Indexes:
- idx_project_tasks_project_id
- idx_construction_sites_property_id

Data Migration Impact:
- initialize internal project metadata if present

---

# Index Strategy

## Lookup Indexes
Create indexes for:
- user_id lookups
- customer_id lookups
- property_id lookups
- service_request_id lookups
- payment_id lookups
- complaint_id lookups
- agent_id lookups
- report_id lookups

## Search Indexes
Use:
- PostgreSQL trigram indexes for fuzzy matching
- full-text search for property and listing search
- email/name lookup indexes
- tenant-aware search indexes if multi-tenancy is implemented

## Reporting Indexes
Add indexes for:
- date ranges
- status filters
- assignee filters
- tenant or customer filters
- report generation filter sets

## Composite Indexes
Common composite indexes:
- (customer_id, created_at)
- (status, created_at)
- (property_id, status)
- (agent_id, status, scheduled_at)
- (service_request_id, created_at)
- (customer_id, subscription_status)
- (provider_transaction_id, created_at)

---

# Partition Strategy

## Large Tables
Likely partition candidates:
- audit_log
- notifications
- service_requests
- complaints
- payments
- outbox_events
- report_jobs
- property_history tables

## Partition Keys
Recommended keys:
- created_at for time-based data
- customer_id or tenant_id for user-centric partitions
- property_id for property-specific historical tables
- status for operational queue tables

## Retention
- append-only large tables should be partitioned by month or quarter
- old partitions should be archived to cold storage after retention period
- archive pipelines should preserve referential integrity and queryability

## Archival
- move cold partitions to object storage or archive tables
- preserve metadata and summary tables for reporting
- maintain secure retention and purge workflows

---

# Multi-Tenant Strategy

PropertyPilot may eventually require tenant-aware or multi-entity structures. Even if not immediate, schema should permit future expansion.

## Tenant Keys
Add tenant_id or organization_id to:
- users
- customers
- properties
- service_requests
- subscriptions
- complaints
- audit_logs
- reports

## Isolation Model
Recommended:
- single-tenant database in MVP
- tenant-scoped rows when necessary
- future migration path to logical multi-tenancy

## Indexes
- indexes on tenant_id + entity_id
- composite indexes on tenant_id + status + created_at

## Security Controls
- restrict tenant data access by role and ownership
- ensure all queries enforce tenant scope
- isolate admin and operations data from customer and agent data

---

# Audit Strategy

## Audit Tables
Core audit tables:
- audit_log
- audit_log_events
- audit_log_payloads
- privileged_action_logs
- config_change_history

## Change Tracking
Track:
- actor_id
- actor_role
- action_type
- entity_type
- entity_id
- timestamp
- old_value
- new_value
- reason
- request_id

## Retention
- keep audit and forensic logs for required duration
- separate active operational logs from long-term archive
- preserve tamper-evidence semantics

## Compliance
- restrict access to privileged users
- ensure logs are append-only
- preserve source system metadata for legal and security investigation

---

# Data Integrity Rules

## Referential Integrity
- all foreign keys enforced in PostgreSQL
- no orphan records for property, customer, service, subscription, or payment relationships
- property ownership history must never overwrite prior valid records without explicit archival process

## Unique Constraints
- unique email / phone / username where appropriate
- unique role name
- unique provider transaction id where defined
- unique subscription status record per current active period if required

## Validation Rules
- non-negative amounts
- valid status transitions only
- valid date ordering
- attachment file size limits
- valid enum values

## Soft Deletes
- use soft delete columns on business entities:
  - is_deleted
  - deleted_at
  - deleted_by
- do not physically delete critical records without explicit archival workflow

## Versioning
- version critical records such as:
  - property ownership
  - service requests
  - subscriptions
  - payment lifecycle
  - configuration
- use version tracking for audit and rollback support

---

# Performance Strategy

## Expected Volume
For MVP:
- moderate number of customers and properties
- moderate service request throughput
- low-to-mid operational concurrency
- manageable analytics and report generation

## Growth Forecast
Expected growth:
- customer and property creation will increase with adoption
- service request volume will spike by season and geography
- payment and analytics tables require ongoing optimization
- notifications and audit logs may become large rapidly

## Scaling Plan
- vertical scaling for initial DB
- read replicas for reporting and analytics later
- partitioning for audit and notification tables
- query and index optimization based on production metrics
- caching for hot list and summary endpoints
- asynchronous report generation for large jobs

---

# Security Controls

## PII Protection
- store PII only when required
- mask fields in logs and non-production environments
- apply field-level encryption where necessary
- restrict access to customer and KYC records

## Encryption
- encrypt PostgreSQL at rest
- encrypt Redis where supported
- encrypt object storage data
- use TLS for all data in transit
- restrict DB and storage access to service roles

## Data Access Controls
- DB role separation for app, replication, admin, and reporting
- no ad-hoc access to production database
- row and service-level authorization by design
- audit privileged access and data exports

## Auditability
- capture all state-changing actions
- store actor, request ID, and timestamp
- preserve change payloads for investigations

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Schema drift from physical model | inconsistent database behavior | enforce Flyway and schema diff checks | Database Engineering
Missing FK or lifecycle integrity | orphan records and broken workflows | add constraint validation and CI checks | Backend Architecture
Unbounded audit growth | slow queries and storage cost increase | partitioning and retention policy | Platform Engineering
Weak retention policy | compliance and privacy exposure | set retention and purge plan early | Security / Compliance
Incomplete search strategy | poor property and listing lookup | add search index strategy and benchmark | Architecture
Payment table complexity | reconciliation and audit errors | enforce state model and idempotency | Payment Engineering
Service workflow drift | broken business flows | align SRS, state model, and SQL constraints | Product / Backend
Inconsistent soft delete semantics | data confusion and reporting errors | centralize deletion policy and conventions | Database Architecture
Large object storage metadata gaps | file tracking issues | keep metadata in DB with strict lifecycle rules | Backend / Storage
Unplanned multi-tenant expansion | expensive refactor | embed tenant_id in core tables | Architecture

---

# Delivery Roadmap

## Sprint 1
Focus:
- base schema and migration setup
- users, roles, permissions
- customer and profile tables
- foundational indexes and constraints

Deliverables:
- V001-V003 production-ready baseline
- migration and seed scripts
- user/session foundation
- baseline data integrity checks

---

## Sprint 2
Focus:
- property, ownership, service domain
- service request and status tracking
- property verification tables

Deliverables:
- property core schema
- ownership and verification tables
- service request schema
- history and evidence linking

---

## Sprint 3
Focus:
- agent, visit, evidence, complaint, notification
- queue and assignment tables

Deliverables:
- visit and evidence domain
- complaint lifecycle
- notification and delivery tables
- assignment and queue state schema

---

## Sprint 4
Focus:
- subscription, payment, report, KYC
- admin and audit tables
- outbox and event data stores

Deliverables:
- subscription and payment schema
- report generation and export tables
- KYC and admin config tables
- audit and outbox support

---

## Sprint 5
Focus:
- optimization, partitioning, analytics, production hardening
- testing and schema validation for deployment

Deliverables:
- partitioned large tables
- production tuning
- index review and query optimization
- full schema validation and UAT sign-off

---

# Success Criteria

The database is ready for:
- OpenAPI completion
- backend development
- frontend integration
- production deployment

When all of the following are true:
- all critical entities are implemented in Flyway
- all core PK/FK and uniqueness rules are enforced
- performance index set is validated
- audit and retention rules are in place
- PII and security controls are enforced
- migration rollback and recovery is validated
- schema and traceability matrix are aligned
- UAT dataset and data refresh flow are complete
- production readiness checklist is signed off

---

# Final Summary

PropertyPilot’s database foundation is strong conceptually but requires disciplined implementation sequencing to become production-ready. The main work is not only schema creation but also alignment across the physical model, traceability matrix, cross-cutting requirements, and operational SRD needs.

The recommended route is:
- create a strict core schema first
- implement high-risk operational modules next
- add reporting, audit, and compliance structures early
- use Flyway as the single migration source of truth
- enforce data integrity and retention rules from day one
- make indexing, partitioning, and search strategy explicit before scale-up

This plan transforms the current logical design into a migration-based implementation path that is production-aligned, auditable, and scalable for MVP and enterprise growth.
```// filepath: c:\PropertyPilot\docs\Database_Implementation_Plan.md
# Database Implementation Plan

Document Type: Database Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Database Architecture / Backend Architecture / Platform Engineering

---

# Purpose

This document converts the PropertyPilot database documentation set into an implementation-ready production database blueprint. It aligns the canonical data dictionary, physical model, schema backlog, traceability matrix, and cross-cutting requirements into a coherent implementation plan for PostgreSQL and supporting storage services.

The purpose is to:
- confirm the current database state
- identify missing and partial schema components
- resolve model conflicts and gaps
- define the entity implementation roadmap by domain
- establish Flyway migration sequencing
- create index, partition, and retention rules
- finalize data integrity and audit standards
- define a delivery roadmap ready for backend implementation and production deployment

---

# Current State Assessment

## Implemented Tables
The repository indicates a strong logical data model and a physical schema direction, with clear evidence of structured domain modeling. Based on the available documentation and design artifacts, the following are likely to be either already modeled or partially represented:

- users
- roles
- permissions
- customer profiles
- properties
- property ownership records
- service catalog items
- service requests
- service request history
- subscriptions
- payment records
- invoice records
- notification templates
- notifications
- complaints
- audit logs
- admin configuration records

These tables are typically found in an early-to-mid-stage implementation baseline and are suitable for a controlled production progression.

## Missing Tables
The following areas are likely under-modeled or not fully translated into SQL migrations:

- user sessions and refresh tokens
- property verification and approval chain
- evidence attachments and media metadata
- visit scheduling and onsite visit records
- agent assignment state and capacity records
- escalation records
- payment provider transaction metadata
- report jobs and exports
- KYC verification records
- vendor master data
- project management records
- construction activity records
- marketplace listing and inquiry tables
- notification delivery and preference tables
- archival tables for historical retention
- outbox tables for event reliability

## Partial Tables
The following domain objects are likely only partially complete in current design artifacts:
- payment states and retries
- workflow state transitions
- audit log detail payloads
- notification delivery status
- service evidence lifecycle
- complaint thread / resolution state
- user permission mappings
- dashboard/report data models
- configuration versioning

## Conflicting Models
Likely areas of tension between modeled and operational requirements:
- physical model vs. migration history may differ in naming conventions
- domain lifecycle states may be inconsistent across modules
- retention logic may be defined in business docs but absent in schema
- audit logging may be planned but not fully normalized
- soft delete conventions may be applied inconsistently
- reporting tables may not map cleanly to transactional schema

## Schema Gaps
The primary schema gaps are:
- multi-tenant and tenant ownership metadata
- soft delete and history tracking conventions
- outbox/event tables
- secure secret and credential storage design
- large-object or file metadata tables
- evidence retention and purge mechanisms
- KYC and compliance enforcement data
- reporting fact tables and summary/index tables
- cross-domain FK integrity and maturation

---

# Canonical Database Architecture

## PostgreSQL
PostgreSQL is the source of truth for all transactional data. It is the system of record for:
- customer and user data
- property and ownership records
- service lifecycle
- payment and subscription data
- complaint and audit state
- admin configuration
- operational records

The PostgreSQL design should emphasize:
- explicit PK/FK constraints
- timestamped lifecycle tracking
- index-backed search and lookup
- soft delete for business recovery
- versioning for critical entities
- dataclass and enum constraints

## Redis
Redis is not a source-of-truth database. It is used for:
- token/session validation
- caching hot read models
- IDs for deduplication
- rate-limit counters
- temporary workflow coordination
- notification queue short-term state
- report cache and dashboard cache

Redis should not store authoritative customer or property data unless explicitly treated as temporary caches with TTL.

## Object Storage
Object Storage should be used for:
- property evidence files
- agent-captured media
- complaint attachments
- invoice and report exports
- document archive
- support files
- large media payloads not suitable for PostgreSQL

Recommended object storage model:
- file metadata in PostgreSQL
- binary content in object storage
- object URL and checksum stored in DB
- signed URL access model for secure retrieval

## Search Index
A search index should be used for:
- property search
- marketplace listing search
- service request query
- audit search
- admin lookup
- customer lookup

Recommended strategy:
- Phase 1: PostgreSQL full-text and trigram search
- Phase 2: dedicated search cluster if product scale requires it
- optional OpenSearch/Elasticsearch for enterprise scale

---

# Entity Implementation Roadmap

## Customer Domain

Entities:
- users
- user_roles
- permissions
- customers
- customer_profiles
- customer_addresses
- customer_preferences
- user_sessions
- refresh_tokens

Implementation Notes:
- user and role tables are foundational
- customer_entity should reference user_id
- profile and preference tables should be 1:1 or 1:N as required
- user session and refresh token tables should be short-lived and audited

---

## Property Domain

Entities:
- properties
- property_addresses
- property_media
- property_documents
- property_status_history
- property_verifications
- property_ownership
- property_ownership_history

Implementation Notes:
- property_id should be the canonical identifier
- status history table should capture state transitions
- verification table should hold approval/rejection metadata
- ownership should support historical chain-of-title view

---

## Ownership Domain

Entities:
- ownership_records
- ownership_transfers
- ownership_verification_requests
- legal_party_records

Implementation Notes:
- ownership is a critical domain; avoid overwriting historical records
- support linear auditability and valid-date semantics
- verification requests should be associated with a property and user

---

## Service Domain

Entities:
- service_catalog
- service_request_types
- service_requests
- service_request_history
- service_request_assignments
- service_requirement_items
- service_request_evidence
- service_sla_rules

Implementation Notes:
- status transitions should be explicit and auditable
- assignment records should connect service request to agent/operations
- evidence should reference object storage metadata

---

## Agent Domain

Entities:
- agents
- agent_profiles
- agent_availability
- agent_assignments
- agent_performance_summary
- agent_worklog

Implementation Notes:
- assignment and availability tables should support queue operations
- agent performance summary may be derived and materialized
- worklog should support historical operations audit

---

## Visit Domain

Entities:
- visits
- visit_slots
- visit_checklists
- visit_outcomes
- visit_evidence
- visit_status_history

Implementation Notes:
- visit dates and location metadata should be explicit
- checklist items should support structured tracking
- outcomes should be linked to service request and agent

---

## Evidence Domain

Entities:
- evidence_records
- evidence_metadata
- evidence_file_links
- evidence_review_queue
- evidence_review_history

Implementation Notes:
- file metadata should be in DB
- actual content belongs in object storage
- review state should be explicit and queryable
- retention and legal hold states should be stored

---

## Report Domain

Entities:
- reports
- report_templates
- report_jobs
- report_exports
- report_schedules
- report_metrics

Implementation Notes:
- report generation should be async and job-driven
- export metadata should support retention and access tracking
- metrics may be materialized or computed from source data

---

## Subscription Domain

Entities:
- subscription_plans
- subscriptions
- subscription_events
- billing_cycles
- renewal_history
- plan_features

Implementation Notes:
- subscriptions should have clear lifecycle states
- renewal and billing tables should support invoice generation
- status transitions must be versioned

---

## Payment Domain

Entities:
- payments
- invoices
- payment_attempts
- refunds
- payment_providers
- payment_status_history
- payment_gateway_responses

Implementation Notes:
- idempotency keys are critical
- payment events should be historically immutable
- provider response payloads should be stored with traceability
- payment state transitions must be well-defined

---

## Marketplace Domain

Entities:
- marketplace_listings
- listing_categories
- listing_media
- listing_enquiries
- listing_preferences
- listing_review_queue

Implementation Notes:
- listings should include visibility and moderation status
- enquiry tables should explicitly capture lead conversion metadata
- moderation and lifecycle states should be versioned

---

## Vendor Domain

Entities:
- vendors
- vendor_profiles
- vendor_services
- vendor_contracts
- vendor_performance

Implementation Notes:
- vendors may be used for service providers or third-party agencies
- contracts should support legal and operational metadata
- performance metrics should be derived or aggregated

---

## Complaint Domain

Entities:
- complaints
- complaint_threads
- complaint_status_history
- complaint_resolution
- complaint_escalations

Implementation Notes:
- complaint lifecycle states must be traceable
- thread entries should preserve chronology and actor identity
- resolution detail should support evidence and closure trust

---

## Notification Domain

Entities:
- notification_templates
- notifications
- notification_channels
- notification_preferences
- notification_delivery_status
- notification_failures

Implementation Notes:
- notification data should not contain raw sensitive PII
- delivery status must be auditable and queryable
- templates should support localization and versioning

---

## Audit Domain

Entities:
- audit_log
- audit_log_events
- audit_log_payloads
- config_change_history
- privileged_action_logs

Implementation Notes:
- this should be append-only in production
- values should be stored in JSON or structured fields
- sensitive info should be hashed or masked appropriately

---

## KYC Domain

Entities:
- kyc_profiles
- kyc_documents
- kyc_verification_requests
- kyc_verification_results
- kyc_status_history

Implementation Notes:
- must support approval, rejection, pending, expired states
- document metadata should be stored in DB; actual files in object storage
- legal hold and PII handling should be strict

---

## Project Management Domain

Entities:
- projects
- project_members
- project_tasks
- project_status_history
- project_delivery_milestones

Implementation Notes:
- project metadata may support internal implementation operations
- tasks should capture assignee, status, and due dates
- milestones should support timeline reporting

---

## Construction Domain

Entities:
- construction_sites
- site_activities
- site_checklists
- site_materials
- site_progress_updates
- site_work_orders

Implementation Notes:
- maintain explicit relationship to property
- work orders and progress should support operational reporting
- material and site updates may be later phased but should be planned with schema structure

---

# Missing Tables to Implement

Below are the high-priority tables that should be implemented first to complete the schema baseline.

## 1. user_sessions
Purpose:
- store session metadata for authenticated users

Primary Key:
- session_id

Foreign Keys:
- user_id -> users.user_id

Important Columns:
- session_id
- user_id
- session_token_hash
- issued_at
- expires_at
- revoked_at
- device_info
- ip_address
- is_active

Indexes:
- idx_user_sessions_user_id
- idx_user_sessions_expires_at
- idx_user_sessions_status

Retention Rules:
- delete expired rows after 30-90 days
- purge on logout and security invalidation

---

## 2. refresh_tokens
Purpose:
- support JWT refresh token lifecycle

Primary Key:
- refresh_token_id

Foreign Keys:
- user_id -> users.user_id

Important Columns:
- refresh_token_hash
- user_id
- issued_at
- expires_at
- rotated_from
- revoked_at
- user_agent
- ip_address

Indexes:
- idx_refresh_tokens_user_id
- idx_refresh_tokens_expires_at
- idx_refresh_tokens_hash

Retention Rules:
- 7-30 day retention depending on platform policy
- revoke upon logout and password reset

---

## 3. property_verifications
Purpose:
- capture verification workflow for property ownership or status

Primary Key:
- property_verification_id

Foreign Keys:
- property_id -> properties.property_id
- verifier_user_id -> users.user_id

Important Columns:
- verification_type
- status
- reviewed_by
- reviewed_at
- remarks
- evidence_reference

Indexes:
- idx_property_verifications_property_id
- idx_property_verifications_status
- idx_property_verifications_reviewed_at

Retention Rules:
- retain for audit and compliance lifecycle
- purge only after legal and operational retention review

---

## 4. property_status_history
Purpose:
- record status changes over time

Primary Key:
- status_history_id

Foreign Keys:
- property_id -> properties.property_id

Important Columns:
- previous_status
- new_status
- changed_by
- changed_at
- reason_code

Indexes:
- idx_property_status_history_property_id
- idx_property_status_history_changed_at

Retention Rules:
- retain as operational history
- align with property life cycle retention

---

## 5. service_request_evidence
Purpose:
- link evidence files to service requests

Primary Key:
- evidence_id

Foreign Keys:
- service_request_id -> service_requests.service_request_id
- uploaded_by -> users.user_id

Important Columns:
- evidence_type
- file_key
- file_size
- mime_type
- checksum
- review_status

Indexes:
- idx_service_evidence_request_id
- idx_service_evidence_uploaded_by
- idx_service_evidence_review_status

Retention Rules:
- dependent on service request or case lifecycle
- purge only post-retention and legal review

---

## 6. visit_records
Purpose:
- represent an onsite or remote visit instance

Primary Key:
- visit_id

Foreign Keys:
- service_request_id -> service_requests.service_request_id
- agent_id -> agents.agent_id

Important Columns:
- scheduled_at
- started_at
- completed_at
- visit_status
- latitude
- longitude
- notes

Indexes:
- idx_visit_records_service_request_id
- idx_visit_records_agent_id
- idx_visit_records_status

Retention Rules:
- retain for operational and dispute review
- align with legal evidence retention

---

## 7. visit_evidence
Purpose:
- store evidence gathered during a visit

Primary Key:
- visit_evidence_id

Foreign Keys:
- visit_id -> visit_records.visit_id
- evidence_id -> evidence_records.evidence_id

Important Columns:
- evidence_type
- captured_at
- file_reference
- quality_score

Indexes:
- idx_visit_evidence_visit_id
- idx_visit_evidence_type

Retention Rules:
- same as evidence retention policy

---

## 8. complaint_threads
Purpose:
- support threaded conversations on complaints

Primary Key:
- thread_id

Foreign Keys:
- complaint_id -> complaints.complaint_id
- created_by -> users.user_id

Important Columns:
- message_body
- message_type
- created_at
- is_internal

Indexes:
- idx_complaint_threads_complaint_id
- idx_complaint_threads_created_at

Retention Rules:
- retain with complaint case lifecycle

---

## 9. complaint_resolution
Purpose:
- store final complaint closure decisions

Primary Key:
- complaint_resolution_id

Foreign Keys:
- complaint_id -> complaints.complaint_id
- resolved_by -> users.user_id

Important Columns:
- resolution_status
- resolution_summary
- closure_reason
- resolved_at

Indexes:
- idx_complaint_resolution_complaint_id
- idx_complaint_resolution_resolved_at

Retention Rules:
- retain for compliance and support records

---

## 10. payment_attempts
Purpose:
- capture provider attempts and retries

Primary Key:
- payment_attempt_id

Foreign Keys:
- payment_id -> payments.payment_id

Important Columns:
- provider_name
- provider_transaction_id
- status
- response_code
- request_payload_hash
- created_at

Indexes:
- idx_payment_attempts_payment_id
- idx_payment_attempts_provider_tx_id

Retention Rules:
- retain for reconciliation and fraud analysis

---

## 11. payment_status_history
Purpose:
- track payment lifecycle state changes

Primary Key:
- payment_status_history_id

Foreign Keys:
- payment_id -> payments.payment_id

Important Columns:
- previous_status
- new_status
- changed_at
- changed_by
- notes

Indexes:
- idx_payment_status_history_payment_id
- idx_payment_status_history_changed_at

Retention Rules:
- retain for dispute and audit resolution

---

## 12. notification_delivery_status
Purpose:
- record send and failure status for notification messages

Primary Key:
- notification_delivery_id

Foreign Keys:
- notification_id -> notifications.notification_id

Important Columns:
- channel
- status
- sent_at
- delivered_at
- failed_at
- provider_response

Indexes:
- idx_notification_delivery_notification_id
- idx_notification_delivery_status

Retention Rules:
- store for operational and support analysis
- subject to notification retention policy

---

## 13. report_jobs
Purpose:
- capture async generation pipelines

Primary Key:
- report_job_id

Foreign Keys:
- report_id -> reports.report_id
- requested_by -> users.user_id

Important Columns:
- job_status
- started_at
- completed_at
- parameters_json
- output_location

Indexes:
- idx_report_jobs_report_id
- idx_report_jobs_status
- idx_report_jobs_requested_by

Retention Rules:
- retain job metadata for operational analysis

---

## 14. kyc_verification_results
Purpose:
- store approval/rejection of KYC verification

Primary Key:
- kyc_verification_id

Foreign Keys:
- customer_id -> customers.customer_id
- reviewed_by -> users.user_id

Important Columns:
- verification_status
- verification_type
- result_reason
- processed_at

Indexes:
- idx_kyc_verification_customer_id
- idx_kyc_verification_status

Retention Rules:
- secure retention per KYC policy

---

## 15. project_tasks
Purpose:
- support project execution and delivery operations

Primary Key:
- project_task_id

Foreign Keys:
- project_id -> projects.project_id
- assignee_user_id -> users.user_id

Important Columns:
- task_name
- status
- due_date
- completion_date
- priority

Indexes:
- idx_project_tasks_project_id
- idx_project_tasks_assignee
- idx_project_tasks_status

Retention Rules:
- retain for operational lifecycle

---

## 16. outbox_events
Purpose:
- ensure reliable asynchronous event publication

Primary Key:
- outbox_event_id

Important Columns:
- aggregate_type
- aggregate_id
- event_type
- payload_json
- occurred_at
- processed_at
- status

Indexes:
- idx_outbox_events_status
- idx_outbox_events_occurred_at

Retention Rules:
- retain for operational event replay and recovery
- purge after safe replay window

---

## 17. webhook_events
Purpose:
- support payload delivery to external systems

Primary Key:
- webhook_event_id

Important Columns:
- endpoint_url
- payload_json
- status
- attempts
- last_attempt_at
- next_attempt_at

Indexes:
- idx_webhook_events_status
- idx_webhook_events_next_attempt_at

Retention Rules:
- retain until success or safe dead-lettering

---

## 18. audit_log_payloads
Purpose:
- centralize payload and change history for audit tables

Primary Key:
- audit_log_payload_id

Foreign Keys:
- audit_log_id -> audit_log.audit_log_id

Important Columns:
- old_value_json
- new_value_json
- diff_json

Indexes:
- idx_audit_log_payloads_audit_log_id

Retention Rules:
- retain for compliance and forensic investigation

---

# Schema Alignment Tasks

The following tasks are required to align the physical model with the business and traceability documents.

## 1. Resolve naming inconsistencies
- standardize singular vs plural naming
- align table names to the canonical dictionary
- standardize snake_case and foreign key naming

## 2. Resolve domain merge gaps
- unify customer, user, and customer profile models
- ensure service request and visit records align with state model
- align complaint and escalation ownership semantics
- unify audit and platform configuration definitions

## 3. Harmonize lifecycle state values
- create one authoritative enum/state table or controlled value set
- ensure all modules reference the same state transitions
- remove drift between SRS and implementation states

## 4. Align with traceability matrix
- match all SRS requirements to tables and fields
- ensure each critical feature has a data backing model
- verify no requirement exists without a persistence artifact

## 5. Confirm soft-deletion semantics
- define global soft delete column conventions
- ensure deletion logic is consistent across domain tables
- decide whether inactive records remain visible with filters

## 6. Align retention and compliance controls
- define data retention for files, logs, and customer records
- ensure PII handling and deletion rules are reflected in schema and API flow

---

# Flyway Migration Roadmap

Each migration should be versioned and transactional. The version order below is representative and should be refined during implementation.

## V001 - Foundation and Schema Bootstrapping
Purpose:
- create baseline tables and extension support

Tables:
- users
- roles
- permissions
- user_roles
- permission_role_map

Constraints:
- PK/FK integrity
- unique indexes for role and permission names

Indexes:
- idx_users_email_unique
- idx_roles_name_unique

Data Migration Impact:
- seed default roles and permissions
- set bootstrapped admin account data if required

---

## V002 - Customer and Profile Schema
Purpose:
- create customer domain tables

Tables:
- customers
- customer_profiles
- customer_addresses
- customer_preferences

Constraints:
- FK to users
- unique customer external identifiers if applicable

Indexes:
- idx_customers_user_id
- idx_customer_profiles_customer_id

Data Migration Impact:
- migrate legacy customer records into base domain

---

## V003 - Property Core Schema
Purpose:
- create property and address tables

Tables:
- properties
- property_addresses
- property_media
- property_documents

Constraints:
- property status constraints
- property owner FK integrity

Indexes:
- idx_properties_customer_id
- idx_property_addresses_property_id
- idx_properties_status

Data Migration Impact:
- load initial seed property records if any

---

## V004 - Ownership and Verification Schema
Purpose:
- implement property ownership and verification lifecycle

Tables:
- property_ownership
- property_ownership_history
- property_verifications
- property_status_history

Constraints:
- enforce legal ownership history model
- verification status values

Indexes:
- idx_property_ownership_property_id
- idx_property_verifications_property_id

Data Migration Impact:
- backfill existing property status history if available

---

## V005 - Service Catalog and Request Workflow
Purpose:
- create service domain foundations

Tables:
- service_catalog
- service_request_types
- service_requests
- service_request_history
- service_request_assignments
- service_requirement_items

Constraints:
- status transition rule support
- FK integrity for assignments

Indexes:
- idx_service_requests_customer_id
- idx_service_request_assignments_request_id

Data Migration Impact:
- seed catalog entries
- map legacy requests if present

---

## V006 - Agent and Visit Support
Purpose:
- enable agent work and visit tracking

Tables:
- agents
- agent_profiles
- agent_availability
- agent_assignments
- visits
- visit_status_history
- visit_evidence

Constraints:
- agent assignment invariants
- scheduled and completed visit constraints

Indexes:
- idx_agents_user_id
- idx_agent_assignments_request_id
- idx_visits_agent_id

Data Migration Impact:
- initialize agent records
- migrate assigned visits if any

---

## V007 - Evidence and File Metadata
Purpose:
- implement evidence metadata and object references

Tables:
- evidence_records
- evidence_metadata
- evidence_file_links
- evidence_review_queue

Constraints:
- FK to service and visit tables
- object storage path validation

Indexes:
- idx_evidence_records_request_id
- idx_evidence_records_review_status

Data Migration Impact:
- backfill media asset metadata if existing files exist

---

## V008 - Subscription and Billing
Purpose:
- implement subscriptions, billing cycle, and plan structures

Tables:
- subscription_plans
- subscriptions
- billing_cycles
- renewal_history
- plan_features

Constraints:
- status, plan validity, and renewal regulations

Indexes:
- idx_subscriptions_customer_id
- idx_billing_cycles_subscription_id

Data Migration Impact:
- seed plan catalog and existing subscriptions

---

## V009 - Payment Processing
Purpose:
- support payment lifecycle and provider integration

Tables:
- payments
- invoices
- payment_attempts
- refunds
- payment_status_history
- payment_gateway_responses

Constraints:
- unique provider transaction mapping
- status and amount validation

Indexes:
- idx_payments_customer_id
- idx_invoices_subscription_id
- idx_payments_status

Data Migration Impact:
- migrate existing payment records if available

---

## V010 - Notifications and Messaging
Purpose:
- support message templates and delivery outcomes

Tables:
- notification_templates
- notifications
- notification_preferences
- notification_channels
- notification_delivery_status
- notification_failures

Constraints:
- channel and status validation
- unique template code values if designed

Indexes:
- idx_notifications_user_id
- idx_notification_delivery_status_notification_id

Data Migration Impact:
- seed templates and default notification preferences

---

## V011 - Complaint and Escalation
Purpose:
- implement complaint handling and escalation schemas

Tables:
- complaints
- complaint_threads
- complaint_resolution
- complaint_escalations

Constraints:
- lifecycle constraints and actor tracking
- escalation policy links

Indexes:
- idx_complaints_customer_id
- idx_complaint_threads_complaint_id

Data Migration Impact:
- migrate existing complaint records if any

---

## V012 - Marketplace and Vendor
Purpose:
- create marketplace and vendor foundation

Tables:
- vendors
- vendor_profiles
- vendor_services
- marketplace_listings
- listing_enquiries
- listing_categories

Constraints:
- listing visibility state
- vendor service association rules

Indexes:
- idx_marketplace_listings_status
- idx_listing_enquiries_listing_id

Data Migration Impact:
- seed categories and vendor lists if required

---

## V013 - KYC and Compliance
Purpose:
- support KYC onboarding and verification

Tables:
- kyc_profiles
- kyc_documents
- kyc_verification_requests
- kyc_verification_results
- kyc_status_history

Constraints:
- controlled status values
- document relation restrictions

Indexes:
- idx_kyc_profiles_customer_id
- idx_kyc_verification_results_customer_id

Data Migration Impact:
- migrate verification status if legacy compliance data exists

---

## V014 - Audit, Administration, and Outbox
Purpose:
- finalize governance and operational reliability

Tables:
- audit_log
- audit_log_events
- audit_log_payloads
- admin_configurations
- feature_flags
- outbox_events
- webhook_events

Constraints:
- append-only audit log
- event processing status states

Indexes:
- idx_audit_log_user_id
- idx_outbox_events_status
- idx_webhook_events_status

Data Migration Impact:
- seed config values
- create default feature flags
- initialize admin settings

---

## V015 - Reporting and Analytics Schema
Purpose:
- support dashboard and reporting workloads

Tables:
- reports
- report_templates
- report_jobs
- report_exports
- report_schedules
- report_metrics

Constraints:
- report generation status
- export and reporting storage location constraints

Indexes:
- idx_report_jobs_status
- idx_report_exports_report_id

Data Migration Impact:
- seed report templates and admin-level report definitions

---

## V016 - Project and Construction Data
Purpose:
- support internal planning and site operations

Tables:
- projects
- project_members
- project_tasks
- project_milestones
- construction_sites
- site_activities
- site_checklists

Constraints:
- project ownership and task assignment rules

Indexes:
- idx_project_tasks_project_id
- idx_construction_sites_property_id

Data Migration Impact:
- initialize internal project metadata if present

---

# Index Strategy

## Lookup Indexes
Create indexes for:
- user_id lookups
- customer_id lookups
- property_id lookups
- service_request_id lookups
- payment_id lookups
- complaint_id lookups
- agent_id lookups
- report_id lookups

## Search Indexes
Use:
- PostgreSQL trigram indexes for fuzzy matching
- full-text search for property and listing search
- email/name lookup indexes
- tenant-aware search indexes if multi-tenancy is implemented

## Reporting Indexes
Add indexes for:
- date ranges
- status filters
- assignee filters
- tenant or customer filters
- report generation filter sets

## Composite Indexes
Common composite indexes:
- (customer_id, created_at)
- (status, created_at)
- (property_id, status)
- (agent_id, status, scheduled_at)
- (service_request_id, created_at)
- (customer_id, subscription_status)
- (provider_transaction_id, created_at)

---

# Partition Strategy

## Large Tables
Likely partition candidates:
- audit_log
- notifications
- service_requests
- complaints
- payments
- outbox_events
- report_jobs
- property_history tables

## Partition Keys
Recommended keys:
- created_at for time-based data
- customer_id or tenant_id for user-centric partitions
- property_id for property-specific historical tables
- status for operational queue tables

## Retention
- append-only large tables should be partitioned by month or quarter
- old partitions should be archived to cold storage after retention period
- archive pipelines should preserve referential integrity and queryability

## Archival
- move cold partitions to object storage or archive tables
- preserve metadata and summary tables for reporting
- maintain secure retention and purge workflows

---

# Multi-Tenant Strategy

PropertyPilot may eventually require tenant-aware or multi-entity structures. Even if not immediate, schema should permit future expansion.

## Tenant Keys
Add tenant_id or organization_id to:
- users
- customers
- properties
- service_requests
- subscriptions
- complaints
- audit_logs
- reports

## Isolation Model
Recommended:
- single-tenant database in MVP
- tenant-scoped rows when necessary
- future migration path to logical multi-tenancy

## Indexes
- indexes on tenant_id + entity_id
- composite indexes on tenant_id + status + created_at

## Security Controls
- restrict tenant data access by role and ownership
- ensure all queries enforce tenant scope
- isolate admin and operations data from customer and agent data

---

# Audit Strategy

## Audit Tables
Core audit tables:
- audit_log
- audit_log_events
- audit_log_payloads
- privileged_action_logs
- config_change_history

## Change Tracking
Track:
- actor_id
- actor_role
- action_type
- entity_type
- entity_id
- timestamp
- old_value
- new_value
- reason
- request_id

## Retention
- keep audit and forensic logs for required duration
- separate active operational logs from long-term archive
- preserve tamper-evidence semantics

## Compliance
- restrict access to privileged users
- ensure logs are append-only
- preserve source system metadata for legal and security investigation

---

# Data Integrity Rules

## Referential Integrity
- all foreign keys enforced in PostgreSQL
- no orphan records for property, customer, service, subscription, or payment relationships
- property ownership history must never overwrite prior valid records without explicit archival process

## Unique Constraints
- unique email / phone / username where appropriate
- unique role name
- unique provider transaction id where defined
- unique subscription status record per current active period if required

## Validation Rules
- non-negative amounts
- valid status transitions only
- valid date ordering
- attachment file size limits
- valid enum values

## Soft Deletes
- use soft delete columns on business entities:
  - is_deleted
  - deleted_at
  - deleted_by
- do not physically delete critical records without explicit archival workflow

## Versioning
- version critical records such as:
  - property ownership
  - service requests
  - subscriptions
  - payment lifecycle
  - configuration
- use version tracking for audit and rollback support

---

# Performance Strategy

## Expected Volume
For MVP:
- moderate number of customers and properties
- moderate service request throughput
- low-to-mid operational concurrency
- manageable analytics and report generation

## Growth Forecast
Expected growth:
- customer and property creation will increase with adoption
- service request volume will spike by season and geography
- payment and analytics tables require ongoing optimization
- notifications and audit logs may become large rapidly

## Scaling Plan
- vertical scaling for initial DB
- read replicas for reporting and analytics later
- partitioning for audit and notification tables
- query and index optimization based on production metrics
- caching for hot list and summary endpoints
- asynchronous report generation for large jobs

---

# Security Controls

## PII Protection
- store PII only when required
- mask fields in logs and non-production environments
- apply field-level encryption where necessary
- restrict access to customer and KYC records

## Encryption
- encrypt PostgreSQL at rest
- encrypt Redis where supported
- encrypt object storage data
- use TLS for all data in transit
- restrict DB and storage access to service roles

## Data Access Controls
- DB role separation for app, replication, admin, and reporting
- no ad-hoc access to production database
- row and service-level authorization by design
- audit privileged access and data exports

## Auditability
- capture all state-changing actions
- store actor, request ID, and timestamp
- preserve change payloads for investigations

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Schema drift from physical model | inconsistent database behavior | enforce Flyway and schema diff checks | Database Engineering
Missing FK or lifecycle integrity | orphan records and broken workflows | add constraint validation and CI checks | Backend Architecture
Unbounded audit growth | slow queries and storage cost increase | partitioning and retention policy | Platform Engineering
Weak retention policy | compliance and privacy exposure | set retention and purge plan early | Security / Compliance
Incomplete search strategy | poor property and listing lookup | add search index strategy and benchmark | Architecture
Payment table complexity | reconciliation and audit errors | enforce state model and idempotency | Payment Engineering
Service workflow drift | broken business flows | align SRS, state model, and SQL constraints | Product / Backend
Inconsistent soft delete semantics | data confusion and reporting errors | centralize deletion policy and conventions | Database Architecture
Large object storage metadata gaps | file tracking issues | keep metadata in DB with strict lifecycle rules | Backend / Storage
Unplanned multi-tenant expansion | expensive refactor | embed tenant_id in core tables | Architecture

---

# Delivery Roadmap

## Sprint 1
Focus:
- base schema and migration setup
- users, roles, permissions
- customer and profile tables
- foundational indexes and constraints

Deliverables:
- V001-V003 production-ready baseline
- migration and seed scripts
- user/session foundation
- baseline data integrity checks

---

## Sprint 2
Focus:
- property, ownership, service domain
- service request and status tracking
- property verification tables

Deliverables:
- property core schema
- ownership and verification tables
- service request schema
- history and evidence linking

---

## Sprint 3
Focus:
- agent, visit, evidence, complaint, notification
- queue and assignment tables

Deliverables:
- visit and evidence domain
- complaint lifecycle
- notification and delivery tables
- assignment and queue state schema

---

## Sprint 4
Focus:
- subscription, payment, report, KYC
- admin and audit tables
- outbox and event data stores

Deliverables:
- subscription and payment schema
- report generation and export tables
- KYC and admin config tables
- audit and outbox support

---

## Sprint 5
Focus:
- optimization, partitioning, analytics, production hardening
- testing and schema validation for deployment

Deliverables:
- partitioned large tables
- production tuning
- index review and query optimization
- full schema validation and UAT sign-off

---

# Success Criteria

The database is ready for:
- OpenAPI completion
- backend development
- frontend integration
- production deployment

When all of the following are true:
- all critical entities are implemented in Flyway
- all core PK/FK and uniqueness rules are enforced
- performance index set is validated
- audit and retention rules are in place
- PII and security controls are enforced
- migration rollback and recovery is validated
- schema and traceability matrix are aligned
- UAT dataset and data refresh flow are complete
- production readiness checklist is signed off

---

# Final Summary

PropertyPilot’s database foundation is strong conceptually but requires disciplined implementation sequencing to become production-ready. The main work is not only schema creation but also alignment across the physical model, traceability matrix, cross-cutting requirements, and operational SRD needs.

The recommended route is:
- create a strict core schema first
- implement high-risk operational modules next
- add reporting, audit, and compliance structures early
- use Flyway as the single migration source of truth
- enforce data integrity and retention rules from day one
- make indexing, partitioning, and search strategy explicit before scale-up

This plan transforms the current logical design into a migration-based implementation path that is production-aligned, auditable, and scalable for MVP and enterprise growth.
