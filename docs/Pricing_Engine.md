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
# Pricing Lifecycle

Service Selected

↓

Coverage Validation

↓

Distance Calculation

↓

Base Price Calculation

↓

Add-On Calculation

↓

Discount Evaluation

↓

Final Price Calculation

↓

Payment

↓

Pricing Audit

All pricing calculations shall be traceable and auditable.
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
# Pricing Components

The Pricing Engine shall support:

Service Charges

Travel Charges

Food Allowances

Add-On Charges

Platform Fees

Administrative Overhead

Profit Margins

Discounts

Coupons

Subscription Benefits

Taxes (Future Support)

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
# Pricing Status

ESTIMATED

CALCULATED

CONFIRMED

DISCOUNT_APPLIED

PAID

REFUNDED

CANCELLED
---

# Pricing Rule Master

Every pricing rule shall be configurable.

Fields:

Rule ID

Rule Name

Service Type

Coverage Zone

Cluster

Effective Date

Expiry Date

Priority

Status

Description

Pricing rules may be activated or deactivated without code deployment.
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
# Coverage Based Pricing

Integrates With:

Coverage_Management.md

Coverage may influence:

Remote Area Charges

Extended Travel Charges

Special Coverage Fees

Priority Coverage Discounts

Coverage pricing rules shall be configurable.
---

# Cluster Based Pricing

Integrates With:

Cluster_Management.md

Pricing may vary by cluster.

Examples:

Hyderabad Cluster

Property Verification

₹500

Remote Village Cluster

Property Verification

₹700

Premium Cluster

Property Verification

₹900

Cluster pricing rules shall be configurable.

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
# Discount Management

The system shall support:

Percentage Discounts

Fixed Amount Discounts

Promotional Discounts

Festival Offers

Referral Discounts

Admin Discounts

Examples:

10% Off

₹100 Off

First Service Discount

All discount rules shall be configurable.
---

# Surge Pricing

Future Support

Pricing may increase based on:

High Demand

Low Agent Availability

Remote Locations

Emergency Services

Weekend Services

Holiday Services

Surge rules shall be configurable.

Examples:

Weekend Service

+10%

Holiday Service

+20%

Emergency Service

+30%
---
# Coupon Management

Coupons may support:

Percentage Discounts

Fixed Discounts

Service Specific Coupons

Location Specific Coupons

Customer Specific Coupons

Coupon Attributes:

Coupon Code

Validity Period

Usage Limits

Discount Type

Discount Value

Status
---

# Vendor Quotation Pricing

Integrates With:

Quotation_Management.md

Vendor_Management.md

Supports:

Vendor Quotes

Material Cost

Labor Cost

Equipment Cost

Transportation Cost

Vendor Margins

Customer Final Price

Pricing Engine may calculate customer pricing using vendor quotations.
---
# Subscription Pricing

Integrates With:

Subscription_Management.md

Subscription plans may provide:

Discounted Pricing

Free Monitoring Visits

Priority Services

Reduced Travel Charges

Exclusive Service Packages

Subscription benefits shall be configurable.

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
# Pricing Analytics

Track:

Average Service Price

Revenue By Service

Revenue By Coverage Zone

Discount Usage

Coupon Usage

Profit Margins

Travel Cost Trends

Subscription Savings

Pricing Conversion Rates
Track:

Estimated Cost

Actual Cost

Customer Revenue

Vendor Cost

Agent Cost

Gross Margin

Net Margin

Profitability By Service

Profitability By Cluster
---

# Pricing Transparency

The system shall store:

Base Price

Travel Charges

Food Allowance

Add-On Charges

Discounts

Platform Fee

Administrative Overhead

Profit Margin

Final Price

Every pricing component shall be visible in pricing audit records.
# Pricing Dashboard

Admin shall view:

Pricing Rules

Revenue Trends

Discount Usage

Coupon Usage

Profit Margins

Top Revenue Services

Coverage Based Revenue

Pricing Exceptions

# Future Enhancements

Cluster Pricing

Travel Time Pricing

Dynamic Pricing

Demand Based Pricing

Seasonal Pricing

AI Optimized Pricing

Weather Based Pricing

Subscription Pricing
Vendor Quotation Pricing

Marketplace Service Pricing

AI Cost Prediction

Regional Pricing Models
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
---

# Pricing Approval Workflow

Certain pricing changes may require approval.

Examples:

Discount > 25%

Manual Price Override

Emergency Pricing

Special Customer Pricing

Workflow

Price Change

↓

Review

↓

Approval

↓

Activation

All approval activities shall be audit logged.
# Pricing Audit Trail

Every pricing change shall store:

- Old Value
- New Value
- Changed By
- Changed Date
- Reason
# Business Rules

1. Every service shall have a pricing model.

2. Distance calculations shall influence travel charges.

3. Food allowance rules shall be configurable.

4. Discounts and coupons shall be configurable.

5. Coverage may influence pricing.

6. Subscription plans may influence pricing.

7. Pricing calculations shall be auditable.

8. Pricing rules shall not require code deployment.

9. Pricing shall integrate with Coverage Management, Service Workflow, Agent Payout, Subscription Management, and Payment modules.

10. Final customer price shall be transparently calculated.