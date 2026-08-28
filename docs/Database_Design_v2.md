# PropertyPilot Database Design

## Version

2.0

---
## Status

Phase 1 Foundation Design

# Purpose

This document defines the database architecture for PropertyPilot.

Shared lifecycle, financial, evidence, subscription, authorization, and operational invariants are defined in [Cross_Cutting_Requirements.md](Cross_Cutting_Requirements.md). The database implementation shall enforce those invariants with constraints, unique keys, immutable event records, and transactional state transitions.

The database shall support:

- Property Management
- Service Management
- Agent Operations
- Pricing Engine
- Agent Payout Engine
- Subscription Management
- Vendor Management
- Quotation Management
- Marketplace Management
- Media Evidence Management
- Reporting
- Notifications
- Complaint & Dispute Management
- Review & Rating Management
- Payment Processing
---

# Database Design Goals

- Scalability
- Maintainability
- Configurability
- Auditability
- Extensibility
- Security
- Performance

---

# Database Principles

## Principle 1: Configuration Over Code

Business rules shall be stored in database tables wherever possible.

Examples:

- Pricing Rules
- ETA Rules
- Agent Payout Rules
- Coverage Rules
- Subscription Rules

---

## Principle 2: Auditability

All critical business activities shall be traceable.

---

## Principle 3: Soft Delete

All major business entities shall support:

- is_active
- is_deleted
- deleted_at
- deleted_by

---

## Principle 4: Geographic Scalability

Support:

Country
↓
State
↓
District
↓
Mandal
↓
Village
↓
Coverage Zone
↓
Cluster

---

## Principle 5: Relationship Driven Design

Normalize relationships where appropriate.

---

## Principle 6: Pricing ≠ Agent Payout

Customer pricing and agent compensation shall remain independent.

---

## Principle 7: Future Readiness

Support future expansion without major redesign.

---

# Naming Standards

## Table Names

snake_case

Examples:

- users
- properties
- service_requests

---

## Column Names

snake_case

Examples:

- created_at
- property_id
- service_request_id

---

## Primary Keys

UUID

---

## Foreign Keys

Pattern:

```text
<table_name>_id
```

Examples:

- user_id
- property_id
- vendor_id

---

# Standard Audit Fields

Every major business table shall contain:

- id
- created_at
- created_by
- updated_at
- updated_by
- is_active
- is_deleted
- deleted_at
- deleted_by

Immutable history and event tables may omit lifecycle fields, but shall retain
their own actor and timestamp fields where applicable.

Unless a table definition explicitly states otherwise, the standard audit
fields above are part of that table even when they are omitted from the
abbreviated definition below.

## Decision 1

A property may have multiple owners.

Implementation:

Property
↓
Property Owners
↓
Users

---

## Decision 2

Every service request shall belong to a property.

---

## Decision 3

Pricing Engine and Agent Payout Engine shall remain separate.

---

## Decision 4

Business rules shall be configurable.

---

## Decision 5

Soft delete shall be used across business entities.

---
## Decision 6

Total ownership percentage for a property should equal 100%.

---

# Domain Summary


## Identity Domain

Core authentication and authorization entities:

- users
- user_profiles
- roles
- user_roles
- user_sessions
- otp_history

---

## Property Domain

Property master and ownership model:

- properties
- property_owners (UNIQUE(property_id, user_id))
- property_documents
- property_tags
- property_tag_mapping
- property_status_history
- property_code: VARCHAR(50) NOT NULL UNIQUE
- Property Code Format: PP-PROP-000001


---

## Geo Domain

Geographic hierarchy and operational coverage:

- countries
- states UNIQUE(country_id, name)
- districts UNIQUE(state_id, name)
- mandals UNIQUE(district_id, name)
- villages UNIQUE(mandal_id, name)
- coverage_zones
- clusters
- cluster_villages
### states
UNIQUE(country_id, name)

### districts
UNIQUE(state_id, name)

