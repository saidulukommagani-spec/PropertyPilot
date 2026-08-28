# PropertyPilot Coverage Management

## Version

1.0

---

# Purpose

Coverage Management defines where PropertyPilot services are available.

The Coverage Engine determines:

- Service Availability
- ETA Calculation
- Agent Assignment
- Cluster Assignment
- Pricing Eligibility
- Service Restrictions

Coverage Management is the foundation of the PropertyPilot operational model.

---

# Coverage Hierarchy

PropertyPilot shall support the following hierarchy.

Country

↓

State

↓

District

↓

Mandal / Taluk

↓

Village / Locality

↓

Coverage Zone

↓

Cluster

---

# Coverage Levels

## Level 1 - Fully Covered

Characteristics

- Active Agents Available
- Cluster Available
- All Services Available
- Standard ETA

Examples

Major Cities

District Headquarters

High Demand Areas

---

## Level 2 - Partially Covered

Characteristics

- Limited Agents Available
- Limited Services Available
- Extended ETA

Examples

Small Towns

Semi Urban Areas

---

## Level 3 - Remote Coverage

Characteristics

- No Dedicated Agent
- Service Requires Manual Assignment
- Extended ETA

Examples

Remote Villages

Interior Agriculture Areas

Forest Border Regions

---

## Level 4 - Not Covered

Characteristics

- Services Not Available
- No Agent Network
- No Operational Support

Customer shall be informed before booking.

---

# Coverage Status

ACTIVE

LIMITED

REMOTE

INACTIVE

SUSPENDED

---

# Coverage Zone

A Coverage Zone is a geographical operational area.

Examples

Hyderabad East Zone

Hyderabad West Zone

Nalgonda North Zone

Nalgonda South Zone

Suryapet Central Zone

---

# Coverage Zone Attributes

Zone Name

State

District

Mandal

Village

Status

Coverage Level

Cluster Mapping

Service Availability

Priority

---

# Service Availability Rules

Coverage determines which services can be offered.

Example

Urban Areas

Available Services

- Property Verification
- Rental Services
- Construction Services
- Drone Services
- Maintenance Services

---

Remote Agriculture Areas

Available Services

- Land Verification
- Crop Monitoring
- Borewell Verification

Unavailable Services

- Same Day Service
- Emergency Service

---

# Coverage Priority

Coverage zones may be prioritized.

Priority Levels

HIGH

MEDIUM

LOW

---

Examples

Hyderabad

HIGH

Nalgonda

HIGH

Remote Village

LOW

---

# Service Eligibility Validation

Before service booking:

The system shall verify:

- Property Location
- Coverage Availability
- Service Availability
- Cluster Availability

Only eligible services shall be displayed.

---

# Coverage Based ETA

Coverage directly impacts ETA.

Example

Fully Covered

2 Days

Partially Covered

5 Days

Remote

14 Days

All values configurable.

---

# Coverage Based Pricing

Future Enhancement

Coverage zones may influence pricing.

Examples

Remote Service Surcharge

Remote Travel Charges

Special Coverage Charges

Priority Area Discounts

---

# Coverage Based Agent Assignment

When a service is booked:

System shall identify:

1. Coverage Zone

2. Cluster

3. Eligible Agents

4. Backup Agents

---

# Coverage Expansion

Admin shall be able to:

Add New State

Add New District

Add New Mandal

Add New Village

Add New Coverage Zone

Activate Coverage

Deactivate Coverage

Without code changes.

---

# Coverage Dashboard

Admin Portal shall display:

Total States

Total Districts

Total Mandals

Total Villages

Active Coverage Zones

Inactive Coverage Zones

Coverage Percentage

Service Availability Matrix

---

# Coverage Restrictions

Coverage can restrict:

Specific Services

Specific Property Types

Specific Add-ons

Specific ETA Rules

Specific Pricing Rules

---

# GPS Validation

Property location shall be validated using:

Latitude

Longitude

Map Location

Google Place Information

---

# Future Enhancements

Automatic Coverage Expansion

AI Coverage Recommendation

Demand Based Coverage Planning

Coverage Heat Maps

Service Density Analytics

Revenue Based Coverage Planning

Cluster Based Coverage Optimization