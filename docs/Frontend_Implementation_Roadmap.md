````markdown
# Frontend Implementation Roadmap

Document Type: Frontend Engineering Roadmap  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Frontend Architecture / Product Engineering

---

# Objectives

The frontend roadmap defines the implementation sequence for the PropertyPilot MVP across:
- customer mobile app
- agent mobile app
- operations portal
- admin portal

The primary objectives are to:
- deliver the highest-value user journeys in dependency order
- align frontend implementation with backend API contracts and database schema
- enable consistent validation, API handling, state management, and security patterns
- support mobile-first UX for customers and agents
- provide operational control and admin visibility in a secure portal model
- keep all screens compliant with accessibility, loading, and error state standards

---

# Technology Stack

Recommended frontend stack:
- Mobile app framework: React Native + Expo or Flutter
- Web portal framework: React + TypeScript + Vite
- UI library: React Native Paper / MUI / Chakra / custom design system
- State management: Redux Toolkit or Zustand + RTK Query or React Query
- Form library: React Hook Form / Formik
- Navigation: React Navigation for mobile, React Router for web
- API client: Axios or Fetch wrapper with centralized auth and retry logic
- Offline support: local store with persistence for read-only data and queued actions
- Caching: react-query / persisted cache with TTL and invalidation
- Analytics: event tracking service
- Mobile features: camera, geolocation, file upload, push notifications
- Testing: Jest, React Native Testing Library, Cypress for web flow smoke tests
- CI/CD: frontend validation pipeline, design review, Lighthouse or performance checks

---

# Application Structure

## Customer Mobile App
- onboarding/auth
- dashboard/home
- property management
- service catalog and booking
- tracking and history
- payment / subscription / invoices
- notifications and complaints

## Agent Mobile App
- login
- task list and assignment queue
- service execution
- GPS capture and validation
- evidence upload
- visit completion / status tracking

## Operations Portal
- service request dashboard
- queue management
- assignment control
- report review
- complaint triage
- customer support summary

## Admin Portal
- user management
- role and permission management
- pricing management
- subscription administration
- analytics dashboard
- system configuration
- audit log review

---

# Phase 1

## 1. Authentication Screens

### 1.1 Welcome / Splash Screen
Purpose:
- launch app and onboard user to login or registration

Dependencies:
- app config
- auth service readiness
- existing user session check

APIs:
- none or GET /app/config

Navigation:
- Login -> Login screen
- Register -> Registration
- Authenticated user -> Dashboard

Permissions:
- public

Validation Rules:
- none for initial app start

Loading States:
- splash animation or skeleton

Error Handling:
- if config fails, show retry and offline state

Definition of Done:
- app loads successfully
- redirect rules work
- no blocking error for valid startup

---

### 1.2 Login Screen
Purpose:
- allow users to sign in with mobile number or identifier

Dependencies:
- auth API
- OTP service
- session store

APIs:
- POST /auth/customers/login
- POST /auth/customers/otp/send
- POST /auth/agents/login

Navigation:
- success -> OTP or dashboard
- register -> Registration
- forgot access -> support or recovery flow

Permissions:
- public

Validation Rules:
- valid phone or identifier format
- required fields
- rate limit handling
- no multiple simultaneous submit

Loading States:
- button spinner
- input disabled while request in progress

Error Handling:
- invalid credentials
- rate limit
- offline mode
- validation message mapping

Definition of Done:
- login request sends correct payload
- invalid inputs prevented
- authenticated session stored securely

---

### 1.3 OTP Verification Screen
Purpose:
- verify customer or agent identity using OTP

Dependencies:
- login flow data
- OTP model
- token/session service

APIs:
- POST /auth/customers/otp/verify
- POST /auth/agents/otp/verify
- POST /auth/customers/otp/send

Navigation:
- success -> Dashboard
- resend -> refresh OTP
- back -> Login

Permissions:
- public, user-specific session

Validation Rules:
- 6-digit OTP required
- expiry enforcement
- max attempts enforcement

Loading States:
- OTP field disabled while verifying
- spinner on verification button

Error Handling:
- invalid OTP
- expired OTP
- rate-limited resend
- network failure with retry

Definition of Done:
- correct OTP routes to authenticated dashboard
- OTP expiry and retry flows work
- tokens are stored securely

---

### 1.4 Customer Registration Screen
Purpose:
- create customer account with profile and consent information

Dependencies:
- customer model
- auth contract
- OTP service

APIs:
- POST /auth/customers/register

Navigation:
- submit -> OTP verification or login
- cancel -> login

Permissions:
- public

Validation Rules:
- required first/last name
- valid phone and email
- consent required
- unique phone/email enforcement

Loading States:
- submit button spinner
- disabled form while submitting

Error Handling:
- duplicate account
- invalid email/phone
- API failure with retry
- duplicate consent or validation message mapping

Definition of Done:
- customer account is created without invalid data
- OTP flow followed after successful registration

---

## 2. Customer Dashboard
Purpose:
- provide a landing view with service status, property summary, and quick actions

Dependencies:
- auth session
- customer profile
- property list
- active service requests

APIs:
- GET /customers/{customerId}/profile
- GET /customers/{customerId}/properties
- GET /customers/{customerId}/service-requests
- GET /customers/{customerId}/notifications

Navigation:
- property -> Property List
- service status -> tracking view
- payments -> payment history
- subscriptions -> subscription detail

Permissions:
- customer self

Validation Rules:
- session must be valid
- user must have active customer profile

Loading States:
- dashboard skeleton
- refresh action spinner

Error Handling:
- empty state when no property or service data
- network failure fallback
- unauthorized session redirect

Definition of Done:
- dashboard renders user-specific data
- summary cards are accurate
- session expiration redirects to login

---

## 3. Property Management

### 3.1 Property List Screen
Purpose:
- list all customer properties

Dependencies:
- customer session
- property API
- address and ownership models

APIs:
- GET /customers/{customerId}/properties
- GET /properties/search

Navigation:
- add property -> Add Property
- item tap -> Property Details
- filter -> update list
- back -> dashboard

Permissions:
- customer self
- support/admin with explicit access

Validation Rules:
- valid property data only
- list should filter by active state if needed

Loading States:
- skeleton cards
- list refresh spinner

Error Handling:
- empty property state
- API failure fallback
- permission denial message

Definition of Done:
- property list loads and filters
- empty states handled
- navigation to detail works

---

### 3.2 Add Property Screen
Purpose:
- register a new property with address and ownership metadata

Dependencies:
- property-create API
- customer profile
- location/GPS capability

APIs:
- POST /properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership

Navigation:
- save -> Property Details
- cancel -> Property List

Permissions:
- customer self
- support/admin if assisted flow

Validation Rules:
- required address fields
- owner metadata required
- valid property type
- GPS optional depending on workflow
- document validation for verification steps

Loading States:
- form submit spinner
- GPS validation spinner
- upload progress for document files

Error Handling:
- duplicate property record
- invalid address
- upload failure
- GPS validation failure with recovery prompt

Definition of Done:
- property is created successfully
- status and verification flags are set correctly
- user can continue to detail view

---

### 3.3 Property Details Screen
Purpose:
- show details, ownership status, verification state, and supporting documents

Dependencies:
- property API
- verification APIs
- document API

APIs:
- GET /properties/{propertyId}
- GET /properties/{propertyId}/documents
- PATCH /properties/{propertyId}/status

Navigation:
- edit -> Add Property
- back -> Property List
- verify -> verification flow
- documents -> evidence viewer

Permissions:
- owner and authorized operations/admin

Validation Rules:
- property exists
- document and verification status mapping
- state transition validation

Loading States:
- skeleton detail view
- document loading spinner

Error Handling:
- missing verification data
- fetch failure with retry
- unauthorized access blocking

Definition of Done:
- details render correctly
- verification statuses should match API results
- editing and verification flows work

---

# Phase 2

## 4. Service Management

### 4.1 Service Catalog Screen
Purpose:
- display available services and allow selection

Dependencies:
- service catalog APIs
- user property data
- customer session

APIs:
- GET /services
- GET /services/{serviceId}

