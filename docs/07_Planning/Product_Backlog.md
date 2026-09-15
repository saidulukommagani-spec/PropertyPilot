# PropertyPilot Product Backlog

Document Type: Product Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for product, architecture, and engineering review

This backlog is derived from the current PropertyPilot project documents:
- Feature_Catalog.md
- Service_Catalog.md
- Customer_Journeys.md
- Subscription_Plans.md
- Screen_Catalog.md
- Screen_Flows.md
- Database_Physical_Model.md

It is structured by release and functional area and includes:
- Epic
- Feature
- User Story
- Priority (P0/P1/P2/P3)
- Story Points
- Release
- Dependencies

---

## 1. MVP Backlog

### 1.1 Customer App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Customer Experience | Account Access | As a tenant, I want to log in securely so that I can access my account and property information. | P0 | 5 | MVP | Auth Service, RBAC, MFA |
| Customer Experience | Resident Dashboard | As a tenant, I want to view my lease, billing, and request status in one place so that I can self-serve. | P0 | 8 | MVP | Dashboard API, Lease Service, Billing Service |
| Customer Experience | Maintenance Request | As a tenant, I want to submit a maintenance request with photos so that issues can be resolved quickly. | P0 | 8 | MVP | Maintenance Service, Document Storage |
| Customer Experience | Billing and Payment | As a tenant, I want to view invoices and pay online so that I can settle my account. | P0 | 8 | MVP | Billing Service, Payment Gateway, Invoice API |
| Customer Experience | Notifications | As a tenant, I want to receive payment and maintenance reminders so that I do not miss deadlines. | P1 | 5 | MVP | Notification Service, SMS/Email/WhatsApp Provider |

### 1.2 Agent App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Agent Operations | Lead and Applicant Tracking | As an agent, I want to capture and manage leads so that I can convert prospects to tenants. | P0 | 8 | MVP | Leasing Service, CRM Integration |
| Agent Operations | Tour Scheduling | As an agent, I want to schedule property tours so that prospects can view units. | P1 | 5 | MVP | Lease Service, Property Service |
| Agent Operations | Lease Workflow | As an agent, I want to create and update lease records so that occupancy and billing can start accurately. | P0 | 8 | MVP | Lease Service, Tenant Service |
| Agent Operations | Maintenance Dispatch | As an agent, I want to create and assign work orders so that tenant issues are resolved. | P0 | 8 | MVP | Maintenance Service, Work Order APIs |

### 1.3 Operations Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Property Operations | Property and Unit Setup | As a property manager, I want to create and maintain properties and units so that operational records are accurate. | P0 | 8 | MVP | Property Service, Unit Domain |
| Property Operations | Tenant and Lease Management | As a property manager, I want to manage tenants and leases so that rental operations are accurate and current. | P0 | 13 | MVP | Tenant Service, Lease Service |
| Property Operations | Maintenance Queue | As an operations user, I want to track work orders by status so that I can prioritize service requests. | P0 | 8 | MVP | Maintenance Service, Workflow Engine |
| Property Operations | Collections and Billing | As a property manager, I want to review invoice and delinquency status so that I can act on overdue balances. | P0 | 8 | MVP | Billing Service, Collections Rules |
| Property Operations | Operational Reporting | As a property manager, I want to view occupancy and collections summaries so that I can make decisions faster. | P1 | 8 | MVP | Reporting Service, PostgreSQL |

### 1.4 Admin Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Platform Administration | User and Permission Management | As an admin, I want to invite users and assign roles so that access is secure and role-based. | P0 | 5 | MVP | User Service, RBAC |
| Platform Administration | Organization Setup | As an admin, I want to configure organizations and portfolios so that the platform is ready for operations. | P0 | 5 | MVP | Organization Service |
| Platform Administration | Subscription Administration | As an admin, I want to manage subscription plans and billing settings so that the platform can be monetized correctly. | P0 | 8 | MVP | Subscription Service, Billing Service |

### 1.5 Subscriptions

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Monetization | Plan Selection | As an admin, I want to choose a subscription plan so that the organization can use the platform. | P0 | 5 | MVP | Subscription Service |
| Monetization | Plan Upgrade / Downgrade | As an admin, I want to change the plan when business needs change so that services stay aligned with usage. | P1 | 5 | MVP | Subscription Service, Billing Service |
| Monetization | Renewal Workflow | As an admin, I want renewal reminders and billing dates so that I can avoid service interruption. | P0 | 5 | MVP | Subscription Service, Notification Service |

