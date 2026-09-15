# Frontend Module Implementation Plan

Document Type: Frontend Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Frontend Architecture / Product / UX / QA

---

# Purpose

This document converts the PropertyPilot product, screen catalog, user journeys, feature catalog, and API contracts into a production-grade frontend delivery plan.

The frontend will be implemented using:
- React 18
- TypeScript
- Vite
- Material UI
- Redux Toolkit
- React Query
- React Router

The goal is to deliver:
- customer-facing app with role-aware experiences
- agent operations app for field execution
- operations portal for assignment and monitoring
- admin portal for pricing, users, and platform configuration
- NRI and marketplace experiences
- consistent design system and API contracts
- scalable frontend architecture for MVP and future growth

---

# Frontend Delivery Strategy

## Component First
- build reusable UI primitives before page composition
- ensure visual consistency across screens and roles
- prioritize modular cards, tables, forms, dialogs, and filters
- keep domain-specific behavior isolated in feature modules

## API First
- align frontend state and UI contracts to the OpenAPI specification
- create typed API clients and DTOs before full feature implementation
- model loading, success, and error states consistently
- avoid UI logic depending on undocumented backend behavior

## Mobile Responsive
- optimize for mobile-first workflows, especially customer and agent journeys
- ensure tablet and desktop support for admin/operations use cases
- use responsive breakpoints and adaptive layouts consistently

## Role Based Access
- use centralized RBAC checks for route guards and feature visibility
- separate customer, agent, operations, admin, NRI, and marketplace experiences
- enforce authorization both in UI and through application logic

## Reusable Design System
- define theme with MUI and maintain global tokens
- create reusable components:
  - forms
  - dialogs
  - tables
  - empty states
  - loaders
  - notifications
  - media viewer
  - maps
  - charts
  - dashboard widgets

---

# Customer App Modules

## Authentication

### Login
API Mapping:
- POST /api/v1/auth/login
- GET /api/v1/auth/me
- GET /api/v1/auth/permissions

Components:
- LoginForm
- PasswordField
- RememberMeToggle
- AuthLayout
- ErrorBanner

State Management:
- auth slice
- token persistence
- session hydration
- refresh token lifecycle

Validation:
- email/mobile validation
- required field checks
- password policy enforcement

Dependencies:
- auth API
- Redux auth slice
- route guard
- MUI theme

Effort Estimate:
- 1 sprint

### Registration
API Mapping:
- POST /api/v1/customers
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify

Components:
- RegistrationForm
- OTPVerificationDialog
- AddressForm
- ConsentCheckboxes

Validation:
- duplicate email/mobile checks
- consent validation
- strong password rules

Dependencies:
- customer APIs
- OTP APIs
- auth state

Effort Estimate:
- 1 sprint

### OTP Verification
API Mapping:
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify

Components:
- OtpInput
- ResendOtpButton
- VerificationStatus

Dependencies:
- auth module
- timer hook

Effort Estimate:
- 0.5 sprint

### Forgot Password
API Mapping:
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify
- POST /api/v1/auth/reset-password

Components:
- ForgotPasswordForm
- ResetPasswordForm

Dependencies:
- auth APIs
- OTP module

Effort Estimate:
- 0.5 sprint

### Profile
API Mapping:
- GET /api/v1/customers/me/profile
- PUT /api/v1/customers/me/profile
- PATCH /api/v1/customers/me/profile

Components:
- ProfileCard
- EditableProfileForm
- AvatarUploader
- PreferenceSelector

Dependencies:
- customer module
- file upload utilities

Effort Estimate:
- 1 sprint

---

## Dashboard

### Home Dashboard
API Mapping:
- GET /api/v1/customers/me/dashboard
- GET /api/v1/properties
- GET /api/v1/service-requests/my
- GET /api/v1/notifications

Components:
- OverviewCard
- PropertySummaryTile
- ServiceStatusWidget
- RecentActivityList
- QuickActionButtons
- NotificationBadge

Dependencies:
- customer APIs
- dashboard data hooks
- notification state

Effort Estimate:
- 1 sprint

### Notifications
API Mapping:
- GET /api/v1/notifications
- PATCH /api/v1/notifications/{id}/read

Components:
- NotificationCenter
- NotificationList
- NotificationItem
- FilterTabs

Dependencies:
- notifications module
- query invalidation for reads

Effort Estimate:
- 0.5 sprint

### Profile
Use same profile components above.

### Recent Reports
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportSummaryCard
- ReportList
- DownloadButton

Dependencies:
- reports module

Effort Estimate:
- 0.5 sprint

### Pending Services
API Mapping:
- GET /api/v1/service-requests/my
- GET /api/v1/visits

Components:
- PendingServiceCard
- TrackingStatusChip

Dependencies:
- service module
- visit module

Effort Estimate:
- 0.5 sprint

---

## Property Management

### Property List
API Mapping:
- GET /api/v1/properties

Components:
- PropertyListTable
- PropertyCard
- SearchBar
- FilterPanel
- PaginationControls

Dependencies:
- property APIs
- search and filter state

Effort Estimate:
- 1 sprint

### Property Details
API Mapping:
- GET /api/v1/properties/{id}
- GET /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/ownership

Components:
- PropertyHeader
- PropertySummary
- DocumentGallery
- OwnershipHistoryTable
- StatusTimeline

Dependencies:
- property details API
- document viewer
- map or location display

Effort Estimate:
- 1 sprint

### Add Property
API Mapping:
- POST /api/v1/properties

Components:
- PropertyFormWizard
- AddressForm
- OwnershipDetailsForm
- MediaUploadSection

Validation:
- required fields
- geolocation accuracy
- document validation

Dependencies:
- property create API
- upload component
- validation library

Effort Estimate:
- 1 sprint

### Edit Property
API Mapping:
- PUT /api/v1/properties/{id}
- PATCH /api/v1/properties/{id}

Components:
- EditablePropertyForm
- ConfirmationDialog

Dependencies:
- property update APIs

Effort Estimate:
- 0.5 sprint

### Property Documents
API Mapping:
- POST /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/documents
- DELETE /api/v1/properties/{id}/documents/{documentId}

Components:
- UploadDropzone
- DocumentList
- FilePreviewDialog
- UploadProgress

Dependencies:
- evidence storage integration
- file metadata hooks

Effort Estimate:
- 1 sprint

---

## Service Management

### Service Catalog
API Mapping:
- GET /api/v1/service-catalog
- GET /api/v1/service-catalog/{id}

Components:
- ServiceCategoryList
- ServiceCard
- ServiceDetailsDrawer
- PricingDisplay

Dependencies:
- service catalog APIs
- filters and sorting

Effort Estimate:
- 1 sprint

### Service Details
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-catalog/{id}

Components:
- ServiceDetailPage
- ServicePlanSelector
- DateTimePicker
- FeatureChipList

Dependencies:
- service API
- booking flow

Effort Estimate:
- 0.5 sprint

### Book Service
API Mapping:
- POST /api/v1/service-requests

Components:
- BookingForm
- PropertySelectionPicker
- DateTimeSelection
- AddressSelector
- ConfirmationDialog

Dependencies:
- service requests API
- property APIs
- customer context

Effort Estimate:
- 1 sprint

### Tracking
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-requests/{id}/history
- GET /api/v1/visits/{id}

Components:
- TrackingTimeline
- StatusBadge
- AssignmentCard
- VisitProgressCard

Dependencies:
- service request APIs
- visit APIs

Effort Estimate:
- 1 sprint

### History
API Mapping:
- GET /api/v1/service-requests/my
- GET /api/v1/service-requests/{id}/history

Components:
- HistoryTable
- TimelineView
- FilterChips

Dependencies:
- service request history API

Effort Estimate:
- 0.5 sprint

---

## Reports

### Report Dashboard
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportDashboardCard
- ReportSummaryChart
- Filters
- RecentExportList

Dependencies:
- reports API
- chart library

Effort Estimate:
- 1 sprint

### PDF Viewer
API Mapping:
- GET /api/v1/reports/{id}/download
- GET /api/v1/reports/{id}/export

Components:
- ReportViewer
- DownloadButton
- ReportMetadataPanel

Dependencies:
- report download APIs
- PDF viewer library

