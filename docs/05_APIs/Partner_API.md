# PropertyPilot Partner API Specification

## Version

1.0

---

# Purpose

The Partner API provides standardized REST endpoints for managing partners within the PropertyPilot platform.

This API serves as the authoritative interface for partner onboarding, relationship management, referrals, commissions, contracts, performance tracking, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/partners
```

Authentication

```text
OAuth 2.0
JWT Bearer Token
```

Content Type

```http
application/json
```

---

# API Standards

Supports:

- RESTful Design
- JSON Payloads
- Pagination
- Filtering
- Sorting
- Search
- Versioning
- Audit Logging

---

# Partner Resource

```json
{
  "partnerId": "uuid",
  "partnerCode": "PAR-10001",
  "partnerName": "ABC Realty Partners",
  "partnerType": "Broker",
  "partnerLevel": "Gold",
  "status": "Active",
  "email": "contact@abcpartners.com",
  "phoneNumber": "+919999999999"
}
```

---

# Create Partner

## Endpoint

```http
POST /api/v1/partners
```

## Request

```json
{
  "partnerName": "ABC Realty Partners",
  "partnerType": "Broker",
  "email": "contact@abcpartners.com",
  "phoneNumber": "+919999999999"
}
```

## Response

```http
201 Created
```

```json
{
  "partnerId": "uuid",
  "partnerCode": "PAR-10001",
  "status": "Registered"
}
```

---

# Get Partner By Id

## Endpoint

```http
GET /api/v1/partners/{partnerId}
```

---

# Search Partners

## Endpoint

```http
GET /api/v1/partners
```

### Query Parameters

```http
?page=1
&pageSize=20
&partnerType=Broker
&partnerLevel=Gold
&status=Active
&search=abc
```

---

# Update Partner

## Endpoint

```http
PUT /api/v1/partners/{partnerId}
```

---

# Patch Partner

## Endpoint

```http
PATCH /api/v1/partners/{partnerId}
```

### Example

```json
{
  "status": "Suspended"
}
```

---

# Delete Partner

## Endpoint

```http
DELETE /api/v1/partners/{partnerId}
```

### Response

```http
204 No Content
```

---

# Partner Contacts

## Add Contact

```http
POST /api/v1/partners/{partnerId}/contacts
```

---

## Get Contacts

```http
GET /api/v1/partners/{partnerId}/contacts
```

---

## Update Contact

```http
PUT /api/v1/partners/{partnerId}/contacts/{contactId}
```

---

## Delete Contact

```http
DELETE /api/v1/partners/{partnerId}/contacts/{contactId}
```

---

# Partner Contracts

## Create Contract

```http
POST /api/v1/partners/{partnerId}/contracts
```

---

## Get Contracts

```http
GET /api/v1/partners/{partnerId}/contracts
```

---

# Partner Referrals

## Create Referral

```http
POST /api/v1/partners/{partnerId}/referrals
```

### Request

```json
{
  "leadId": "uuid"
}
```

---

## Get Referrals

```http
GET /api/v1/partners/{partnerId}/referrals
```

---

## Get Referral Details

```http
GET /api/v1/partners/{partnerId}/referrals/{referralId}
```

---

# Partner Commissions

## Get Commissions

```http
GET /api/v1/partners/{partnerId}/commissions
```

---

## Get Commission Summary

```http
GET /api/v1/partners/{partnerId}/commissions/summary
```

---

# Partner Performance

## Get Performance

```http
GET /api/v1/partners/{partnerId}/performance
```

---

## Recalculate Performance

```http
POST /api/v1/partners/{partnerId}/performance/recalculate
```

---

# Partner Activities

## Create Activity

```http
POST /api/v1/partners/{partnerId}/activities
```

---

## Get Activities

```http
GET /api/v1/partners/{partnerId}/activities
```

---

# Partner Notes

## Add Note

```http
POST /api/v1/partners/{partnerId}/notes
```

---

## Get Notes

```http
GET /api/v1/partners/{partnerId}/notes
```

---

# Partner Documents

## Upload Document

```http
POST /api/v1/partners/{partnerId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/partners/{partnerId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/partners/{partnerId}/documents/{documentId}
```

---

# Partner Analytics

## Partner Summary

```http
GET /api/v1/partners/{partnerId}/summary
```

---

## Partner Metrics

```http
GET /api/v1/partners/{partnerId}/metrics
```

---

# Global Search

```http
GET /api/v1/partners/search?q=broker
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=partnerName
&sortOrder=asc
```

---

# Filtering Standard

```http
?partnerType=Broker
&status=Active
```

---

# Error Response

```json
{
  "errorCode": "PARTNER_NOT_FOUND",
  "message": "Partner not found",
  "correlationId": "uuid"
}
```

---

# HTTP Status Codes

| Code | Meaning |
|--------|---------|
| 200 | Success |
| 201 | Created |
| 204 | Deleted |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Validation Error |
| 500 | Internal Server Error |

---

# Security

Supports:

- OAuth2
- JWT Authentication
- Role-Based Access Control
- Tenant Isolation
- Audit Logging

---

# Rate Limiting

```text
1000 Requests / Minute / Tenant
```

---

# Audit Events

Generate Events For:

- Partner Created
- Partner Updated
- Partner Deleted
- Partner Approved
- Referral Created
- Commission Generated
- Performance Updated

---

# Integration Events

```text
PartnerCreated
PartnerUpdated
PartnerApproved
PartnerSuspended
ReferralCreated
CommissionGenerated
```

---

# OpenAPI Compliance

Supports:

```text
OpenAPI 3.1
REST Standards
JSON Schema
```

---

# Related Documents

Partner_Data_Model.md

Partner_Management.md

Lead_Data_Model.md

Contract_Data_Model.md

Revenue_Data_Model.md

API_Governance.md

Integration_Hub.md

Security_Design.md

Reference_Architecture.md

---

# Business Rules

1. Partner codes shall be system generated.

2. Partners shall be tenant isolated.

3. Referral history shall be retained.

4. Commission calculations shall be auditable.

5. Partner contracts shall be linked to contract records.

6. Soft delete shall be used.

7. Partner performance metrics shall be retained historically.

8. APIs shall support pagination, filtering, and sorting.

9. Sensitive partner information shall be protected.

10. Partner API shall be the authoritative partner service interface.