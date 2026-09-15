# PropertyPilot Customer Journeys

## Version

1.0

---

# Purpose

This document defines the end-to-end customer journeys supported by PropertyPilot.

Objectives:

- Understand customer experience
- Identify required screens
- Define notifications
- Define integrations
- Validate feature completeness

---
# Journey Classification

Customer Acquisition Journeys

Service Delivery Journeys

Subscription Journeys

Marketplace Journeys

Operational Journeys

Vendor Journeys

Agent Journeys

Administrative Journeys

---
# Journey 0: Check Your Property Service Cost
### Outputs

Customer can view:

- One-Time Service Cost
- Service Bundle Cost
- Subscription Cost
- Estimated Savings

Customer may continue as Guest.

Registration is required only before purchase.

## Goal

Visitor estimates service cost before registration.

## Flow

Home Page

    ↓

Check Your Property Service Cost

    ↓

Select Property Type

    ↓

Enter Location

    ↓

Select Service

    ↓

Estimate Generated

    ↓

Compare

        One-Time Cost
        Package Cost
        Subscription Cost

    ↓

Register / Continue
---
# Journey: Compare Plans

Customer

    ↓

Select Property

    ↓

Compare Silver / Gold / Platinum / NRI

    ↓

View Included Services

    ↓

View Savings

    ↓

Purchase Plan
---
# Journey: Buy Property

Search Property

    ↓

Shortlist Property

    ↓

Request Details

    ↓

PropertyPilot Review

    ↓

Site Visit Coordination

    ↓

Verification Services

    ↓

Offer Submission

    ↓

Negotiation Coordination

    ↓

Transaction Support
---
# Journey: Sell Property

Register Property

    ↓

Property Verification

    ↓

Property Listing

    ↓

Lead Generation

    ↓

Buyer Matching

    ↓

Property Visits

    ↓

Negotiation

    ↓

Transaction Completion
---
# Journey: Rent Property

Register Property

    ↓

Rental Listing

    ↓

Tenant Search

    ↓

Tenant Verification

    ↓

Rental Agreement

    ↓

Move-In Inspection

    ↓

Rental Management
---
# Journey: Construction Service

Select Property

    ↓

Request Construction

    ↓

Scope Definition

    ↓

Vendor Evaluation

    ↓

Quotation

    ↓

Approval

    ↓

Execution

    ↓

Monitoring

    ↓

Completion
---
# Journey: Plot Cleaning

Select Property

    ↓

Request Cleaning

    ↓

Price Calculation

    ↓

Payment

    ↓

Vendor Assignment

    ↓

Execution

    ↓

Photos Uploaded

    ↓

Completion Report
---
# Journey: Protected Buyer-Seller Interaction

Buyer Requests Property

    ↓

PropertyPilot Reviews Request

    ↓

Seller Notification

    ↓

PropertyPilot Coordinates

    ↓

Buyer-Seller Interaction Managed

    ↓

Transaction Completion
### Lead Protection Rules

Buyer contact information is hidden from Seller.

Seller contact information is hidden from Buyer.

PropertyPilot acts as intermediary.

All communication flows through PropertyPilot.

Direct contact sharing is controlled by platform rules.

---
# Journey: Shared Property Ownership

Owner 1 Registers Property

    ↓

Invite Additional Owners

    ↓

Ownership Validation

    ↓

Access Permissions

    ↓

Shared Dashboard
---


# Journey 1: New Customer Registration

## Goal

Customer creates an account and accesses PropertyPilot services.

## Actors

- Customer
- Authentication Service

## Flow

```text
Open App
    ↓
Register
    ↓
Verify Mobile OTP
    ↓
Complete Profile
    ↓
Accept Terms
    ↓
Dashboard
```

### Notifications

```text
Welcome SMS

Welcome Email

WhatsApp Welcome Message
```

---

# Journey 2: Property Registration

## Goal

Customer registers a property.

## Actors

- Customer
- Property Service

## Flow

```text
Dashboard
    ↓
Add Property
    ↓
Select Property Type
    ↓
Enter Property Details
    ↓
Upload Documents
    ↓
Upload Photos
    ↓
Submit
    ↓
Property Created
```

### Required Information

```text
Property Name

Property Type

Location

Survey Number

Extent

Documents

Photos
```

### Output

```text
Property ID Generated
```

---

# Journey 3: GPS Verification

