# PropertyPilot Database Physical Model

## Version

1.0

---

# Purpose

Defines the physical database implementation for PropertyPilot.

Objectives:

- Define database tables
- Define relationships
- Define constraints
- Define indexes
- Define audit strategy
- Support implementation

---

# Database Technology

Primary Database:

```text
PostgreSQL
```

Supporting Stores:

```text
Redis
ElasticSearch
Object Storage
```

---

# Naming Standards

## Tables

snake_case

Examples:

```text
customers
properties
service_requests
reports
```

---

## Columns

snake_case

Examples:

```text
customer_id
created_at
updated_at
```

---

# Common Audit Columns

All tables shall contain:

```sql
created_at
created_by
updated_at
updated_by
version
```

---

# CUSTOMER DOMAIN

## customers

| Column | Type | Constraints |
|----------|----------|-------------|
| customer_id | UUID | PK |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NULL |
| mobile_number | VARCHAR(20) | UNIQUE |
| email | VARCHAR(255) | UNIQUE |
| status | VARCHAR(30) | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

---

## customer_addresses

| Column | Type |
|----------|------|
| address_id | UUID |
| customer_id | UUID |
| address_line_1 | VARCHAR(255) |
| city | VARCHAR(100) |
| state | VARCHAR(100) |
| pincode | VARCHAR(20) |

FK:

customer_id → customers.customer_id

---

# PROPERTY DOMAIN

## properties

| Column | Type |
|----------|------|
| property_id | UUID |
| customer_id | UUID |
| property_name | VARCHAR(255) |
| property_type | VARCHAR(50) |
| survey_number | VARCHAR(100) |
| area_sqft | DECIMAL |
| latitude | DECIMAL |
| longitude | DECIMAL |
| status | VARCHAR(30) |

---

FK:

customer_id → customers.customer_id

---

## property_documents

| Column | Type |
|----------|------|
| document_id | UUID |
| property_id | UUID |
| document_type | VARCHAR(50) |
| file_url | TEXT |
| uploaded_at | TIMESTAMP |

---

# SERVICE DOMAIN

## services

| Column | Type |
|----------|------|
| service_id | UUID |
| service_name | VARCHAR(255) |
| category | VARCHAR(100) |
| price | DECIMAL |
| active_flag | BOOLEAN |

---

## service_requests

| Column | Type |
|----------|------|
| request_id | UUID |
| customer_id | UUID |
| property_id | UUID |
| service_id | UUID |
| assigned_agent_id | UUID |
| status | VARCHAR(50) |
| scheduled_date | DATE |

---

# AGENT DOMAIN

## agents

| Column | Type |
|----------|------|
| agent_id | UUID |
| agent_name | VARCHAR(255) |
| mobile_number | VARCHAR(20) |
| rating | DECIMAL(3,2) |
| status | VARCHAR(30) |

---

## agent_skills

| Column | Type |
|----------|------|
| skill_id | UUID |
| agent_id | UUID |
| skill_name | VARCHAR(100) |
| level | INTEGER |

---

# VISIT DOMAIN

## visits

| Column | Type |
|----------|------|
| visit_id | UUID |
| request_id | UUID |
| agent_id | UUID |
| start_time | TIMESTAMP |
| end_time | TIMESTAMP |
| status | VARCHAR(30) |

---

## gps_captures

| Column | Type |
|----------|------|
| gps_id | UUID |
| visit_id | UUID |
| latitude | DECIMAL |
| longitude | DECIMAL |
| accuracy | DECIMAL |

---

# EVIDENCE DOMAIN

## evidence

| Column | Type |
|----------|------|
| evidence_id | UUID |
| visit_id | UUID |
| evidence_type | VARCHAR(50) |
| file_url | TEXT |
| captured_at | TIMESTAMP |

---

# REPORT DOMAIN

## reports

| Column | Type |
|----------|------|
| report_id | UUID |
| request_id | UUID |
| report_type | VARCHAR(100) |
| report_status | VARCHAR(50) |
| generated_at | TIMESTAMP |

---

# SUBSCRIPTION DOMAIN

## subscription_plans

## customer_subscriptions

## subscription_renewals

---

# PAYMENT DOMAIN

## payments

## invoices

## refunds

---

# PARTNER DOMAIN

## partners

## partner_services

---

# VENDOR DOMAIN

## vendors

## quotations

## vendor_assignments

---

# COMPLAINT DOMAIN

## complaints

## complaint_comments

---

# NOTIFICATION DOMAIN

## notifications

## notification_templates

---

# AUDIT DOMAIN

## audit_logs

| Column | Type |
|----------|------|
| audit_id | UUID |
| entity_type | VARCHAR(100) |
| entity_id | UUID |
| action | VARCHAR(50) |
| changed_by | UUID |
| changed_at | TIMESTAMP |

---

# Index Strategy

