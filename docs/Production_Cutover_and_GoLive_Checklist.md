# Production Cutover and Go-Live Checklist

Document Type: Production Deployment and Go-Live Procedure  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This checklist defines the complete production deployment, cutover, go-live, rollback, and hypercare process for PropertyPilot.

The purpose of this procedure is to ensure that:
- production release quality is validated before go-live
- environment readiness is confirmed
- operational controls are in place
- security and compliance risks are mitigated
- rollback and incident response are prepared
- support teams are ready for hypercare and stabilization

This checklist is the authoritative release control document for production deployment decisions.

---

## Scope

This checklist applies to:
- Development
- QA
- UAT
- Production

The checklist covers:
- release governance
- deployment sequencing
- database migration and validation
- API validation
- infrastructure readiness
- security validation
- integration validation
- smoke testing
- rollback readiness
- hypercare support
- production monitoring
- incident response
- final go-live signoff

---

## Release Governance

### Roles
- Product Owner
- Engineering Manager
- Release Manager
- Operations Lead
- Security Lead
- Database Administrator
- DevOps Engineer
- Support Lead
- Compliance / Privacy Lead
- Leadership Sponsor

### Approvals
Production release requires approval from:
- Business owner
- Product owner
- Engineering lead
- Operations lead
- Security lead
- Database / infrastructure owner where applicable
- Leadership sponsor for major or customer-facing releases

### Go/No-Go Criteria
Go-live is approved only if:
- all critical defects are closed or waived
- smoke testing passes
- security review is cleared
- infrastructure health is stable
- backups and rollback are validated
- integrations are verified
- monitoring and alerting are active
- support coverage is confirmed
- signoff is captured from required owners

No-Go triggers:
- critical production defects
- unresolved security finding
- incomplete rollback plan
- failed smoke tests
- unstable data migration
- failed performance or authentication validation
- unresolved incident or support capacity issue

---

## Pre-GoLive Checklist

### Business Readiness
- release scope and business value are confirmed
- customer communication plan is approved
- launch window and cutover window are agreed
- support team has user impact summary
- exception and risk log is reviewed
- finance or billing impact is confirmed

### Product Readiness
- feature scope is approved
- user journeys are tested in production-like environment
- signoff from product owner is recorded
- release notes and user guidance are prepared
- customer-facing workflows are validated

### Technical Readiness
- production build is complete and approved
- configuration drift is reviewed
- environment variables and secrets are validated
- release manifest is approved
- deployment dependencies are confirmed
- application health checks are passing
- database schema and migration scripts are validated in staging and pre-prod
- APIs are tested end-to-end with production-like data
- autoscaling and performance thresholds are validated

### Security Readiness
- vulnerability scan is completed
- secret rotation plan is validated
- TLS and certificate validation is complete
- IAM roles and access policies are verified
- audit logging is enabled
- sensitive data handling is validated
- dependency and image scanning is complete

### Operations Readiness
- monitoring dashboards and alerts are active
- runbooks are published
- incident escalation paths are tested
- support rotation is assigned
- pager and escalation contacts are current
- log retention is validated
- backups and restore jobs are confirmed

### Support Readiness
- hypercare team is scheduled
- customer support scripts and FAQs are prepared
- known issue list is reviewed
- escalation contacts are available
- support tooling is configured
- incident triage process is agreed

---

## Database Migration Checklist

### Migration Validation
- migration scripts are reviewed and approved
- schema drift between environments is checked
- database migration order is validated
- data type compatibility is reviewed
- indexes and constraints are validated
- migration dry run is completed successfully
- zero-downtime or controlled cutover strategy is approved where required

### Backup Validation
- full backup completed before migration
- point-in-time recovery is validated
- backup integrity check is successful
- restore process is tested
- retention policy is confirmed

### Rollback Validation
- rollback script or previous version restore path is tested
- data rollback logic is reviewed
- migration is reversible or compensating action is documented
- rollback timing and risk are estimated
- failback process is approved

### Data Integrity Validation
- row counts are compared against baseline
- nullability violations are checked
- referential integrity is validated
- data transformation results are reviewed
- duplicate IDs or orphan records are checked
- archive and retention rules are validated

---

## API Validation Checklist

### Health Checks
- application health endpoint returns healthy
- dependency health is verified
- API uptime is monitored
- required services are reachable
- versioned routes are active

### Authentication
- auth token generation works
- refresh tokens and expiry are correct
- user/session lifecycle is validated
- partner integration auth is valid
- service-to-service auth is validated

