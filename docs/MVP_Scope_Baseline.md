````markdown
# MVP Scope Baseline

Document Type: Product Scope Baseline  
Version: 1.0  
Date: 2026-08-31  
Approval Date: 2026-08-31  
Baseline Owner: Product Owner  
Status: Approved for MVP Baseline

---

# MVP Objectives

## Business Goals
- Launch a production-ready property services platform focused on core customer acquisition, property onboarding, service fulfillment, and recurring revenue.
- Establish a stable, measurable operating foundation for customer support, agent workflows, and service execution.
- Deliver a monetizable MVP with subscription, payment, and operational capability.
- Enable rapid future expansion without reworking the core product model.

## Customer Goals
- Register quickly and complete onboarding with minimal friction.
- Discover and manage property listings with trust and clarity.
- Book and track service requests with visibility and accountability.
- Receive timely updates and notifications for key events.
- Access a simple, mobile-first experience for service and account actions.

## Operational Goals
- Support a manageable end-to-end workflow for customer, agent, and operations teams.
- Provide clear ownership, assignment, and status tracking.
- Enable operational monitoring, alerting, and exception handling from day one.
- Maintain security, privacy, and compliance baselines at MVP launch.

## Revenue Goals
- Enable recurring subscription revenue through plan-based engagement.
- Support direct payment and invoice-backed transactions.
- Provide a clean foundation for upsell, premium features, and additional service monetization in later phases.
- Reduce churn risk by creating a clear customer lifecycle and support model.

---

# MVP Success Criteria

## Customer Acquisition
- MVP supports rapid onboarding of customers and property owners.
- Customer registration and OTP-based login are production-ready.
- Property registration and profile setup are operationally stable.

## Property Registrations
- Users can create and manage property records with essential ownership and verification metadata.
- Required property data is captured and available to internal operations teams.

## Service Requests
- Customers can submit service requests and track them through lifecycle stages.
- Agents can accept or be assigned work and record progress.
- Support teams can access status updates and evidence visibility.

## Subscriptions
- Plans can be created and managed through the subscription lifecycle.
- Auto-renewal and billing states are validated.
- Upgrade, downgrade, pause, and cancel flows are available.

## Revenue Targets
- Basic monetization flows are functional and measured.
- Payment capture and invoice generation are available.
- Subscription activation and payment confirmation are validated.

## Operational KPIs
- Service request completion and agent assignment are measurable.
- Support and operations can monitor workflow health and exceptions.
- Basic analytics and reporting provide operational insight.

---

# MVP Functional Scope

## Included Features

### Customer Registration
- customer onboarding
- OTP-based login and authentication
- profile creation and basic account management

### Customer Login (OTP)
- OTP generation and verification
- secure session handling
- login state and access control

### Property Registration
- new property capture
- property status and ownership metadata
- basic property information and listing lifecycle

### Property Management
- property profile maintenance
- ownership visibility
- basic property record updates

### GPS Verification
- location validation for property and service context
- verification of service delivery and agent presence when applicable

### Ownership Verification
- owner identity and property relationship validation
- ownership metadata for approvals and downstream workflows

### Service Booking
- customer submits service request
- basic service category and priority handling
- booking intake and validation

### Service Tracking
- service request lifecycle tracking
- progress updates
- status visibility to customer and operations

### Reports
- basic reporting and export capability for operational usage
- essential summary reports for support and management

### Evidence Viewing
- upload and review of service evidence
- availability of evidence within request workflows

### Subscriptions
- service plan selection
- lifecycle states
- billing and renewal states
- pause/resume/cancel flows

### Payments
- payment capture
- invoice generation
- simple refund initiation flow
- reconciliation support for core transactions

### Agent Assignment
- assignment of tasks to agents
- queue visibility
- operational assignment and reassignment workflow

### Agent Visit Execution
- scheduling and execution of assigned visits
- field activity capture
- visit completion and evidence update

### Notifications
- customer and operational notifications for key events
- delivery status tracking for core notifications
- preference management for communication channels

### Complaints
- customer complaint intake
- complaint assignment and status tracking
- basic issue workflow

### Admin Management
- admin access to core operational entities
- user and support administration
- key configuration and oversight functions

### Operations Management
- property and service operations controls
- service assignment and workflow coordination
- monitoring and exception handling support