Navigation:
- category filter -> update list
- service tap -> booking flow
- back -> dashboard

Permissions:
- customer
- support/admin read access

Validation Rules:
- service must be active
- valid price metadata
- duration and category required

Loading States:
- card skeletons
- filter loading state

Error Handling:
- no services available
- fetch failure with retry
- unavailable service message

Definition of Done:
- catalog displays valid service list
- selection leads to booking flow
- empty states protected

---

### 4.2 Service Booking Screen
Purpose:
- capture booking date, service type, property selection, notes, and service requirements

Dependencies:
- property list
- service selection
- GPS and validation policy

APIs:
- POST /service-requests
- GET /customers/{customerId}/properties
- POST /properties/{propertyId}/verify/gps

Navigation:
- continue -> Booking Summary
- back -> Service Catalog
- cancel -> dashboard

Permissions:
- customer self
- support/admin if assisted

Validation Rules:
- required property selected
- required service selected
- future date/time only
- notes length validation
- GPS validation if service policy requires

Loading States:
- form submit spinner
- GPS verifier spinner

Error Handling:
- invalid time/date
- service not available
- GPS failure with prompt
- duplicate or incomplete request detection

Definition of Done:
- valid booking creates request
- invalid policies block submission
- booking summary loads with captured details

---

### 4.3 Booking Summary Screen
Purpose:
- final review before request creation and payment

Dependencies:
- booking form state
- payment flow
- service request model

APIs:
- POST /service-requests
- POST /payments/initiate

Navigation:
- confirm -> Payment Screen
- edit -> Service Booking
- cancel -> dashboard

Permissions:
- customer self

Validation Rules:
- all booking details complete
- payment amount valid
- service request state transitions valid

Loading States:
- confirm spinner
- payment initialization spinner

Error Handling:
- API failure
- payment initiation issue
- request validation failure with correction path

Definition of Done:
- summary accurately reflects final request
- user can proceed to payment
- cancel/edit flows work

---

### 4.4 Service Tracking Screen
Purpose:
- show request status, timeline, and progress

Dependencies:
- service request API
- request history API
- payment/subscription status if relevant

APIs:
- GET /service-requests/{requestId}
- GET /service-requests/{requestId}/history

Navigation:
- back -> dashboard
- timeline item -> details
- evidence -> Evidence Viewer
- complaint -> complaint form

Permissions:
- customer self
- assigned agent and authorized support/admin

Validation Rules:
- request must exist
- status transitions permitted
- read access enforced

Loading States:
- skeleton detail view
- refresh spinner

Error Handling:
- request not found
- permission restricted
- empty timeline fallback

Definition of Done:
- timeline and status correctly render
- customer sees relevant progress
- unauthorized users blocked

---

### 4.5 Reports Screen
Purpose:
- show service metrics and export actions for the current user scope

Dependencies:
- reports API
- customer request data
- admin/ops roles as needed

APIs:
- GET /reports/summary
- GET /reports/service-requests
- POST /reports/export

Navigation:
- export -> file download
- filter -> update data
- dashboard -> back

Permissions:
- operations/admin
- customer limited reports if designed

Validation Rules:
- valid date range
- permitted export format
- access to report category must match role

Loading States:
- chart loading
- export spinner

Error Handling:
- empty report results
- export failure with retry
- unauthorized role message

Definition of Done:
- report views render accurate aggregated values
- export works for approved formats

---

### 4.6 Evidence Viewer Screen
Purpose:
- show uploaded files and metadata for service requests and property verification

Dependencies:
- service evidence API
- document retrieval
- object storage signed URL

APIs:
- GET /service-requests/{requestId}/evidence
- GET /properties/{propertyId}/documents

Navigation:
- back -> tracking or property details
- open item -> fullscreen preview

Permissions:
- authorized request owner, assigned agent, support/admin

Validation Rules:
- valid file ID
- file access must be permission-checked

Loading States:
- image skeleton
- gallery loading

Error Handling:
- missing file
- expired token
- unauthorized access

Definition of Done:
- evidence loads correctly
- access restrictions enforced
- preview and detail states are correct

---

### 4.7 Payment Screen
Purpose:
- handle booking or subscription payment flow

Dependencies:
- payment API
- booking summary state
- payment gateway integration

APIs:
- POST /payments/initiate
- POST /payments/{paymentId}/confirm
- GET /payments/{paymentId}

Navigation:
- success -> Service Tracking
- failure -> retry
- back -> Booking Summary

Permissions:
- customer self

Validation Rules:
- valid amount and currency
- payment method required
- gateway response validation

Loading States:
- payment spinner
- secure form processing state

Error Handling:
- payment declined
- timeout or gateway error
- duplicate payment prevention

Definition of Done:
- payment succeeds or fails with clear user messaging
- processing state is visible
- receipt or status is shown

---

### 4.8 Subscription Screen
Purpose:
- show active subscription details and lifecycle options

Dependencies:
- customer subscription APIs
- pricing data
- payment flow

APIs:
- GET /customers/{customerId}/subscriptions
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}

Navigation:
- manage -> pause/resume/cancel
- back -> dashboard
- pricing -> plans screen

Permissions:
- customer self
- support/admin assist flow

Validation Rules:
- valid plan and billing cycle
- lifecycle action allowed by state machine
- no invalid transitions

Loading States:
- card skeleton
- action spinner

Error Handling:
- invalid state transition
- duplicate lifecycle request
- API failure fallback

Definition of Done:
- current plan and state are displayed correctly
- lifecycle actions are validated and tracked

---

# Phase 3

## 5. Agent Application

### 5.1 Agent Task List Screen
Purpose:
- show daily assigned tasks and statuses

Dependencies:
- auth session
- agent assignments API
- service request data

APIs:
- GET /agents/{agentId}/assignments

Navigation:
- item tap -> Task Detail
- filter -> update list
- profile -> settings
- availability toggle -> status change

Permissions:
- assigned agent
- support/admin read access

Validation Rules:
- task belongs to agent
- date ranges valid
- status filters valid

Loading States:
- skeleton task rows
- pull-to-refresh spinner

Error Handling:
- no task state
- network failure with retry
- permissions issue

Definition of Done:
- only assigned tasks are visible
- task detail is accessible
- assignment queue works

---

### 5.2 Agent Task Details Screen
Purpose:
- provide details of a specific work item and action controls

Dependencies:
- service request API
- evidence API
- assignment data

APIs:
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/history

Navigation:
- start visit -> GPS / Visit Execution
- photos -> Evidence Upload
- back -> Task List
- notes -> update modal

Permissions:
- assigned agent
- operations/admin with override access

Validation Rules:
- valid task owner
- status transition constraints
- required notes for failure or completion if policy requires

Loading States:
- skeleton summary
- status update spinner

Error Handling:
- invalid transition
- request not found
- unauthorized access

Definition of Done:
- task rundown is accurate
- state updates persist
- notes and evidence are linked

---

### 5.3 GPS Capture Screen
Purpose:
- record and validate customer/property location while executing work

Dependencies:
- GPS permission
- geolocation service
- verification API

APIs:
- POST /properties/{propertyId}/verify/gps
- POST /service-requests/{requestId}/gps/validate

Navigation:
- save -> Task Details
- retry -> capture again
- cancel -> return to task

Permissions:
- assigned agent
- operations/admin for review

Validation Rules:
- valid coordinates within allowed range
- location required when policy requires
- failed validation must not auto-complete task

Loading States:
- map loading
- GPS capture spinner
- validation progress

Error Handling:
- GPS unavailable
- permission denied
- invalid coordinates with retry guidance

Definition of Done:
- location capture works on supported devices
- invalid capture is identified clearly
- verification result is recorded

---

### 5.4 Photo Capture Screen
Purpose:
- capture images for evidence and service completion

Dependencies:
- device camera
- upload API
- service request context

APIs:
- POST /service-requests/{requestId}/evidence

Navigation:
- capture -> review -> upload
- cancel -> return to task details

Permissions:
- assigned agent

Validation Rules:
- file type restriction
- size restriction
- required context metadata
- max image count if applicable