### mandals
UNIQUE(district_id, name)

### villages
UNIQUE(mandal_id, name)

---

## Service Domain


---

## Pricing Domain

(To Be Designed)

---
## Configuration Domain

Purpose:
System-wide configurable settings.

Planned Tables:

- system_configurations
- business_rules
- lookup_values
- feature_flags
---

## Agent Domain

(To Be Designed)

---

## Subscription Domain

(To Be Designed)

---

## Vendor Domain

(To Be Designed)

---

## Quotation Domain

(To Be Designed)

---

## Marketplace Domain

(To Be Designed)

---

## CRM Domain

(To Be Designed)

---

## Media Domain

(To Be Designed)

The planned `media_files` table is the target of `property_documents.media_file_id`.

---

## Reporting Domain

(To Be Designed)

---

## Notification Domain

(To Be Designed)

---

## Complaint Domain

(To Be Designed)

---

## Review Domain

(To Be Designed)

---

## Payment Domain

(To Be Designed)

---

## Security Domain

(To Be Designed)

---

# Core Schema Definitions

The following tables represent the Phase 1 foundation for the system. They are intentionally aligned with the audit, soft-delete, and domain-driven design principles defined earlier in this document.

## Identity Domain

### Table: users

Core user identity and login credentials.

- id: UUID PRIMARY KEY
- email: VARCHAR(255) NULL, UNIQUE
- phone_number: VARCHAR(20) NULL, UNIQUE
- password_hash: VARCHAR(255) NULL
- auth_provider: VARCHAR(30) DEFAULT 'local'
- first_name: VARCHAR(100) NULL
- last_name: VARCHAR(100) NULL
- user_type: VARCHAR(30) NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- is_deleted: BOOLEAN DEFAULT FALSE
- deleted_at: TIMESTAMP NULL
- deleted_by: UUID NULL
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL

### Table: user_profiles

Extended profile data linked to a user.

- id: UUID PRIMARY KEY
- user_id: UUID NOT NULL UNIQUE FK -> users.id
- profile_image_url: TEXT NULL
- gender: VARCHAR(20) NULL
- date_of_birth: DATE NULL
- address_line_1: TEXT NULL
- address_line_2: TEXT NULL
- city: VARCHAR(100) NULL
- state_id: UUID NULL FK -> states.id
- country_id: UUID NULL FK -> countries.id
- emergency_contact_name: VARCHAR(150) NULL
- emergency_contact_phone: VARCHAR(20) NULL
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL

### Table: roles

System permission groups.

- id: UUID PRIMARY KEY
- name: VARCHAR(100) UNIQUE NOT NULL
- description: TEXT NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL

### Table: user_roles

Many-to-many mapping between users and roles.

- id: UUID PRIMARY KEY
- user_id: UUID NOT NULL FK -> users.id
- role_id: UUID NOT NULL FK -> roles.id
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL
- UNIQUE(user_id, role_id)

### Table: user_sessions

Stores active and historical authenticated sessions.

- id: UUID PRIMARY KEY
- user_id: UUID NOT NULL FK -> users.id
- session_token: TEXT NOT NULL
- refresh_token: TEXT NULL
- device_id: VARCHAR(255) NULL
- device_type: VARCHAR(50) NULL
- ip_address: VARCHAR(45) NULL
- user_agent: TEXT NULL
- expires_at: TIMESTAMP NOT NULL
- revoked_at: TIMESTAMP NULL
- created_at: TIMESTAMP NOT NULL

### Table: otp_history

Tracks OTP generation, verification, and expiry.

- id: UUID PRIMARY KEY
- user_id: UUID NULL FK -> users.id
- phone_number: VARCHAR(20) NULL
- email: VARCHAR(255) NULL
- otp_code: VARCHAR(10) NOT NULL
- purpose: VARCHAR(50) NOT NULL
- is_used: BOOLEAN DEFAULT FALSE
- sent_at: TIMESTAMP NOT NULL
- expires_at: TIMESTAMP NOT NULL
- verified_at: TIMESTAMP NULL
- created_at: TIMESTAMP NOT NULL

