# PropertyPilot Customer Data Model

## Version

1.0

---

# Purpose

The Customer Data Model defines the structure, relationships, validation rules, governance standards, lifecycle management, and business rules for customer data within the PropertyPilot platform.

This model serves as the authoritative source for customer-related information across CRM, Lead Management, Partner Management, Revenue Management, Contract Management, Case Management, Reporting, Analytics, and AI services.

---

# Objectives

The Customer Data Model shall:

- Establish a single source of truth for customers
- Support customer lifecycle management
- Enable customer analytics
- Support customer segmentation
- Improve customer engagement
- Ensure data consistency
- Support regulatory compliance
- Enable reporting and forecasting
- Support multi-tenant operations
- Enable AI-driven customer insights

---

# Entity Overview

Primary Entity:

Customer

Supporting Entities:

CustomerAddress

CustomerContact

CustomerPreference

CustomerDocument

CustomerRelationship

CustomerActivity

CustomerNote

CustomerTag

CustomerSubscription

CustomerAudit

---

# Customer Lifecycle

Prospect

↓

Lead

↓

Qualified Lead

↓

Customer

↓

Active Customer

↓

Inactive Customer

↓

Archived Customer

---

# Primary Entity

## Customer

Represents an individual or organization receiving services from PropertyPilot.

---

# Customer Attributes

| Field | Type | Required |
|---------|---------|---------|
| CustomerId | UUID | Yes |
| TenantId | UUID | Yes |
| CustomerCode | String(50) | Yes |
| CustomerType | Enum | Yes |
| CustomerName | String(255) | Yes |
| Status | Enum | Yes |
| Email | String(255) | No |
| PhoneNumber | String(50) | No |
| AlternatePhone | String(50) | No |
| Website | String(255) | No |
| PreferredLanguage | String(20) | No |
| PreferredContactMethod | Enum | No |
| Source | String(100) | No |
| Industry | String(100) | No |
| CustomerSegment | String(100) | No |
| DateOfBirth | Date | No |
| RegistrationDate | Date | Yes |
| LastInteractionDate | DateTime | No |
| CustomerScore | Integer | No |
| IsActive | Boolean | Yes |
| Notes | Text | No |

---

# Customer Type

Values:

```text
Individual
Organization
Corporate
Government
PartnerCustomer
FranchiseCustomer
```

---

# Customer Status

Values:

```text
Prospect
Lead
Active
Inactive
Suspended
Archived
```

---

# Customer Address Entity

## CustomerAddress

Stores customer location information.

---

### Attributes

| Field | Type |
|---------|---------|
| AddressId | UUID |
| CustomerId | UUID |
| AddressType | Enum |
| AddressLine1 | String |
| AddressLine2 | String |
| City | String |
| State | String |
| Country | String |
| PostalCode | String |
| IsPrimary | Boolean |

---

# Address Types

```text
Home
Office
Billing
Shipping
Property
Other
```

---

# Customer Contact Entity

## CustomerContact

Stores multiple customer contacts.

---

### Attributes

| Field | Type |
|---------|---------|
| ContactId | UUID |
| CustomerId | UUID |
| ContactName | String |
| Role | String |
| Email | String |
| Phone | String |
| Mobile | String |
| IsPrimary | Boolean |

---

# Customer Preference Entity

## CustomerPreference

Stores communication preferences.

---

### Attributes

| Field | Type |
|---------|---------|
| PreferenceId | UUID |
| CustomerId | UUID |
| PreferredChannel | Enum |
| MarketingOptIn | Boolean |
| SMSOptIn | Boolean |
| EmailOptIn | Boolean |
| NotificationOptIn | Boolean |

---

# Customer Document Entity

## CustomerDocument

Stores customer-related documents.

---

### Attributes

| Field | Type |
|---------|---------|
| DocumentId | UUID |
| CustomerId | UUID |
| DocumentType | String |
| FileName | String |
| FilePath | String |
| UploadDate | DateTime |
| UploadedBy | UUID |

---

# Customer Relationship Entity

## CustomerRelationship

