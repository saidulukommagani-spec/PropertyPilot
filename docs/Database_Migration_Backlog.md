````markdown
# Database Migration Backlog

Document Type: Data Migration Implementation Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Data Architecture / Engineering

---

## Purpose

This backlog captures the remaining database migration work required to complete the PropertyPilot MVP data model and align implementation with the approved physical schema.

It is derived from:
- Database_Physical_Model.md
- Schema_Completion_Backlog.md

The backlog is structured to identify missing database objects, attributes, constraints, and indexes, and to sequence the migration work in a controlled, implementation-ready order.

---

# 1. Migration Strategy

## Guiding Rules
- Migrate in dependency order: reference tables first, transactional tables second, operational/reporting tables last.
- Add constraints only after the associated tables are populated or during zero-downtime migration windows.
- Avoid adding soft schema changes in the same release as a high-risk data migration.
- All migrations must be idempotent and rollback-tested.
- All new columns must include default values or backfill strategy where required.

---

# 2. Missing Tables

| Priority | Table Name | Purpose | Reason Missing | Effort Estimate |
|---|---|---|---|---|
| Critical | otp_verifications | Track OTP issuance, expiry, and validation results | Needed for secure customer and agent login | M |
| Critical | user_sessions | Track active sessions and refresh/token state | Needed for auth lifecycle | M |
| Critical | property_verification | Track GPS, document, and ownership verification status | Needed for property onboarding + trust checks | M |
| Critical | service_request_evidence | Store uploaded evidence and metadata for service requests | Required for audits and confirmations | M |
| Critical | service_request_assignments | Track assignment and reassignment state | Needed for agent-task flow | M |
| Critical | payment_transactions | Record payment provider and transaction metadata | Needed for billing and reconciliation | M |
| Critical | payment_status_history | Maintain payment lifecycle transitions | Required for traceability and retries | S |
| High | notification_preferences | Store user notification channels and settings | Required for notification delivery control | S |
| High | complaint_cases | Track customer complaints and root ownership | Required for support workflow | M |
| High | report_jobs | Track asynchronous generated reports | Required for operational reporting | M |
| High | audit_logs | Centralize controlled audit events | Required for governance and security | M |
| High | user_roles | Map users to allowed access roles | Required for RBAC enforcement | S |
| High | user_permissions | Track permission definitions | Required for secure authorization | S |
| Medium | billing_cycles | Track recurring subscription billing periods | Required for subscription lifecycle | S |
| Medium | subscription_status_history | Track plan state transitions | Required for plan lifecycle traceability | S |
| Medium | report_export_jobs | Track report exports and delivery metadata | Required for operational reporting | S |
| Medium | property_status_history | Track lifecycle changes for properties | Required for auditability | S |
| Medium | service_request_status_history | Track status transitions for service requests | Required for event history and support | S |

Legend:
- S = Small
- M = Medium
- L = Large

---

# 3. Missing Columns