### 1.6 Payments

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Financial Operations | Payment Collection | As a tenant, I want to make an invoice payment in the app so that I can clear my account. | P0 | 8 | MVP | Payment Gateway, Billing Service |
| Financial Operations | Payment Reconciliation | As a finance team member, I want to reconcile payments against invoices so that financial records are accurate. | P0 | 8 | MVP | Integration Service, Billing Service |
| Financial Operations | Delinquency Tracking | As a property manager, I want to see overdue balances so that I can act on collections. | P1 | 5 | MVP | Billing Service, Collections Rules |

### 1.7 Notifications

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Communications | Reminders | As a tenant, I want to receive rent and renewal reminders so that I do not miss due dates. | P0 | 5 | MVP | Notification Service |
| Communications | Maintenance Updates | As a resident, I want status updates for my maintenance request so that I know when work is progressing. | P0 | 5 | MVP | Notification Service, Maintenance Service |
| Communications | Notification Preferences | As a user, I want to set communication preferences so that I control how I receive updates. | P1 | 3 | MVP | User Profile, Notification Service |

### 1.8 Reports

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Visibility and Analytics | Portfolio Summary | As a property manager, I want a summary of occupancy, delinquencies, and maintenance so that I can assess portfolio health. | P0 | 8 | MVP | Reporting Service |
| Visibility and Analytics | Invoice Aging | As a finance manager, I want to view invoice aging so that I can prioritize collections. | P0 | 5 | MVP | Reporting Service, Billing Service |
| Visibility and Analytics | Export Reports | As a manager, I want to export reports so that I can share operational data with stakeholders. | P1 | 5 | MVP | Reporting Service, Storage |

### 1.9 Property Management

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Real Estate Operations | Property Portfolio Setup | As an owner, I want to create portfolio hierarchies so that properties can be grouped intelligently. | P0 | 5 | MVP | Organization Service, Property Service |
| Real Estate Operations | Unit Availability Tracking | As a manager, I want to see unit availability so that I can manage occupancy effectively. | P1 | 5 | MVP | Property Service, Search Service |

### 1.10 Verification Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Risk and Compliance | Document Verification | As an admin, I want documents to be linked to the correct property or tenant so that records are trustworthy. | P1 | 5 | MVP | Document Service, Validation Rules |
| Risk and Compliance | Access Review | As a compliance reviewer, I want to review access and important changes so that audit risk is reduced. | P1 | 5 | MVP | Audit Service, Admin Portal |

### 1.11 Monitoring Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Operations Monitoring | System Health Dashboard | As an operator, I want to see health and alert status so that I can respond to service issues quickly. | P1 | 5 | MVP | Monitoring Service, Alerting |
| Operations Monitoring | Audit Log Review | As an admin, I want to review changes and access events so that I can investigate issues. | P0 | 5 | MVP | Audit Service |

### 1.12 Marketplace

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | Marketplace Placeholder | As a product owner, I want a roadmap placeholder for marketplace features so that future expansion is planned. | P3 | 2 | MVP | None |

### 1.13 NRI Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | NRI Service Placeholder | As a product owner, I want a roadmap placeholder for NRI services so that regional expansion can be planned. | P3 | 2 | MVP | None |

---

## 2. Phase 2 Backlog

### 2.1 Customer App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Customer Experience | Payment Dispute Handling | As a tenant, I want to dispute a payment issue in the app so that billing concerns are resolved quickly. | P1 | 5 | Phase 2 | Billing Service, Payment Reconciliation |
| Customer Experience | Document Access | As a tenant, I want to access lease and support documents from my account so that I can review and download them. | P1 | 3 | Phase 2 | Document Service, Storage |

### 2.2 Agent App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Agent Operations | Collections Follow-up | As an agent, I want to follow up on overdue invoices so that collection outcomes improve. | P1 | 5 | Phase 2 | Billing Service, Collections Workflow |
| Agent Operations | Offline Notes | As an agent, I want to capture field notes offline so that I can update records in low-connectivity environments. | P2 | 5 | Phase 2 | Mobile Storage, Sync Service |

### 2.3 Operations Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Property Operations | Vendor Management | As an operations manager, I want to onboard and assign vendors so that service work can be completed efficiently. | P1 | 8 | Phase 2 | Vendor Management Service |
| Property Operations | SLA Tracking | As an operations manager, I want to track repair SLA compliance so that service quality is monitored. | P1 | 5 | Phase 2 | Maintenance Service, Reporting Service |
| Property Operations | Compliance Center | As an operations manager, I want to track expiring and missing compliance documents so that legal risks are reduced. | P1 | 8 | Phase 2 | Document Service, Compliance Rules |