Defines relationships between customers.

---

### Attributes

| Field | Type |
|---------|---------|
| RelationshipId | UUID |
| CustomerId | UUID |
| RelatedCustomerId | UUID |
| RelationshipType | Enum |

---

# Relationship Types

```text
Spouse
Family
BusinessPartner
Employee
Employer
Vendor
Referral
Other
```

---

# Customer Activity Entity

## CustomerActivity

Tracks customer interactions.

---

### Attributes

| Field | Type |
|---------|---------|
| ActivityId | UUID |
| CustomerId | UUID |
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
Visit
PropertyViewing
ContractDiscussion
SupportCase
Payment
Other
```

---

# Customer Note Entity

## CustomerNote

Stores internal notes.

---

### Attributes

| Field | Type |
|---------|---------|
| NoteId | UUID |
| CustomerId | UUID |
| NoteText | Text |
| CreatedBy | UUID |
| CreatedDate | DateTime |

---

# Customer Tag Entity

## CustomerTag

Supports segmentation and categorization.

---

### Attributes

| Field | Type |
|---------|---------|
| TagId | UUID |
| CustomerId | UUID |
| TagName | String |

---

# Customer Subscription Entity

## CustomerSubscription

Tracks subscriptions and memberships.

---

### Attributes

| Field | Type |
|---------|---------|
| SubscriptionId | UUID |
| CustomerId | UUID |
| SubscriptionType | String |
| StartDate | Date |
| EndDate | Date |
| Status | Enum |

---

# Relationships

```text
Customer
│
├── CustomerAddress
├── CustomerContact
├── CustomerPreference
├── CustomerDocument
├── CustomerRelationship
├── CustomerActivity
├── CustomerNote
├── CustomerTag
└── CustomerSubscription
```

---

# Mandatory Fields

Required:

```text
CustomerId
TenantId
CustomerCode
CustomerName
CustomerType
Status
RegistrationDate
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

## Customer Code

- Must be unique per tenant

---

## Email

- Must be valid email format

---

## Phone Number

- Must contain valid country code

---

## Customer Name

- Cannot be empty

---

## Status

- Must use approved enumeration

---

# Searchable Fields

CustomerCode

CustomerName

Email

PhoneNumber

CustomerSegment

Industry

Tags

---

# Indexing Strategy

Indexes:

```text
TenantId
CustomerCode
CustomerName
Email
PhoneNumber
Status
CustomerSegment
CreatedDate
```

---

# Security Controls

Integrates With:

- Security_Design.md
- Identity_Access_Management.md

---

Supports:

- Role-Based Access
- Data Masking
- Audit Logging
- Tenant Isolation

---

# Data Retention

Active Customers:

Retain Indefinitely

---

Inactive Customers:

Retain 7 Years

---

Archived Customers:

Retain Per Compliance Policy

---

# Analytics Use Cases

Supports:

- Customer Segmentation
- Customer Lifetime Value
- Revenue Analysis
- Customer Retention
- Customer Churn Prediction
- Customer Engagement Analytics

---

# AI Use Cases

Supports:

- Customer Scoring
- Recommendation Engines
- Lead Conversion Prediction
- Customer Sentiment Analysis
- Personalized Experiences

---

# Integration Points

Customer_Management.md

CRM_Management.md

Lead_Management.md

Partner_Management.md

Revenue_Management.md

Contract_Management.md

Case_Management.md

Business_Intelligence.md

Enterprise_Reporting.md

Data_Governance.md

Master_Data_Management.md

Database_Architecture.md

---

# Business Rules

1. Every customer shall belong to a tenant.

2. Customer codes shall be unique within a tenant.

3. Customer lifecycle status shall be tracked.

4. Customer activities shall be audit logged.

5. Customer relationships shall be maintained separately.

6. Customer preferences shall support consent management.

7. Soft delete shall be used for customer records.

8. Sensitive customer information shall be protected.

9. Customer analytics shall use governed data sources.

10. Customer Data Model shall serve as the authoritative customer data structure for PropertyPilot.