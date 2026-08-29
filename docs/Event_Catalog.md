# PropertyPilot Event Catalog

## Version

1.0

---

# Purpose

The Event Catalog provides a centralized inventory of all business, domain, integration, and platform events published and consumed within PropertyPilot.

The catalog serves as the authoritative source for:

- Event Discovery
- Event Governance
- Event Ownership
- Event Standardization
- Event-Driven Architecture
- Integration Management
- Event Lifecycle Management

---

# Event Categories

PropertyPilot events are organized into:

```text
Business Events
Domain Events
Integration Events
System Events
Audit Events
Platform Events
```

---

# Event Naming Standard

Format:

```text
<Entity><Action>
```

Examples:

```text
LeadCreated
LeadQualified
LeadConverted

CustomerCreated
CustomerUpdated

PropertyListed
PropertyReserved

ContractApproved
ContractExpired

InvoiceGenerated
PaymentReceived
```

---

# Event Metadata Standard

All events shall contain:

```json
{
  "eventId": "uuid",
  "eventType": "LeadCreated",
  "eventVersion": "1.0",
  "eventTimestamp": "datetime",
  "tenantId": "uuid",
  "sourceSystem": "PropertyPilot",
  "correlationId": "uuid",
  "payload": {}
}
```

---

# Lead Management Events

## LeadCreated

Description:

A new lead is created.

Publisher:

Lead Service

Consumers:

CRM
Analytics
Notification Service

---

## LeadAssigned

Publisher:

Lead Service

Consumers:

CRM
Task Management

---

## LeadQualified

Publisher:

Lead Service

Consumers:

Sales
Analytics

---

## LeadConverted

Publisher:

Lead Service

Consumers:

Customer Service
Revenue Service
Reporting

---

## LeadDeleted

Publisher:

Lead Service

Consumers:

Audit
Reporting

---

# Customer Management Events

## CustomerCreated

Publisher:

Customer Service

Consumers:

Property Service
Contract Service
Reporting

---

## CustomerUpdated

Publisher:

Customer Service

Consumers:

Reporting
Analytics

---

## CustomerActivated

Publisher:

Customer Service

Consumers:

Security
Reporting

---

## CustomerDeactivated

Publisher:

Customer Service

Consumers:

Security
Reporting

---

# Partner Management Events

## PartnerCreated

Publisher:

Partner Service

Consumers:

Reporting
Analytics

---

## PartnerApproved

Publisher:

Partner Service

Consumers:

Revenue Service

---

## ReferralCreated

Publisher:

Partner Service

Consumers:

Lead Service

---

## CommissionGenerated

Publisher:

Revenue Service

Consumers:

Partner Service
Finance

---

# Vendor Management Events

## VendorCreated

Publisher:

Vendor Service

Consumers:

Procurement
Reporting

---

## VendorApproved

Publisher:

Vendor Service

Consumers:

Procurement

---

## VendorPaymentProcessed

Publisher:

Vendor Service

Consumers:

Finance

---

# Property Management Events

## PropertyCreated

Publisher:

Property Service

Consumers:

Search Service
Reporting

---

## PropertyListed

Publisher:

Property Service

Consumers:

Marketing
Search Service

---

## PropertyReserved

Publisher:

Property Service

Consumers:

Contract Service

---

## PropertyOccupied

Publisher:

Property Service

Consumers:

Reporting

---

## PropertyValuationUpdated

Publisher:

Property Service

Consumers:

Analytics

---

# Contract Management Events

## ContractCreated

Publisher:

Contract Service

Consumers:

Compliance
Reporting

---

## ContractSubmitted

Publisher:

Contract Service

Consumers:

Approval Workflow

---

## ContractApproved

Publisher:

Contract Service

Consumers:

Revenue Service
Compliance

---

## ContractRejected

Publisher:

Contract Service

Consumers:

Reporting

---

## ContractRenewed

Publisher:

Contract Service

Consumers:

Revenue Service

---

## ContractExpired

Publisher:

Contract Service

Consumers:

Compliance
Reporting

---

# Revenue Management Events

## RevenueCreated

Publisher:

Revenue Service

Consumers:

Finance
Reporting

---

## InvoiceGenerated

Publisher:

Revenue Service

Consumers:

Customer Service
Notification Service

---

## PaymentReceived

Publisher:

Revenue Service

Consumers:

Reporting
Analytics

---

## RefundProcessed

Publisher:

Revenue Service

Consumers:

Finance

---

## RevenueClosed

Publisher:

Revenue Service

Consumers:

Reporting

---

# Platform Events

## UserCreated

Publisher:

Identity Service

Consumers:

Security
Audit

---

## UserRoleAssigned

Publisher:

IAM Service

Consumers:

Audit

---

## NotificationSent

Publisher:

Notification Service

Consumers:

Audit

---

# Audit Events

## AuditRecordCreated

Publisher:

Audit Service

Consumers:

Compliance

---

## SecurityViolationDetected

Publisher:

Security Service

Consumers:

SOC
Compliance

---

# Event Delivery Standards

Supported Mechanisms:

```text
Kafka
RabbitMQ
Azure Service Bus
AWS SNS/SQS
Webhook
REST Callback
```

---

# Event Versioning

Example:

```text
LeadCreated v1
LeadCreated v2
```

Rules:

- Breaking changes require new version.
- Consumers must support version upgrades.
- Events shall remain backward compatible whenever possible.

---

# Event Lifecycle

```text
Draft
Approved
Published
Deprecated
Retired
```

---

# Event Governance Rules

1. Every event shall have an owner.

2. Every event shall have a schema.

3. Events shall be versioned.

4. Events shall be documented in Event Catalog.

5. Events shall support tenant isolation.

6. Events shall be auditable.

7. Event payloads shall avoid sensitive information.

8. Events shall support correlation tracking.

---

# Event Schema Registry

All event schemas shall be registered in:

```text
Schema Registry
```

Supported Formats:

```text
JSON Schema
Avro
Protobuf
```

---

# Related Documents

Event_Driven_Architecture.md

Integration_Hub.md

API_Catalog.md

Canonical_Data_Model.md

Service_Catalog.md

API_Governance.md

Reference_Architecture.md

Observability_Monitoring.md

Architecture_Governance.md