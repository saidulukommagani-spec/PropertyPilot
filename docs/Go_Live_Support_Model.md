# Go-Live Support Model

Document Type: Post Go-Live Operations and Support Model  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Objectives

This document defines the post go-live support operating model for PropertyPilot. It establishes how support, operations, engineering, and business teams manage incidents, customer issues, service degradation, and stabilization during the first phase after production launch.

The objectives are to:
- ensure rapid triage and resolution of production incidents
- define escalation responsibilities across support layers
- coordinate business and technical response during hypercare
- protect customer experience during the first release window
- ensure service continuity for payment, KYC, subscription, notifications, and operational workflows
- maintain knowledge, communication, and learning loops after go-live

---

## Scope

This model applies to all production support activities following go-live, including:
- customer support workflows
- service issue triage
- infrastructure and application support
- exception handling for integrations
- incident response and escalation
- hypercare stabilization
- post-incident review and continuous improvement

This model covers:
- customer journeys
- service workflows
- payments and subscriptions
- agent and field operations
- NRI considerations
- business-critical operational workflows
- enterprise support and governance

---

## Support Principles

The following principles govern the go-live support model:
- customer impact comes first
- clear ownership is mandatory for every incident
- severity and impact drive escalation
- monitoring and alerting are the primary detection mechanism
- no production incident remains without a named owner
- known issues must be documented and tracked
- service restoration is prioritized above perfect root-cause analysis at initial triage
- support runs in alignment with engineering, operations, security, and business teams
- communication is frequent and transparent during active incidents

---

# Support Organization

## Level 1 Support
Level 1 support handles:
- first-line triage
- initial case intake
- basic customer queries
- service impact classification
- coordination with Level 2 when required
- issue logging and routing

Level 1 responsibilities:
- validate customer impact
- capture relevant details
- determine if issue is supportable or engineering-owned
- check known issue and runbook guidance
- route issue to correct team
- maintain customer updates until resolution

Typical Level 1 functions:
- customer service representatives
- operations support desk
- frontline support agents
- service desk or helpdesk personnel

---

## Level 2 Support
Level 2 support handles:
- operational troubleshooting
- service performance and availability issues
- workflow and integration issue diagnosis
- known issue validation
- incident coordination with engineering
- triage of service-specific failures

Typical Level 2 ownership:
- platform operations
- application operations
- data and API support
- messaging and integration support

---

## Level 3 Support
Level 3 support handles:
- core engineering investigation
- code-level diagnosis
- architecture and dependency issues
- data integrity and migration problems
- complex incident root cause analysis
- production remediation and service restoration

Typical Level 3 ownership:
- engineering leads
- platform engineering
- senior developers
- architects
- database and infrastructure specialists

---

## Business Support
Business support handles:
- customer escalation and communication
- service continuity coordination
- partner and stakeholder updates
- business impact assessment
- release and process exceptions
- operational policy decisions

Typical business support stakeholders:
- product owner
- business owner
- customer support manager
- operations manager

---

## Operations Support
Operations support handles:
- environment health
- monitoring and alert response
- infrastructure and network checks
- service continuity and recovery
- runbook execution
- incident bridge coordination

---

## Engineering Support
Engineering support handles:
- code fixes
- release coordination
- service restoration
- root cause analysis
- defect prioritization
- post-incident remediation

---

## Vendor Support
Vendor support handles:
- payment gateway support
- messaging provider support
- email provider support
- storage and CDN support
- maps or external third-party dependencies
- callback and API provider incident investigation

Vendor coordination is required when the issue originates outside PropertyPilot services.

---

# Support Hours

## Business Hours
- standard support coverage during normal business operations
- primary coverage for customer support and service issues
- product and business stakeholders are available for business decisions

## Extended Hours
- appropriate for high-risk customer and operational workflows
- includes early morning, late evening, and weekend support as required
- targeted toward payment, subscription, KYC, and critical operations flows

## Emergency Support
- 24x7 support during the initial go-live period and critical production incidents
- required for:
  - payment processing failure
  - KYC and onboarding failure
  - system outage
  - severe auth or billing issue
  - critical customer-facing workflow failures

## NRI Support Considerations
NRI customer support requires:
- time-zone-aware escalation
- multilingual support capabilities where applicable
- KYC verification and identity documentation support
- specific handling for onboarding and document submission issues
- extended monitoring for international or cross-border workflows

---

# Incident Management

## Incident Classification

### P1 Critical
Definition:
- complete or major service outage
- critical customer-facing feature unavailable
- payment processing outage
- significant authentication or authorization failure
- data integrity or security incident
- severe business interruption

Response:
- immediate
- on-call escalation required
- incident commander assigned
- executive communication if required

---

### P2 High
Definition:
- major degradation in customer-facing functionality
- critical workflow partially unavailable
- major integration failure affecting a key service
- KYC or subscription processing impacted at scale