---

## Property Domain

### Table: properties

Master property records.

- id: UUID PRIMARY KEY
- property_name: VARCHAR(255) NOT NULL
- property_type: VARCHAR(50) NOT NULL
- property_status: VARCHAR(50) NOT NULL DEFAULT 'draft'
- address_line_1: TEXT NOT NULL
- address_line_2: TEXT NULL
- village_id: UUID NULL FK -> villages.id
- mandal_id: UUID NULL FK -> mandals.id
- district_id: UUID NULL FK -> districts.id
- state_id: UUID NULL FK -> states.id
- country_id: UUID NULL FK -> countries.id
- latitude: DECIMAL(10,8) NULL
- longitude: DECIMAL(11,8) NULL
- area_sq_ft: DECIMAL(12,2) NULL
- bedrooms: INT NULL
- bathrooms: INT NULL
- is_active: BOOLEAN DEFAULT TRUE
- is_deleted: BOOLEAN DEFAULT FALSE
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL
- property_code: VARCHAR(50) NOT NULL UNIQUE
- CHECK(latitude IS NULL OR latitude BETWEEN -90 AND 90)
- CHECK(longitude IS NULL OR longitude BETWEEN -180 AND 180)
- CHECK(area_sq_ft IS NULL OR area_sq_ft >= 0)
- CHECK(bedrooms IS NULL OR bedrooms >= 0)
- CHECK(bathrooms IS NULL OR bathrooms >= 0)
- The village, mandal, district, state, and country references must describe one valid geographic hierarchy; enforce this with composite foreign keys or service-layer validation.

### Table: property_owners

Maps multiple owners to a property.

- id: UUID PRIMARY KEY
- property_id: UUID NOT NULL FK -> properties.id
- user_id: UUID NOT NULL FK -> users.id
- ownership_type: VARCHAR(30) DEFAULT 'owner'
- share_percentage: DECIMAL(5,2) NOT NULL
- is_primary_owner: BOOLEAN DEFAULT FALSE
- is_active: BOOLEAN DEFAULT TRUE
- is_deleted: BOOLEAN DEFAULT FALSE
- deleted_at: TIMESTAMP NULL
- deleted_by: UUID NULL FK -> users.id
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL FK -> users.id
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL FK -> users.id
- UNIQUE(property_id, user_id)
- CHECK(share_percentage >= 0 AND share_percentage <= 100)
- Sum of share_percentage for a property = 100%, enforced by a deferred constraint trigger or transaction-level service validation


### Table: property_documents

Property legal and verification documents.

- id: UUID PRIMARY KEY
- property_id: UUID NOT NULL FK -> properties.id
- document_type: VARCHAR(50) NOT NULL
- document_url: TEXT NOT NULL
- uploaded_by: UUID NULL FK -> users.id
- verified_by: UUID NULL FK -> users.id
- verified_at: TIMESTAMP NULL
- expiry_date: TIMESTAMP NULL
- verification_status: VARCHAR(50) NOT NULL DEFAULT 'pending'
- media_file_id: UUID NULL FK -> media_files.id
- is_active: BOOLEAN DEFAULT TRUE
- is_deleted: BOOLEAN DEFAULT FALSE
- deleted_at: TIMESTAMP NULL
- deleted_by: UUID NULL FK -> users.id
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL FK -> users.id
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL FK -> users.id
- CHECK(verification_status <> 'verified' OR (verified_by IS NOT NULL AND verified_at IS NOT NULL))

### Table: property_tags

Reusable property tags.

- id: UUID PRIMARY KEY
- tag_name: VARCHAR(100) UNIQUE NOT NULL
- tag_group: VARCHAR(50) NULL
- is_active: BOOLEAN DEFAULT TRUE

### Table: property_tag_mapping

Maps tags to properties.

