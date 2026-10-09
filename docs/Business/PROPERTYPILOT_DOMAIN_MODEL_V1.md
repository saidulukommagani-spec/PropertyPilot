Absolutely. Consider this saved as the current architectural baseline for our future PropertyPilot discussions.
PropertyPilot Reference Baselines
✅ V1 Business Layer
PROPERTYPILOT_BUSINESS_BLUEPRINT_V1
Defines:
- Vision
- Mission
- Revenue Streams
- Service Philosophy
- Subscription Philosophy
- Lead Philosophy
- Deal Philosophy
- Property-Centric Business Model
Status:
APPROVED BASELINE

✅ V1 Operational Layer
PROPERTYPILOT_OPERATIONAL_WORKFLOW_V1
Defines:
- Customer Lifecycle
- Property Lifecycle
- Service Request Lifecycle
- Assignment Workflow
- Visit Workflow
- Evidence Workflow
- Subscription Workflow
- Lead Workflow
- Deal Workflow
- Revenue Workflow
Status:
APPROVED BASELINE

✅ V1 Domain Layer
PROPERTYPILOT_DOMAIN_MODEL_V1
Tier 1 Domains (Core Revenue Engine)
Customer Domain
Owns:
Customer
Address
Preferences
Verification
Favorites
Search History

Property Domain
Owns:
Property
Property Owner
Property Media
Property Documents
Property Attributes
Property Coordinates

Central Entity:
Property

Service Catalog Domain
Owns:
Service Category
Service
Service Rules
Evidence Rules
Pricing Rules

Purpose:
What PropertyPilot Can Do

Service Delivery Domain
Owns:
Service Request
Assignment
Visit
Evidence
Completion

Primary Revenue Engine.
Commercial Domain
Owns:
Pricing
Quotes
Estimates
Invoices
Payments

Money Flow Domain.
Tier 2 Domains (Growth Engine)
Subscription Domain
Owns:
Plans
Add-ons
Benefits
Entitlements
Consumption
Renewals

Recurring Revenue.
Lead Domain
Owns:
Lead
Lead Source
Qualification
Conversion

Opportunity Management.
Deal Domain
Owns:
Deal
Buyer
Seller
Negotiation
Closure
Commission

Transaction Revenue.
Tier 3 Domains (Operational Excellence)
Operations Domain
Owns:
Agent
Monitoring
Schedules
SLA
Assignments

Reporting Domain
Owns:
KPIs
Dashboards
Revenue Reports
Operational Reports

Revenue Priority Ranking
Priority 1
Property
Service Catalog
Service Requests
Visits
Evidence
Pricing

Priority 2
Subscriptions
Leads
Deals

Priority 3
Reporting
Analytics
Marketplace

PropertyPilot Core Relationship Model
Customer
    ↓
Property
    ↓
Service Request
    ↓
Assignment
    ↓
Visit
    ↓
Evidence
    ↓
Completion

Property
    ↓
Lead
    ↓
Deal

Property
    ↓
Subscription
    ↓
Monitoring

Architectural Principle
Property is the Center.

Services are the Revenue Engine.

Subscriptions create Recurring Revenue.

Leads create Opportunities.

Deals create Transaction Revenue.

Status:
APPROVED BASELINE
VERSION 1.0

What comes next?
The next document should be:
PROPERTYPILOT_SCREEN_BLUEPRINT_V1

This will define:
- Admin Portal Navigation
- Customer Portal Navigation
- Agent Portal Navigation
- Dashboard Layouts
- Screen-to-Screen Navigation
- End-to-End User Journeys
This is the document that will finally let us compare:
Business Vision
       vs
Current Frontend
       vs
Current Backend

and identify exactly what remains before PropertyPilot reaches production readiness.