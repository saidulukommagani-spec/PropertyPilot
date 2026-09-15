# PropertyPilot Revenue API Specification

## Version

1.0

---

# Purpose

The Revenue API provides standardized REST endpoints for managing revenue, invoices, payments, collections, commissions, forecasting, and revenue analytics within the PropertyPilot platform.

This API serves as the authoritative interface for revenue lifecycle management.

---

# API Overview

Base URL

```http
/api/v1/revenues
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

# Revenue Resource

```json
{
  "revenueId": "uuid",
  "revenueCode": "REV-10001",
  "revenueType": "PropertySale",
  "customerId": "uuid",
  "propertyId": "uuid",
  "contractId": "uuid",
  "amount": 5000000,
  "currencyCode": "INR",
  "status": "Recognized"
}
```

---

# Create Revenue

## Endpoint

```http
POST /api/v1/revenues
```

## Request

```json
{
  "revenueType": "PropertySale",
  "customerId": "uuid",
  "propertyId": "uuid",
  "contractId": "uuid",
  "amount": 5000000,
  "currencyCode": "INR"
}
```

## Response

```http
201 Created
```

```json
{
  "revenueId": "uuid",
  "revenueCode": "REV-10001",
  "status": "Planned"
}
```

---

# Get Revenue By Id

## Endpoint

```http
GET /api/v1/revenues/{revenueId}
```

---

# Search Revenue

## Endpoint

```http
GET /api/v1/revenues
```

### Query Parameters

```http
?page=1
&pageSize=20
&revenueType=PropertySale
&status=Collected
&customerId=uuid
```

---

# Update Revenue

## Endpoint

```http
PUT /api/v1/revenues/{revenueId}
```

---

# Delete Revenue

## Endpoint

```http
DELETE /api/v1/revenues/{revenueId}
```

### Response

```http
204 No Content
```

---

# Revenue Invoices

## Create Invoice

```http
POST /api/v1/revenues/{revenueId}/invoices
```

### Request

```json
{
  "invoiceDate": "2027-01-15",
  "dueDate": "2027-02-15",
  "invoiceAmount": 5000000
}
```

---

## Get Invoices

```http
GET /api/v1/revenues/{revenueId}/invoices
```

---

## Get Invoice

```http
GET /api/v1/revenues/{revenueId}/invoices/{invoiceId}
```

---

# Revenue Payments

## Record Payment

```http
POST /api/v1/revenues/{revenueId}/payments
```

### Request

```json
{
  "invoiceId": "uuid",
  "paymentAmount": 1000000,
  "paymentMethod": "UPI"
}
```

---

## Get Payments

```http
GET /api/v1/revenues/{revenueId}/payments
```

---

## Payment Summary

```http
GET /api/v1/revenues/{revenueId}/payments/summary
```

---

# Revenue Refunds

## Create Refund

```http
POST /api/v1/revenues/{revenueId}/refunds
```

### Request

```json
{
  "refundAmount": 50000,
  "reason": "Duplicate Payment"
}
```

---

## Get Refunds

```http
GET /api/v1/revenues/{revenueId}/refunds
```

---

# Revenue Adjustments

## Create Adjustment

```http
POST /api/v1/revenues/{revenueId}/adjustments
```

### Request

```json
{
  "adjustmentAmount": 10000,
  "reason": "Price Correction"
}
```

---

## Get Adjustments

```http
GET /api/v1/revenues/{revenueId}/adjustments
```

---

# Revenue Commissions

## Generate Commission

```http
POST /api/v1/revenues/{revenueId}/commissions
```

---

## Get Commissions

```http
GET /api/v1/revenues/{revenueId}/commissions
```

---

## Commission Summary

```http
GET /api/v1/revenues/{revenueId}/commissions/summary
```

---

# Revenue Forecasting

## Create Forecast

```http
POST /api/v1/revenues/forecasts
```

---

## Get Forecasts

```http
GET /api/v1/revenues/forecasts
```

---

## Revenue Forecast Summary

```http
GET /api/v1/revenues/forecasts/summary
```

---

# Revenue Analytics

## Revenue Summary

```http
GET /api/v1/revenues/summary
```

---

## Revenue Metrics

```http
GET /api/v1/revenues/metrics
```

---

## Collection Dashboard

```http
GET /api/v1/revenues/collections/dashboard
```

---

## Forecast Dashboard

```http
GET /api/v1/revenues/forecasts/dashboard
```

---

# Revenue Notes

## Add Note

```http
POST /api/v1/revenues/{revenueId}/notes
```

---

## Get Notes

```http
GET /api/v1/revenues/{revenueId}/notes
```

---

# Revenue Activities

## Create Activity

```http
POST /api/v1/revenues/{revenueId}/activities
```

---

## Get Activities

```http
GET /api/v1/revenues/{revenueId}/activities
```

---

# Global Search

```http
GET /api/v1/revenues/search?q=sale
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=revenueDate
&sortOrder=desc
```

---

# Filtering Standard

```http
?status=Collected
&revenueType=PropertySale
```

---

# Error Response

```json
{
  "errorCode": "REVENUE_NOT_FOUND",
  "message": "Revenue record not found",
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

- Revenue Created
- Revenue Updated
- Invoice Generated
- Payment Recorded
- Refund Processed
- Commission Generated
- Revenue Deleted

---

# Integration Events

```text
RevenueCreated
RevenueUpdated
InvoiceGenerated
PaymentReceived
RefundProcessed
CommissionGenerated
RevenueClosed
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

Revenue_Data_Model.md

Revenue_Management.md

Contract_Data_Model.md

Customer_Data_Model.md

Partner_Data_Model.md

FinOps_Architecture.md

Business_Intelligence.md

Enterprise_Reporting.md

API_Governance.md

Integration_Hub.md

Reference_Architecture.md

---

# Business Rules

1. Revenue codes shall be system generated.

2. Revenue records shall be tenant isolated.

3. Revenue recognition shall be auditable.

4. Partial payments shall be supported.

5. Invoice history shall be retained permanently.

6. Refunds and adjustments shall be fully traceable.

7. Commission calculations shall be auditable.

8. Soft delete shall be used.

9. APIs shall support pagination, filtering, and sorting.

10. Revenue API shall be the authoritative revenue service interface.