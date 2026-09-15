# PropertyPilot Software Requirements Specification

Document Type: Consolidated Software Requirements Specification  
Version: 2.0  
Status: Source-of-truth consolidation  
Date: 2026-08-31

---

## 1. Purpose

This SRS consolidates the requirements already defined in the PropertyPilot product documents. It establishes requirement-level traceability while referring to the detailed catalogs, journeys, screens, flows, plans, data model, and vendor specification instead of repeating them.

No feature, business rule, price, workflow, data entity, screen, or integration is introduced by this document unless it is defined in a referenced source.

## 2. Source-of-Truth and Precedence

| Source | Governs |
|---|---|
| [Feature Catalog](01_Business/Feature_Catalog.md) | Product features, priority, and phased scope. |
| [Service Catalog](02_Product/Service_Catalog.md) | Service definitions, categories, execution models, eligibility, deliverables, evidence, SLA, and service lifecycle. |
| [Customer Journeys](01_Business/Customer_Journeys.md) | Customer, agent, vendor, NRI, marketplace, payment, and monitoring journey outcomes. |
| [Subscription Plans](01_Business/Subscription_Plans.md) | Plan names, entitlements, plan rules, add-ons, renewal, pause, cancellation, refund, and subscription KPIs. |
| [Screen Catalog](09_Diagrams/Screen_Catalog.md) | Required channels, screens, user-facing capabilities, and role access points. |
| [Screen Flows](09_Diagrams/Screen_Flows.md) | Screen-level workflow sequences and cross-cutting payment/notification flows. |
| [Database Physical Model](04_Data/Database_Physical_Model.md) | Physical data entities, audit columns, indexes, partitioning, retention, and database technology. |
| [Vendor Management](01_Business/Vendor_Management.md) | Vendor lifecycle, verification, assignment, quotation, job, payment, SLA, marketplace, and control requirements. |

`Pricing_Strategy.md` was requested as a source but is not present in this workspace. Consequently, this SRS includes no pricing rules attributed to that absent document. Pricing requirements are limited to the dynamic-pricing capability in Feature Catalog and the pricing principle in Subscription Plans.

When referenced sources differ, Feature Catalog governs release scope; Service Catalog governs service behaviour; Subscription Plans govern plan entitlements and policy; Screen Catalog/Flows govern user experience; and Database Physical Model governs persisted entities.

## 3. Product Scope

PropertyPilot shall provide a property-service platform for customer property management, verification, monitoring, service booking and execution, subscriptions, payments, reporting, field-agent operations, vendor delivery, NRI services, marketplace capabilities, notifications, administration, and analytics.

The detailed feature inventory and priority definitions are maintained in [Feature Catalog](01_Business/Feature_Catalog.md). Service availability, property applicability, and execution models are maintained in [Service Catalog](02_Product/Service_Catalog.md).

## 4. Users and Channels

| User or channel | Required capabilities | Source |
|---|---|---|
| Public website visitor | Service-cost calculation, plan comparison, service discovery, registration, and public marketplace discovery. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md) |
| Customer/property owner | Profile, property, shared ownership, service, report, evidence, subscription, payment, support, and marketplace functions. | [Screen Catalog](09_Diagrams/Screen_Catalog.md) |
| NRI customer | Remote property monitoring, video reports, relationship-manager communication, and emergency alerts. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Subscription Plans](01_Business/Subscription_Plans.md) |
| Field agent | Task execution, GPS, photo/video/observation capture, evidence submission, and report submission. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md) |
| Vendor | Assigned jobs, job acceptance, completion evidence, invoice submission, payments, and performance. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Vendor Management](01_Business/Vendor_Management.md) |
| Operations user | Service requests, assignment, visit monitoring, report review, complaint resolution, and escalations. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Administrator | Users, roles, agents, partners, vendors, services, subscriptions, pricing, payments, marketplace, audit, and analytics administration. | [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Vendor Management](01_Business/Vendor_Management.md) |

## 5. Functional Requirements

### 5.1 Customer, Lead, and Property

- FR-01: The system shall support customer registration, authentication, KYC verification, profile management, and communication preferences. [Feature Catalog](01_Business/Feature_Catalog.md)
- FR-02: The system shall support property registration, search, details, photos, documents, status tracking, and multiple-property management. [Feature Catalog](01_Business/Feature_Catalog.md)
- FR-03: The system shall support shared property ownership, including co-owner invitation, ownership approval, and property access permissions. [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md)
- FR-04: The system shall capture, qualify, assign, track, protect, and convert leads according to the lead-management and marketplace requirements. [Feature Catalog](01_Business/Feature_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md)

### 5.2 Service, Verification, and Monitoring