Effort Estimate:
- 0.5 sprint

### Evidence Viewer
API Mapping:
- GET /api/v1/evidence/{id}
- GET /api/v1/evidence/{id}/preview

Components:
- EvidenceViewer
- ImageGallery
- VideoPlayer
- DocumentPreview

Dependencies:
- evidence APIs
- media viewer components

Effort Estimate:
- 1 sprint

### Downloads
API Mapping:
- GET /api/v1/reports/{id}/download
- GET /api/v1/invoices/{id}/download

Components:
- DownloadActionMenu
- DownloadList

Dependencies:
- report and invoice APIs

Effort Estimate:
- 0.5 sprint

---

## Subscription Management

### Plans
API Mapping:
- GET /api/v1/subscriptions/plans

Components:
- PlanCard
- FeatureComparisonTable

Dependencies:
- subscription plans API

Effort Estimate:
- 1 sprint

### Purchase
API Mapping:
- POST /api/v1/subscriptions/purchase

Components:
- PurchaseFlow
- CheckoutSummary
- SubscriptionConfirmation

Dependencies:
- payments module
- subscription APIs

Effort Estimate:
- 1 sprint

### Renewal
API Mapping:
- POST /api/v1/subscriptions/{id}/renew

Components:
- RenewalDialog
- SummaryCard

Dependencies:
- subscription service APIs

Effort Estimate:
- 0.5 sprint

### Upgrade
API Mapping:
- POST /api/v1/subscriptions/{id}/upgrade

Components:
- UpgradeDialog
- PlanComparison

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Downgrade
API Mapping:
- POST /api/v1/subscriptions/{id}/downgrade

Components:
- DowngradeConfirmationDialog

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Pause
API Mapping:
- POST /api/v1/subscriptions/{id}/pause

Components:
- PauseSubscriptionDialog

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Cancellation
API Mapping:
- POST /api/v1/subscriptions/{id}/cancel

Components:
- CancelSubscriptionDialog
- ReasonSelector

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

---

## Payments

### Checkout
API Mapping:
- POST /api/v1/payments/checkout

Components:
- CheckoutForm
- PaymentMethodsSelector
- OrderSummary

Dependencies:
- payment API
- secure token handling

Effort Estimate:
- 1 sprint

### Success
API Mapping:
- GET /api/v1/payments/{id}

Components:
- PaymentSuccessPage
- ReceiptSummary

Dependencies:
- payment status APIs

Effort Estimate:
- 0.5 sprint

### History
API Mapping:
- GET /api/v1/payments
- GET /api/v1/invoices/my

Components:
- PaymentHistoryTable
- InvoiceList

Dependencies:
- invoice API

Effort Estimate:
- 0.5 sprint

### Invoices
API Mapping:
- GET /api/v1/invoices/{id}
- POST /api/v1/invoices/{id}/download

Components:
- InvoiceViewer
- DownloadButton

Dependencies:
- invoice API
- report viewer

Effort Estimate:
- 0.5 sprint

---

## Support

### Complaints
API Mapping:
- POST /api/v1/complaints
- GET /api/v1/complaints/my

Components:
- ComplaintForm
- ComplaintList
- ComplaintDetails

Dependencies:
- complaint API

Effort Estimate:
- 1 sprint

### Tracking
API Mapping:
- GET /api/v1/complaints/{id}
- PATCH /api/v1/complaints/{id}/resolve

Components:
- ComplaintTimeline
- StatusBadge

Dependencies:
- complaint tracking API

Effort Estimate:
- 0.5 sprint

### FAQ
Components:
- FAQAccordion
- HelpCenterPage
- SearchableHelpContent

Dependencies:
- static content / CMS if available

Effort Estimate:
- 0.5 sprint

---

# Agent App Modules

## Dashboard
API Mapping:
- GET /api/v1/operations/assignments
- GET /api/v1/agents/me/dashboard

Components:
- AgentOverviewCard
- TaskSummaryWidget
- ScheduleCard
- LocationStatusCard

Dependencies:
- agent APIs
- operations APIs

Effort Estimate:
- 1 sprint

## Task List
API Mapping:
- GET /api/v1/operations/assignments
- GET /api/v1/service-requests/my

Components:
- TaskList
- FilterByStatus
- AssignmentDetailsCard

Dependencies:
- assignment APIs
- service request APIs

Effort Estimate:
- 1 sprint

## Task Details
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/visits/{id}

Components:
- TaskDetailHeader
- ActionButtons
- TimelineView
- Property and Customer Summary

Dependencies:
- service request and visit APIs

Effort Estimate:
- 0.5 sprint

## GPS Capture
API Mapping:
- POST /api/v1/visits/{id}/gps/checkin
- POST /api/v1/visits/{id}/gps/checkout
- GET /api/v1/visits/{id}/gps

Components:
- GPSCaptureButton
- GPSMapView
- LocationStatusWidget

Dependencies:
- maps library
- visit API

Effort Estimate:
- 1 sprint

## Photo Capture
API Mapping:
- POST /api/v1/visits/{id}/evidence
- POST /api/v1/properties/{id}/documents

Components:
- CameraCapture
- ImagePreview
- UploadQueue

Dependencies:
- media upload API
- camera utilities

Effort Estimate:
- 1 sprint

## Video Capture
API Mapping:
- POST /api/v1/visits/{id}/evidence

Components:
- VideoRecorder
- PlaybackPreview

Dependencies:
- media capture components
- evidence API

Effort Estimate:
- 1 sprint

## Observation Entry
API Mapping:
- PATCH /api/v1/visits/{id}
- POST /api/v1/visits/{id}/evidence

Components:
- ObservationForm
- ChecklistForm
- NotesEditor

Dependencies:
- visit update API

Effort Estimate:
- 0.5 sprint

## Report Drafting
API Mapping:
- POST /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportDraftEditor
- AutoFillSummary
- SaveDraftButton

Dependencies:
- report generation APIs

Effort Estimate:
- 1 sprint

## Visit Completion
API Mapping:
- PATCH /api/v1/visits/{id}/complete

Components:
- CompletionSummary
- FinalChecklist
- SubmitButton

Dependencies:
- visit completion API

Effort Estimate:
- 0.5 sprint

---

# Operations Portal Modules

## Operations Dashboard
API Mapping:
- GET /api/v1/operations/dashboard
- GET /api/v1/operations/workload

Components:
- KPITiles
- PipelineView
- WorkloadChart
- SLAStatusCard

Dependencies:
- operations dashboard APIs
- charting library

Effort Estimate:
- 1 sprint

## Task Assignment
API Mapping:
- GET /api/v1/operations/assignments
- POST /api/v1/operations/assignments
- PATCH /api/v1/operations/assignments/{id}

Components:
- AssignmentBoard
- AgentSelector
- QueuePanel
- ReassignmentDialog

Dependencies:
- operations APIs
- agent APIs

Effort Estimate:
- 1 sprint

## Agent Tracking
API Mapping:
- GET /api/v1/agents
- GET /api/v1/visits
- GET /api/v1/operations/monitoring

Components:
- AgentMapView
- LiveStatusList
- RouteTimeline

Dependencies:
- agents and visits APIs
- maps and location services

Effort Estimate:
- 1 sprint

## Visit Monitoring
API Mapping:
- GET /api/v1/visits
- GET /api/v1/visits/{id}

Components:
- VisitMonitorBoard
- StatusFilters
- MonitoringTimeline

Dependencies:
- visit APIs

Effort Estimate:
- 0.5 sprint

## Report Review
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}
- PATCH /api/v1/reports/{id}

Components:
- ReportReviewQueue
- ReviewCommentsPanel
- ApprovalActionBar

Dependencies:
- report APIs

Effort Estimate:
- 1 sprint

## Complaint Management
API Mapping:
- GET /api/v1/complaints
- PATCH /api/v1/complaints/{id}/resolve
- PATCH /api/v1/complaints/{id}/escalate

Components:
- ComplaintQueue
- ComplaintDetailPanel
- ResolutionDialog

Dependencies:
- complaint APIs

Effort Estimate:
- 1 sprint

## Escalation Management
API Mapping:
- GET /api/v1/operations/escalations
- POST /api/v1/operations/escalations

