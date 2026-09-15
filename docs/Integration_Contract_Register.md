# PropertyPilot Integration Contract Register

Document Type: Integration Contract Register  
Version: 1.0  
Date: 2026-08-31  
Status: Draft

---

## 1. Purpose

This register maintains the master inventory of all external and internal integrations used by PropertyPilot. It defines the required contract, ownership, security, operational, and lifecycle controls to ensure safe, auditable, and scalable integration behavior across the platform.

This register governs:
- third-party provider integrations
- internal service-to-service integrations
- webhook consumers and producers
- event-driven integrations
- data exchange and object storage connectivity
- operational telemetry and observability integrations

This register supports enterprise governance and aligns with:
- API_Contract_Governance.md
- Webhook_Contract_Catalog.md
- Technical_Architecture.md
- Security_Design.md

---

## 2. Scope

This register covers integrations used in:
- Customer App
- Agent App
- Operations Portal
- Admin Portal
- Subscriptions
- Payments
- Notifications
- Reports
- Property Management
- Verification Services
- Monitoring Services
- Marketplace
- NRI Services

The register applies to:
- inbound integrations
- outbound integrations
- bidirectional integrations
- asynchronous webhook integrations
- object storage and file transfer
- reporting and analytics exports
- identity and OTP providers
- monitoring and telemetry systems

---

## 3. Integration Governance Principles

1. All integrations must have named ownership.
2. No production integration may be activated without security and architecture review.
3. All integrations must support secure authentication and authorized access.
4. Financial and identity integrations must be idempotent by design.
5. Critical integrations must have documented failover and recovery procedures.
6. All integrations must emit observable telemetry and auditable logs.
7. Contract changes require version review and dependency validation.
8. Sensitive data must be minimized and encrypted in transit and at rest.
9. Provider contracts must specify SLA, support window, and escalation path.
10. Every integration must document data retention, deletion, and exit strategy.

---

## 4. Integration Inventory

| Integration ID | Integration Name | Provider | Category | Direction | Protocol | Authentication Method | Owner | Criticality | SLA | Environment Support | Status |
|---|---|---|---|---|---|---|---|---|---|---|---|
| INT-001 | Payment Gateway | Razorpay | Payment | Bidirectional | REST + Webhook | OAuth / signed webhook / idempotency key | Finance Platform | Critical | 99.95% | Dev / QA / UAT / Prod | Planned |
| INT-002 | Payment Gateway | Cashfree | Payment | Bidirectional | REST + Webhook | OAuth / signed webhook / idempotency key | Finance Platform | Critical | 99.95% | Dev / QA / UAT / Prod | Planned |
| INT-003 | Payment Gateway | Stripe | Payment | Bidirectional | REST + Webhook | OAuth / signed webhook / idempotency key | Finance Platform | Critical | 99.95% | Dev / QA / UAT / Prod | Future |
| INT-004 | SMS Provider | SMS Provider | Communication | Bidirectional | REST + Webhook | API key / OAuth / signed callback | Notification Platform | Critical | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-005 | WhatsApp Business API | WhatsApp Business API | Communication | Bidirectional | REST + Webhook | OAuth / signed callback | Notification Platform | High | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-006 | Email Provider | Email Provider | Communication | Bidirectional | REST + Webhook | OAuth / API secret / signed callback | Notification Platform | High | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-007 | Maps Provider | Google Maps | Maps & Location | Outbound | REST | API key / OAuth | Property Platform | Medium | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-008 | Maps Provider | MapMyIndia | Maps & Location | Outbound | REST | API key / OAuth | Property Platform | Medium | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-009 | Object Storage | AWS S3 | Storage | Bidirectional | REST / S3 API | IAM role / signed URLs | Platform Engineering | Critical | 99.95% | Dev / QA / UAT / Prod | Active |
| INT-010 | Object Storage | Azure Blob Storage | Storage | Bidirectional | REST / Blob API | Managed identity / SAS / IAM | Platform Engineering | Medium | 99.9% | Dev / QA / UAT / Prod | Future |
| INT-011 | Aadhaar Verification | Aadhaar Verification | KYC | Bidirectional | REST + Webhook | OAuth / mTLS / signed callback | Verification Platform | Critical | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-012 | PAN Verification | PAN Verification | KYC | Bidirectional | REST | OAuth / mTLS | Verification Platform | High | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-013 | CKYC | CKYC | KYC | Bidirectional | REST + Webhook | OAuth / signed callback | Verification Platform | Critical | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-014 | Analytics | Google Analytics | Analytics | Outbound | REST / Web SDK | OAuth / measurement ID | Product Analytics | Medium | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-015 | Analytics | Power BI | Analytics | Bidirectional | REST / OData | OAuth / service principal | Reporting Platform | High | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-016 | Analytics | Metabase | Analytics | Bidirectional | REST / SQL Connector | OAuth / service account | Reporting Platform | Medium | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-017 | Monitoring | Prometheus | Monitoring | Inbound | HTTP / Pull | Service account / network policy | Platform Operations | High | 99.9% | Dev / QA / UAT / Prod | Active |
| INT-018 | Monitoring | Grafana | Monitoring | Bidirectional | HTTP / API | OAuth / service account | Platform Operations | High | 99.9% | Dev / QA / UAT / Prod | Active |
| INT-019 | Monitoring | ELK Stack | Monitoring | Bidirectional | HTTP / Log ingestion | TLS / service account | Platform Operations | High | 99.9% | Dev / QA / UAT / Prod | Active |
| INT-020 | OTP Provider | OTP Provider | Identity | Outbound | REST | API key / OAuth | Identity Platform | Critical | 99.9% | Dev / QA / UAT / Prod | Planned |
| INT-021 | Identity Verification | Identity Verification | Identity | Bidirectional | REST + Webhook | OAuth / signed callback | Identity Platform | Critical | 99.9% | Dev / QA / UAT / Prod | Planned |

