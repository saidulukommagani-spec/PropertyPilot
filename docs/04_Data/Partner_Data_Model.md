# PropertyPilot Partner Data Model

## Version

1.0

---

# Purpose

The Partner Data Model defines the structure, relationships, lifecycle, validation rules, governance standards, and business rules for partner management within the PropertyPilot platform.

This model serves as the authoritative source for partner onboarding, relationship management, referrals, commissions, contracts, performance tracking, and partner ecosystem operations.

---

# Objectives

The Partner Data Model shall:

- Manage partner lifecycle
- Support partner onboarding
- Track referrals
- Manage partner contracts
- Track commissions
- Support partner performance management
- Enable partner analytics
- Support multi-tenant operations
- Improve partner engagement
- Support revenue growth

---

# Entity Overview

Primary Entity:

Partner

Supporting Entities:

PartnerContact

PartnerAddress

PartnerDocument

PartnerContract

PartnerCommission

PartnerReferral

PartnerPerformance

PartnerActivity

PartnerNote

PartnerTag

PartnerAudit

---

# Partner Lifecycle

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

## Partner

Represents an individual or organization participating in the PropertyPilot partner ecosystem.

---

# Partner Attributes

| Field | Type | Required |
|---------|---------|---------|
| PartnerId | UUID | Yes |
| TenantId | UUID | Yes |
| PartnerCode | String(50) | Yes |
| PartnerType | Enum | Yes |
| PartnerName | String(255) | Yes |
| Status | Enum | Yes |
| Email | String(255) | No |
| PhoneNumber | String(50) | No |
| Website | String(255) | No |
| RegistrationNumber | String(100) | No |
| TaxNumber | String(100) | No |
| Industry | String(100) | No |
| PartnerLevel | Enum | No |
| ReferralPercentage | Decimal | No |
| JoinDate | Date | Yes |
| ApprovalDate | Date | No |
| ApprovedBy | UUID | No |
| IsActive | Boolean | Yes |
| Notes | Text | No |

---

# Partner Types

```text
Broker
Agent
ReferralPartner
VendorPartner
TechnologyPartner
ChannelPartner
StrategicPartner
FranchisePartner
Other
```

---

# Partner Status

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

# Partner Levels

```text
Silver
Gold
Platinum
Diamond
Strategic
```

---

# Partner Contact Entity

## PartnerContact

Stores partner contact information.

---

### Attributes

| Field | Type |
|---------|---------|
| ContactId | UUID |
| PartnerId | UUID |
| ContactName | String |
| Designation | String |
| Email | String |
| Phone | String |
| Mobile | String |
| IsPrimary | Boolean |

---

# Partner Address Entity

## PartnerAddress

Stores partner location information.

---

### Attributes