### 2.4 Admin Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Platform Administration | Integration Configuration | As an admin, I want to configure external integrations so that the platform can communicate with payment and accounting systems. | P1 | 8 | Phase 2 | Integration Service |
| Platform Administration | Notification Templates | As an admin, I want to manage message templates so that communication is consistent and compliant. | P1 | 5 | Phase 2 | Notification Service |
| Platform Administration | Security Review Dashboard | As an admin, I want to review security and audit events so that I can monitor access and risk. | P0 | 8 | Phase 2 | Audit Service, Security Dashboard |

### 2.5 Subscriptions

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Monetization | Proration and Adjustments | As an admin, I want proration when a plan changes so that billing is fair and accurate. | P0 | 8 | Phase 2 | Subscription Service, Billing Service |
| Monetization | Failed Renewal Flow | As an admin, I want to manage failed renewals so that service continues or is suspended according to policy. | P0 | 8 | Phase 2 | Subscription Service, Notification Service |
| Monetization | Contract Cancellation | As an admin, I want to cancel and archive subscriptions so that contract terms are respected. | P1 | 5 | Phase 2 | Subscription Service, Audit Service |

### 2.6 Payments

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Financial Operations | Payment Retry Rules | As a finance user, I want payment retries based on status so that failed payments do not stay unresolved. | P0 | 8 | Phase 2 | Payment Gateway, Billing Service |
| Financial Operations | Manual Adjustments | As a finance manager, I want to post manual adjustments so that exception cases are handled legally and correctly. | P1 | 5 | Phase 2 | Billing Service, Audit Service |

### 2.7 Notifications

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Communications | Delivery Status Tracking | As an admin, I want to track delivery failures so that messaging issues can be resolved quickly. | P1 | 5 | Phase 2 | Notification Service, Audit Service |
| Communications | Multi-Channel Routing | As a platform operator, I want to route notifications by channel preferences so that tenants receive the right message format. | P1 | 5 | Phase 2 | Notification Service, User Profile |

### 2.8 Reports

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Visibility and Analytics | Maintenance and SLA Reports | As an operations manager, I want scheduled maintenance and SLA reports so that I can monitor service quality. | P1 | 8 | Phase 2 | Reporting Service, Maintenance Service |
| Visibility and Analytics | Executive Dashboards | As an executive sponsor, I want a KPI dashboard so that I can track portfolio and platform health. | P1 | 8 | Phase 2 | Reporting Service, Dashboard Service |

### 2.9 Property Management

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Real Estate Operations | Portfolio Compliance Dashboard | As an owner, I want to see compliance and document status by property so that legal exposure is visible. | P1 | 5 | Phase 2 | Document Service, Reporting Service |
| Real Estate Operations | Expense Tracking | As a property manager, I want to capture operating expenses so that I can compare actuals with budget. | P1 | 5 | Phase 2 | Billing Service, Property Service |

### 2.10 Verification Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Risk and Compliance | Document Expiry Alerts | As a property manager, I want to receive alerts for expiring documents so that compliance tasks are completed on time. | P1 | 5 | Phase 2 | Document Service, Notification Service |
| Risk and Compliance | Compliance Review Workflow | As a compliance reviewer, I want to manage document and policy review workflows so that audit obligations are met. | P1 | 8 | Phase 2 | Compliance Service, Audit Service |

### 2.11 Monitoring Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Operations Monitoring | Alerting Rules | As an operations manager, I want alerting for failed recurring jobs and service issues so that downtime is reduced. | P1 | 5 | Phase 2 | Monitoring Service |
| Operations Monitoring | Incident Workflow | As a support operator, I want to log and track incidents so that service issues are triaged and resolved. | P1 | 8 | Phase 2 | Monitoring Service, Support Workflow |

### 2.12 Marketplace

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | Marketplace Partner Listing | As a property operator, I want to publish listings to approved marketplace partners so that inventory reaches more prospects. | P2 | 8 | Phase 2 | Listing Service, Marketplace Integration |

### 2.13 NRI Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | NRI Portfolio Dashboard | As an NRI investor, I want a portfolio and payment dashboard so that my investments can be monitored remotely. | P2 | 8 | Phase 2 | Investor Dashboard Service, Multi-Org Services |

---

## 3. Phase 3 Backlog