High Priority Indexes:

customers.mobile_number

customers.email

properties.customer_id

service_requests.status

service_requests.assigned_agent_id

reports.report_status

payments.payment_status

---

# Partition Strategy

Large Tables:

audit_logs

notifications

evidence

reports

Partition By:

```text
Month
```

---

# Data Retention

audit_logs = 7 years

reports = 7 years

evidence = 7 years

notifications = 1 year

---

# Database Size Projections

Year 1

100 GB

Year 3

1 TB

Year 5

5 TB

---

# Related Documents

Database_Architecture.md

Database_Design.md

Customer_Data_Model.md

Property_Data_Model.md

Revenue_Data_Model.md

# Newly Added Tables

| Table | Purpose | Key columns |
|---|---|---|
| `customer_verifications` | Record customer KYC and verification outcomes. | `customer_verification_id UUID PK`, `customer_id UUID`, `verification_type VARCHAR(50)`, `status VARCHAR(30)`, `provider_reference VARCHAR(255)`, `verified_at TIMESTAMP`, `expires_at TIMESTAMP` |
| `customer_preferences` | Store customer communication and service preferences. | `preference_id UUID PK`, `customer_id UUID`, `preference_type VARCHAR(50)`, `channel VARCHAR(30)`, `enabled BOOLEAN`, `value_json JSONB` |
| `property_owners` | Support joint, family, and role-based property ownership. | `property_owner_id UUID PK`, `property_id UUID`, `customer_id UUID`, `ownership_role VARCHAR(50)`, `ownership_status VARCHAR(30)`, `approved_at TIMESTAMP` |
| `leads` | Store captured service, subscription, marketplace, and referral leads. | `lead_id UUID PK`, `customer_id UUID NULL`, `property_id UUID NULL`, `source VARCHAR(50)`, `lead_type VARCHAR(50)`, `status VARCHAR(30)`, `priority VARCHAR(20)`, `assigned_agent_id UUID NULL`, `converted_at TIMESTAMP NULL` |
| `lead_activities` | Store lead communication, follow-up, assignment, and conversion history. | `lead_activity_id UUID PK`, `lead_id UUID`, `activity_type VARCHAR(50)`, `activity_status VARCHAR(30)`, `assigned_to UUID NULL`, `due_at TIMESTAMP NULL`, `completed_at TIMESTAMP NULL`, `notes TEXT` |
| `service_eligibility_rules` | Configure service eligibility by property type, coverage, subscription, verification, and documentation. | `eligibility_rule_id UUID PK`, `service_id UUID`, `rule_type VARCHAR(50)`, `rule_value JSONB`, `effective_from TIMESTAMP`, `effective_to TIMESTAMP NULL`, `status VARCHAR(30)` |
| `service_sla_policies` | Configure service start, completion, review, and escalation targets. | `service_sla_policy_id UUID PK`, `service_id UUID`, `property_type VARCHAR(50) NULL`, `expected_start_minutes INTEGER`, `expected_completion_minutes INTEGER`, `review_minutes INTEGER`, `escalation_rule JSONB`, `status VARCHAR(30)` |
| `service_price_rules` | Store effective dynamic-pricing rules and inputs for services. | `service_price_rule_id UUID PK`, `service_id UUID`, `property_type VARCHAR(50) NULL`, `coverage_scope JSONB`, `rule_type VARCHAR(50)`, `rule_value JSONB`, `effective_from TIMESTAMP`, `effective_to TIMESTAMP NULL`, `status VARCHAR(30)` |
| `pricing_estimates` | Preserve customer cost-calculator and booking estimate outputs. | `estimate_id UUID PK`, `customer_id UUID NULL`, `property_id UUID NULL`, `service_id UUID`, `subscription_plan_id UUID NULL`, `status VARCHAR(30)`, `pricing_breakdown JSONB`, `final_amount DECIMAL`, `expires_at TIMESTAMP NULL` |
| `service_assignments` | Record assignment of a service request to an agent or vendor. | `assignment_id UUID PK`, `request_id UUID`, `assignee_type VARCHAR(20)`, `agent_id UUID NULL`, `vendor_id UUID NULL`, `status VARCHAR(30)`, `scheduled_start TIMESTAMP NULL`, `scheduled_end TIMESTAMP NULL`, `accepted_at TIMESTAMP NULL` |
| `service_request_status_history` | Preserve auditable service-request lifecycle transitions. | `request_status_history_id UUID PK`, `request_id UUID`, `previous_status VARCHAR(50) NULL`, `new_status VARCHAR(50)`, `changed_at TIMESTAMP`, `reason TEXT NULL` |
| `monitoring_schedules` | Generate and track recurring monitoring visits for active subscriptions. | `monitoring_schedule_id UUID PK`, `customer_subscription_id UUID`, `property_id UUID`, `service_id UUID`, `frequency VARCHAR(30)`, `next_due_at TIMESTAMP`, `status VARCHAR(30)`, `last_generated_at TIMESTAMP NULL` |
| `monitoring_alerts` | Store monitoring findings, evidence references, severity, and remediation outcome. | `monitoring_alert_id UUID PK`, `property_id UUID`, `request_id UUID NULL`, `report_id UUID NULL`, `severity VARCHAR(20)`, `alert_type VARCHAR(50)`, `status VARCHAR(30)`, `recommended_service_id UUID NULL`, `acknowledged_at TIMESTAMP NULL` |
| `subscription_entitlement_consumptions` | Track included-visit entitlement, consumption, expiry, and related service request. | `entitlement_consumption_id UUID PK`, `customer_subscription_id UUID`, `service_id UUID`, `period_start DATE`, `period_end DATE`, `entitled_quantity INTEGER`, `consumed_quantity INTEGER`, `request_id UUID NULL`, `expired_at TIMESTAMP NULL` |
| `subscription_pauses` | Record allowed subscription pause periods and outcomes. | `subscription_pause_id UUID PK`, `customer_subscription_id UUID`, `pause_start DATE`, `pause_end DATE`, `reason VARCHAR(255)`, `status VARCHAR(30)`, `approved_by UUID NULL` |
| `service_ratings` | Capture customer rating and feedback for completed services and assigned providers. | `service_rating_id UUID PK`, `request_id UUID`, `customer_id UUID`, `agent_id UUID NULL`, `vendor_id UUID NULL`, `rating SMALLINT`, `comments TEXT NULL`, `submitted_at TIMESTAMP` |
| `vendor_service_mappings` | Define vendor eligibility, coverage, capacity, verification level, and commercial status for a service. | `vendor_service_mapping_id UUID PK`, `vendor_id UUID`, `service_id UUID`, `coverage_scope JSONB`, `required_verification_level VARCHAR(30)`, `capacity_status VARCHAR(30)`, `status VARCHAR(30)` |
| `marketplace_listings` | Store buy, sell, and rental marketplace listings linked to properties. | `listing_id UUID PK`, `property_id UUID`, `customer_id UUID`, `listing_type VARCHAR(30)`, `status VARCHAR(30)`, `published_at TIMESTAMP NULL`, `price DECIMAL NULL` |
| `marketplace_inquiries` | Store marketplace enquiries and lead linkage without exposing protected contact details. | `inquiry_id UUID PK`, `listing_id UUID`, `lead_id UUID NULL`, `customer_id UUID NULL`, `status VARCHAR(30)`, `created_at TIMESTAMP` |
| `marketplace_contact_disclosures` | Audit controlled buyer-seller or customer-vendor contact disclosure decisions. | `contact_disclosure_id UUID PK`, `inquiry_id UUID`, `approved_by UUID`, `status VARCHAR(30)`, `disclosed_at TIMESTAMP NULL`, `reason TEXT NULL` |
| `marketplace_commissions` | Record marketplace commission accrual, adjustment, and settlement state. | `commission_id UUID PK`, `listing_id UUID NULL`, `inquiry_id UUID NULL`, `vendor_id UUID NULL`, `amount DECIMAL`, `status VARCHAR(30)`, `settled_at TIMESTAMP NULL` |
| `nri_relationship_assignments` | Associate NRI customers/properties with an authorised relationship manager. | `nri_relationship_assignment_id UUID PK`, `customer_id UUID`, `property_id UUID NULL`, `relationship_manager_id UUID`, `status VARCHAR(30)`, `assigned_at TIMESTAMP` |

