# Canonical Data Dictionary

Document Type: Master Data Dictionary  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This document is the authoritative business and technical definition for every PropertyPilot data element. It serves as the master source for:
- database design
- API payload contracts
- UI field definitions
- reporting definitions
- analytics dimensions and measures
- system integrations
- governance and compliance controls

This dictionary is intended to be the canonical reference for data model implementation and should be used as the source of truth for design and validation.

---

## Scope

This document covers the canonical definitions for the following domains:
- Customer
- Property
- Service
- Subscription
- Payment
- Agent
- Visit
- Evidence
- Report
- Vendor
- Complaint
- Notification
- Audit

It includes:
- field names
- data type
- requirement level
- description
- validation rules
- classification
- retention guidance

---

## Data Governance Principles

1. Every data element must have a single canonical definition.
2. Data classification must be applied consistently across all systems.
3. PII and financial data must be protected using encryption, masking, and access control.
4. Sensitive data must not be logged in plaintext.
5. All domain objects must support auditability.
6. Data retention must be policy-driven and consistent across services.
7. API payloads and database fields must align with this dictionary.
8. Any schema change requires review against this document and the physical model.

---

# Data Classification

## 1. Classification Levels

| Classification | Description | Examples | Retention | Encryption Requirement |
|---|---|---|---|---|
| Public | Non-sensitive information intended for broad sharing | property listing summary, public office details | business defined | not required beyond transport encryption |
| Internal | Internal operational data | property status, agent assignment, internal notes | governed by business policy | required at rest for sensitive operational data |
| Confidential | Sensitive business or personal data not intended for public disclosure | customer profile, service history, documents, lease metadata | based on legal and product retention | required at rest and in transit |
| Restricted | High-sensitivity regulated or financial data | payment data, KYC data, audit logs, PII, bank data | legal retention + archive | required at rest and in transit, access restricted |

### Public
- Description: data that can be exposed without material risk.
- Examples: public property summary, listing name, city, property category
- Retention: business-defined minimum
- Encryption: transport encryption preferred; storage encryption standard

### Internal
- Description: internal operational data not publicly shared.
- Examples: internal workflow notes, service request assignments, operational timestamps
- Retention: typically 1–5 years depending on domain
- Encryption: required at rest for data stores containing operational or user-bound records

### Confidential
- Description: personal, property, or customer-sensitive data that requires access restriction.
- Examples: customer phone numbers, email, address, KYC documents, service history
- Retention: governed by product and legal policy
- Encryption: required both in transit and at rest

### Restricted
- Description: highly sensitive, regulated, or financial data.
- Examples: payment card references, KYC identifiers, audit records, bank account information
- Retention: legal and compliance retention windows
- Encryption: mandatory with strict access governance and key management

---

# Customer Domain

## Entity: Customer

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| customer_id | UUID | Yes | Unique identifier for the customer | UUID v4 format | Confidential |
| first_name | VARCHAR(100) | Yes | Customer first name | 1–100 chars, letters/spaces/apostrophes allowed | Confidential |
| last_name | VARCHAR(100) | Yes | Customer last name | 1–100 chars, letters/spaces/apostrophes allowed | Confidential |
| mobile_number | VARCHAR(20) | Yes | Mobile phone number | E.164 format or country-specific regex | Confidential |
| email | VARCHAR(255) | Yes | Primary email address | valid email format | Confidential |
| status | ENUM | Yes | Lifecycle status of customer account | in (active, inactive, pending, blocked, archived) | Internal |
| created_at | TIMESTAMP | Yes | Record creation timestamp | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification timestamp | ISO-8601 UTC | Internal |

## Entity: Customer Address

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| customer_address_id | UUID | Yes | Unique address record identifier | UUID v4 format | Confidential |
| customer_id | UUID | Yes | Reference to customer | must exist in customer.customer_id | Confidential |
| address_type | VARCHAR(30) | Yes | Type of address | in (home, office, mailing, other) | Internal |
| address_line_1 | VARCHAR(255) | Yes | Street address line 1 | 1–255 chars | Confidential |
| address_line_2 | VARCHAR(255) | No | Street address line 2 | max 255 chars | Confidential |
| landmark | VARCHAR(255) | No | Landmark near address | max 255 chars | Confidential |
| city | VARCHAR(100) | Yes | City | 1–100 chars | Confidential |
| state | VARCHAR(100) | Yes | State or province | 1–100 chars | Confidential |
| postal_code | VARCHAR(20) | Yes | Postal or ZIP code | country-specific validation | Confidential |
| country | VARCHAR(100) | Yes | Country | ISO country code or full name accepted | Confidential |
| latitude | DECIMAL(9,6) | No | Latitude coordinate | between -90 and 90 | Confidential |
| longitude | DECIMAL(9,6) | No | Longitude coordinate | between -180 and 180 | Confidential |
| is_primary | BOOLEAN | No | Whether this is primary address | default false | Internal |
| created_at | TIMESTAMP | Yes | Address creation timestamp | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Address last update timestamp | ISO-8601 UTC | Internal |

