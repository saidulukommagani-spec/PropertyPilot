# PropertyPilot Vendor API Specification

## Version

1.0

---

# Purpose

The Vendor API provides standardized REST endpoints for managing vendors within the PropertyPilot platform.

This API serves as the authoritative interface for vendor onboarding, service management, contract management, certifications, payments, performance tracking, procurement integration, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/vendors
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

# Vendor Resource

```json
{
  "vendorId": "uuid",
  "vendorCode": "VEN-10001",
  "vendorName": "ABC Maintenance Services",
  "vendorType": "Maintenance",
  "status": "Active",
  "email": "contact@abcmaintenance.com",
  "phoneNumber": "+919999999999",
  "rating": 4.5
}
```

---

# Create Vendor

## Endpoint

```http
POST /api/v1/vendors
```

## Request

```json
{
  "vendorName": "ABC Maintenance Services",
  "vendorType": "Maintenance",
  "email": "contact@abcmaintenance.com",
  "phoneNumber": "+919999999999"
}
```

## Response

```http
201 Created
```

```json
{
  "vendorId": "uuid",
  "vendorCode": "VEN-10001",
  "status": "Registered"
}
```

---

# Get Vendor By Id

## Endpoint

```http
GET /api/v1/vendors/{vendorId}
```

---

# Search Vendors

## Endpoint

```http
GET /api/v1/vendors
```

### Query Parameters

```http
?page=1
&pageSize=20
&vendorType=Maintenance
&status=Active
&city=Hyderabad
&search=maintenance
```

---

# Update Vendor

## Endpoint

```http
PUT /api/v1/vendors/{vendorId}
```

---

# Patch Vendor

## Endpoint

```http
PATCH /api/v1/vendors/{vendorId}
```

### Example

```json
{
  "status": "Suspended"
}
```

---

# Delete Vendor

## Endpoint

```http
DELETE /api/v1/vendors/{vendorId}
```

### Response

```http
204 No Content
```

---

# Vendor Contacts

## Add Contact

```http
POST /api/v1/vendors/{vendorId}/contacts
```

---

## Get Contacts

```http
GET /api/v1/vendors/{vendorId}/contacts
```

---

## Update Contact

```http
PUT /api/v1/vendors/{vendorId}/contacts/{contactId}
```

---

## Delete Contact

```http
DELETE /api/v1/vendors/{vendorId}/contacts/{contactId}
```

---

# Vendor Services

## Create Service

```http
POST /api/v1/vendors/{vendorId}/services
```

### Request

```json
{
  "serviceName": "Electrical Maintenance",
  "serviceCategory": "Maintenance"
}
```

---

## Get Services

```http
GET /api/v1/vendors/{vendorId}/services
```

---

## Update Service

```http
PUT /api/v1/vendors/{vendorId}/services/{serviceId}
```

---

## Delete Service

```http
DELETE /api/v1/vendors/{vendorId}/services/{serviceId}
```

---

# Vendor Contracts

## Create Contract

```http
POST /api/v1/vendors/{vendorId}/contracts
```

---

## Get Contracts

```http
GET /api/v1/vendors/{vendorId}/contracts
```

---

# Vendor Certifications

## Add Certification

```http
POST /api/v1/vendors/{vendorId}/certifications
```

### Request

```json
{
  "certificationName": "ISO 9001",
  "expiryDate": "2028-12-31"
}
```

---

## Get Certifications

```http
GET /api/v1/vendors/{vendorId}/certifications
```

---

## Delete Certification

```http
DELETE /api/v1/vendors/{vendorId}/certifications/{certificationId}
```

---

# Vendor Payments

## Create Payment

```http
POST /api/v1/vendors/{vendorId}/payments
```

### Request

```json
{
  "invoiceNumber": "INV-10001",
  "paymentAmount": 50000
}
```

---

## Get Payments

```http
GET /api/v1/vendors/{vendorId}/payments
```

---

## Get Payment Summary

```http
GET /api/v1/vendors/{vendorId}/payments/summary
```

---

# Vendor Performance

## Get Performance

```http
GET /api/v1/vendors/{vendorId}/performance
```

---

## Recalculate Performance

```http
POST /api/v1/vendors/{vendorId}/performance/recalculate
```

---

# Vendor Activities

## Create Activity

```http
POST /api/v1/vendors/{vendorId}/activities
```

---

## Get Activities

```http
GET /api/v1/vendors/{vendorId}/activities
```

---

# Vendor Notes

## Add Note

```http
POST /api/v1/vendors/{vendorId}/notes
```

---

## Get Notes

```http
GET /api/v1/vendors/{vendorId}/notes
```

---

# Vendor Documents

## Upload Document

```http
POST /api/v1/vendors/{vendorId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/vendors/{vendorId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/vendors/{vendorId}/documents/{documentId}
```

---

# Vendor Analytics

## Vendor Summary

```http
GET /api/v1/vendors/{vendorId}/summary
```

---

## Vendor Metrics

```http
GET /api/v1/vendors/{vendorId}/metrics
```

---

# Global Search

```http
GET /api/v1/vendors/search?q=maintenance
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=vendorName
&sortOrder=asc
```

---

# Filtering Standard

```http
?vendorType=Maintenance
&status=Active
```

---

# Error Response

```json
{
  "errorCode": "VENDOR_NOT_FOUND",
  "message": "Vendor not found",
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

- Vendor Created
- Vendor Updated
- Vendor Deleted
- Vendor Approved
- Service Added
- Certification Added
- Payment Processed
- Performance Updated

---

# Integration Events

```text
VendorCreated
VendorUpdated
VendorApproved
VendorSuspended
VendorPaymentProcessed
VendorPerformanceUpdated
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

Vendor_Data_Model.md

Vendor_Management.md

Procurement_Management.md

Contract_Data_Model.md

Revenue_Data_Model.md

API_Governance.md

Integration_Hub.md

Security_Design.md

Reference_Architecture.md

---

# Business Rules

1. Vendor codes shall be system generated.

2. Vendors shall be tenant isolated.

3. Vendor certifications shall be tracked.

4. Vendor payments shall be auditable.

5. Vendor contracts shall reference Contract Management.

6. Vendor performance history shall be retained.

7. Soft delete shall be used.

8. APIs shall support pagination, filtering, and sorting.

9. Sensitive vendor information shall be protected.

10. Vendor API shall be the authoritative vendor service interface.