# Newly Added Relationships

| Child table | Foreign key | Parent table | Cardinality |
|---|---|---|---|
| `customer_verifications` | `customer_id` | `customers.customer_id` | Customer 1:N verification records |
| `customer_preferences` | `customer_id` | `customers.customer_id` | Customer 1:N preferences |
| `property_owners` | `property_id`, `customer_id` | `properties.property_id`, `customers.customer_id` | Property N:M customers through ownership roles |
| `leads` | `customer_id`, `property_id`, `assigned_agent_id` | `customers.customer_id`, `properties.property_id`, `agents.agent_id` | Optional customer/property context and agent assignment |
| `lead_activities` | `lead_id` | `leads.lead_id` | Lead 1:N activities |
| `service_eligibility_rules`, `service_sla_policies`, `service_price_rules` | `service_id` | `services.service_id` | Service 1:N effective rules/policies |
| `pricing_estimates` | `customer_id`, `property_id`, `service_id`, `subscription_plan_id` | `customers`, `properties`, `services`, `subscription_plans` | Estimate references selected pricing context |
| `service_assignments` | `request_id`, `agent_id`, `vendor_id` | `service_requests`, `agents`, `vendors` | Request 1:N assignment history; one assignee per assignment |
| `service_request_status_history` | `request_id` | `service_requests.request_id` | Request 1:N status transitions |
| `monitoring_schedules` | `customer_subscription_id`, `property_id`, `service_id` | `customer_subscriptions`, `properties`, `services` | Subscription 1:N recurring schedules |
| `monitoring_alerts` | `property_id`, `request_id`, `report_id`, `recommended_service_id` | `properties`, `service_requests`, `reports`, `services` | Alert links findings to the monitored service context |
| `subscription_entitlement_consumptions` | `customer_subscription_id`, `service_id`, `request_id` | `customer_subscriptions`, `services`, `service_requests` | Subscription 1:N entitlement periods/consumption records |
| `subscription_pauses` | `customer_subscription_id` | `customer_subscriptions.customer_subscription_id` | Subscription 1:N pause history |
| `service_ratings` | `request_id`, `customer_id`, `agent_id`, `vendor_id` | `service_requests`, `customers`, `agents`, `vendors` | Completed request 1:N role-linked feedback records |
| `vendor_service_mappings` | `vendor_id`, `service_id` | `vendors.vendor_id`, `services.service_id` | Vendor N:M eligible services |
| `marketplace_listings` | `property_id`, `customer_id` | `properties.property_id`, `customers.customer_id` | Property/customer 1:N listings |
| `marketplace_inquiries` | `listing_id`, `lead_id`, `customer_id` | `marketplace_listings`, `leads`, `customers` | Listing 1:N protected enquiries |
| `marketplace_contact_disclosures` | `inquiry_id` | `marketplace_inquiries.inquiry_id` | Inquiry 1:N disclosure-decision history |
| `marketplace_commissions` | `listing_id`, `inquiry_id`, `vendor_id` | `marketplace_listings`, `marketplace_inquiries`, `vendors` | Commercial outcome references |
| `nri_relationship_assignments` | `customer_id`, `property_id` | `customers.customer_id`, `properties.property_id` | NRI customer/property 1:N relationship assignments |