### 3.1 Customer App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Customer Experience | Renewal Self-Service | As a tenant, I want to view renewal options and respond digitally so that lease renewal is easy. | P1 | 8 | Phase 3 | Lease Service, Notification Service |
| Customer Experience | Service Feedback | As a tenant, I want to rate completed maintenance work so that service quality can improve. | P2 | 3 | Phase 3 | Maintenance Service, Survey Service |

### 3.2 Agent App

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Agent Operations | Smart Lead Routing | As an agent, I want leads to be routed to the most relevant properties or teams so that conversion rate improves. | P2 | 8 | Phase 3 | Lead Routing Service, CRM Integration |
| Agent Operations | Performance Insights | As an agent, I want activity and conversion metrics so that I can improve my performance. | P2 | 5 | Phase 3 | Reporting Service, Analytics |

### 3.3 Operations Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Property Operations | Predictive Maintenance | As a property manager, I want maintenance risk insights so that I can prevent issues before they escalate. | P2 | 8 | Phase 3 | Analytics, Maintenance Service |
| Property Operations | Portfolio Forecasting | As an operations executive, I want forecast views so that I can plan budgets and resource allocation. | P2 | 8 | Phase 3 | Reporting Service, Analytics Service |

### 3.4 Admin Portal

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Platform Administration | Enterprise SSO | As an admin, I want enterprise SSO so that security and user administration align with corporate policies. | P1 | 8 | Phase 3 | Identity Provider, Auth Service |
| Platform Administration | Advanced Governance Controls | As an admin, I want policy-based governance and approval flows so that enterprise compliance is enforced. | P1 | 8 | Phase 3 | Policy Service, Audit Rules |

### 3.5 Subscriptions

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Monetization | Usage-Based Expansion | As an admin, I want usage-based billing options so that enterprise customers can scale cost with demand. | P2 | 8 | Phase 3 | Subscription Service, Billing Service |
| Monetization | Partner Reseller Billing | As a business owner, I want reseller or partner billing support so that large accounts can be managed effectively. | P2 | 8 | Phase 3 | Billing Service, Partner Service |

### 3.6 Payments

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Financial Operations | Multi-Method Payment Expansion | As a tenant, I want more payment options so that I can pay in a way that suits me. | P2 | 5 | Phase 3 | Payment Gateway, Billing Service |
| Financial Operations | Payment Analytics | As a finance leader, I want payment trend insights so that cashflow and risk can be managed better. | P2 | 5 | Phase 3 | Reporting Service, Billing Service |

### 3.7 Notifications

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Communications | AI-Assisted Messaging | As a manager, I want intelligent reminders and escalations so that communication is more timely. | P3 | 5 | Phase 3 | AI Workflow, Notification Service |
| Communications | Event-Triggered Campaigns | As a marketing or retention team, I want segment-based campaigns so that operational communication is more targeted. | P3 | 5 | Phase 3 | Campaign Service, Notification Service |

### 3.8 Reports

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Visibility and Analytics | Predictive Portfolio Insights | As an executive, I want predictive insights by portfolio so that I can plan resource and revenue decisions. | P2 | 8 | Phase 3 | Reporting Service, Machine Learning |
| Visibility and Analytics | Custom Analytical Views | As a manager, I want custom dashboards so that I can track domain-specific KPIs. | P2 | 8 | Phase 3 | Reporting Service, Dashboard Builder |

### 3.9 Property Management

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Real Estate Operations | Portfolio Optimization | As an owner, I want portfolio insights to optimize site performance so that operational returns improve. | P2 | 8 | Phase 3 | Reporting Service, Analytics |
| Real Estate Operations | Risk Scoring | As a portfolio manager, I want risk insights for properties so that I can prioritize attention. | P2 | 5 | Phase 3 | Analytics Service, Property Service |

### 3.10 Verification Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Risk and Compliance | Automated Compliance Checks | As a compliance reviewer, I want automated checks so that compliance tasks are not missed. | P2 | 8 | Phase 3 | Compliance Service, Rules Engine |
| Risk and Compliance | Regulatory Reporting | As a legal or compliance team, I want regulatory outputs so that external reporting obligations are met. | P2 | 8 | Phase 3 | Reporting Service, Compliance Service |

### 3.11 Monitoring Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Operations Monitoring | Auto-Remediation | As an operator, I want the platform to take safe corrective action when simple failures occur so that service impact is reduced. | P3 | 8 | Phase 3 | Monitoring Service, Automation Engine |
| Operations Monitoring | Predictive Capacity Planning | As an engineering leader, I want capacity forecasts so that the platform remains performant and stable. | P3 | 8 | Phase 3 | Monitoring Service, Analytics |

