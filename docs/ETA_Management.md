# PropertyPilot ETA Management

## Version

1.0

---

# Purpose

ETA (Estimated Time of Arrival / Estimated Time of Action) Management defines when a service is expected to start and when it is expected to be completed.

The ETA engine helps:

- Set customer expectations
- Plan agent assignments
- Track service delivery
- Manage SLAs
- Support future cluster-based operations

All ETA rules shall be configurable through the Admin Portal.

No code changes shall be required to modify ETA rules.

---
# ETA Lifecycle

Service Request Created

↓

ETA Calculation

↓

ETA Published

↓

Assignment

↓

ETA Monitoring

↓

ETA Update (If Required)

↓

Service Completion

↓

Archive

All ETA changes shall be audit logged.
---

# ETA Types

PropertyPilot supports two ETA categories:

## 1. Service Start ETA

Defines when a service is expected to begin after booking.

Examples:

- Property Verification
- Site Inspection
- Plot Monitoring
- Rental Inspection

Service Start ETA is primarily based on:

- Distance
- Coverage Availability
- Agent Availability
- Cluster Availability

---

## 2. Service Completion ETA

Defines when a service is expected to finish after it has started.

Examples:

Property Verification

Completion Time:
1 Day

Drone Survey

Completion Time:
2 Days

Compound Wall Construction

Completion Time:
30 Days

Guest House Construction

Completion Time:
90 Days

---

# Service Start ETA Rules

## Distance Based ETA

Distance is calculated from service coverage location to property location.

### ETA Matrix

| Distance (One Way) | ETA |
|--------------------|------|
| 0 - 20 KM | 2 Days |
| 20 - 30 KM | 3 Days |
| 30 - 70 KM | 5 Days |
| 70 - 100 KM | 7 Days |
| More Than 100 KM | 14 Days |

All values are configurable.

---

# ETA Calculation Logic

Example

Distance:

45 KM

Applicable Slab:

30 - 70 KM

Result:

Service Start ETA = 5 Days

---
# ETA Recalculation Rules

ETA may be recalculated when:

- Property Location Changes
- Coverage Zone Changes
- Cluster Changes
- Assignment Changes
- Service Type Changes
- Add-On Changes
- Admin ETA Rules Updated

Recalculation shall update:

- Expected Start Date
- Expected Completion Date

Previous ETA values shall remain available in audit history.
---

# Future ETA Enhancements

Future versions may additionally consider:

- Agent Availability
- Cluster Coverage
- Workload
- Traffic Conditions
- Weather Conditions
- Holidays
- Service Priority

---

# Coverage Based ETA

Coverage level may influence ETA.

Example

Fully Covered Area

ETA:
2 Days

Limited Coverage Area

ETA:
7 Days

Remote Area

ETA:
14 Days

---

# Cluster Based ETA

Future versions may calculate ETA based on:

- Assigned Cluster
- Cluster Workload
- Available Agents
- Cluster SLA

Example

Cluster A

Active Agents:
20

ETA:
2 Days

Cluster B

Active Agents:
2

ETA:
5 Days

---

# Service Completion ETA

Service completion depends on service type.

Examples

| Service | Completion Duration |
|----------|--------------------|
| Property Verification | 1 Day |
| Site Visit | Same Day |
| Plot Monitoring | Same Day |
| Rental Inspection | 1 Day |
| Agriculture Inspection | 1 Day |
| Drone Survey | 2 Days |
| Construction Monitoring | 3 Days |
| Compound Wall Construction | 30 Days |
| Guest House Construction | 90 Days |

All values configurable.

---

# Add-On Impact Rules

Add-ons may increase completion time.

Examples

AI Property Analysis

+1 Day

Drone Videography

+1 Day

Premium Report

+1 Day

360 Virtual Tour

+2 Days

All values configurable.

---

# Customer Display Rules

Customer should not see technical calculations.

Display examples:

Expected Start Date

05 September 2026

Expected Completion Date

06 September 2026

OR

Expected Start Within 5 Days

Expected Completion Within 6 Days

---

# ETA Status

PENDING

CALCULATED

ASSIGNED

IN_PROGRESS

DELAYED

COMPLETED

---

# SLA Tracking

The system shall compare:

Expected Start Date

vs

Actual Start Date

and

Expected Completion Date

vs

Actual Completion Date

---

# SLA Breach Conditions

Start Date Breach

Service started after expected start date.

Completion Date Breach

Service completed after expected completion date.

---

# Notifications

Customer notifications shall be triggered for:

- ETA Calculated
- ETA Updated
- Service Assigned
- Service Delayed
- Service Started
- Service Completed

Agent notifications shall be triggered for:

- Assignment Received
- SLA Warning
- Delay Alerts

Admin notifications shall be triggered for:

- SLA Breach
- High Delay Services
- Cluster Delays

---
# ETA Integrations

ETA Management integrates with:

- Service_Workflow.md
- Geo_Location_Architecture.md
- Agent_Management.md
- SLA.md
- Notification_Strategy.md

ETA outputs are used for:

- Customer Communication
- Assignment Planning
- SLA Monitoring
- Service Tracking
- Delay Detection
---
# Admin Configuration

Admin shall be able to configure:

- Distance Slabs
- ETA Days
- Coverage Rules
- Cluster Rules
- Service Completion Duration
- Add-On Duration
- SLA Thresholds

No code deployment shall be required.

---

# Audit Requirements

Track:

- Old ETA Rule
- New ETA Rule
- Changed By
- Changed Date
- Reason

All ETA modifications shall be audit logged.

---

# Future Enhancements

Real-Time ETA

AI ETA Prediction

Weather Aware ETA

Traffic Aware ETA

Cluster Load Based ETA

Agent Availability Based ETA

Priority Service ETA

Same Day Service ETA

Emergency Service ETA
# ETA Applicability

Service Start ETA

Based on:

- Distance
- Coverage
- Cluster Availability

Service Completion ETA

Based on:

- Service Type
- Add-ons

# ETA Applicability

Service Start ETA

Based on:

- Distance
- Coverage
- Cluster Availability

---

Service Completion ETA

Based on:

- Service Type
- Add-ons

---

Execution Projects

Examples:

- Compound Wall
- Farm Development
- Borewell
- Guest House Construction

For execution projects:

Completion ETA shall be derived from:

- Approved Quotation
- Project Plan
- Milestones
- Vendor Capacity

Project ETA may span days, weeks, or months.