# Newly Added Indexes

| Table | Index | Columns |
|---|---|---|
| `customer_verifications` | `ix_customer_verifications_customer_status` | `customer_id, status, expires_at` |
| `property_owners` | `ux_property_owners_active_role` | `property_id, customer_id, ownership_role` where ownership status is active |
| `leads` | `ix_leads_status_assignee_created` | `status, assigned_agent_id, created_at DESC` |
| `leads` | `ix_leads_source_type_created` | `source, lead_type, created_at DESC` |
| `lead_activities` | `ix_lead_activities_due_status` | `assigned_to, activity_status, due_at` |
| `service_eligibility_rules` | `ix_service_eligibility_rules_active` | `service_id, status, effective_from, effective_to` |
| `service_sla_policies` | `ix_service_sla_policies_active` | `service_id, property_type, status` |
| `service_price_rules` | `ix_service_price_rules_effective` | `service_id, property_type, status, effective_from, effective_to` |
| `pricing_estimates` | `ix_pricing_estimates_customer_property_created` | `customer_id, property_id, created_at DESC` |
| `service_assignments` | `ix_service_assignments_assignee_status_schedule` | `agent_id, vendor_id, status, scheduled_start` |
| `service_request_status_history` | `ix_request_status_history_request_changed` | `request_id, changed_at DESC` |
| `monitoring_schedules` | `ix_monitoring_schedules_due_status` | `status, next_due_at` |
| `monitoring_alerts` | `ix_monitoring_alerts_property_status_severity` | `property_id, status, severity, created_at DESC` |
| `subscription_entitlement_consumptions` | `ix_subscription_entitlement_period` | `customer_subscription_id, service_id, period_start, period_end` |
| `subscription_pauses` | `ix_subscription_pauses_subscription_dates` | `customer_subscription_id, pause_start, pause_end` |
| `service_ratings` | `ix_service_ratings_request_submitted` | `request_id, submitted_at DESC` |
| `vendor_service_mappings` | `ix_vendor_service_mappings_service_status` | `service_id, status, required_verification_level` |
| `marketplace_listings` | `ix_marketplace_listings_type_status_published` | `listing_type, status, published_at DESC` |
| `marketplace_inquiries` | `ix_marketplace_inquiries_listing_status` | `listing_id, status, created_at DESC` |
| `nri_relationship_assignments` | `ix_nri_relationship_assignments_manager_status` | `relationship_manager_id, status` |

# Newly Added Constraints