---

# Property Domain

## Entity: Property

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| property_id | UUID | Yes | Unique identifier for the property | UUID v4 format | Confidential |
| property_name | VARCHAR(255) | Yes | Property display name | 1–255 chars | Internal |
| property_type | VARCHAR(50) | Yes | Category of property | in (apartment, villa, office, commercial, land, plot) | Internal |
| survey_number | VARCHAR(100) | No | Legal survey number | 1–100 chars | Confidential |
| extent | DECIMAL(12,2) | No | Property size or extent | greater than 0 if provided | Confidential |
| latitude | DECIMAL(9,6) | No | Property latitude | between -90 and 90 | Confidential |
| longitude | DECIMAL(9,6) | No | Property longitude | between -180 and 180 | Confidential |
| ownership_type | VARCHAR(50) | Yes | Ownership model | in (owned, leased, managed, other) | Internal |
| status | ENUM | Yes | Property status | in (draft, active, inactive, under_maintenance, archived) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Property Document

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| property_document_id | UUID | Yes | Unique document identifier | UUID v4 format | Confidential |
| property_id | UUID | Yes | Reference to property | must exist in property.property_id | Confidential |
| document_type | VARCHAR(100) | Yes | Document category | in (title_deed, occupancy, insurance, tax, permit, invoice, photo) | Confidential |
| file_name | VARCHAR(255) | Yes | Original file name | 1–255 chars | Confidential |
| file_url | VARCHAR(500) | Yes | Object storage URL | valid URL | Confidential |
| file_size_bytes | BIGINT | No | File size in bytes | greater than 0 | Internal |
| mime_type | VARCHAR(100) | Yes | MIME type | standard MIME type | Internal |
| checksum | VARCHAR(128) | Yes | File integrity checksum | SHA-256 hex | Restricted |
| uploaded_by | UUID | Yes | User uploading the document | must exist in app_user.user_id | Confidential |
| upload_status | ENUM | Yes | Document upload state | in (uploaded, processing, failed, verified, archived) | Internal |
| gps_accuracy | DECIMAL(10,2) | No | GPS capture accuracy in meters | >= 0 | Confidential |
| captured_at | TIMESTAMP | No | Document capture timestamp | ISO-8601 UTC | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

---

# Service Domain

## Entity: Service

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| service_id | UUID | Yes | Unique service identifier | UUID v4 format | Internal |
| service_name | VARCHAR(255) | Yes | Human-readable service name | 1–255 chars | Internal |
| service_code | VARCHAR(50) | Yes | Machine-readable service code | alphanumeric/underscore | Internal |
| service_category | VARCHAR(100) | Yes | Service grouping | in (maintenance, cleaning, security, utility, support) | Internal |
| is_active | BOOLEAN | Yes | Whether service is active | default true | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Service Request

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| service_request_id | UUID | Yes | Unique service request identifier | UUID v4 format | Confidential |
| customer_id | UUID | Yes | Requesting customer | must exist in customer.customer_id | Confidential |
| property_id | UUID | Yes | Related property | must exist in property.property_id | Confidential |
| service_id | UUID | Yes | Requested service | must exist in service.service_id | Internal |
| request_title | VARCHAR(255) | Yes | Brief request title | 1–255 chars | Internal |
| description | TEXT | Yes | Request description | max 5000 chars | Confidential |
| priority | VARCHAR(30) | Yes | Priority level | in (low, medium, high, urgent) | Internal |
| status | ENUM | Yes | Current request status | in (draft, open, assigned, in_progress, resolved, closed, cancelled) | Internal |
| requested_at | TIMESTAMP | Yes | Request creation timestamp | ISO-8601 UTC | Internal |
| scheduled_at | TIMESTAMP | No | Scheduled execution time | ISO-8601 UTC | Internal |
| resolved_at | TIMESTAMP | No | Resolution timestamp | ISO-8601 UTC | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Service Request Status History

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| status_history_id | UUID | Yes | Unique status history record | UUID v4 format | Internal |
| service_request_id | UUID | Yes | Parent service request | must exist in service_request.service_request_id | Confidential |
| previous_status | VARCHAR(50) | No | Prior status | valid status enum | Internal |
| new_status | VARCHAR(50) | Yes | New status | valid status enum | Internal |
| changed_by | UUID | Yes | User or system that changed status | must exist in app_user.user_id | Confidential |
| changed_at | TIMESTAMP | Yes | Time status changed | ISO-8601 UTC | Internal |
| comments | TEXT | No | Status change notes | max 2000 chars | Confidential |