### 3.12 Marketplace

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | Property Listing Marketplace | As an owner, I want to list units and properties in a marketplace so that occupancy can scale beyond direct channels. | P2 | 8 | Phase 3 | Marketplace Service, Property Service |
| Future Expansion | Partner Commerce | As a property operator, I want to coordinate with partner service providers so that local services are easier to source. | P2 | 5 | Phase 3 | Marketplace Service, Vendor Service |

### 3.13 NRI Services

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Future Expansion | Investor Communication Portal | As an NRI investor, I want to receive portfolio updates so that I stay informed about my investments. | P2 | 5 | Phase 3 | Investor Service, Notification Service |
| Future Expansion | International Payment Coordination | As an NRI investor, I want international transaction support so that cross-border ownership operations are easier. | P3 | 8 | Phase 3 | Payment Service, FX Integration |

---

## 4. Release Summary

| Release | Target Outcome |
|---|---|
| MVP | Core property operations, tenant self-service, maintenance, billing, subscription basics, and secure admin controls |
| Phase 2 | Financial control maturity, SLA/workflow improvements, vendor management, enterprise governance, and analytics |
| Phase 3 | Predictive and market-facing expansion, automation, enterprise identity, and broader ecosystem integrations |

---

## 5. Cross-Cutting Dependencies

| Dependency | Applies To |
|---|---|
| Auth and RBAC | Customer App, Agent App, Ops Portal, Admin Portal |
| PostgreSQL | All transactional records |
| Kafka | Notifications, reporting, integration, workflow events |
| Elasticsearch | Search and reporting queries |
| Redis | Session and hot data caching |
| AWS S3 | Document storage and retrieval |
| Payment Gateway | Invoice payments and reconciliation |
| Email/SMS/WhatsApp Providers | Notification delivery |
| External Accounting and CRM | Billing and lead synchronization |
| Monitoring and Audit Services | Platform health, security, and compliance |

---

## 6. Product Prioritization Notes

- P0 items are required for MVP go-live readiness.
- P1 items are needed for a stable operational launch and expansion readiness.
- P2 items should be funded in Phase 2 or 3 based on product scale.
- P3 items are strategic and future-oriented.

This backlog is intended to support product prioritization, engineering planning, and release strategy for the PropertyPilot platform.

---

## 7. Catalog-Derived Missing Backlog Items

The following items are appended from Feature_Catalog.md, Service_Catalog.md, and Subscription_Plans.md where no equivalent backlog item exists above.

### 7.1 MVP Catalog Coverage

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Customer Management | Customer Registration, KYC and Profile | As a property owner, I want to register, complete KYC, and maintain my profile so that I can use PropertyPilot services securely. | P0 | 8 | MVP | Identity Service, KYC Provider, Customer Service |
| Property Management | Property Registration and Details | As a property owner, I want to register a property with its type, status, photos, and documents so that it can be serviced accurately. | P0 | 8 | MVP | Property Service, Document Service, Media Evidence Service |
| Property Management | Property Search | As a customer or operator, I want to find registered properties quickly so that I can view and manage the correct property. | P0 | 5 | MVP | Property Service, Search Service |
| Verification Services | GPS and Ownership Verification | As a property owner, I want GPS and ownership verification for my property so that its location and ownership evidence are trusted. | P0 | 8 | MVP | Verification Service, Geo-location Service, Document Service |
| Service Management | Service Catalog and Booking | As a customer, I want to browse eligible services and book one for my property so that I can request the required work. | P0 | 8 | MVP | Service Catalog Service, Eligibility Rules, Booking Workflow |
| Service Management | Service Assignment, Tracking and Completion | As an operations user, I want to assign, track, and complete a booked service so that customers can follow its progress. | P0 | 8 | MVP | Assignment Workflow, Agent Management, Notification Service |
| Field Operations | Scheduled Monitoring Visits | As a property owner, I want recurring monitoring visits scheduled for my property so that its condition is checked regularly. | P0 | 8 | MVP | Scheduling Service, Monitoring Service, Subscription Service |
| Field Operations | Field Evidence Capture and Report Submission | As an agent, I want to capture GPS, timestamped photos, notes, and a report from the field so that service evidence is complete. | P0 | 8 | MVP | Agent App, Geo-location Service, Media Evidence Service, Property Report Engine |
| Agent Management | Agent Registration and Task Management | As an operations manager, I want to register agents and assign service tasks so that field work is dispatched to qualified people. | P0 | 8 | MVP | Agent Management Service, Task Service, RBAC |
| Lead Management | Lead Status and Protection Controls | As a lead manager, I want to capture, assign, track, and restrict access to leads so that conversion work and customer data are protected. | P0 | 8 | MVP | Lead Service, RBAC, Audit Service |
| Pricing | Customer Cost Calculator and Dynamic Pricing | As a customer, I want an itemized service estimate based on my property, distance, travel time, and service requirements so that I can decide before booking. | P0 | 8 | MVP | Pricing Engine, Property Service, Coverage Service |
| Payments and Billing | Payment Receipts | As a customer, I want a receipt after a successful payment so that I have proof of payment. | P0 | 3 | MVP | Payment Gateway, Billing Service, Document Service |
| NRI Services | NRI Registration and Updates | As an NRI property owner, I want to register for remote services and receive WhatsApp updates so that I can stay informed from abroad. | P0 | 5 | MVP | Customer Service, Notification Service, WhatsApp Provider |
| Administration | Service, Customer, Property and Agent Administration | As an administrator, I want to manage service definitions and the core customer, property, and agent records so that operations remain controlled. | P0 | 8 | MVP | Admin Portal, Customer Service, Property Service, Agent Management Service |