| Area | Constraint |
|---|---|
| Customer verification | `verification_type` and `status` shall use configured values; an expiry date, when present, shall be later than the verification timestamp. |
| Customer preferences | Only configured communication/service preference types and channels may be stored for a customer. |
| Shared ownership | `property_owners` shall be unique for an active property/customer/ownership-role combination; only authorised owners may approve or change an ownership record. |
| Lead protection | Lead contact data shall not be copied to marketplace listing/disclosure records; disclosure requires a related approved `marketplace_contact_disclosures` record. |
| Service rules | A service eligibility, SLA, or price rule shall reference an existing service and have a valid effective date range; at most one active rule of the same type/scope may apply for an effective period. |
| Pricing estimates | `final_amount` shall be non-negative; a confirmed booking/payment shall retain its estimate/price breakdown reference. |
| Service assignment | Exactly one of `agent_id` or `vendor_id` shall be populated for each assignment; the assignee shall be active and mapped/eligible for the requested service. |
| Service lifecycle | Every service-request status change shall create a `service_request_status_history` row; a completed request shall retain its completion evidence/report reference. |
| Monitoring schedules | Schedule generation shall be idempotent for the same subscription, service, property, and due period; only active subscriptions may generate new schedules. |
| Monitoring alerts | An alert shall reference a property and may link to the triggering request/report; remediation requests shall preserve the source alert reference. |
| Subscription consumption | Consumption cannot exceed the entitled quantity for the relevant period; monthly unused visits expire at month end and annual-plan visits have no carry-forward. |
| Subscription pauses | A subscription may have no more than one approved pause in a plan year and an approved pause duration shall not exceed 60 days. |
| Service ratings | Ratings shall be between 1 and 5 and may be submitted only for a completed request by an authorised customer; duplicate customer ratings for the same request are prohibited. |
| Vendor mapping | A vendor-service mapping requires an active vendor, an existing service, valid coverage, and required verification/capacity status before assignment. |
| Marketplace | Listings shall reference an existing property/customer; inquiries shall reference an active listing; commissions shall be non-negative and traceable to the listing, inquiry, or vendor commercial event. |
| NRI relationship management | An active NRI relationship assignment shall reference an existing customer and authorised relationship manager; property scope, when present, shall belong to or be shared with that customer. |

Canonical_Data_Model.md

---

# Additional Missing Tables

All tables below use the common audit columns defined in this model unless stated otherwise.

## users

| Column | Type | Notes |
|---|---|---|
| `user_id` | UUID | Primary key |
| `full_name` | VARCHAR(150) | Required |
| `email` | VARCHAR(255) | Unique when present |
| `mobile_number` | VARCHAR(20) | Unique when present |
| `status` | VARCHAR(30) | Customer/agent/vendor/admin account state |

- **Primary key:** `user_id`
- **Foreign keys:** None; customer, agent, vendor, and staff profiles reference this table.
- **Indexes:** unique `email`; unique `mobile_number`; `status, created_at DESC`.
- **Constraints:** at least one verified login identifier; `status` uses the customer/account state model.

## roles

| Column | Type | Notes |
|---|---|---|
| `role_id` | UUID | Primary key |
| `role_code` | VARCHAR(80) | Stable role identifier |
| `role_name` | VARCHAR(120) | Display name |
| `status` | VARCHAR(30) | Active/inactive control |

- **Primary key:** `role_id`
- **Foreign keys:** None.
- **Indexes:** unique `role_code`; `status`.
- **Constraints:** role codes are immutable after assignment; inactive roles cannot be newly assigned.

## user_roles

| Column | Type | Notes |
|---|---|---|
| `user_role_id` | UUID | Primary key |
| `user_id` | UUID | Assigned user |
| `role_id` | UUID | Assigned role |
| `effective_from` | TIMESTAMP | Assignment start |
| `effective_to` | TIMESTAMP NULL | Assignment end |

- **Primary key:** `user_role_id`
- **Foreign keys:** `user_id -> users.user_id`; `role_id -> roles.role_id`.
- **Indexes:** unique active `user_id, role_id`; `role_id, effective_to`.
- **Constraints:** only active roles may have active assignments; effective end must be later than effective start.

## auth_otp_challenges

| Column | Type | Notes |
|---|---|---|
| `otp_challenge_id` | UUID | Primary key |
| `user_id` | UUID NULL | Known user when available |
| `identifier` | VARCHAR(255) | Mobile or email identifier |
| `purpose` | VARCHAR(50) | Login, registration, recovery, KYC |
| `delivery_channel` | VARCHAR(30) | SMS, email, WhatsApp |
| `otp_hash` | VARCHAR(255) | Salted OTP hash only |
| `expires_at` | TIMESTAMP | Expiry |
| `attempt_count` | INTEGER | Verification attempts |
| `verified_at` | TIMESTAMP NULL | Successful verification |

- **Primary key:** `otp_challenge_id`
- **Foreign keys:** `user_id -> users.user_id`.
- **Indexes:** `identifier, purpose, expires_at DESC`; `user_id, verified_at DESC`.
- **Constraints:** plaintext OTPs are prohibited; challenge is single-use; `verified_at` cannot be later than `expires_at`.

## auth_sessions

