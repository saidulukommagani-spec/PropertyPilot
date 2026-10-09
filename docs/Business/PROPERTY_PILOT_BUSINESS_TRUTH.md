## PropertyPilot Business Constitution

If business requirements, implementation details, architecture decisions, audits, code reviews, or future recommendations conflict with this document, this document takes precedence.

This document is the authoritative source of truth for PropertyPilot.
# PropertyPilot Business Truth

Version: 1.0
Status: Authoritative Business Document
Purpose: Source of truth for architecture reviews, development decisions, Codex audits, production readiness assessments, and future platform evolution.

---

# 1. Executive Summary

PropertyPilot is a property lifecycle management platform that enables property owners to manage, protect, maintain, monitor, verify, improve, and monetize their properties through a unified service ecosystem.

PropertyPilot is not a marketplace.

PropertyPilot owns the customer relationship, pricing, service quality, SLA management, evidence collection, and overall service delivery experience.

Agents, vendors, and partners operate as fulfillment resources behind the PropertyPilot brand.

Customers buy services from PropertyPilot, not from vendors.

---

# 2. Vision

To become the most trusted platform for managing every aspect of a property throughout its lifecycle.

Property owners should not need multiple vendors, brokers, legal advisors, maintenance providers, tax consultants, inspection teams, monitoring companies, or service providers.

PropertyPilot becomes the single operating platform for all property-related needs.

---

# 3. Mission

Provide transparent, evidence-driven, technology-enabled property services that help customers:

- Protect properties
- Maintain properties
- Verify properties
- Monitor properties
- Improve properties
- Monetize properties
- Transfer properties
- Manage properties remotely

while ensuring trust through documentation, accountability, evidence, and operational transparency.

---

# 4. Core Business Philosophy

PropertyPilot is:

- Property-centric
- Service-centric
- Evidence-driven
- SLA-driven
- Customer-first
- Technology-enabled
- Subscription-friendly
- Scalable

PropertyPilot is NOT:

- A marketplace
- A vendor directory
- A classified platform
- A broker aggregation platform
- A lead-selling business
- An Urban Company clone
- A JustDial clone

---

# 5. Revenue Model

PropertyPilot earns revenue through:

## Direct Service Revenue

Customers purchase services.

Examples:

- Property Inspection
- Tax Verification
- Tax Payment Assistance
- EC Retrieval
- Document Verification
- Tenant Verification
- Property Monitoring

## Subscription Revenue

Customers purchase subscription plans.

Examples:

- Silver
- Gold
- Platinum

Subscriptions provide:

- Discounts
- Entitlements
- Priority support
- Service bundles

## Add-On Revenue

Additional paid services beyond plan benefits.

## Enterprise Revenue (Future)

- Builder Services
- Investor Services
- Corporate Property Management

---

# 6. Property-Centric Architecture

Everything revolves around a property.

Customer
→ Property
→ Service Request
→ Visit
→ Evidence

Property is the primary business asset.

Every operational activity should ultimately relate to a property.

Exceptions may exist during lead qualification.

---

# 7. Property Types

Supported property types:

## Residential

- House
- Villa
- Apartment
- Flat

## Commercial

- Office
- Shop
- Warehouse
- Commercial Building

## Land

- Open Plot
- Residential Plot
- Commercial Plot

## Agricultural

- Agricultural Land
- Farm Land

Future property types must be supported without architecture changes.

---

# 8. Customer Model

Customer sources include:

## Existing Customer

Already registered in PropertyPilot.

## Prospect

Potential future customer.

## Admin-Created Customer

Created internally by PropertyPilot staff.

All customer journeys ultimately converge into the same customer lifecycle.

---

# 9. Lead Management

Leads represent business opportunities.

Lead sources:

- Website
- Mobile App
- Referral
- Existing Customer
- Agent
- Admin
- Walk-In
- Campaign

A lead may or may not become a customer.

A lead may or may not generate revenue.

---

# 10. Deal Management

Deals represent revenue opportunities.

Deals are NOT marketplace transactions.

Deals help track:

- Revenue potential
- Negotiation progress
- Customer intent
- Service opportunities

Flow:

Lead
→ Deal
→ Service Requests

One deal may generate multiple service requests.

Deals may originate from:

- New leads
- Existing customers
- Admin-created opportunities

---

# 11. Service Catalog Philosophy

Everything offered by PropertyPilot is a service.

Examples:

## Compliance Services

- Tax Verification
- Tax Payment
- EC Retrieval