---

# Subscription Domain

## Entity: Subscription Plan

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| subscription_plan_id | UUID | Yes | Unique plan identifier | UUID v4 format | Internal |
| plan_code | VARCHAR(50) | Yes | Machine code for plan | allowed code format | Internal |
| plan_name | VARCHAR(255) | Yes | Plan display name | 1–255 chars | Internal |
| billing_cycle | VARCHAR(30) | Yes | Billing interval | in (monthly, quarterly, yearly) | Internal |
| price | DECIMAL(18,2) | Yes | Plan price | >= 0 | Confidential |
| currency | CHAR(3) | Yes | ISO currency code | ISO 4217 | Internal |
| features_json | JSONB | No | Configured plan features | valid JSON | Internal |
| is_active | BOOLEAN | Yes | Plan availability | default true | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Customer Subscription

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| customer_subscription_id | UUID | Yes | Unique subscription record | UUID v4 format | Confidential |
| customer_id | UUID | Yes | Customer associated with subscription | must exist in customer.customer_id | Confidential |
| subscription_plan_id | UUID | Yes | Plan assigned | must exist in subscription_plan.subscription_plan_id | Internal |
| status | ENUM | Yes | Subscription life cycle status | in (draft, active, paused, cancelled, expired) | Internal |
| start_date | TIMESTAMP | Yes | Subscription start date | ISO-8601 UTC | Internal |
| end_date | TIMESTAMP | No | Subscription end date | ISO-8601 UTC | Internal |
| auto_renew | BOOLEAN | Yes | Whether auto renewal is enabled | default true | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Subscription Renewal

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| renewal_id | UUID | Yes | Unique renewal record | UUID v4 format | Confidential |
| customer_subscription_id | UUID | Yes | Related subscription | must exist in customer_subscription.customer_subscription_id | Confidential |
| renewal_period_start | TIMESTAMP | Yes | Renewal start | ISO-8601 UTC | Internal |
| renewal_period_end | TIMESTAMP | Yes | Renewal end | ISO-8601 UTC | Internal |
| renewal_amount | DECIMAL(18,2) | Yes | Renewal amount | >= 0 | Confidential |
| status | VARCHAR(30) | Yes | Renewal status | in (pending, processed, failed, cancelled) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Subscription Invoice

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| subscription_invoice_id | UUID | Yes | Unique invoice identifier | UUID v4 format | Confidential |
| customer_subscription_id | UUID | Yes | Related subscription | must exist in customer_subscription.customer_subscription_id | Confidential |
| invoice_number | VARCHAR(100) | Yes | Invoice number | unique per org | Confidential |
| invoice_date | TIMESTAMP | Yes | Invoice date | ISO-8601 UTC | Internal |
| due_date | TIMESTAMP | Yes | Due date | ISO-8601 UTC | Internal |
| total_amount | DECIMAL(18,2) | Yes | Invoice total | >= 0 | Confidential |
| status | ENUM | Yes | Invoice status | in (draft, issued, paid, overdue, refunded, cancelled) | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Subscription Usage

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| subscription_usage_id | UUID | Yes | Unique usage event identifier | UUID v4 format | Internal |
| customer_subscription_id | UUID | Yes | Related subscription | must exist in customer_subscription.customer_subscription_id | Confidential |
| usage_metric | VARCHAR(100) | Yes | Metric name | 1–100 chars | Internal |
| usage_value | DECIMAL(18,4) | Yes | Usage value | >= 0 | Internal |
| usage_period_start | TIMESTAMP | Yes | Usage period start | ISO-8601 UTC | Internal |
| usage_period_end | TIMESTAMP | Yes | Usage period end | ISO-8601 UTC | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |

---

# Payment Domain