---

## 5. Payment Integrations

### 5.1 Razorpay
- Purpose: payment capture, refund, invoice payment, reconciliation
- Category: Payment
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Finance Platform
- Criticality: Critical
- Authentication: OAuth or provider-issued credential; signed callback
- Idempotency: required for payment capture, refund, and callback processing
- Retry: safe retry for transient failures; no retry for non-idempotent irreversible operations
- Timeout: 5s–15s for synchronous flows, 30s for callback acknowledgement
- Error Handling: structured provider error translation, no silent fail
- Data shared: invoice ID, customer reference, amount, currency, external transaction reference

### 5.2 Cashfree
- Purpose: payment initiation, status checks, refunds, reconciliation
- Category: Payment
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Finance Platform
- Criticality: Critical
- Authentication: OAuth / signed webhook / request signing
- Idempotency: required on payment creation and refund
- Retry: retry only safe, idempotent operations
- Timeout: 5s–15s
- Error Handling: translate provider errors to internal financial codes
- Data shared: payment reference, invoice reference, amount, status outcome

### 5.3 Stripe (Future)
- Purpose: future payment processing and subscriptions scale-out
- Category: Payment
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Finance Platform
- Criticality: Critical
- Authentication: OAuth / signed webhook
- Idempotency: required on all creation and settlement callbacks
- Retry: safe retry with idempotency key
- Timeout: 5s–15s
- Error Handling: detailed mapping to internal payment state
- Data shared: payment intent, transfer details, status, settlement metadata

---

## 6. Communication Integrations

### 6.1 SMS Provider
- Purpose: OTP delivery, verification, transaction alerts
- Category: Communication
- Direction: Bidirectional
- Protocol: REST + Delivery Callback
- Ownership: Notification Platform
- Criticality: Critical
- Authentication: API key or OAuth; callback signature verification
- Retry: resend only on provider failure or queue-based retry with de-dupe
- Timeout: 5s–10s for send request
- Error Handling: mapping to notification status, no duplicate send due to callback replay
- Idempotency: required on message send and delivery status processing

### 6.2 WhatsApp Business API
- Purpose: template notifications and delivery receipts
- Category: Communication
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Notification Platform
- Criticality: High
- Authentication: OAuth + webhook signature
- Retry: retry transient failures with backoff; template rate compliance required
- Timeout: 5s–15s
- Error Handling: delivery and template validation processing
- Idempotency: dedupe by provider message ID and business notification ID