| Column | Type | Notes |
|---|---|---|
| `session_id` | UUID | Primary key |
| `user_id` | UUID | Authenticated user |
| `device_id` | VARCHAR(255) | Device/session context |
| `refresh_token_hash` | VARCHAR(255) | Token hash only |
| `issued_at` | TIMESTAMP | Session issue time |
| `expires_at` | TIMESTAMP | Session expiry |
| `revoked_at` | TIMESTAMP NULL | Revocation time |
| `risk_status` | VARCHAR(30) | Device/session risk state |

- **Primary key:** `session_id`
- **Foreign keys:** `user_id -> users.user_id`.
- **Indexes:** `user_id, expires_at DESC`; `device_id, revoked_at`; unique active `refresh_token_hash`.
- **Constraints:** revoked or expired sessions cannot refresh tokens; token values are never stored in plaintext.

## subscription_plan_versions

| Column | Type | Notes |
|---|---|---|
| `plan_version_id` | UUID | Primary key |
| `subscription_plan_id` | UUID | Parent plan |
| `version_number` | INTEGER | Immutable plan version |
| `effective_from` | TIMESTAMP | Version start |
| `effective_to` | TIMESTAMP NULL | Version end |
| `status` | VARCHAR(30) | Draft, active, retired |
| `benefits_json` | JSONB | Versioned benefit configuration |

- **Primary key:** `plan_version_id`
- **Foreign keys:** `subscription_plan_id -> subscription_plans.subscription_plan_id`.
- **Indexes:** unique `subscription_plan_id, version_number`; `subscription_plan_id, status, effective_from DESC`.
- **Constraints:** active effective ranges for a plan cannot overlap; an active customer subscription retains its accepted plan version.

## subscription_plan_entitlements

| Column | Type | Notes |
|---|---|---|
| `entitlement_id` | UUID | Primary key |
| `plan_version_id` | UUID | Versioned plan |
| `service_id` | UUID | Entitled service |
| `quantity` | INTEGER | Included quantity |
| `period_type` | VARCHAR(30) | Monthly, annual, term |
| `carry_forward_allowed` | BOOLEAN | Carry-forward policy |
| `status` | VARCHAR(30) | Active/inactive |

- **Primary key:** `entitlement_id`
- **Foreign keys:** `plan_version_id -> subscription_plan_versions.plan_version_id`; `service_id -> services.service_id`.
- **Indexes:** unique active `plan_version_id, service_id, period_type`; `service_id, status`.
- **Constraints:** quantity is non-negative; only recurring-monitoring or explicitly eligible services may be included.

## subscription_add_ons

| Column | Type | Notes |
|---|---|---|
| `subscription_add_on_id` | UUID | Primary key |
| `service_id` | UUID | Add-on service |
| `name` | VARCHAR(150) | Customer-facing name |
| `eligibility_rule_json` | JSONB | Plan/property eligibility |
| `status` | VARCHAR(30) | Active/inactive |

- **Primary key:** `subscription_add_on_id`
- **Foreign keys:** `service_id -> services.service_id`.
- **Indexes:** unique active `service_id`; `status`.
- **Constraints:** add-ons are separately priced unless an active plan-entitlement rule grants a benefit.

## customer_subscription_add_ons

| Column | Type | Notes |
|---|---|---|
| `customer_subscription_add_on_id` | UUID | Primary key |
| `customer_subscription_id` | UUID | Customer subscription |
| `subscription_add_on_id` | UUID | Selected add-on |
| `pricing_estimate_id` | UUID NULL | Accepted price context |
| `effective_from` | TIMESTAMP | Selection start |
| `effective_to` | TIMESTAMP NULL | Selection end |
| `status` | VARCHAR(30) | Active, removed, expired |

- **Primary key:** `customer_subscription_add_on_id`
- **Foreign keys:** `customer_subscription_id -> customer_subscriptions.customer_subscription_id`; `subscription_add_on_id -> subscription_add_ons.subscription_add_on_id`; `pricing_estimate_id -> pricing_estimates.estimate_id`.
- **Indexes:** unique active `customer_subscription_id, subscription_add_on_id`; `status, effective_to`.
- **Constraints:** selection requires an active eligible subscription and a valid accepted price context when chargeable.

## subscription_lifecycle_events

| Column | Type | Notes |
|---|---|---|
| `subscription_lifecycle_event_id` | UUID | Primary key |
| `customer_subscription_id` | UUID | Subscription |
| `previous_status` | VARCHAR(30) NULL | Prior state |
| `new_status` | VARCHAR(30) | New state |
| `effective_at` | TIMESTAMP | Transition time |
| `reason_code` | VARCHAR(80) NULL | Transition reason |
| `payment_id` | UUID NULL | Related payment |