- id: UUID PRIMARY KEY
- property_id: UUID NOT NULL FK -> properties.id
- tag_id: UUID NOT NULL FK -> property_tags.id
- created_at: TIMESTAMP NOT NULL
- UNIQUE(property_id, tag_id)

### Table: property_status_history

Tracks property lifecycle state changes.

- id: UUID PRIMARY KEY
- property_id: UUID NOT NULL FK -> properties.id
- previous_status: VARCHAR(50) NULL
- new_status: VARCHAR(50) NOT NULL
- changed_by: UUID NULL FK -> users.id
- change_reason: TEXT NULL
- created_at: TIMESTAMP NOT NULL

---

## Geo Domain

### Table: countries

- id: UUID PRIMARY KEY
- name: VARCHAR(150) NOT NULL
- code: VARCHAR(10) NOT NULL UNIQUE
- is_active: BOOLEAN DEFAULT TRUE
- UNIQUE(name)

### Table: states

- id: UUID PRIMARY KEY
- country_id: UUID NOT NULL FK -> countries.id
- name: VARCHAR(150) NOT NULL
- code: VARCHAR(20) NULL
- is_active: BOOLEAN DEFAULT TRUE
- UNIQUE(country_id, name)

### Table: districts

- id: UUID PRIMARY KEY
- state_id: UUID NOT NULL FK -> states.id
- name: VARCHAR(150) NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- UNIQUE(state_id, name)

### Table: mandals

- id: UUID PRIMARY KEY
- district_id: UUID NOT NULL FK -> districts.id
- name: VARCHAR(150) NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- UNIQUE(district_id, name)

### Table: villages

- id: UUID PRIMARY KEY
- mandal_id: UUID NOT NULL FK -> mandals.id
- name: VARCHAR(150) NOT NULL
- postal_code: VARCHAR(20) NULL
- is_active: BOOLEAN DEFAULT TRUE
- UNIQUE(mandal_id, name)

### Table: coverage_zones

- id: UUID PRIMARY KEY
- name: VARCHAR(150) NOT NULL
- geo_level: VARCHAR(50) NOT NULL
- parent_zone_id: UUID NULL FK -> coverage_zones.id
- is_active: BOOLEAN DEFAULT TRUE

### Table: clusters

- id: UUID PRIMARY KEY
- coverage_zone_id: UUID NOT NULL FK -> coverage_zones.id
- name: VARCHAR(150) NOT NULL
- is_active: BOOLEAN DEFAULT TRUE

### Table: cluster_villages

- id: UUID PRIMARY KEY
- cluster_id: UUID NOT NULL FK -> clusters.id
- village_id: UUID NOT NULL FK -> villages.id
- UNIQUE(cluster_id, village_id)

---

## Service Domain

### Table: service_requests

- id: UUID PRIMARY KEY
- property_id: UUID NOT NULL FK -> properties.id
- requested_by: UUID NOT NULL FK -> users.id
- category_id: UUID NOT NULL FK -> service_categories.id
- service_id: UUID NULL FK -> service_catalog.id
- request_status: VARCHAR(50) NOT NULL
- scheduled_at: TIMESTAMP NULL
- priority_level: VARCHAR(30) NULL
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL
- request_number: VARCHAR(50) NOT NULL UNIQUE


### Table: service_status_history

- id: UUID PRIMARY KEY
- service_request_id: UUID NOT NULL FK -> service_requests.id
- old_status: VARCHAR(50) NULL
- new_status: VARCHAR(50) NOT NULL
- changed_by: UUID NULL FK -> users.id
- remarks: TEXT NULL
- changed_at: TIMESTAMP NOT NULL
- created_at: TIMESTAMP NOT NULL

### Table: service_assignments