### 7.2 Phase 2 Catalog Coverage

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---:|---:|---|---|
| Property Management | Multi-Property Ownership and Access | As an owner, I want to manage multiple properties and assign ownership roles, joint/family ownership, and property access so that collaborators have appropriate visibility. | P1 | 8 | Phase 2 | Property Service, Customer Service, RBAC |
| Verification Services | Statutory and Legal Verifications | As a property owner, I want property-tax, encroachment, HMDA, DTCP, and legal verification services so that I can assess compliance risks. | P1 | 13 | Phase 2 | Verification Service, External Authority Integrations, Document Service |
| Property Monitoring | Video Reports, Issue Alerts and Emergency Visits | As a property owner, I want video reports, condition alerts, issue escalation, and emergency visits so that urgent property risks receive attention. | P1 | 8 | Phase 2 | Monitoring Service, Media Evidence Service, Notification Service, Dispatch Workflow |
| Service Management | Service Rating and Cancellation | As a customer, I want to rate a completed service or cancel an eligible booking so that service quality and exceptions are handled transparently. | P1 | 5 | Phase 2 | Service Workflow, Refund Service, Review Rating Service |
| Agent Management | Mobile Checklists and Performance Tracking | As an operations manager, I want agents to use service checklists and have performance metrics so that field work is consistent and measurable. | P1 | 8 | Phase 2 | Agent App, Checklist Service, Reporting Service |
| Vendor Marketplace | Vendor Registration, Approval and Directory | As an operations manager, I want to register, approve, and find vendors by supported service so that work can be sourced reliably. | P1 | 8 | Phase 2 | Vendor Management Service, Service Catalog Service, Compliance Service |
| Payments and Billing | Refund Processing | As a finance user, I want to process eligible refunds for undelivered services, duplicate payments, or platform errors so that subscription and payment policies are enforced. | P1 | 5 | Phase 2 | Refund Service, Payment Gateway, Audit Service |
| Subscription Management | Named Plan Entitlements | As an administrator, I want to configure Silver, Gold, Platinum, NRI Elite, and NRI Concierge entitlements and SLAs so that each customer receives the contracted level of service. | P1 | 8 | Phase 2 | Subscription Service, Service Catalog Service, SLA Service |
| Subscription Management | Pause, Visit-Expiry and Refund Policies | As a subscription administrator, I want to apply pause limits, visit carry-forward/expiry rules, and usage-based refund eligibility so that plan policies are applied consistently. | P1 | 5 | Phase 2 | Subscription Service, Billing Service, Refund Service |
| Notifications | Push Notifications and Escalation Alerts | As a customer, I want push notifications and escalation alerts for significant service events so that I can respond promptly. | P1 | 5 | Phase 2 | Notification Service, Mobile Push Provider, Workflow Engine |
| Reporting | Verification, Monitoring and Revenue Reports | As a manager, I want verification, monitoring, property-summary, and revenue reports so that I can oversee services and business performance. | P1 | 8 | Phase 2 | Property Report Engine, Reporting Service, Verification Service |
| NRI Services | NRI Video Walkthroughs and Emergency Checks | As an NRI customer, I want video walkthrough reports and emergency property checks so that I can inspect and protect my property remotely. | P1 | 8 | Phase 2 | Monitoring Service, Media Evidence Service, Dispatch Workflow |
| Service Catalog Management | Service Lifecycle, Eligibility and SLA Configuration | As a service administrator, I want to configure service lifecycle status, property/coverage eligibility, required skills, SLA, pricing, and deliverables so that only deliverable services can be booked. | P1 | 13 | Phase 2 | Service Catalog Service, Pricing Engine, SLA Service, Coverage Management, Agent Management |
| Service Catalog Management | Service Audit, Ownership and Analytics | As a service owner, I want service configuration changes audited and service performance measured so that governance and catalog decisions are evidence-based. | P1 | 8 | Phase 2 | Audit Service, Reporting Service, Service Catalog Service |