Components:
- EscalationBoard
- EscalationReasonForm
- PrioritizationControls

Dependencies:
- escalations API

Effort Estimate:
- 0.5 sprint

## Customer Management
API Mapping:
- GET /api/v1/customers
- GET /api/v1/customers/{id}

Components:
- CustomerTable
- CustomerProfileDrawer

Dependencies:
- customer APIs

Effort Estimate:
- 0.5 sprint

## Property Management
API Mapping:
- GET /api/v1/properties
- GET /api/v1/properties/{id}

Components:
- PropertyPortfolioView
- StatusFilters
- OwnershipTable

Dependencies:
- property APIs

Effort Estimate:
- 0.5 sprint

---

# Admin Portal Modules

## Admin Dashboard
API Mapping:
- GET /api/v1/admin/dashboard
- GET /api/v1/admin/analytics

Components:
- KPIOverview
- RevenueChart
- LTVInsights
- FunnelDashboard

Dependencies:
- admin analytics APIs
- charting library

Effort Estimate:
- 1 sprint

## Users
API Mapping:
- GET /api/v1/admin/users
- POST /api/v1/admin/users
- PATCH /api/v1/admin/users/{id}

Components:
- UserTable
- UserDrawer
- RoleSelector

Dependencies:
- admin user APIs
- auth roles APIs

Effort Estimate:
- 1 sprint

## Roles
API Mapping:
- GET /api/v1/auth/roles
- GET /api/v1/auth/roles/{roleId}/permissions

Components:
- RoleMatrix
- PermissionToggleList

Dependencies:
- auth permission APIs

Effort Estimate:
- 0.5 sprint

## Agents
API Mapping:
- GET /api/v1/agents
- PATCH /api/v1/agents/{id}

Components:
- AgentManagementTable
- AgentProfileDrawer

Dependencies:
- agent APIs

Effort Estimate:
- 0.5 sprint

## Partners
API Mapping:
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}

Components:
- PartnerTable
- PartnerDetailDrawer

Dependencies:
- marketplace APIs

Effort Estimate:
- 0.5 sprint

## Vendors
API Mapping:
- GET /api/v1/marketplace/vendors
- POST /api/v1/marketplace/vendors

Components:
- VendorList
- VendorApprovalFlow

Dependencies:
- vendor APIs

Effort Estimate:
- 0.5 sprint

## Services
API Mapping:
- GET /api/v1/service-catalog
- POST /api/v1/service-catalog
- PUT /api/v1/service-catalog/{id}

Components:
- ServiceCatalogManager
- PricingEditor

Dependencies:
- catalog APIs

Effort Estimate:
- 1 sprint

## Subscriptions
API Mapping:
- GET /api/v1/subscriptions/plans
- POST /api/v1/admin/pricing/plans

Components:
- SubscriptionPlanManager
- PlanEditor

Dependencies:
- subscription plans APIs

Effort Estimate:
- 1 sprint

## Pricing
API Mapping:
- GET /api/v1/admin/pricing/plans
- PUT /api/v1/admin/pricing/plans/{id}

Components:
- PricingTable
- PricingForm

Dependencies:
- admin pricing APIs

Effort Estimate:
- 0.5 sprint

## Coupons
API Mapping:
- GET /api/v1/admin/coupons
- POST /api/v1/admin/coupons

Components:
- CouponList
- CouponForm

Dependencies:
- admin coupons if present at backend

Effort Estimate:
- 0.5 sprint

## Revenue
API Mapping:
- GET /api/v1/admin/analytics/revenue
- GET /api/v1/payments/reconciliation

Components:
- RevenueSummaryChart
- RevenueTable

Dependencies:
- analytics and payment APIs

Effort Estimate:
- 0.5 sprint

## Payments
API Mapping:
- GET /api/v1/payments/reconciliation
- GET /api/v1/payments

Components:
- PaymentAdminTable
- ReconciliationStatusPanel

Dependencies:
- reconciliation APIs

Effort Estimate:
- 0.5 sprint

## Analytics
API Mapping:
- GET /api/v1/admin/analytics
- GET /api/v1/reports

Components:
- AnalyticsDashboard
- KPIComparisonCharts

Dependencies:
- analytics APIs
- charts library

Effort Estimate:
- 1 sprint

## Audit Logs
API Mapping:
- GET /api/v1/admin/audit

Components:
- AuditLogTable
- AuditDetailDrawer

Dependencies:
- audit APIs

Effort Estimate:
- 0.5 sprint

## Configuration
API Mapping:
- GET /api/v1/admin/config
- PUT /api/v1/admin/config/{key}

Components:
- ConfigForm
- ToggleSettings

Dependencies:
- configuration APIs

Effort Estimate:
- 0.5 sprint

---

# NRI Modules

## NRI Dashboard
API Mapping:
- GET /api/v1/nri/dashboard

Components:
- SummaryCards
- InvestmentOverview
- AlertsPanel

Dependencies:
- NRI-specific APIs or enriched endpoints

Effort Estimate:
- 1 sprint

## Monitoring Summary
API Mapping:
- GET /api/v1/properties
- GET /api/v1/reports
- GET /api/v1/notifications

Components:
- MonitoringSummaryGrid
- TimelineStatus
- AlertTicker

Dependencies:
- property and report APIs

Effort Estimate:
- 0.5 sprint

## Video Reports
API Mapping:
- GET /api/v1/reports
- GET /api/v1/evidence/{id}

Components:
- VideoReportList
- EvidenceViewer
- DownloadActions

Dependencies:
- evidence and report APIs

Effort Estimate:
- 1 sprint

## Relationship Manager
API Mapping:
- GET /api/v1/admin/users
- GET /api/v1/customers

Components:
- RMContactPanel
- ClientPortfolioView

Dependencies:
- customer and admin APIs

Effort Estimate:
- 0.5 sprint

## Emergency Alerts
API Mapping:
- GET /api/v1/notifications
- POST /api/v1/notifications

Components:
- AlertCard
- EmergencyActionList

Dependencies:
- notification APIs

Effort Estimate:
- 0.5 sprint

---

# Marketplace Modules

## Vendor Marketplace
API Mapping:
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}

Components:
- VendorSearch
- VendorCardGrid
- VendorFilters

Dependencies:
- marketplace APIs

Effort Estimate:
- 1 sprint

## Vendor Details
API Mapping:
- GET /api/v1/marketplace/vendors/{id}

Components:
- VendorProfileHeader
- ServiceGrid
- ReviewList

Dependencies:
- vendor detail API

Effort Estimate:
- 0.5 sprint

## Request Quotation
API Mapping:
- POST /api/v1/marketplace/quotations

Components:
- QuotationRequestForm
- ItemizedCostSummary

Dependencies:
- quotation API

Effort Estimate:
- 1 sprint

## Quotation Comparison
API Mapping:
- GET /api/v1/marketplace/quotations/my
- GET /api/v1/marketplace/quotations/{id}

Components:
- QuoteComparisonTable
- SelectedVendorSummary

Dependencies:
- quotation APIs

Effort Estimate:
- 0.5 sprint

---

# Analytics Modules

## Customer Analytics
API Mapping:
- GET /api/v1/admin/analytics/customers
- GET /api/v1/customers

Components:
- CustomerRetentionChart
- SegmentTables

Dependencies:
- analytics APIs

Effort Estimate:
- 0.5 sprint

## Operations Analytics
API Mapping:
- GET /api/v1/admin/analytics/operations
- GET /api/v1/operations/dashboard

Components:
- QueuePerformanceChart
- ResolutionTimeMetrics

Dependencies:
- operations analytics

Effort Estimate:
- 0.5 sprint

## Revenue Analytics
API Mapping:
- GET /api/v1/admin/analytics/revenue
- GET /api/v1/payments/reconciliation

Components:
- RevenueTrendChart
- PaymentStatsCards

Dependencies:
- payment and analytics APIs

Effort Estimate:
- 0.5 sprint

## Agent Performance
API Mapping:
- GET /api/v1/agents
- GET /api/v1/admin/analytics/agents

Components:
- AgentPerformanceTable
- PerformanceTrendChart

Dependencies:
- agent APIs and analytics

Effort Estimate:
- 0.5 sprint

