# PropertyPilot Agent Payout Engine

## Version

1.0

---

# Objective

The Agent Payout Engine determines how much an agent receives for completing a service.

IMPORTANT:

Customer Pricing and Agent Payout are completely independent systems.

Customer Price ≠ Agent Earnings

PropertyPilot may charge one amount to the customer and pay a different amount to the agent.

---

# Agent Payout Formula

Agent Payout =

Travel Reimbursement
+
Food Allowance
+
Service Payout
+
Incentives
-
Penalties

---

# Travel Reimbursement

Travel reimbursement is independent from customer travel charges.

Formula:

Round Trip Distance × Reimbursement Rate

Example:

Distance = 60 KM

Agent Reimbursement Rate = ₹3/KM

Travel Reimbursement = ₹180

Admin configurable.

---

# Travel Reimbursement Configuration

Default:

₹3/KM

Future:

Can be configured by:

Cluster

Agent Grade

Vehicle Type

Service Type

---

# Agent Food Allowance

Food allowance is independent from customer food allowance.

Food Slabs

0 - 20 KM

₹0

20 - 30 KM

₹75

30 - 50 KM

₹100

50 - 70 KM

₹150

More Than 70 KM

₹250

Admin configurable.

---

# Service Payout Master

Property Verification

₹300

Site Visit

₹300

Plot Monitoring

₹250

Rental Inspection

₹400

Construction Inspection

₹500

Live Video Inspection

₹500

Drone Survey Coordination

₹700

Agriculture Land Inspection

₹400

Commercial Property Inspection

₹500

Apartment Inspection

₹350

Admin configurable.

---

# Incentives

Future Features

5 Star Rating Bonus

Urgent Job Bonus

Weekend Bonus

Festival Bonus

Monthly Target Bonus

Referral Bonus

Cluster Performance Bonus

All values configurable.

---

# Penalties

Future Features

No Show

Fake Visit

Incomplete Report

Missing Photos

Poor Quality Report

Customer Complaints

Policy Violations

Admin configurable.

---

# Payout Eligibility

Agent becomes eligible for payment only after:

Service Completed

Photos Uploaded

Required Documents Uploaded

Report Submitted

Verification Completed

Admin Approval

---

# Workflow

ASSIGNED

↓

IN_PROGRESS

↓

COMPLETED

↓

VERIFIED

↓

APPROVED

↓

PAYMENT_ELIGIBLE

↓

PAID

---

# Payout Status

PENDING

UNDER_REVIEW

APPROVED

PAID

REJECTED

ON_HOLD

---

# Payout Cycle

Supported

Daily

Weekly

Bi-Weekly

Monthly

Recommended:

Weekly Settlement

Every Monday

Admin configurable.

---

# Payment Methods

UPI

Bank Transfer

IMPS

NEFT

Future:

Agent Wallet

---

# Agent Wallet

Fields

Pending Amount

Approved Amount

Paid Amount

Rejected Amount

Adjustments

Lifetime Earnings

---

# Reports

Daily Payout Report

Weekly Payout Report

Monthly Payout Report

Agent Earnings Report

Cluster Wise Payout Report

Service Wise Payout Report

Pending Settlement Report

---

# Audit Requirements

Track:

Who Changed Payout

Old Value

New Value

Date

Time

Reason

No payout changes without audit logging.

---

# Future Enhancements

Performance Based Payouts

Cluster Based Payouts

Surge Payouts

Dynamic Incentives

Vehicle Based Reimbursement

AI Assisted Agent Scoring
# Agent Payout Engine

The system shall support separate payout calculations for agents.

Customer pricing and agent payouts shall be independent.

# ETA Independence

Agent payouts are independent of ETA.

Changing ETA shall not automatically change payouts.
# Agent Grades

Bronze

Silver

Gold

Platinum

Future payout rules may vary based on agent grade.
# Payout Recipient Types

AGENT

VENDOR

PARTNER

Future Support
# Payout Approval Authority

Agent Payouts may be approved by:

Cluster Manager

Operations Team

Admin

Based on configurable rules.

# Hold Reasons

Missing Evidence

Missing GPS Validation

Customer Complaint

Quality Review Pending

Fraud Investigation

Manual Audit

Payment Verification Pending
# Adjustments

The system shall support:

Positive Adjustment

Negative Adjustment

Recovery

Manual Correction

Incentive Adjustment

Penalty Adjustment

All adjustments shall be audit logged.
# Service Workflow Integration

Integrates With:

Service_Workflow.md

Supports:

Completion Validation

Approval Workflow

Payout Eligibility
# Review & Rating Integration

Integrates With:

Review_Rating_Management.md

Supports:

Performance Incentives

Rating Based Bonuses
# Vendor Management Integration

Integrates With:

Vendor_Management.md

Supports:

Future Vendor Payouts
# Business Rules

1. Customer pricing and agent payout shall be independent.

2. Payout eligibility requires service completion.

3. Payouts shall be auditable.

4. Incentives and penalties shall be configurable.

5. Payout rules shall not require code deployment.

6. Payment status changes shall be audit logged.

7. Future vendor payouts shall use the same framework.

8. ETA calculations shall not directly impact payouts.

9. Payout calculations shall support future grade-based rules.

10. Agent earnings history shall be permanently maintained.