# PropertyPilot Contract API Specification

## Version

1.0

---

# Purpose

The Contract API provides standardized REST endpoints for managing contracts within the PropertyPilot platform.

This API serves as the authoritative interface for contract creation, approval, execution, amendments, renewals, obligations, compliance tracking, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/contracts
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

# Contract Resource

```json
{
  "contractId": "uuid",
  "contractNumber": "CON-10001",
  "contractType": "CustomerContract",
  "contractTitle": "Villa Purchase Agreement",
  "status": "Draft",
  "effectiveDate": "2027-01-01",
  "expiryDate": "2028-01-01",
  "contractValue": 5000000
}
```

---

# Create Contract

## Endpoint

```http
POST /api/v1/contracts
```

## Request

```json
{
  "contractType": "CustomerContract",
  "contractTitle": "Villa Purchase Agreement",
  "effectiveDate": "2027-01-01",
  "contractValue": 5000000
}
```

## Response

```http
201 Created
```

```json
{
  "contractId": "uuid",
  "contractNumber": "CON-10001",
  "status": "Draft"
}
```

---

# Get Contract By Id

## Endpoint

```http
GET /api/v1/contracts/{contractId}
```

---

# Search Contracts

## Endpoint

```http
GET /api/v1/contracts
```

### Query Parameters

```http
?page=1
&pageSize=20
&contractType=CustomerContract
&status=Active
&search=villa
```

---

# Update Contract

## Endpoint

```http
PUT /api/v1/contracts/{contractId}
```

---

# Patch Contract

## Endpoint

```http
PATCH /api/v1/contracts/{contractId}
```

### Example

```json
{
  "status": "UnderReview"
}
```

---

# Delete Contract

## Endpoint

```http
DELETE /api/v1/contracts/{contractId}
```

### Response

```http
204 No Content
```

---

# Contract Parties

## Add Party

```http
POST /api/v1/contracts/{contractId}/parties
```

### Request

```json
{
  "partyType": "Customer",
  "partyReferenceId": "uuid",
  "role": "Buyer"
}
```

---

## Get Parties

```http
GET /api/v1/contracts/{contractId}/parties
```

---

## Remove Party

```http
DELETE /api/v1/contracts/{contractId}/parties/{partyId}
```

---

# Contract Approval Workflow

## Submit For Approval

```http
POST /api/v1/contracts/{contractId}/submit
```

---

## Approve Contract

```http
POST /api/v1/contracts/{contractId}/approve
```

### Request

```json
{
  "comments": "Approved"
}
```

---

## Reject Contract

```http
POST /api/v1/contracts/{contractId}/reject
```

### Request

```json
{
  "comments": "Missing supporting documents"
}
```

---

## Approval History

```http
GET /api/v1/contracts/{contractId}/approvals
```

---

# Contract Amendments

## Create Amendment

```http
POST /api/v1/contracts/{contractId}/amendments
```

### Request

```json
{
  "amendmentReason": "Price Revision"
}
```

---

## Get Amendments

```http
GET /api/v1/contracts/{contractId}/amendments
```

---

# Contract Renewals

## Renew Contract

```http
POST /api/v1/contracts/{contractId}/renew
```

### Request

```json
{
  "newExpiryDate": "2029-01-01"
}
```

---

## Renewal History

```http
GET /api/v1/contracts/{contractId}/renewals
```

---

# Contract Obligations

## Create Obligation

```http
POST /api/v1/contracts/{contractId}/obligations
```

### Request

```json
{
  "obligationTitle": "Monthly Maintenance Payment",
  "dueDate": "2027-03-01"
}
```

---

## Get Obligations

```http
GET /api/v1/contracts/{contractId}/obligations
```

---

## Update Obligation

```http
PUT /api/v1/contracts/{contractId}/obligations/{obligationId}
```

---

# Contract Milestones

## Create Milestone

```http
POST /api/v1/contracts/{contractId}/milestones
```

---

## Get Milestones

```http
GET /api/v1/contracts/{contractId}/milestones
```

---

# Contract Documents

## Upload Document

```http
POST /api/v1/contracts/{contractId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/contracts/{contractId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/contracts/{contractId}/documents/{documentId}
```

---

# Contract Notes

## Add Note

```http
POST /api/v1/contracts/{contractId}/notes
```

---

## Get Notes

```http
GET /api/v1/contracts/{contractId}/notes
```

---

# Contract Activities

## Create Activity

```http
POST /api/v1/contracts/{contractId}/activities
```

---

## Get Activities

```http
GET /api/v1/contracts/{contractId}/activities
```

---

# Contract Analytics

## Contract Summary

```http
GET /api/v1/contracts/{contractId}/summary
```

---

## Contract Metrics

```http
GET /api/v1/contracts/{contractId}/metrics
```

---

## Expiry Dashboard

```http
GET /api/v1/contracts/expiry-dashboard
```

---

# Global Search

```http
GET /api/v1/contracts/search?q=agreement
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=contractNumber
&sortOrder=asc
```

---

# Filtering Standard

```http
?status=Active
&contractType=CustomerContract
```

---

# Error Response

```json
{
  "errorCode": "CONTRACT_NOT_FOUND",
  "message": "Contract not found",
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

- Contract Created
- Contract Updated
- Contract Submitted
- Contract Approved
- Contract Rejected
- Contract Renewed
- Contract Amended
- Contract Deleted

---

# Integration Events

```text
ContractCreated
ContractUpdated
ContractSubmitted
ContractApproved
ContractRejected
ContractRenewed
ContractExpired
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

Contract_Data_Model.md

Contract_Management.md

Customer_Data_Model.md

Partner_Data_Model.md

Vendor_Data_Model.md

Revenue_Data_Model.md

Compliance_Management.md

Risk_Management.md

API_Governance.md

Integration_Hub.md

Reference_Architecture.md

---

# Business Rules

1. Contract numbers shall be system generated.

2. Contracts shall be tenant isolated.

3. Contracts must be approved before activation.

4. Amendment history shall be retained permanently.

5. Renewal history shall be retained permanently.

6. Contract obligations shall be tracked separately.

7. Soft delete shall be used.

8. APIs shall support pagination, filtering, and sorting.

9. Sensitive contract information shall be protected.

10. Contract API shall be the authoritative contract service interface.