Loading States:
- camera preview
- upload progress bar

Error Handling:
- failed capture
- unsupported file type
- upload timeout
- storage failure with retry

Definition of Done:
- user can capture and attach image
- metadata is retained
- evidence appears in task details

---

### 5.5 Evidence Upload Screen
Purpose:
- add evidence images or files to a request

Dependencies:
- photo capture screen or file selector
- evidence API
- storage config

APIs:
- POST /service-requests/{requestId}/evidence

Navigation:
- submit -> Task Details
- remove item -> update selection
- cancel -> previous step

Permissions:
- assigned agent
- support/admin with override

Validation Rules:
- allowed file types only
- max file size
- metadata required for document classification

Loading States:
- upload progress per file
- submit spinner

Error Handling:
- upload rejection
- network interruption
- failed attempts with retry

Definition of Done:
- evidence is uploaded and linked to task
- invalid files blocked
- file metadata stored

---

### 5.6 Visit Completion Screen
Purpose:
- finalize worker visit and update task result

Dependencies:
- task status model
- evidence and notes
- service request update API

APIs:
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence

Navigation:
- complete -> Task Details
- cancel -> current task
- fail -> status update with reason

Permissions:
- assigned agent only

Validation Rules:
- valid state transition
- required cause or reason for failed completion
- notes required for certain completion states

Loading States:
- button spinner
- progress indicator

Error Handling:
- invalid state change
- offline failure
- permission denial

Definition of Done:
- task can be completed and saved
- failure reason captured
- state transition visible to customer and ops

---

# Phase 4

## 6. Operations Portal

### 6.1 Operations Dashboard
Purpose:
- provide overview of service demand, queue, agent performance, and issues

Dependencies:
- report APIs
- service request APIs
- auth roles

APIs:
- GET /operations/dashboard
- GET /reports/summary

Navigation:
- queue -> Service Requests
- tasks -> assignment area
- reports -> report review

Permissions:
- operations
- admin
- leadership role if configured

Validation Rules:
- valid date filters
- role-specific access only

Loading States:
- KPI skeleton
- chart loading states

Error Handling:
- insufficient data state
- backend failure with retry
- unauthorized role prompt

Definition of Done:
- dashboard metrics render correctly
- filters work
- data source aligns with report APIs

---

### 6.2 Service Requests Queue Screen
Purpose:
- review open service requests and assign or reassign tasks

Dependencies:
- service request list API
- assignment API
- agent availability

APIs:
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/status

Navigation:
- item -> Task Detail
- assign -> agent selector
- escalate -> status update

Permissions:
- operations/admin
- support with limited access

Validation Rules:
- valid agents and request ownership
- no invalid reassignment without queue state

Loading States:
- queue skeleton
- assignment action spinner

Error Handling:
- no requests state
- assignment failure
- invalid role assignment

Definition of Done:
- queue items can be filtered and assigned
- updates are visible in task detail and customer view

---

### 6.3 Task Assignment Screen
Purpose:
- allow operations to assign or reassign work to available agents

Dependencies:
- queue API
- agent availability
- assignment API

APIs:
- GET /agents/available
- POST /service-requests/{requestId}/assign

Navigation:
- assign -> confirmation modal
- cancel -> back to queue

Permissions:
- operations/admin

Validation Rules:
- assignment requires active request and eligible agent
- agent availability must be current
- no duplicates for active task

Loading States:
- agent list loading
- assign spinner

Error Handling:
- no available agents
- assignment conflict
- invalid request state

Definition of Done:
- assignment updates request state and queue
- audit logging captures action

---

### 6.4 Report Review Screen
Purpose:
- review operational or financial reports in detail

Dependencies:
- report APIs
- export mechanism
- role-based access

APIs:
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/agents
- POST /reports/export

Navigation:
- filter -> update report
- export -> download or preview
- back -> dashboard

Permissions:
- operations/admin
- leadership if configured

Validation Rules:
- valid date range
- allowed formats
- report permissions enforced

Loading States:
- table skeleton
- export spinner

Error Handling:
- empty report state
- export failure fallback
- unauthorized access

Definition of Done:
- reports align with underlying data
- export works and respects filters

---

### 6.5 Customer Management Screen
Purpose:
- review customer status, request history, and support needs

Dependencies:
- customer profile API
- complaint API
- service request API

APIs:
- GET /customers/{customerId}
- GET /customers/{customerId}/service-requests
- GET /customers/{customerId}/complaints

Navigation:
- customer -> request detail
- complaint -> Complaint Management
- back -> dashboard

Permissions:
- support/admin/operations

Validation Rules:
- customer must exist
- access based on role and scope

Loading States:
- profile skeleton
- request list loading

Error Handling:
- not found customer
- access denied
- empty state on no requests

Definition of Done:
- customer profile and request history visible
- support workflows can access the right records

---

### 6.6 Complaint Management Screen
Purpose:
- review customer complaints and update lifecycle status

Dependencies:
- complaint API
- customer request API
- support workflow

APIs:
- GET /customers/{customerId}/complaints
- POST /customers/{customerId}/complaints
- PATCH /complaints/{complaintId}/status

Navigation:
- open complaint -> detail
- update status -> modal
- close -> dashboard or queue

Permissions:
- support/admin/operations

Validation Rules:
- complaint ID valid
- allowed state transitions only
- required notes for resolution

Loading States:
- list skeleton
- action spinner

Error Handling:
- invalid status
- not found complaint
- duplicate complaint prevention

Definition of Done:
- complaint status changes are traceable
- customer visible updates follow status change

---

# Phase 5

## 7. Admin Portal

### 7.1 Admin Dashboard
Purpose:
- provide high-level platform health, system metrics, and admin actions

Dependencies:
- user management APIs
- system configuration APIs
- report APIs

APIs:
- GET /admin/dashboard
- GET /reports/summary

Navigation:
- users -> User Management
- roles -> Role Management
- pricing -> Pricing Management
- analytics -> Analytics Dashboard
- config -> System Configuration

Permissions:
- admin only

Validation Rules:
- admin session required

Loading States:
- KPI skeleton
- widget loading states

Error Handling:
- service failure
- unauthorized or expired session
- empty metrics state

Definition of Done:
- dashboard loads and maps to the real platform state
- admin actions are available from the workspace

---

### 7.2 User Management Screen
Purpose:
- view and manage system users, statuses, and role assignment

Dependencies:
- user API
- role API
- admin auth

APIs:
- GET /admin/users
- PATCH /admin/users/{userId}/status
- PATCH /admin/users/{userId}/role

Navigation:
- row select -> user detail
- create action -> add user form
- back -> dashboard

Permissions:
- admin only

Validation Rules:
- active/inactive state valid
- role permission valid
- blocked invalid assignment

Loading States:
- table skeleton
- save spinner

Error Handling:
- invalid role
- duplicate user
- permission denial

Definition of Done:
- user account lifecycle works
- role assignment is enforced
- audit logs capture actions

---

### 7.3 Role Management Screen
Purpose:
- create and assign permissions to roles

Dependencies:
- permissions API
- role model
- user management model

APIs:
- GET /admin/permissions
- POST /admin/roles
- PATCH /admin/roles/{roleId}

Navigation:
- role list -> detail view
- save -> return to role list

Permissions:
- admin only

Validation Rules:
- permission names valid
- role name unique
- role restrictions enforced

Loading States:
- list skeleton
- save spinner

Error Handling:
- duplicate role
- invalid permission
- unauthorized roles changed

Definition of Done:
- roles can be created and updated
- permissions are correctly enforced
- audit changes are recorded

---

### 7.4 Pricing Management Screen
Purpose:
- manage service or plan pricing

Dependencies:
- pricing APIs
- service catalog
- subscription plans

APIs:
- GET /services
- PATCH /services/{serviceId}/pricing
- GET /subscriptions/plans
- PATCH /subscriptions/plans/{planId}

Navigation:
- price list -> edit
- save -> confirmation modal

Permissions:
- admin only

Validation Rules:
- positive pricing only
- valid effective date
- price history retained

Loading States:
- table skeleton
- update spinner