### Authorization
- RBAC and scope validation are correct
- tenant separation is enforced
- customer access is restricted appropriately
- admin and support access is validated
- privileged actions are protected

### Performance Validation
- p95/p99 latency is within threshold
- concurrency is tested
- rate limiting is validated
- memory and CPU are within expected range
- timeout and retry thresholds are tested

---

## Infrastructure Checklist

### Servers
- required instance counts are provisioned
- autoscaling is enabled where required
- capacity planning is validated
- health checks are active
- resource utilization is acceptable

### Containers
- image signatures are verified
- container versions are approved
- deployment manifests are validated
- rolling update strategy is confirmed
- service dependencies are mapped

### Networking
- DNS is correct
- ingress and egress rules are validated
- firewall and security group rules are approved
- load balancer health checks pass
- TLS termination is configured correctly

### DNS
- production hostnames resolve correctly
- wildcard or specific route entries are active
- DNS TTL and propagation are confirmed
- alternate or fallback endpoints are validated

### SSL
- certificates are installed and valid
- certificate renewals are scheduled
- TLS version policy is enforced
- HTTP to HTTPS redirection is correct

### CDN
- CDN route is configured for static or shared assets
- cache invalidation is tested
- content freshness is validated
- origin health is confirmed

### Storage
- object storage and database volumes are provisioned
- retention and lifecycle rules are active
- backup and restore paths are functioning
- storage performance meets expected load

### Monitoring
- application metrics are active
- infrastructure metrics are active
- log shipping is functioning
- alert routing is validated
- dashboard access and permissions are correct

### Logging
- logs are being emitted in production format
- central log aggregation is active
- structured logs are enabled
- correlation IDs are present
- log retention meets requirements

---

## Security Checklist

### Secrets
- all secrets are loaded from approved secret manager
- no hardcoded keys or credentials remain
- secret rotation plan is validated
- environment-specific secrets are isolated
- emergency secret rollback path is available

### Encryption
- data at rest is encrypted
- data in transit uses TLS
- backups are encrypted
- keys are managed in approved system
- encryption scope is validated for production workloads

### Access Controls
- IAM and RBAC are validated
- privileged access is restricted
- break-glass process is documented
- production access logging is enabled
- least privilege is enforced

### Audit Logging
- audit logs are active for:
  - admin changes
  - payment actions
  - customer data changes
  - KYC changes
  - deletion or legal operation
- logs are immutable and retained
- logs are monitored for anomalous access

### Vulnerability Review
- image scan is clear or approved
- dependency audit is completed
- known CVEs reviewed
- web or API vulnerability scan completed
- risk acceptance signed if required

---

## Integration Validation

### Payment Gateway
- payment initiation works
- refund flow works
- payment status callbacks are working
- webhook signatures are validated
- failed transaction handling is tested
- reconciliation works as expected

### SMS
- OTP flow works
- notification sending works
- provider callback status is confirmed
- error handling is tested
- delivery timeout behavior is validated

### WhatsApp
- message sending works
- delivery status event processing works
- webhook verification is validated
- rate-limit policy is functioning

### Email
- transactional email sending works
- email templates render correctly
- bounce and delivery status logging is active
- SPF/DKIM validation configured

### Maps
- map tiles and geolocation APIs respond correctly
- location data is captured and validated
- fallback behavior is tested

### Storage
- file upload works
- checksum validation is active
- document retrieval is stable
- signed URLs work for secure access
- storage lifecycle and retention are validated

### Analytics
- event ingestion works
- dashboards update correctly
- custom events are recorded
- consumer and business metrics are accurate

---

## Deployment Process

### Build
- production build is created from approved branch or tag
- build artifact hash is recorded
- security scan passed
- infrastructure configuration is locked
- release notes prepared

### Package
- deployment package includes all changes
- database migration package is included
- configuration defaults are reviewed
- feature flags are set for staged rollout
- rollback package is prepared

### Deploy
- rollout plan is approved
- deployment order is executed
- canary or phased deployment is used where required
- health checks are watched during deployment
- deployment log is captured

### Validate
- smoke tests run after deployment
- API checks run
- database checks run
- performance checks run
- business KPIs are measured
- support and incident channels are active

### Approve
- release owner confirms health
- product owner confirms feature readiness
- operations confirms service stability
- security confirms no open issues
- leadership confirms launch decision if required

### Release
- production access is opened to users
- monitoring is reviewed continuously
- stakeholder communication is sent
- hypercare begins immediately

---

## Smoke Testing Checklist

### Customer Flows
- registration
- login
- profile update
- property browsing
- service request creation
- subscription onboarding