### 6.3 Email Provider
- Purpose: transactional notifications, invoice delivery, operational alerts
- Category: Communication
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Notification Platform
- Criticality: High
- Authentication: OAuth or service credential; signed callback
- Retry: retry transient network/provider failures; skip hard bounces
- Timeout: 5s–15s
- Error Handling: provider error codes mapped to platform statuses
- Idempotency: dedupe by notification event ID and message ID

---

## 7. Maps and Location Integrations

### 7.1 Google Maps
- Purpose: geocoding, address validation, route and ETA estimation
- Category: Maps & Location
- Direction: Outbound
- Protocol: REST
- Ownership: Property Platform
- Criticality: Medium
- Authentication: API key or restricted OAuth
- Retry: retry rate-limited or 5xx responses with jitter
- Timeout: 3s–8s
- Error Handling: degrade gracefully to cached or partial info
- Idempotency: usually not required for lookup requests

### 7.2 MapMyIndia
- Purpose: geocoding and route data for property and service coverage
- Category: Maps & Location
- Direction: Outbound
- Protocol: REST
- Ownership: Property Platform
- Criticality: Medium
- Authentication: API key
- Retry: retry transient errors; cap request rate
- Timeout: 3s–8s
- Error Handling: degrade to alternative provider or cached result
- Idempotency: not required for read-only lookups

---

## 8. Storage Integrations

### 8.1 AWS S3
- Purpose: document storage, attachments, exports, reports, evidence files
- Category: Storage
- Direction: Bidirectional
- Protocol: S3 API / REST
- Ownership: Platform Engineering
- Criticality: Critical
- Authentication: IAM roles, signed URLs, bucket policy, encrypted access
- Retry: retry failed multipart upload or download
- Timeout: 10s–60s depending on file size
- Error Handling: checksum validation, retry, preserve object versioning
- Idempotency: object key plus checksum should be used for upload dedupe

### 8.2 Azure Blob Storage
- Purpose: alternative or secondary object storage
- Category: Storage
- Direction: Bidirectional
- Protocol: Blob API
- Ownership: Platform Engineering
- Criticality: Medium
- Authentication: managed identity, SAS, or IAM
- Retry: retry idempotent object operations
- Timeout: 10s–60s
- Error Handling: validate object integrity before processing
- Idempotency: key-based upload dedupe

---

## 9. KYC Integrations

### 9.1 Aadhaar Verification
- Purpose: identity verification for tenants, residents, and vendors
- Category: KYC
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Verification Platform
- Criticality: Critical
- Authentication: OAuth / mTLS / signed callbacks
- Retry: retry safe verification status retrieval; no duplicate verification
- Timeout: 10s–20s
- Error Handling: verification flow rollback and queue for manual review
- Idempotency: dedupe on verification ID and request ID

### 9.2 PAN Verification
- Purpose: identity and compliance verification
- Category: KYC
- Direction: Outbound
- Protocol: REST
- Ownership: Verification Platform
- Criticality: High
- Authentication: OAuth or service credential
- Retry: retry transient failures; no duplicate verification
- Timeout: 10s–15s
- Error Handling: invalid or expired document routing to rework queue
- Idempotency: required when calling the same verification endpoint multiple times

### 9.3 CKYC
- Purpose: centralized KYC / background compliance verification
- Category: KYC
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Verification Platform
- Criticality: Critical
- Authentication: OAuth / signed callback
- Retry: idempotent retry with event ID
- Timeout: 10s–20s
- Error Handling: manual compliance review for rejected or incomplete cases
- Idempotency: required for terminal status callbacks

---

## 10. Analytics Integrations

### 10.1 Google Analytics
- Purpose: product and acquisition analytics
- Category: Analytics
- Direction: Outbound
- Protocol: REST / SDK
- Ownership: Product Analytics
- Criticality: Medium
- Authentication: measurement ID + OAuth or client credential
- Retry: safe retries for event export or queueing
- Timeout: 5s–10s
- Error Handling: drop low-priority analytics failures without blocking business flows
- Idempotency: not strictly required for event tracking

