# PropertyPilot Business Context Document

## Business Identity

PropertyPilot is NOT a marketplace.

PropertyPilot is a property management and property services platform.

Customers interact only with PropertyPilot.

PropertyPilot owns:

- Customer relationship
- Pricing
- SLA
- Service delivery
- Service quality
- Evidence collection
- Reporting
- Billing

Vendors and agents are fulfillment partners.

Customers should never feel they are dealing with vendors.

---

## Core Business Model

Every operation in PropertyPilot is a Service.

Examples:

- Property inspection
- Property cleaning
- Document verification
- Encumbrance check
- Tax verification
- Property health check
- Occupancy inspection
- Boundary verification
- Property photography
- Rental management
- Tenant verification
- Legal review
- Site visit
- Water inspection
- Electricity inspection

Everything is a service.

Every service can be chargeable.

---

## Property Health Check

Property Health Check is a major offering.

Health check includes:

### Physical Checks

- Structure condition
- Boundary condition
- Occupancy status
- Encroachment signs
- Maintenance status

### Document Checks

- Sale deed verification
- Registration verification
- EC verification
- Tax verification
- Mutation verification
- Survey verification

Physical inspection and document verification can both exist independently.

---

## Visits Are Optional

Not every service requires a visit.

Examples:

### Requires Visit

- Property inspection
- Photography
- Cleaning
- Occupancy verification

### Does Not Require Visit

- Legal review
- EC verification
- Tax verification
- Document review
- Subscription services

Service configuration determines:

- requires_visit
- visit_mandatory
- max_visits_allowed
- requires_gps
- requires_before_photo
- requires_after_photo
- requires_signature
- requires_document_upload

Never hardcode visit assumptions.

---

## Evidence Driven Operations

Workflow:

Service Request
→ Visit(s)
→ Evidence
→ Validation
→ Completion

Evidence types:

- Before Photo
- After Photo
- Signature
- Document
- Video
- GPS Verification

Evidence requirements must be configurable.

---

## Customer Types

PropertyPilot supports:

### Existing Customers

Registered customers.

### External Customers

Leads not yet onboarded.

### Admin Created Customers

Created internally.

All can generate leads.

---

## Lead Management

Lead sources:

- Existing customer
- Referral
- Website
- Walk-in
- Agent
- Admin

Lead simply means potential future business.

---

## Deal Management

Deals are supported.

Examples:

- Property sale assistance
- Property purchase assistance
- Rental onboarding
- Property management contract

Deals can trigger Service Requests.

Lead
→ Deal
→ Service Requests

---

## Property Ownership

Property can have:

- Single owner
- Multiple owners

PropertyPilot manages:

- Property
- Owners
- Documents
- Services
- Visits
- Evidence
- History

Property is a core domain entity.

---

## Subscription Model

Subscriptions provide:

- Included services
- Discounts
- Priority service
- Bundled inspections

Subscriptions affect pricing and entitlement.

---

## Pricing Philosophy

Pricing must be admin configurable.

Support:

- Vendor Cost
- Travel Cost
- Agent DA
- Overhead
- Margin
- GST
- Cluster Discount
- ETA Discount
- Subscription Discount
- Coupon Discount
- Promotional Discount

No hardcoded pricing.

---

## Customer Pricing Transparency

Customer sees:

- Standard Price
- Customer Price
- Savings

Customer should not see internal operational cost breakdown.

---

## Agent Philosophy

Agents perform work.

Customers belong to PropertyPilot.

Agents:

- Accept assignment
- Visit property
- Upload evidence
- Complete visit

---

## Vendor Philosophy

Vendor remains invisible to customer.

PropertyPilot owns accountability.

---

## Scalability Goal

Support:

- Thousands of customers
- Thousands of properties
- Multiple cities
- Multiple service categories
- Multiple visits per request
- Dynamic pricing
- Dynamic evidence requirements

---

## Architectural Principle

Prefer Database Driven over Code Driven.

Examples:

- Pricing Rules
- Evidence Rules
- Service Rules
- Validation Rules
- Discounts

should be configurable wherever possible.