## Goal

Customer requests GPS verification.

## Flow

```text
Select Property
    ↓
Book GPS Verification
    ↓
Payment
    ↓
Agent Assignment
    ↓
Field Visit
    ↓
GPS Capture
    ↓
Report Generation
    ↓
Customer Notification
```

### Customer Status Tracking

```text
Requested

Assigned

In Progress

Completed
```

### Deliverables

```text
GPS Coordinates

Photos

Verification Report
```

---

# Journey 4: Ownership Verification

## Goal

Customer validates ownership details.

## Flow

```text
Select Property
    ↓
Book Verification
    ↓
Upload Documents
    ↓
Payment
    ↓
Verification Review
    ↓
Report Generation
    ↓
Completion
```

### Documents

```text
Sale Deed

Passbook

EC

Tax Receipts
```

### Deliverables

```text
Ownership Report

Risk Assessment

Recommendations
```

---

# Journey 5: Monitoring Subscription

## Goal

Customer subscribes to monitoring services.

## Flow

```text
Select Property
    ↓
Choose Plan
    ↓
Payment
    ↓
Subscription Activated
    ↓
Scheduled Visits
    ↓
Reports Generated
    ↓
Customer Notifications
```

### Plans

```text
Silver

Gold

Platinum NRI
```

### Deliverables

```text
Photos

GPS Logs

Reports

Alerts
```

---

# Journey 6: NRI Property Management

## Goal

NRI customer manages property remotely.

## Flow

Register
    ↓
Add Property
    ↓
Purchase NRI Plan
    ↓
Property Verification
    ↓
Monitoring Activation
    ↓
Periodic Updates
    ↓
Issue Management
---
# Journey: Upgrade to Subscription

Customer Uses One-Time Service

    ↓

System Calculates Annual Cost

    ↓

System Shows Savings

    ↓

Compare Plans

    ↓

Upgrade to Subscription

    ↓

Plan Activated
---


### Communication Channels

```text
WhatsApp

Email

Video Reports

Phone Support
```

---

# Journey 7: Monitoring Alert Handling

## Goal

Customer is informed about an issue.

## Example

```text
Boundary Damage

Illegal Occupation

Construction Activity

Trespassing
```

## Flow

Issue Detected

↓

Evidence Captured

↓

Customer Alert

↓

Recommended Services

↓

Customer Approval

↓

Service Request Created

↓

Vendor Assignment

↓

Work Completion

↓

Closure Report

### Deliverables

```text
Photos

Video

GPS Evidence

Recommendations
```

---

# Journey 8: Service Booking

## Goal

Customer books any available service.

## Flow

```text
Browse Services
    ↓
Select Service
    ↓
Choose Property
    ↓
Payment
    ↓
Confirmation
    ↓
Execution
    ↓
Report Delivery
```

---

# Journey 9: Agent Workflow

## Goal

Field agent executes assigned tasks.

## Flow

```text
Receive Assignment
    ↓
Accept Task
    ↓
Navigate to Property
    ↓
Capture GPS
    ↓
Capture Photos
    ↓
Submit Report
    ↓
Complete Task
```

### Mobile Features

```text
GPS

Camera

Offline Mode

Digital Signature
```

---

# Journey 10: Vendor Service Execution

## Goal

Vendor completes customer-requested work.

## Flow

```text
Job Assignment
    ↓
Accept Work
    ↓
Execute Service
    ↓
Upload Evidence
    ↓
Completion Approval
    ↓
Payment
```

---

# Customer Touchpoints

```text
Mobile App

Web Portal

WhatsApp

Email

Phone Support
```

---

# Critical Notifications

## Customer

```text
Registration

Property Created

Payment Success

Agent Assigned

Report Ready

Subscription Renewal

Issue Alert
```

---

## Agent

```text
New Assignment

Task Reminder

Escalation Alert
```

---

## Vendor

```text
Job Assigned

Payment Released
```

---

# MVP Journeys

Customer Registration

Property Registration

Check Property Service Cost

GPS Verification

Ownership Verification

Service Booking

Monitoring Subscription

Agent Workflow

Payments

Notifications

Admin Operations

---

# Future Journeys

```text
Rental Management

Construction Monitoring

Vendor Marketplace

Property Valuation

AI-Based Risk Analysis
```

---
# Journey: Customer-Vendor Protected Interaction

Customer Requests Service

    ↓

