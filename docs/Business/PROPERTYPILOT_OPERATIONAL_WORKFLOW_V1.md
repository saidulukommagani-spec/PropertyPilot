Excellent. This document is actually more important than another sprint of coding.
The Business Blueprint defines what PropertyPilot is.
The Operational Workflow defines how PropertyPilot works.
PROPERTYPILOT_OPERATIONAL_WORKFLOW_V1
Version: 1.0
Status: Approved Baseline
Owner: PropertyPilot
1. Operational Philosophy
PropertyPilot operates as a property-centric service platform.
Every activity originates from one of:
Property
Service Request
Subscription
Lead
Deal

All workflows must ultimately relate to a property.
2. Core Operational Entities
Customer
A person or organization using PropertyPilot services.
Customer Types
Individual
Investor
NRI
Tenant
Buyer
Seller
Builder
Commercial Organization

Property
Central entity of PropertyPilot.
A property may have:
Multiple Service Requests
Multiple Subscriptions
Multiple Leads
Multiple Deals
Multiple Visits
Multiple Documents
Multiple Evidence Records

Service Request
Represents a property need.
Examples:
Legal Verification
EC
Survey
Cleaning
Monitoring
Documentation
Construction Inspection

Subscription
Recurring service plan.
Provides:
Benefits
Discounts
Monitoring
Priority Support
Bundled Services

Lead
Represents a potential opportunity.
Examples:
Buy Property
Sell Property
Investment Opportunity
Partnership Opportunity

Deal
Represents an active transaction process.
Examples:
Purchase
Sale
Transfer

3. Customer Lifecycle
New Customer
Register
    ↓
Verify Contact Details
    ↓
Create Profile
    ↓
Create Property

Existing Customer
Login
    ↓
Dashboard
    ↓
Manage Property Activities

4. Property Lifecycle
Property Registration
Customer
    ↓
Add Property
    ↓
Property Verification
    ↓
Property Activated

Property Information
Property Type
Location
Area
Ownership
Documents
Coordinates

Property Statuses
DRAFT
ACTIVE
UNDER_VERIFICATION
INACTIVE
ARCHIVED

5. Service Request Workflow
Service Creation
Customer
    ↓
Select Property
    ↓
Select Service
    ↓
Price Calculation
    ↓
Submit Request

Request Lifecycle
DRAFT
    ↓
SUBMITTED
    ↓
PENDING_ASSIGNMENT
    ↓
ASSIGNED
    ↓
ACCEPTED
    ↓
IN_PROGRESS
    ↓
VISIT_COMPLETED
    ↓
EVIDENCE_REVIEW
    ↓
COMPLETED

Alternate Statuses
REJECTED
CANCELLED
FAILED
ON_HOLD

6. Assignment Workflow
Admin Assignment
Service Request
    ↓
Available Agent Search
    ↓
Agent Assignment

Agent Response
Assigned
    ↓
Accept
or
Reject

Accepted
Assignment Accepted
    ↓
Visit Planning

Rejected
Assignment Rejected
    ↓
Reassignment

7. Visit Workflow
Visit Creation
Assignment
    ↓
Schedule Visit

Visit Lifecycle
SCHEDULED
    ↓
STARTED
    ↓
ON_SITE
    ↓
EVIDENCE_COLLECTION
    ↓
COMPLETED

Visit Data
Date
Time
Location
Agent
Property
Service Request
Notes

8. Evidence Workflow
Evidence is mandatory.
No service completion without evidence.
Evidence Types
Media
Photo
Video
Audio

Documents
PDF
Certificates
Reports
Invoices

Verification
GPS Coordinates
Timestamp
Geo Tag

Evidence Flow
Visit
    ↓
Upload Evidence
    ↓
Validation
    ↓
Approval

9. Service Completion Workflow
Visit Completed
    ↓
Evidence Submitted
    ↓
Evidence Validation
    ↓
Service Closure

Validation Result
Passed
COMPLETED

Failed
Additional Visit Required

10. Subscription Workflow
Subscription Purchase
Customer
    ↓
Select Plan
    ↓
Payment
    ↓
Activation

Subscription Lifecycle
ACTIVE
    ↓
CONSUMPTION
    ↓
RENEWAL

Expiry Flow
ACTIVE
    ↓
EXPIRING
    ↓
EXPIRED

11. Monitoring Workflow
One of PropertyPilot's flagship offerings.
Monitoring Enrollment
Property
    ↓
Monitoring Plan
    ↓
Schedule Creation

Monitoring Execution
Schedule
    ↓
Visit
    ↓
Evidence
    ↓
Report

Monitoring Deliverables
Photos
Videos
Inspection Notes
Observations
Recommendations

12. Lead Workflow
Lead Sources
Customer Generated
Customer
    ↓
Raise Lead

Admin Generated
Admin
    ↓
Create Lead

Service Generated
Service
    ↓
Opportunity Identified
    ↓
Lead Created

Lead Lifecycle
NEW
    ↓
QUALIFIED
    ↓
ASSESSMENT
    ↓
SERVICE_REQUIRED
    ↓
READY_FOR_DEAL

Alternate Outcomes
CONVERTED_TO_DEAL
CLOSED
LOST

13. Deal Workflow
Deal Creation
Lead
    ↓
Deal Created

Deal Participants
Buyer
Seller
Property

Deal Lifecycle
INITIATED
    ↓
DOCUMENT_VERIFICATION
    ↓
NEGOTIATION
    ↓
AGREEMENT
    ↓
PAYMENT_PROCESS
    ↓
CLOSURE

Deal Outcomes
COMPLETED
CANCELLED
FAILED

14. Revenue Workflow
Service Revenue
Service Request
    ↓
Invoice
    ↓
Payment

Subscription Revenue
Plan Purchase
    ↓
Payment

Deal Revenue
Deal Closure
    ↓
Success Fee

15. Customer Dashboard Workflow
Customer should see:
My Properties
My Services
My Subscriptions
My Leads
My Deals
My Documents
My Reports
Invoices
Payments

16. Agent Workflow
Agent Portal
Assignments
Visits
Evidence Upload
Reports
Performance

Daily Agent Flow
Login
    ↓
Assignments
    ↓
Visits
    ↓
Evidence Upload
    ↓
Complete Work

17. Admin Workflow
Admin Portal
Dashboard
Customers
Properties
Services
Service Requests
Assignments
Visits
Evidence
Subscriptions
Leads
Deals
Pricing
Invoices
Payments
Reports

18. Operational Rules
Rule 1
Every service request must belong to a property.
Rule 2
Every visit belongs to a service request.
Rule 3
Every evidence record belongs to a visit.
Rule 4
Service completion requires evidence validation.
Rule 5
Lead may generate services.
Rule 6
Lead may convert to deal.
Rule 7
Deal may generate services.
Example:
Deal
   ↓
Legal Verification
Survey
Documentation

Rule 8
Subscriptions provide benefits but do not replace services.
Rule 9
PropertyPilot owns customer relationship.
Rule 10
All pricing must be dynamic and configurable.
19. Enterprise Success Flow
Customer
    ↓
Property
    ↓
Service Request
    ↓
Visit
    ↓
Evidence
    ↓
Completion
    ↓
Lead (Optional)
    ↓
Deal (Optional)
    ↓
Subscription Renewal