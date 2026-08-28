# PropertyPilot Quotation Management

## Version

1.0

---

# Purpose

The Quotation Management module enables PropertyPilot to generate, manage, compare, approve, and track quotations for execution-based services.

The module supports customer requests, vendor quotations, quotation revisions, approvals, negotiations, and execution workflows.

This module primarily supports:

- Compound Wall Services
- Precast Wall Services
- Fencing Services
- Borewell Services
- Farm Development
- Guest House Construction
- Farm House Construction
- Land Levelling
- Site Development Services

---

# Objectives

The Quotation Management module shall:

- Support quotation requests
- Support multiple vendor quotations
- Support quotation comparison
- Support quotation approval workflows
- Support quotation revisions
- Support negotiation tracking
- Support execution service workflows
- Support future BOQ management

---

# Quotation Overview

Customer Requirement

↓

Site Assessment

↓

Quotation Request

↓

Vendor Quotations

↓

Comparison

↓

Customer Review

↓

Approval

↓

Vendor Assignment

↓

Execution

---

# Supported Services

## Construction Services

- Compound Wall
- Precast Wall
- Guest House
- Farm House

---

## Site Development Services

- Land Levelling
- Site Cleaning
- Fencing

---

## Agriculture Services

- Borewell
- Farm Development
- Irrigation Support

---

# Quotation Request

Quotation requests may originate from:

Customer Request

Marketplace Lead

Property Assessment

Agent Recommendation

Vendor Recommendation

---

# Quotation Status

DRAFT

REQUESTED

RECEIVED

UNDER_REVIEW

NEGOTIATION

APPROVED

REJECTED

EXPIRED

CANCELLED

CONVERTED_TO_PROJECT

---

# Quotation Information

Quotation ID

Request ID

Customer

Property

Service

Vendor

Quotation Date

Expiry Date

Status

Total Amount

Currency

Remarks

---

# Site Assessment

A site assessment may be required before quotation generation.

Assessment may include:

Property Visit

Measurements

Photos

Videos

GPS Validation

Requirement Gathering

Site Constraints

---

# Vendor Quotation Submission

Vendor may provide:

Material Cost

Labor Cost

Equipment Cost

Transportation Cost

Additional Charges

Estimated Duration

Terms & Conditions

Remarks

---

# Quotation Components

## Material Cost

Examples:

- Cement
- Sand
- Bricks
- Steel
- Precast Materials

---

## Labor Cost

Examples:

- Mason
- Helpers
- Skilled Labor

---

## Equipment Cost

Examples:

- Excavator
- Tractor
- Drilling Equipment

---

## Transportation Cost

Examples:

- Material Transport
- Equipment Transport

---

## Miscellaneous Cost

Examples:

- Permissions
- Local Charges
- Contingencies

---

# Cost Summary

Quotation shall calculate:

Material Cost

+

Labor Cost

+

Equipment Cost

+

Transportation Cost

+

Miscellaneous Cost

=

Total Cost

---

# Quotation Comparison

Customer may compare:

Vendor

Price

Duration

Rating

Verification Status

Experience

Past Projects

---

# Vendor Comparison Score

Future Support

Factors:

Price

Quality Rating

Completion History

Response Time

Customer Feedback

---

# Negotiation Management

Support:

Customer Negotiation

Vendor Revision

Admin Assisted Negotiation

PropertyPilot Mediation

---

# Revision Management

Vendor may:

Revise Quotation

Update Costs

Change Timeline

Add Notes

---

Track:

Revision Number

Revision Date

Revision Reason

---

# Quotation Approval Workflow

Quotation Submitted

↓

Customer Review

↓

Approval

↓

Vendor Confirmation

↓

Project Creation

---

# Rejection Workflow

Quotation Submitted

↓

Customer Rejects

↓

Reason Captured

↓

Vendor Notified

---

# Project Conversion

Approved quotations may create:

Execution Project

Vendor Assignment

Work Schedule

Project Tracking

---

# BOQ Support (Future)

BOQ = Bill Of Quantities

Support:

Material Lists

Quantity Estimates

Cost Estimates

Work Breakdown

---

# Milestone Support

Future Support

Examples:

Foundation

↓

Wall Construction

↓

Roofing

↓

Finishing

↓

Completion

---

Each milestone may have:

Start Date

End Date

Cost

Status

Evidence

---

# Customer Dashboard

Customers may view:

Quotation Requests

Vendor Quotations

Comparisons

Approvals

Project Status

History

---

# Vendor Dashboard

Vendor may view:

Quotation Requests

Submitted Quotations

Revisions

Approvals

Assigned Projects

---

# Admin Dashboard

Admin may view:

Quotation Volume

Vendor Performance

Approval Rates

Conversion Rates

Revenue Analytics

---

# Notifications

Notify Customer:

Quotation Received

Quotation Updated

Quotation Expiring

Approval Required

Project Created

---

Notify Vendor:

Quotation Request

Quotation Approved

Quotation Rejected

Revision Requested

Project Assigned

---

# Marketplace Integration

Integrates With:

Marketplace_Management.md

Supports:

Buyer Requests

Seller Requests

Property Development Services

Marketplace Leads

---

# Vendor Integration

Integrates With:

Vendor_Management.md

Supports:

Vendor Selection

Vendor Comparison

Vendor Assignment

Vendor Performance

---

# Security Integration

Integrates With:

Security_Design.md

Supports:

Access Control

Quotation Privacy

Audit Logging

Vendor Data Protection

---

# Audit Requirements

Track:

Quotation Creation

Quotation Revision

Approval

Rejection

Negotiation

Project Conversion

Vendor Assignment

---

Audit Fields

User

Timestamp

Action

Old Value

New Value

Reason

---

# Analytics

Track:

Quotation Requests

Average Quotation Value

Approval Rate

Vendor Response Rate

Project Conversion Rate

Vendor Success Rate

Revenue Contribution

---

# Admin Configuration

Admin shall configure:

Quotation Templates

Approval Rules

Negotiation Rules

Expiry Rules

Comparison Rules

Vendor Selection Rules

Project Conversion Rules

No code deployment required.

---

# Future Enhancements

AI Cost Estimation

AI Vendor Recommendations

AI BOQ Generation

AI Project Forecasting

Smart Vendor Comparison

Digital Contract Generation

Milestone Billing

Multi-Vendor Quotations

---

# Business Rules

1. Quotations shall support multiple vendors.

2. Quotations shall support revisions.

3. Quotations shall support comparison.

4. Approved quotations may create projects.

5. Quotation activities shall be audit logged.

6. Vendor information shall be protected.

7. Quotation approval workflows shall be configurable.

8. Quotation Management shall integrate with Vendor Management.

9. Quotation Management shall integrate with Marketplace.

10. Quotation configuration shall not require code deployment.
# Approval Levels

Auto Approval

Customer Approval

Admin Approval

Dual Approval

Examples:

Quotation < ₹50,000
Customer Approval

Quotation > ₹5,00,000
Customer + Admin Approval