| Table | Missing Column | Type | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| customers | phone_verified_at | timestamp | Track when customer phone was validated | Critical | S |
| customers | status | enum | Support lifecycle state | Critical | S |
| customers | consent_notifications | boolean | Capture messaging consent | High | S |
| customers | consent_terms | boolean | Record legal acknowledgement | High | S |
| properties | ownership_verified_at | timestamp | Track verification completion | Critical | S |
| properties | gps_verified_at | timestamp | Track GPS validation | Critical | S |
| properties | verification_status | enum | Store property verification lifecycle state | Critical | S |
| properties | owner_id | uuid | Link to owner/customer identity | Critical | S |
| properties | geolocation_lat | decimal | Store GPS latitude | High | S |
| properties | geolocation_lng | decimal | Store GPS longitude | High | S |
| services | is_active | boolean | Mark active services in catalog | Critical | S |
| services | base_price | decimal | Capture default pricing | Critical | S |
| services | duration_minutes | integer | Service duration metadata | High | S |
| service_requests | assigned_agent_id | uuid | Link active assignment to agent | Critical | S |
| service_requests | payment_status | enum | Track payment state | Critical | S |
| service_requests | evidence_count | integer | Support quick UI summary | Medium | S |
| service_requests | preferred_date | date | Store booking date | Critical | S |
| service_requests | preferred_time | time | Store booking time | Critical | S |
| service_requests | request_status | enum | Standardized workflow status | Critical | S |
| service_requests | gps_verified | boolean | Capture GPS validation completion | Critical | S |
| payments | booking_id | uuid | Link payment to booking or request | Critical | S |
| payments | provider_reference | varchar | Store external transaction reference | Critical | S |
| payments | payment_method | varchar | Capture payment method details | High | S |
| payments | refunded_amount | decimal | Support refund tracking | High | S |
| subscriptions | plan_id | uuid | Link to active plan | Critical | S |
| subscriptions | auto_renew | boolean | Track renewal behavior | High | S |
| subscriptions | cancelled_at | timestamp | Capture cancellation lifecycle | High | S |
| subscriptions | next_billing_date | date | Support cycle tracking | High | S |
| agent_assignments | assigned_at | timestamp | Track assignment timestamp | Critical | S |
| agent_assignments | reassigned_at | timestamp | Capture reassignment events | Medium | S |
| notifications | channel | enum | Record notification channel | High | S |
| notifications | delivered_at | timestamp | Track send status | High | S |
| notifications | read_at | timestamp | Track user read state | Medium | S |
| complaints | associated_request_id | uuid | Link complaint to service request | High | S |
| complaints | status | enum | Complaint lifecycle state | High | S |
| complaints | assigned_to | uuid | Support triage assignment | High | S |
| report_jobs | status | enum | Track job lifecycle | Medium | S |
| report_jobs | generated_at | timestamp | Track report creation | Medium | S |

---

# 4. Missing Constraints

| Constraint Name | Table | Constraint Type | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| fk_customers_primary_profile | customers | foreign key | Ensure profile state references valid data model | High | S |
| ck_customers_status | customers | check | Restrict status to valid lifecycle states | Critical | S |
| uq_customers_phone | customers | unique | Prevent duplicate phone number registration | Critical | S |
| uq_customers_email | customers | unique | Prevent duplicate customer emails | High | S |
| fk_properties_owner | properties | foreign key | Ensure property is linked to valid customer | Critical | M |
| ck_properties_verification_status | properties | check | Restrict verification lifecycle states | Critical | S |
| fk_service_requests_property | service_requests | foreign key | Ensure booking references real property | Critical | S |
| fk_service_requests_customer | service_requests | foreign key | Ensure service request belongs to valid customer | Critical | S |
| fk_service_requests_agent | service_requests | foreign key | Ensure assignment references valid agent | Critical | S |
| ck_service_requests_status | service_requests | check | Restrict allowed request states | Critical | S |
| fk_payments_booking | payments | foreign key | Ensure payment reference is valid | Critical | S |
| ck_payments_status | payments | check | Restrict payment lifecycle values | Critical | S |
| fk_subscriptions_customer | subscriptions | foreign key | Ensure subscription belongs to valid customer | Critical | S |
| fk_subscriptions_plan | subscriptions | foreign key | Ensure subscription references valid plan | Critical | S |
| ck_subscriptions_status | subscriptions | check | Restrict valid subscription states | High | S |
| fk_agent_assignments_request | agent_assignments | foreign key | Ensure assignment references valid request | Critical | S |
| fk_agent_assignments_agent | agent_assignments | foreign key | Ensure assignment references valid agent | Critical | S |
| ck_notifications_channel | notifications | check | Restrict supported notification channels | High | S |
| fk_notifications_customer | notifications | foreign key | Ensure notification belongs to valid customer | High | S |
| fk_complaints_customer | complaints | foreign key | Ensure complaint is linked to valid customer | High | S |
| ck_complaints_status | complaints | check | Restrict support states | High | S |
| fk_report_jobs_user | report_jobs | foreign key | Ensure report jobs are tied to valid user | Medium | S |

---

# 5. Missing Indexes

