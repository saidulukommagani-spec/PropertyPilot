# PropertyPilot Lead Data Model

## Version

1.0

---

# Purpose

The Lead Data Model defines the structure, relationships, lifecycle, validation rules, governance standards, and business rules for lead management within the PropertyPilot platform.

This model serves as the authoritative source for lead acquisition, qualification, nurturing, conversion, and sales pipeline management.

---

# Objectives

The Lead Data Model shall:

- Manage lead lifecycle
- Support lead qualification
- Enable lead nurturing
- Improve conversion rates
- Track lead activities
- Support sales forecasting
- Enable lead analytics
- Support campaign attribution
- Maintain data quality
- Support multi-tenant operations

---

# Entity Overview

Primary Entity:

Lead

Supporting Entities:

LeadSource

LeadContact

LeadAddress

LeadActivity

LeadNote

LeadDocument

LeadAssignment

LeadQualification

LeadConversion

LeadTag

LeadAudit

---

# Lead Lifecycle

New

↓

Assigned

↓

Contacted

↓

Qualified

↓

Proposal

↓

Negotiation

↓

Won

↓

Converted

OR

Lost

---

# Primary Entity

## Lead

Represents a potential customer before conversion.

---

# Lead Attributes

| Field | Type | Required |
|---------|---------|---------|
| LeadId | UUID | Yes |
| TenantId | UUID | Yes |
| LeadCode | String(50) | Yes |
| LeadName | String(255) | Yes |
| LeadType | Enum | Yes |
| Status | Enum | Yes |
| Email | String(255) | No |
| PhoneNumber | String(50) | No |
| AlternatePhone | String(50) | No |
| CompanyName | String(255) | No |
| JobTitle | String(100) | No |
| Industry | String(100) | No |
| SourceId | UUID | No |
| LeadScore | Integer | No |
| EstimatedValue | Decimal | No |
| ExpectedCloseDate | Date | No |
| AssignedTo | UUID | No |
| IsConverted | Boolean | Yes |
| ConvertedDate | DateTime | No |
| Notes | Text | No |

---

# Lead Types

```text
Individual
Corporate
PartnerReferral
WebsiteInquiry
CampaignLead
WalkInLead
MarketplaceLead
Other
```

---

# Lead Status

```text
New
Assigned
Contacted
Qualified
Proposal
Negotiation
Won
Lost
Converted
```

---

# Lead Source Entity

## LeadSource

Stores acquisition source details.

---

### Attributes

| Field | Type |
|---------|---------|
| SourceId | UUID |
| SourceCode | String |
| SourceName | String |
| SourceCategory | String |
| IsActive | Boolean |

---

# Example Sources

```text
Website
Facebook
Google Ads
Referral
Partner
Campaign
Walk-In
Cold Call
Marketplace
```

---

# Lead Contact Entity

## LeadContact

Stores multiple contacts for a lead.

---

### Attributes

| Field | Type |
|---------|---------|
| ContactId | UUID |
| LeadId | UUID |
| ContactName | String |
| Role | String |
| Email | String |
| Phone | String |
| IsPrimary | Boolean |

---

# Lead Address Entity

## LeadAddress

Stores lead location information.

---

### Attributes

| Field | Type |
|---------|---------|
| AddressId | UUID |
| LeadId | UUID |
| AddressType | Enum |
| AddressLine1 | String |
| AddressLine2 | String |
| City | String |
| State | String |
| Country | String |
| PostalCode | String |

---

# Lead Activity Entity

## LeadActivity

Tracks lead interactions.

---

### Attributes