### 7.3 Phase 3 Catalog Coverage

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---:|---:|---|---|
| Customer Management | Family Member Access and Customer Preferences | As a customer, I want to give family members controlled access and manage broader service preferences so that the account reflects my household needs. | P2 | 5 | Phase 3 | Customer Service, RBAC, Preference Service |
| Verification Services | Title Search and Court Case Verification | As a property owner, I want title-search and court-case verification services so that I can complete deeper due diligence. | P2 | 8 | Phase 3 | Verification Service, Legal Data Integrations, Document Service |
| Agent Management | Agent Ratings | As an operations manager, I want customers and operators to rate agents so that staffing decisions reflect service quality. | P2 | 5 | Phase 3 | Review Rating Service, Agent Management Service |
| Vendor Marketplace | Quotes, Ratings and Marketplace Search | As a customer or operator, I want to compare vendor quotes, ratings, and availability so that I can select the right provider. | P2 | 8 | Phase 3 | Vendor Management Service, Quotation Service, Search Service |
| Marketplace | Buyer-Seller Workflow, Matching and Commission | As a marketplace user, I want protected buyer-seller workflows, property matching, and commission tracking so that property transactions can be managed safely. | P2 | 13 | Phase 3 | Marketplace Service, Lead Protection Engine, Payment Service |
| Service Expansion | Rental Management Services | As a property owner, I want tenant search and verification, agreements, inspections, rent follow-up, and rental management so that rental property operations are handled end-to-end. | P2 | 13 | Phase 3 | Rental Service, Tenant Service, Billing Service, Inspection Workflow |
| Service Expansion | Construction Monitoring Services | As a property owner, I want to register construction work and track milestones, site visits, progress reports, and budgets so that construction is transparent. | P2 | 13 | Phase 3 | Construction Service, Project Workflow, Media Evidence Service, Reporting Service |
| Service Expansion | Maintenance, Agriculture and Security Services | As a property owner, I want to request maintenance, farm, and security services so that specialist property care can be coordinated through one platform. | P2 | 13 | Phase 3 | Service Catalog Service, Vendor Management Service, Scheduling Service |
| Premium Services | Drone and Virtual Property Services | As a property owner, I want drone surveys, photography/videography, live walkthroughs, and virtual tours so that I can inspect property remotely. | P2 | 8 | Phase 3 | Qualified Agent Management, Media Evidence Service, Vendor Management Service |
| Subscription Management | Future Service Plans | As a product administrator, I want construction, agriculture, rental, commercial, and drone-monitoring subscription plans so that recurring services can expand by property need. | P2 | 8 | Phase 3 | Subscription Service, Service Catalog Service, Pricing Engine |
| Service Expansion | Future Partner Services | As a property owner, I want loan, insurance, solar, EV charging, smart-home, and WhatsApp-based services available through the catalog so that additional property needs can be fulfilled. | P3 | 13 | Phase 3 | Marketplace Service, Partner Integrations, Service Catalog Service |

# Newly Added Epics

| Epic | Scope | Priority |
|---|---|---|
| Customer Support and Trust | Customer support, complaints, protected communication, and relationship continuity. | P1 |
| Pricing Administration | Configurable pricing rules, travel/allowance inputs, and pricing simulation. | P1 |
| Vendor Job Execution and Settlement | Vendor job acceptance, completion evidence, invoicing, payment status, and performance. | P2 |
| Subscription Engagement | Customer plan comparison, self-service renewal, entitlement visibility, and subscription KPIs. | P1 |

# Newly Added User Stories