Error Handling:
- invalid numeric value
- duplicate price entry
- API failure with retry

Definition of Done:
- pricing updates apply correctly
- effective dates and audit logs are captured

---

### 7.5 Subscription Management Screen
Purpose:
- manage customer subscriptions and lifecycle administration

Dependencies:
- subscription plan API
- customer profile API
- payment status

APIs:
- GET /subscriptions/plans
- GET /customers/{customerId}/subscriptions
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/cancel

Navigation:
- customer -> subscription details
- action -> confirm modal

Permissions:
- admin/support with role scope

Validation Rules:
- lifecycle states valid
- plan or customer must exist
- policy rules enforced

Loading States:
- subscription skeleton
- action spinner

Error Handling:
- invalid transition
- data mismatch
- access denied

Definition of Done:
- subscription management works for active and cancelled accounts
- admin actions are logged

---

### 7.6 Analytics Dashboard
Purpose:
- summarize platform metrics, trend performance, and operational data

Dependencies:
- report APIs
- analytics endpoints
- user permissions

APIs:
- GET /analytics/overview
- GET /analytics/service-trends
- GET /analytics/payment-trends
- GET /analytics/agent-performance

Navigation:
- filter date range -> update metrics
- export -> report or csv

Permissions:
- admin
- operations
- leadership if authorized

Validation Rules:
- filter range valid
- metric names valid
- permission checks on report scope

Loading States:
- chart area skeleton
- filter loading

Error Handling:
- empty dataset
- stale metrics
- unauthorized access

Definition of Done:
- metrics align with report sources
- charts and values update correctly
- admin users can review trends

---

### 7.7 System Configuration Screen
Purpose:
- configure core app and platform settings safely

Dependencies:
- config API
- admin auth
- environment policies

APIs:
- GET /system/config
- PATCH /system/config

Navigation:
- update config -> confirm save
- reject -> revert or cancel

Permissions:
- admin only

Validation Rules:
- settings must pass validation schema
- no unsafe config or invalid values
- environment-specific rules enforced

Loading States:
- settings skeleton
- save spinner

Error Handling:
- configuration invalid
- permission denied
- failure with rollback guidance

Definition of Done:
- config changes apply safely
- change is auditable
- defaults remain safe

---

# Component Strategy

## Shared UI Components
- form fields
- error banners and inline validation
- modals
- confirmation dialogs
- data tables
- charts
- cards and list items
- date/time pickers
- file upload widgets
- OTP input components
- map and location capture component
- camera capture component

## Component Principles
- consistent forms across app and portal
- centralized validation messages
- reusable loading and empty-state patterns
- strict role-aware rendering
- accessibility-first component design
- design tokens for spacing, typography, colors

---

# State Management Strategy

## Mobile App
- use Redux Toolkit or Zustand for global session, user, property, booking, service request states
- use React Query for server state and cache invalidation
- local persisted state for auth token, onboarding status, recent properties
- optimistic updates only for low-risk actions like marking notifications as read

## Portal App
- use React Query for API-driven state
- contextual local state for filters, modals, and tables
- normalized data for list views and detail pages
- central store for auth, permissions, and role metadata

## Shared Rules
- separate server state from UI state
- avoid storing raw PII in client state beyond requirement
- use cache invalidation on mutation success
- keep form state local to edit screens

---

# Offline Strategy

## Mobile
- store essential data locally:
  - recent properties
  - service request state
  - last synced notifications
  - cached service catalog
- queue actions for:
  - note creation
  - photo evidence staging
  - form submission retries
- show offline banner if network is unavailable
- support retry for GET/POST operations with local queue

## Portal
- offline support is optional for admin/ops views
- cache recent report data and service queue snapshots
- queue user actions with explicit “sync pending” state

---

# Caching Strategy

- cache stable data:
  - service catalog
  - pricing plans
  - customer profile
  - frequently used reports
- use TTL with invalidation on mutation
- do not cache sensitive PII beyond session scope
- invalidate cache after update actions:
  - property save
  - service request status change
  - payment confirmation
  - role assignment
  - price update

---

# Performance Strategy

- lazy-load screens and route-based modules
- paginate request and queue lists
- use memoized selectors and list virtualization where needed
- compress and optimize images before upload
- use native map and camera components for mobile efficiency
- prefetch data for next likely screen
- ensure report and analytics requests use query optimization and filters

---

# Security Requirements

- store auth tokens in secure storage
- use role-based route guards
- enforce permission checks before rendering sensitive data
- never render raw payment or token values to the UI
- secure file upload permissions
- sanitize and validate all API payloads
- use cross-site protections and secure headers in web apps
- ensure PII access is auditable and restricted
- implement session timeout and re-auth flow

---

# Accessibility Requirements

- minimum WCAG AA compliance
- keyboard navigation for all portal controls
- screen-reader labels for form inputs
- visible focus indicators
- color contrast compliance
- support screen-reader announcements for toast messages and errors
- OTP fields should be accessible and properly labeled
- camera and file upload flows must have alternative user guidance

---

# Testing Requirements

## Unit Tests
- form validation logic
- role guard logic
- permission rendering logic
- service state reduction logic
- pricing calculation logic
- route decision logic
- analytics transforms

## Integration Tests
- auth and OTP flow
- property creation and detail retrieval
- booking and payment flow
- service request lifecycle
- assignment and queue behavior
- evidence upload and retrieval
- report export and dashboard data mapping

## UI Tests
- login and registration flows
- customer property creation
- service booking and tracking
- agent assignment workflow
- admin role and pricing management
- portal dashboard filters and actions

## E2E Tests
- customer onboarding to booking
- agent assignment to completion
- operations queue to assignment
- admin user and role management
- payment and invoice flow
- complaint handling from submission to resolution

---

# Implementation Timeline

## Phase 1: Foundation and Customer Core
- auth flows
- customer onboarding
- property management
- dashboard
- initial customer UI shell

Duration: 2 weeks

## Phase 2: Service and Billing Experience
- service catalog
- booking flow
- tracking
- payments
- subscriptions
- invoices

Duration: 2 weeks

## Phase 3: Agent Execution
- task list
- task detail
- GPS and evidence
- visit completion
- field workflow validation

Duration: 2 weeks

## Phase 4: Operations and Support
- queue and assignment
- report review
- complaints
- customer support views

Duration: 2 weeks

## Phase 5: Admin and Platform Governance
- user and role management
- pricing management
- analytics
- system config
- audit log review

Duration: 2 weeks

---

# Critical Path Analysis

Critical frontend path:
1. Authentication and session flow
2. Customer dashboard and property management
3. Service catalog and booking flow
4. Payment and subscription flows
5. Agent task workflow
6. GPS and evidence capture
7. Operations queue and assignment
8. Reporting and admin governance features

Risks:
- auth/session mismatch between mobile and web
- service lifecycle state drift from backend
- GPS and camera permissions on mobile devices
- inconsistent role gating across portal apps
- report data mismatches due to filters or stale cache
- upload limits or file validation failures

Mitigations:
- align with central API contract and typed models
- use shared state and role constants
- define device permission handling strategy early
- implement cache invalidation and state sync checks
- validate report and admin access through role-aware route guards

---

# Definition of Done by Screen

A frontend screen is considered done when:
- the screen matches the approved UX specification and API contract
- all required fields and validation rules are implemented
- loading and empty states are handled
- API errors are mapped to user-friendly messages
- permissions are correctly enforced
- navigation flows are wired and tested
- the screen works across supported devices and browsers
- accessibility checks pass for interactive controls
- unit and integration tests cover critical flows
- the screen is demo-ready and ready for QA signoff

---

# Final Summary

This frontend roadmap follows the approved MVP scope and sprint sequencing. It starts with authentication and customer-facing onboarding, then expands into service execution and billing, followed by agent execution and operations workflow, and finally closes with admissions, reporting, and system governance controls.

Implementation should prioritize:
- customer onboarding and property management
- booking lifecycle and service tracking
- agent execution and evidence capture
- operations and report review
- admin control and platform governance