| Field | Type |
|---------|---------|
| AddressId | UUID |
| PartnerId | UUID |
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
Branch
Other
```

---

# Partner Document Entity

## PartnerDocument

Stores partner documents.

---

### Attributes

| Field | Type |
|---------|---------|
| DocumentId | UUID |
| PartnerId | UUID |
| DocumentType | String |
| FileName | String |
| FilePath | String |
| UploadDate | DateTime |
| UploadedBy | UUID |

---

# Partner Contract Entity

## PartnerContract

Tracks partner agreements.

---

### Attributes

| Field | Type |
|---------|---------|
| ContractId | UUID |
| PartnerId | UUID |
| ContractNumber | String |
| ContractType | String |
| StartDate | Date |
| EndDate | Date |
| Status | String |

---

# Partner Commission Entity

## PartnerCommission

Tracks commissions payable.

---

### Attributes

| Field | Type |
|---------|---------|
| CommissionId | UUID |
| PartnerId | UUID |
| ReferralId | UUID |
| CommissionAmount | Decimal |
| CommissionPercentage | Decimal |
| Status | Enum |
| PaymentDate | Date |

---

# Commission Status

```text
Pending
Approved
Paid
Cancelled
Disputed
```

---

# Partner Referral Entity

## PartnerReferral

Tracks referrals submitted by partners.

---

### Attributes

| Field | Type |
|---------|---------|
| ReferralId | UUID |
| PartnerId | UUID |
| LeadId | UUID |
| ReferralDate | DateTime |
| ReferralStatus | Enum |
| ReferralValue | Decimal |

---

# Referral Status

```text
Submitted
Accepted
Rejected
Converted
Closed
```

---

# Partner Performance Entity

## PartnerPerformance

Stores performance metrics.

---

### Attributes

| Field | Type |
|---------|---------|
| PerformanceId | UUID |
| PartnerId | UUID |
| ReportingPeriod | String |
| TotalReferrals | Integer |
| ConvertedReferrals | Integer |
| TotalRevenue | Decimal |
| CommissionEarned | Decimal |
| PerformanceScore | Integer |

---

# Partner Activity Entity

## PartnerActivity

Tracks partner interactions.

---

### Attributes

| Field | Type |
|---------|---------|
| ActivityId | UUID |
| PartnerId | UUID |
| ActivityType | Enum |
| ActivityDate | DateTime |
| Description | Text |
| PerformedBy | UUID |

---

# Activity Types

```text
Call
Email
Meeting
ContractReview
ReferralSubmission
Training
SupportRequest
Other
```

---

# Partner Note Entity

## PartnerNote

Stores internal notes.

---

### Attributes

| Field | Type |
|---------|---------|
| NoteId | UUID |
| PartnerId | UUID |
| NoteText | Text |
| CreatedBy | UUID |
| CreatedDate | DateTime |

---

# Partner Tag Entity

## PartnerTag

Supports categorization.

---

### Attributes

| Field | Type |
|---------|---------|
| TagId | UUID |
| PartnerId | UUID |
| TagName | String |

---

# Relationships

```text
Partner
│
├── PartnerContact
├── PartnerAddress
├── PartnerDocument
├── PartnerContract
├── PartnerCommission
├── PartnerReferral
├── PartnerPerformance
├── PartnerActivity
├── PartnerNote
└── PartnerTag
```

---

# Mandatory Fields

```text
PartnerId
TenantId
PartnerCode
PartnerName
PartnerType
Status
JoinDate
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

## Partner Code

Must be unique per tenant.

---

## Referral Percentage

Range:

```text
0 - 100
```

---

## Email

Must be valid email format.

---

## Contract Dates

End Date must be greater than Start Date.

---

# Performance Score

Range:

```text
0 - 100
```

Categories:

```text
0-25    Poor
26-50   Average
51-75   Good
76-100  Excellent
```

---

# Searchable Fields

```text
PartnerCode
PartnerName
Email
PhoneNumber
Industry
PartnerLevel
Status
Tags
```

---

# Indexing Strategy

Indexes:

```text
TenantId
PartnerCode
PartnerName
Email
Status
PartnerType
PartnerLevel
CreatedDate
```

---

# Commission Rules

- Commissions shall be linked to referrals.
- Commission percentage shall be defined by partner agreement.
- Paid commissions cannot be modified.
- Cancelled commissions require approval.

---

# Referral Rules

- Every referral shall belong to a partner.
- Referrals may generate leads.
- Converted referrals may generate commissions.

---

# Security Controls

Supports:

- Role-Based Access
- Partner Data Isolation
- Audit Logging
- Tenant Isolation

---

# Analytics Use Cases

Supports:

- Partner Performance Analysis
- Referral Conversion Analysis
- Commission Analysis
- Revenue Attribution
- Partner Ranking
- Partner Retention

---

# AI Use Cases

Supports:

- Partner Scoring
- Referral Quality Prediction
- Revenue Forecasting
- Partner Recommendation
- Performance Optimization

---

# Integration Points

Partner_Management.md

Lead_Data_Model.md

Customer_Data_Model.md

Contract_Management.md

Revenue_Management.md

Commission_Management.md

Business_Intelligence.md

Enterprise_Reporting.md

Database_Architecture.md

Data_Governance.md

Master_Data_Management.md

---

# Business Rules

1. Every partner shall belong to a tenant.

2. Partner codes shall be unique within a tenant.

3. Partner lifecycle status shall be tracked.

4. Partner contracts shall be maintained separately.

5. Partner referrals shall be traceable.

6. Partner commissions shall be auditable.

7. Soft delete shall be used for partner records.

8. Partner performance metrics shall be maintained.

9. Sensitive partner information shall be protected.

10. Partner Data Model shall serve as the authoritative partner data structure for PropertyPilot.