### 10.2 Power BI
- Purpose: business dashboards, executive reporting, operational KPI reporting
- Category: Analytics
- Direction: Bidirectional
- Protocol: REST / OData
- Ownership: Reporting Platform
- Criticality: High
- Authentication: OAuth / service principal
- Retry: retry scheduled data refresh or export jobs
- Timeout: 15s–60s for API calls
- Error Handling: queue pipeline and retry import
- Idempotency: required for repeated dataset refresh requests

### 10.3 Metabase
- Purpose: internal dashboarding and ad hoc query reporting
- Category: Analytics
- Direction: Bidirectional
- Protocol: REST / SQL Connector
- Ownership: Reporting Platform
- Criticality: Medium
- Authentication: service account or OAuth
- Retry: retry scheduled queries and exports
- Timeout: 15s–30s
- Error Handling: isolate reporting failures from transactional workflows
- Idempotency: not critical for read-only analytics

---

## 11. Monitoring Integrations

### 11.1 Prometheus
- Purpose: metrics collection and alerting
- Category: Monitoring
- Direction: Inbound
- Protocol: HTTP / Pull
- Ownership: Platform Operations
- Criticality: High
- Authentication: service identity / network restriction
- Retry: standard scrape retry and alerting policy
- Timeout: 5s–15s
- Error Handling: alert based on scrape loss and target status
- Idempotency: not applicable for metrics collection

### 11.2 Grafana
- Purpose: dashboarding and alert visualization
- Category: Monitoring
- Direction: Bidirectional
- Protocol: HTTP / API
- Ownership: Platform Operations
- Criticality: High
- Authentication: OAuth / service account
- Retry: not critical for dashboard reads; scheduled exports may retry
- Timeout: 5s–15s
- Error Handling: alerting fallbacks for missing dashboards
- Idempotency: not required

### 11.3 ELK Stack
- Purpose: centralized logs and search
- Category: Monitoring
- Direction: Bidirectional
- Protocol: HTTP / Log ingestion
- Ownership: Platform Operations
- Criticality: High
- Authentication: TLS + credentials / service account
- Retry: log ingestion retries with retry queues
- Timeout: 5s–30s
- Error Handling: drop or queue logs on validation issues
- Idempotency: not required for log events

---

## 12. Identity Integrations

### 12.1 OTP Provider
- Purpose: one-time password delivery and validation
- Category: Identity
- Direction: Outbound
- Protocol: REST
- Ownership: Identity Platform
- Criticality: Critical
- Authentication: API key, scoped access, or OAuth
- Retry: retry transient failures and rate-limit conditions
- Timeout: 5s–10s
- Error Handling: OTP generation and validation failures must be logged and actioned securely
- Idempotency: required on request generation if retries occur

### 12.2 Identity Verification
- Purpose: identity and user profile validation
- Category: Identity
- Direction: Bidirectional
- Protocol: REST + Webhook
- Ownership: Identity Platform
- Criticality: Critical
- Authentication: OAuth / signed callback / mTLS
- Retry: safe retry for verification status or lookup operations
- Timeout: 10s–20s
- Error Handling: route to review when verification cannot complete
- Idempotency: required for status and callback processing

---

## 13. Contract Standards

For every integration, the following standards apply.

### 13.1 Endpoint Ownership
Each integration requires:
- technical owner
- domain owner
- operational owner
- support contact
- escalation path
- security approver

### 13.2 Request Format
- all external APIs must use versioned contracts
- JSON is the default format for REST payloads
- binary or object storage APIs follow provider-specific semantics
- requests must include correlation ID where supported
- all payloads must be schema validated

### 13.3 Response Format
- responses must be structured and explicit
- error bodies must include code, message, and trace metadata
- failure states must be encoded in provider-native error codes and platform mappings
- all responses must support retry classification

### 13.4 Retry Rules
- retry only on transient failures or safe idempotent actions
- exponential backoff with jitter
- bounded retry count
- no retry for irreversible financial or identity decisions without explicit idempotency
- all retries must be logged and auditable

### 13.5 Timeout Rules
- provider timeout must be defined per integration type
- timeouts must be shorter than user-visible SLA expectations
- failed synchronous operations must degrade gracefully
- asynchronous jobs must be queued or retried if processing exceeds timeout

