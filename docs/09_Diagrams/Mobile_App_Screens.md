# PropertyPilot Mobile App Screens

## Version

1.0

---

# Purpose

This document defines detailed specifications for all mobile application screens.

Objectives:

- Support UI/UX Design
- Support Mobile Development
- Support API Integration
- Support Testing
- Define Navigation Behavior

---

# Mobile Applications

## Customer Mobile App

Users:

```text
Property Owners
Investors
NRI Customers
```

---

## Agent Mobile App

Users:

```text
Field Agents
Survey Agents
Drone Operators
```

---

# Screen Specification Template

Each screen contains:

```text
Screen ID
Screen Name
Purpose
User Roles
UI Components
Actions
Validations
APIs
Navigation
```

---

# CUSTOMER MOBILE APP

---

# SCR-CUST-001

## Login Screen

### Purpose

Authenticate customer.

---

### Components

```text
Mobile Number

Password

Login Button

Forgot Password

Register Link
```

---

### Actions

```text
Login

Navigate to Registration

Navigate to Forgot Password
```

---

### APIs

```text
POST /auth/login
```

---

### Navigation

```text
Success → Dashboard

Failure → Error Message
```

---

# SCR-CUST-002

## Registration Screen

### Components

```text
Name

Mobile Number

Email

Password

Register Button
```

---

### APIs

```text
POST /customers/register
```

---

# SCR-CUST-005

## Customer Dashboard

### Purpose

Provide summary of customer account.

---

### Components

```text
Property Summary Card

Active Services

Recent Reports

Notifications

Quick Actions
```

---

### Quick Actions

```text
Add Property

Book Service

View Reports

Renew Subscription
```

---

### APIs

```text
GET /dashboard
```

---

# SCR-CUST-008

## Property List

### Components

```text
Property Cards

Search

Filter

Add Property Button
```

---

### Actions

```text
View Property

Add Property

Edit Property
```

---

### APIs

```text
GET /properties
```

---

# SCR-CUST-009

## Property Details

### Components

```text
Property Information

Documents

Service History

Reports

Subscription Status
```

---

### Actions

```text
Book Service

Upload Document

View Reports
```

---

### APIs

```text
GET /properties/{id}
```

---

# SCR-CUST-010

## Add Property

### Components

```text
Property Name

Property Type

Survey Number

Address

Coordinates

Upload Documents
```

---

### Validation

```text
Property Name Mandatory

Property Type Mandatory
```

---

### APIs

```text
POST /properties
```

---

# SCR-CUST-013

## Service Catalog

### Components

```text
Service Categories

Search

Filters

Popular Services
```

---

### Actions

```text
View Service

Book Service
```

---

### APIs

```text
GET /services
```

---

# SCR-CUST-015

## Book Service

### Components

```text
Selected Service

Property Selection

Schedule Date

Notes

Checkout Button
```

---

### APIs

```text
POST /service-requests
```

---

# SCR-CUST-017

## Service Tracking

### Components

```text
Current Status

Assigned Agent

Timeline

Expected Completion
```

---

### Statuses

```text
Requested

Assigned

In Progress

Review

Completed
```

---

### APIs

```text
GET /service-requests/{id}
```

---

# SCR-CUST-018

## Reports Dashboard

### Components

```text
Report List

Search

Filter

Download
```

---

### APIs

```text
GET /reports
```

---

# SCR-CUST-019

## Report Details

### Components

```text
Report Summary

Evidence

Recommendations

Download PDF
```

---

### APIs

```text
GET /reports/{id}
```

---

# SCR-CUST-021

## Evidence Viewer

### Components

```text
Photos

Videos

GPS Location

Visit Details
```

---

### APIs

```text
GET /evidence
```

---

# SCR-CUST-022

## Subscription Plans

### Components

```text
Silver

Gold

Platinum

NRI Plans
```

---

### Actions

```text
Subscribe

Compare Plans
```

---

### APIs

```text
GET /subscription-plans
```

---

# SCR-CUST-025

## Checkout Screen

### Components

```text
Order Summary

Coupon Code

Payment Method

Pay Button
```

---

### APIs

```text
POST /payments
```

---

# SCR-CUST-029

## Support Center

### Components

```text
Complaints

FAQ

Contact Support

Tickets
```

---

### APIs

```text
GET /support
```

---

# AGENT MOBILE APP

---

# SCR-AGT-003

## Agent Dashboard

### Components

```text
Assigned Tasks

Today's Visits

Pending Reports

Performance Metrics
```

---

### APIs

```text
GET /agent/dashboard
```

---

# SCR-AGT-004

## Task List

### Components

```text
Pending Tasks

In Progress

Completed
```

---

### APIs

```text
GET /agent/tasks
```

---

# SCR-AGT-005

## Task Details

### Components

```text
Customer

Property

Service

Instructions

Location
```

---

### Actions

```text
Accept Task

Start Visit
```

---

### APIs

```text
GET /agent/tasks/{id}
```

---

# SCR-AGT-006

## Start Visit

### Components

```text
Start Button

GPS Validation

Visit Checklist
```

---

### Validation

```text
GPS Required
```

---

# SCR-AGT-007

## GPS Capture

### Components

```text
Latitude

Longitude

Accuracy

Capture Button
```

---

### APIs

```text
POST /gps-captures
```

---

# SCR-AGT-008

## Photo Capture

### Components

```text
Camera

Photo Gallery

Upload
```

---

### Requirements

```text
Geo Tagged

Timestamped
```

---

### APIs

```text
POST /media/photos
```

---

# SCR-AGT-009

## Video Capture

### Components

```text
Video Camera

Record Button

Upload
```

---

### APIs

```text
POST /media/videos
```

---

# SCR-AGT-010

## Observation Entry

### Components

```text
Observations

Risk Level

Comments
```

---

### APIs

```text
POST /observations
```

---

# SCR-AGT-011

## Submit Evidence

### Components

```text
Photos

Videos

GPS

Notes
```

---

### Validation

```text
Mandatory Evidence Check
```

---

### APIs

```text
POST /evidence
```

---

# SCR-AGT-012

## Visit Completion

### Components

```text
Visit Summary

Evidence Summary

Submit Button
```

---

### APIs

```text
POST /visits/complete
```

---

# SCR-AGT-013

## Report Drafting

### Components

```text
Findings

Recommendations

Attachments
```

---

### APIs

```text
POST /reports/draft
```

---

# Mobile App Design Principles

```text
Mobile First

Offline Capability

Minimal Data Entry

Fast Navigation

GPS First

Evidence First
```

---

# Mobile Security Controls

```text
Biometric Login

OTP Authentication

Session Timeout

Encrypted Storage

Device Registration
```

---

# Mobile Performance Targets

```text
Screen Load < 2 Seconds

Photo Upload < 5 Seconds

Dashboard Load < 3 Seconds
```

---

# Related Documents

Screen_Catalog.md

Screen_Flows.md

Customer_Journeys.md

UI_UX_Architecture.md

Property_API.md

Customer_API.md

Service_Catalog.md