This sequence minimizes frontend risk while enabling rapid demo readiness and production-grade operational coverage.
```// filepath: c:\PropertyPilot\docs\Frontend_Implementation_Roadmap.md
# Frontend Implementation Roadmap

Document Type: Frontend Engineering Roadmap  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Frontend Architecture / Product Engineering

---

# Objectives

The frontend roadmap defines the implementation sequence for the PropertyPilot MVP across:
- customer mobile app
- agent mobile app
- operations portal
- admin portal

The primary objectives are to:
- deliver the highest-value user journeys in dependency order
- align frontend implementation with backend API contracts and database schema
- enable consistent validation, API handling, state management, and security patterns
- support mobile-first UX for customers and agents
- provide operational control and admin visibility in a secure portal model
- keep all screens compliant with accessibility, loading, and error state standards

---

# Technology Stack

Recommended frontend stack:
- Mobile app framework: React Native + Expo or Flutter
- Web portal framework: React + TypeScript + Vite
- UI library: React Native Paper / MUI / Chakra / custom design system
- State management: Redux Toolkit or Zustand + RTK Query or React Query
- Form library: React Hook Form / Formik
- Navigation: React Navigation for mobile, React Router for web
- API client: Axios or Fetch wrapper with centralized auth and retry logic
- Offline support: local store with persistence for read-only data and queued actions
- Caching: react-query / persisted cache with TTL and invalidation
- Analytics: event tracking service
- Mobile features: camera, geolocation, file upload, push notifications
- Testing: Jest, React Native Testing Library, Cypress for web flow smoke tests
- CI/CD: frontend validation pipeline, design review, Lighthouse or performance checks

---

# Application Structure

## Customer Mobile App
- onboarding/auth
- dashboard/home
- property management
- service catalog and booking
- tracking and history
- payment / subscription / invoices
- notifications and complaints

## Agent Mobile App
- login
- task list and assignment queue
- service execution
- GPS capture and validation
- evidence upload
- visit completion / status tracking

## Operations Portal
- service request dashboard
- queue management
- assignment control
- report review
- complaint triage
- customer support summary

## Admin Portal
- user management
- role and permission management
- pricing management
- subscription administration
- analytics dashboard
- system configuration
- audit log review

---

# Phase 1

## 1. Authentication Screens

### 1.1 Welcome / Splash Screen
Purpose:
- launch app and onboard user to login or registration

Dependencies:
- app config
- auth service readiness
- existing user session check

APIs:
- none or GET /app/config

Navigation:
- Login -> Login screen
- Register -> Registration
- Authenticated user -> Dashboard

Permissions:
- public

Validation Rules:
- none for initial app start

Loading States:
- splash animation or skeleton

Error Handling:
- if config fails, show retry and offline state

Definition of Done:
- app loads successfully
- redirect rules work
- no blocking error for valid startup

---

### 1.2 Login Screen
Purpose:
- allow users to sign in with mobile number or identifier

Dependencies:
- auth API
- OTP service
- session store

APIs:
- POST /auth/customers/login
- POST /auth/customers/otp/send
- POST /auth/agents/login

Navigation:
- success -> OTP or dashboard
- register -> Registration
- forgot access -> support or recovery flow

Permissions:
- public

Validation Rules:
- valid phone or identifier format
- required fields
- rate limit handling
- no multiple simultaneous submit

Loading States:
- button spinner
- input disabled while request in progress

Error Handling:
- invalid credentials
- rate limit
- offline mode
- validation message mapping

Definition of Done:
- login request sends correct payload
- invalid inputs prevented
- authenticated session stored securely

---

### 1.3 OTP Verification Screen
Purpose:
- verify customer or agent identity using OTP

Dependencies:
- login flow data
- OTP model
- token/session service

APIs:
- POST /auth/customers/otp/verify
- POST /auth/agents/otp/verify
- POST /auth/customers/otp/send

Navigation:
- success -> Dashboard
- resend -> refresh OTP
- back -> Login

Permissions:
- public, user-specific session

Validation Rules:
- 6-digit OTP required
- expiry enforcement
- max attempts enforcement

Loading States:
- OTP field disabled while verifying
- spinner on verification button

Error Handling:
- invalid OTP
- expired OTP
- rate-limited resend
- network failure with retry

Definition of Done:
- correct OTP routes to authenticated dashboard
- OTP expiry and retry flows work
- tokens are stored securely

---

### 1.4 Customer Registration Screen
Purpose:
- create customer account with profile and consent information

Dependencies:
- customer model
- auth contract
- OTP service

APIs:
- POST /auth/customers/register

Navigation:
- submit -> OTP verification or login
- cancel -> login

Permissions:
- public

Validation Rules:
- required first/last name
- valid phone and email
- consent required
- unique phone/email enforcement

Loading States:
- submit button spinner
- disabled form while submitting

Error Handling:
- duplicate account
- invalid email/phone
- API failure with retry
- duplicate consent or validation message mapping

Definition of Done:
- customer account is created without invalid data
- OTP flow followed after successful registration

---

## 2. Customer Dashboard
Purpose:
- provide a landing view with service status, property summary, and quick actions

Dependencies:
- auth session
- customer profile
- property list
- active service requests

APIs:
- GET /customers/{customerId}/profile
- GET /customers/{customerId}/properties
- GET /customers/{customerId}/service-requests
- GET /customers/{customerId}/notifications

Navigation:
- property -> Property List
- service status -> tracking view
- payments -> payment history
- subscriptions -> subscription detail

Permissions:
- customer self

Validation Rules:
- session must be valid
- user must have active customer profile

Loading States:
- dashboard skeleton
- refresh action spinner

Error Handling:
- empty state when no property or service data
- network failure fallback
- unauthorized session redirect

Definition of Done:
- dashboard renders user-specific data
- summary cards are accurate
- session expiration redirects to login

---

## 3. Property Management

### 3.1 Property List Screen
Purpose:
- list all customer properties

Dependencies:
- customer session
- property API
- address and ownership models

APIs:
- GET /customers/{customerId}/properties
- GET /properties/search

Navigation:
- add property -> Add Property
- item tap -> Property Details
- filter -> update list
- back -> dashboard

Permissions:
- customer self
- support/admin with explicit access

Validation Rules:
- valid property data only
- list should filter by active state if needed

Loading States:
- skeleton cards
- list refresh spinner

Error Handling:
- empty property state
- API failure fallback
- permission denial message

Definition of Done:
- property list loads and filters
- empty states handled
- navigation to detail works

---

### 3.2 Add Property Screen
Purpose:
- register a new property with address and ownership metadata

Dependencies:
- property-create API
- customer profile
- location/GPS capability

APIs:
- POST /properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership

Navigation:
- save -> Property Details
- cancel -> Property List

Permissions:
- customer self
- support/admin if assisted flow

Validation Rules:
- required address fields
- owner metadata required
- valid property type
- GPS optional depending on workflow
- document validation for verification steps

Loading States:
- form submit spinner
- GPS validation spinner
- upload progress for document files

Error Handling:
- duplicate property record
- invalid address
- upload failure
- GPS validation failure with recovery prompt

Definition of Done:
- property is created successfully
- status and verification flags are set correctly
- user can continue to detail view

---

### 3.3 Property Details Screen
Purpose:
- show details, ownership status, verification state, and supporting documents

Dependencies:
- property API
- verification APIs
- document API

APIs:
- GET /properties/{propertyId}
- GET /properties/{propertyId}/documents
- PATCH /properties/{propertyId}/status

Navigation:
- edit -> Add Property
- back -> Property List
- verify -> verification flow
- documents -> evidence viewer

Permissions:
- owner and authorized operations/admin

Validation Rules:
- property exists
- document and verification status mapping
- state transition validation

Loading States:
- skeleton detail view
- document loading spinner

Error Handling:
- missing verification data
- fetch failure with retry
- unauthorized access blocking

Definition of Done:
- details render correctly
- verification statuses should match API results
- editing and verification flows work

---

# Phase 2

## 4. Service Management

### 4.1 Service Catalog Screen
Purpose:
- display available services and allow selection

Dependencies:
- service catalog APIs
- user property data
- customer session

APIs:
- GET /services
- GET /services/{serviceId}

Navigation:
- category filter -> update list
- service tap -> booking flow
- back -> dashboard

Permissions:
- customer
- support/admin read access

Validation Rules:
- service must be active
- valid price metadata
- duration and category required

Loading States:
- card skeletons
- filter loading state

Error Handling:
- no services available
- fetch failure with retry
- unavailable service message

Definition of Done:
- catalog displays valid service list
- selection leads to booking flow
- empty states protected

---

### 4.2 Service Booking Screen
Purpose:
- capture booking date, service type, property selection, notes, and service requirements

Dependencies:
- property list
- service selection
- GPS and validation policy

APIs:
- POST /service-requests
- GET /customers/{customerId}/properties
- POST /properties/{propertyId}/verify/gps

Navigation:
- continue -> Booking Summary
- back -> Service Catalog
- cancel -> dashboard

Permissions:
- customer self
- support/admin if assisted

Validation Rules:
- required property selected
- required service selected
- future date/time only
- notes length validation
- GPS validation if service policy requires

Loading States:
- form submit spinner
- GPS verifier spinner

Error Handling:
- invalid time/date
- service not available
- GPS failure with prompt
- duplicate or incomplete request detection

Definition of Done:
- valid booking creates request
- invalid policies block submission
- booking summary loads with captured details

---

### 4.3 Booking Summary Screen
Purpose:
- final review before request creation and payment

Dependencies:
- booking form state
- payment flow
- service request model

APIs:
- POST /service-requests
- POST /payments/initiate

Navigation:
- confirm -> Payment Screen
- edit -> Service Booking
- cancel -> dashboard

Permissions:
- customer self

Validation Rules:
- all booking details complete
- payment amount valid
- service request state transitions valid

Loading States:
- confirm spinner
- payment initialization spinner

Error Handling:
- API failure
- payment initiation issue
- request validation failure with correction path

Definition of Done:
- summary accurately reflects final request
- user can proceed to payment
- cancel/edit flows work

---

### 4.4 Service Tracking Screen
Purpose:
- show request status, timeline, and progress

Dependencies:
- service request API
- request history API
- payment/subscription status if relevant

APIs:
- GET /service-requests/{requestId}
- GET /service-requests/{requestId}/history

Navigation:
- back -> dashboard
- timeline item -> details
- evidence -> Evidence Viewer
- complaint -> complaint form

Permissions:
- customer self
- assigned agent and authorized support/admin

Validation Rules:
- request must exist
- status transitions permitted
- read access enforced

Loading States:
- skeleton detail view
- refresh spinner

Error Handling:
- request not found
- permission restricted
- empty timeline fallback

Definition of Done:
- timeline and status correctly render
- customer sees relevant progress
- unauthorized users blocked

---

### 4.5 Reports Screen
Purpose:
- show service metrics and export actions for the current user scope

Dependencies:
- reports API
- customer request data
- admin/ops roles as needed

APIs:
- GET /reports/summary
- GET /reports/service-requests
- POST /reports/export

Navigation:
- export -> file download
- filter -> update data
- dashboard -> back

Permissions:
- operations/admin
- customer limited reports if designed

Validation Rules:
- valid date range
- permitted export format
- access to report category must match role

Loading States:
- chart loading
- export spinner

Error Handling:
- empty report results
- export failure with retry
- unauthorized role message

Definition of Done:
- report views render accurate aggregated values
- export works for approved formats

---

### 4.6 Evidence Viewer Screen
Purpose:
- show uploaded files and metadata for service requests and property verification

Dependencies:
- service evidence API
- document retrieval
- object storage signed URL

APIs:
- GET /service-requests/{requestId}/evidence
- GET /properties/{propertyId}/documents

Navigation:
- back -> tracking or property details
- open item -> fullscreen preview

Permissions:
- authorized request owner, assigned agent, support/admin

Validation Rules:
- valid file ID
- file access must be permission-checked

Loading States:
- image skeleton
- gallery loading

Error Handling:
- missing file
- expired token
- unauthorized access

Definition of Done:
- evidence loads correctly
- access restrictions enforced
- preview and detail states are correct

---

### 4.7 Payment Screen
Purpose:
- handle booking or subscription payment flow

Dependencies:
- payment API
- booking summary state
- payment gateway integration

APIs:
- POST /payments/initiate
- POST /payments/{paymentId}/confirm
- GET /payments/{paymentId}

Navigation:
- success -> Service Tracking
- failure -> retry
- back -> Booking Summary

Permissions:
- customer self

Validation Rules:
- valid amount and currency
- payment method required
- gateway response validation

Loading States:
- payment spinner
- secure form processing state

Error Handling:
- payment declined
- timeout or gateway error
- duplicate payment prevention

Definition of Done:
- payment succeeds or fails with clear user messaging
- processing state is visible
- receipt or status is shown

---

### 4.8 Subscription Screen
Purpose:
- show active subscription details and lifecycle options

Dependencies:
- customer subscription APIs
- pricing data
- payment flow

APIs:
- GET /customers/{customerId}/subscriptions
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}

Navigation:
- manage -> pause/resume/cancel
- back -> dashboard
- pricing -> plans screen

Permissions:
- customer self
- support/admin assist flow

Validation Rules:
- valid plan and billing cycle
- lifecycle action allowed by state machine
- no invalid transitions

Loading States:
- card skeleton
- action spinner

Error Handling:
- invalid state transition
- duplicate lifecycle request
- API failure fallback

Definition of Done:
- current plan and state are displayed correctly
- lifecycle actions are validated and tracked

---

# Phase 3

## 5. Agent Application

### 5.1 Agent Task List Screen
Purpose:
- show daily assigned tasks and statuses

Dependencies:
- auth session
- agent assignments API
- service request data

APIs:
- GET /agents/{agentId}/assignments

Navigation:
- item tap -> Task Detail
- filter -> update list
- profile -> settings
- availability toggle -> status change

Permissions:
- assigned agent
- support/admin read access

Validation Rules:
- task belongs to agent
- date ranges valid
- status filters valid

Loading States:
- skeleton task rows
- pull-to-refresh spinner

Error Handling:
- no task state
- network failure with retry
- permissions issue

Definition of Done:
- only assigned tasks are visible
- task detail is accessible
- assignment queue works

---

### 5.2 Agent Task Details Screen
Purpose:
- provide details of a specific work item and action controls

Dependencies:
- service request API
- evidence API
- assignment data

APIs:
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/history

Navigation:
- start visit -> GPS / Visit Execution
- photos -> Evidence Upload
- back -> Task List
- notes -> update modal

Permissions:
- assigned agent
- operations/admin with override access

Validation Rules:
- valid task owner
- status transition constraints
- required notes for failure or completion if policy requires

Loading States:
- skeleton summary
- status update spinner

Error Handling:
- invalid transition
- request not found
- unauthorized access

Definition of Done:
- task rundown is accurate
- state updates persist
- notes and evidence are linked

---

### 5.3 GPS Capture Screen
Purpose:
- record and validate customer/property location while executing work

Dependencies:
- GPS permission
- geolocation service
- verification API

APIs:
- POST /properties/{propertyId}/verify/gps
- POST /service-requests/{requestId}/gps/validate

Navigation:
- save -> Task Details
- retry -> capture again
- cancel -> return to task

Permissions:
- assigned agent
- operations/admin for review

Validation Rules:
- valid coordinates within allowed range
- location required when policy requires
- failed validation must not auto-complete task

Loading States:
- map loading
- GPS capture spinner
- validation progress

Error Handling:
- GPS unavailable
- permission denied
- invalid coordinates with retry guidance

Definition of Done:
- location capture works on supported devices
- invalid capture is identified clearly
- verification result is recorded

---

### 5.4 Photo Capture Screen
Purpose:
- capture images for evidence and service completion

Dependencies:
- device camera
- upload API
- service request context

APIs:
- POST /service-requests/{requestId}/evidence

Navigation:
- capture -> review -> upload
- cancel -> return to task details

Permissions:
- assigned agent

Validation Rules:
- file type restriction
- size restriction
- required context metadata
- max image count if applicable

Loading States:
- camera preview
- upload progress bar

Error Handling:
- failed capture
- unsupported file type
- upload timeout
- storage failure with retry

Definition of Done:
- user can capture and attach image
- metadata is retained
- evidence appears in task details

---

### 5.5 Evidence Upload Screen
Purpose:
- add evidence images or files to a request

Dependencies:
- photo capture screen or file selector
- evidence API
- storage config

APIs:
- POST /service-requests/{requestId}/evidence

Navigation:
- submit -> Task Details
- remove item -> update selection
- cancel -> previous step

Permissions:
- assigned agent
- support/admin with override

Validation Rules:
- allowed file types only
- max file size
- metadata required for document classification

Loading States:
- upload progress per file
- submit spinner

Error Handling:
- upload rejection
- network interruption
- failed attempts with retry

Definition of Done:
- evidence is uploaded and linked to task
- invalid files blocked
- file metadata stored

---

### 5.6 Visit Completion Screen
Purpose:
- finalize worker visit and update task result

Dependencies:
- task status model
- evidence and notes
- service request update API

APIs:
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence

Navigation:
- complete -> Task Details
- cancel -> current task
- fail -> status update with reason

Permissions:
- assigned agent only

Validation Rules:
- valid state transition
- required cause or reason for failed completion
- notes required for certain completion states

Loading States:
- button spinner
- progress indicator

Error Handling:
- invalid state change
- offline failure
- permission denial

Definition of Done:
- task can be completed and saved
- failure reason captured
- state transition visible to customer and ops

---

# Phase 4

## 6. Operations Portal

### 6.1 Operations Dashboard
Purpose:
- provide overview of service demand, queue, agent performance, and issues

Dependencies:
- report APIs
- service request APIs
- auth roles

APIs:
- GET /operations/dashboard
- GET /reports/summary

Navigation:
- queue -> Service Requests
- tasks -> assignment area
- reports -> report review

Permissions:
- operations
- admin
- leadership role if configured

Validation Rules:
- valid date filters
- role-specific access only

Loading States:
- KPI skeleton
- chart loading states

Error Handling:
- insufficient data state
- backend failure with retry
- unauthorized role prompt

Definition of Done:
- dashboard metrics render correctly
- filters work
- data source aligns with report APIs

---

### 6.2 Service Requests Queue Screen
Purpose:
- review open service requests and assign or reassign tasks

Dependencies:
- service request list API
- assignment API
- agent availability

APIs:
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/status

Navigation:
- item -> Task Detail
- assign -> agent selector
- escalate -> status update

Permissions:
- operations/admin
- support with limited access

Validation Rules:
- valid agents and request ownership
- no invalid reassignment without queue state

Loading States:
- queue skeleton
- assignment action spinner

Error Handling:
- no requests state
- assignment failure
- invalid role assignment

Definition of Done:
- queue items can be filtered and assigned
- updates are visible in task detail and customer view

---

### 6.3 Task Assignment Screen
Purpose:
- allow operations to assign or reassign work to available agents

Dependencies:
- queue API
- agent availability
- assignment API

APIs:
- GET /agents/available
- POST /service-requests/{requestId}/assign

Navigation:
- assign -> confirmation modal
- cancel -> back to queue

Permissions:
- operations/admin

Validation Rules:
- assignment requires active request and eligible agent
- agent availability must be current
- no duplicates for active task

Loading States:
- agent list loading
- assign spinner

Error Handling:
- no available agents
- assignment conflict
- invalid request state

Definition of Done:
- assignment updates request state and queue
- audit logging captures action

---

### 6.4 Report Review Screen
Purpose:
- review operational or financial reports in detail

Dependencies:
- report APIs
- export mechanism
- role-based access

APIs:
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/agents
- POST /reports/export

Navigation:
- filter -> update report
- export -> download or preview
- back -> dashboard

Permissions:
- operations/admin
- leadership if configured

Validation Rules:
- valid date range
- allowed formats
- report permissions enforced

Loading States:
- table skeleton
- export spinner

Error Handling:
- empty report state
- export failure fallback
- unauthorized access

Definition of Done:
- reports align with underlying data
- export works and respects filters

---

### 6.5 Customer Management Screen
Purpose:
- review customer status, request history, and support needs

Dependencies:
- customer profile API
- complaint API
- service request API

APIs:
- GET /customers/{customerId}
- GET /customers/{customerId}/service-requests
- GET /customers/{customerId}/complaints

Navigation:
- customer -> request detail
- complaint -> Complaint Management
- back -> dashboard

Permissions:
- support/admin/operations

Validation Rules:
- customer must exist
- access based on role and scope

Loading States:
- profile skeleton
- request list loading

Error Handling:
- not found customer
- access denied
- empty state on no requests

Definition of Done:
- customer profile and request history visible
- support workflows can access the right records

---

### 6.6 Complaint Management Screen
Purpose:
- review customer complaints and update lifecycle status

Dependencies:
- complaint API
- customer request API
- support workflow

APIs:
- GET /customers/{customerId}/complaints
- POST /customers/{customerId}/complaints
- PATCH /complaints/{complaintId}/status

Navigation:
- open complaint -> detail
- update status -> modal
- close -> dashboard or queue

Permissions:
- support/admin/operations

Validation Rules:
- complaint ID valid
- allowed state transitions only
- required notes for resolution

Loading States:
- list skeleton
- action spinner

Error Handling:
- invalid status
- not found complaint
- duplicate complaint prevention

Definition of Done:
- complaint status changes are traceable
- customer visible updates follow status change

---

# Phase 5

## 7. Admin Portal

### 7.1 Admin Dashboard
Purpose:
- provide high-level platform health, system metrics, and admin actions

Dependencies:
- user management APIs
- system configuration APIs
- report APIs

APIs:
- GET /admin/dashboard
- GET /reports/summary

Navigation:
- users -> User Management
- roles -> Role Management
- pricing -> Pricing Management
- analytics -> Analytics Dashboard
- config -> System Configuration

Permissions:
- admin only

Validation Rules:
- admin session required

Loading States:
- KPI skeleton
- widget loading states

Error Handling:
- service failure
- unauthorized or expired session
- empty metrics state

Definition of Done:
- dashboard loads and maps to the real platform state
- admin actions are available from the workspace

---

### 7.2 User Management Screen
Purpose:
- view and manage system users, statuses, and role assignment

Dependencies:
- user API
- role API
- admin auth

APIs:
- GET /admin/users
- PATCH /admin/users/{userId}/status
- PATCH /admin/users/{userId}/role

Navigation:
- row select -> user detail
- create action -> add user form
- back -> dashboard

Permissions:
- admin only

Validation Rules:
- active/inactive state valid
- role permission valid
- blocked invalid assignment

Loading States:
- table skeleton
- save spinner

Error Handling:
- invalid role
- duplicate user
- permission denial

Definition of Done:
- user account lifecycle works
- role assignment is enforced
- audit logs capture actions

---

### 7.3 Role Management Screen
Purpose:
- create and assign permissions to roles

Dependencies:
- permissions API
- role model
- user management model

APIs:
- GET /admin/permissions
- POST /admin/roles
- PATCH /admin/roles/{roleId}

Navigation:
- role list -> detail view
- save -> return to role list

Permissions:
- admin only

Validation Rules:
- permission names valid
- role name unique
- role restrictions enforced

Loading States:
- list skeleton
- save spinner

Error Handling:
- duplicate role
- invalid permission
- unauthorized roles changed

Definition of Done:
- roles can be created and updated
- permissions are correctly enforced
- audit changes are recorded

---

### 7.4 Pricing Management Screen
Purpose:
- manage service or plan pricing

Dependencies:
- pricing APIs
- service catalog
- subscription plans

APIs:
- GET /services
- PATCH /services/{serviceId}/pricing
- GET /subscriptions/plans
- PATCH /subscriptions/plans/{planId}

Navigation:
- price list -> edit
- save -> confirmation modal

Permissions:
- admin only

Validation Rules:
- positive pricing only
- valid effective date
- price history retained

Loading States:
- table skeleton
- update spinner

Error Handling:
- invalid numeric value
- duplicate price entry
- API failure with retry

Definition of Done:
- pricing updates apply correctly
- effective dates and audit logs are captured

---

### 7.5 Subscription Management Screen
Purpose:
- manage customer subscriptions and lifecycle administration

Dependencies:
- subscription plan API
- customer profile API
- payment status

APIs:
- GET /subscriptions/plans
- GET /customers/{customerId}/subscriptions
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/cancel

Navigation:
- customer -> subscription details
- action -> confirm modal

Permissions:
- admin/support with role scope

Validation Rules:
- lifecycle states valid
- plan or customer must exist
- policy rules enforced

Loading States:
- subscription skeleton
- action spinner

Error Handling:
- invalid transition
- data mismatch
- access denied

Definition of Done:
- subscription management works for active and cancelled accounts
- admin actions are logged

---

### 7.6 Analytics Dashboard
Purpose:
- summarize platform metrics, trend performance, and operational data

Dependencies:
- report APIs
- analytics endpoints
- user permissions

APIs:
- GET /analytics/overview
- GET /analytics/service-trends
- GET /analytics/payment-trends
- GET /analytics/agent-performance

Navigation:
- filter date range -> update metrics
- export -> report or csv

Permissions:
- admin
- operations
- leadership if authorized

Validation Rules:
- filter range valid
- metric names valid
- permission checks on report scope

Loading States:
- chart area skeleton
- filter loading

Error Handling:
- empty dataset
- stale metrics
- unauthorized access

Definition of Done:
- metrics align with report sources
- charts and values update correctly
- admin users can review trends

---

### 7.7 System Configuration Screen
Purpose:
- configure core app and platform settings safely

Dependencies:
- config API
- admin auth
- environment policies

APIs:
- GET /system/config
- PATCH /system/config

Navigation:
- update config -> confirm save
- reject -> revert or cancel

Permissions:
- admin only

Validation Rules:
- settings must pass validation schema
- no unsafe config or invalid values
- environment-specific rules enforced

Loading States:
- settings skeleton
- save spinner

Error Handling:
- configuration invalid
- permission denied
- failure with rollback guidance

Definition of Done:
- config changes apply safely
- change is auditable
- defaults remain safe

---

# Component Strategy

## Shared UI Components
- form fields
- error banners and inline validation
- modals
- confirmation dialogs
- data tables
- charts
- cards and list items
- date/time pickers
- file upload widgets
- OTP input components
- map and location capture component
- camera capture component

## Component Principles
- consistent forms across app and portal
- centralized validation messages
- reusable loading and empty-state patterns
- strict role-aware rendering
- accessibility-first component design
- design tokens for spacing, typography, colors

---

# State Management Strategy

## Mobile App
- use Redux Toolkit or Zustand for global session, user, property, booking, service request states
- use React Query for server state and cache invalidation
- local persisted state for auth token, onboarding status, recent properties
- optimistic updates only for low-risk actions like marking notifications as read

## Portal App
- use React Query for API-driven state
- contextual local state for filters, modals, and tables
- normalized data for list views and detail pages
- central store for auth, permissions, and role metadata

## Shared Rules
- separate server state from UI state
- avoid storing raw PII in client state beyond requirement
- use cache invalidation on mutation success
- keep form state local to edit screens

---

# Offline Strategy

## Mobile
- store essential data locally:
  - recent properties
  - service request state
  - last synced notifications
  - cached service catalog
- queue actions for:
  - note creation
  - photo evidence staging
  - form submission retries
- show offline banner if network is unavailable
- support retry for GET/POST operations with local queue

## Portal
- offline support is optional for admin/ops views
- cache recent report data and service queue snapshots
- queue user actions with explicit “sync pending” state

---

# Caching Strategy

- cache stable data:
  - service catalog
  - pricing plans
  - customer profile
  - frequently used reports
- use TTL with invalidation on mutation
- do not cache sensitive PII beyond session scope
- invalidate cache after update actions:
  - property save
  - service request status change
  - payment confirmation
  - role assignment
  - price update

---

# Performance Strategy

- lazy-load screens and route-based modules
- paginate request and queue lists
- use memoized selectors and list virtualization where needed
- compress and optimize images before upload
- use native map and camera components for mobile efficiency
- prefetch data for next likely screen
- ensure report and analytics requests use query optimization and filters

---

# Security Requirements

- store auth tokens in secure storage
- use role-based route guards
- enforce permission checks before rendering sensitive data
- never render raw payment or token values to the UI
- secure file upload permissions
- sanitize and validate all API payloads
- use cross-site protections and secure headers in web apps
- ensure PII access is auditable and restricted
- implement session timeout and re-auth flow

---

# Accessibility Requirements

- minimum WCAG AA compliance
- keyboard navigation for all portal controls
- screen-reader labels for form inputs
- visible focus indicators
- color contrast compliance
- support screen-reader announcements for toast messages and errors
- OTP fields should be accessible and properly labeled
- camera and file upload flows must have alternative user guidance

---

# Testing Requirements

## Unit Tests
- form validation logic
- role guard logic
- permission rendering logic
- service state reduction logic
- pricing calculation logic
- route decision logic
- analytics transforms

## Integration Tests
- auth and OTP flow
- property creation and detail retrieval
- booking and payment flow
- service request lifecycle
- assignment and queue behavior
- evidence upload and retrieval
- report export and dashboard data mapping

## UI Tests
- login and registration flows
- customer property creation
- service booking and tracking
- agent assignment workflow
- admin role and pricing management
- portal dashboard filters and actions

## E2E Tests
- customer onboarding to booking
- agent assignment to completion
- operations queue to assignment
- admin user and role management
- payment and invoice flow
- complaint handling from submission to resolution

---

# Implementation Timeline

## Phase 1: Foundation and Customer Core
- auth flows
- customer onboarding
- property management
- dashboard
- initial customer UI shell

Duration: 2 weeks

## Phase 2: Service and Billing Experience
- service catalog
- booking flow
- tracking
- payments
- subscriptions
- invoices

Duration: 2 weeks

## Phase 3: Agent Execution
- task list
- task detail
- GPS and evidence
- visit completion
- field workflow validation

Duration: 2 weeks

## Phase 4: Operations and Support
- queue and assignment
- report review
- complaints
- customer support views

Duration: 2 weeks

## Phase 5: Admin and Platform Governance
- user and role management
- pricing management
- analytics
- system config
- audit log review

Duration: 2 weeks

---

# Critical Path Analysis

Critical frontend path:
1. Authentication and session flow
2. Customer dashboard and property management
3. Service catalog and booking flow
4. Payment and subscription flows
5. Agent task workflow
6. GPS and evidence capture
7. Operations queue and assignment
8. Reporting and admin governance features

Risks:
- auth/session mismatch between mobile and web
- service lifecycle state drift from backend
- GPS and camera permissions on mobile devices
- inconsistent role gating across portal apps
- report data mismatches due to filters or stale cache
- upload limits or file validation failures

Mitigations:
- align with central API contract and typed models
- use shared state and role constants
- define device permission handling strategy early
- implement cache invalidation and state sync checks
- validate report and admin access through role-aware route guards

---

# Definition of Done by Screen

A frontend screen is considered done when:
- the screen matches the approved UX specification and API contract
- all required fields and validation rules are implemented
- loading and empty states are handled
- API errors are mapped to user-friendly messages
- permissions are correctly enforced
- navigation flows are wired and tested
- the screen works across supported devices and browsers
- accessibility checks pass for interactive controls
- unit and integration tests cover critical flows
- the screen is demo-ready and ready for QA signoff

---

# Final Summary

This frontend roadmap follows the approved MVP scope and sprint sequencing. It starts with authentication and customer-facing onboarding, then expands into service execution and billing, followed by agent execution and operations workflow, and finally closes with admissions, reporting, and system governance controls.

Implementation should prioritize:
- customer onboarding and property management
- booking lifecycle and service tracking
- agent execution and evidence capture
- operations and report review
- admin control and platform governance

This sequence minimizes frontend risk while enabling rapid demo readiness and production-grade operational coverage.
