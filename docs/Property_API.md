# PropertyPilot Property API Specification

## Version

1.0

---

# Purpose

The Property API provides standardized REST endpoints for managing properties within the PropertyPilot platform.

This API serves as the authoritative interface for property creation, maintenance, listing management, ownership management, valuation tracking, inspections, maintenance activities, analytics, and reporting.

---

# API Overview

Base URL

```http
/api/v1/properties
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

# Property Resource

```json
{
  "propertyId": "uuid",
  "propertyCode": "PROP-10001",
  "propertyName": "Sunrise Villas",
  "propertyType": "Residential",
  "propertyCategory": "Villa",
  "status": "Available",
  "marketValue": 7500000,
  "isActive": true
}
```

---

# Create Property

## Endpoint

```http
POST /api/v1/properties
```

## Request

```json
{
  "propertyName": "Sunrise Villas",
  "propertyType": "Residential",
  "propertyCategory": "Villa",
  "marketValue": 7500000
}
```

## Response

```http
201 Created
```

```json
{
  "propertyId": "uuid",
  "propertyCode": "PROP-10001",
  "status": "Available"
}
```

---

# Get Property By Id

## Endpoint

```http
GET /api/v1/properties/{propertyId}
```

---

# Search Properties

## Endpoint

```http
GET /api/v1/properties
```

### Query Parameters

```http
?page=1
&pageSize=20
&propertyType=Residential
&propertyCategory=Villa
&status=Available
&city=Hyderabad
&search=sunrise
```

---

# Update Property

## Endpoint

```http
PUT /api/v1/properties/{propertyId}
```

---

# Patch Property

## Endpoint

```http
PATCH /api/v1/properties/{propertyId}
```

### Example

```json
{
  "status": "Reserved"
}
```

---

# Delete Property

## Endpoint

```http
DELETE /api/v1/properties/{propertyId}
```

### Response

```http
204 No Content
```

---

# Property Addresses

## Add Address

```http
POST /api/v1/properties/{propertyId}/addresses
```

---

## Get Addresses

```http
GET /api/v1/properties/{propertyId}/addresses
```

---

## Update Address

```http
PUT /api/v1/properties/{propertyId}/addresses/{addressId}
```

---

## Delete Address

```http
DELETE /api/v1/properties/{propertyId}/addresses/{addressId}
```

---

# Property Owners

## Add Owner

```http
POST /api/v1/properties/{propertyId}/owners
```

### Request

```json
{
  "customerId": "uuid",
  "ownershipPercentage": 100
}
```

---

## Get Owners

```http
GET /api/v1/properties/{propertyId}/owners
```

---

## Update Owner

```http
PUT /api/v1/properties/{propertyId}/owners/{ownerId}
```

---

## Delete Owner

```http
DELETE /api/v1/properties/{propertyId}/owners/{ownerId}
```

---

# Property Units

## Create Unit

```http
POST /api/v1/properties/{propertyId}/units
```

---

## Get Units

```http
GET /api/v1/properties/{propertyId}/units
```

---

## Update Unit

```http
PUT /api/v1/properties/{propertyId}/units/{unitId}
```

---

## Delete Unit

```http
DELETE /api/v1/properties/{propertyId}/units/{unitId}
```

---

# Property Amenities

## Add Amenity

```http
POST /api/v1/properties/{propertyId}/amenities
```

### Request

```json
{
  "amenityName": "Swimming Pool"
}
```

---

## Get Amenities

```http
GET /api/v1/properties/{propertyId}/amenities
```

---

## Delete Amenity

```http
DELETE /api/v1/properties/{propertyId}/amenities/{amenityId}
```

---

# Property Listings

## Create Listing

```http
POST /api/v1/properties/{propertyId}/listings
```

### Request

```json
{
  "listingPrice": 8000000,
  "marketplaceSource": "Website"
}
```

---

## Get Listings

```http
GET /api/v1/properties/{propertyId}/listings
```

---

## Update Listing

```http
PUT /api/v1/properties/{propertyId}/listings/{listingId}
```

---

# Property Valuations

## Create Valuation

```http
POST /api/v1/properties/{propertyId}/valuations
```

### Request

```json
{
  "marketValue": 8500000,
  "valuationMethod": "Comparative Market Analysis"
}
```

---

## Get Valuations

```http
GET /api/v1/properties/{propertyId}/valuations
```

---

# Property Inspections

## Schedule Inspection

```http
POST /api/v1/properties/{propertyId}/inspections
```

---

## Get Inspections

```http
GET /api/v1/properties/{propertyId}/inspections
```

---

# Property Maintenance

## Create Maintenance Request

```http
POST /api/v1/properties/{propertyId}/maintenance
```

### Request

```json
{
  "description": "Water leakage repair",
  "assignedVendorId": "uuid"
}
```

---

## Get Maintenance Requests

```http
GET /api/v1/properties/{propertyId}/maintenance
```

---

## Update Maintenance Request

```http
PUT /api/v1/properties/{propertyId}/maintenance/{maintenanceId}
```

---

# Property Documents

## Upload Document

```http
POST /api/v1/properties/{propertyId}/documents
```

Content Type:

```http
multipart/form-data
```

---

## Get Documents

```http
GET /api/v1/properties/{propertyId}/documents
```

---

## Delete Document

```http
DELETE /api/v1/properties/{propertyId}/documents/{documentId}
```

---

# Property Images

## Upload Image

```http
POST /api/v1/properties/{propertyId}/images
```

---

## Get Images

```http
GET /api/v1/properties/{propertyId}/images
```

---

## Delete Image

```http
DELETE /api/v1/properties/{propertyId}/images/{imageId}
```

---

# Property Analytics

## Property Summary

```http
GET /api/v1/properties/{propertyId}/summary
```

---

## Property Metrics

```http
GET /api/v1/properties/{propertyId}/metrics
```

---

## Occupancy Metrics

```http
GET /api/v1/properties/{propertyId}/occupancy
```

---

# Global Search

```http
GET /api/v1/properties/search?q=villa
```

---

# Pagination Standard

```http
?page=1&pageSize=20
```

---

# Sorting Standard

```http
?sortBy=propertyName
&sortOrder=asc
```

---

# Filtering Standard

```http
?propertyType=Residential
&status=Available
```

---

# Error Response

```json
{
  "errorCode": "PROPERTY_NOT_FOUND",
  "message": "Property not found",
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

- Property Created
- Property Updated
- Property Deleted
- Property Listed
- Property Reserved
- Property Occupied
- Property Valuated
- Property Inspected

---

# Integration Events

```text
PropertyCreated
PropertyUpdated
PropertyDeleted
PropertyListed
PropertyReserved
PropertyOccupied
PropertyValuationUpdated
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

Property_Data_Model.md

Property_Management.md

Customer_Data_Model.md

Vendor_Data_Model.md

Contract_Data_Model.md

API_Governance.md

Integration_Hub.md

Security_Design.md

Reference_Architecture.md

---

# Business Rules

1. Property codes shall be system generated.

2. Property records shall be tenant isolated.

3. Ownership percentages shall total 100%.

4. Property valuation history shall be retained.

5. Property listings shall be auditable.

6. Property maintenance activities shall be tracked.

7. Soft delete shall be used.

8. APIs shall support pagination, filtering, and sorting.

9. Sensitive property information shall be protected.

10. Property API shall be the authoritative property service interface.