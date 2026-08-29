# PropertyPilot Subscription Management

## Version

1.0

---

# Purpose

The Subscription Management module enables PropertyPilot to offer recurring services through configurable subscription plans.

The module supports subscription creation, renewals, monitoring schedules, billing cycles, upgrades, downgrades, and cancellations.

The primary goal is to generate recurring revenue while providing continuous property monitoring and management services.

---

# Objectives

The Subscription Management module shall:

- Support recurring service plans
- Enable subscription lifecycle management
- Support automated renewals
- Support monitoring schedules
- Support subscription upgrades
- Support subscription downgrades
- Support cancellation workflows
- Support future auto-billing
- Support recurring notifications

---

# Subscription Overview

Customer

↓

Select Subscription Plan

↓

Purchase Subscription

↓

Schedule Created

↓

Recurring Service Execution

↓

Reports Generated

↓

Subscription Renewal

---

# Subscription Types

## Monthly Plan

Recurring every month.

---

## Quarterly Plan

Recurring every 3 months.

---

## Half-Yearly Plan

Recurring every 6 months.

---

## Annual Plan

Recurring every year.

---

## Custom Plan

Admin-configured subscription frequency.

---

# Supported Subscription Products

## Property Monitoring

Monthly Monitoring

Quarterly Monitoring

Annual Monitoring

---

## Agriculture Monitoring

Crop Monitoring

Farm Monitoring

Seasonal Monitoring

---

## Construction Monitoring

Construction Progress Monitoring

Milestone Monitoring

Quality Monitoring

---

## Rental Monitoring

Occupancy Monitoring

Rental Property Monitoring

---

# Subscription Lifecycle

DRAFT

↓

ACTIVE

↓

RENEWAL_DUE

↓

RENEWED

↓

EXPIRED

↓

CANCELLED

---

# Subscription Status

DRAFT

PENDING_PAYMENT

ACTIVE

PAUSED

RENEWAL_DUE

EXPIRED

CANCELLED

SUSPENDED

---

# Subscription Information

Subscription ID

Customer

Property

Product

Plan

Start Date

End Date

Renewal Date

Billing Cycle

Status

---
# Subscription Ownership

Every subscription shall belong to:

Customer

Property

Subscription Plan

Created By

Coverage Zone

Cluster

Assigned Agent (Optional)

All ownership relationships shall be auditable.

# Service Scheduling

Subscription shall generate service schedules automatically.

Example:

Monthly Plan

↓

Every Month

↓

Service Request Generated

↓

Agent Assigned

↓

Service Executed

---
# Service Generation Rules

Subscriptions shall automatically generate service requests based on plan frequency.

## Generation Workflow

Subscription

↓

Next Schedule Date

↓

Service Request Created

↓

Assignment Triggered

↓

Service Executed

↓

Report Generated

## Rules

- Only ACTIVE subscriptions may generate service requests.
- PAUSED subscriptions shall not generate service requests.
- CANCELLED subscriptions shall not generate service requests.
- EXPIRED subscriptions shall not generate service requests.
- Duplicate schedules shall not generate duplicate service requests.
- Generation failures shall be audit logged.

# Renewal Management

Support:

Manual Renewal

Auto Renewal

Admin Renewal

---

# Upgrade Management

Customer may:

Upgrade Plan

Change Frequency

Add Add-Ons

---

# Downgrade Management

Customer may:

Move To Lower Plan

Reduce Frequency

Remove Add-Ons

---

# Pause Subscription

Support:

Temporary Pause

Vacation Pause

Property Unavailable

---

# Cancellation Management

Reasons:

Customer Request

Property Sold

Duplicate Subscription

Admin Action

---

# Notifications

Subscription Created

Renewal Due

Subscription Expiring

Service Scheduled

Service Completed

Plan Upgraded

Plan Downgraded

Subscription Cancelled

---

# Pricing Integration

Integrates With:

Pricing_Engine.md

Supports:

Monthly Pricing

Quarterly Pricing

Annual Pricing

Discount Rules

Promotional Pricing

---

# Marketplace Integration

Future Support

Premium Listing Subscription

Featured Listing Subscription

Seller Subscription Plans

---

# Reporting

Generate:

Active Subscriptions

Expired Subscriptions

Renewal Reports

Subscription Revenue

Plan Adoption Reports

---
# Access Control

Customer

Own Subscriptions Only

Agent

Assigned Subscription Services

Cluster Manager

Cluster Subscriptions

Admin

All Subscriptions

Access shall be governed by role-based permissions.

# Admin Configuration

Admin shall configure:

Plans

Frequencies

Renewal Rules

Pause Rules

Cancellation Rules

Discount Rules

Notification Rules

No code deployment required.

---

# Future Enhancements

Auto Billing

Subscription Wallet

Family Plans

Portfolio Plans

AI Renewal Recommendations

Predictive Churn Analysis

---
# Subscription Plan Master

Plan ID

Plan Name

Plan Type

Monthly Price

Quarterly Price

Annual Price

Included Services

Service Frequency

Add-On Eligibility

Status
# Subscription Add-Ons

Drone Survey

Live Video Inspection

Extra Monitoring Visit

Premium Report

AI Property Analysis

Emergency Visit

Weekend Visit
# Portfolio Plans

Support subscriptions for:

Single Property

Multiple Properties

Corporate Property Portfolios

NRI Portfolios
# Missed Schedule Management

Reasons

Agent Unavailable

Weather

Property Access Issue

Customer Request

System shall:

Reschedule Visit

Notify Customer

Track Missed Visits
# Renewal Workflow

30 Days Before Expiry

↓

Renewal Reminder

↓

Payment

↓

Renewal Confirmation

↓

Next Cycle Created
---
Track:

MRR (Monthly Recurring Revenue)

ARR (Annual Recurring Revenue)

Renewal Rate

Cancellation Rate

Plan Popularity

Revenue Per Property

Revenue Per Cluster
Track:

Subscription Creation

Renewal

Upgrade

Downgrade

Pause

Resume

Cancellation

Plan Change

Payment Status Change
---
Property may have:

0..N Subscriptions

Subscription must belong to:

Customer
Property
Plan
# Suspension

Reasons

Payment Failure

Fraud Detection

Admin Action

Policy Violation

# Business Rules

1. Subscriptions shall generate recurring service schedules.

2. Subscription pricing shall integrate with Pricing Engine.

3. Renewal rules shall be configurable.

4. Subscription plans shall support upgrades and downgrades.

5. Subscription activities shall be audit logged.

6. Subscription configuration shall not require code deployment.

7. Subscription Management shall support future recurring revenue models.