# PropertyPilot Service Workflow

## Version

1.0

---

# Purpose

The Service Workflow Engine defines the complete lifecycle of a service request.

The workflow controls:

- Booking
- Validation
- Pricing
- Payment
- Assignment
- Service Execution
- Reporting
- Completion
- Feedback
- Payout Processing

The workflow shall provide complete visibility to:

- Customers
- Agents
- Cluster Managers
- Administrators

---
# Service Request Types

PropertyPilot supports multiple service request categories.

Examples:

Verification Services

- Property Verification
- Plot Verification
- Rental Verification

Monitoring Services

- Agriculture Monitoring
- Construction Monitoring

Inspection Services

- Site Inspection
- Drone Inspection

Execution Services

- Compound Wall
- Borewell
- Farm Development
- Guest House Construction

Each service type may follow the standard workflow with configurable variations.
---


# Workflow Overview

Customer Creates Request

↓

Coverage Validation

↓

Pricing Calculation

↓

Payment

↓

Service Request Creation

↓

ETA Calculation

↓

Agent Assignment

↓

Agent Acceptance

↓

Service Execution

↓

Report Submission

↓

Quality Review

↓

Service Completion

↓

Customer Feedback

↓

Agent Payout Processing

---

# Workflow Statuses

NEW

PENDING_PAYMENT

PAYMENT_COMPLETED

PENDING_ASSIGNMENT

ASSIGNED

ACCEPTED

IN_PROGRESS

REPORT_SUBMITTED

UNDER_REVIEW

COMPLETED

CANCELLED

FAILED

ESCALATED

---

# Stage 1 - Service Booking

Customer selects:

- Property
- Service
- Add-ons
- Preferred Date
- Property Location

System validates:

- Coverage Area
- Service Availability
- Property Eligibility

---

# Stage 2 - Coverage Validation

System identifies:

- State
- District
- Coverage Zone
- Cluster

Validation Checks:

Coverage Available

Service Available

Cluster Available

If validation fails:

Status:

FAILED

Reason recorded.

---

# Stage 3 - Pricing Calculation

System calculates:

Service Charge

+

Travel Cost

+

Food Allowance

+

Add-ons

↓

Subtotal

+

Platform Fee

+

Admin Overhead

+

Profit Margin

↓

Final Customer Price

Status:

PENDING_PAYMENT

---

# Stage 4 - Payment

Customer completes payment.

Success:

PAYMENT_COMPLETED

Failure:

PENDING_PAYMENT

Retry allowed.

---

# Stage 5 - ETA Calculation

System calculates:

Expected Start Date

Expected Completion Date

Based on:

- Distance
- Coverage Rules
- ETA Rules
- Service Type
- Add-ons

---

# Stage 6 - Assignment

System identifies:

Coverage Zone

↓

Cluster

↓

Eligible Agents

↓

Best Agent

Assignment Criteria:

- Availability
- Distance
- Grade
- Rating
- Workload

Status:

ASSIGNED

---

# Stage 7 - Agent Acceptance

Agent may:

ACCEPT

REJECT

NO RESPONSE

---

# Acceptance

Status:

ACCEPTED

---

# Rejection

System reassigns.

---

# No Response

Auto escalation.

---

# Stage 8 - Service Execution

Agent starts work.

Status:

IN_PROGRESS

Agent captures:

- Photos
- Videos
- Notes
- GPS Coordinates

---

# GPS Validation

System records:

Latitude

Longitude

Timestamp

Device Information

Used for service authenticity.

---

# Media Upload

Agent uploads:

Photos

Videos

Documents

Voice Notes (Future)

Drone Footage (Future)

---

# Stage 9 - Service Summary Creation

Agent submits:

Observations

Findings

Remarks

Recommendations

Status:

REPORT_SUBMITTED

---

# Stage 10 - AI Analysis

Optional

System may generate:

AI Summary

Risk Assessment

Property Insights

Recommendations

AI output is supplementary only.

---

# Stage 11 - Quality Review

Review performed by:

Cluster Manager

or

Operations Team

Review Checks:

Photo Quality

GPS Validity

Report Completeness

Service Compliance

Status:

UNDER_REVIEW

---

# Review Result

APPROVED

or

REJECTED

---

# Rejected Reports

Returned to agent.

Corrections required.

---
# Rework Workflow

When a report is rejected:

Agent receives correction request.

↓

Corrections Submitted

↓

Review Reopened

↓

Quality Review

↓

Approved or Rejected Again

All rework cycles shall be audit logged.
---

# Stage 12 - Service Completion

Approved report.

Status:

COMPLETED

Customer receives:

Service Summary

Photos

Videos

Final Report

AI Insights

---

# Stage 13 - Customer Feedback

Customer may provide:

Rating

Review

Complaint

Suggestions

Rating Scale:

1 to 5

Stars

---

# Stage 14 - Agent Payout Processing

After completion:

System calculates:

Travel Reimbursement

+

Food Allowance

+

Service Payout

+

Incentives

-

Penalties

↓

Final Agent Payout

---

# Cancellation Workflow

Cancellation Before Assignment

Allowed

Partial Refund Rules Configurable

---

# Cancellation After Assignment

Admin Review Required

Cancellation Charges May Apply

---

# Escalation Workflow

Escalation Triggers:

Assignment Delay

Service Delay

Customer Complaint

Poor Quality Report

SLA Breach

---

# Escalation Levels

Level 1

Assigned Agent

---

Level 2

Cluster Manager

---

Level 3

Operations Team

---

Level 4

Admin Team

---

# SLA Monitoring

Track:

Expected Start Date

Actual Start Date

Expected Completion Date

Actual Completion Date

Report Submission Time

Review Time

---

# Notification Triggers

Customer

- Booking Confirmed
- Payment Success
- Agent Assigned
- Service Started
- Report Ready
- Service Completed

---

Agent

- New Assignment
- Reminder
- Escalation Warning
- Payout Processed

---

Cluster Manager

- Assignment Failure
- SLA Breach
- Report Review Required

---

Admin

- High Delay Cases
- Cluster Overload
- Escalations

---

# Workflow Audit

Track:

Status Changes

Assignment Changes

Payment Events

Report Changes

Review Decisions

Escalations

Audit Fields:

User

Timestamp

Old Value

New Value

Reason

---

# Business Rules

1. Every request must pass coverage validation.

2. Every request must have ETA calculation.

3. Every request must belong to a cluster.

4. Every assignment must have an eligible agent.

5. Every completed service must have a report.

6. Every report must contain GPS information.

7. AI analysis is optional.

8. Agent payout occurs only after completion.

9. All workflow rules shall be admin configurable.

10. Workflow changes shall not require code deployment.

---

# Future Enhancements

Live Agent Tracking

Real-Time Customer Updates

Video Verification

Drone Automation

AI Assignment Engine

AI Quality Review

WhatsApp Workflow Updates

Voice Report Generation

Multilingual Reports

Predictive SLA Monitoring
# Execution Service Workflow

Applicable For:

- Compound Wall
- Farm House
- Guest House
- Borewell
- Farm Development

Workflow:

Booking

↓

Assessment

↓

Quotation Request

↓

Vendor Quotation Submission

↓

Quotation Approval

↓

Vendor Assignment

↓

Execution

↓

Progress Monitoring

↓

Completion

↓

Customer Confirmation

↓

Vendor Settlement