| Epic | Feature | User Story | Priority | Story Points | Release | Dependencies |
|---|---|---|---|---:|---|---|
| Customer Experience | OTP and Password Recovery | As a customer, I want to verify my mobile number by OTP and recover my password so that I can regain secure access to my account. | P0 | 5 | MVP | Auth Service, SMS Provider, User Service |
| Customer Experience | Property Owner Dashboard | As a property owner, I want a dashboard with my property summary, recent reports, pending services, and notifications so that I can understand property status at a glance. | P0 | 8 | MVP | Property Service, Reporting Service, Notification Service |
| Customer Support and Trust | Complaint and Knowledge Base | As a customer, I want to raise, track, and review a complaint and access support guidance so that service issues can be resolved transparently. | P1 | 8 | Phase 2 | Complaint Service, Notification Service, Knowledge Base |
| Customer Support and Trust | Protected Communication History | As a marketplace or service customer, I want PropertyPilot-mediated communications to be logged while direct contact details remain masked so that interactions are protected and traceable. | P2 | 8 | Phase 3 | Lead Protection Service, Communication Service, Audit Service |
| Pricing Administration | Pricing Rule Configuration and Simulation | As an administrator, I want to configure pricing formula inputs and simulate a price so that service estimates remain accurate without code changes. | P1 | 8 | Phase 2 | Pricing Engine, Admin Portal, Audit Service |
| Subscription Engagement | Customer Plan Comparison and Self-Service Renewal | As a property owner, I want to compare plan inclusions and renew my subscription from the app so that I can retain the right monitoring coverage. | P1 | 5 | Phase 2 | Subscription Service, Pricing Engine, Payment Gateway |
| Subscription Engagement | Subscription KPI Dashboard | As an administrator, I want to view MRR, ARR, renewal rate, churn, ARPU, and CLV so that I can manage subscription performance. | P1 | 5 | Phase 2 | Subscription Service, Reporting Service |
| Property Monitoring | Alert-Driven Remediation Approval | As a property owner, I want to approve a recommended service after a monitoring issue is detected so that remediation can begin without losing context. | P1 | 5 | Phase 2 | Monitoring Service, Service Request Service, Payment Gateway |
| Vendor Job Execution and Settlement | Vendor Job Acceptance and Completion | As a vendor, I want to accept an assigned job, submit completion evidence, and update the job status so that my work can be reviewed and closed. | P2 | 8 | Phase 3 | Vendor Management Service, Media Evidence Service, Workflow Engine |
| Vendor Job Execution and Settlement | Vendor Invoice and Payment Status | As a vendor, I want to submit an invoice and view payment status for completed work so that I can manage settlement. | P2 | 5 | Phase 3 | Vendor Management Service, Billing Service, Payment Service |
| Platform Administration | Partner Onboarding | As an administrator, I want to onboard partners and map their supported services so that approved external capacity can be used in operations. | P1 | 5 | Phase 2 | Partner Management Service, Service Catalog Service, Audit Service |

# Newly Added Technical Tasks

| Epic | Technical Task | Priority | Dependencies |
|---|---|---|---|
| Customer Experience | Implement OTP request/verification, password-recovery tokens, expiry, retry limits, and account-access audit events. | P0 | Auth Service, SMS/Email Provider, Audit Service |
| Customer Experience | Build a customer property-dashboard read model aggregating property, report, service-request, and notification status. | P0 | Property Service, Reporting Service, Notification Service |
| Service Management | Implement the service-request lifecycle states used by booking, payment, assignment, execution, report delivery, cancellation, and rating flows. | P0 | Service Workflow, Payment Gateway, Assignment Service |
| Field Operations | Implement offline-safe field capture with queued GPS, photo, video, observation, and report synchronisation plus duplicate-submission protection. | P1 | Agent App, Media Evidence Service, Geo-location Service |
| Customer Support and Trust | Implement complaint case, comment, escalation, and customer-visible status data flows for the support screens. | P1 | Complaint Service, Notification Service, Audit Service |
| Pricing Administration | Implement versioned pricing-rule configuration, effective dates, travel/allowance inputs, and an auditable pricing-simulation workflow. | P1 | Pricing Engine, Admin Portal, Audit Service |
| Subscription Engagement | Implement plan-comparison, renewal reminder, entitlement, visit-consumption, pause, and expiry processing aligned to plan policy. | P1 | Subscription Service, Notification Service, Billing Service |
| Property Monitoring | Implement alert-to-service-request linking so an approved remediation request retains its source alert, evidence, and recommendation. | P1 | Monitoring Service, Service Request Service, Media Evidence Service |
| Vendor Job Execution and Settlement | Implement vendor job state transitions, completion-evidence validation, invoice submission, and settlement-status synchronisation. | P2 | Vendor Management Service, Media Evidence Service, Billing Service |
| Customer Support and Trust | Implement contact masking and auditable mediated-message routing for protected buyer-seller and customer-vendor interactions. | P2 | Lead Protection Service, Communication Service, Audit Service |