### Basic Analytics
- operational reporting for service usage, requests, subscriptions, and payment flow
- KPI reporting sufficient for early launch decisions

---

## Deferred Features

The following items are explicitly deferred beyond MVP and are not part of the approved MVP baseline:

- Rental Management
- Construction Monitoring
- Vendor Marketplace
- AI Risk Analysis
- Drone Automation
- Advanced Analytics
- Relationship Manager Chat
- Multi-Language Support
- Referral Program
- Coupon Engine Enhancements
- Future Marketplace Services

These items may be evaluated for subsequent release phases but must not be treated as part of the MVP baseline.

---

# MVP Customer Journeys

## Supported Journeys
- Customer registration and OTP login
- Property discovery and property onboarding
- Property ownership and property profile management
- Service booking and service request lifecycle tracking
- Agent assignment and agent visit execution
- Subscription selection and payment flow
- Billing and invoice review
- Notification viewing and preference updates
- Complaint submission and tracking
- Privacy data request handling
- Basic admin and operations workflow completion

## Excluded Journeys
- advanced marketplace buying and negotiation journeys
- large-scale project and construction monitoring journeys
- advanced AI-assisted risk or recommendation journeys
- multilingual user journeys
- referral-based acquisition journeys
- advanced relationship management and conversational support flows
- drone-enabled inspection and automation flows

---

# MVP Screen Scope

## Customer App Screens
- welcome and login
- OTP verification
- customer profile
- property registration
- property listing and details
- service request creation
- service request detail and status
- subscription plans
- payment and invoice
- notification center
- notification preferences
- privacy center
- complaint intake

## Agent App Screens
- agent login
- assigned work list
- service request detail
- visit execution and evidence capture
- status update
- complaint handling
- notification center

## Operations Screens
- service queue
- assignment management
- property operations dashboard
- support issue and complaint queue
- billing and reconciliation dashboard
- report dashboard
- notification and event status view

## Admin Screens
- customer management
- property management
- user access and role management
- configuration and operational controls
- system reports and summary views

## Total Screen Count
- Baseline count is maintained in Screen_Catalog.md and must be treated as the approved MVP screen inventory.
- The MVP baseline includes only screens required for the approved business and operational journeys.
- Screens outside the approved set are not considered part of MVP.

---

# MVP API Scope

## Included APIs
The MVP baseline includes APIs required for:
- customer onboarding and authentication
- property registration and management
- service request creation and status tracking
- assignment and operational workflows
- subscription creation and lifecycle management
- payment capture, invoice access, and refunds
- notification status and preference handling
- complaint and support handling
- privacy request handling
- basic reporting and data export
- webhook callbacks for provider integration

## Deferred APIs
- advanced marketplace APIs
- construction lifecycle APIs
- AI risk and recommendation APIs
- advanced analytics export APIs
- future multi-language or localization APIs
- extended referral and coupon functionality APIs

## Future APIs
- vendor marketplace management APIs
- drone and sensor-driven inspection APIs
- relationship manager and chat integration APIs
- advanced operational dashboards and machine learning endpoints

---

# MVP Database Scope

## Core Tables
The MVP baseline includes the core logical and physical data models required for:
- customer and account data
- property and ownership data
- service request and lifecycle data
- subscription and billing data
- payment and invoice records
- notifications and delivery tracking
- KYC records and review state
- privacy request records
- complaint and support state
- operational assignment data
- report and export job data

## Deferred Tables
- advanced marketplace entities
- construction-specific project and milestone structures beyond core MVP
- advanced AI or fraud scoring tables
- complex referral and incentive tables
- extended analytics warehouses

## Future Tables
- additional features added in later release waves
- future multi-tenant operational expansion models
- advanced marketplace and ecosystem data models
- predictive analytics and recommendation data stores

---

# MVP Integrations

## Payment Gateway
- required for payment capture and invoice validation
- supports core billing and transaction processing for MVP

## SMS
- required for OTP, alerts, and key operational notifications
- included in MVP notification and authentication flows

## Email
- required for notifications, support updates, and message delivery
- included in MVP standard communication flows

## WhatsApp
- required for critical notifications and customer messaging
- included in MVP operational notification set

## Maps
- required for location validation and service-related routing
- included in service execution and GPS verification workflows

## Object Storage
- required for evidence upload and retention
- included in support and service evidence flows

---

# MVP Security Scope

