# PropertyPilot Vendor Data Model

## Version

1.0

---

# Purpose

The Vendor Data Model defines the structure, relationships, lifecycle, validation rules, governance standards, and business rules for vendor management within the PropertyPilot platform.

This model serves as the authoritative source for vendor onboarding, procurement, maintenance services, contract management, payments, performance tracking, and vendor ecosystem management.

---

# Objectives

The Vendor Data Model shall:

- Manage vendor lifecycle
- Support vendor onboarding
- Track vendor services
- Manage vendor contracts
- Support procurement activities
- Track vendor performance
- Support payment processing
- Enable vendor analytics
- Support multi-tenant operations
- Ensure vendor compliance

---

# Entity Overview

Primary Entity:

Vendor

Supporting Entities:

VendorContact

VendorAddress

VendorDocument

VendorService

VendorContract

VendorCertification

VendorPerformance

VendorPayment

VendorActivity

VendorNote

VendorTag

VendorAudit

---

# Vendor Lifecycle

Prospective

↓

Invited

↓

Registered

↓

Approved

↓

Active

↓

Suspended

↓

Inactive

↓

Archived

---

# Primary Entity

## Vendor

Represents an individual or organization providing products or services.

---

# Vendor Attributes

| Field | Type | Required |
|---------|---------|---------|
| VendorId | UUID | Yes |
| TenantId | UUID | Yes |
| VendorCode | String(50) | Yes |
| VendorName | String(255) | Yes |
| VendorType | Enum | Yes |
| Status | Enum | Yes |
| Email | String(255) | No |
| PhoneNumber | String(50) | No |
| Website | String(255) | No |
| RegistrationNumber | String(100) | No |
| TaxNumber | String(100) | No |
| Industry | String(100) | No |
| VendorCategory | String(100) | No |
| Rating | Decimal | No |
| ApprovalDate | Date | No |
| ApprovedBy | UUID | No |
| IsActive | Boolean | Yes |
| Notes | Text | No |

---

# Vendor Types

```text
Maintenance
Construction
Security
Cleaning
Utilities
Technology
Consulting
Procurement
Legal
Finance
Other
```

---

# Vendor Status

```text
Prospective
Invited
Registered
Approved
Active
Suspended
Inactive
Archived
```

---

# Vendor Contact Entity

## VendorContact

Stores vendor contacts.

---

### Attributes

| Field | Type |
|---------|---------|
| ContactId | UUID |
| VendorId | UUID |
| ContactName | String |
| Designation | String |
| Email | String |
| Phone | String |
| Mobile | String |
| IsPrimary | Boolean |

---

# Vendor Address Entity

## VendorAddress

Stores vendor addresses.

---

### Attributes

| Field | Type |
|---------|---------|
| AddressId | UUID |
| VendorId | UUID |
| AddressType | Enum |
| AddressLine1 | String |
| AddressLine2 | String |
| City | String |
| State | String |
| Country | String |
| PostalCode | String |

---

# Address Types

```text
Office
Billing
Registered
Warehouse
Branch
Other
```

---

# Vendor Document Entity

## VendorDocument

Stores vendor documents.

---

### Attributes

| Field | Type |
|---------|---------|
| DocumentId | UUID |
| VendorId | UUID |
| DocumentType | String |
| FileName | String |
| FilePath | String |
| UploadDate | DateTime |

---

# Vendor Service Entity

## VendorService

Stores services offered by vendors.

---

### Attributes

| Field | Type |
|---------|---------|
| ServiceId | UUID |
| VendorId | UUID |
| ServiceName | String |
| ServiceCategory | String |
| Description | Text |
| IsActive | Boolean |

---

# Vendor Contract Entity

## VendorContract

Tracks vendor agreements.

---

### Attributes

| Field | Type |
|---------|---------|
| ContractId | UUID |
| VendorId | UUID |
| ContractNumber | String |
| StartDate | Date |
| EndDate | Date |
| ContractValue | Decimal |
| Status | String |

---

# Vendor Certification Entity

## VendorCertification

Stores compliance certifications.

---

### Attributes

| Field | Type |
|---------|---------|
| CertificationId | UUID |
| VendorId | UUID |
| CertificationName | String |
| IssueDate | Date |
| ExpiryDate | Date |
| IssuingAuthority | String |

