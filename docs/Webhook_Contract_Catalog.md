```markdown
# Webhook Contract Catalog

Document Type: Webhook Integration Contract Catalog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for review

---

## 1. Purpose

This document defines all inbound and outbound webhook contracts used by PropertyPilot. It governs the contract, security, retry, replay, monitoring, and operational handling of external callbacks and event notifications exchanged with payment providers, communication providers, KYC providers, and internal platform consumers.

The purpose of this catalog is to ensure:
- consistent contract design
- secure callback validation
- reliable retry handling
- idempotent event processing
- auditable event traces
- operational governance across integrations

---

## 2. Scope

This catalog applies to:
- payment provider callbacks
- SMS delivery status callbacks
- WhatsApp delivery callbacks
- email provider status callbacks
- KYC verification callbacks
- internal platform event notifications
- subscription lifecycle events
- payment lifecycle events
- service request events
- report ready events
- notification event delivery

This catalog covers:
- inbound webhook contracts received by PropertyPilot
- outbound webhook contracts emitted by PropertyPilot
- security validation rules
- DLQ handling
- replay rules
- audit and monitoring standards

---

## 3. Webhook Architecture

PropertyPilot uses a hybrid integration approach:
- synchronous REST APIs for direct request/response patterns
- asynchronous webhooks for event-driven integration
- message bus for event publication and downstream processing
- idempotency service for financial and workflow-safe replay
- dead-letter queue for failed callback processing

Recommended architecture:
- external systems call PropertyPilot endpoints using signed HTTP requests
- PropertyPilot validates signature, timestamp, and payload integrity
- inbound requests are transformed into domain events or service actions
- outbound webhook events are published from central event bus
- downstream consumers receive signed payloads and acknowledge success
- failed deliveries are retried per policy and observed in DLQ

Typical event flow:
1. external system produces event
2. event is delivered to PropertyPilot or emitted by PropertyPilot
3. payload is validated
4. event is processed idempotently
5. success is acknowledged
6. failure is retried or dead-lettered
7. event is logged and correlated

---

## 4. Inbound Webhooks

Inbound webhooks are callbacks received by PropertyPilot from third-party systems.

### 4.1 Payment Provider

#### 4.1.1 Event Name
- payment.authorized
- payment.paid
- payment.failed
- payment.refunded
- payment.disputed
- payment.settled

#### 4.1.2 Source System
- Payment Gateway / PSP

#### 4.1.3 Endpoint
- POST /api/v1/webhooks/payments
- POST /api/v1/webhooks/payment-provider

#### 4.1.4 Authentication Method
- HMAC signature
- timestamp validation
- shared secret or signed certificate
- optional IP allowlist

#### 4.1.5 Payload Structure
```json
{
  "eventId": "evt_123456",
  "eventType": "payment.paid",
  "provider": "stripe",
  "organizationId": "org_001",
  "tenantId": "tenant_010",
  "paymentId": "pay_987",
  "invoiceId": "inv_654",
  "amount": 2450.00,
  "currency": "USD",
  "status": "paid",
  "occurredAt": "2026-08-31T12:00:00Z",
  "metadata": {
    "reference": "INV-654",
    "gatewayTransactionId": "txn_1122"
  }
}
```

#### 4.1.6 Retry Rules
- retry for 5xx and transient network errors
- retry with exponential backoff
- maximum 5 retries
- if retry budget exhausted, route to DLQ

#### 4.1.7 Idempotency Rules
- eventId must be stored and checked
- duplicate delivery of same eventId must be rejected with 200/202 and no double-processing
- payment operation must be idempotent by paymentId and eventId
- replay of same event must not create duplicate financial ledger entries

---

### 4.2 SMS Provider

#### 4.2.1 Event Name
- sms.sent
- sms.delivered
- sms.failed
- sms.undeliverable

#### 4.2.2 Source System
- SMS provider

#### 4.2.3 Endpoint
- POST /api/v1/webhooks/sms

#### 4.2.4 Authentication Method
- HMAC signature
- timestamp validation
- provider-signed callback token

#### 4.2.5 Payload Structure
```json
{
  "eventId": "sms_evt_001",
  "eventType": "sms.delivered",
  "provider": "twilio",
  "notificationId": "notif_101",
  "messageId": "msg_555",
  "status": "delivered",
  "recipient": "+15551234567",
  "occurredAt": "2026-08-31T12:02:00Z"
}
```

#### 4.2.6 Retry Rules
- provider retries for up to 24 hours
- PropertyPilot must acknowledge 2xx quickly
- if PropertyPilot fails validation, it returns 400 and provider may retry based on provider policy

#### 4.2.7 Idempotency Rules
- dedupe on notificationId and provider messageId
- do not reprocess duplicate delivery status events
- maintain delivery status state machine with last-known-good status logic

---

### 4.3 WhatsApp Provider

#### 4.3.1 Event Name
- whatsapp.sent
- whatsapp.delivered
- whatsapp.read
- whatsapp.failed

#### 4.3.2 Source System
- WhatsApp Business API provider

#### 4.3.3 Endpoint
- POST /api/v1/webhooks/whatsapp

#### 4.3.4 Authentication Method
- HMAC verification
- signed callback token
- timestamp validation
- optional webhook secret

#### 4.3.5 Payload Structure
```json
{
  "eventId": "wa_evt_009",
  "eventType": "whatsapp.read",
  "provider": "meta",
  "notificationId": "notif_305",
  "messageId": "wa_msg_774",
  "status": "read",
  "recipient": "+15550001111",
  "occurredAt": "2026-08-31T12:15:00Z"
}
```

#### 4.3.6 Retry Rules
- retry after transient technical errors
- 429 or 5xx should be retried with backoff
- max 6 retries before DLQ

#### 4.3.7 Idempotency Rules
- dedupe by provider messageId
- ignore repeated read/delivered callbacks
- mark notification status only once per terminal state

---

### 4.4 Email Provider

#### 4.4.1 Event Name
- email.sent
- email.delivered
- email.bounced
- email.failed
- email.opened
- email.clicked

#### 4.4.2 Source System
- Email service provider

#### 4.4.3 Endpoint
- POST /api/v1/webhooks/email

#### 4.4.4 Authentication Method
- HMAC signature
- secret validation
- timestamp validation
- verify domain ownership

#### 4.4.5 Payload Structure
```json
{
  "eventId": "email_evt_888",
  "eventType": "email.bounced",
  "provider": "sendgrid",
  "notificationId": "notif_208",
  "messageId": "sg_msg_9981",
  "status": "bounced",
  "recipient": "tenant@example.com",
  "occurredAt": "2026-08-31T12:20:00Z",
  "metadata": {
    "reason": "hard_bounce"
  }
}
```

#### 4.4.6 Retry Rules
- retry after temporary rejection
- respect provider retry guidance
- retry window: 24 hours
- queue to DLQ after max attempts

#### 4.4.7 Idempotency Rules
- dedupe by provider messageId and eventId
- events must be processed once per unique notification/message
- if same event is replayed, ignore and return success acknowledgment

---

### 4.5 KYC Provider

#### 4.5.1 Event Name
- kyc.started
- kyc.pending
- kyc.approved
- kyc.rejected
- kyc.expired
- kyc.failed

#### 4.5.2 Source System
- KYC / identity verification provider

#### 4.5.3 Endpoint
- POST /api/v1/webhooks/kyc

#### 4.5.4 Authentication Method
- HMAC signature
- timestamp validation
- IP allowlist if required
- JWT or signed provider token where supported

#### 4.5.5 Payload Structure
```json
{
  "eventId": "kyc_evt_77",
  "eventType": "kyc.approved",
  "provider": "kyc_vendor",
  "verificationId": "verify_441",
  "tenantId": "tenant_112",
  "status": "approved",
  "decisionReason": "document_verified",
  "occurredAt": "2026-08-31T12:30:00Z",
  "metadata": {
    "riskScore": 12,
    "country": "US"
  }
}
```

#### 4.5.6 Retry Rules
- retry on 429, 5xx, and transient network errors
- maximum 8 retries with increasing backoff
- escalation to DLQ after threshold

#### 4.5.7 Idempotency Rules
- dedupe on verificationId and eventId
- repeated approvals or rejections must not be applied multiple times
- safe replays must preserve the last verified status

---

## 5. Outbound Webhooks

Outbound webhooks are notifications sent from PropertyPilot to partner or internal systems.

### 5.1 Subscription Events

#### 5.1.1 Event Type
- subscription.created
- subscription.updated
- subscription.renewed
- subscription.cancelled
- subscription.paused
- subscription.failed_renewal

#### 5.1.2 Consumer
- billing partner
- admin systems
- reseller or partner platforms
- internal subscription monitoring

#### 5.1.3 Payload
```json
{
  "eventId": "sub_evt_010",
  "eventType": "subscription.updated",
  "organizationId": "org_001",
  "subscriptionId": "sub_120",
  "status": "active",
  "planCode": "premium",
  "occurredAt": "2026-08-31T12:40:00Z",
  "correlationId": "corr_aa77"
}
```

#### 5.1.4 Retry Policy
- immediate retry on 5xx or network failure
- exponential backoff up to 5 attempts
- DLQ after retry exhaustion

#### 5.1.5 Replay Policy
- replay allowed if consumer requests event retransmission
- event replay requires correlation to original eventId
- replay events must be idempotent on consumer side

---

### 5.2 Payment Events

#### 5.2.1 Event Type
- payment.initiated
- payment.authorized
- payment.paid
- payment.failed
- payment.refunded
- payment.reversed

#### 5.2.2 Consumer
- accounting systems
- finance workflows
- admin portals
- partner integrations

#### 5.2.3 Payload
```json
{
  "eventId": "pay_evt_204",
  "eventType": "payment.paid",
  "organizationId": "org_001",
  "paymentId": "pay_987",
  "invoiceId": "inv_654",
  "amount": 2450.00,
  "currency": "USD",
  "status": "paid",
  "occurredAt": "2026-08-31T12:00:00Z",
  "correlationId": "corr_77bb"
}
```

#### 5.2.4 Retry Policy
- three-tier retry policy: immediate, backoff, final
- must protect against duplicate settlement processing
- payment-related events must be durable and recoverable

#### 5.2.5 Replay Policy
- replay only by authorized consumer or retry engine
- idempotency key required
- consumer must check eventId before processing

---

### 5.3 Service Request Events

#### 5.3.1 Event Type
- service-request.created
- service-request.assigned
- service-request.in_progress
- service-request.completed
- service-request.cancelled
- service-request.escalated

#### 5.3.2 Consumer
- customer app
- agent app
- operations portal
- vendor systems
- support systems

#### 5.3.3 Payload
```json
{
  "eventId": "sr_evt_311",
  "eventType": "service-request.assigned",
  "organizationId": "org_001",
  "serviceRequestId": "sr_425",
  "assignedToUserId": "user_991",
  "status": "assigned",
  "occurredAt": "2026-08-31T12:44:00Z",
  "correlationId": "corr_112"
}
```

#### 5.3.4 Retry Policy
- retry for network and 5xx errors
- allow delayed retry for non-critical workflow components
- DLQ for permanent failure or malformed payloads

#### 5.3.5 Replay Policy
- replay is allowed for operational resyncs
- consumer must ignore duplicate eventIds

---

### 5.4 Report Ready Events

#### 5.4.1 Event Type
- report.ready
- report.failed
- report.expired

#### 5.4.2 Consumer
- admin portal
- operations portal
- report subscribers
- partner systems

#### 5.4.3 Payload
```json
{
  "eventId": "report_evt_501",
  "eventType": "report.ready",
  "reportId": "rep_420",
  "reportName": "PortfolioSummary",
  "downloadUrl": "https://s3.example.com/reports/rep_420.csv",
  "status": "ready",
  "generatedAt": "2026-08-31T12:50:00Z",
  "correlationId": "corr_abc"
}
```

#### 5.4.4 Retry Policy
- retry if the delivery endpoint is unavailable
- report ready event must be idempotent and recoverable
- store pre-signed URL generation status for traceability

#### 5.4.5 Replay Policy
- allow replay by authorized report subscriber
- event replay should be bound to the same reportId and version

---

### 5.5 Notification Events

#### 5.5.1 Event Type
- notification.sent
- notification.delivered
- notification.failed
- notification.read
- notification.clicked

#### 5.5.2 Consumer
- notification systems
- admin dashboards
- analytics systems
- downstream communication monitoring

#### 5.5.3 Payload
```json
{
  "eventId": "notif_evt_775",
  "eventType": "notification.sent",
  "notificationId": "notif_118",
  "channel": "sms",
  "recipient": "+15551234567",
  "status": "sent",
  "occurredAt": "2026-08-31T12:55:00Z",
  "correlationId": "corr_cred"
}
```

#### 5.5.4 Retry Policy
- notify retry engine on 429 and 5xx
- retries use exponential backoff
- final state and delivery log retained

#### 5.5.5 Replay Policy
- event replay is allowed for monitoring and reconciliation
- duplicate delivery events are ignored when same eventId is already processed

---

## 6. Webhook Security

### 6.1 HMAC
All incoming webhooks must be validated using HMAC or an equivalent signature mechanism.

Rules:
- signature must be verified before processing
- payload must be compared exactly to the signed payload
- all signing keys must be stored in a managed secret service
- rotation is required according to policy

### 6.2 Timestamp Validation
Webhook requests must include a timestamp to prevent stale requests.

Rules:
- timestamp must be within a valid replay window
- stale requests must be rejected
- drift tolerance must be bounded and documented
- requests outside allowed window are rejected and logged

### 6.3 Replay Protection
Webhook replay protection must include:
- eventId dedupe
- timestamp check
- signed payload validation
- idempotency storage
- replay window enforcement

Rejected conditions:
- missing signature
- invalid timestamp
- expired payload
- repeated eventId
- payload mismatch
- invalid source system

---

## 7. Dead Letter Queue Handling

PropertyPilot must maintain a dead-letter queue (DLQ) for failed webhook processing.

### 7.1 DLQ Governance
The DLQ must capture:
- webhook payload
- source system
- event type
- status code
- failure reason
- timestamp
- correlation ID
- raw headers where safe and needed

### 7.2 DLQ Retry Criteria
DLQ entries are created when:
- max retry attempts exceeded
- payload schema invalid
- external system unreachable beyond retry window
- signature validation fails beyond safe handling
- consumer processing fails repeatedly

### 7.3 DLQ Handling Rules
- DLQ items must be retriable via operational tooling
- manual replay is required for business-critical failures
- DLQ entries must be visible to production support
- no financial event may be silently dropped

---

## 8. Replay Procedures

### 8.1 Replay Trigger
Replay may be initiated by:
- system operator
- support team
- integration owner
- automated retry controller

### 8.2 Replay Rules
- replay requires a valid event or message identifier
- consumer and payload must be verified
- replay is allowed only for approved sources
- replayed messages must be processed idempotently
- replay activity must be recorded in audit logs

### 8.3 Replay Validation
Before replaying:
- verify the event has not already been applied
- validate timestamp and source authenticity
- confirm the event is not beyond retention or legal hold
- confirm target consumer is valid

### 8.4 Replay Record
Each replay must include:
- original eventId
- replayedBy
- replayedAt
- reason for replay
- correlationId
- outcome

---

## 9. Audit Requirements

All webhook activity must be auditable.

Required audit data:
- source system
- event type
- eventId
- correlationId
- payload hash
- timestamp
- sender IP or origin
- signature verification result
- retry count
- delivery outcome
- DLQ status
- replay status
- consumer target

Required audit events:
- webhook received
- webhook validated
- webhook rejected
- webhook retried
- webhook processed
- webhook failed permanently
- replay initiated
- replay completed

---

## 10. Monitoring and Alerting

### 10.1 Monitoring Signals
PropertyPilot must monitor:
- inbound webhook volume
- failure rate
- retry count
- DLQ size
- validation failures
- latency
- dropped events
- consumer acknowledgments

### 10.2 Alerting Conditions
Alerts should be sent when:
- 5xx failure rate exceeds threshold
- payment callback failure rate exceeds threshold
- DLQ count exceeds operating threshold
- signature validation failure spikes
- replay or duplicate event spikes occur
- external provider outage is detected

### 10.3 Operational Visibility
Dashboards should show:
- webhook success rate
- top failure reasons
- provider status
- event type distribution
- replay and DLQ metrics
- downstream latency

---

## 11. Governance Rules

### 11.1 Contract Ownership
Each webhook contract must have:
- service owner
- integration owner
- security owner
- business owner
- escalation path

### 11.2 Change Control
Webhook changes require:
- versioning review
- schema validation
- backward compatibility review
- test coverage for consumer compatibility
- approval in API governance process

### 11.3 Versioning
Webhook payloads and endpoints must be versioned where business semantics change.

Rules:
- versioned payload or endpoint should be used for breaking changes
- old versions must remain supported during deprecation window
- event naming must remain stable across supported versions

### 11.4 Documentation
Each webhook contract must be documented in:
- event catalog
- API catalog
- OpenAPI definitions where applicable
- this webhook catalog

---

## 12. Related Documents

- `API_Contract_Governance.md`
- Event_Catalog.md
- Notification_Catalog.md
- Technical_Architecture.md

---

## 13. Summary

PropertyPilot’s webhook ecosystem is a critical integration layer for payments, communication delivery, verification, and operational events. To maintain reliability and trust, every webhook must be:
- authenticated
- validated
- idempotent
- retriable
- auditable
- observable
- replay-safe
- governed through review and operational controls

This catalog defines the expected contract and processing model for all inbound and outbound webhook traffic in PropertyPilot.