| Field | Type |
|---------|---------|
| ActivityId | UUID |
| LeadId | UUID |
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
Demo
PropertyViewing
SiteVisit
ProposalSent
FollowUp
Other
```

---

# Lead Note Entity

## LeadNote

Stores internal lead notes.

---

### Attributes

| Field | Type |
|---------|---------|
| NoteId | UUID |
| LeadId | UUID |
| NoteText | Text |
| CreatedBy | UUID |
| CreatedDate | DateTime |

---

# Lead Document Entity

## LeadDocument

Stores lead-related documents.

---

### Attributes

| Field | Type |
|---------|---------|
| DocumentId | UUID |
| LeadId | UUID |
| DocumentType | String |
| FileName | String |
| FilePath | String |
| UploadDate | DateTime |

---

# Lead Assignment Entity

## LeadAssignment

Tracks ownership history.

---

### Attributes

| Field | Type |
|---------|---------|
| AssignmentId | UUID |
| LeadId | UUID |
| AssignedTo | UUID |
| AssignedBy | UUID |
| AssignmentDate | DateTime |
| AssignmentReason | String |

---

# Lead Qualification Entity

## LeadQualification

Stores qualification information.

---

### Attributes

| Field | Type |
|---------|---------|
| QualificationId | UUID |
| LeadId | UUID |
| Budget | Decimal |
| Requirement | Text |
| Timeline | String |
| QualificationScore | Integer |
| QualifiedBy | UUID |
| QualificationDate | DateTime |

---

# Lead Conversion Entity

## LeadConversion

Tracks lead conversion details.

---

### Attributes

| Field | Type |
|---------|---------|
| ConversionId | UUID |
| LeadId | UUID |
| CustomerId | UUID |
| ConversionDate | DateTime |
| ConvertedBy | UUID |

---

# Lead Tag Entity

## LeadTag

Supports lead categorization.

---

### Attributes

| Field | Type |
|---------|---------|
| TagId | UUID |
| LeadId | UUID |
| TagName | String |

---

# Relationships

```text
Lead
│
├── LeadSource
├── LeadContact
├── LeadAddress
├── LeadActivity
├── LeadNote
├── LeadDocument
├── LeadAssignment
├── LeadQualification
├── LeadConversion
└── LeadTag
```

---

# Mandatory Fields

```text
LeadId
TenantId
LeadCode
LeadName
LeadType
Status
IsConverted
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

## Lead Code

Must be unique per tenant.

---

## Lead Score

Range:

```text
0 - 100
```

---

## Email

Must be valid email format.

---

## Expected Close Date

Cannot be earlier than current date.

---

# Lead Scoring

Score Range:

```text
0 - 100
```

Categories:

```text
0-25    Cold
26-50   Warm
51-75   Hot
76-100  Highly Qualified
```

---

# Searchable Fields

```text
LeadCode
LeadName
CompanyName
Email
PhoneNumber
Industry
Status
Tags
```

---

# Indexing Strategy

Indexes:

```text
TenantId
LeadCode
LeadName
Email
PhoneNumber
Status
AssignedTo
LeadScore
ExpectedCloseDate
CreatedDate
```

---

# Conversion Rules

A lead may be converted only when:

- Status = Won
- Mandatory fields completed
- Qualification completed
- Approval obtained (if required)

---

# Customer Conversion Mapping

```text
Lead
↓
Customer

LeadContact
↓
CustomerContact

LeadAddress
↓
CustomerAddress

LeadActivity
↓
CustomerActivity
```

---

# Security Controls

Supports:

- Role-Based Access
- Lead Ownership Security
- Audit Logging
- Tenant Isolation

---

# Analytics Use Cases

Supports:

- Lead Funnel Analysis
- Conversion Analysis
- Source Effectiveness
- Sales Performance
- Lead Aging
- Pipeline Forecasting

---

# AI Use Cases

Supports:

- Lead Scoring
- Conversion Prediction
- Next Best Action
- Lead Prioritization
- Follow-Up Recommendations

---

# Integration Points

Lead_Management.md

CRM_Management.md

Customer_Data_Model.md

Campaign_Management.md

Revenue_Management.md

Business_Intelligence.md

Enterprise_Reporting.md

Database_Architecture.md

Data_Governance.md

Master_Data_Management.md

---

# Business Rules

1. Every lead shall belong to a tenant.

2. Lead codes shall be unique within a tenant.

3. Lead status shall follow the approved lifecycle.

4. Lead activities shall be audit logged.

5. Lead ownership shall be tracked.

6. Lead conversion shall create customer records.

7. Soft delete shall be used for lead records.

8. Lead scoring shall support prioritization.

9. Sensitive lead information shall be protected.

10. Lead Data Model shall serve as the authoritative lead data structure for PropertyPilot.