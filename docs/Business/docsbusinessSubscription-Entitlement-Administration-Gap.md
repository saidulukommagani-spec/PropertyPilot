# Subscription Entitlement Administration Gap Analysis

## Status
OPEN

## Priority
HIGH

## Phase
Phase 1

## Module
Subscription Management

---

# Background

PropertyPilot currently supports:

1. Subscription Plans
2. Subscription Plan Versions
3. Customer Subscriptions
4. Subscription Entitlement Consumption
5. Usage Tracking
6. Remaining Entitlement Tracking

The database table:

subscription_plan_entitlements

exists and is actively used by the entitlement consumption engine.

However, entitlement records can currently only be created through direct database inserts.

No API layer exists for entitlement administration.

---

# Current State

## Available

### Subscription Plan APIs

- Create Plan
- Update Plan
- Get Plan

### Subscription Version APIs

- Create Version
- Update Version
- Get Version

### Customer Subscription APIs

- Create Customer Subscription
- Update Customer Subscription
- Get Customer Subscription

### Entitlement Consumption APIs

GET /api/subscription-consumptions/{customerSubscriptionId}/usage

GET /api/subscription-consumptions/{customerSubscriptionId}/remaining

POST /api/subscription-consumptions

---

## Missing

### Entitlement Administration APIs

Not Available

POST /api/subscription-plan-entitlements

GET /api/subscription-plan-entitlements/{id}

GET /api/subscription-plan-versions/{planVersionId}/entitlements

PUT /api/subscription-plan-entitlements/{id}

DELETE /api/subscription-plan-entitlements/{id}

---

# Issue Identified During Audit

Entitlement Usage API initially returned:

[]

even though entitlement functionality existed.

Root Cause:

Customer subscriptions were linked to plan version:

a6a0076d-54c6-48a0-be15-9bda1284e4af

while entitlement records existed only for:

b29bde62-8500-41dc-9524-c12bb7da571e

After manually inserting an entitlement record for the active plan version:

a6a0076d-54c6-48a0-be15-9bda1284e4af

usage APIs started working correctly.

This confirmed:

- Consumption Engine Works
- Usage APIs Work
- Remaining APIs Work
- Entitlement Lookup Works

The missing component is Entitlement Administration.

---

# Enterprise Architecture

## Desired Flow

Admin
    ↓
Create Subscription Plan
    ↓
Create Plan Version
    ↓
Configure Entitlements
    ↓
Publish Version
    ↓
Customer Purchases Plan
    ↓
Usage Tracking
    ↓
Consumption Tracking

---

# Current Flow

Admin
    ↓
Create Plan
    ↓
Create Version
    ↓
Run Manual SQL
    ↓
Customer Purchases Plan

This is not acceptable for enterprise production deployment.

---

# Required APIs

## Create Entitlement

POST

/api/admin/subscription-plan-versions/{planVersionId}/entitlements

Request

{
  "serviceId": "UUID",
  "quantity": 24,
  "periodType": "ANNUAL",
  "carryForwardAllowed": false
}

---

## Get Plan Version Entitlements

GET

/api/admin/subscription-plan-versions/{planVersionId}/entitlements

---

## Get Entitlement

GET

/api/admin/subscription-plan-entitlements/{entitlementId}

---

## Update Entitlement

PUT

/api/admin/subscription-plan-entitlements/{entitlementId}

---

## Delete Entitlement

DELETE

/api/admin/subscription-plan-entitlements/{entitlementId}

---

# Frontend Requirements

## Plan Version Screen

Plan Version Details

Services Included

- Document Procurement Service
  - 24 Per Year

- Property Inspection
  - 12 Per Year

- Tax Filing Assistance
  - 4 Per Year

[ Add Service ]

---

## Add Entitlement Popup

Service

Quantity

Period Type

Carry Forward Allowed

Save

Cancel

---

# Business Rules

## Rule 1

A service can be added only once per plan version.

---

## Rule 2

Quantity must be greater than zero.

---

## Rule 3

Only ACTIVE services can be assigned.

---

## Rule 4

Entitlements cannot be modified after plan version is PUBLISHED.

Changes require creation of a new plan version.

---

## Rule 5

Deleting an entitlement should not impact historical customer subscriptions.

Historical subscriptions retain entitlement snapshot.

---

# Future Enhancement

Support unlimited usage.

Example

{
  "serviceId": "...",
  "quantity": -1,
  "unlimited": true
}

---

# Gap Matrix Entry

Module:
Subscription Entitlement Administration

Current Status:
Missing

Priority:
High

Sprint Estimate:
1 Sprint

Impact:
Required for enterprise-grade subscription management

Owner:
Subscription Domain

Target Release:
Phase 1 Completion

---

# Completion Criteria

The module will be considered complete when:

- Entitlement Create API exists
- Entitlement Update API exists
- Entitlement Delete API exists
- Entitlement List API exists
- Swagger documentation exists
- Controller tests exist
- Admin UI supports entitlement management
- Manual SQL is no longer required

---

# Final Assessment

Current Subscription Domain Maturity:
82%

Expected After Completion:
95%+

This gap must be completed before declaring the Subscription Management module production-ready.


Subscription Entitlements are designed as admin-configurable plan-version-specific service allowances. The underlying data model supports this capability, but entitlement administration APIs and UI are not yet implemented.


Module:
Subscription Entitlement Administration

Status:
Backlog / Phase-2 Candidate

Architecture:
Admin Configurable

Current State:
Database Model Exists
Consumption Engine Exists
Usage APIs Exist

Missing:
Admin CRUD APIs
Admin UI
Controller Tests

Priority:
Not blocking current production-readiness validation