| Index Name | Table | Columns | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| idx_customers_phone | customers | phone | Fast lookup for customer login and deduplication | Critical | S |
| idx_customers_email | customers | email | Fast lookup for customer profile resolution | High | S |
| idx_properties_owner_id | properties | owner_id | Fast listing and lookup for property portfolios | Critical | S |
| idx_properties_status | properties | status | Support property management view | Medium | S |
| idx_service_requests_customer_id | service_requests | customer_id | Support fast customer request lookup | Critical | S |
| idx_service_requests_property_id | service_requests | property_id | Support property request search | Critical | S |
| idx_service_requests_status | service_requests | request_status | Support queue and triage display | Critical | S |
| idx_service_requests_assigned_agent_id | service_requests | assigned_agent_id | Support agent assignment and queue views | Critical | S |
| idx_payments_booking_id | payments | booking_id | Support payment lookup and reconciliation | Critical | S |
| idx_payments_status | payments | payment_status | Support payment workflow monitoring | High | S |
| idx_subscriptions_customer_id | subscriptions | customer_id | Support subscription search | High | S |
| idx_subscriptions_status | subscriptions | status | Support plan lifecycle tracking | High | S |
| idx_agent_assignments_agent_id | agent_assignments | agent_id | Support agent dashboard and queue queries | Critical | S |
| idx_agent_assignments_status | agent_assignments | status | Support assignment queue filtering | Critical | S |
| idx_notifications_customer_id | notifications | customer_id | Support notification retrieval | High | S |
| idx_notifications_status | notifications | status | Support unread and archived filtering | Medium | S |
| idx_complaints_customer_id | complaints | customer_id | Support complaint lookup and triage | High | S |
| idx_report_jobs_status | report_jobs | status | Support async report monitoring | Medium | S |

---

# 6. Migration Sequence

## Phase 1: Foundation and Identity
1. Create `users`
2. Create `user_roles`
3. Create `user_permissions`
4. Create `user_sessions`
5. Create `otp_verifications`
6. Add all identity-related columns and constraints
7. Add indexes for login and user lookup

Priority: Critical  
Effort: M

## Phase 2: Customer and Property Domain
1. Create `customers`
2. Create `customer_profiles`
3. Create `customer_preferences`
4. Create `properties`
5. Create `property_verification`
6. Create `property_status_history`
7. Add customer/property-related constraints and indexes

Priority: Critical  
Effort: M

## Phase 3: Service and Assignment Domain
1. Create `services`
2. Create `service_categories`
3. Create `service_requests`
4. Create `service_request_status_history`
5. Create `service_request_assignments`
6. Create `service_request_evidence`
7. Add queue, status, and assignment indexes

Priority: Critical  
Effort: M

## Phase 4: Subscription and Payment Domain
1. Create `subscription_plans`
2. Create `subscriptions`
3. Create `billing_cycles`
4. Create `payment_transactions`
5. Create `payments`
6. Create `payment_status_history`
7. Create `invoices`
8. Create `refunds`
9. Add payment and billing indexes and constraints

Priority: Critical  
Effort: L

## Phase 5: Agent and Operations Domain
1. Create `agents`
2. Create `agent_profiles`
3. Create `agent_availability`
4. Create `agent_visit_logs`
5. Create `complaint_cases`
6. Add operational assignment and complaint constraints

Priority: High  
Effort: M

## Phase 6: Notifications and Reporting
1. Create `notifications`
2. Create `notification_templates`
3. Create `notification_preferences`
4. Create `report_jobs`
5. Create `report_exports`
6. Create `report_templates`
7. Add notification and reporting indexes

Priority: High  
Effort: M

## Phase 7: Security and Audit
1. Create `audit_logs`
2. Create `event_logs`
3. Add access and data retention enforcement rules
4. Add final RBAC and audit indexes

Priority: Critical  
Effort: M

---

# 7. Priority Summary

## Critical
- otp_verifications
- user_sessions
- property_verification
- service_request_evidence
- service_request_assignments
- payment_transactions
- customers phone/email uniqueness
- service request and property foreign keys
- all key index coverage for login, request lookup, queue, assignment, and payment

## High
- notification_preferences
- complaint_cases
- report_jobs
- user_roles / permissions
- billing cycle and subscription lifecycle status tracking

