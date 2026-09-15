# PropertyPilot Canonical Data Model

## Version

1.0

---

# Purpose

The Canonical Data Model (CDM) provides a standardized enterprise-wide representation of business entities used across PropertyPilot.

The CDM serves as the authoritative model for:

- APIs
- Events
- Integrations
- Data Warehouse
- Reporting
- Analytics
- AI/ML
- Master Data Management

---

# Objectives

The Canonical Data Model shall:

- Eliminate data duplication
- Standardize business terminology
- Simplify integrations
- Improve reporting consistency
- Enable event-driven architecture
- Support data governance
- Enable enterprise analytics

---

# Canonical Domains

```text
Lead
Customer
Partner
Vendor
Property
Contract
Revenue
User
Organization
Reference Data
```

---

# Domain Relationship Overview

```text
Lead
 ↓
Customer
 ↓
Contract
 ↓
Revenue

Partner ──┐
           │
           ├── Contract
           │
Vendor ────┘

Property
 ↓
Contract
 ↓
Revenue
```

---

# Canonical Lead

## Business Definition

A prospective customer who has expressed interest in products or services.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| LeadId | UUID |
| LeadCode | String |
| LeadName | String |
| LeadType | String |
| Status | String |
| Source | String |
| Email | String |
| PhoneNumber | String |
| LeadScore | Integer |

---

# Canonical Customer

## Business Definition

An individual or organization engaged in business with PropertyPilot.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| CustomerId | UUID |
| CustomerCode | String |
| CustomerName | String |
| CustomerType | String |
| Status | String |
| Email | String |
| PhoneNumber | String |

---

# Canonical Partner

## Business Definition

An external business entity participating in revenue generation.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| PartnerId | UUID |
| PartnerCode | String |
| PartnerName | String |
| PartnerType | String |
| PartnerLevel | String |
| Status | String |

---

# Canonical Vendor

## Business Definition

An external supplier providing products or services.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| VendorId | UUID |
| VendorCode | String |
| VendorName | String |
| VendorType | String |
| Status | String |

---

# Canonical Property

## Business Definition

A real estate asset managed by PropertyPilot.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| PropertyId | UUID |
| PropertyCode | String |
| PropertyName | String |
| PropertyType | String |
| PropertyCategory | String |
| Status | String |
| MarketValue | Decimal |

---

# Canonical Contract

## Business Definition

A legally binding agreement between parties.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| ContractId | UUID |
| ContractNumber | String |
| ContractType | String |
| ContractTitle | String |
| Status | String |
| EffectiveDate | Date |
| ExpiryDate | Date |
| ContractValue | Decimal |

---

# Canonical Revenue

## Business Definition

Revenue generated through business activities.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| RevenueId | UUID |
| RevenueCode | String |
| RevenueType | String |
| Amount | Decimal |
| CurrencyCode | String |
| Status | String |
| RevenueDate | Date |

---

# Canonical User

## Business Definition

A platform user with assigned roles and permissions.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| UserId | UUID |
| UserName | String |
| Email | String |
| Status | String |
| RoleId | UUID |

---

# Canonical Organization

## Business Definition

A tenant or business entity operating within PropertyPilot.

---

### Canonical Attributes

| Field | Type |
|---------|---------|
| OrganizationId | UUID |
| OrganizationCode | String |
| OrganizationName | String |
| Status | String |

---

# Common Address Model

Used by:

```text
Lead
Customer
Partner
Vendor
Property
Organization
```

---

### Address Structure

```json
{
  "addressLine1": "",
  "addressLine2": "",
  "city": "",
  "state": "",
  "postalCode": "",
  "country": ""
}
```

---

# Common Contact Model

Used by:

```text
Lead
Customer
Partner
Vendor
Organization
```

---

### Contact Structure

```json
{
  "contactName": "",
  "email": "",
  "phoneNumber": "",
  "mobileNumber": ""
}
```

---

# Common Audit Model

All canonical entities shall contain:

```json
{
  "createdBy": "",
  "createdDate": "",
  "modifiedBy": "",
  "modifiedDate": "",
  "isDeleted": false
}
```

---

# Common Tenant Model

All business entities shall contain:

```json
{
  "tenantId": "uuid"
}
```

---

# Reference Data Standards

Reference Data includes:

```text
Country
State
City
Currency
Language
PropertyType
PartnerType
VendorType
ContractType
RevenueType
```

---

# Data Ownership

| Domain | Owner |
|----------|----------|
| Lead | CRM |
| Customer | CRM |
| Partner | Partner Operations |
| Vendor | Procurement |
| Property | Property Operations |
| Contract | Legal |
| Revenue | Finance |
| User | IAM |
| Organization | Platform Operations |

---

# Master Data Entities

```text
Customer
Partner
Vendor
Property
Organization
Reference Data
```

---

# Integration Standards

Canonical model shall be used by:

- REST APIs
- Event Payloads
- Data Warehouse
- Reporting
- AI/ML Pipelines
- External Integrations

---

# Event Mapping

Examples:

```text
LeadCreated → Canonical Lead

CustomerCreated → Canonical Customer

PropertyCreated → Canonical Property

ContractApproved → Canonical Contract

PaymentReceived → Canonical Revenue
```

---

# API Mapping

```text
Lead_API.md
Customer_API.md
Partner_API.md
Vendor_API.md
Property_API.md
Contract_API.md
Revenue_API.md
```

must align with Canonical Data Model definitions.

---

# Data Governance Rules

1. Canonical definitions shall be authoritative.

2. Domain models shall align with canonical models.

3. API payloads shall map to canonical structures.

4. Event payloads shall map to canonical structures.

5. Duplicate business definitions are prohibited.

6. Master data entities shall be governed.

7. Tenant isolation shall be enforced.

8. Canonical models shall be version controlled.

---

# Related Documents

Master_Data_Management.md

Data_Governance.md

API_Catalog.md

Event_Catalog.md

Customer_Data_Model.md

Lead_Data_Model.md

Partner_Data_Model.md

Vendor_Data_Model.md

Property_Data_Model.md

Contract_Data_Model.md

Revenue_Data_Model.md

Integration_Hub.md

Data_Warehouse_Architecture.md

Reference_Architecture.md