# PropertyPilot Customer API Specification

## Version

1.0

---

# Purpose

The Customer API provides standardized REST endpoints for managing customer data within the PropertyPilot platform.

This API serves as the authoritative interface for customer creation, maintenance, search, lifecycle management, integrations, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/customers
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

# Customer Resource

```json
{
  "customerId": "uuid",
  "customerCode": "CUS-10001",
  "customerName": "John Smith",
  "customerType": "Individual",
  "status": "Active",
  "email": "john@example.com",
  "phoneNumber": "+919999999999",
  "isActive": true
}
```

---

# Create Customer

## Endpoint

```http
POST /api/v1/customers
```

## Request

```json
{
  "customerName": "John Smith",
  "customerType": "Individual",
  "email": "john@example.com",
  "phoneNumber": "+919999999999"
}
```

## Response

```http
201 Created
```

```json
{
  "customerId": "uuid",
  "customerCode": "CUS-10001",
  "status": "Active"
}
```

---

# Get Customer By Id

## Endpoint

```http
GET /api/v1/customers/{customerId}
```

## Response

```http
200 OK
```

```json
{
  "customerId": "uuid",
  "customerCode": "CUS-10001",
  "customerName": "John Smith",
  "status": "Active"
}
```

---

# Search Customers

## Endpoint

```http
GET /api/v1/customers
```

## Query Parameters

```http
?page=1
&pageSize=20
&status=Active
&customerType=Individual
&search=john
```

## Response

```json
{
  "totalRecords": 100,
  "page": 1,
  "pageSize": 20,
  "items": []
}
```

---

# Update Customer

## Endpoint

```http
PUT /api/v1/customers/{customerId}
```

## Request

```json
{
  "customerName": "John Smith Updated",
  "email": "john.updated@example.com"
}
```

## Response

```http
200 OK
```

---

# Patch Customer

## Endpoint

```http
PATCH /api/v1/customers/{customerId}
```

## Request

```json
{
  "status": "Inactive"
}
```

## Response

```http
200 OK
```

---

# Delete Customer

## Endpoint

```http
DELETE /api/v1/customers/{customerId}
```

## Response

```http
204 No Content
```

---

# Customer Addresses

## Add Address

```http
POST /api/v1/customers/{customerId}/addresses
```

### Request

```json
{
  "addressType": "Home",
  "addressLine1": "Street 1",
  "city": "Hyderabad",
  "state": "Telangana",
  "country": "India"
}
```

---

## Get Addresses

```http
GET /api/v1/customers/{customerId}/addresses
```

---

## Update Address

```http
PUT /api/v1/customers/{customerId}/addresses/{addressId}
```

---

## Delete Address

```http
DELETE /api/v1/customers/{customerId}/addresses/{addressId}
```

---

# Customer Contacts

## Add Contact

```http
POST /api/v1/customers/{customerId}/contacts
```

---

## Get Contacts

```http
GET /api/v1/customers/{customerId}/contacts
```

---

## Update Contact

```http
PUT /api/v1/customers/{customerId}/contacts/{contactId}
```

---

## Delete Contact

```http
DELETE /api/v1/customers/{customerId}/contacts/{contactId}
```

---

# Customer Activities

## Create Activity

```http
POST /api/v1/customers/{customerId}/activities
```

### Request

```json
{
  "activityType": "Call",
  "description": "Customer discussion"
}
```

---

## Get Activities

```http
GET /api/v1/customers/{customerId}/activities
```

---

# Customer Notes

## Add Note

```http
POST /api/v1/customers/{customerId}/notes
```

### Request

```json
{
  "noteText": "Important customer"
}
```

---

## Get Notes

```http
GET /api/v1/customers/{customerId}/notes
```

---

# Customer Documents

## Upload Document

```http
POST /api/v1/customers/{customerId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/customers/{customerId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/customers/{customerId}/documents/{documentId}
```

---

# Customer Tags

## Add Tag

```http
POST /api/v1/customers/{customerId}/tags
```

### Request

```json
{
  "tagName": "Premium"
}
```

---

## Get Tags

```http
GET /api/v1/customers/{customerId}/tags
```

---

## Delete Tag

```http
DELETE /api/v1/customers/{customerId}/tags/{tagId}
```

---

# Customer Search

## Global Search

```http
GET /api/v1/customers/search
```

### Query Parameters

```http
?q=john
```

---

# Customer Analytics

## Customer Summary

```http
GET /api/v1/customers/{customerId}/summary
```

---

## Customer Metrics

```http
GET /api/v1/customers/{customerId}/metrics
```

---

# Pagination Standard

## Request

```http
?page=1&pageSize=20
```

## Response

```json
{
  "page": 1,
  "pageSize": 20,
  "totalRecords": 100,
  "totalPages": 5,
  "items": []
}
```

---

# Sorting Standard

```http
?sortBy=customerName
&sortOrder=asc
```

---

# Filtering Standard

```http
?status=Active
&customerType=Individual
```

---

# Error Response

```json
{
  "errorCode": "CUSTOMER_NOT_FOUND",
  "message": "Customer not found",
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

Default:

```text
1000 Requests / Minute / Tenant
```

---

# Audit Events

Generate Events For:

- Customer Created
- Customer Updated
- Customer Deleted
- Address Added
- Contact Added
- Activity Added
- Document Uploaded

---

# Integration Events

Published Events

```text
CustomerCreated
CustomerUpdated
CustomerDeleted
CustomerActivated
CustomerDeactivated
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

Customer_Data_Model.md

Customer_Management.md

CRM_Management.md

API_Governance.md

Integration_Hub.md

Security_Design.md

Identity_Access_Management.md

Reference_Architecture.md

---

# Business Rules

1. Customer codes shall be system generated.

2. Customer records shall be tenant isolated.

3. Soft delete shall be used.

4. Customer activities shall be auditable.

5. Sensitive data shall be protected.

6. APIs shall support pagination.

7. APIs shall support filtering and sorting.

8. Customer API shall be the authoritative customer service interface.