---

# Vendor Performance Entity

## VendorPerformance

Stores vendor performance metrics.

---

### Attributes

| Field | Type |
|---------|---------|
| PerformanceId | UUID |
| VendorId | UUID |
| ReportingPeriod | String |
| PerformanceScore | Integer |
| SLACompliance | Decimal |
| QualityScore | Decimal |
| DeliveryScore | Decimal |
| CustomerSatisfactionScore | Decimal |

---

# Vendor Payment Entity

## VendorPayment

Tracks vendor payments.

---

### Attributes

| Field | Type |
|---------|---------|
| PaymentId | UUID |
| VendorId | UUID |
| InvoiceNumber | String |
| PaymentAmount | Decimal |
| PaymentDate | Date |
| PaymentStatus | Enum |

---

# Payment Status

```text
Pending
Approved
Paid
Rejected
Cancelled
```

---

# Vendor Activity Entity

## VendorActivity

Tracks vendor interactions.

---

### Attributes

| Field | Type |
|---------|---------|
| ActivityId | UUID |
| VendorId | UUID |
| ActivityType | Enum |
| ActivityDate | DateTime |
| Description | Text |

---

# Activity Types

```text
Call
Email
Meeting
ContractReview
Inspection
ServiceRequest
PaymentReview
Other
```

---

# Vendor Note Entity

## VendorNote

Stores internal notes.

---

### Attributes

| Field | Type |
|---------|---------|
| NoteId | UUID |
| VendorId | UUID |
| NoteText | Text |
| CreatedBy | UUID |
| CreatedDate | DateTime |

---

# Vendor Tag Entity

## VendorTag

Supports categorization.

---

### Attributes

| Field | Type |
|---------|---------|
| TagId | UUID |
| VendorId | UUID |
| TagName | String |

---

# Relationships

```text
Vendor
│
├── VendorContact
├── VendorAddress
├── VendorDocument
├── VendorService
├── VendorContract
├── VendorCertification
├── VendorPerformance
├── VendorPayment
├── VendorActivity
├── VendorNote
└── VendorTag
```

---

# Mandatory Fields

```text
VendorId
TenantId
VendorCode
VendorName
VendorType
Status
IsActive
```

---

# Audit Fields

All entities shall contain:

```text
CreatedBy
CreatedDate
ModifiedBy
ModifiedDate
DeletedBy
DeletedDate
IsDeleted
VersionNumber
```

---

# Validation Rules

## Vendor Code

Must be unique per tenant.

---

## Rating

Range:

```text
0.0 - 5.0
```

---

## Email

Must be valid email format.

---

## Contract Dates

End Date must be greater than Start Date.

---

# Searchable Fields

```text
VendorCode
VendorName
Email
PhoneNumber
VendorType
VendorCategory
Status
Tags
```

---

# Indexing Strategy

Indexes:

```text
TenantId
VendorCode
VendorName
VendorType
Status
CreatedDate
```

---

# Security Controls

Supports:

- Role-Based Access
- Vendor Data Isolation
- Audit Logging
- Tenant Isolation

---

# Analytics Use Cases

Supports:

- Vendor Performance Analysis
- SLA Compliance Analysis
- Procurement Analytics
- Vendor Spend Analysis
- Contract Analysis
- Vendor Risk Analysis

---

# AI Use Cases

Supports:

- Vendor Scoring
- Risk Prediction
- SLA Breach Prediction
- Procurement Optimization
- Vendor Recommendation

---

# Integration Points

Vendor_Management.md

Procurement_Management.md

Contract_Management.md

Inventory_Management.md

Property_Data_Model.md

Revenue_Management.md

Business_Intelligence.md

Enterprise_Reporting.md

Database_Architecture.md

Data_Governance.md

Master_Data_Management.md

---

# Business Rules

1. Every vendor shall belong to a tenant.

2. Vendor codes shall be unique within a tenant.

3. Vendor lifecycle status shall be maintained.

4. Vendor contracts shall be maintained separately.

5. Vendor certifications shall be tracked.

6. Vendor payments shall be auditable.

7. Soft delete shall be used for vendor records.

8. Vendor performance shall be continuously measured.

9. Sensitive vendor information shall be protected.

10. Vendor Data Model shall serve as the authoritative vendor data structure for PropertyPilot.