## Authentication
- OTP-based login for customers
- role-based access for agents, admin, and operations users
- secure session management

## Authorization
- role separation across customer, agent, operations, admin, and support
- permission check for sensitive operational and customer data
- segregation of access by operational responsibility

## Encryption
- encryption in transit and at rest for sensitive data
- secure handling of credentials, tokens, and stored customer information
- secure handling of KYC related documents and sensitive references

## Audit Logging
- action-level audit trail for key business and administrative events
- traceability for updates to customer, property, payment, and privacy data

## Monitoring
- production health monitoring
- authentication and authorization monitoring
- API and dependency monitoring
- operational alerting for customer-facing workflows

---

# MVP Non-Functional Requirements

## Availability
- production services must meet the minimum service continuity expectation for all MVP business flows
- critical workflows must remain available during launch and hypercare

## Performance
- customer-facing flows must perform within target thresholds for real use
- API and screen interactions must be responsive for core user journeys

## Scalability
- services must support expected user and transaction volumes for MVP launch
- growth assumptions must be validated against deployment and operational design

## Security
- secure authentication, authorization, data handling, and logging
- compliance with accepted MVP security controls

## Compliance
- privacy handling and audit logging must meet the agreed data and privacy baseline
- production controls must support legal and compliance obligations

---

# Out of Scope

The following are explicitly out of scope for the MVP baseline:
- rental management
- construction monitoring and project lifecycle management beyond basic operational support
- vendor marketplace and third-party service matching
- AI risk analysis and predictive intelligence
- drone automation or remote inspection technology
- advanced analytics and operational intelligence
- relationship manager chat and conversational customer support
- multi-language interface support
- referral programs and coupon engine enhancements
- advanced marketplace features and service ecosystem expansion
- long-term strategic product features not required for the initial operating release

If a future requirement is introduced, it must be evaluated through the scope change process and linked to a release phase.

---

# Scope Change Governance

## How New Requirements Are Evaluated
New requirements must be reviewed against:
- business value
- customer impact
- operational complexity
- technical dependency and architecture impact
- security and privacy impact
- data model impact
- API and integration impact
- release timing and risk

## Approval Workflow
1. requirement raised by business, product, or operations
2. impact analysis completed
3. product and architecture review completed
4. security and privacy review if required
5. release impact and dependency assessment completed
6. approval by Product Owner and Architecture or governance approver
7. release planning update if accepted

## Versioning Process
- MVP baseline version must be maintained in the product baseline
- any scope change requires a version increment
- major scope additions beyond MVP are tracked as release-based change items
- changes are documented and linked to requirement traceability

---

# MVP Acceptance Criteria

## Business Acceptance
- business value is aligned to launch goals
- key customer journeys are supported
- monetization and core service flows function
- operational support can manage the release

## Product Acceptance
- all MVP features are implemented and traceable
- all included screens and workflows are available
- customer and business workflows meet approved acceptance criteria

## Architecture Acceptance
- architecture supports the approved MVP service model
- APIs, integrations, and database model align to the release baseline
- performance and resilience assumptions are acceptable

## Operations Acceptance
- support model is ready
- monitoring and alerting are active
- rollback and hypercare are prepared
- operational ownership is assigned

## Security Acceptance
- authentication, authorization, privacy, encryption, and logging controls are validated
- security and risk review signoff is recorded

---

# Future Releases

## Phase 2
- advanced marketplace capability
- expanded service catalog and vendor operations
- deeper analytics and management dashboards
- higher automation and workflow orchestration

## Phase 3
- advanced customer support and relationship workflows
- broader property and construction lifecycle support
- AI-assisted operational decisions and risk analysis
- extended multi-channel and multilingual support

## Long-Term Vision
- establish PropertyPilot as a full property services and operations platform
- expand into broader service, marketplace, and operational ecosystems
- deepen analytics, automation, and intelligent service management
- scale to broader customer and operational coverage while preserving the controlled core architecture

---

## Summary

This document defines the official MVP scope baseline for PropertyPilot. It is the approved product boundary for the initial release and the authoritative baseline against which all future enhancements will be measured.