---

# Shared Component Library

## Forms
- TextInput
- SelectField
- CheckboxGroup
- RadioGroup
- DatePicker
- TimePicker
- FileUpload
- DynamicForm

## Tables
- DataTable
- ColumnFilters
- SortableTable
- PaginationTable

## Cards
- InfoCard
- SummaryCard
- KPIStatCard
- EmptyStateCard

## Dialogs
- ConfirmationDialog
- FormDialog
- DetailsDialog

## Notifications
- Toast
- SnackBar
- InlineNotification
- Banner

## File Upload
- SingleFileUpload
- MultiFileUpload
- UploadProgress

## Media Viewer
- ImageViewer
- VideoViewer
- DocumentPreview

## Maps
- MapContainer
- MarkerPin
- MapSearch

## Charts
- BarChart
- LineChart
- PieChart
- AreaChart

## Pagination
- PaginationBar
- PageSizeSelector

## Search
- SearchInput
- DebouncedSearch
- FilterChips

## Filters
- RangeFilter
- TagFilter
- DateRangeFilter
- StatusFilter

---

# Routing Plan

## Public Routes
- /login
- /register
- /forgot-password
- /otp/verify
- /launch

## Customer Routes
- /customer/dashboard
- /customer/properties
- /customer/properties/new
- /customer/properties/:id
- /customer/services
- /customer/services/:id
- /customer/service-requests
- /customer/service-requests/:id
- /customer/reports
- /customer/subscriptions
- /customer/payments
- /customer/support
- /customer/profile

## Agent Routes
- /agent/dashboard
- /agent/tasks
- /agent/tasks/:id
- /agent/visits
- /agent/visits/:id
- /agent/evidence
- /agent/reports

## Operations Routes
- /operations/dashboard
- /operations/assignments
- /operations/agents
- /operations/visits
- /operations/reports
- /operations/complaints
- /operations/escalations
- /operations/customers
- /operations/properties

## Admin Routes
- /admin/dashboard
- /admin/users
- /admin/roles
- /admin/agents
- /admin/partners
- /admin/vendors
- /admin/services
- /admin/subscriptions
- /admin/pricing
- /admin/payments
- /admin/revenue
- /admin/analytics
- /admin/audit
- /admin/config

## Protected Routes
- all application routes require authentication
- role check at route and page boundaries
- loaders and guards for denied access
- redirect to unauthorized page when missing permissions

---

# State Management Plan

## Redux Store Structure
```text
store/
  app/
    uiSlice
    themeSlice
    notificationSlice
  auth/
    authSlice
    sessionSlice
  customer/
    profileSlice
    propertySlice
    serviceSlice
  agent/
    taskSlice
    visitSlice
    evidenceSlice
  operations/
    dashboardSlice
    assignmentSlice
  admin/
    userSlice
    configSlice
    analyticsSlice
  payments/
    paymentSlice
    invoiceSlice
  notifications/
    notificationSlice
  marketplace/
    vendorSlice
    quotationSlice
```

## React Query Usage
Use React Query for:
- server-state fetching
- caching of list/detail endpoints
- background invalidation
- optimistic update flows
- mutation and retry behavior

## Caching
Use React Query cache for:
- customer profile
- property list and details
- service catalog
- subscription plans
- payment history
- dashboard summaries
- admin metadata

## Optimistic Updates
Use optimistic UI for:
- notification read state
- task status updates
- form save flows
- property status updates
- complaint status changes
- service request lifecycle changes

---

# Testing Plan

## Unit Tests
- utilities
- validators
- selectors
- reducers
- custom hooks
- API parsing logic

## Component Tests
- form validation
- route guards
- table behavior
- dialogs and modals
- dashboard widgets
- complex form interactions

## Integration Tests
- auth flow
- customer property lifecycle
- service booking and tracking
- payment flow
- report download
- complaint actions
- admin config updates

## E2E Tests
- login and onboarding
- property listing and creation
- service request booking
- agent task execution
- payment checkout
- report generation and download
- admin user management
- role-based route access

---

# Sprint-wise Delivery Plan

## Sprint 1
Focus:
- design system foundation
- app shell and route structure
- auth flows
- basic dashboard shell
- common API client and react-query setup

Deliverables:
- app scaffolding
- theme and design tokens
- login/register/reset flows
- protected route guards
- base query client and error handling

---

## Sprint 2
Focus:
- customer app core
- property management
- service catalog and booking

Deliverables:
- dashboard
- property list/details/add/edit
- service catalog and booking flow
- tracking and history screens

---

## Sprint 3
Focus:
- agent app
- visit management
- evidence management
- report draft flow

Deliverables:
- agent dashboard
- task list and details
- visit operations
- GPS and media capture
- report drafting

---

## Sprint 4
Focus:
- subscription and payment flows
- notifications
- complaint management

Deliverables:
- subscription plan UI
- payment checkout and history
- invoice and receipt views
- complaint creation and tracking
- notification center

---

## Sprint 5
Focus:
- operations portal
- admin portal
- analytics screens
- marketplace baseline

Deliverables:
- operations dashboard and assignment management
- admin user and config flows
- analytics charts
- marketplace vendor listing and quotes

---

## Sprint 6
Focus:
- production readiness
- UX finalization
- accessibility and performance hardening
- QA and UAT support

Deliverables:
- final bug fixes
- visual polish
- accessibility review
- route and data validation
- UAT sign-off

---

# Team Structure

## Frontend Developers
- feature-focused engineers for customer, agent, operations, and admin apps
- strong state, API integration, and React architecture experience

## UI/UX
- design system ownership
- wireframe and usability review
- accessibility and user flow validation

## QA
- component and integration validation
- regression testing
- E2E validation
- browser compatibility testing

## Product Owner
- feature prioritization
- acceptance criteria
- screen and flow validation
- release readiness sign-off

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Inconsistent API contract usage | broken screen logic and regression issues | enforce generated API client and schema validation | Frontend Architecture
Unclear role boundaries | inaccessible or leaked screens | route guards and role-level feature checks | Frontend Team
Large state complexity | hard-to-debug UI behavior | modular Redux slices and clear data ownership | Frontend Architecture
Too much UI coupling | slow feature development | component-driven architecture and design system | UI/UX + Frontend Team
Weak mobile UX | poor customer and agent experience | mobile-first design, usability reviews | UI/UX
Slow report/media rendering | degraded performance | lazy loading, virtualized tables, and media optimization | Frontend Team
Duplicate screens by role | maintenance overhead | centralize shared components and route-level role slices | Frontend Team
Late backend contract changes | rework and delays | contract review gate before screen implementation | Product / API Team
Complex analytics dashboards | performance degradation | progressive loading and server-side summarized data | Frontend Architecture
Accessibility gaps | poor usability and compliance issues | automated a11y checks and manual review | QA / UX

---

# Success Criteria

The frontend is ready for:
- backend integration
- UAT
- production release

When all of the following are true:
- all priority screens for MVP are implemented
- customer, agent, operations, and admin experiences are role-aware
- design system is consistent and reusable
- API contracts are aligned to backend responses
- forms and validation hold up under real conditions
- responsive layouts work across mobile and desktop
- loading, error, and empty states are implemented
- notifications and dashboards are functional
- tests cover component and integration paths
- route guards and RBAC validate correctly
- UAT sign-off is complete

---

# Final Recommendation

PropertyPilot should be implemented as a multi-app frontend system under a shared design system and common state architecture, with a modular monolith backend behind it. The most effective path is to deliver the customer app first, then agent and operations flows, followed by admin and analytics experiences.

The recommended delivery sequence is:
- shared app shell and auth
- customer flows
- agent flows
- operations flows
- admin and analytics
- marketplace and NRI expansion

The frontend should enforce:
- consistent API usage patterns
- UI validation standards
- RBAC-driven access control
- modular component reuse
- query-based data loading
- responsive design standards
- strong testing gates before UAT and release