- **Primary key:** `subscription_lifecycle_event_id`
- **Foreign keys:** `customer_subscription_id -> customer_subscriptions.customer_subscription_id`; `payment_id -> payments.payment_id`.
- **Indexes:** `customer_subscription_id, effective_at DESC`; `new_status, effective_at DESC`.
- **Constraints:** every subscription-state transition creates one event; transitions must comply with the subscription state model.

## pricing_quote_line_items

| Column | Type | Notes |
|---|---|---|
| `pricing_quote_line_item_id` | UUID | Primary key |
| `estimate_id` | UUID | Parent estimate/quote |
| `line_type` | VARCHAR(50) | Service, travel, allowance, add-on, fee, discount |
| `description` | VARCHAR(255) | Customer-visible explanation |
| `quantity` | DECIMAL(12,2) | Applied quantity |
| `amount` | DECIMAL(15,2) | Signed amount |
| `rule_reference` | VARCHAR(100) NULL | Applied rule/version |

- **Primary key:** `pricing_quote_line_item_id`
- **Foreign keys:** `estimate_id -> pricing_estimates.estimate_id`.
- **Indexes:** `estimate_id, line_type`; `rule_reference`.
- **Constraints:** a confirmed booking/payment preserves its full line-item set; final quote total equals the sum of line-item amounts.

## pricing_discount_applications

| Column | Type | Notes |
|---|---|---|
| `discount_application_id` | UUID | Primary key |
| `estimate_id` | UUID | Quote context |
| `discount_type` | VARCHAR(50) | Promotion, coupon, referral, plan benefit |
| `source_reference` | VARCHAR(100) | Rule, coupon, or entitlement reference |
| `amount` | DECIMAL(15,2) | Applied reduction |
| `applied_at` | TIMESTAMP | Application time |

- **Primary key:** `discount_application_id`
- **Foreign keys:** `estimate_id -> pricing_estimates.estimate_id`.
- **Indexes:** `estimate_id`; `discount_type, source_reference`.
- **Constraints:** amount is positive; stacking follows configured rules; the same benefit cannot be applied twice to one estimate.

## coupons

| Column | Type | Notes |
|---|---|---|
| `coupon_id` | UUID | Primary key |
| `code` | VARCHAR(80) | Customer-entered code |
| `discount_type` | VARCHAR(30) | Percentage or fixed amount |
| `discount_value` | DECIMAL(15,2) | Configured value |
| `eligibility_rule_json` | JSONB | Service, plan, property, customer scope |
| `valid_from` | TIMESTAMP | Start |
| `valid_to` | TIMESTAMP | End |
| `status` | VARCHAR(30) | Active/inactive |

- **Primary key:** `coupon_id`
- **Foreign keys:** None.
- **Indexes:** unique `code`; `status, valid_from, valid_to`.
- **Constraints:** validity end is after start; discount values are non-negative; coupon use respects configured limits and approval controls.

## coupon_redemptions

| Column | Type | Notes |
|---|---|---|
| `coupon_redemption_id` | UUID | Primary key |
| `coupon_id` | UUID | Redeemed coupon |
| `customer_id` | UUID | Redeeming customer |
| `estimate_id` | UUID | Quote context |
| `payment_id` | UUID NULL | Confirmed payment |
| `redeemed_at` | TIMESTAMP | Redemption time |
| `status` | VARCHAR(30) | Reserved, redeemed, reversed |

- **Primary key:** `coupon_redemption_id`
- **Foreign keys:** `coupon_id -> coupons.coupon_id`; `customer_id -> customers.customer_id`; `estimate_id -> pricing_estimates.estimate_id`; `payment_id -> payments.payment_id`.
- **Indexes:** `coupon_id, status`; `customer_id, redeemed_at DESC`; unique active `coupon_id, estimate_id`.
- **Constraints:** redemption occurs only within coupon validity and eligible quote scope; reversal is audit logged when payment fails or is refunded.

## pricing_rule_approvals

| Column | Type | Notes |
|---|---|---|
| `pricing_rule_approval_id` | UUID | Primary key |
| `service_price_rule_id` | UUID NULL | Rule under approval |
| `coupon_id` | UUID NULL | Coupon under approval |
| `decision` | VARCHAR(30) | Pending, approved, rejected |
| `approved_by` | UUID NULL | Authorised approver |
| `reason` | TEXT | Decision evidence |
| `decided_at` | TIMESTAMP NULL | Decision time |

- **Primary key:** `pricing_rule_approval_id`
- **Foreign keys:** `service_price_rule_id -> service_price_rules.service_price_rule_id`; `coupon_id -> coupons.coupon_id`; `approved_by -> users.user_id`.
- **Indexes:** `decision, decided_at`; `service_price_rule_id`; `coupon_id`.
- **Constraints:** exactly one of service-price rule or coupon is populated; approval is required before activation where configured threshold rules apply.

## customer_favorites