Response:
- within 15–30 minutes
- escalation to Level 2 and engineering lead
- business impact assessment required

---

### P3 Medium
Definition:
- isolated or limited impact
- non-critical feature degradation
- moderate workflow delay or partial failure
- customer issue affecting a subset of users

Response:
- within 1–4 hours
- route to service owner and engineering support
- documented workaround if available

---

### P4 Low
Definition:
- minor defect or low customer impact
- non-blocking issue
- cosmetic or low-risk user experience issue

Response:
- within 1 business day
- tracked as backlog item or known issue

---

# Escalation Matrix

| Team | Trigger | Escalation Path |
|---|---|---|
| Support Team | customer issue logged | Level 1 → Level 2 → Level 3 |
| Operations Team | monitoring alert or infrastructure event | Ops Lead → Engineering Lead → Architecture |
| Engineering Team | production bug or critical technical issue | Engineering Lead → Architecture → Security |
| Architecture Team | design or dependency risk | Architecture Lead → Leadership |
| Leadership Team | major customer impact, business risk, security event | CTO/COO/Business Owner as appropriate |

Escalation requirements:
- immediate escalation for P1 and P2
- business impact review for customer-facing failures
- security escalation for any confidentiality or access incident
- executive notification for major customer or financial impact

---

# SLA Matrix

| Incident Type | Response Time | Resolution Time | Escalation Time |
|---|---|---|---|
| P1 Critical | Immediate | 1–4 hours target | Immediate |
| P2 High | 15–30 min | 4–24 hours target | 30 min |
| P3 Medium | 1–4 hours | 1–3 business days target | 2 hours |
| P4 Low | 1 business day | 5 business days target | 1 business day |

SLA caveats:
- actual resolution time depends on customer impact, root cause complexity, and vendor dependency
- emergency or security incidents may require shorter recovery objectives
- vendor or third-party outages may require coordinated resolution windows

---

# Monitoring and Alerting

## Application Monitoring
Monitor:
- service health
- API error rate
- latency
- worker and job health
- login and authentication success
- transaction processing success
- error count trends

## API Monitoring
Monitor:
- 4xx and 5xx rates
- request volume and spike events
- response time percentiles
- failed callback patterns
- auth failures
- rate limit activations

## Database Monitoring
Monitor:
- DB CPU and memory
- connection pool health
- query latency
- lock contention and deadlocks
- storage utilization
- replication lag
- backup health

## Integration Monitoring
Monitor:
- payment status and gateway health
- messaging provider status
- storage latency and failures
- email and WhatsApp callback availability
- maps and provider response time
- analytics pipeline health

## Infrastructure Monitoring
Monitor:
- server health
- container health
- network throughput and latency
- DNS and certificate health
- load balancer performance
- CDN health and origin reachability

Alerting principles:
- alert noise should be minimized
- business-impacting alerts must route to on-call teams
- critical alerts require acknowledgment and triage within SLA
- all alerts must have a clear runbook and ownership

---

# Customer Support Flows

## Complaint Handling
- log the complaint in support system
- categorize by issue type and severity
- record customer impact and urgency
- route to appropriate service or engineering owner
- provide interim resolution or workaround where possible
- close with documented root cause and action

## Service Issue Handling
- confirm whether issue is a product service defect, integration problem, or user error
- verify whether issue affects all users or a subset
- check known issues and alerts
- provide status updates and estimated resolution
- escalate to engineering if repeat or high impact

## Refund Requests
- validate payment and transaction context
- verify policy eligibility
- check subscription or payment status
- route to finance or payment operations as needed
- communicate resolution with tracking number if required

## Subscription Issues
- confirm plan status, billing cycle, and customer state
- validate renewals, pauses, upgrades, and cancellations
- determine if issue is technical or commercial in nature
- route to billing or subscription team as required
- communicate resolution and any service impact

## Agent Issues
- validate agent access, role permissions, assignment status
- check support tickets or workflow assignment data
- verify property and service request association
- validate logs and access restrictions

## NRI Customer Support
- support onboarding and document review issues
- validate KYC workflow exceptions and document validity
- route to KYC operations for document or identity issue
- account for time-zone and language considerations

---

# Operational Support Flows

## Agent Assignment Issues
- verify assignment records and queue state
- check vendor/service request routing
- validate permissions and assignment rules
- confirm escalation and re-assignment path
- monitor assignment backlogs and delays

## Visit Failures
- validate visit scheduling and assignment
- check agent availability and time slot conflicts
- confirm geolocation and route availability
- validate device, app, or field tools access
- route to operations for field support coordination

## Evidence Upload Failures
- validate upload permissions and storage access
- confirm file format, size, and checksum requirements
- verify storage service health
- identify failed or incomplete upload states
- guide customer or agent to retry if appropriate

## Report Generation Failures
- validate report jobs and data sources
- confirm background job queue health
- verify analytics or reporting job dependencies
- requeue or rebuild reports when safe
- check export and delivery permissions