This produces a frontend architecture that is maintainable, fast to iterate, and ready for MVP, scale, and production deployment.
```// filepath: c:\PropertyPilot\docs\Frontend_Module_Implementation_Plan.md
# Frontend Module Implementation Plan

Document Type: Frontend Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Frontend Architecture / Product / UX / QA

---

# Purpose

This document converts the PropertyPilot product, screen catalog, user journeys, feature catalog, and API contracts into a production-grade frontend delivery plan.

The frontend will be implemented using:
- React 18
- TypeScript
- Vite
- Material UI
- Redux Toolkit
- React Query
- React Router

The goal is to deliver:
- customer-facing app with role-aware experiences
- agent operations app for field execution
- operations portal for assignment and monitoring
- admin portal for pricing, users, and platform configuration
- NRI and marketplace experiences
- consistent design system and API contracts
- scalable frontend architecture for MVP and future growth

---

# Frontend Delivery Strategy

## Component First
- build reusable UI primitives before page composition
- ensure visual consistency across screens and roles
- prioritize modular cards, tables, forms, dialogs, and filters
- keep domain-specific behavior isolated in feature modules

## API First
- align frontend state and UI contracts to the OpenAPI specification
- create typed API clients and DTOs before full feature implementation
- model loading, success, and error states consistently
- avoid UI logic depending on undocumented backend behavior

## Mobile Responsive
- optimize for mobile-first workflows, especially customer and agent journeys
- ensure tablet and desktop support for admin/operations use cases
- use responsive breakpoints and adaptive layouts consistently

## Role Based Access
- use centralized RBAC checks for route guards and feature visibility
- separate customer, agent, operations, admin, NRI, and marketplace experiences
- enforce authorization both in UI and through application logic

## Reusable Design System
- define theme with MUI and maintain global tokens
- create reusable components:
  - forms
  - dialogs
  - tables
  - empty states
  - loaders
  - notifications
  - media viewer
  - maps
  - charts
  - dashboard widgets

---

# Customer App Modules

## Authentication

### Login
API Mapping:
- POST /api/v1/auth/login
- GET /api/v1/auth/me
- GET /api/v1/auth/permissions

Components:
- LoginForm
- PasswordField
- RememberMeToggle
- AuthLayout
- ErrorBanner

State Management:
- auth slice
- token persistence
- session hydration
- refresh token lifecycle

Validation:
- email/mobile validation
- required field checks
- password policy enforcement

Dependencies:
- auth API
- Redux auth slice
- route guard
- MUI theme

Effort Estimate:
- 1 sprint

### Registration
API Mapping:
- POST /api/v1/customers
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify

Components:
- RegistrationForm
- OTPVerificationDialog
- AddressForm
- ConsentCheckboxes

Validation:
- duplicate email/mobile checks
- consent validation
- strong password rules

Dependencies:
- customer APIs
- OTP APIs
- auth state

Effort Estimate:
- 1 sprint

### OTP Verification
API Mapping:
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify

Components:
- OtpInput
- ResendOtpButton
- VerificationStatus

Dependencies:
- auth module
- timer hook

Effort Estimate:
- 0.5 sprint

### Forgot Password
API Mapping:
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify
- POST /api/v1/auth/reset-password

Components:
- ForgotPasswordForm
- ResetPasswordForm

Dependencies:
- auth APIs
- OTP module

Effort Estimate:
- 0.5 sprint

### Profile
API Mapping:
- GET /api/v1/customers/me/profile
- PUT /api/v1/customers/me/profile
- PATCH /api/v1/customers/me/profile

Components:
- ProfileCard
- EditableProfileForm
- AvatarUploader
- PreferenceSelector

Dependencies:
- customer module
- file upload utilities

Effort Estimate:
- 1 sprint

---

## Dashboard

### Home Dashboard
API Mapping:
- GET /api/v1/customers/me/dashboard
- GET /api/v1/properties
- GET /api/v1/service-requests/my
- GET /api/v1/notifications

Components:
- OverviewCard
- PropertySummaryTile
- ServiceStatusWidget
- RecentActivityList
- QuickActionButtons
- NotificationBadge

Dependencies:
- customer APIs
- dashboard data hooks
- notification state

Effort Estimate:
- 1 sprint

### Notifications
API Mapping:
- GET /api/v1/notifications
- PATCH /api/v1/notifications/{id}/read

Components:
- NotificationCenter
- NotificationList
- NotificationItem
- FilterTabs

Dependencies:
- notifications module
- query invalidation for reads

Effort Estimate:
- 0.5 sprint

### Profile
Use same profile components above.

### Recent Reports
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportSummaryCard
- ReportList
- DownloadButton

Dependencies:
- reports module

Effort Estimate:
- 0.5 sprint

### Pending Services
API Mapping:
- GET /api/v1/service-requests/my
- GET /api/v1/visits

Components:
- PendingServiceCard
- TrackingStatusChip

Dependencies:
- service module
- visit module

Effort Estimate:
- 0.5 sprint

---

## Property Management

### Property List
API Mapping:
- GET /api/v1/properties

Components:
- PropertyListTable
- PropertyCard
- SearchBar
- FilterPanel
- PaginationControls

Dependencies:
- property APIs
- search and filter state

Effort Estimate:
- 1 sprint

### Property Details
API Mapping:
- GET /api/v1/properties/{id}
- GET /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/ownership

Components:
- PropertyHeader
- PropertySummary
- DocumentGallery
- OwnershipHistoryTable
- StatusTimeline

Dependencies:
- property details API
- document viewer
- map or location display

Effort Estimate:
- 1 sprint

### Add Property
API Mapping:
- POST /api/v1/properties

Components:
- PropertyFormWizard
- AddressForm
- OwnershipDetailsForm
- MediaUploadSection

Validation:
- required fields
- geolocation accuracy
- document validation

Dependencies:
- property create API
- upload component
- validation library

Effort Estimate:
- 1 sprint

### Edit Property
API Mapping:
- PUT /api/v1/properties/{id}
- PATCH /api/v1/properties/{id}

Components:
- EditablePropertyForm
- ConfirmationDialog

Dependencies:
- property update APIs

Effort Estimate:
- 0.5 sprint

### Property Documents
API Mapping:
- POST /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/documents
- DELETE /api/v1/properties/{id}/documents/{documentId}

Components:
- UploadDropzone
- DocumentList
- FilePreviewDialog
- UploadProgress

Dependencies:
- evidence storage integration
- file metadata hooks

Effort Estimate:
- 1 sprint

---

## Service Management

### Service Catalog
API Mapping:
- GET /api/v1/service-catalog
- GET /api/v1/service-catalog/{id}

Components:
- ServiceCategoryList
- ServiceCard
- ServiceDetailsDrawer
- PricingDisplay

Dependencies:
- service catalog APIs
- filters and sorting

Effort Estimate:
- 1 sprint

### Service Details
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-catalog/{id}

Components:
- ServiceDetailPage
- ServicePlanSelector
- DateTimePicker
- FeatureChipList

Dependencies:
- service API
- booking flow

Effort Estimate:
- 0.5 sprint

### Book Service
API Mapping:
- POST /api/v1/service-requests

Components:
- BookingForm
- PropertySelectionPicker
- DateTimeSelection
- AddressSelector
- ConfirmationDialog

Dependencies:
- service requests API
- property APIs
- customer context

Effort Estimate:
- 1 sprint

### Tracking
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-requests/{id}/history
- GET /api/v1/visits/{id}

Components:
- TrackingTimeline
- StatusBadge
- AssignmentCard
- VisitProgressCard

Dependencies:
- service request APIs
- visit APIs

Effort Estimate:
- 1 sprint

### History
API Mapping:
- GET /api/v1/service-requests/my
- GET /api/v1/service-requests/{id}/history

Components:
- HistoryTable
- TimelineView
- FilterChips

Dependencies:
- service request history API

Effort Estimate:
- 0.5 sprint

---

## Reports

### Report Dashboard
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportDashboardCard
- ReportSummaryChart
- Filters
- RecentExportList

Dependencies:
- reports API
- chart library

Effort Estimate:
- 1 sprint

### PDF Viewer
API Mapping:
- GET /api/v1/reports/{id}/download
- GET /api/v1/reports/{id}/export

Components:
- ReportViewer
- DownloadButton
- ReportMetadataPanel

Dependencies:
- report download APIs
- PDF viewer library

Effort Estimate:
- 0.5 sprint

### Evidence Viewer
API Mapping:
- GET /api/v1/evidence/{id}
- GET /api/v1/evidence/{id}/preview

Components:
- EvidenceViewer
- ImageGallery
- VideoPlayer
- DocumentPreview

Dependencies:
- evidence APIs
- media viewer components

Effort Estimate:
- 1 sprint

### Downloads
API Mapping:
- GET /api/v1/reports/{id}/download
- GET /api/v1/invoices/{id}/download

Components:
- DownloadActionMenu
- DownloadList

Dependencies:
- report and invoice APIs

Effort Estimate:
- 0.5 sprint

---

## Subscription Management

### Plans
API Mapping:
- GET /api/v1/subscriptions/plans

Components:
- PlanCard
- FeatureComparisonTable

Dependencies:
- subscription plans API

Effort Estimate:
- 1 sprint

### Purchase
API Mapping:
- POST /api/v1/subscriptions/purchase

Components:
- PurchaseFlow
- CheckoutSummary
- SubscriptionConfirmation

Dependencies:
- payments module
- subscription APIs

Effort Estimate:
- 1 sprint

### Renewal
API Mapping:
- POST /api/v1/subscriptions/{id}/renew

Components:
- RenewalDialog
- SummaryCard

Dependencies:
- subscription service APIs

Effort Estimate:
- 0.5 sprint

### Upgrade
API Mapping:
- POST /api/v1/subscriptions/{id}/upgrade

Components:
- UpgradeDialog
- PlanComparison

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Downgrade
API Mapping:
- POST /api/v1/subscriptions/{id}/downgrade

Components:
- DowngradeConfirmationDialog

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Pause
API Mapping:
- POST /api/v1/subscriptions/{id}/pause

Components:
- PauseSubscriptionDialog

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

### Cancellation
API Mapping:
- POST /api/v1/subscriptions/{id}/cancel

Components:
- CancelSubscriptionDialog
- ReasonSelector

Dependencies:
- subscription APIs

Effort Estimate:
- 0.5 sprint

---

## Payments

### Checkout
API Mapping:
- POST /api/v1/payments/checkout

Components:
- CheckoutForm
- PaymentMethodsSelector
- OrderSummary

Dependencies:
- payment API
- secure token handling

Effort Estimate:
- 1 sprint

### Success
API Mapping:
- GET /api/v1/payments/{id}

Components:
- PaymentSuccessPage
- ReceiptSummary

Dependencies:
- payment status APIs

Effort Estimate:
- 0.5 sprint

### History
API Mapping:
- GET /api/v1/payments
- GET /api/v1/invoices/my

Components:
- PaymentHistoryTable
- InvoiceList

Dependencies:
- invoice API

Effort Estimate:
- 0.5 sprint

### Invoices
API Mapping:
- GET /api/v1/invoices/{id}
- POST /api/v1/invoices/{id}/download

Components:
- InvoiceViewer
- DownloadButton

Dependencies:
- invoice API
- report viewer

Effort Estimate:
- 0.5 sprint

---

## Support

### Complaints
API Mapping:
- POST /api/v1/complaints
- GET /api/v1/complaints/my

Components:
- ComplaintForm
- ComplaintList
- ComplaintDetails

Dependencies:
- complaint API

Effort Estimate:
- 1 sprint

### Tracking
API Mapping:
- GET /api/v1/complaints/{id}
- PATCH /api/v1/complaints/{id}/resolve

Components:
- ComplaintTimeline
- StatusBadge

Dependencies:
- complaint tracking API

Effort Estimate:
- 0.5 sprint

### FAQ
Components:
- FAQAccordion
- HelpCenterPage
- SearchableHelpContent

Dependencies:
- static content / CMS if available

Effort Estimate:
- 0.5 sprint

---

# Agent App Modules

## Dashboard
API Mapping:
- GET /api/v1/operations/assignments
- GET /api/v1/agents/me/dashboard

Components:
- AgentOverviewCard
- TaskSummaryWidget
- ScheduleCard
- LocationStatusCard

Dependencies:
- agent APIs
- operations APIs

Effort Estimate:
- 1 sprint

## Task List
API Mapping:
- GET /api/v1/operations/assignments
- GET /api/v1/service-requests/my

Components:
- TaskList
- FilterByStatus
- AssignmentDetailsCard

Dependencies:
- assignment APIs
- service request APIs

Effort Estimate:
- 1 sprint

## Task Details
API Mapping:
- GET /api/v1/service-requests/{id}
- GET /api/v1/visits/{id}

Components:
- TaskDetailHeader
- ActionButtons
- TimelineView
- Property and Customer Summary

Dependencies:
- service request and visit APIs

Effort Estimate:
- 0.5 sprint

## GPS Capture
API Mapping:
- POST /api/v1/visits/{id}/gps/checkin
- POST /api/v1/visits/{id}/gps/checkout
- GET /api/v1/visits/{id}/gps

Components:
- GPSCaptureButton
- GPSMapView
- LocationStatusWidget

Dependencies:
- maps library
- visit API

Effort Estimate:
- 1 sprint

## Photo Capture
API Mapping:
- POST /api/v1/visits/{id}/evidence
- POST /api/v1/properties/{id}/documents

Components:
- CameraCapture
- ImagePreview
- UploadQueue

Dependencies:
- media upload API
- camera utilities

Effort Estimate:
- 1 sprint

## Video Capture
API Mapping:
- POST /api/v1/visits/{id}/evidence

Components:
- VideoRecorder
- PlaybackPreview

Dependencies:
- media capture components
- evidence API

Effort Estimate:
- 1 sprint

## Observation Entry
API Mapping:
- PATCH /api/v1/visits/{id}
- POST /api/v1/visits/{id}/evidence

Components:
- ObservationForm
- ChecklistForm
- NotesEditor

Dependencies:
- visit update API

Effort Estimate:
- 0.5 sprint

## Report Drafting
API Mapping:
- POST /api/v1/reports
- GET /api/v1/reports/{id}

Components:
- ReportDraftEditor
- AutoFillSummary
- SaveDraftButton

Dependencies:
- report generation APIs

Effort Estimate:
- 1 sprint

## Visit Completion
API Mapping:
- PATCH /api/v1/visits/{id}/complete

Components:
- CompletionSummary
- FinalChecklist
- SubmitButton

Dependencies:
- visit completion API

Effort Estimate:
- 0.5 sprint

---

# Operations Portal Modules

## Operations Dashboard
API Mapping:
- GET /api/v1/operations/dashboard
- GET /api/v1/operations/workload

Components:
- KPITiles
- PipelineView
- WorkloadChart
- SLAStatusCard

Dependencies:
- operations dashboard APIs
- charting library

Effort Estimate:
- 1 sprint

## Task Assignment
API Mapping:
- GET /api/v1/operations/assignments
- POST /api/v1/operations/assignments
- PATCH /api/v1/operations/assignments/{id}

Components:
- AssignmentBoard
- AgentSelector
- QueuePanel
- ReassignmentDialog

Dependencies:
- operations APIs
- agent APIs

Effort Estimate:
- 1 sprint

## Agent Tracking
API Mapping:
- GET /api/v1/agents
- GET /api/v1/visits
- GET /api/v1/operations/monitoring

Components:
- AgentMapView
- LiveStatusList
- RouteTimeline

Dependencies:
- agents and visits APIs
- maps and location services

Effort Estimate:
- 1 sprint

## Visit Monitoring
API Mapping:
- GET /api/v1/visits
- GET /api/v1/visits/{id}

Components:
- VisitMonitorBoard
- StatusFilters
- MonitoringTimeline

Dependencies:
- visit APIs

Effort Estimate:
- 0.5 sprint

## Report Review
API Mapping:
- GET /api/v1/reports
- GET /api/v1/reports/{id}
- PATCH /api/v1/reports/{id}

Components:
- ReportReviewQueue
- ReviewCommentsPanel
- ApprovalActionBar

Dependencies:
- report APIs

Effort Estimate:
- 1 sprint

## Complaint Management
API Mapping:
- GET /api/v1/complaints
- PATCH /api/v1/complaints/{id}/resolve
- PATCH /api/v1/complaints/{id}/escalate

Components:
- ComplaintQueue
- ComplaintDetailPanel
- ResolutionDialog

Dependencies:
- complaint APIs

Effort Estimate:
- 1 sprint

## Escalation Management
API Mapping:
- GET /api/v1/operations/escalations
- POST /api/v1/operations/escalations

Components:
- EscalationBoard
- EscalationReasonForm
- PrioritizationControls

Dependencies:
- escalations API

Effort Estimate:
- 0.5 sprint

## Customer Management
API Mapping:
- GET /api/v1/customers
- GET /api/v1/customers/{id}

Components:
- CustomerTable
- CustomerProfileDrawer

Dependencies:
- customer APIs

Effort Estimate:
- 0.5 sprint

## Property Management
API Mapping:
- GET /api/v1/properties
- GET /api/v1/properties/{id}

Components:
- PropertyPortfolioView
- StatusFilters
- OwnershipTable

Dependencies:
- property APIs

Effort Estimate:
- 0.5 sprint

---

# Admin Portal Modules

## Admin Dashboard
API Mapping:
- GET /api/v1/admin/dashboard
- GET /api/v1/admin/analytics

Components:
- KPIOverview
- RevenueChart
- LTVInsights
- FunnelDashboard

Dependencies:
- admin analytics APIs
- charting library

Effort Estimate:
- 1 sprint

## Users
API Mapping:
- GET /api/v1/admin/users
- POST /api/v1/admin/users
- PATCH /api/v1/admin/users/{id}

Components:
- UserTable
- UserDrawer
- RoleSelector

Dependencies:
- admin user APIs
- auth roles APIs

Effort Estimate:
- 1 sprint

## Roles
API Mapping:
- GET /api/v1/auth/roles
- GET /api/v1/auth/roles/{roleId}/permissions

Components:
- RoleMatrix
- PermissionToggleList

Dependencies:
- auth permission APIs

Effort Estimate:
- 0.5 sprint

## Agents
API Mapping:
- GET /api/v1/agents
- PATCH /api/v1/agents/{id}

Components:
- AgentManagementTable
- AgentProfileDrawer

Dependencies:
- agent APIs

Effort Estimate:
- 0.5 sprint

## Partners
API Mapping:
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}

Components:
- PartnerTable
- PartnerDetailDrawer

Dependencies:
- marketplace APIs

Effort Estimate:
- 0.5 sprint

## Vendors
API Mapping:
- GET /api/v1/marketplace/vendors
- POST /api/v1/marketplace/vendors

Components:
- VendorList
- VendorApprovalFlow

Dependencies:
- vendor APIs

Effort Estimate:
- 0.5 sprint

## Services
API Mapping:
- GET /api/v1/service-catalog
- POST /api/v1/service-catalog
- PUT /api/v1/service-catalog/{id}

Components:
- ServiceCatalogManager
- PricingEditor

Dependencies:
- catalog APIs

Effort Estimate:
- 1 sprint

## Subscriptions
API Mapping:
- GET /api/v1/subscriptions/plans
- POST /api/v1/admin/pricing/plans

Components:
- SubscriptionPlanManager
- PlanEditor

Dependencies:
- subscription plans APIs

Effort Estimate:
- 1 sprint

## Pricing
API Mapping:
- GET /api/v1/admin/pricing/plans
- PUT /api/v1/admin/pricing/plans/{id}

Components:
- PricingTable
- PricingForm

Dependencies:
- admin pricing APIs

Effort Estimate:
- 0.5 sprint

## Coupons
API Mapping:
- GET /api/v1/admin/coupons
- POST /api/v1/admin/coupons

Components:
- CouponList
- CouponForm

Dependencies:
- admin coupons if present at backend

Effort Estimate:
- 0.5 sprint

## Revenue
API Mapping:
- GET /api/v1/admin/analytics/revenue
- GET /api/v1/payments/reconciliation

Components:
- RevenueSummaryChart
- RevenueTable

Dependencies:
- analytics and payment APIs

Effort Estimate:
- 0.5 sprint

## Payments
API Mapping:
- GET /api/v1/payments/reconciliation
- GET /api/v1/payments

Components:
- PaymentAdminTable
- ReconciliationStatusPanel

Dependencies:
- reconciliation APIs

Effort Estimate:
- 0.5 sprint

## Analytics
API Mapping:
- GET /api/v1/admin/analytics
- GET /api/v1/reports

Components:
- AnalyticsDashboard
- KPIComparisonCharts

Dependencies:
- analytics APIs
- charts library

Effort Estimate:
- 1 sprint

## Audit Logs
API Mapping:
- GET /api/v1/admin/audit

Components:
- AuditLogTable
- AuditDetailDrawer

Dependencies:
- audit APIs

Effort Estimate:
- 0.5 sprint

## Configuration
API Mapping:
- GET /api/v1/admin/config
- PUT /api/v1/admin/config/{key}

Components:
- ConfigForm
- ToggleSettings

Dependencies:
- configuration APIs

Effort Estimate:
- 0.5 sprint

---

# NRI Modules

## NRI Dashboard
API Mapping:
- GET /api/v1/nri/dashboard

Components:
- SummaryCards
- InvestmentOverview
- AlertsPanel

Dependencies:
- NRI-specific APIs or enriched endpoints

Effort Estimate:
- 1 sprint

## Monitoring Summary
API Mapping:
- GET /api/v1/properties
- GET /api/v1/reports
- GET /api/v1/notifications

Components:
- MonitoringSummaryGrid
- TimelineStatus
- AlertTicker

Dependencies:
- property and report APIs

Effort Estimate:
- 0.5 sprint

## Video Reports
API Mapping:
- GET /api/v1/reports
- GET /api/v1/evidence/{id}

Components:
- VideoReportList
- EvidenceViewer
- DownloadActions

Dependencies:
- evidence and report APIs

Effort Estimate:
- 1 sprint

## Relationship Manager
API Mapping:
- GET /api/v1/admin/users
- GET /api/v1/customers

Components:
- RMContactPanel
- ClientPortfolioView

Dependencies:
- customer and admin APIs

Effort Estimate:
- 0.5 sprint

## Emergency Alerts
API Mapping:
- GET /api/v1/notifications
- POST /api/v1/notifications

Components:
- AlertCard
- EmergencyActionList

Dependencies:
- notification APIs

Effort Estimate:
- 0.5 sprint

---

# Marketplace Modules

## Vendor Marketplace
API Mapping:
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}

Components:
- VendorSearch
- VendorCardGrid
- VendorFilters

Dependencies:
- marketplace APIs

Effort Estimate:
- 1 sprint

## Vendor Details
API Mapping:
- GET /api/v1/marketplace/vendors/{id}

Components:
- VendorProfileHeader
- ServiceGrid
- ReviewList

Dependencies:
- vendor detail API

Effort Estimate:
- 0.5 sprint

## Request Quotation
API Mapping:
- POST /api/v1/marketplace/quotations

Components:
- QuotationRequestForm
- ItemizedCostSummary

Dependencies:
- quotation API

Effort Estimate:
- 1 sprint

## Quotation Comparison
API Mapping:
- GET /api/v1/marketplace/quotations/my
- GET /api/v1/marketplace/quotations/{id}

Components:
- QuoteComparisonTable
- SelectedVendorSummary

Dependencies:
- quotation APIs

Effort Estimate:
- 0.5 sprint

---

# Analytics Modules

## Customer Analytics
API Mapping:
- GET /api/v1/admin/analytics/customers
- GET /api/v1/customers

Components:
- CustomerRetentionChart
- SegmentTables

Dependencies:
- analytics APIs

Effort Estimate:
- 0.5 sprint

## Operations Analytics
API Mapping:
- GET /api/v1/admin/analytics/operations
- GET /api/v1/operations/dashboard

Components:
- QueuePerformanceChart
- ResolutionTimeMetrics

Dependencies:
- operations analytics

Effort Estimate:
- 0.5 sprint

## Revenue Analytics
API Mapping:
- GET /api/v1/admin/analytics/revenue
- GET /api/v1/payments/reconciliation

Components:
- RevenueTrendChart
- PaymentStatsCards

Dependencies:
- payment and analytics APIs

Effort Estimate:
- 0.5 sprint

## Agent Performance
API Mapping:
- GET /api/v1/agents
- GET /api/v1/admin/analytics/agents

Components:
- AgentPerformanceTable
- PerformanceTrendChart

Dependencies:
- agent APIs and analytics

Effort Estimate:
- 0.5 sprint

---

# Shared Component Library

## Forms
- TextInput
- SelectField
- CheckboxGroup
- RadioGroup
- DatePicker
- TimePicker
- FileUpload
- DynamicForm

## Tables
- DataTable
- ColumnFilters
- SortableTable
- PaginationTable

## Cards
- InfoCard
- SummaryCard
- KPIStatCard
- EmptyStateCard

## Dialogs
- ConfirmationDialog
- FormDialog
- DetailsDialog

## Notifications
- Toast
- SnackBar
- InlineNotification
- Banner

## File Upload
- SingleFileUpload
- MultiFileUpload
- UploadProgress

## Media Viewer
- ImageViewer
- VideoViewer
- DocumentPreview

## Maps
- MapContainer
- MarkerPin
- MapSearch

## Charts
- BarChart
- LineChart
- PieChart
- AreaChart

## Pagination
- PaginationBar
- PageSizeSelector

## Search
- SearchInput
- DebouncedSearch
- FilterChips

## Filters
- RangeFilter
- TagFilter
- DateRangeFilter
- StatusFilter

---

# Routing Plan

## Public Routes
- /login
- /register
- /forgot-password
- /otp/verify
- /launch

## Customer Routes
- /customer/dashboard
- /customer/properties
- /customer/properties/new
- /customer/properties/:id
- /customer/services
- /customer/services/:id
- /customer/service-requests
- /customer/service-requests/:id
- /customer/reports
- /customer/subscriptions
- /customer/payments
- /customer/support
- /customer/profile

## Agent Routes
- /agent/dashboard
- /agent/tasks
- /agent/tasks/:id
- /agent/visits
- /agent/visits/:id
- /agent/evidence
- /agent/reports

## Operations Routes
- /operations/dashboard
- /operations/assignments
- /operations/agents
- /operations/visits
- /operations/reports
- /operations/complaints
- /operations/escalations
- /operations/customers
- /operations/properties

## Admin Routes
- /admin/dashboard
- /admin/users
- /admin/roles
- /admin/agents
- /admin/partners
- /admin/vendors
- /admin/services
- /admin/subscriptions
- /admin/pricing
- /admin/payments
- /admin/revenue
- /admin/analytics
- /admin/audit
- /admin/config

## Protected Routes
- all application routes require authentication
- role check at route and page boundaries
- loaders and guards for denied access
- redirect to unauthorized page when missing permissions

---

# State Management Plan

## Redux Store Structure
```text
store/
  app/
    uiSlice
    themeSlice
    notificationSlice
  auth/
    authSlice
    sessionSlice
  customer/
    profileSlice
    propertySlice
    serviceSlice
  agent/
    taskSlice
    visitSlice
    evidenceSlice
  operations/
    dashboardSlice
    assignmentSlice
  admin/
    userSlice
    configSlice
    analyticsSlice
  payments/
    paymentSlice
    invoiceSlice
  notifications/
    notificationSlice
  marketplace/
    vendorSlice
    quotationSlice