## Medium
- report export tables
- property and request history
- lower-priority operational metrics tables
- phase-delayed reporting and summary tables

---

# 8. Effort Estimate

| Area | Effort |
|---|---|
| Identity + auth data model | M |
| Customer + property model completion | M |
| Service + assignment model completion | M |
| Subscription + payment model completion | L |
| Agent + complaint model completion | M |
| Notification + reporting model completion | M |
| Audit + security model completion | M |
| Total | L |

Estimated total implementation effort: Large

Note: The effort estimate assumes the project executes within a controlled MVP scope and uses phased migration releases rather than a single monolithic cutover.

---

# 9. Risks and Dependencies

## Risks
- schema drift during parallel backend implementation
- missing unique constraints causing duplicate records
- payment and subscription data not linked cleanly to service requests
- inconsistent handling of GPS and ownership verification lifecycle
- missing indexes causing poor performance in assignment and reporting queries
- missing audit logging for sensitive modifications

## Dependencies
- Completed API contract freeze
- Approved final physical schema
- Production environment readiness for schema migration
- Data backfill and validation plan
- Spot-check of production rollback plan

---

# 10. Recommended Backlog Execution

## Sprint 1 Migration Scope
- OTP and user session tables
- customer and property entities
- core service request table and assignment table
- basic audit logging
- key indexes for login and request lookup

## Sprint 2 Migration Scope
- payment and subscription data model
- complaint and notification models
- report job setup
- compliance and security tables

## Sprint 3 Migration Scope
- reporting optimization
- performance tuning
- final index and constraint stabilization
- migration rollback validation

---

# 11. Final Recommendation

The database backlog is implementation-ready but not yet complete. The project should not proceed to broad backend implementation without closing the critical gaps in:
- authentication tables
- service request and assignment tables
- payment and subscription tables
- verification and audit records
- final constraint and index definitions

The database should be treated as a staging-controlled delivery area: build the core schema first, validate migration behavior, then extend into broader operational data stores.