## Entity: Payment

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| payment_id | UUID | Yes | Unique payment identifier | UUID v4 format | Restricted |
| customer_id | UUID | Yes | Payer / customer | must exist in customer.customer_id | Confidential |
| invoice_id | UUID | No | Related invoice | must exist in invoice.invoice_id | Confidential |
| payment_reference | VARCHAR(100) | Yes | Payment reference / external identifier | unique per provider | Restricted |
| amount | DECIMAL(18,2) | Yes | Payment amount | > 0 | Restricted |
| currency | CHAR(3) | Yes | ISO currency code | ISO 4217 | Restricted |
| payment_method | VARCHAR(50) | Yes | Payment method | in (upi, card, netbanking, wallet, offline) | Restricted |
| status | ENUM | Yes | Payment lifecycle status | in (initiated, authorized, paid, failed, refunded, disputed, reversed) | Restricted |
| provider_name | VARCHAR(100) | Yes | Third-party payment provider | valid provider name | Restricted |
| gateway_transaction_id | VARCHAR(255) | No | Provider transaction reference | max 255 chars | Restricted |
| paid_at | TIMESTAMP | No | Payment completion time | ISO-8601 UTC | Restricted |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Refund

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| refund_id | UUID | Yes | Unique refund identifier | UUID v4 format | Restricted |
| payment_id | UUID | Yes | Related payment | must exist in payment.payment_id | Restricted |
| customer_id | UUID | Yes | Refund recipient | must exist in customer.customer_id | Confidential |
| refund_amount | DECIMAL(18,2) | Yes | Refund amount | > 0 | Restricted |
| refund_reason | VARCHAR(255) | Yes | Refund reason | 1–255 chars | Confidential |
| status | ENUM | Yes | Refund status | in (requested, approved, processed, failed, rejected) | Restricted |
| processed_at | TIMESTAMP | No | Refund processing timestamp | ISO-8601 UTC | Restricted |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Invoice

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| invoice_id | UUID | Yes | Unique invoice ID | UUID v4 format | Confidential |
| customer_id | UUID | Yes | Invoice owner | must exist in customer.customer_id | Confidential |
| invoice_number | VARCHAR(100) | Yes | Unique invoice number | unique within org | Confidential |
| invoice_date | TIMESTAMP | Yes | Invoice issue date | ISO-8601 UTC | Internal |
| due_date | TIMESTAMP | Yes | Due date | ISO-8601 UTC | Internal |
| subtotal | DECIMAL(18,2) | Yes | Before taxes | >= 0 | Confidential |
| tax_amount | DECIMAL(18,2) | Yes | Tax amount | >= 0 | Confidential |
| total_amount | DECIMAL(18,2) | Yes | Final amount | >= 0 | Confidential |
| status | ENUM | Yes | Invoice status | in (draft, issued, paid, overdue, cancelled, partially_paid) | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Payment Ledger

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| payment_ledger_id | UUID | Yes | Unique ledger entry ID | UUID v4 format | Restricted |
| payment_id | UUID | No | Related payment | must exist in payment.payment_id | Restricted |
| invoice_id | UUID | No | Related invoice | must exist in invoice.invoice_id | Confidential |
| entry_type | VARCHAR(50) | Yes | Ledger entry type | in (debit, credit, adjustment, refund, writeoff) | Restricted |
| amount | DECIMAL(18,2) | Yes | Ledger amount | >= 0 | Restricted |
| balance_after | DECIMAL(18,2) | Yes | Balance after transaction | numeric | Restricted |
| entry_reference | VARCHAR(255) | No | Reference ID | max 255 chars | Restricted |
| created_at | TIMESTAMP | Yes | Entry creation time | ISO-8601 UTC | Internal |

---

# Agent Domain

## Entity: Agent

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| agent_id | UUID | Yes | Unique agent identifier | UUID v4 format | Confidential |
| first_name | VARCHAR(100) | Yes | Agent first name | 1–100 chars | Confidential |
| last_name | VARCHAR(100) | Yes | Agent last name | 1–100 chars | Confidential |
| mobile_number | VARCHAR(20) | Yes | Agent mobile | E.164 format | Confidential |
| email | VARCHAR(255) | Yes | Agent email | valid email | Confidential |
| status | ENUM | Yes | Agent status | in (active, inactive, on_leave, blocked) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Agent Skill

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| agent_skill_id | UUID | Yes | Unique skill record | UUID v4 format | Internal |
| agent_id | UUID | Yes | Related agent | must exist in agent.agent_id | Confidential |
| skill_name | VARCHAR(100) | Yes | Skill name | 1–100 chars | Internal |
| skill_level | VARCHAR(30) | Yes | Proficiency level | in (basic, intermediate, advanced, expert) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |

## Entity: Agent Assignment

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| agent_assignment_id | UUID | Yes | Unique assignment record | UUID v4 format | Internal |
| agent_id | UUID | Yes | Assigned agent | must exist in agent.agent_id | Confidential |
| property_id | UUID | No | Property assigned | must exist in property.property_id | Confidential |
| customer_id | UUID | No | Customer assigned | must exist in customer.customer_id | Confidential |
| assignment_type | VARCHAR(50) | Yes | Type of assignment | in (property, customer, service, lease, visit) | Internal |
| assigned_at | TIMESTAMP | Yes | Assignment timestamp | ISO-8601 UTC | Internal |
| unassigned_at | TIMESTAMP | No | Unassignment timestamp | ISO-8601 UTC | Internal |
| status | VARCHAR(30) | Yes | Assignment status | in (active, completed, revoked) | Internal |

---

# Visit Domain

## Entity: Visit

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| visit_id | UUID | Yes | Unique visit identifier | UUID v4 format | Confidential |
| customer_id | UUID | No | Customer related to visit | must exist in customer.customer_id | Confidential |
| property_id | UUID | No | Property visited | must exist in property.property_id | Confidential |
| agent_id | UUID | No | Assigned agent | must exist in agent.agent_id | Confidential |
| visit_type | VARCHAR(50) | Yes | Type of visit | in (site_visit, inspection, tour, follow_up) | Internal |
| scheduled_at | TIMESTAMP | Yes | Scheduled time | ISO-8601 UTC | Internal |
| actual_start_at | TIMESTAMP | No | Actual start | ISO-8601 UTC | Internal |
| actual_end_at | TIMESTAMP | No | Actual end | ISO-8601 UTC | Internal |
| status | ENUM | Yes | Visit status | in (scheduled, started, completed, cancelled, no_show) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: GPS Capture

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| gps_capture_id | UUID | Yes | Unique GPS capture record | UUID v4 format | Confidential |
| visit_id | UUID | Yes | Parent visit | must exist in visit.visit_id | Confidential |
| latitude | DECIMAL(9,6) | Yes | GPS latitude | between -90 and 90 | Confidential |
| longitude | DECIMAL(9,6) | Yes | GPS longitude | between -180 and 180 | Confidential |
| accuracy_meters | DECIMAL(10,2) | No | GPS accuracy | >= 0 | Confidential |
| captured_at | TIMESTAMP | Yes | Capture time | ISO-8601 UTC | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |

## Entity: Observation

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| observation_id | UUID | Yes | Unique observation record | UUID v4 format | Confidential |
| visit_id | UUID | Yes | Parent visit | must exist in visit.visit_id | Confidential |
| observation_type | VARCHAR(100) | Yes | Observation type | 1–100 chars | Internal |
| notes | TEXT | No | Observation note | max 5000 chars | Confidential |
| severity | VARCHAR(30) | No | Severity | in (low, medium, high) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

---

# Evidence Domain

## Entity: Evidence

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| evidence_id | UUID | Yes | Unique evidence ID | UUID v4 format | Confidential |
| entity_type | VARCHAR(100) | Yes | Related entity type | 1–100 chars | Internal |
| entity_id | UUID | Yes | Related entity record | valid UUID | Confidential |
| checksum | VARCHAR(128) | Yes | File checksum | SHA-256 hex | Restricted |
| file_url | VARCHAR(500) | Yes | Storage URL | valid URL | Confidential |
| upload_status | ENUM | Yes | File upload state | in (pending, uploaded, processing, verified, failed) | Internal |
| gps_accuracy | DECIMAL(10,2) | No | GPS accuracy for evidence capture | >= 0 | Confidential |
| captured_at | TIMESTAMP | Yes | Capture time | ISO-8601 UTC | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

---

# Report Domain

## Entity: Report

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| report_id | UUID | Yes | Unique report identifier | UUID v4 format | Internal |
| report_name | VARCHAR(255) | Yes | Report name | 1–255 chars | Internal |
| report_type | VARCHAR(100) | Yes | Report category | in (summary, operational, financial, compliance, custom) | Internal |
| created_by | UUID | Yes | Report creator | must exist in app_user.user_id | Confidential |
| status | ENUM | Yes | Report status | in (draft, generating, ready, failed, expired) | Internal |
| generated_at | TIMESTAMP | No | Report generation timestamp | ISO-8601 UTC | Internal |
| expires_at | TIMESTAMP | No | Expiry timestamp | ISO-8601 UTC | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Report Version

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| report_version_id | UUID | Yes | Unique report version | UUID v4 format | Internal |
| report_id | UUID | Yes | Parent report | must exist in report.report_id | Internal |
| version_number | INTEGER | Yes | Version number | >= 1 | Internal |
| file_url | VARCHAR(500) | Yes | Report file storage URL | valid URL | Confidential |
| created_by | UUID | Yes | Version creator | must exist in app_user.user_id | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |

