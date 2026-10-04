# PP-ADR-007: Subscription Add-On Capability Classification

**Status:** Approved

**Date:** 2026-10-03

**Decision Type:** Architecture & Product Scope Decision

---

# Background

PropertyPilot backend contains Subscription Add-On capability through the following components:

## Database Tables

- subscription_add_ons
- customer_subscription_add_ons

## APIs

- CustomerSubscriptionAddOnController

## Services

- CustomerSubscriptionAddOnService

## Domain Entities

- SubscriptionAddOnEntity
- CustomerSubscriptionAddOnEntity

---

# Review Context

This review was performed during Phase-1 Production Readiness and End-to-End (E2E) Validation.

Completed E2E Flows:

- Flow #1 – Subscription Plan Version Lifecycle ✅
- Flow #2 – Subscription Entitlement Lifecycle ✅
- Flow #3 – Customer Subscription Lifecycle ✅

During Flow #4 (Customer Subscription Add-On Lifecycle) validation, a detailed architecture and business review was performed.

---

# Observations

## Database State

Current database contains no Subscription Add-On master records.

```sql
SELECT *
FROM subscription_add_ons;
```

Result:

```text
0 rows
```

## API Availability

Customer Subscription Add-On APIs exist.

However, no Subscription Add-On Master Management APIs currently exist to:

- Create Add-Ons
- Update Add-Ons
- Activate/Deactivate Add-Ons
- Manage Add-On Pricing

## Business Workflow

No approved customer journey currently requires Subscription Add-On functionality.

---

# Business Analysis

Current PropertyPilot workflows are fully supported through:

```text
Customer
    ↓
Service Catalog
    ↓
Pricing Engine
    ↓
Service Request
    ↓
Visit
    ↓
Evidence
```

Examples already supported:

- Property Verification
- EC Verification
- Mutation Support
- Registration Support
- Property Tax Payment
- Tenant Verification
- Legal Opinion
- Documentation Services

These offerings are already modeled as Services and do not require Subscription Add-On functionality.

---

# Architectural Assessment

The Subscription Add-On capability does not currently participate in any core operational workflow.

The implementation represents a potential future business capability rather than an active launch requirement.

Current launch scope can operate successfully without enabling this capability.

---

# Capability Classification

| Attribute | Value |
|------------|------------|
| Capability Type | Strategic Expansion Capability |
| Business Status | Dormant |
| Release Scope | Excluded from Phase-1 Launch |
| Production Dependency | No |
| Frontend Dependency | No |
| E2E Certification Requirement | No |

---

# Frontend Guidance

## Customer Portal

Do NOT create the following screens in Phase-1:

- Subscription Add-On Catalog
- Subscription Add-On Purchase
- Subscription Add-On History
- Subscription Add-On Management

## Admin Portal

Do NOT create the following screens in Phase-1:

- Subscription Add-On Master Management
- Subscription Add-On Pricing Configuration
- Subscription Add-On Eligibility Configuration
- Subscription Add-On Reporting

---

# Future Review Triggers

This capability should be re-evaluated when any of the following conditions are met:

1. First 100 paying customers onboarded.
2. Six months after production launch.
3. Entitlement booster packs become a business requirement.
4. Premium subscription upgrade offerings are introduced.
5. Revenue expansion initiatives require subscription-based upsell capabilities.

---

# Potential Future Use Cases

The existing framework may be activated for future scenarios such as:

## Entitlement Booster Packs

Examples:

- Additional Property Visits
- Additional Inspections
- Additional Monitoring Requests

## Premium Upgrade Packs

Examples:

- Priority Visit Upgrade
- Priority SLA Upgrade
- Fast-Track Processing

## Premium Evidence Packages

Examples:

- Drone Inspection Package
- Video Walkthrough Package
- Enhanced Reporting Package

## NRI Premium Services

Examples:

- Dedicated Relationship Manager
- Premium Monitoring Package
- Concierge Support Package

---

# Phase-1 Decision

## Approved Decision

- Keep backend implementation.
- Keep database schema.
- Keep APIs and services.
- Do NOT remove existing code.
- Do NOT expose functionality in frontend.
- Do NOT include in Phase-1 E2E certification.
- Mark capability as Dormant.

---

# E2E Tracker Update

| Flow | Status |
|--------|--------|
| Flow #1 – Subscription Plan Version Lifecycle | ✅ Passed |
| Flow #2 – Subscription Entitlement Lifecycle | ✅ Passed |
| Flow #3 – Customer Subscription Lifecycle | ✅ Passed |
| Flow #4 – Customer Subscription Add-On Lifecycle | 🟡 Deferred |

### Flow #4 Deferred Reason

Subscription Add-On capability has been classified as a Strategic Expansion Capability.

The feature is not currently part of any approved operational workflow and is therefore excluded from Phase-1 Production Readiness certification.

---

# Future Backlog Item

## PP-FUTURE-001

### Title

Subscription Add-On Framework Re-Evaluation

### Priority

Low

### Classification

Strategic Expansion Capability

### Status

Dormant

### Target Release

Phase-2 or Later

### Review Date

After:
- First 100 paying customers, OR
- 6 months post go-live

---

# Final Decision

Subscription Add-On capability remains part of the PropertyPilot architecture but is classified as a Dormant Strategic Expansion Capability.

The capability will not be exposed in Phase-1 frontend applications and does not block production launch readiness.