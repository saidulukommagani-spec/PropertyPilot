# PropertyPilot Pricing Engine

## Version

1.0

---

# Objective

The Pricing Engine determines the amount charged to the customer for a service request.

Goals:

- Fair customer pricing
- Transparent calculations
- Scalable pricing model
- Location-based pricing
- Admin configurable
- No code changes required for pricing updates

---
# Pricing Engine

The system shall support dynamic pricing based on:

- Service Charge
- Distance
- Food Allowance
- Add-On Services
- Platform Fee
- Admin Overhead
- Profit Margin

All pricing values shall be configurable through Admin Portal.
# Pricing Formula

Final Price =

Service Charge
+
Travel Cost
+
Food Allowance
+
Add-On Charges

↓

Subtotal

↓

Platform Fee

↓

Admin Overhead

↓

Profit Margin

↓

Final Customer Price

---

# Service Charge

Base charge for performing a service.

Examples:

Property Verification

₹500

Site Visit

₹500

Plot Monitoring

₹400

Rental Inspection

₹700

Construction Inspection

₹1000

Live Video Inspection

₹800

Drone Survey

₹1500

Agriculture Land Inspection

₹600

Commercial Property Inspection

₹800

Apartment Inspection

₹500

All values configurable via Admin Portal.

---

# Travel Cost

Travel cost is calculated using round-trip distance.

Formula:

Travel Cost =
Round Trip Distance × Travel Cost Per KM

Example:

One Way Distance = 30 KM

Round Trip Distance = 60 KM

Travel Cost Per KM = ₹4

Travel Cost = ₹240

---

# Travel Cost Per KM

Default:

₹4 / KM

Admin configurable.

---

# Distance Calculation

Distance shall be calculated using:

Google Maps Distance Matrix API

OR

Google Directions API

Inputs:

Agent Coordinates

Property Coordinates

Outputs:

Distance

Travel Time

Round Trip Distance

---

# Food Allowance

Food allowance is based on round-trip distance.

Food Slabs

0 - 20 KM

₹0

20 - 30 KM

₹100

30 - 50 KM

₹150

50 - 70 KM

₹200

More Than 70 KM

₹300

Admin configurable.

---

# Add-On Charges

Examples:

Live Video Call

AI Property Analysis

Drone Survey

Drone Video

360 Virtual Tour

Premium PDF Report

Extra Photo Package

Weekend Service

Holiday Service

Priority Service

All values configurable.

---

# Subtotal

Subtotal =

Service Charge
+
Travel Cost
+
Food Allowance
+
Add-On Charges

---

# Platform Fee

Purpose:

Cloud Hosting

Application Maintenance

Push Notifications

SMS / OTP

Maps Services

Payment Gateway Costs

Formula:

Platform Fee =
Subtotal × Platform Fee %

Default:

5%

Admin configurable.

---

# Admin Overhead

Purpose:

Customer Support

Operations Team

Quality Checks

Management Costs

Escalation Handling

Formula:

Admin Overhead =
Subtotal × Admin Overhead %

Default:

5%

Admin configurable.

---

# Profit Margin

Purpose:

Business Profit

Marketing

Expansion

Technology Investments

Formula:

Profit Margin =
Subtotal × Profit Margin %

Default:

10%

Admin configurable.

---

# Final Formula

Final Price =

Subtotal
+
Platform Fee
+
Admin Overhead
+
Profit Margin

---

# Complete Example

Property Verification

Service Charge = ₹500

Distance = 60 KM Round Trip

Travel Cost = ₹240

Food Allowance = ₹200

Add-Ons = ₹0

Subtotal = ₹940

Platform Fee (5%) = ₹47

Admin Overhead (5%) = ₹47

Profit Margin (10%) = ₹94

Final Customer Price

₹1128

---

# Future Enhancements

Cluster Pricing

Travel Time Pricing

Dynamic Pricing

Demand Based Pricing

Seasonal Pricing

AI Optimized Pricing

Weather Based Pricing

Subscription Pricing

---

# Admin Configurable Parameters

Service Charges

Travel Cost Per KM

Food Allowance Slabs

Platform Fee %

Admin Overhead %

Profit Margin %

Add-On Charges

Promotions

Coupons

Discount Rules

Subscription Plans

# Anonymous Price Calculator

Users may calculate estimated pricing without registration.

Price Calculator shall:

- Accept Property Location
- Accept Service Type
- Calculate Estimated Charges

No login required.
# Pricing Audit Trail

Every pricing change shall store:

- Old Value
- New Value
- Changed By
- Changed Date
- Reason