---

# Vendor Domain

## Entity: Vendor

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| vendor_id | UUID | Yes | Unique vendor identifier | UUID v4 format | Confidential |
| vendor_name | VARCHAR(255) | Yes | Vendor legal or display name | 1–255 chars | Internal |
| vendor_type | VARCHAR(50) | Yes | Category of vendor | in (maintenance, cleaning, security, legal, other) | Internal |
| status | ENUM | Yes | Vendor status | in (active, inactive, suspended, blacklisted) | Internal |
| contact_phone | VARCHAR(20) | No | Vendor contact number | E.164 format | Confidential |
| contact_email | VARCHAR(255) | No | Vendor email | valid email | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Quotation

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| quotation_id | UUID | Yes | Unique quotation ID | UUID v4 format | Confidential |
| vendor_id | UUID | Yes | Related vendor | must exist in vendor.vendor_id | Confidential |
| service_request_id | UUID | No | Related service request | must exist in service_request.service_request_id | Confidential |
| quote_number | VARCHAR(100) | Yes | Vendor quote number | 1–100 chars | Internal |
| amount | DECIMAL(18,2) | Yes | Quote amount | >= 0 | Confidential |
| currency | CHAR(3) | Yes | Quote currency | ISO 4217 | Internal |
| status | ENUM | Yes | Quotation status | in (draft, submitted, accepted, rejected, expired) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Vendor Assignment

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| vendor_assignment_id | UUID | Yes | Unique vendor assignment | UUID v4 format | Internal |
| vendor_id | UUID | Yes | Assigned vendor | must exist in vendor.vendor_id | Confidential |
| service_request_id | UUID | Yes | Related service request | must exist in service_request.service_request_id | Confidential |
| assigned_by | UUID | Yes | User assigning vendor | must exist in app_user.user_id | Confidential |
| assigned_at | TIMESTAMP | Yes | Assignment timestamp | ISO-8601 UTC | Internal |
| status | VARCHAR(30) | Yes | Assignment status | in (assigned, accepted, in_progress, completed, cancelled) | Internal |

---

# Complaint Domain

## Entity: Complaint

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| complaint_id | UUID | Yes | Unique complaint ID | UUID v4 format | Confidential |
| customer_id | UUID | Yes | Complaining customer | must exist in customer.customer_id | Confidential |
| property_id | UUID | No | Related property | must exist in property.property_id | Confidential |
| complaint_title | VARCHAR(255) | Yes | Complaint title | 1–255 chars | Internal |
| complaint_description | TEXT | Yes | Complaint details | max 5000 chars | Confidential |
| status | ENUM | Yes | Complaint status | in (open, assigned, pending, resolved, closed, escalated) | Internal |
| priority | VARCHAR(30) | Yes | Complaint priority | in (low, medium, high, urgent) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Complaint Comment

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| complaint_comment_id | UUID | Yes | Unique complaint comment ID | UUID v4 format | Confidential |
| complaint_id | UUID | Yes | Parent complaint | must exist in complaint.complaint_id | Confidential |
| comment_by | UUID | Yes | User posting comment | must exist in app_user.user_id | Confidential |
| comment_text | TEXT | Yes | Comment content | max 5000 chars | Confidential |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |

---

# Notification Domain

## Entity: Notification

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| notification_id | UUID | Yes | Unique notification ID | UUID v4 format | Confidential |
| customer_id | UUID | No | Notification recipient customer | must exist in customer.customer_id | Confidential |
| user_id | UUID | No | Notification recipient user | must exist in app_user.user_id | Confidential |
| notification_type | VARCHAR(100) | Yes | Notification type | in (sms, email, whatsapp, in_app, push) | Internal |
| template_id | UUID | No | Notification template reference | must exist in notification_template.template_id | Internal |
| subject | VARCHAR(255) | No | Email subject | max 255 chars | Internal |
| body | TEXT | Yes | Notification message | max 5000 chars | Confidential |
| status | ENUM | Yes | Notification state | in (queued, sent, delivered, failed, read, clicked) | Internal |
| sent_at | TIMESTAMP | No | Send timestamp | ISO-8601 UTC | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