- FR-05: The system shall expose the Service Catalog and support service booking, assignment, tracking, completion, cancellation, rating, eligibility validation, configurable SLA, deliverables, evidence, and auditability. [Service Catalog](02_Product/Service_Catalog.md), [Feature Catalog](01_Business/Feature_Catalog.md)
- FR-06: The system shall support the verification, inspection, monitoring, coordination, execution, rental, construction, security, NRI, premium, and future AI-assisted service categories defined in Service Catalog. [Service Catalog](02_Product/Service_Catalog.md)
- FR-07: The system shall support GPS verification, ownership verification, monitoring requests, scheduled visits, GPS tracking, photo uploads, monitoring reports, alert notifications, issue escalation, video reports, and emergency visits according to the applicable feature priority. [Feature Catalog](01_Business/Feature_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md)
- FR-08: The system shall capture field evidence and deliver service outputs through the agent workflow, including GPS coordinates, timestamp, agent information, property information, photos, optional video, notes, summary, recommendations, and PDF report where applicable. [Service Catalog](02_Product/Service_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md)

### 5.3 Pricing and Payment

- FR-09: The system shall provide customer cost calculation, service-cost estimation, package comparison, subscription comparison, savings calculation, distance-based pricing, travel-cost calculation, and pricing-rule management as defined in Feature Catalog. [Feature Catalog](01_Business/Feature_Catalog.md)
- FR-10: Subscription price values are placeholders; final customer pricing shall be generated dynamically using the factors specified in Subscription Plans. [Subscription Plans](01_Business/Subscription_Plans.md)
- FR-11: The system shall support online payments, invoices, payment receipts, refund processing, and subscription billing according to the Feature Catalog priority. [Feature Catalog](01_Business/Feature_Catalog.md)

### 5.4 Subscriptions

- FR-12: The system shall support Silver, Gold, Platinum, NRI Elite, and NRI Concierge plans, including their target customer, included services, monitoring frequency, evidence/reporting benefit, support level, and SLA. [Subscription Plans](01_Business/Subscription_Plans.md)
- FR-13: The system shall support plan activation after payment confirmation, optional auto renewal, manual renewal, upgrades, downgrades, pauses, cancellations, refunds, and visit-expiry/carry-forward policy. [Subscription Plans](01_Business/Subscription_Plans.md)
- FR-14: The system shall provide subscription plan, customer subscription, renewal, and comparison experiences. [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Screen Flows](09_Diagrams/Screen_Flows.md)

### 5.5 Agents and Vendors

- FR-15: The agent application shall support task assignment, GPS capture, photo upload, report submission, task acceptance, visit execution, video/observation capture, evidence submission, and emergency visits. [Feature Catalog](01_Business/Feature_Catalog.md), [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Screen Flows](09_Diagrams/Screen_Flows.md)
- FR-16: The system shall support vendor registration, verification, approval, category/service mapping, coverage, availability, capacity, assignment, quotation, job execution, completion evidence, rating, payment, commission where configured, SLA, and audit controls. [Vendor Management](01_Business/Vendor_Management.md)
- FR-17: The vendor portal shall support job assignment and acceptance, job details, completion evidence, invoice submission, payment dashboard, and vendor performance. [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Vendor Management](01_Business/Vendor_Management.md)

### 5.6 Marketplace, NRI, Support, and Reporting

- FR-18: The system shall support buy, sell, and rental marketplace journeys, including property search/listing, enquiry, lead tracking, protected buyer-seller interaction, matching, and commission tracking according to Feature Catalog priorities. [Feature Catalog](01_Business/Feature_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md)
- FR-19: The system shall mask contact details and mediate protected buyer-seller and customer-vendor communication according to documented protection rules. [Customer Journeys](01_Business/Customer_Journeys.md)
- FR-20: The system shall support NRI registration, dashboards, remote monitoring, video walkthroughs, WhatsApp updates, emergency checks, property-care management, and relationship-manager functions according to Feature Catalog priorities and plan entitlements. [Feature Catalog](01_Business/Feature_Catalog.md), [Subscription Plans](01_Business/Subscription_Plans.md), [Screen Catalog](09_Diagrams/Screen_Catalog.md)
- FR-21: The system shall support support dashboard, complaint creation/detail, FAQ/knowledge-base access, and complaint/escalation flows represented in screens and flows. [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Screen Flows](09_Diagrams/Screen_Flows.md)
- FR-22: The system shall provide verification, monitoring, property-summary, revenue, service, customer, agent, vendor, and marketplace reports/dashboards as prioritised in Feature Catalog and exposed in Screen Catalog. [Feature Catalog](01_Business/Feature_Catalog.md), [Screen Catalog](09_Diagrams/Screen_Catalog.md)

### 5.7 Notifications and Administration

- FR-23: The system shall support SMS, email, WhatsApp, and push notifications, including reminders, escalation alerts, delivery tracking, and customer notification preferences according to feature priority. [Feature Catalog](01_Business/Feature_Catalog.md), [Customer Journeys](01_Business/Customer_Journeys.md)
- FR-24: The system shall provide administration for users, roles, agents, partners, vendors, services, subscriptions, pricing, coupons, revenue, payments, analytics, audit logs, and system configuration. [Screen Catalog](09_Diagrams/Screen_Catalog.md)

## 6. User Experience and Workflow Requirements