| Column | Type | Notes |
|---|---|---|
| `customer_favorite_id` | UUID | Primary key |
| `customer_id` | UUID | Customer |
| `listing_id` | UUID | Marketplace listing |
| `created_at` | TIMESTAMP | Saved time |

- **Primary key:** `customer_favorite_id`
- **Foreign keys:** `customer_id -> customers.customer_id`; `listing_id -> marketplace_listings.listing_id`.
- **Indexes:** unique `customer_id, listing_id`; `customer_id, created_at DESC`.
- **Constraints:** a favourite references an active/visible listing and is unique per customer/listing.

## customer_search_history

| Column | Type | Notes |
|---|---|---|
| `search_history_id` | UUID | Primary key |
| `customer_id` | UUID | Customer |
| `search_context` | VARCHAR(50) | Property, service, marketplace |
| `query_json` | JSONB | Applied search criteria |
| `searched_at` | TIMESTAMP | Search time |

- **Primary key:** `search_history_id`
- **Foreign keys:** `customer_id -> customers.customer_id`.
- **Indexes:** `customer_id, searched_at DESC`; `search_context, searched_at DESC`.
- **Constraints:** history excludes unmasked third-party contact data and is deleted or anonymised with authorised customer-data deletion.

## report_reviews

| Column | Type | Notes |
|---|---|---|
| `report_review_id` | UUID | Primary key |
| `report_id` | UUID | Reviewed report |
| `reviewer_user_id` | UUID | Reviewer |
| `decision` | VARCHAR(30) | Approved, rejected, rework required |
| `comments` | TEXT NULL | Review notes |
| `reviewed_at` | TIMESTAMP | Review time |

- **Primary key:** `report_review_id`
- **Foreign keys:** `report_id -> reports.report_id`; `reviewer_user_id -> users.user_id`.
- **Indexes:** `report_id, reviewed_at DESC`; `reviewer_user_id, reviewed_at DESC`.
- **Constraints:** only authorised reviewers may decide; one active decision controls report progression; rework requires a reason.

## knowledge_base_articles

| Column | Type | Notes |
|---|---|---|
| `article_id` | UUID | Primary key |
| `title` | VARCHAR(255) | Article title |
| `slug` | VARCHAR(255) | Public identifier |
| `content` | TEXT | Versioned article content |
| `status` | VARCHAR(30) | Draft, published, archived |
| `published_by` | UUID NULL | Publishing user |
| `published_at` | TIMESTAMP NULL | Publication time |

- **Primary key:** `article_id`
- **Foreign keys:** `published_by -> users.user_id`.
- **Indexes:** unique `slug`; `status, published_at DESC`.
- **Constraints:** only published articles are publicly searchable; published changes preserve a content version and audit trail.

## nri_relationship_messages

| Column | Type | Notes |
|---|---|---|
| `relationship_message_id` | UUID | Primary key |
| `relationship_assignment_id` | UUID | NRI manager assignment |
| `sender_user_id` | UUID | Sender |
| `recipient_user_id` | UUID | Recipient |
| `message_body` | TEXT | Controlled message content |
| `sent_at` | TIMESTAMP | Send time |
| `read_at` | TIMESTAMP NULL | Read time |
| `status` | VARCHAR(30) | Sent, delivered, read, failed |

- **Primary key:** `relationship_message_id`
- **Foreign keys:** `relationship_assignment_id -> nri_relationship_assignments.nri_relationship_assignment_id`; `sender_user_id -> users.user_id`; `recipient_user_id -> users.user_id`.
- **Indexes:** `relationship_assignment_id, sent_at DESC`; `recipient_user_id, read_at`.
- **Constraints:** senders and recipients must be authorised within the active relationship assignment; attachments use controlled document/evidence references rather than unbounded inline files.

## offline_sync_operations

| Column | Type | Notes |
|---|---|---|
| `offline_sync_operation_id` | UUID | Primary key |
| `user_id` | UUID | Field user |
| `device_id` | VARCHAR(255) | Device context |
| `entity_type` | VARCHAR(50) | Visit, evidence, report |
| `entity_id` | UUID | Synchronised entity |
| `operation_type` | VARCHAR(50) | Create, update, upload |
| `idempotency_key` | VARCHAR(255) | Offline operation key |
| `status` | VARCHAR(30) | Queued, processing, completed, conflict, failed |
| `queued_at` | TIMESTAMP | Queue time |
| `completed_at` | TIMESTAMP NULL | Completion time |

- **Primary key:** `offline_sync_operation_id`
- **Foreign keys:** `user_id -> users.user_id`.
- **Indexes:** unique `user_id, device_id, idempotency_key`; `status, queued_at`; `entity_type, entity_id`.
- **Constraints:** an operation is applied at most once; conflicts retain both local-operation metadata and the server resolution outcome; only authorised field users may queue visit, evidence, or report operations.