## Entity: Notification Template

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| template_id | UUID | Yes | Unique template ID | UUID v4 format | Internal |
| template_name | VARCHAR(255) | Yes | Template name | 1–255 chars | Internal |
| template_type | VARCHAR(100) | Yes | Template category | in (sms, email, whatsapp, push) | Internal |
| subject | VARCHAR(255) | No | Email subject | max 255 chars | Internal |
| body_template | TEXT | Yes | Message body template | valid template syntax | Internal |
| status | ENUM | Yes | Template state | in (draft, active, inactive, archived) | Internal |
| created_at | TIMESTAMP | Yes | Creation time | ISO-8601 UTC | Internal |
| updated_at | TIMESTAMP | Yes | Last modification time | ISO-8601 UTC | Internal |

---

# Audit Domain

## Entity: Audit Log

| Field Name | Data Type | Required | Description | Validation | Classification |
|---|---|---|---|---|---|
| audit_log_id | UUID | Yes | Unique audit record | UUID v4 format | Restricted |
| entity_type | VARCHAR(100) | Yes | Audited entity type | 1–100 chars | Internal |
| entity_id | UUID | Yes | Audited entity ID | valid UUID | Restricted |
| event_type | VARCHAR(100) | Yes | Audit event type | 1–100 chars | Internal |
| actor_user_id | UUID | No | Acting user | must exist in app_user.user_id | Restricted |
| actor_role | VARCHAR(50) | No | Actor role | valid role enum | Internal |
| action_summary | TEXT | Yes | Human-readable action description | max 2000 chars | Restricted |
| before_value_json | JSONB | No | Previous value snapshot | valid JSON | Restricted |
| after_value_json | JSONB | No | New value snapshot | valid JSON | Restricted |
| source_ip | VARCHAR(64) | No | Client source IP | IPv4/IPv6 valid | Restricted |
| created_at | TIMESTAMP | Yes | Audit event time | ISO-8601 UTC | Restricted |

---

# Enum Catalog

## Customer Status
| Value | Meaning |
|---|---|
| active | customer account is active |
| inactive | account is temporarily inactive |
| pending | onboarding or verification pending |
| blocked | account restricted due to compliance or risk |
| archived | account retained for historical access only |

## Property Status
| Value | Meaning |
|---|---|
| draft | property not yet fully configured |
| active | property is available for use |
| inactive | property disabled temporarily |
| under_maintenance | property under maintenance work |
| archived | property retained in historical record only |

## Service Request Status
| Value | Meaning |
|---|---|
| draft | request created but not submitted |
| open | request awaiting assignment |
| assigned | task assigned to agent or vendor |
| in_progress | work underway |
| resolved | issue addressed |
| closed | final closure complete |
| cancelled | request cancelled |

## Subscription Status
| Value | Meaning |
|---|---|
| draft | plan not yet active |
| active | subscription currently valid |
| paused | subscription temporarily paused |
| cancelled | cancelled before expiry |
| expired | subscription ended due to expiry |

## Payment Status
| Value | Meaning |
|---|---|
| initiated | payment request started |
| authorized | provider authorized but not settled |
| paid | payment completed successfully |
| failed | payment attempt failed |
| refunded | reverse payment completed |
| disputed | payment under dispute |
| reversed | payment reversed by system |

## Refund Status
| Value | Meaning |
|---|---|
| requested | refund requested |
| approved | refund approved |
| processed | refund completed |
| failed | processing failed |
| rejected | request rejected |

## Visit Status
| Value | Meaning |
|---|---|
| scheduled | visit planned |
| started | visit started |
| completed | visit finished |
| cancelled | visit cancelled |
| no_show | customer or agent did not attend |

## Complaint Status
| Value | Meaning |
|---|---|
| open | complaint waiting for action |
| assigned | complaint assigned to owner |
| pending | waiting on customer or vendor |
| resolved | action completed |
| closed | complaint finalized |
| escalated | complaint raised to higher level |

## Notification Status
| Value | Meaning |
|---|---|
| queued | notification queued for delivery |
| sent | provider accepted send |
| delivered | delivery confirmed |
| failed | delivery failed |
| read | message opened/read |
| clicked | link clicked |

---

# Validation Rules

## 1. Common Validation Rules

| Field/Category | Format | Length | Regex / Rule | Business Rule |
|---|---|---|---|---|
| UUID | UUID v4 | 36 chars | standard UUID regex | must be unique across domain table |
| Email | RFC 5322-compatible | <= 255 | standard email regex | must be unique per customer/user if active |
| Mobile Number | E.164 or country-specific | <= 20 | `^\+[1-9]\d{1,14}$` or country equivalent | must be verified for OTP and notifications |
| ISO Currency | 3-letter ISO code | 3 | `[A-Z]{3}` | must match configured legal currency |
| Timestamp | ISO-8601 UTC | n/a | `YYYY-MM-DDTHH:MM:SSZ` | must be timezone-normalized |
| Latitude | decimal | <= 9,6 precision | between -90 and 90 | valid geographic coordinate |
| Longitude | decimal | <= 9,6 precision | between -180 and 180 | valid geographic coordinate |
| Postal Code | country-specific | <= 20 | varies by country | display and validation according to country |
| Amount | decimal | <= 18,2 | numeric > 0 where required | validated against currency rules |