```

## React Query Usage
Use React Query for:
- server-state fetching
- caching of list/detail endpoints
- background invalidation
- optimistic update flows
- mutation and retry behavior

## Caching
Use React Query cache for:
- customer profile
- property list and details
- service catalog
- subscription plans
- payment history
- dashboard summaries
- admin metadata

## Optimistic Updates
Use optimistic UI for:
- notification read state
- task status updates
- form save flows
- property status updates
- complaint status changes
- service request lifecycle changes

---

# Testing Plan

## Unit Tests
- utilities
- validators
- selectors
- reducers
- custom hooks
- API parsing logic

## Component Tests
- form validation
- route guards
- table behavior
- dialogs and modals
- dashboard widgets
- complex form interactions

## Integration Tests
- auth flow
- customer property lifecycle
- service booking and tracking
- payment flow
- report download
- complaint actions
- admin config updates

## E2E Tests
- login and onboarding
- property listing and creation
- service request booking
- agent task execution
- payment checkout
- report generation and download
- admin user management
- role-based route access

---

# Sprint-wise Delivery Plan

## Sprint 1
Focus:
- design system foundation
- app shell and route structure
- auth flows
- basic dashboard shell
- common API client and react-query setup

Deliverables:
- app scaffolding
- theme and design tokens
- login/register/reset flows
- protected route guards
- base query client and error handling

---

## Sprint 2
Focus:
- customer app core
- property management
- service catalog and booking

Deliverables:
- dashboard
- property list/details/add/edit
- service catalog and booking flow
- tracking and history screens

---

## Sprint 3
Focus:
- agent app
- visit management
- evidence management
- report draft flow

Deliverables:
- agent dashboard
- task list and details
- visit operations
- GPS and media capture
- report drafting

---

## Sprint 4
Focus:
- subscription and payment flows
- notifications
- complaint management

Deliverables:
- subscription plan UI
- payment checkout and history
- invoice and receipt views
- complaint creation and tracking
- notification center

---

## Sprint 5
Focus:
- operations portal
- admin portal
- analytics screens
- marketplace baseline

Deliverables:
- operations dashboard and assignment management
- admin user and config flows
- analytics charts
- marketplace vendor listing and quotes

---

## Sprint 6
Focus:
- production readiness
- UX finalization
- accessibility and performance hardening
- QA and UAT support

Deliverables:
- final bug fixes
- visual polish
- accessibility review
- route and data validation
- UAT sign-off

---

# Team Structure

## Frontend Developers
- feature-focused engineers for customer, agent, operations, and admin apps
- strong state, API integration, and React architecture experience

## UI/UX
- design system ownership
- wireframe and usability review
- accessibility and user flow validation

## QA
- component and integration validation
- regression testing
- E2E validation
- browser compatibility testing

## Product Owner
- feature prioritization
- acceptance criteria
- screen and flow validation
- release readiness sign-off

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Inconsistent API contract usage | broken screen logic and regression issues | enforce generated API client and schema validation | Frontend Architecture
Unclear role boundaries | inaccessible or leaked screens | route guards and role-level feature checks | Frontend Team
Large state complexity | hard-to-debug UI behavior | modular Redux slices and clear data ownership | Frontend Architecture
Too much UI coupling | slow feature development | component-driven architecture and design system | UI/UX + Frontend Team
Weak mobile UX | poor customer and agent experience | mobile-first design, usability reviews | UI/UX
Slow report/media rendering | degraded performance | lazy loading, virtualized tables, and media optimization | Frontend Team
Duplicate screens by role | maintenance overhead | centralize shared components and route-level role slices | Frontend Team
Late backend contract changes | rework and delays | contract review gate before screen implementation | Product / API Team
Complex analytics dashboards | performance degradation | progressive loading and server-side summarized data | Frontend Architecture
Accessibility gaps | poor usability and compliance issues | automated a11y checks and manual review | QA / UX

---

# Success Criteria

The frontend is ready for:
- backend integration
- UAT
- production release

When all of the following are true:
- all priority screens for MVP are implemented
- customer, agent, operations, and admin experiences are role-aware
- design system is consistent and reusable
- API contracts are aligned to backend responses
- forms and validation hold up under real conditions
- responsive layouts work across mobile and desktop
- loading, error, and empty states are implemented
- notifications and dashboards are functional
- tests cover component and integration paths
- route guards and RBAC validate correctly
- UAT sign-off is complete

---

# Final Recommendation

PropertyPilot should be implemented as a multi-app frontend system under a shared design system and common state architecture, with a modular monolith backend behind it. The most effective path is to deliver the customer app first, then agent and operations flows, followed by admin and analytics experiences.

The recommended delivery sequence is:
- shared app shell and auth
- customer flows
- agent flows
- operations flows
- admin and analytics
- marketplace and NRI expansion

The frontend should enforce:
- consistent API usage patterns
- UI validation standards
- RBAC-driven access control
- modular component reuse
- query-based data loading
- responsive design standards
- strong testing gates before UAT and release

This produces a frontend architecture that is maintainable, fast to iterate, and ready for MVP, scale, and production deployment.