This backlog is the minimum required migration path for a stable MVP implementation.
```// filepath: c:\PropertyPilot\docs\Database_Migration_Backlog.md
# Database Migration Backlog

Document Type: Data Migration Implementation Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Data Architecture / Engineering

---

## Purpose

This backlog captures the remaining database migration work required to complete the PropertyPilot MVP data model and align implementation with the approved physical schema.

It is derived from:
- Database_Physical_Model.md
- Schema_Completion_Backlog.md

The backlog is structured to identify missing database objects, attributes, constraints, and indexes, and to sequence the migration work in a controlled, implementation-ready order.

---

# 1. Migration Strategy

## Guiding Rules
- Migrate in dependency order: reference tables first, transactional tables second, operational/reporting tables last.
- Add constraints only after the associated tables are populated or during zero-downtime migration windows.
- Avoid adding soft schema changes in the same release as a high-risk data migration.
- All migrations must be idempotent and rollback-tested.
- All new columns must include default values or backfill strategy where required.

---

# 2. Missing Tables

| Priority | Table Name | Purpose | Reason Missing | Effort Estimate |
|---|---|---|---|---|
| Critical | otp_verifications | Track OTP issuance, expiry, and validation results | Needed for secure customer and agent login | M |
| Critical | user_sessions | Track active sessions and refresh/token state | Needed for auth lifecycle | M |
| Critical | property_verification | Track GPS, document, and ownership verification status | Needed for property onboarding + trust checks | M |
| Critical | service_request_evidence | Store uploaded evidence and metadata for service requests | Required for audits and confirmations | M |
| Critical | service_request_assignments | Track assignment and reassignment state | Needed for agent-task flow | M |
| Critical | payment_transactions | Record payment provider and transaction metadata | Needed for billing and reconciliation | M |
| Critical | payment_status_history | Maintain payment lifecycle transitions | Required for traceability and retries | S |
| High | notification_preferences | Store user notification channels and settings | Required for notification delivery control | S |
| High | complaint_cases | Track customer complaints and root ownership | Required for support workflow | M |
| High | report_jobs | Track asynchronous generated reports | Required for operational reporting | M |
| High | audit_logs | Centralize controlled audit events | Required for governance and security | M |
| High | user_roles | Map users to allowed access roles | Required for RBAC enforcement | S |
| High | user_permissions | Track permission definitions | Required for secure authorization | S |
| Medium | billing_cycles | Track recurring subscription billing periods | Required for subscription lifecycle | S |
| Medium | subscription_status_history | Track plan state transitions | Required for plan lifecycle traceability | S |
| Medium | report_export_jobs | Track report exports and delivery metadata | Required for operational reporting | S |
| Medium | property_status_history | Track lifecycle changes for properties | Required for auditability | S |
| Medium | service_request_status_history | Track status transitions for service requests | Required for event history and support | S |

Legend:
- S = Small
- M = Medium
- L = Large

---

# 3. Missing Columns

| Table | Missing Column | Type | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| customers | phone_verified_at | timestamp | Track when customer phone was validated | Critical | S |
| customers | status | enum | Support lifecycle state | Critical | S |
| customers | consent_notifications | boolean | Capture messaging consent | High | S |
| customers | consent_terms | boolean | Record legal acknowledgement | High | S |
| properties | ownership_verified_at | timestamp | Track verification completion | Critical | S |
| properties | gps_verified_at | timestamp | Track GPS validation | Critical | S |
| properties | verification_status | enum | Store property verification lifecycle state | Critical | S |
| properties | owner_id | uuid | Link to owner/customer identity | Critical | S |
| properties | geolocation_lat | decimal | Store GPS latitude | High | S |
| properties | geolocation_lng | decimal | Store GPS longitude | High | S |
| services | is_active | boolean | Mark active services in catalog | Critical | S |
| services | base_price | decimal | Capture default pricing | Critical | S |
| services | duration_minutes | integer | Service duration metadata | High | S |
| service_requests | assigned_agent_id | uuid | Link active assignment to agent | Critical | S |
| service_requests | payment_status | enum | Track payment state | Critical | S |
| service_requests | evidence_count | integer | Support quick UI summary | Medium | S |
| service_requests | preferred_date | date | Store booking date | Critical | S |
| service_requests | preferred_time | time | Store booking time | Critical | S |
| service_requests | request_status | enum | Standardized workflow status | Critical | S |
| service_requests | gps_verified | boolean | Capture GPS validation completion | Critical | S |
| payments | booking_id | uuid | Link payment to booking or request | Critical | S |
| payments | provider_reference | varchar | Store external transaction reference | Critical | S |
| payments | payment_method | varchar | Capture payment method details | High | S |
| payments | refunded_amount | decimal | Support refund tracking | High | S |
| subscriptions | plan_id | uuid | Link to active plan | Critical | S |
| subscriptions | auto_renew | boolean | Track renewal behavior | High | S |
| subscriptions | cancelled_at | timestamp | Capture cancellation lifecycle | High | S |
| subscriptions | next_billing_date | date | Support cycle tracking | High | S |
| agent_assignments | assigned_at | timestamp | Track assignment timestamp | Critical | S |
| agent_assignments | reassigned_at | timestamp | Capture reassignment events | Medium | S |
| notifications | channel | enum | Record notification channel | High | S |
| notifications | delivered_at | timestamp | Track send status | High | S |
| notifications | read_at | timestamp | Track user read state | Medium | S |
| complaints | associated_request_id | uuid | Link complaint to service request | High | S |
| complaints | status | enum | Complaint lifecycle state | High | S |
| complaints | assigned_to | uuid | Support triage assignment | High | S |
| report_jobs | status | enum | Track job lifecycle | Medium | S |
| report_jobs | generated_at | timestamp | Track report creation | Medium | S |

---

# 4. Missing Constraints

| Constraint Name | Table | Constraint Type | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| fk_customers_primary_profile | customers | foreign key | Ensure profile state references valid data model | High | S |
| ck_customers_status | customers | check | Restrict status to valid lifecycle states | Critical | S |
| uq_customers_phone | customers | unique | Prevent duplicate phone number registration | Critical | S |
| uq_customers_email | customers | unique | Prevent duplicate customer emails | High | S |
| fk_properties_owner | properties | foreign key | Ensure property is linked to valid customer | Critical | M |
| ck_properties_verification_status | properties | check | Restrict verification lifecycle states | Critical | S |
| fk_service_requests_property | service_requests | foreign key | Ensure booking references real property | Critical | S |
| fk_service_requests_customer | service_requests | foreign key | Ensure service request belongs to valid customer | Critical | S |
| fk_service_requests_agent | service_requests | foreign key | Ensure assignment references valid agent | Critical | S |
| ck_service_requests_status | service_requests | check | Restrict allowed request states | Critical | S |
| fk_payments_booking | payments | foreign key | Ensure payment reference is valid | Critical | S |
| ck_payments_status | payments | check | Restrict payment lifecycle values | Critical | S |
| fk_subscriptions_customer | subscriptions | foreign key | Ensure subscription belongs to valid customer | Critical | S |
| fk_subscriptions_plan | subscriptions | foreign key | Ensure subscription references valid plan | Critical | S |
| ck_subscriptions_status | subscriptions | check | Restrict valid subscription states | High | S |
| fk_agent_assignments_request | agent_assignments | foreign key | Ensure assignment references valid request | Critical | S |
| fk_agent_assignments_agent | agent_assignments | foreign key | Ensure assignment references valid agent | Critical | S |
| ck_notifications_channel | notifications | check | Restrict supported notification channels | High | S |
| fk_notifications_customer | notifications | foreign key | Ensure notification belongs to valid customer | High | S |
| fk_complaints_customer | complaints | foreign key | Ensure complaint is linked to valid customer | High | S |
| ck_complaints_status | complaints | check | Restrict support states | High | S |
| fk_report_jobs_user | report_jobs | foreign key | Ensure report jobs are tied to valid user | Medium | S |

---

# 5. Missing Indexes

| Index Name | Table | Columns | Purpose | Priority | Effort |
|---|---|---|---|---|---|
| idx_customers_phone | customers | phone | Fast lookup for customer login and deduplication | Critical | S |
| idx_customers_email | customers | email | Fast lookup for customer profile resolution | High | S |
| idx_properties_owner_id | properties | owner_id | Fast listing and lookup for property portfolios | Critical | S |
| idx_properties_status | properties | status | Support property management view | Medium | S |
| idx_service_requests_customer_id | service_requests | customer_id | Support fast customer request lookup | Critical | S |
| idx_service_requests_property_id | service_requests | property_id | Support property request search | Critical | S |
| idx_service_requests_status | service_requests | request_status | Support queue and triage display | Critical | S |
| idx_service_requests_assigned_agent_id | service_requests | assigned_agent_id | Support agent assignment and queue views | Critical | S |
| idx_payments_booking_id | payments | booking_id | Support payment lookup and reconciliation | Critical | S |
| idx_payments_status | payments | payment_status | Support payment workflow monitoring | High | S |
| idx_subscriptions_customer_id | subscriptions | customer_id | Support subscription search | High | S |
| idx_subscriptions_status | subscriptions | status | Support plan lifecycle tracking | High | S |
| idx_agent_assignments_agent_id | agent_assignments | agent_id | Support agent dashboard and queue queries | Critical | S |
| idx_agent_assignments_status | agent_assignments | status | Support assignment queue filtering | Critical | S |
| idx_notifications_customer_id | notifications | customer_id | Support notification retrieval | High | S |
| idx_notifications_status | notifications | status | Support unread and archived filtering | Medium | S |
| idx_complaints_customer_id | complaints | customer_id | Support complaint lookup and triage | High | S |
| idx_report_jobs_status | report_jobs | status | Support async report monitoring | Medium | S |

---

# 6. Migration Sequence

## Phase 1: Foundation and Identity
1. Create `users`
2. Create `user_roles`
3. Create `user_permissions`
4. Create `user_sessions`
5. Create `otp_verifications`
6. Add all identity-related columns and constraints
7. Add indexes for login and user lookup

Priority: Critical  
Effort: M

## Phase 2: Customer and Property Domain
1. Create `customers`
2. Create `customer_profiles`
3. Create `customer_preferences`
4. Create `properties`
5. Create `property_verification`
6. Create `property_status_history`
7. Add customer/property-related constraints and indexes

Priority: Critical  
Effort: M

## Phase 3: Service and Assignment Domain
1. Create `services`
2. Create `service_categories`
3. Create `service_requests`
4. Create `service_request_status_history`
5. Create `service_request_assignments`
6. Create `service_request_evidence`
7. Add queue, status, and assignment indexes

Priority: Critical  
Effort: M

## Phase 4: Subscription and Payment Domain
1. Create `subscription_plans`
2. Create `subscriptions`
3. Create `billing_cycles`
4. Create `payment_transactions`
5. Create `payments`
6. Create `payment_status_history`
7. Create `invoices`
8. Create `refunds`
9. Add payment and billing indexes and constraints

Priority: Critical  
Effort: L

## Phase 5: Agent and Operations Domain
1. Create `agents`
2. Create `agent_profiles`
3. Create `agent_availability`
4. Create `agent_visit_logs`
5. Create `complaint_cases`
6. Add operational assignment and complaint constraints

Priority: High  
Effort: M

## Phase 6: Notifications and Reporting
1. Create `notifications`
2. Create `notification_templates`
3. Create `notification_preferences`
4. Create `report_jobs`
5. Create `report_exports`
6. Create `report_templates`
7. Add notification and reporting indexes

Priority: High  
Effort: M

## Phase 7: Security and Audit
1. Create `audit_logs`
2. Create `event_logs`
3. Add access and data retention enforcement rules
4. Add final RBAC and audit indexes

Priority: Critical  
Effort: M

---

# 7. Priority Summary

## Critical
- otp_verifications
- user_sessions
- property_verification
- service_request_evidence
- service_request_assignments
- payment_transactions
- customers phone/email uniqueness
- service request and property foreign keys
- all key index coverage for login, request lookup, queue, assignment, and payment

## High
- notification_preferences
- complaint_cases
- report_jobs
- user_roles / permissions
- billing cycle and subscription lifecycle status tracking

## Medium
- report export tables
- property and request history
- lower-priority operational metrics tables
- phase-delayed reporting and summary tables

---

# 8. Effort Estimate

| Area | Effort |
|---|---|
| Identity + auth data model | M |
| Customer + property model completion | M |
| Service + assignment model completion | M |
| Subscription + payment model completion | L |
| Agent + complaint model completion | M |
| Notification + reporting model completion | M |
| Audit + security model completion | M |
| Total | L |

Estimated total implementation effort: Large

Note: The effort estimate assumes the project executes within a controlled MVP scope and uses phased migration releases rather than a single monolithic cutover.

---

# 9. Risks and Dependencies

## Risks
- schema drift during parallel backend implementation
- missing unique constraints causing duplicate records
- payment and subscription data not linked cleanly to service requests
- inconsistent handling of GPS and ownership verification lifecycle
- missing indexes causing poor performance in assignment and reporting queries
- missing audit logging for sensitive modifications

## Dependencies
- Completed API contract freeze
- Approved final physical schema
- Production environment readiness for schema migration
- Data backfill and validation plan
- Spot-check of production rollback plan

---

# 10. Recommended Backlog Execution

## Sprint 1 Migration Scope
- OTP and user session tables
- customer and property entities
- core service request table and assignment table
- basic audit logging
- key indexes for login and request lookup

## Sprint 2 Migration Scope
- payment and subscription data model
- complaint and notification models
- report job setup
- compliance and security tables

## Sprint 3 Migration Scope
- reporting optimization
- performance tuning
- final index and constraint stabilization
- migration rollback validation

---

# 11. Final Recommendation

The database backlog is implementation-ready but not yet complete. The project should not proceed to broad backend implementation without closing the critical gaps in:
- authentication tables
- service request and assignment tables
- payment and subscription tables
- verification and audit records
- final constraint and index definitions

The database should be treated as a staging-controlled delivery area: build the core schema first, validate migration behavior, then extend into broader operational data stores.

This backlog is the minimum required migration path for a stable MVP implementation.