## 2. Critical Field Rules by Domain

### Customer
- `mobile_number`: must be unique for active customer accounts
- `email`: must be unique for active identity records
- `status`: must match enum definitions and lifecycle transitions

### Property
- `survey_number`: required for legal/compliance workflows when applicable
- `ownership_type`: must be set prior to property activation
- `status`: active properties must have valid location and ownership data

### Service Request
- `priority`: required for all active requests
- `status`: cannot move to resolved without evidence or validation step
- `scheduled_at`: must be greater than or equal to requested_at if provided

### Subscription
- `price`: must be non-negative
- `auto_renew`: required for subscription lifecycle processing
- `renewal_amount`: must align with plan pricing prior to processing

### Payment
- `amount`: must be greater than zero
- `status`: must be immutable once settled unless explicit reversal workflow
- `gateway_transaction_id`: required for settled or failed processed payments

### Evidence
- `checksum`: required for all uploaded files
- `file_url`: must resolve to accessible storage path
- `upload_status`: transitions must be validated and auditable

### Notification
- `body`: may contain template variables but must be sanitized
- `status`: must not be set to delivered without provider callback or explicit send confirmation

---

# Data Retention Rules

Retention is governed by domain, legal compliance, and business retention requirements.

| Entity | Retention Period | Archive Policy | Purge Policy |
|---|---|---|---|
| Customer | 7 years after account closure | archive after closure | purge only after legal approval |
| Customer Address | 7 years after customer closure | archive with customer record | purge with customer data |
| Property | 10 years after property archival | archive on property retirement | purge after legal review |
| Property Document | 7 years or per legal requirement | archive to cold storage | purge only after compliance signoff |
| Service Request | 5 years after closure | archive after completion | purge after retention window |
| Subscription Plan | 10 years or until product sunset | archive in config history | purge after decommission |
| Customer Subscription | 7 years after expiry/cancel | archive with billing history | purge under compliance policy |
| Payment | 7 years or longer depending on regulation | archive ledger and reconciliation records | purge only with legal approval |
| Refund | 7 years | archive with payment record | purge after legal review |
| Invoice | 7 years | archive for financial audit | purge with legal approval |
| Agent | 7 years post employment | archive in personnel record | purge after policy approval |
| Visit | 5 years | archive if used in compliance or dispute | purge after retention period |
| Evidence | 5–7 years depending on category | store in immutable archive tier | purge after legal retention period |
| Report | 2–5 years depending on use | archive generated report outputs | purge with report lifecycle policy |
| Vendor | 7 years | archive vendor compliance and contract records | purge after approval |
| Complaint | 5 years | archive resolved complaints and actions | purge after retention window |
| Notification | 2 years | archive delivery logs and metadata | purge after message retention window |
| Audit Log | 7 years minimum | immutable archive | purge only under legal review |

---

# PII Classification

## PII
Personal information that directly identifies or could reasonably identify a person.
- customer_id
- first_name
- last_name
- mobile_number
- email
- address_line_1
- city
- state
- postal_code
- country
- landlord or resident contact identifiers

## Sensitive PII
Higher-risk personal information requiring stricter controls.
- KYC document references
- Aadhaar or government ID numbers
- PAN, CKYC, passport numbers
- biometrics
- exact GPS location tied to a person
- payment and banking metadata

## Financial Data
- payment_id
- amount
- invoice_number
- bank account references
- payment_method
- refund_amount
- ledger balances
- external payment provider IDs

## Audit Data
- audit_log_id
- actor_user_id
- source_ip
- before_value_json
- after_value_json
- event_type
- entity_type

---

# Related Documents

- Canonical_Data_Model.md
- Database_Physical_Model.md
- API_Catalog.md
- OpenAPI_Specification.yaml
- Cross_Cutting_Requirements.md

---

## Summary

This Canonical Data Dictionary establishes the authoritative meaning and usage of every essential PropertyPilot data element. It is intended to align:
- database design
- application logic
- API contracts
- UI form generation
- reports and dashboards
- data governance
- compliance and retention

This document must be maintained as the primary source of truth for data definitions and validation behavior.