## Notification Failures
- validate message provider health and callback status
- check queue backlog and retry path
- confirm template and content parameters
- review delivery attempt counts and error codes
- verify user preferences and consent states

---

# Hypercare Support

## Day 1
- production support active with rapid triage
- issue and customer impact tracking live
- daily stand-up with support, engineering, and operations
- monitoring and alert check at frequent intervals
- problem management starts immediately

## Week 1
- daily issue review and priority triage
- remediation tracking for open production defects
- business KPI and service health review
- customer support summary and escalation review
- known issue register maintained and shared

## Month 1
- weekly stabilization meeting
- review critical and high incidents
- track root-cause fixes and permanent remediation
- assess release quality and operational readiness
- decide on transition from hypercare to standard support

## Daily Reviews
- review new incidents and open issues
- review customer-impact trends
- validate known issue status
- agree on action owners
- assess if additional engineering support is required

## Weekly Reviews
- review incident volume and severity
- review SLA performance and MTTR
- assess customer satisfaction and support backlog
- identify operational improvements
- schedule permanent remediation actions

---

# Communication Plan

## Customer Communication
- provide status updates for major incidents
- communicate downtime or degraded service expectations
- use customer support channels and service notifications
- provide workaround instructions when available
- communicate resolution and cause summary after closure

## Internal Communication
- status updates to engineering, operations, support, and stakeholders
- ping updates for Sev-1 and Sev-2 incidents
- issue ownership and triage updates
- create incident record and action log

## Incident Communication
- incident commander manages communication
- communication cadence depends on severity
- update all affected stakeholders at least at triage, mitigation, and closure
- document who was informed and when

## Executive Updates
- required for critical customer impact, major outage, financial risk, or security events
- concise summary of:
  - impact
  - mitigation
  - timeline
  - expected next update
  - business risk

---

# Support Metrics

| Metric | Definition |
|---|---|
| First Response Time | time to acknowledge and accept the incident |
| Resolution Time | time to restore service or provide permanent fix |
| Customer Satisfaction | post-incident customer feedback and support experience |
| Incident Volume | number of incidents by severity and category |
| Escalation Rate | percentage of cases escalated beyond Level 1 |
| SLA Compliance | percentage of incidents meeting target response and resolution times |

Tracking expectations:
- daily review of service health and SLA compliance
- weekly review of trends and backlog
- immediate review for P1 and P2 incident violations

---

# Knowledge Management

## Runbooks
- incident runbooks
- operational runbooks
- service-specific workflows
- escalation and rollback runbooks
- known issue mitigations

## FAQs
- customer support FAQs
- troubleshooting guides for core flows
- operational troubleshooting for common issues
- vendor troubleshooting for integration failures

## Known Issues
- tracked with owner, severity, workaround, and status
- reviewed during hypercare and release reviews
- must be made visible to support and product teams

## Training Materials
- support onboarding
- operational workflow training
- incident triage training
- vendor and integration handling training
- customer communication scripts

---

# Roles and Responsibilities

## Support Lead
- owns support model execution
- manages support staffing and case triage
- coordinates case escalation and service recovery
- ensures support coverage and incident readiness

## Operations Lead
- manages monitoring, environments, and infrastructure health
- coordinates technical operational support
- ensures production health and runbook adherence
- owns service continuity decisions

## Engineering Lead
- owns technical triage and remediation
- coordinates engineering investigation and fix plans
- validates production restoration and root-cause correction

## Security Lead
- owns security event triage and response
- validates access, auth, privacy, and data handling incidents
- coordinates any required security incident communication

## Product Owner
- validates business impact and feature priority
- confirms support impact and release priorities
- helps define product-level incident and communication decisions

## Business Owner
- owns overall service continuity decisions
- supports customer and partner communication
- engages leadership for significant business impact

---

# Continuous Improvement

## Post Incident Reviews
- all Sev-1 and Sev-2 incidents require a review
- capture immediate cause, impact, and remediation
- evaluate support actions and communication effectiveness

## Root Cause Analysis
- document technical cause and business trigger
- identify latency, dependency, configuration, or process gaps
- define prevention actions

## Process Improvements
- improve runbooks and escalation paths
- refine monitoring thresholds
- update support training
- strengthen automation and self-healing

## Support Reviews
- weekly during hypercare
- monthly after stabilization
- include engineering, support, product, operations, and security
- capture lessons learned and improvements

---

## Summary

The PropertyPilot go-live support model is designed to provide disciplined, fast, and accountable service support immediately after production release. It balances customer impact management with technical root-cause investigation, operational stability, and continuous improvement.

The model requires:
- clear lane ownership across support, engineering, and operations
- fast incident triage and escalation
- continuous production monitoring and alerting
- strong communication and hypercare governance
- documented knowledge and process improvement loops

This model should remain active throughout the early hypercare period and continue in a mature form after stabilization as the standard production support model.
