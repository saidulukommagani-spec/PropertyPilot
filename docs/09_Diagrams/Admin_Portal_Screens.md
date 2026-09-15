# PropertyPilot Admin Portal Screens

## Version

1.0

---

# Purpose

This document defines detailed specifications for all Admin and Operations Portal screens.

Objectives:

- Support operations management
- Support customer management
- Support field operations
- Support service delivery
- Support revenue management
- Support platform administration

---

# User Roles

## Super Admin

Responsibilities:

```text
Full Platform Access
```

---

## Operations Manager

Responsibilities:

```text
Task Assignment
Report Review
Agent Management
```

---

## Support Executive

Responsibilities:

```text
Customer Support
Complaint Resolution
```

---

## Finance Manager

Responsibilities:

```text
Payments
Refunds
Revenue
Payouts
```

---

## Regional Manager

Responsibilities:

```text
Cluster Operations
Agent Oversight
```

---

# Portal Navigation

```text
Dashboard
Customers
Properties
Service Requests
Agents
Reports
Subscriptions
Partners
Vendors
Payments
Revenue
Analytics
Settings
```

---

# DASHBOARD MODULE

---

## SCR-ADM-001

### Admin Dashboard

Purpose:

```text
Platform Overview
```

---

### Widgets

```text
Active Customers

Active Properties

Open Service Requests

Pending Reports

Revenue Summary

Subscription Metrics

Agent Performance

Issue Alerts
```

---

### APIs

```text
GET /admin/dashboard
```

---

# CUSTOMER MANAGEMENT

---

## SCR-ADM-002

### Customer List

Components:

```text
Search

Filters

Customer Table

Export
```

---

### Actions

```text
View Customer

Edit Customer

Deactivate Customer
```

---

### APIs

```text
GET /customers
```

---

## SCR-ADM-003

### Customer Details

Components:

```text
Profile

Properties

Subscriptions

Reports

Service History

Complaints
```

---

### APIs

```text
GET /customers/{id}
```

---

# PROPERTY MANAGEMENT

---

## SCR-ADM-004

### Property List

Components:

```text
Search

Filters

Property Table

Map View
```

---

### APIs

```text
GET /properties
```

---

## SCR-ADM-005

### Property Details

Components:

```text
Property Information

Documents

Reports

Service History

Monitoring History
```

---

### APIs

```text
GET /properties/{id}
```

---

# SERVICE REQUEST MANAGEMENT

---

## SCR-ADM-006

### Service Request List

Components:

```text
Filters

Status

Priority

Assignment Status
```

---

### Statuses

```text
Requested

Assigned

In Progress

Review

Completed

Cancelled
```

---

### APIs

```text
GET /service-requests
```

---

## SCR-ADM-007

### Service Request Details

Components:

```text
Customer

Property

Service

Assigned Agent

Timeline
```

---

### Actions

```text
Assign Agent

Reassign Agent

Escalate

Cancel Request
```

---

# AGENT MANAGEMENT

---

## SCR-ADM-008

### Agent List

Components:

```text
Search

Filters

Status

Ratings
```

---

### APIs

```text
GET /agents
```

---

## SCR-ADM-009

### Agent Details

Components:

```text
Profile

Skills

Tasks

Ratings

Payouts

Performance
```

---

### Actions

```text
Activate

Deactivate

Assign Training
```

---

## SCR-ADM-010

### Agent Assignment Console

Components:

```text
Pending Requests

Available Agents

Map View

Assignment Rules
```

---

### Actions

```text
Auto Assign

Manual Assign
```

---

# REPORT MANAGEMENT

---

## SCR-ADM-011

### Report Queue

Components:

```text
Pending Review

Rejected

Approved
```

---

### APIs

```text
GET /reports/review
```

---

## SCR-ADM-012

### Report Review Screen

Components:

```text
Evidence

Observations

Recommendations

Approval Workflow
```

---

### Actions

```text
Approve

Reject

Request Changes
```

---

# SUBSCRIPTION MANAGEMENT

---

## SCR-ADM-013

### Subscription Dashboard

Components:

```text
Active Plans

Expiring Plans

Renewals

Revenue
```

---

### APIs

```text
GET /subscriptions
```

---

## SCR-ADM-014

### Subscription Plan Management

Components:

```text
Plan Catalog

Pricing

Features

Limits
```

---

### Actions

```text
Create Plan

Edit Plan

Deactivate Plan
```

---

# PARTNER MANAGEMENT

---

## SCR-ADM-015

### Partner List

Components:

```text
Lawyers

Surveyors

Contractors

Property Managers
```

---

### APIs

```text
GET /partners
```

---

## SCR-ADM-016

### Partner Details

Components:

```text
Profile

Services

Ratings

Revenue
```

---

# VENDOR MANAGEMENT

---

## SCR-ADM-017

### Vendor List

Components:

```text
Search

Categories

Ratings
```

---

### APIs

```text
GET /vendors
```

---

## SCR-ADM-018

### Vendor Details

Components:

```text
Services

Quotes

Performance

Revenue
```

---

# PAYMENT MANAGEMENT

---

## SCR-ADM-019

### Payments Dashboard

Components:

```text
Successful Payments

Failed Payments

Refunds

Pending Payments
```

---

### APIs

```text
GET /payments
```

---

## SCR-ADM-020

### Refund Management

Components:

```text
Refund Requests

Approval Queue

History
```

---

# REVENUE MANAGEMENT

---

## SCR-ADM-021

### Revenue Dashboard

Components:

```text
Daily Revenue

Monthly Revenue

Subscription Revenue

Commission Revenue
```

---

### APIs

```text
GET /revenue
```

---

# COMPLAINT MANAGEMENT

---

## SCR-ADM-022

### Complaint Dashboard

Components:

```text
Open Cases

Escalated Cases

Resolved Cases
```

---

### APIs

```text
GET /complaints
```

---

## SCR-ADM-023

### Complaint Details

Components:

```text
Customer

Evidence

Resolution

Communication Log
```

---

# ANALYTICS

---

## SCR-ADM-024

### Analytics Dashboard

Widgets:

```text
Customer Growth

Revenue Trends

Agent Performance

Service Performance

Subscription Metrics
```

---

# AUDIT & COMPLIANCE

---

## SCR-ADM-025

### Audit Logs

Components:

```text
User Activity

Changes

Approvals

Security Events
```

---

# SETTINGS

---

## SCR-ADM-026

### System Configuration

Components:

```text
Master Data

Configurations

Rules

Thresholds
```

---

## SCR-ADM-027

### Role Management

Components:

```text
Roles

Permissions

Access Matrix
```

---

# Portal Design Principles

```text
Role Based Access

Minimal Clicks

Search First

Data Driven

Mobile Responsive
```

---

# Performance Targets

```text
Dashboard Load < 3 Seconds

Search < 2 Seconds

Report Review < 2 Seconds

Assignment < 5 Seconds
```

---

# Related Documents

Screen_Catalog.md

Screen_Flows.md

Mobile_App_Screens.md

Role_Permission_Management.md

Customer_Management.md

Agent_Management.md

Service_Request.md

Revenue_Management.md