# PropertyPilot Property Data Model

## Version

1.0

---

# Purpose

The Property Data Model defines the structure, relationships, lifecycle, validation rules, governance standards, and business rules for property management within the PropertyPilot platform.

This model serves as the authoritative source for residential, commercial, industrial, rental, lease, and investment properties managed across the platform.

---

# Objectives

The Property Data Model shall:

- Manage property lifecycle
- Maintain property master data
- Support property listing and marketing
- Track ownership and tenancy
- Enable property analytics
- Support revenue generation
- Support contract management
- Enable property maintenance tracking
- Support multi-tenant operations
- Enable AI-driven property insights

---

# Entity Overview

Primary Entity:

Property

Supporting Entities:

PropertyAddress

PropertyOwner

PropertyContact

PropertyDocument

PropertyImage

PropertyAmenity

PropertyUnit

PropertyValuation

PropertyListing

PropertyContract

PropertyInspection

PropertyMaintenance

PropertyActivity

PropertyTag

PropertyAudit

---

# Property Lifecycle

Draft

↓

Available

↓

Listed

↓

Reserved

↓

Contracted

↓

Occupied

↓

Inactive

↓

Archived

---

# Primary Entity

## Property

Represents a real estate asset managed within PropertyPilot.

---

# Property Attributes

| Field | Type | Required |
|---------|---------|---------|
| PropertyId | UUID | Yes |
| TenantId | UUID | Yes |
| PropertyCode | String(50) | Yes |
| PropertyName | String(255) | Yes |
| PropertyType | Enum | Yes |
| PropertyCategory | Enum | Yes |
| Status | Enum | Yes |
| Description | Text | No |
| TotalArea | Decimal | No |
| BuiltUpArea | Decimal | No |
| LandArea | Decimal | No |
| NumberOfFloors | Integer | No |
| NumberOfUnits | Integer | No |
| ConstructionYear | Integer | No |
| MarketValue | Decimal | No |
| RentalValue | Decimal | No |
| CurrencyCode | String(10) | No |
| IsActive | Boolean | Yes |

---

# Property Types

```text
Residential
Commercial
Industrial
Land
Retail
Warehouse
MixedUse
Hospitality
```

---

# Property Categories

```text
Apartment
Villa
IndependentHouse
Office
Shop
Building
Plot
FarmLand
Other
```

---

# Property Status

```text
Draft
Available
Listed
Reserved
Contracted
Occupied
Inactive
Archived
```

---

# Property Address Entity

## PropertyAddress

Stores property location details.

---

### Attributes

| Field | Type |
|---------|---------|
| AddressId | UUID |
| PropertyId | UUID |
| AddressLine1 | String |
| AddressLine2 | String |
| City | String |
| State | String |
| Country | String |
| PostalCode | String |
| Latitude | Decimal |
| Longitude | Decimal |

---

# Property Owner Entity

## PropertyOwner

Stores ownership information.

---

### Attributes

| Field | Type |
|---------|---------|
| OwnerId | UUID |
| PropertyId | UUID |
| CustomerId | UUID |
| OwnershipPercentage | Decimal |
| StartDate | Date |
| EndDate | Date |

---

# Property Contact Entity

## PropertyContact

Stores property-specific contacts.

---

### Attributes

| Field | Type |
|---------|---------|
| ContactId | UUID |
| PropertyId | UUID |
| ContactName | String |
| ContactType | Enum |
| Email | String |
| Phone | String |

---

# Contact Types

```text
Owner
Manager
Caretaker
Tenant
Broker
Agent
Other
```

---

# Property Document Entity

## PropertyDocument

Stores legal and operational documents.

---

### Attributes

| Field | Type |
|---------|---------|
| DocumentId | UUID |
| PropertyId | UUID |
| DocumentType | String |
| FileName | String |
| FilePath | String |
| UploadDate | DateTime |

---

# Property Image Entity

## PropertyImage

Stores property media.

---

### Attributes

| Field | Type |
|---------|---------|
| ImageId | UUID |
| PropertyId | UUID |
| ImageUrl | String |
| DisplayOrder | Integer |
| IsPrimary | Boolean |

---

# Property Amenity Entity

## PropertyAmenity

Stores amenities.

---

### Attributes

| Field | Type |
|---------|---------|
| AmenityId | UUID |
| PropertyId | UUID |
| AmenityName | String |
| Category | String |

---

# Common Amenities

```text
Parking
SwimmingPool
Gym
Security
PowerBackup
Elevator
Garden
ClubHouse
Internet
CCTV
```

---

# Property Unit Entity

## PropertyUnit

Stores units belonging to a property.

---

### Attributes

