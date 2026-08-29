# PropertyPilot Lead API Specification

## Version

1.0

---

# Purpose

The Lead API provides standardized REST endpoints for managing leads within the PropertyPilot platform.

This API serves as the authoritative interface for lead creation, qualification, assignment, nurturing, conversion, tracking, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/leads
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

# Lead Resource

```json
{
  "leadId": "uuid",
  "leadCode": "LEAD-10001",
  "leadName": "John Smith",
  "leadType": "Individual",
  "status": "New",
  "email": "john@example.com",
  "phoneNumber": "+919999999999",
  "leadScore": 75
}
```

---

# Create Lead

## Endpoint

```http
POST /api/v1/leads
```

## Request

```json
{
  "leadName": "John Smith",
  "leadType": "Individual",
  "email": "john@example.com",
  "phoneNumber": "+919999999999",
  "sourceId": "uuid"
}
```

## Response

```http
201 Created
```

```json
{
  "leadId": "uuid",
  "leadCode": "LEAD-10001",
  "status": "New"
}
```

---

# Get Lead By Id

## Endpoint

```http
GET /api/v1/leads/{leadId}
```

---

# Search Leads

## Endpoint

```http
GET /api/v1/leads
```

## Query Parameters

```http
?page=1
&pageSize=20
&status=Qualified
&leadType=Individual
&assignedTo=userId
&search=john
```

---

# Update Lead

## Endpoint

```http
PUT /api/v1/leads/{leadId}
```

---

# Patch Lead

## Endpoint

```http
PATCH /api/v1/leads/{leadId}
```

### Example

```json
{
  "status": "Qualified"
}
```

---

# Delete Lead

## Endpoint

```http
DELETE /api/v1/leads/{leadId}
```

### Response

```http
204 No Content
```

---

# Lead Assignment

## Assign Lead

```http
POST /api/v1/leads/{leadId}/assign
```

### Request

```json
{
  "assignedTo": "userId",
  "assignmentReason": "Regional Assignment"
}
```

---

## Get Assignment History

```http
GET /api/v1/leads/{leadId}/assignments
```

---

# Lead Qualification

## Qualify Lead

```http
POST /api/v1/leads/{leadId}/qualification
```

### Request

```json
{
  "budget": 5000000,
  "timeline": "3 Months",
  "qualificationScore": 85
}
```

---

## Get Qualification

```http
GET /api/v1/leads/{leadId}/qualification
```

---

# Lead Activities

## Create Activity

```http
POST /api/v1/leads/{leadId}/activities
```

### Request

```json
{
  "activityType": "Call",
  "description": "Initial discussion completed"
}
```

---

## Get Activities

```http
GET /api/v1/leads/{leadId}/activities
```

---

# Lead Notes

## Add Note

```http
POST /api/v1/leads/{leadId}/notes
```

### Request

```json
{
  "noteText": "Interested in villa properties"
}
```

---

## Get Notes

```http
GET /api/v1/leads/{leadId}/notes
```

---

# Lead Documents

## Upload Document

```http
POST /api/v1/leads/{leadId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/leads/{leadId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/leads/{leadId}/documents/{documentId}
```

---

# Lead Tags

## Add Tag

```http
POST /api/v1/leads/{leadId}/tags
```

### Request

```json
{
  "tagName": "HighPriority"
}
```

---

## Get Tags

```http
GET /api/v1/leads/{leadId}/tags
```

---

# Lead Conversion

## Convert Lead

```http
POST /api/v1/leads/{leadId}/convert
```

### Request

```json
{
  "customerType": "Individual"
}
```

### Response

```json
{
  "customerId": "uuid",
  "conversionDate": "datetime"
}
```

---

# Lead Scoring

## Recalculate Score

```http
POST /api/v1/leads/{leadId}/score
```

---

## Get Score

```http
GET /api/v1/leads/{leadId}/score
```

---

# Lead Search

## Global Search

```http
GET /api/v1/leads/search
```

### Query

```http
?q=villa
```

---

# Lead Analytics

## Lead Summary

```http
GET /api/v1/leads/{leadId}/summary
```

---

## Lead Metrics

```http
GET /api/v1/leads/{leadId}/metrics
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=leadName
&sortOrder=asc
```

---

# Filtering Standard

```http
?status=Qualified
&leadType=Individual
```

---

# Error Response

```json
{
  "errorCode": "LEAD_NOT_FOUND",
  "message": "Lead not found",
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

- Lead Created
- Lead Updated
- Lead Deleted
- Lead Assigned
- Lead Qualified
- Lead Converted
- Lead Score Updated

---

# Integration Events

Published Events

```text
LeadCreated
LeadUpdated
LeadAssigned
LeadQualified
LeadConverted
LeadDeleted
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

Lead_Data_Model.md

Lead_Management.md

Customer_API.md

CRM_Management.md

API_Governance.md

Integration_Hub.md

Security_Design.md

Identity_Access_Management.md

Reference_Architecture.md

---

# Business Rules

1. Lead codes shall be system generated.

2. Leads shall be tenant isolated.

3. Lead assignment history shall be retained.

4. Lead qualification shall be auditable.

5. Lead conversion shall create customer records.

6. Soft delete shall be used.

7. Lead scoring shall support prioritization.

8. APIs shall support pagination, filtering, and sorting.

9. Sensitive lead data shall be protected.

10. Lead API shall be the authoritative lead service interface.