- id: UUID PRIMARY KEY
- service_request_id: UUID NOT NULL FK -> service_requests.id
- assignment_status: VARCHAR(50) NOT NULL
- assigned_at: TIMESTAMP NOT NULL
- completed_at: TIMESTAMP NULL
- is_active: BOOLEAN DEFAULT TRUE
- is_deleted: BOOLEAN DEFAULT FALSE
- deleted_at: TIMESTAMP NULL
- deleted_by: UUID NULL FK -> users.id
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL FK -> users.id
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL FK -> users.id

### Table: service_categories

- id: UUID PRIMARY KEY
- name: VARCHAR(150) NOT NULL
- parent_category_id: UUID NULL FK -> service_categories.id
- is_active: BOOLEAN DEFAULT TRUE

### Table: service_catalog

- id: UUID PRIMARY KEY
- category_id: UUID NOT NULL FK -> service_categories.id
- service_name: VARCHAR(200) NOT NULL
- description: TEXT NULL
- base_price: DECIMAL(12,2) NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- CHECK(base_price IS NULL OR base_price >= 0)

### Table: service_assignment_resources

- id: UUID PRIMARY KEY
- service_assignment_id: UUID NOT NULL FK -> service_assignments.id
- resource_type: VARCHAR(50) NOT NULL
- resource_id: UUID NOT NULL
- role: VARCHAR(50) NULL
- created_at: TIMESTAMP NOT NULL
- Resource references are polymorphic; resource_type must map to an approved resource table and be validated by the application or replaced with type-specific foreign keys.

---

## Pricing Domain

### Table: pricing_rules

- id: UUID PRIMARY KEY
- rule_name: VARCHAR(150) NOT NULL
- rule_type: VARCHAR(50) NOT NULL
- applies_to: VARCHAR(50) NOT NULL
- effective_from: TIMESTAMP NOT NULL
- effective_to: TIMESTAMP NULL
- rule_payload: JSONB NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- created_by: UUID NULL FK -> users.id
- updated_at: TIMESTAMP NOT NULL
- updated_by: UUID NULL FK -> users.id
- CHECK(effective_to IS NULL OR effective_to >= effective_from)

### Table: food_allowance_rules

- id: UUID PRIMARY KEY
- rule_name: VARCHAR(150) NOT NULL
- min_distance_km: DECIMAL(10,2) NULL
- max_distance_km: DECIMAL(10,2) NULL
- allowance_amount: DECIMAL(12,2) NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL
- CHECK(min_distance_km IS NULL OR min_distance_km >= 0)
- CHECK(max_distance_km IS NULL OR max_distance_km >= 0)
- CHECK(max_distance_km IS NULL OR min_distance_km IS NULL OR max_distance_km >= min_distance_km)
- CHECK(allowance_amount >= 0)

### Table: platform_fee_rules

- id: UUID PRIMARY KEY
- fee_type: VARCHAR(50) NOT NULL
- percentage_value: DECIMAL(10,4) NULL
- fixed_amount: DECIMAL(12,2) NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL
- CHECK((percentage_value IS NOT NULL) <> (fixed_amount IS NOT NULL))
- CHECK(percentage_value IS NULL OR percentage_value BETWEEN 0 AND 100)
- CHECK(fixed_amount IS NULL OR fixed_amount >= 0)

---

# Indexing Strategy

(To Be Designed)

---

# Archival Strategy

(To Be Designed)

---

# Scalability Strategy

(To Be Designed)

---

# ER Relationship Summary

users
 ↓
property_owners
 ↓
properties
 ↓
service_requests
 ↓
service_assignments


# Future Enhancements

## Phase 2

- AI Property Insights
- AI Pricing Recommendations
- AI Vendor Matching
- AI Service Recommendations

## Phase 3

- Satellite Monitoring
- Drone Monitoring
- Route Optimization
- Demand Forecasting

## Phase 4

- Franchise Management
- Multi-Country Support
- White Label Platform
# Assumptions & Constraints
## Assumptions

- UUID used across all domains
- Soft delete enabled
- Multi-owner properties supported

## Constraints

- Service Request requires Property
- Ownership percentage = 100%
- Marketplace listing requires active property