| Field | Type |
|---------|---------|
| UnitId | UUID |
| PropertyId | UUID |
| UnitNumber | String |
| FloorNumber | Integer |
| UnitType | String |
| Area | Decimal |
| Status | String |

---

# Property Valuation Entity

## PropertyValuation

Stores valuation history.

---

### Attributes

| Field | Type |
|---------|---------|
| ValuationId | UUID |
| PropertyId | UUID |
| ValuationDate | Date |
| MarketValue | Decimal |
| ValuationMethod | String |
| PerformedBy | UUID |

---

# Property Listing Entity

## PropertyListing

Tracks marketplace listings.

---

### Attributes

| Field | Type |
|---------|---------|
| ListingId | UUID |
| PropertyId | UUID |
| ListingDate | Date |
| ListingPrice | Decimal |
| ListingStatus | String |
| MarketplaceSource | String |

---

# Property Contract Entity

## PropertyContract

Tracks contracts associated with a property.

---

### Attributes

| Field | Type |
|---------|---------|
| PropertyContractId | UUID |
| PropertyId | UUID |
| ContractId | UUID |
| StartDate | Date |
| EndDate | Date |
| Status | String |

---

# Property Inspection Entity

## PropertyInspection

Tracks inspections.

---

### Attributes

| Field | Type |
|---------|---------|
| InspectionId | UUID |
| PropertyId | UUID |
| InspectionDate | Date |
| InspectionType | String |
| Findings | Text |
| InspectorId | UUID |

---

# Property Maintenance Entity

## PropertyMaintenance

Tracks maintenance activities.

---

### Attributes

| Field | Type |
|---------|---------|
| MaintenanceId | UUID |
| PropertyId | UUID |
| RequestDate | Date |
| Description | Text |
| Status | String |
| AssignedVendorId | UUID |

---

# Property Activity Entity

## PropertyActivity

Tracks all property events.

---

### Attributes

| Field | Type |
|---------|---------|
| ActivityId | UUID |
| PropertyId | UUID |
| ActivityType | String |
| ActivityDate | DateTime |
| Description | Text |

---

# Property Tag Entity

## PropertyTag

Supports categorization.

---

### Attributes

| Field | Type |
|---------|---------|
| TagId | UUID |
| PropertyId | UUID |
| TagName | String |

---

# Relationships

```text
Property
│
├── PropertyAddress
├── PropertyOwner
├── PropertyContact
├── PropertyDocument
├── PropertyImage
├── PropertyAmenity
├── PropertyUnit
├── PropertyValuation
├── PropertyListing
├── PropertyContract
├── PropertyInspection
├── PropertyMaintenance
├── PropertyActivity
└── PropertyTag
```

---

# Mandatory Fields

```text
PropertyId
TenantId
PropertyCode
PropertyName
PropertyType
PropertyCategory
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

## Property Code

Must be unique per tenant.

---

## Property Name

Cannot be empty.

---

## Market Value

Must be greater than zero.

---

## Ownership Percentage

Total ownership percentage must equal 100%.

---

# Searchable Fields

```text
PropertyCode
PropertyName
PropertyType
PropertyCategory
City
State
Status
Tags
```

---

# Indexing Strategy

Indexes:

```text
TenantId
PropertyCode
PropertyName
PropertyType
PropertyCategory
Status
City
CreatedDate
```

---

# Security Controls

Supports:

- Role-Based Access
- Property Data Isolation
- Audit Logging
- Tenant Isolation

---

# Analytics Use Cases

Supports:

- Property Utilization
- Occupancy Analysis
- Revenue Analysis
- Property Valuation Trends
- Maintenance Analytics
- Portfolio Performance

---

# AI Use Cases

Supports:

- Property Valuation Prediction
- Occupancy Forecasting
- Maintenance Prediction
- Property Recommendation
- Investment Analysis

---

# Integration Points

Property_Management.md

Customer_Data_Model.md

Partner_Data_Model.md

Contract_Management.md

Revenue_Management.md

Inventory_Management.md

Case_Management.md

Business_Intelligence.md

Enterprise_Reporting.md

Database_Architecture.md

Data_Governance.md

Master_Data_Management.md

---

# Business Rules

1. Every property shall belong to a tenant.

2. Property codes shall be unique within a tenant.

3. Property lifecycle status shall be maintained.

4. Ownership information shall be tracked separately.

5. Property documents shall be auditable.

6. Property valuation history shall be retained.

7. Soft delete shall be used for property records.

8. Property activities shall be audit logged.

9. Sensitive property information shall be protected.

10. Property Data Model shall serve as the authoritative property data structure for PropertyPilot.