## Legal Services

- Document Verification
- Registration Assistance

## Monitoring Services

- Property Monitoring
- Encroachment Monitoring

## Inspection Services

- Property Inspection
- Site Verification
- Physical Survey

## Tenant Services

- Tenant Verification
- Tenant Management

## Advisory Services

- Property Consultation

## Property Health Check Services

- Document Audit
- Compliance Audit
- Ownership Verification
- Physical Inspection
- Risk Assessment

Property Health Check is a service category.

It is NOT a separate platform module.

---

# 12. Service Requests

Service Requests represent customer work orders.

Lifecycle:

CREATED
→ ASSIGNED
→ IN_PROGRESS
→ COMPLETED

Additional states:

- CANCELLED
- REJECTED
- ON_HOLD

Every service request must be auditable.

---

# 13. Visit Model

Not every service requires a visit.

Examples:

Visit Required:

- Property Inspection
- Physical Survey
- Site Verification

Visit Not Required:

- EC Download
- Tax Verification
- Legal Review
- Document Audit

Visit requirements must be configurable per service.

No hardcoding.

---

# 14. Evidence Model

Evidence is the foundation of trust.

Evidence proves work was performed.

Supported evidence types:

- Before Photo
- After Photo
- Video
- GPS Verification
- Documents
- Signature

Evidence requirements vary by service.

Evidence rules must be configurable.

No hardcoded assumptions.

---

# 15. Service Completion Philosophy

A service request can only be completed after satisfying all configured business rules.

Examples:

Property Inspection:

- Visit Required
- Before Photo Required
- After Photo Required

Tax Verification:

- No Visit Required
- Document Required

Completion validation must be service-driven and configurable.

---

# 16. Agent Model

Agents perform work on behalf of PropertyPilot.

Agents are operational resources.

Customers receive services from PropertyPilot.

Agents are not independent businesses in the customer experience.

---

# 17. Vendor Model

Vendors are fulfillment partners.

PropertyPilot owns:

- Pricing
- SLA
- Customer Relationship
- Quality
- Outcomes

Customers primarily interact with PropertyPilot.

Vendor complexity should remain hidden.

---

# 18. Pricing Philosophy

Pricing is owned by PropertyPilot.

Vendor pricing is input.

Customer pricing is output.

Pricing must support:

- Vendor Cost
- Travel Cost
- Agent DA
- Overheads
- Margin
- GST
- Subscription Discount
- Coupon Discount
- Cluster Discount
- ETA Discount

All pricing rules must be configurable.

No hardcoding.

---

# 19. Subscription Philosophy

Subscriptions are value programs.

Subscriptions influence:

- Discounts
- Entitlements
- Service Bundles
- Priority Levels
- Customer Benefits

Subscriptions are not merely memberships.

---

# 20. Customer Transparency

Customers should be able to view:

- Service Status
- Visit History
- Evidence
- Pricing Benefits
- Savings
- Property History

Transparency builds trust.

---

# 21. Operational Transparency

Every action must be auditable.

Track:

- Who performed the action
- When it occurred
- What changed
- Why it changed

Auditability is mandatory.

---

# 22. Future Expansion Principles

Future modules must follow:

## Property First

Everything ultimately relates to a property.

## Service First

New capabilities should be offered as services where possible.

## Evidence First

Work must be provable.

## Configuration First

Business rules should be configurable.

## Customer Trust First

Trust outweighs operational convenience.