PropertyPilot Reviews Request

    ↓

Vendor Assignment

    ↓

Service Coordination

    ↓

Completion

    ↓

Payment Settlement
---


# Related Documents

Feature_Catalog.md

Service_Catalog.md

Business_Process_Catalog.md

Pricing_Strategy.md

Subscription_Plans.md

Customer_Data_Model.md

Property_Data_Model.md

# Journey Ownership Matrix

| Journey | Customer | Agent | Vendor | Admin |
|----------|----------|--------|---------|---------|
| Registration | Yes | No | No | No |
| Property Registration | Yes | No | No | No |
| Verification | Yes | Yes | No | Yes |
| Monitoring | Yes | Yes | No | Yes |
| Service Booking | Yes | Yes | Yes | Yes |
| Construction | Yes | Yes | Yes | Yes |
| Marketplace | Yes | No | No | Yes |

---

# Newly Added Journeys

## Customer Journey: KYC Verification

```text
Registration or Profile
    ↓
Start KYC Verification
    ↓
Submit Identity Details and Documents
    ↓
Verification Review
    ↓
Approved / Exception Resolution
    ↓
Verified Customer Profile
```

## Customer Journey: Subscription Renewal and Plan Change

```text
My Subscription
    ↓
Renewal Reminder or Plan Change Request
    ↓
Review Plan Entitlements and Savings
    ↓
Pricing Calculation
    ↓
Checkout
    ↓
Payment Confirmation
    ↓
Updated Subscription
```

## Customer Journey: Service Cancellation and Refund

```text
Service Request Details
    ↓
Request Cancellation
    ↓
Cancellation and Usage Eligibility Review
    ↓
Cancellation Confirmed / Declined
    ↓
Refund Processing, When Eligible
    ↓
Customer Notification
```

## Customer Journey: Service Completion Feedback

```text
Service Completion Notification
    ↓
View Completion Evidence or Report
    ↓
Rate Service
    ↓
Submit Feedback
    ↓
Service Closure
```

## Agent Journey: Emergency Visit Execution

```text
Emergency Assignment
    ↓
Accept Task
    ↓
Navigate to Property
    ↓
Capture GPS and Urgent Evidence
    ↓
Submit Urgent Report
    ↓
Operations Review and Customer Alert
```

## Agent Journey: Offline Evidence Synchronisation

```text
Assigned Task
    ↓
Capture Required Evidence Offline
    ↓
Queue Evidence and Report Draft
    ↓
Reconnect
    ↓
Synchronise or Resolve Conflict
    ↓
Submit Evidence and Complete Task
```

## Vendor Journey: Registration and Approval

```text
Vendor Registration
    ↓
Submit Category, Service, and Verification Details
    ↓
Admin Verification
    ↓
Approval / Rework Request
    ↓
Vendor Activation
    ↓
Vendor Directory Availability
```

## Vendor Journey: Quotation Response and Selection

```text
Quotation Request
    ↓
Review Scope and Property Details
    ↓
Submit Itemised Quotation
    ↓
Customer Comparison
    ↓
Quotation Selection
    ↓
Vendor Assignment or Notification
```

## Admin Journey: Service and Pricing Configuration

```text
Admin Dashboard
    ↓
Service Management
    ↓
Configure Service Eligibility, Deliverables, and SLA
    ↓
Configure Pricing Rules and Add-ons
    ↓
Approval and Audit Logging
    ↓
Publish Service Configuration
```

## Admin Journey: Subscription Plan Configuration

```text
Admin Dashboard
    ↓
Subscription Management
    ↓
Create or Update Plan Entitlements
    ↓
Configure Visit Frequency, Benefits, and Eligibility
    ↓
Set Effective Dates and Pricing Rules
    ↓
Approval and Publish
```

## NRI Journey: Emergency Alert Response

```text
Emergency Alert Notification
    ↓
Review Video, Photo, and GPS Evidence
    ↓
Review Recommended Action
    ↓
Approve Service or Emergency Visit
    ↓
Track Remediation
    ↓
Receive Closure Report
```

## NRI Journey: Relationship Manager Coordination

```text
NRI Dashboard
    ↓
Open Relationship Manager Conversation
    ↓
Discuss Property Issue or Service Need
    ↓
Create or Update Service Request
    ↓
Receive Progress Updates
    ↓
Resolution Confirmation
```