Any future requirement, feature, or workflow outside this baseline must be explicitly approved through the scope change governance process and associated with the corresponding future release stage. The MVP baseline is intentionally constrained to the essentials required for customer acquisition, service fulfillment, subscription monetization, operational readiness, and launch stability.
```// filepath: c:\PropertyPilot\docs\MVP_Scope_Baseline.md
# MVP Scope Baseline

Document Type: Product Scope Baseline  
Version: 1.0  
Date: 2026-08-31  
Approval Date: 2026-08-31  
Baseline Owner: Product Owner  
Status: Approved for MVP Baseline

---

# MVP Objectives

## Business Goals
- Launch a production-ready property services platform focused on core customer acquisition, property onboarding, service fulfillment, and recurring revenue.
- Establish a stable, measurable operating foundation for customer support, agent workflows, and service execution.
- Deliver a monetizable MVP with subscription, payment, and operational capability.
- Enable rapid future expansion without reworking the core product model.

## Customer Goals
- Register quickly and complete onboarding with minimal friction.
- Discover and manage property listings with trust and clarity.
- Book and track service requests with visibility and accountability.
- Receive timely updates and notifications for key events.
- Access a simple, mobile-first experience for service and account actions.

## Operational Goals
- Support a manageable end-to-end workflow for customer, agent, and operations teams.
- Provide clear ownership, assignment, and status tracking.
- Enable operational monitoring, alerting, and exception handling from day one.
- Maintain security, privacy, and compliance baselines at MVP launch.

## Revenue Goals
- Enable recurring subscription revenue through plan-based engagement.
- Support direct payment and invoice-backed transactions.
- Provide a clean foundation for upsell, premium features, and additional service monetization in later phases.
- Reduce churn risk by creating a clear customer lifecycle and support model.

---

# MVP Success Criteria

## Customer Acquisition
- MVP supports rapid onboarding of customers and property owners.
- Customer registration and OTP-based login are production-ready.
- Property registration and profile setup are operationally stable.

## Property Registrations
- Users can create and manage property records with essential ownership and verification metadata.
- Required property data is captured and available to internal operations teams.

## Service Requests
- Customers can submit service requests and track them through lifecycle stages.
- Agents can accept or be assigned work and record progress.
- Support teams can access status updates and evidence visibility.

## Subscriptions
- Plans can be created and managed through the subscription lifecycle.
- Auto-renewal and billing states are validated.
- Upgrade, downgrade, pause, and cancel flows are available.

## Revenue Targets
- Basic monetization flows are functional and measured.
- Payment capture and invoice generation are available.
- Subscription activation and payment confirmation are validated.

## Operational KPIs
- Service request completion and agent assignment are measurable.
- Support and operations can monitor workflow health and exceptions.
- Basic analytics and reporting provide operational insight.

---

# MVP Functional Scope

## Included Features

### Customer Registration
- customer onboarding
- OTP-based login and authentication
- profile creation and basic account management

### Customer Login (OTP)
- OTP generation and verification
- secure session handling
- login state and access control

### Property Registration
- new property capture
- property status and ownership metadata
- basic property information and listing lifecycle

### Property Management
- property profile maintenance
- ownership visibility
- basic property record updates

### GPS Verification
- location validation for property and service context
- verification of service delivery and agent presence when applicable

### Ownership Verification
- owner identity and property relationship validation
- ownership metadata for approvals and downstream workflows

### Service Booking
- customer submits service request
- basic service category and priority handling
- booking intake and validation

### Service Tracking
- service request lifecycle tracking
- progress updates
- status visibility to customer and operations

### Reports
- basic reporting and export capability for operational usage
- essential summary reports for support and management

### Evidence Viewing
- upload and review of service evidence
- availability of evidence within request workflows

### Subscriptions
- service plan selection
- lifecycle states
- billing and renewal states
- pause/resume/cancel flows

### Payments
- payment capture
- invoice generation
- simple refund initiation flow
- reconciliation support for core transactions

### Agent Assignment
- assignment of tasks to agents
- queue visibility
- operational assignment and reassignment workflow

### Agent Visit Execution
- scheduling and execution of assigned visits
- field activity capture
- visit completion and evidence update

### Notifications
- customer and operational notifications for key events
- delivery status tracking for core notifications
- preference management for communication channels

### Complaints
- customer complaint intake
- complaint assignment and status tracking
- basic issue workflow

### Admin Management
- admin access to core operational entities
- user and support administration
- key configuration and oversight functions

### Operations Management
- property and service operations controls
- service assignment and workflow coordination
- monitoring and exception handling support

### Basic Analytics
- operational reporting for service usage, requests, subscriptions, and payment flow
- KPI reporting sufficient for early launch decisions

---

## Deferred Features

The following items are explicitly deferred beyond MVP and are not part of the approved MVP baseline:

- Rental Management
- Construction Monitoring
- Vendor Marketplace
- AI Risk Analysis
- Drone Automation
- Advanced Analytics
- Relationship Manager Chat
- Multi-Language Support
- Referral Program
- Coupon Engine Enhancements
- Future Marketplace Services

These items may be evaluated for subsequent release phases but must not be treated as part of the MVP baseline.

---

# MVP Customer Journeys

## Supported Journeys
- Customer registration and OTP login
- Property discovery and property onboarding
- Property ownership and property profile management
- Service booking and service request lifecycle tracking
- Agent assignment and agent visit execution
- Subscription selection and payment flow
- Billing and invoice review
- Notification viewing and preference updates
- Complaint submission and tracking
- Privacy data request handling
- Basic admin and operations workflow completion

## Excluded Journeys
- advanced marketplace buying and negotiation journeys
- large-scale project and construction monitoring journeys
- advanced AI-assisted risk or recommendation journeys
- multilingual user journeys
- referral-based acquisition journeys
- advanced relationship management and conversational support flows
- drone-enabled inspection and automation flows

---

# MVP Screen Scope

## Customer App Screens
- welcome and login
- OTP verification
- customer profile
- property registration
- property listing and details
- service request creation
- service request detail and status
- subscription plans
- payment and invoice
- notification center
- notification preferences
- privacy center
- complaint intake

## Agent App Screens
- agent login
- assigned work list
- service request detail
- visit execution and evidence capture
- status update
- complaint handling
- notification center

## Operations Screens
- service queue
- assignment management
- property operations dashboard
- support issue and complaint queue
- billing and reconciliation dashboard
- report dashboard
- notification and event status view

## Admin Screens
- customer management
- property management
- user access and role management
- configuration and operational controls
- system reports and summary views

## Total Screen Count
- Baseline count is maintained in Screen_Catalog.md and must be treated as the approved MVP screen inventory.
- The MVP baseline includes only screens required for the approved business and operational journeys.
- Screens outside the approved set are not considered part of MVP.

---

# MVP API Scope

## Included APIs
The MVP baseline includes APIs required for:
- customer onboarding and authentication
- property registration and management
- service request creation and status tracking
- assignment and operational workflows
- subscription creation and lifecycle management
- payment capture, invoice access, and refunds
- notification status and preference handling
- complaint and support handling
- privacy request handling
- basic reporting and data export
- webhook callbacks for provider integration

## Deferred APIs
- advanced marketplace APIs
- construction lifecycle APIs
- AI risk and recommendation APIs
- advanced analytics export APIs
- future multi-language or localization APIs
- extended referral and coupon functionality APIs

## Future APIs
- vendor marketplace management APIs
- drone and sensor-driven inspection APIs
- relationship manager and chat integration APIs
- advanced operational dashboards and machine learning endpoints

---

# MVP Database Scope

## Core Tables
The MVP baseline includes the core logical and physical data models required for:
- customer and account data
- property and ownership data
- service request and lifecycle data
- subscription and billing data
- payment and invoice records
- notifications and delivery tracking
- KYC records and review state
- privacy request records
- complaint and support state
- operational assignment data
- report and export job data

## Deferred Tables
- advanced marketplace entities
- construction-specific project and milestone structures beyond core MVP
- advanced AI or fraud scoring tables
- complex referral and incentive tables
- extended analytics warehouses

## Future Tables
- additional features added in later release waves
- future multi-tenant operational expansion models
- advanced marketplace and ecosystem data models
- predictive analytics and recommendation data stores

---

# MVP Integrations

## Payment Gateway
- required for payment capture and invoice validation
- supports core billing and transaction processing for MVP

## SMS
- required for OTP, alerts, and key operational notifications
- included in MVP notification and authentication flows

## Email
- required for notifications, support updates, and message delivery
- included in MVP standard communication flows

## WhatsApp
- required for critical notifications and customer messaging
- included in MVP operational notification set

## Maps
- required for location validation and service-related routing
- included in service execution and GPS verification workflows

## Object Storage
- required for evidence upload and retention
- included in support and service evidence flows

---

# MVP Security Scope

## Authentication
- OTP-based login for customers
- role-based access for agents, admin, and operations users
- secure session management

## Authorization
- role separation across customer, agent, operations, admin, and support
- permission check for sensitive operational and customer data
- segregation of access by operational responsibility

## Encryption
- encryption in transit and at rest for sensitive data
- secure handling of credentials, tokens, and stored customer information
- secure handling of KYC related documents and sensitive references

## Audit Logging
- action-level audit trail for key business and administrative events
- traceability for updates to customer, property, payment, and privacy data

## Monitoring
- production health monitoring
- authentication and authorization monitoring
- API and dependency monitoring
- operational alerting for customer-facing workflows

---

# MVP Non-Functional Requirements

## Availability
- production services must meet the minimum service continuity expectation for all MVP business flows
- critical workflows must remain available during launch and hypercare

## Performance
- customer-facing flows must perform within target thresholds for real use
- API and screen interactions must be responsive for core user journeys

## Scalability
- services must support expected user and transaction volumes for MVP launch
- growth assumptions must be validated against deployment and operational design

## Security
- secure authentication, authorization, data handling, and logging
- compliance with accepted MVP security controls

## Compliance
- privacy handling and audit logging must meet the agreed data and privacy baseline
- production controls must support legal and compliance obligations

---

# Out of Scope

The following are explicitly out of scope for the MVP baseline:
- rental management
- construction monitoring and project lifecycle management beyond basic operational support
- vendor marketplace and third-party service matching
- AI risk analysis and predictive intelligence
- drone automation or remote inspection technology
- advanced analytics and operational intelligence
- relationship manager chat and conversational customer support
- multi-language interface support
- referral programs and coupon engine enhancements
- advanced marketplace features and service ecosystem expansion
- long-term strategic product features not required for the initial operating release

If a future requirement is introduced, it must be evaluated through the scope change process and linked to a release phase.

---

# Scope Change Governance

## How New Requirements Are Evaluated
New requirements must be reviewed against:
- business value
- customer impact
- operational complexity
- technical dependency and architecture impact
- security and privacy impact
- data model impact
- API and integration impact
- release timing and risk

## Approval Workflow
1. requirement raised by business, product, or operations
2. impact analysis completed
3. product and architecture review completed
4. security and privacy review if required
5. release impact and dependency assessment completed
6. approval by Product Owner and Architecture or governance approver
7. release planning update if accepted

## Versioning Process
- MVP baseline version must be maintained in the product baseline
- any scope change requires a version increment
- major scope additions beyond MVP are tracked as release-based change items
- changes are documented and linked to requirement traceability

---

# MVP Acceptance Criteria

## Business Acceptance
- business value is aligned to launch goals
- key customer journeys are supported
- monetization and core service flows function
- operational support can manage the release

## Product Acceptance
- all MVP features are implemented and traceable
- all included screens and workflows are available
- customer and business workflows meet approved acceptance criteria

## Architecture Acceptance
- architecture supports the approved MVP service model
- APIs, integrations, and database model align to the release baseline
- performance and resilience assumptions are acceptable

## Operations Acceptance
- support model is ready
- monitoring and alerting are active
- rollback and hypercare are prepared
- operational ownership is assigned

## Security Acceptance
- authentication, authorization, privacy, encryption, and logging controls are validated
- security and risk review signoff is recorded

---

# Future Releases

## Phase 2
- advanced marketplace capability
- expanded service catalog and vendor operations
- deeper analytics and management dashboards
- higher automation and workflow orchestration

## Phase 3
- advanced customer support and relationship workflows
- broader property and construction lifecycle support
- AI-assisted operational decisions and risk analysis
- extended multi-channel and multilingual support

## Long-Term Vision
- establish PropertyPilot as a full property services and operations platform
- expand into broader service, marketplace, and operational ecosystems
- deepen analytics, automation, and intelligent service management
- scale to broader customer and operational coverage while preserving the controlled core architecture

---

## Summary

This document defines the official MVP scope baseline for PropertyPilot. It is the approved product boundary for the initial release and the authoritative baseline against which all future enhancements will be measured.

Any future requirement, feature, or workflow outside this baseline must be explicitly approved through the scope change governance process and associated with the corresponding future release stage. The MVP baseline is intentionally constrained to the essentials required for customer acquisition, service fulfillment, subscription monetization, operational readiness, and launch stability.