### 13.6 Error Handling
- map provider errors to internal status codes
- avoid swallowing unknown failures
- classify failure as retryable or permanent
- create explicit alerting and operational response rules
- route critical failures to support or security escalation

### 13.7 Idempotency Requirements
For all integrations involving:
- payment
- verification
- workflow creation
- subscription activation
- refunds
- replays
- webhook callback processing

The system must support:
- idempotency key
- dedupe storage
- replay protection
- verification of repeated requests
- safe reprocessing behavior

---

## 14. Security Controls

All integrations must comply with the following controls:

- TLS 1.2+ required for all external communication
- secrets stored in managed secret stores
- no hard-coded credentials
- OAuth or scoped tokens preferred over static API keys
- webhook signatures required for inbound callbacks
- timestamp validation required for replay protection
- role-based access controls for every integration credential
- least-privilege access per provider
- encrypted data in transit and at rest
- support for environment separation: Dev / QA / UAT / Prod
- security review required before production deployment
- data minimization and classification checks before production use

Additional controls:
- private connectivity where supported
- IP allowlisting where required
- DPA / compliance review for personal data exchange
- masking of PII in telemetry and logs
- secret rotation policy and monitoring
- no storage of payment credentials or OTP values in application logs

---

## 15. Audit Requirements

Each integration must be auditable.

Required audit data:
- integration ID and name
- provider and endpoint
- request timestamp
- correlation ID
- actor or service identity
- source and destination
- success/failure outcome
- retry and compensation actions
- payload hash or redacted event reference
- security validation result
- configuration version

Audit-critical integrations:
- payment
- KYC
- identity
- subscriptions
- refunds
- notifications
- security and monitoring systems

---

## 16. Monitoring Requirements

Each integration must have:
- health checks
- response time metrics
- error rate monitoring
- retry and queue metrics
- DLQ visibility
- alerting thresholds
- owner assignment
- runbook and escalation path

Monitoring must include:
- provider availability
- call success/failure rates
- timeout count
- payload validation failure count
- callback volume
- retry count
- duplicate event rate
- service degradation pattern

---

## 17. Disaster Recovery Considerations

All production integrations must support disaster recovery planning.

Required DR controls:
- documented failover strategy
- alternative provider or degraded mode
- replay and recovery procedures
- safe recovery of asynchronous jobs
- backup of configuration and secrets
- restoration and validation testing

Critical DR considerations:
- payment provider failover without duplicate charging
- KYC service failover without bypassing required checks
- notification channel fallback without violating consent rules
- storage redundancy and cross-region recovery
- monitoring and alerting continuity in degraded mode

---

## 18. Change Management Process

All integration changes must follow a controlled process.

Required steps:
1. Identify integration scope and impact.
2. Review data classification and security risk.
3. Validate endpoint and contract version.
4. Update this register and associated contract docs.
5. Test success, timeout, retry, failure, replay, idempotency, and security scenarios.
6. Approve through architecture, domain owner, and security review.
7. Release only to approved environment(s).
8. Monitor for production drift and incident signature.

Breaking changes require:
- contract version increase
- consumer compatibility review
- readiness checks for all downstream consumers
- deprecation notice and sunset plan where applicable

---

## 19. Integration Lifecycle

### 19.1 Draft
- new or exploratory integration
- contract not yet approved
- not eligible for production use

### 19.2 Planned
- approved for project use
- environment and test plan defined
- not yet production activated

### 19.3 Active
- production integration is live
- monitored and governed
- supported under SLA

### 19.4 Deprecated
- still active but scheduled for retirement
- sunset date documented
- consumer migration underway

### 19.5 Retired
- no longer in use
- support and traffic removed
- contracts and audit evidence archived

---

## 20. Related Documents

- API_Contract_Governance.md
- Webhook_Contract_Catalog.md
- Technical_Architecture.md
- Security_Design.md
- Event_Catalog.md
- Notification_Catalog.md
- Cross_Cutting_Requirements.md

---

## 21. Summary

This register provides the master inventory and governance model for PropertyPilot’s external and internal integrations. It ensures every integration is:
- secure
- auditable
- idempotent where required
- observable
- recoverable
- contract-governed
- suitable for enterprise production operations

This register must be updated every time a new integration is introduced, removed, or materially changed.