---
# 23. Geo Intelligence Framework
Location is a first-class business concept.
PropertyPilot operations are geographically aware.
The platform must support:
- Coverage Zones
- Clusters
- Agent Territories
- GPS Validation
- Travel Cost Calculation
- Distance-Based Pricing
- Geo Eligibility Rules
Coverage Zones
Coverage Zones define where PropertyPilot services can be delivered.
Zones influence:
- Service availability
- Agent assignment
- Travel pricing
- SLA commitments
Clusters
Clusters group nearby properties.
Clusters exist to:
- Improve operational efficiency
- Reduce travel cost
- Improve agent utilization
- Enable cluster discounts
GPS Validation
Certain services require GPS verification.
GPS may be required for:
- Site inspections
- Property visits
- Physical surveys
- Monitoring visits
GPS validation requirements must be configurable.
No hardcoding.
# 24. Property Health Framework
Property Health is a service category.
Property Health evaluates the condition and risk profile of a property.
Property Health assessments may include:
Documentation Health
- Ownership verification
- Registration validation
- EC verification
- Missing document detection
Compliance Health
- Tax status
- Municipal compliance
- Regulatory obligations
Physical Health
- Structural condition
- Safety observations
- Maintenance requirements
Risk Assessment
- Legal risk
- Compliance risk
- Physical risk
- Financial risk
Property Health produces:
- Health Score
- Risk Score
- Findings
- Recommendations
- Service Opportunities
# 25. Monitoring Framework
PropertyPilot supports ongoing monitoring services.
Monitoring may be:
- Scheduled
- Periodic
- Event Driven
Examples:
- Vacant Property Monitoring
- Encroachment Monitoring
- Tax Monitoring
- Compliance Monitoring
- Maintenance Monitoring
Monitoring may automatically generate:
- Alerts
- Recommendations
- Service Requests
Monitoring is a revenue-generating service category.
# 26. Notification Framework
PropertyPilot owns customer communication.
Customers should never depend on agents or vendors for status updates.
Customer Notifications
Examples:
- Service Request Created
- Agent Assigned
- Visit Scheduled
- Visit Completed
- Service Completed
- Service Cancelled
- Subscription Activated
- Subscription Expiring
- Health Alerts
- Monitoring Alerts
Agent Notifications
Examples:
- Assignment Created
- Assignment Accepted
- Visit Scheduled
- Evidence Rejected
- Completion Approved
Admin Notifications
Examples:
- SLA Breach
- Escalation Created
- Payment Failure
- Validation Failure
- Service Stuck
Supported Channels
Phase 1:
- Email
- SMS
- In-App Notifications
Phase 2:
- WhatsApp
- Push Notifications
Phase 3:
- Voice and IVR
Notification content must be template-driven.
No hardcoded messages.
# 27. Payment and Refund Framework
PropertyPilot supports:
- Service Payments
- Subscription Payments
- Add-On Purchases
- Refunds
- Partial Refunds
Payment events must be fully auditable.
Track:
- Amount
- Method
- Status
- Gateway Reference
- Refund Details
# 28. Complaint and Escalation Framework
Customers may raise complaints.
Complaint lifecycle:
OPEN
→ IN_PROGRESS
→ ESCALATED
→ RESOLVED
→ CLOSED
Complaints must support:
- SLA tracking
- Escalation levels
- Ownership assignment
- Resolution evidence
# 29. Document Vault Framework
PropertyPilot stores and manages:
- Property Documents
- Service Documents
- Evidence Files
- Health Reports
- Compliance Reports
Documents must support:
- Versioning
- Audit History
- Access Control
- Retention Policies
# 30. AI Recommendation Framework (Future)
PropertyPilot may proactively recommend services.
Examples:
- Property Tax Due
- Compliance Renewal Required
- Health Score Declining
- Inspection Recommended
- Insurance Renewal Due
AI recommendations should create business opportunities while improving customer outcomes.
# 31. Customer Dashboard Philosophy
The customer dashboard is the customer's single window into property operations.
Customers should be able to view:
- Properties
- Service Requests
- Visits
- Evidence
- Health Reports
- Monitoring Alerts
- Subscription Benefits
- Savings
- Recommendations
Transparency is a trust-building feature.
# 32. Admin Platform Philosophy
The admin platform controls business operations.
Administrators should manage:
- Services
- Pricing Rules
- Evidence Rules
- Visit Rules
- Notification Templates
- Coverage Zones
- Clusters
- Agents
- Vendors
- Monitoring Policies
Business rules should be configuration-driven.
No deployment should be required for ordinary business rule changes.
# 33. Enterprise Architecture Principles
PropertyPilot architecture must remain:
Configuration First
Business behavior should come from configuration.
Evidence First
Completed work must be provable.
Audit First
Every important action must be traceable.
Service First
Capabilities should be modeled as services whenever possible.
Property First
Properties remain the center of the business model.
Customer First
Customer trust outweighs operational convenience.
Scale First
Architecture should support large-scale growth without redesign.


# 34. Non-Negotiable Rules



Never:

- Convert PropertyPilot into a marketplace
- Expose vendor complexity to customers
- Hardcode pricing rules
- Hardcode evidence requirements
- Hardcode visit requirements
- Couple business rules to code when configuration is possible

Always:

- Keep pricing configurable
- Keep services configurable
- Keep evidence configurable
- Keep workflows auditable
- Keep customer experience simple
- Keep PropertyPilot as the single trusted brand

---
END OF DOCUMENT