### Agent Flows
- agent login
- property assignment
- visit creation
- service request update
- notes and observations

### Admin Flows
- user management
- vendor management
- complaint handling
- configuration updates
- report generation

### Payments
- invoice generation
- payment initiation
- refund flow
- ledger consistency
- payment status updates

### Subscriptions
- plan selection
- renewal process
- pause/resume flow
- cancel flow
- billing summary

### Reports
- report generation
- export flow
- PDF/CSV output
- role-specific access

### Notifications
- OTP delivery
- confirmation alerts
- status updates
- email and WhatsApp flows

---

## Rollback Plan

### Trigger Conditions
Rollback is required when:
- critical business flow fails
- severe security vulnerability is found
- API or database instability is severe
- payment or subscription logic fails in production
- data integrity issue is discovered
- service availability or SLA falls below acceptable threshold

### Rollback Process
1. Pause or disable impacted release traffic if required
2. Switch to prior stable version or redeploy previous artifact
3. Revert database changes if required and supported
4. Restore backups where necessary
5. Validate system health and data integrity
6. Resume service only after validation
7. Record action and incident details

### Communication Plan
- notify release owner and incident commander
- inform support stakeholders
- issue status updates to leadership as needed
- update customer communication where business impact is relevant

### Validation Steps
- service health restored
- core product flows operate
- data integrity confirmed
- integrations function
- monitoring stable
- no outstanding critical issues

---

## Hypercare Support Plan

### Duration
- standard hypercare: 7 to 14 days
- extended hypercare depending on release complexity and risk

### War Room
- daily stand-up during hypercare
- incident ownership matrix
- channel for triage updates
- decision log and known issue tracker

### Incident Escalation
- Level 1: support / on-call engineers
- Level 2: service owners and engineering leads
- Level 3: architecture / security / compliance as required
- executive communication for severe impact

### Monitoring
- monitor critical pathways continuously
- review dashboards at defined intervals
- monitor error rates, latency, failed transactions, and business impact
- review incident volume and status

### Daily Reviews
- summarize incidents and resolutions
- identify recurring issues
- evaluate operational and customer impact
- review action items and release stabilisation plan

---

## Production Monitoring Checklist

### Application Health
- service health checks pass
- API error rate normal
- latency within thresholds
- login and session success rate stable
- job processing is stable

### Database Health
- DB CPU and storage normal
- replica lag acceptable
- deadlock or lock contention monitored
- query performance normal
- backup and restore schedule valid

### Infrastructure Health
- server health good
- networking stable
- container health good
- load balancer stable
- autoscaling and capacity adequate

### Business KPIs
- registration completions
- payment success rate
- invoice generation rate
- subscription conversion rate
- service request completion rate
- report generation rate
- notification delivery success

---

## Incident Response Matrix

| Severity | Definition | Response Time | Escalation Path |
|---|---|---|---|
| Sev-1 | Critical outage or security issue | Immediate | Incident Commander → Engineering Leadership → Security → Leadership |
| Sev-2 | Major functionality affected with user impact | 15–30 minutes | Engineering Lead → Support Lead → Product Owner |
| Sev-3 | Moderate issue with limited impact | 1–4 hours | Service Owner → On-call Team |
| Sev-4 | Low impact or minor defects | 1 business day | Support / Operations |

### Response Times
- acknowledge immediately
- triage within defined SLA
- mitigate or isolate within threshold
- resolve or escalate per incident severity

---

## Go-Live Signoff

### Business
- business owner confirms readiness
- product value and launch timing accepted

### Product
- feature readiness confirmed
- release notes and user communication approved

### Engineering
- deployment completed
- smoke tests pass
- rollback plan validated

### Operations
- monitoring active
- runbooks available
- support staffing confirmed

### Security
- vulnerability review cleared
- access controls verified
- audit logging confirmed

### Leadership
- final launch approval recorded
- business risk accepted

---

## Post-GoLive Review

### Lessons Learned
- capture launch outcomes
- record root causes of issues
- note deployment or process gaps
- identify training or operational needs

### Open Issues
- unresolved defects
- unstable integrations
- monitoring gaps
- support process improvement opportunities

### Improvement Actions
- patch issues
- refine deployment process
- improve smoke coverage
- update runbooks
- improve monitoring thresholds
- strengthen rollback practices

---

## Summary

Production deployment is a controlled operational event that requires business, product, engineering, security, and operational readiness. No release is considered live until the cutover, validation, monitoring, rollback, and support procedures are fully checked and signed off.

This checklist must be followed for every production deployment and must be retained as the formal record of release readiness and go-live approval.