Screen Catalog is the authoritative inventory of required screens. Screen Flows is the authoritative sequence for customer registration/login, property registration, verification booking, service tracking, reports, subscription, complaints, NRI, marketplace, construction, plot cleaning, shared ownership, pricing, agent field work, operations processing, administration, vendor delivery, payment, and notifications.

| Journey outcome | Authoritative reference |
|---|---|
| Guest cost estimate and plan comparison | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Customer/property registration and ownership | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| GPS and ownership verification | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Monitoring subscription and alert handling | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Service booking, agent execution, report delivery | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Vendor quotation and service delivery | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md), [Vendor Management](01_Business/Vendor_Management.md) |
| NRI and relationship-manager interaction | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |
| Marketplace and protected contact interaction | [Customer Journeys](01_Business/Customer_Journeys.md), [Screen Flows](09_Diagrams/Screen_Flows.md) |

## 7. Data Requirements

PostgreSQL is the primary transactional store, with Redis, Elasticsearch, and object storage as supporting stores. The authoritative table definitions, audit columns, indexes, partitions, retention periods, and size projections are in [Database Physical Model](04_Data/Database_Physical_Model.md).

| Domain | Required physical entities |
|---|---|
| Customer | `customers`, `customer_addresses` |
| Property | `properties`, `property_documents` |
| Service | `services`, `service_requests` |
| Agent and visit | `agents`, `agent_skills`, `visits`, `gps_captures` |
| Evidence and report | `evidence`, `reports` |
| Subscription | `subscription_plans`, `customer_subscriptions`, `subscription_renewals` |
| Payment | `payments`, `invoices`, `refunds` |
| Partner/vendor | `partners`, `partner_services`, `vendors`, `quotations`, `vendor_assignments` |
| Complaint | `complaints`, `complaint_comments` |
| Notification and audit | `notifications`, `notification_templates`, `audit_logs` |

All entities shall use the common audit columns defined in Database Physical Model. Its retention policy applies: audit logs, reports, and evidence are retained for seven years; notifications are retained for one year.

## 8. Cross-Cutting Requirements

- CR-01: Role-specific access shall be applied to the channels and functions defined in Screen Catalog, Customer Journeys, and Vendor Management. [Screen Catalog](09_Diagrams/Screen_Catalog.md), [Vendor Management](01_Business/Vendor_Management.md)
- CR-02: Service changes, pricing changes, SLA changes, eligibility changes, activation/deactivation, and vendor lifecycle/commercial actions shall be audit logged as prescribed by their source documents. [Service Catalog](02_Product/Service_Catalog.md), [Vendor Management](01_Business/Vendor_Management.md)
- CR-03: Service eligibility shall validate property type, coverage, cluster, agent/vendor availability, subscription status, customer verification, and required documentation. [Service Catalog](02_Product/Service_Catalog.md)
- CR-04: Service deliveries shall retain the required evidence and deliverables defined in Service Catalog and exposed through the documented customer/agent flows. [Service Catalog](02_Product/Service_Catalog.md), [Screen Flows](09_Diagrams/Screen_Flows.md)
- CR-05: Subscription and service policy changes shall preserve historical plan, entitlement, scheduling, and audit records. [Subscription Plans](01_Business/Subscription_Plans.md), [Database Physical Model](04_Data/Database_Physical_Model.md)

## 9. Release Scope

Release priority is defined only by [Feature Catalog](01_Business/Feature_Catalog.md):

- P0: MVP mandatory capabilities.
- P1: Phase 2 capabilities.
- P2: Future enhancement capabilities.
- P3: Long-term vision capabilities.

The Feature Catalog's explicit MVP, Phase 2, and Phase 3 sections are the release baseline. This SRS does not alter those priorities or promote a future capability into MVP.

## 10. Acceptance and Traceability

A requirement is accepted when:

1. The corresponding source feature, service, journey, screen, flow, plan rule, vendor control, or data entity is implemented without contradiction.
2. The applicable user role can complete the documented flow through the required screen/channel.
3. The required data, evidence, notification, status, and audit output is persisted or delivered as specified by its source.
4. Release acceptance respects the Feature Catalog priority.

| SRS area | Source artifacts used for acceptance |
|---|---|
| Functional scope | Feature Catalog, Service Catalog, Subscription Plans, Vendor Management |
| User journeys | Customer Journeys, Screen Flows |
| User interface | Screen Catalog, Screen Flows |
| Data | Database Physical Model |
| Pricing | Feature Catalog and Subscription Plans only, pending availability of Pricing_Strategy.md |

## 11. Related Documents

- [Feature Catalog](01_Business/Feature_Catalog.md)
- [Service Catalog](02_Product/Service_Catalog.md)
- [Customer Journeys](01_Business/Customer_Journeys.md)
- [Subscription Plans](01_Business/Subscription_Plans.md)
- [Screen Catalog](09_Diagrams/Screen_Catalog.md)
- [Screen Flows](09_Diagrams/Screen_Flows.md)
- [Database Physical Model](04_Data/Database_Physical_Model.md)
- [Vendor Management](01_Business/Vendor_Management.md)
