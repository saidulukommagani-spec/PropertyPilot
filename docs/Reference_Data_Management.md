# PropertyPilot Reference Data Management

## Version

1.0

---

# Purpose

This document defines the governance, ownership, lifecycle, and management of Reference Data within PropertyPilot.

Reference Data Management ensures:

- Consistent business terminology
- Standardized values
- Improved data quality
- Integration consistency
- Reporting accuracy
- Regulatory compliance

---

# Definition

Reference Data represents standardized values used to classify, categorize, validate, and describe business entities.

Reference Data is not transactional data.

Examples:

```text
Country
State
Currency
Language
Property Type
Contract Type
Revenue Type
Lead Source
Partner Type
Vendor Type
```

---

# Objectives

Reference Data shall:

- Have a single source of truth
- Be centrally governed
- Be reusable across domains
- Support integrations
- Support analytics
- Support validation rules

---

# Reference Data Categories

```text
Geographic Reference Data

Financial Reference Data

Customer Reference Data

Property Reference Data

Contract Reference Data

Revenue Reference Data

Platform Reference Data

Security Reference Data
```

---

# Geographic Reference Data

## Country

Examples:

```text
India
United States
United Kingdom
Australia
```

---

## State

Examples:

```text
Telangana
Andhra Pradesh
Karnataka
Tamil Nadu
```

---

## City

Examples:

```text
Hyderabad
Chennai
Bangalore
Mumbai
```

---

# Financial Reference Data

## Currency

Examples:

```text
INR
USD
EUR
GBP
```

---

## Payment Method

Examples:

```text
UPI
Credit Card
Debit Card
NEFT
RTGS
Cash
```

---

## Tax Category

Examples:

```text
GST
VAT
Service Tax
```

---

# Customer Reference Data

## Customer Type

Examples:

```text
Individual
Corporate
Government
Partner
```

---

## Customer Status

Examples:

```text
Active
Inactive
Suspended
Prospect
```

---

# Lead Reference Data

## Lead Source

Examples:

```text
Website
Referral
Partner
Campaign
Social Media
Walk-In
```

---

## Lead Status

Examples:

```text
New
Assigned
Qualified
Converted
Rejected
```

---

# Property Reference Data

## Property Type

Examples:

```text
Residential
Commercial
Industrial
Agricultural
```

---

## Property Category

Examples:

```text
Apartment
Villa
Office
Warehouse
Land
```

---

## Property Status

Examples:

```text
Available
Reserved
Sold
Leased
Inactive
```

---

# Partner Reference Data

## Partner Type

Examples:

```text
Broker
Agency
Consultant
Referral Partner
```

---

## Partner Tier

Examples:

```text
Gold
Silver
Bronze
```

---

# Vendor Reference Data

## Vendor Type

Examples:

```text
Construction
Maintenance
Legal
Financial
Marketing
```

---

## Vendor Status

Examples:

```text
Active
Inactive
Suspended
```

---

# Contract Reference Data

## Contract Type

Examples:

```text
Sale
Lease
Maintenance
Service
Vendor
```

---

## Contract Status

Examples:

```text
Draft
Submitted
Approved
Rejected
Expired
```

---

# Revenue Reference Data

## Revenue Type

Examples:

```text
Property Sale
Lease Revenue
Commission
Service Revenue
```

---

## Revenue Status

Examples:

```text
Planned
Recognized
Collected
Closed
```

---

# Platform Reference Data

## Language

Examples:

```text
English
Telugu
Hindi
Tamil
```

---

## Time Zone

Examples:

```text
IST
UTC
EST
PST
```

---

# Security Reference Data

## User Status

Examples:

```text
Active
Inactive
Locked
Disabled
```

---

## Role Category

Examples:

```text
Admin
Manager
User
Auditor
```

---

# Reference Data Model

## Standard Structure

```json
{
  "referenceId": "uuid",
  "referenceType": "PropertyType",
  "referenceCode": "RES",
  "referenceValue": "Residential",
  "description": "Residential Property",
  "status": "Active",
  "effectiveDate": "2027-01-01",
  "expiryDate": null
}
```

---

# Ownership Model

| Reference Type | Owner |
|----------------|--------|
| Geographic | Data Governance |
| Financial | Finance |
| Customer | CRM |
| Lead | CRM |
| Property | Property Operations |
| Partner | Partner Operations |
| Vendor | Procurement |
| Contract | Legal |
| Revenue | Finance |
| Platform | Platform Engineering |
| Security | Security Team |

---

# Lifecycle Management

```text
Draft
Review
Approved
Active
Deprecated
Retired
```

---

# Change Management

Changes require:

1. Business justification

2. Owner approval

3. Data Governance review

4. Testing

5. Deployment approval

---

# Versioning

Reference Data changes shall support:

```text
Version History

Effective Dating

Audit Trail
```

---

# Effective Dating

Reference Data shall support:

```text
Effective Date

Expiry Date
```

Example:

```text
Property Category = Apartment

Effective: 2027-01-01

Expiry: null
```

---

# Validation Rules

Reference Data shall:

- Prevent duplicates
- Enforce unique codes
- Support active/inactive status
- Support effective dating
- Support audit logging

---

# Integration Standards

Reference Data shall be exposed through:

```text
REST APIs

Events

Bulk Export

Data Warehouse Feeds
```

---

# API Example

```http
GET /api/v1/reference-data/property-types
```

Response:

```json
[
  {
    "code": "RES",
    "value": "Residential"
  }
]
```

---

# Data Quality Requirements

Targets:

| Metric | Target |
|----------|----------|
| Accuracy | 99% |
| Completeness | 99% |
| Consistency | 99% |
| Duplicate Rate | < 1% |

---

# Audit Requirements

All changes shall capture:

```text
Created By

Created Date

Modified By

Modified Date

Reason For Change
```

---

# Governance Rules

1. Reference Data shall have an owner.

2. Reference Data shall have unique codes.

3. Reference Data shall support versioning.

4. Reference Data shall support effective dating.

5. Reference Data shall be auditable.

6. Reference Data shall be centrally governed.

7. Deprecated values shall not be physically deleted.

8. APIs shall use Reference Data whenever applicable.

---

# Reporting Requirements

Reference Data shall be available for:

- BI Reporting
- Analytics
- Data Warehouse
- AI/ML Pipelines

---

# Related Documents

Data_Governance.md

Master_Data_Management.md

Canonical_Data_Model.md

Data_Model_Standards.md

API_Catalog.md

Enterprise_Reporting.md

Business_Intelligence.md

Data_Warehouse_Architecture.md

Architecture_Governance.md