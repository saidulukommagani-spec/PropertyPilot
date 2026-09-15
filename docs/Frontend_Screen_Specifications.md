
# Frontend Screen Specifications

Document Type: Frontend Screen Specification  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: UX Lead / Frontend Engineering

---

## Purpose

This document defines the frontend screen specifications for the PropertyPilot MVP. It translates the approved screen catalog, user journeys, and flow definitions into developer-ready UI contracts for implementation.

The scope includes all screens required for the approved MVP customer, agent, operations, and admin journeys. Each screen specification describes:
- screen purpose
- UI components
- fields
- validation rules
- actions
- API integrations
- navigation rules
- error handling
- loading states
- permissions

---

# 1. MVP Screen Inventory

## Customer App
1. Splash / Welcome Screen
2. Login Screen
3. OTP Verification Screen
4. Customer Registration Screen
5. Customer Profile Screen
6. Property List Screen
7. Property Registration Screen
8. Property Detail Screen
9. Service Catalog Screen
10. Booking Request Screen
11. Booking Summary Screen
12. Service Request Detail Screen
13. Payment Screen
14. Notification Center Screen
15. Complaint Submission Screen
16. Subscription Plans Screen
17. Subscription Detail Screen

## Agent App
18. Agent Login Screen
19. Agent Dashboard
20. Assignment Queue Screen
21. Task Detail Screen
22. Visit Execution Screen
23. Evidence Upload Screen
24. Task Status Update Screen

## Operations / Admin
25. Operations Dashboard
26. Service Queue Screen
27. Admin User Management Screen
28. Report Dashboard Screen

---

# 2. Screen Specifications

## Screen 1: Splash / Welcome Screen

### Screen Purpose
Entry screen shown to unauthenticated users and first-time visitors. Provides access to login and onboarding.

### UI Components
- app logo
- headline
- short description
- primary CTA: Login
- secondary CTA: Register
- support / help link

### Fields
- none

### Validations
- none

### Actions
- tap Login
- tap Register
- tap Help

### API Integrations
- none
- optional app configuration fetch

### Navigation Rules
- Login -> Login Screen
- Register -> Customer Registration Screen
- if user already authenticated -> redirect to Property List or Dashboard

### Error Handling
- if app config fails, show retry
- if network is unavailable, show offline state

### Loading States
- skeleton or splash animation only

### Permissions
- public
- accessible to all unauthenticated users

---

## Screen 2: Login Screen

### Screen Purpose
Allow existing customers to log into the app.

### UI Components
- mobile number field
- submit button
- country code selector
- resend OTP link
- help text
- error banner

### Fields
- mobileNumber
- countryCode

### Validations
- required mobile number
- valid country code
- supported mobile format only
- max retries enforcement

### Actions
- send OTP
- go to registration
- open help

### API Integrations
- POST /auth/customers/otp/send

### Navigation Rules
- success -> OTP Verification Screen
- if user not found -> proceed to registration or show create account CTA
- if error -> stay on current screen and display error

### Error Handling
- invalid format: inline validation
- rate limit: blocked message + retry countdown
- network failure: retry banner

### Loading States
- button shows “Sending OTP…”
- disable inputs during request

### Permissions
- public

---

## Screen 3: OTP Verification Screen

### Screen Purpose
Verify OTP sent to the customer’s registered mobile number.

### UI Components
- OTP input fields (6-digit)
- resend OTP link
- verify button
- timer countdown
- support text

### Fields
- otpCode
- mobileNumber

### Validations
- 6-digit OTP required
- OTP expiry check
- max attempts enforcement
- retry wait window

### Actions
- verify OTP
- resend OTP
- change mobile number

### API Integrations
- POST /auth/customers/otp/verify
- POST /auth/customers/otp/send

### Navigation Rules
- success -> redirect to Property List or Customer Profile if onboarding incomplete
- failure -> remain on screen with error and retry count
- timeout -> show resend option

### Error Handling
- invalid OTP -> inline error and attempts counter
- expired OTP -> instruct resend
- network issue -> retry option

### Loading States
- verify button spinner
- disable OTP fields during verification

### Permissions
- public, but only valid OTP user flow

---

## Screen 4: Customer Registration Screen

### Screen Purpose
Register a new customer account.

### UI Components
- first name
- last name
- mobile number
- email address
- passwordless / OTP registration flow
- submit button
- consent checkbox
- terms and conditions link

### Fields
- firstName
- lastName
- mobileNumber
- email
- consentToTerms
- consentToNotifications

### Validations
- required name fields
- valid mobile number format
- valid email format
- consent required
- uniqueness check for mobile/email before submission
- max length restrictions

### Actions
- submit registration
- cancel
- read terms

### API Integrations
- POST /auth/customers/register
- POST /auth/customers/otp/send

### Navigation Rules
- success -> OTP Verification Screen or direct login flow
- cancel -> Login Screen
- duplicate account -> show existing account state

### Error Handling
- duplicate mobile/email -> inline validation and support CTA
- server failure -> retry dialog
- validation error -> highlight fields

### Loading States
- button spinner
- disable all inputs on submit

### Permissions
- public

---

## Screen 5: Customer Profile Screen

### Screen Purpose
View, edit, and verify customer details.

### UI Components
- profile avatar or placeholder
- full name
- mobile number
- email
- status badges
- edit button
- save button
- notification preferences
- account status card

### Fields
- firstName
- lastName
- email
- phone
- preferences
- profileStatus

### Validations
- valid email
- check duplicate email if changed
- status settings allowed only for valid values

### Actions
- edit profile
- save changes
- manage notification preferences
- request privacy action if supported

### API Integrations
- GET /customers/{customerId}
- PUT /customers/{customerId}
- PATCH /customers/{customerId}/preferences
- GET /customers/{customerId}/notifications

### Navigation Rules
- back -> previous screen
- save -> return to profile view
- notifications -> Notification Center

### Error Handling
- validation message for invalid input
- server failure with retry
- session timeout -> redirect to login

### Loading States
- profile skeleton while loading
- save spinner

### Permissions
- customer self-access
- admin/support read access with role restrictions

---

## Screen 6: Property List Screen

### Screen Purpose
Display all customer properties and allow management actions.

### UI Components
- search bar
- add property button
- filter chips
- property cards
- status badges
- empty state illustration
- quick actions: view, edit, delete

### Fields
- propertyId
- propertyName
- address
- status
- ownershipVerificationStatus
- lastUpdated

### Validations
- none at view level
- data must be loaded for each card

### Actions
- add property
- filter list
- open detail
- edit
- delete (with confirmation)

### API Integrations
- GET /customers/{customerId}/properties
- GET /properties/search
- DELETE /properties/{propertyId} (if supported)
- PATCH /properties/{propertyId}/status

### Navigation Rules
- add property -> Property Registration Screen
- card tap -> Property Detail Screen
- back -> Home or previous dashboard

### Error Handling
- empty state if no properties
- fetch failure with retry CTA
- permission denied message if user lacks access

### Loading States
- list skeleton
- spinner on refresh

### Permissions
- customer self-access
- operations/admin access to assigned properties

---

## Screen 7: Property Registration Screen

### Screen Purpose
Capture a new property record and initial metadata.

### UI Components
- property name
- full address
- location fields
- property type
- ownership information
- GPS button
- upload document button
- save button
- cancel button

### Fields
- propertyName
- addressLine1
- addressLine2
- city
- state
- postalCode
- country
- propertyType
- ownerName
- ownerId
- gpsCoordinates
- ownershipProofDocument

### Validations
- required property fields
- valid address format
- valid ownership metadata
- document required if ownership verification enabled
- duplicate check by address + owner
- GPS validation required for some property types

### Actions
- save property
- capture GPS
- upload ownership proof
- cancel

### API Integrations
- POST /properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership
- upload to object storage

### Navigation Rules
- save -> Property Detail Screen
- cancel -> Property List Screen
- GPS verify -> location capture flow

### Error Handling
- validation errors on required fields
- duplicate record error
- upload failure with retry
- GPS check failure with user-friendly instruction

### Loading States
- save spinner
- upload progress bar
- loading on GPS verification

### Permissions
- customer self-write
- supported operations/admin for assisted onboarding

---

## Screen 8: Property Detail Screen

### Screen Purpose
Display all key property details and verification status.

### UI Components
- property summary card
- verification badges
- ownership status
- address and GPS section
- document list
- actions: edit, request verification, request support
- timeline of property changes

### Fields
- propertyId
- propertyName
- address
- gpsStatus
- ownershipStatus
- createdAt
- updatedAt
- documents

### Validations
- none on display
- status transitions governed by backend

### Actions
- edit property
- verify ownership
- verify GPS
- request support
- view documents

### API Integrations
- GET /properties/{propertyId}
- PATCH /properties/{propertyId}/status
- GET /properties/{propertyId}/verify
- GET /properties/{propertyId}/documents

### Navigation Rules
- edit -> Property Registration Screen
- back -> Property List
- ownership verify -> verification flow or modal
- support -> complaint flow

### Error Handling
- fetch failure with retry
- if verification not available, show “pending” state
- permission denied state

### Loading States
- property skeleton
- document gallery loader

### Permissions
- owner access
- assigned operations/admin read access

---

## Screen 9: Service Catalog Screen

### Screen Purpose
Allow customers to browse available services and select a service for booking.

### UI Components
- category filters
- service cards
- pricing
- duration
- service description
- availability indicator
- CTA: Book Now

### Fields
- serviceId
- serviceName
- category
- price
- duration
- description
- availabilityStatus

### Validations
- service is active
- price and category are loaded
- location and property required before booking

### Actions
- choose category
- select service
- proceed to booking
- search by keyword

### API Integrations
- GET /services
- GET /services/{serviceId}

### Navigation Rules
- book now -> Booking Request Screen
- back -> previous screen
- category filter changes list view

### Error Handling
- no services available state
- API failure with retry
- unavailable service message

### Loading States
- skeleton cards
- spinner on filter change

### Permissions
- customer and operations/admin read
- service management may be admin-only

---

## Screen 10: Booking Request Screen

### Screen Purpose
Capture the service request and planned booking details.

### UI Components
- property selector
- service selection summary
- date/time selector
- notes field
- GPS verification trigger
- address or property context
- checkout or continue button

### Fields
- customerId
- propertyId
- serviceId
- preferredDate
- preferredTime
- notes
- gpsRequired
- locationContext

### Validations
- required property selected
- selected service must be valid and active
- date/time must be future
- notes max length
- GPS verification required if service policy demands it

### Actions
- continue to summary
- verify GPS
- save draft
- cancel

### API Integrations
- POST /service-requests
- POST /properties/{propertyId}/verify/gps
- GET /properties/{customerId}/properties
- GET /services/{serviceId}

### Navigation Rules
- continue -> Booking Summary Screen
- save draft -> view pending booking
- back -> Service Catalog Screen

### Error Handling
- invalid time/date -> inline validation
- duplicate or incomplete request -> show message
- GPS failure -> show retry or manual entry guidance

### Loading States
- form spinner
- GPS verification spinner
- date/time loading state

### Permissions
- customer self-create
- admin/support create assistance if needed

---

## Screen 11: Booking Summary Screen

### Screen Purpose
Provide a final booking preview before confirmation and payment.

### UI Components
- service summary
- property summary
- appointment summary
- charges / payment summary
- confirm button
- cancel button
- terms or consent note

### Fields
- bookingId
- serviceName
- propertyAddress
- appointmentDate
- totalAmount
- currency
- status

### Validations
- booking detail must be complete
- amount must be > 0 if payment is required
- status transitions must be valid

### Actions
- confirm booking
- edit booking
- proceed to payment
- cancel

### API Integrations
- POST /service-requests
- POST /payments/initiate
- GET /service-requests/{requestId}

### Navigation Rules
- confirm -> Payment Screen
- edit -> Booking Request Screen
- cancel -> Service Catalog or dashboard

### Error Handling
- payment initiation failure -> retry message
- validation failure -> return to booking form
- server error -> show user support action

### Loading States
- confirm spinner
- payment initialization spinner

### Permissions
- customer self
- operations/admin support if booking was created by assistance flow

---

## Screen 12: Service Request Detail Screen

### Screen Purpose
Show progress, history, and actions for an active service request.

### UI Components
- request status card
- timeline
- service details
- agent assignment section
- payment status
- evidence section
- actions: update, complain, cancel

### Fields
- requestId
- status
- createdAt
- updatedAt
- assignmentStatus
- paymentStatus
- serviceType
- notes
- evidenceCount

### Validations
- status transitions allowed only per backend rules

### Actions
- view evidence
- contact support
- cancel request
- reopen / reschedule if permitted

### API Integrations
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/history

### Navigation Rules
- back -> property or dashboard
- evidence -> evidence viewer or upload flow
- payment -> payment detail
- support -> complaint submission

### Error Handling
- fetch failure -> retry
- no timeline entries -> empty state
- permission denied -> support message

### Loading States
- skeleton on first load
- refresh spinner

### Permissions
- customer: own request access
- agent: assigned tasks only
- operations/admin: broader access

---

## Screen 13: Payment Screen

### Screen Purpose
Collect payment for booking or subscription and confirm payment state.

### UI Components
- order summary
- payment method selector
- card details form or wallet flow
- confirm payment button
- invoice / receipt preview
- secure payment notice

### Fields
- paymentMethod
- cardNumberMasked
- expiryDate
- CVV
- billingAddress
- amount
- currency
- plan or booking reference

### Validations
- required payment fields
- valid card/payment details
- required amount > 0
- secure provider rules enforced
- duplicate initiation protection

### Actions
- pay
- retry
- cancel
- view receipt

### API Integrations
- POST /payments/initiate
- POST /payments/{paymentId}/confirm
- GET /invoices/{invoiceId}
- GET /payments/{paymentId}

### Navigation Rules
- success -> Service Request Detail Screen
- failure -> stay with a retry CTA
- back -> Booking Summary

### Error Handling
- declined transaction message
- gateway timeout with retry
- invalid amount or booking mismatch
- duplicate payment detection

### Loading States
- payment spinner
- progress step indicator

### Permissions
- customer self-pay
- operations billing role for assisted scenarios

---

## Screen 14: Notification Center Screen

### Screen Purpose
Display customer notifications and status alerts.

### UI Components
- filter tabs: all / unread / service / billing
- notification list
- unread badge
- date/time stamps
- empty state
- mark all as read button

### Fields
- notificationId
- title
- message
- category
- createdAt
- readStatus

### Validations
- none beyond data integrity

### Actions
- open notification
- mark as read
- delete or archive if relevant
- filter

### API Integrations
- GET /customers/{customerId}/notifications
- PATCH /notifications/{notificationId}/status

### Navigation Rules
- item tap -> related screen or detail view
- back -> profile or dashboard

### Error Handling
- empty state for no notifications
- fetch errors with retry
- permission denied message

### Loading States
- list skeleton
- action spinner on mark read

### Permissions
- customer self
- operational staff for support notifications if relevant

---

## Screen 15: Complaint Submission Screen

### Screen Purpose
Allow customers to raise service or support issues.

### UI Components
- complaint type selector
- service or property selector
- complaint description
- attachment uploader
- submit button
- cancel button

### Fields
- complaintType
- relatedRequestId
- description
- attachments

### Validations
- required complaint type
- description required with minimum length
- attachments format validation
- validation of related entity access

### Actions
- submit complaint
- attach evidence
- cancel

### API Integrations
- POST /customers/{customerId}/complaints
- POST /service-requests/{requestId}/evidence

### Navigation Rules
- submit -> submitted confirmation
- back -> Service Request Detail or Profile

### Error Handling
- invalid complaint reason
- upload failures
- server errors with retry

### Loading States
- spinner on submit
- upload progress bar

### Permissions
- customer self
- support/admin retrieval and update

---

## Screen 16: Subscription Plans Screen

### Screen Purpose
Display available plans and allow customer plan selection.

### UI Components
- plan cards
- pricing
- benefits summary
- CTA buttons
- compare view
- active plan badge

### Fields
- planId
- planName
- price
- billingCycle
- benefits
- status

### Validations
- active plans only
- valid billing cycle
- plan activation rules

### Actions
- select plan
- subscribe
- view plan details

### API Integrations
- GET /subscriptions/plans
- POST /subscriptions

### Navigation Rules
- select -> Subscription Detail Screen or checkout step
- cancel -> profile or account area

### Error Handling
- no plans available state
- fetch failure with retry
- invalid plan selection

### Loading States
- plan card skeleton
- button spinner

### Permissions
- customer self and admin for assisted onboarding

---

## Screen 17: Subscription Detail Screen

### Screen Purpose
Display active subscription details and lifecycle options.

### UI Components
- plan details
- renewal date
- payment summary
- action buttons: cancel, pause, resume
- billing status

### Fields
- subscriptionId
- status
- planName
- startDate
- renewalDate
- amount
- billingCycle

### Validations
- lifecycle transitions follow policy
- active status must match plan rules

### Actions
- pause
- resume
- cancel
- change plan if supported

### API Integrations
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/pause
- POST /subscriptions/{subscriptionId}/resume
- POST /subscriptions/{subscriptionId}/cancel

### Navigation Rules
- back -> Subscription Plans
- action -> confirm modal

### Error Handling
- invalid transition -> user-friendly message
- API failure -> retry prompt
- unauthorized access -> redirect or access error

### Loading States
- data skeleton
- action spinner

### Permissions
- customer self
- admin / support for assisted lifecycle changes

---

## Screen 18: Agent Login Screen

### Screen Purpose
Allow an agent to authenticate for operational tasks.

### UI Components
- agent ID or phone field
- OTP or password path
- login button
- support text
- company branding

### Fields
- agentIdentifier
- otpCode or password

### Validations
- valid ID / OTP
- account must be active
- rate limits and lockout rules

### Actions
- login
- request OTP
- recover access

### API Integrations
- POST /auth/agents/login
- POST /auth/agents/otp/send
- POST /auth/agents/otp/verify

### Navigation Rules
- success -> Agent Dashboard
- failure -> stay on screen with retry

### Error Handling
- invalid credentials
- expired OTP
- rate limit message

### Loading States
- spinner on login
- button disabled during processing

### Permissions
- agent role only

---

## Screen 19: Agent Dashboard

### Screen Purpose
Give the agent a summary of assigned work and operational status.

### UI Components
- summary cards
- today’s assignments
- pending tasks
- completed tasks
- quick actions
- availability toggle

### Fields
- assignedCount
- pendingCount
- completedCount
- availabilityStatus

### Validations
- availability state must be valid

### Actions
- view assignments
- change availability
- open task detail
- navigate to reports if allowed

### API Integrations
- GET /agents/{agentId}/assignments
- PATCH /agents/{agentId}/availability

### Navigation Rules
- assignment item -> Task Detail Screen
- task quick actions -> status update flow

### Error Handling
- fetch error with retry
- no tasks state
- backend unavailability

### Loading States
- dashboard skeleton
- refresh spinner

### Permissions
- agent self
- operations/admin read access if required

---

## Screen 20: Assignment Queue Screen

### Screen Purpose
Operational and agent assignment management.

### UI Components
- queue filters
- list of pending jobs
- assignment actions
- map or location hints
- status badges
- agent assignment selector

### Fields
- requestId
- property
- serviceType
- dueDate
- location
- assignedAgent
- status

### Validations
- assignment eligible only for active requests
- no duplicate assignment
- agent availability check

### Actions
- assign agent
- reassign
- filter queue
- open detail

### API Integrations
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/assignments/{assignmentId}

### Navigation Rules
- item tap -> Task Detail Screen
- assign action -> modal or selection state
- back -> dashboard

### Error Handling
- queue unavailable
- assignment conflict
- no eligible agents

### Loading States
- loading queue skeleton
- assign button spinner

### Permissions
- operations/admin
- agent view of assigned tasks only

---

## Screen 21: Task Detail Screen

### Screen Purpose
Show service request details for the assigned agent and enable work progress.

### UI Components
- request details
- property and service summary
- visit schedule
- status timeline
- action buttons
- notes area

### Fields
- requestId
- propertyId
- customerName
- serviceName
- schedule
- status
- notes

### Validations
- valid task ownership
- valid status transitions

### Actions
- start visit
- update status
- add notes
- upload evidence
- complete task

### API Integrations
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence

### Navigation Rules
- start visit -> Visit Execution Screen
- upload evidence -> Evidence Upload Screen
- back -> Dashboard or Assignment Queue

### Error Handling
- unauthorized task access
- invalid transition message
- network failure with retry

### Loading States
- task skeleton
- update spinner

### Permissions
- assigned agent
- operations/admin

---

## Screen 22: Visit Execution Screen

### Screen Purpose
Support the agent while executing the visit and recording field activity.

### UI Components
- map or GPS validation
- service checklist
- status controls
- notes input
- action buttons
- evidence section

### Fields
- visitStatus
- startTime
- endTime
- gpsCoordinates
- checklistItems
- comments

### Validations
- required checklist if workflow demands completion
- GPS validation if configured
- completion status must be valid

### Actions
- start visit
- complete visit
- cancel visit
- save notes

### API Integrations
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence
- POST /properties/{propertyId}/verify/gps

### Navigation Rules
- complete -> Task Detail Screen
- cancel -> Assignment Queue
- back -> Task Detail Screen

### Error Handling
- GPS failure with retry
- missing checklist required fields
- network issue on save
- invalid workflow transitions

### Loading States
- map loading
- save spinner
- progress indicator

### Permissions
- assigned agent
- operations support only for override case

---

## Screen 23: Evidence Upload Screen

### Screen Purpose
Allow the agent to upload evidence for a visit or service action.

### UI Components
- upload button
- file list
- preview thumbnails
- notes field
- submit button
- progress bar

### Fields
- fileUpload
- fileType
- description
- relatedRequestId

### Validations
- file size restrictions
- allowed extensions
- required description if policy requires
- file ownership access

### Actions
- upload file
- remove file
- submit evidence
- cancel

### API Integrations
- POST /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/evidence
- object storage upload endpoint

### Navigation Rules
- submit -> return to Task Detail Screen
- cancel -> Visit Execution or Task Detail

### Error Handling
- upload failure with retry
- unsupported file type
- quota or storage issue
- permission denied

### Loading States
- file upload progress
- upload spinner

### Permissions
- assigned agent / support override
- operations/admin read access

---

## Screen 24: Task Status Update Screen

### Screen Purpose
Allow the agent to submit progress updates and close the work item.

### UI Components
- status drop-down
- notes field
- submit button
- reason codes
- required checklist toggle

### Fields
- status
- reasonCode
- notes
- updateTime

### Validations
- valid state transition
- required notes for completion/cancel states
- reason required for failed or cancelled task

### Actions
- update status
- save
- cancel

### API Integrations
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}

### Navigation Rules
- save -> Task Detail Screen
- cancel -> previous screen

### Error Handling
- invalid transition warning
- retry after failure
- no permission message

### Loading States
- action spinner
- save disable state

### Permissions
- assigned agent
- operations/admin for override

---

## Screen 25: Operations Dashboard

### Screen Purpose
Provide administrators and operations staff a view of total service health and workload.

### UI Components
- KPI cards
- service queues
- assignment summary
- recent incidents
- operational alerts
- report links
- recent complaints

### Fields
- metrics
- date range
- service counts
- alerts
- incidents

### Validations
- date range validation
- metric filters must be valid

### Actions
- filter date range
- view queue
- open service detail
- navigate to reports

### API Integrations
- GET /reports/summary
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/agents

### Navigation Rules
- queue item -> Service Queue Screen
- reports -> Report Dashboard
- support -> complaint view

### Error Handling
- empty data state
- failed reports or metric fetch
- role-based access denial

### Loading States
- skeleton KPI cards
- loading chart state

### Permissions
- operations, admin, support, leadership with role-based access

---

## Screen 26: Service Queue Screen

### Screen Purpose
Display all service requests in the operational pipeline for triage and assignment.

### UI Components
- filters by status/date/agent
- table or card list
- assignment controls
- bulk actions
- search field

### Fields
- requestId
- customerName
- property
- serviceType
- assignedAgent
- status
- createdAt

### Validations
- statuses valid
- agent selection valid

### Actions
- assign agent
- view detail
- reassign
- escalate

### API Integrations
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/status

### Navigation Rules
- item click -> Task Detail Screen
- bulk action -> processing modal
- back -> dashboard

### Error Handling
- empty queue
- assignment conflict if agent unavailable
- fetch failure with retry

### Loading States
- list skeleton
- action spinner

### Permissions
- operations/admin/support with proper role

---

## Screen 27: Admin User Management Screen

### Screen Purpose
Manage user roles, access, and account state for administrators.

### UI Components
- user list
- create user action
- role selector
- search and filter
- active/inactive toggle
- audit trail summary

### Fields
- userId
- fullName
- role
- status
- createdAt

### Validations
- role permissions valid
- active/inactive state allowed
- account actions restricted to authorized admins

### Actions
- create user
- update role
- deactivate/activate account
- reset credentials if policy allows

### API Integrations
- GET /admin/users
- POST /admin/users
- PATCH /admin/users/{userId}/role
- PATCH /admin/users/{userId}/status

### Navigation Rules
- create user -> form
- row select -> detail
- back -> dashboard

### Error Handling
- invalid role assignment
- update failure
- access denied warnings

### Loading States
- table skeleton
- save spinner

### Permissions
- admin only

---

## Screen 28: Report Dashboard Screen

### Screen Purpose
Display operational metrics and generated reports for management and support teams.

### UI Components
- date range picker
- KPI widgets
- chart area
- table of report data
- export button
- filter controls

### Fields
- metricName
- metricValue
- timeRange
- category
- exportFormat

### Validations
- date range valid
- allowed export format
- permission to export restricted

### Actions
- change date range
- export CSV/PDF
- view detail report

### API Integrations
- GET /reports/summary
- GET /reports/payments
- GET /reports/agents
- POST /reports/export

### Navigation Rules
- export -> download or preview
- back -> operations dashboard

### Error Handling
- data unavailable
- export failure with retry
- unauthorized access

### Loading States
- charts loading
- export spinner

### Permissions
- operations/admin/support/leadership per role policy

---

# 3. Shared UI Patterns

## Global Patterns
- all primary actions must show loading state
- all destructive actions require confirmation modal
- all error states should provide retry or recovery CTA
- all API-driven screens must use consistent skeleton loading
- all forms should show inline validation and server-side error mapping
- all states should include a “back” or “cancel” control

## Error Handling Standards
- show field-level errors inline for form inputs
- show banner messages for global errors
- use retry, refresh, or back action where appropriate
- avoid silent failure
- log all user-visible error states and API failure reasons

## Loading Standards
- use skeleton placeholders for list/detail screens
- show spinner for mutation actions
- use upload progress for file actions
- disable action buttons while request is in progress

## Accessibility Requirements
- all interactive controls must be keyboard accessible
- toast messages and errors must be readable
- contrast must satisfy accessibility standards
- OTP and form inputs must support screen readers
- permanency and state changes must be announced

---

# 4. Screen Permission Matrix

| Screen | Customer | Agent | Operations | Admin |
|---|---|---|---|---|
| Splash / Welcome | Yes | Yes | Yes | Yes |
| Login | Yes | Yes | Yes | Yes |
| OTP Verification | Yes | Yes | No | No |
| Registration | Yes | No | No | No |
| Profile | Yes | No | Partial | Yes |
| Property List | Yes | No | Partial | Yes |
| Property Registration | Yes | No | Partial | Yes |
| Property Detail | Yes | No | Partial | Yes |
| Service Catalog | Yes | No | Partial | Yes |
| Booking Request | Yes | No | Partial | Yes |
| Booking Summary | Yes | No | Partial | Yes |
| Service Request Detail | Yes | Yes | Yes | Yes |
| Payment | Yes | No | Partial | Yes |
| Notification Center | Yes | Partial | Partial | Yes |
| Complaint Submission | Yes | No | Partial | Yes |
| Subscription Plans | Yes | No | Partial | Yes |
| Subscription Detail | Yes | No | Partial | Yes |
| Agent Login | No | Yes | Yes | Yes |
| Agent Dashboard | No | Yes | Yes | Yes |
| Assignment Queue | No | Partial | Yes | Yes |
| Task Detail | No | Yes | Yes | Yes |
| Visit Execution | No | Yes | Partial | Yes |
| Evidence Upload | No | Yes | Partial | Yes |
| Task Status Update | No | Yes | Partial | Yes |
| Operations Dashboard | No | No | Yes | Yes |
| Service Queue | No | Partial | Yes | Yes |
| Admin User Management | No | No | No | Yes |
| Report Dashboard | No | Partial | Yes | Yes |

---

# 5. Implementation Notes

- All customer app screens should consume a common layout shell and session middleware.
- All operational screens should use role-aware routing and permissions guard.
- All forms should use a common validation layer.
- All API calls should maintain correlation IDs and show stable error maps.
- The OTP, booking, payment, and assignment flows are the primary risk points and need early validation.

---

## Summary

This frontend screen specification provides a developer-ready implementation contract for the PropertyPilot MVP. It aligns user journeys, screen catalog, API contracts, and field expectations into a consistent set of UI requirements for engineering, QA, UX, and product.

The screen inventory covers customer onboarding, property management, service booking, payment, assignments, support, notifications, and operation-level visibility. Implementation should prioritize the MVP screens in the defined flow order and maintain strict alignment with the approved backend API and data model.
```// filepath: c:\PropertyPilot\docs\Frontend_Screen_Specifications.md
# Frontend Screen Specifications

Document Type: Frontend Screen Specification  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: UX Lead / Frontend Engineering

---

## Purpose

This document defines the frontend screen specifications for the PropertyPilot MVP. It translates the approved screen catalog, user journeys, and flow definitions into developer-ready UI contracts for implementation.

The scope includes all screens required for the approved MVP customer, agent, operations, and admin journeys. Each screen specification describes:
- screen purpose
- UI components
- fields
- validation rules
- actions
- API integrations
- navigation rules
- error handling
- loading states
- permissions

---

# 1. MVP Screen Inventory

## Customer App
1. Splash / Welcome Screen
2. Login Screen
3. OTP Verification Screen
4. Customer Registration Screen
5. Customer Profile Screen
6. Property List Screen
7. Property Registration Screen
8. Property Detail Screen
9. Service Catalog Screen
10. Booking Request Screen
11. Booking Summary Screen
12. Service Request Detail Screen
13. Payment Screen
14. Notification Center Screen
15. Complaint Submission Screen
16. Subscription Plans Screen
17. Subscription Detail Screen

## Agent App
18. Agent Login Screen
19. Agent Dashboard
20. Assignment Queue Screen
21. Task Detail Screen
22. Visit Execution Screen
23. Evidence Upload Screen
24. Task Status Update Screen

## Operations / Admin
25. Operations Dashboard
26. Service Queue Screen
27. Admin User Management Screen
28. Report Dashboard Screen

---

# 2. Screen Specifications

## Screen 1: Splash / Welcome Screen

### Screen Purpose
Entry screen shown to unauthenticated users and first-time visitors. Provides access to login and onboarding.

### UI Components
- app logo
- headline
- short description
- primary CTA: Login
- secondary CTA: Register
- support / help link

### Fields
- none

### Validations
- none

### Actions
- tap Login
- tap Register
- tap Help

### API Integrations
- none
- optional app configuration fetch

### Navigation Rules
- Login -> Login Screen
- Register -> Customer Registration Screen
- if user already authenticated -> redirect to Property List or Dashboard

### Error Handling
- if app config fails, show retry
- if network is unavailable, show offline state

### Loading States
- skeleton or splash animation only

### Permissions
- public
- accessible to all unauthenticated users

---

## Screen 2: Login Screen

### Screen Purpose
Allow existing customers to log into the app.

### UI Components
- mobile number field
- submit button
- country code selector
- resend OTP link
- help text
- error banner

### Fields
- mobileNumber
- countryCode

### Validations
- required mobile number
- valid country code
- supported mobile format only
- max retries enforcement

### Actions
- send OTP
- go to registration
- open help

### API Integrations
- POST /auth/customers/otp/send

### Navigation Rules
- success -> OTP Verification Screen
- if user not found -> proceed to registration or show create account CTA
- if error -> stay on current screen and display error

### Error Handling
- invalid format: inline validation
- rate limit: blocked message + retry countdown
- network failure: retry banner

### Loading States
- button shows “Sending OTP…”
- disable inputs during request

### Permissions
- public

---

## Screen 3: OTP Verification Screen

### Screen Purpose
Verify OTP sent to the customer’s registered mobile number.

### UI Components
- OTP input fields (6-digit)
- resend OTP link
- verify button
- timer countdown
- support text

### Fields
- otpCode
- mobileNumber

### Validations
- 6-digit OTP required
- OTP expiry check
- max attempts enforcement
- retry wait window

### Actions
- verify OTP
- resend OTP
- change mobile number

### API Integrations
- POST /auth/customers/otp/verify
- POST /auth/customers/otp/send

### Navigation Rules
- success -> redirect to Property List or Customer Profile if onboarding incomplete
- failure -> remain on screen with error and retry count
- timeout -> show resend option

### Error Handling
- invalid OTP -> inline error and attempts counter
- expired OTP -> instruct resend
- network issue -> retry option

### Loading States
- verify button spinner
- disable OTP fields during verification

### Permissions
- public, but only valid OTP user flow

---

## Screen 4: Customer Registration Screen

### Screen Purpose
Register a new customer account.

### UI Components
- first name
- last name
- mobile number
- email address
- passwordless / OTP registration flow
- submit button
- consent checkbox
- terms and conditions link

### Fields
- firstName
- lastName
- mobileNumber
- email
- consentToTerms
- consentToNotifications

### Validations
- required name fields
- valid mobile number format
- valid email format
- consent required
- uniqueness check for mobile/email before submission
- max length restrictions

### Actions
- submit registration
- cancel
- read terms

### API Integrations
- POST /auth/customers/register
- POST /auth/customers/otp/send

### Navigation Rules
- success -> OTP Verification Screen or direct login flow
- cancel -> Login Screen
- duplicate account -> show existing account state

### Error Handling
- duplicate mobile/email -> inline validation and support CTA
- server failure -> retry dialog
- validation error -> highlight fields

### Loading States
- button spinner
- disable all inputs on submit

### Permissions
- public

---

## Screen 5: Customer Profile Screen

### Screen Purpose
View, edit, and verify customer details.

### UI Components
- profile avatar or placeholder
- full name
- mobile number
- email
- status badges
- edit button
- save button
- notification preferences
- account status card

### Fields
- firstName
- lastName
- email
- phone
- preferences
- profileStatus

### Validations
- valid email
- check duplicate email if changed
- status settings allowed only for valid values

### Actions
- edit profile
- save changes
- manage notification preferences
- request privacy action if supported

### API Integrations
- GET /customers/{customerId}
- PUT /customers/{customerId}
- PATCH /customers/{customerId}/preferences
- GET /customers/{customerId}/notifications

### Navigation Rules
- back -> previous screen
- save -> return to profile view
- notifications -> Notification Center

### Error Handling
- validation message for invalid input
- server failure with retry
- session timeout -> redirect to login

### Loading States
- profile skeleton while loading
- save spinner

### Permissions
- customer self-access
- admin/support read access with role restrictions

---

## Screen 6: Property List Screen

### Screen Purpose
Display all customer properties and allow management actions.

### UI Components
- search bar
- add property button
- filter chips
- property cards
- status badges
- empty state illustration
- quick actions: view, edit, delete

### Fields
- propertyId
- propertyName
- address
- status
- ownershipVerificationStatus
- lastUpdated

### Validations
- none at view level
- data must be loaded for each card

### Actions
- add property
- filter list
- open detail
- edit
- delete (with confirmation)

### API Integrations
- GET /customers/{customerId}/properties
- GET /properties/search
- DELETE /properties/{propertyId} (if supported)
- PATCH /properties/{propertyId}/status

### Navigation Rules
- add property -> Property Registration Screen
- card tap -> Property Detail Screen
- back -> Home or previous dashboard

### Error Handling
- empty state if no properties
- fetch failure with retry CTA
- permission denied message if user lacks access

### Loading States
- list skeleton
- spinner on refresh

### Permissions
- customer self-access
- operations/admin access to assigned properties

---

## Screen 7: Property Registration Screen

### Screen Purpose
Capture a new property record and initial metadata.

### UI Components
- property name
- full address
- location fields
- property type
- ownership information
- GPS button
- upload document button
- save button
- cancel button

### Fields
- propertyName
- addressLine1
- addressLine2
- city
- state
- postalCode
- country
- propertyType
- ownerName
- ownerId
- gpsCoordinates
- ownershipProofDocument

### Validations
- required property fields
- valid address format
- valid ownership metadata
- document required if ownership verification enabled
- duplicate check by address + owner
- GPS validation required for some property types

### Actions
- save property
- capture GPS
- upload ownership proof
- cancel

### API Integrations
- POST /properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership
- upload to object storage

### Navigation Rules
- save -> Property Detail Screen
- cancel -> Property List Screen
- GPS verify -> location capture flow

### Error Handling
- validation errors on required fields
- duplicate record error
- upload failure with retry
- GPS check failure with user-friendly instruction

### Loading States
- save spinner
- upload progress bar
- loading on GPS verification

### Permissions
- customer self-write
- supported operations/admin for assisted onboarding

---

## Screen 8: Property Detail Screen

### Screen Purpose
Display all key property details and verification status.

### UI Components
- property summary card
- verification badges
- ownership status
- address and GPS section
- document list
- actions: edit, request verification, request support
- timeline of property changes

### Fields
- propertyId
- propertyName
- address
- gpsStatus
- ownershipStatus
- createdAt
- updatedAt
- documents

### Validations
- none on display
- status transitions governed by backend

### Actions
- edit property
- verify ownership
- verify GPS
- request support
- view documents

### API Integrations
- GET /properties/{propertyId}
- PATCH /properties/{propertyId}/status
- GET /properties/{propertyId}/verify
- GET /properties/{propertyId}/documents

### Navigation Rules
- edit -> Property Registration Screen
- back -> Property List
- ownership verify -> verification flow or modal
- support -> complaint flow

### Error Handling
- fetch failure with retry
- if verification not available, show “pending” state
- permission denied state

### Loading States
- property skeleton
- document gallery loader

### Permissions
- owner access
- assigned operations/admin read access

---

## Screen 9: Service Catalog Screen

### Screen Purpose
Allow customers to browse available services and select a service for booking.

### UI Components
- category filters
- service cards
- pricing
- duration
- service description
- availability indicator
- CTA: Book Now

### Fields
- serviceId
- serviceName
- category
- price
- duration
- description
- availabilityStatus

### Validations
- service is active
- price and category are loaded
- location and property required before booking

### Actions
- choose category
- select service
- proceed to booking
- search by keyword

### API Integrations
- GET /services
- GET /services/{serviceId}

### Navigation Rules
- book now -> Booking Request Screen
- back -> previous screen
- category filter changes list view

### Error Handling
- no services available state
- API failure with retry
- unavailable service message

### Loading States
- skeleton cards
- spinner on filter change

### Permissions
- customer and operations/admin read
- service management may be admin-only

---

## Screen 10: Booking Request Screen

### Screen Purpose
Capture the service request and planned booking details.

### UI Components
- property selector
- service selection summary
- date/time selector
- notes field
- GPS verification trigger
- address or property context
- checkout or continue button

### Fields
- customerId
- propertyId
- serviceId
- preferredDate
- preferredTime
- notes
- gpsRequired
- locationContext

### Validations
- required property selected
- selected service must be valid and active
- date/time must be future
- notes max length
- GPS verification required if service policy demands it

### Actions
- continue to summary
- verify GPS
- save draft
- cancel

### API Integrations
- POST /service-requests
- POST /properties/{propertyId}/verify/gps
- GET /properties/{customerId}/properties
- GET /services/{serviceId}

### Navigation Rules
- continue -> Booking Summary Screen
- save draft -> view pending booking
- back -> Service Catalog Screen

### Error Handling
- invalid time/date -> inline validation
- duplicate or incomplete request -> show message
- GPS failure -> show retry or manual entry guidance

### Loading States
- form spinner
- GPS verification spinner
- date/time loading state

### Permissions
- customer self-create
- admin/support create assistance if needed

---

## Screen 11: Booking Summary Screen

### Screen Purpose
Provide a final booking preview before confirmation and payment.

### UI Components
- service summary
- property summary
- appointment summary
- charges / payment summary
- confirm button
- cancel button
- terms or consent note

### Fields
- bookingId
- serviceName
- propertyAddress
- appointmentDate
- totalAmount
- currency
- status

### Validations
- booking detail must be complete
- amount must be > 0 if payment is required
- status transitions must be valid

### Actions
- confirm booking
- edit booking
- proceed to payment
- cancel

### API Integrations
- POST /service-requests
- POST /payments/initiate
- GET /service-requests/{requestId}

### Navigation Rules
- confirm -> Payment Screen
- edit -> Booking Request Screen
- cancel -> Service Catalog or dashboard

### Error Handling
- payment initiation failure -> retry message
- validation failure -> return to booking form
- server error -> show user support action

### Loading States
- confirm spinner
- payment initialization spinner

### Permissions
- customer self
- operations/admin support if booking was created by assistance flow

---

## Screen 12: Service Request Detail Screen

### Screen Purpose
Show progress, history, and actions for an active service request.

### UI Components
- request status card
- timeline
- service details
- agent assignment section
- payment status
- evidence section
- actions: update, complain, cancel

### Fields
- requestId
- status
- createdAt
- updatedAt
- assignmentStatus
- paymentStatus
- serviceType
- notes
- evidenceCount

### Validations
- status transitions allowed only per backend rules

### Actions
- view evidence
- contact support
- cancel request
- reopen / reschedule if permitted

### API Integrations
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/history

### Navigation Rules
- back -> property or dashboard
- evidence -> evidence viewer or upload flow
- payment -> payment detail
- support -> complaint submission

### Error Handling
- fetch failure -> retry
- no timeline entries -> empty state
- permission denied -> support message

### Loading States
- skeleton on first load
- refresh spinner

### Permissions
- customer: own request access
- agent: assigned tasks only
- operations/admin: broader access

---

## Screen 13: Payment Screen

### Screen Purpose
Collect payment for booking or subscription and confirm payment state.

### UI Components
- order summary
- payment method selector
- card details form or wallet flow
- confirm payment button
- invoice / receipt preview
- secure payment notice

### Fields
- paymentMethod
- cardNumberMasked
- expiryDate
- CVV
- billingAddress
- amount
- currency
- plan or booking reference

### Validations
- required payment fields
- valid card/payment details
- required amount > 0
- secure provider rules enforced
- duplicate initiation protection

### Actions
- pay
- retry
- cancel
- view receipt

### API Integrations
- POST /payments/initiate
- POST /payments/{paymentId}/confirm
- GET /invoices/{invoiceId}
- GET /payments/{paymentId}

### Navigation Rules
- success -> Service Request Detail Screen
- failure -> stay with a retry CTA
- back -> Booking Summary

### Error Handling
- declined transaction message
- gateway timeout with retry
- invalid amount or booking mismatch
- duplicate payment detection

### Loading States
- payment spinner
- progress step indicator

### Permissions
- customer self-pay
- operations billing role for assisted scenarios

---

## Screen 14: Notification Center Screen

### Screen Purpose
Display customer notifications and status alerts.

### UI Components
- filter tabs: all / unread / service / billing
- notification list
- unread badge
- date/time stamps
- empty state
- mark all as read button

### Fields
- notificationId
- title
- message
- category
- createdAt
- readStatus

### Validations
- none beyond data integrity

### Actions
- open notification
- mark as read
- delete or archive if relevant
- filter

### API Integrations
- GET /customers/{customerId}/notifications
- PATCH /notifications/{notificationId}/status

### Navigation Rules
- item tap -> related screen or detail view
- back -> profile or dashboard

### Error Handling
- empty state for no notifications
- fetch errors with retry
- permission denied message

### Loading States
- list skeleton
- action spinner on mark read

### Permissions
- customer self
- operational staff for support notifications if relevant

---

## Screen 15: Complaint Submission Screen

### Screen Purpose
Allow customers to raise service or support issues.

### UI Components
- complaint type selector
- service or property selector
- complaint description
- attachment uploader
- submit button
- cancel button

### Fields
- complaintType
- relatedRequestId
- description
- attachments

### Validations
- required complaint type
- description required with minimum length
- attachments format validation
- validation of related entity access

### Actions
- submit complaint
- attach evidence
- cancel

### API Integrations
- POST /customers/{customerId}/complaints
- POST /service-requests/{requestId}/evidence

### Navigation Rules
- submit -> submitted confirmation
- back -> Service Request Detail or Profile

### Error Handling
- invalid complaint reason
- upload failures
- server errors with retry

### Loading States
- spinner on submit
- upload progress bar

### Permissions
- customer self
- support/admin retrieval and update

---

## Screen 16: Subscription Plans Screen

### Screen Purpose
Display available plans and allow customer plan selection.

### UI Components
- plan cards
- pricing
- benefits summary
- CTA buttons
- compare view
- active plan badge

### Fields
- planId
- planName
- price
- billingCycle
- benefits
- status

### Validations
- active plans only
- valid billing cycle
- plan activation rules

### Actions
- select plan
- subscribe
- view plan details

### API Integrations
- GET /subscriptions/plans
- POST /subscriptions

### Navigation Rules
- select -> Subscription Detail Screen or checkout step
- cancel -> profile or account area

### Error Handling
- no plans available state
- fetch failure with retry
- invalid plan selection

### Loading States
- plan card skeleton
- button spinner

### Permissions
- customer self and admin for assisted onboarding

---

## Screen 17: Subscription Detail Screen

### Screen Purpose
Display active subscription details and lifecycle options.

### UI Components
- plan details
- renewal date
- payment summary
- action buttons: cancel, pause, resume
- billing status

### Fields
- subscriptionId
- status
- planName
- startDate
- renewalDate
- amount
- billingCycle

### Validations
- lifecycle transitions follow policy
- active status must match plan rules

### Actions
- pause
- resume
- cancel
- change plan if supported

### API Integrations
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/pause
- POST /subscriptions/{subscriptionId}/resume
- POST /subscriptions/{subscriptionId}/cancel

### Navigation Rules
- back -> Subscription Plans
- action -> confirm modal

### Error Handling
- invalid transition -> user-friendly message
- API failure -> retry prompt
- unauthorized access -> redirect or access error

### Loading States
- data skeleton
- action spinner

### Permissions
- customer self
- admin / support for assisted lifecycle changes

---

## Screen 18: Agent Login Screen

### Screen Purpose
Allow an agent to authenticate for operational tasks.

### UI Components
- agent ID or phone field
- OTP or password path
- login button
- support text
- company branding

### Fields
- agentIdentifier
- otpCode or password

### Validations
- valid ID / OTP
- account must be active
- rate limits and lockout rules

### Actions
- login
- request OTP
- recover access

### API Integrations
- POST /auth/agents/login
- POST /auth/agents/otp/send
- POST /auth/agents/otp/verify

### Navigation Rules
- success -> Agent Dashboard
- failure -> stay on screen with retry

### Error Handling
- invalid credentials
- expired OTP
- rate limit message

### Loading States
- spinner on login
- button disabled during processing

### Permissions
- agent role only

---

## Screen 19: Agent Dashboard

### Screen Purpose
Give the agent a summary of assigned work and operational status.

### UI Components
- summary cards
- today’s assignments
- pending tasks
- completed tasks
- quick actions
- availability toggle

### Fields
- assignedCount
- pendingCount
- completedCount
- availabilityStatus

### Validations
- availability state must be valid

### Actions
- view assignments
- change availability
- open task detail
- navigate to reports if allowed

### API Integrations
- GET /agents/{agentId}/assignments
- PATCH /agents/{agentId}/availability

### Navigation Rules
- assignment item -> Task Detail Screen
- task quick actions -> status update flow

### Error Handling
- fetch error with retry
- no tasks state
- backend unavailability

### Loading States
- dashboard skeleton
- refresh spinner

### Permissions
- agent self
- operations/admin read access if required

---

## Screen 20: Assignment Queue Screen

### Screen Purpose
Operational and agent assignment management.

### UI Components
- queue filters
- list of pending jobs
- assignment actions
- map or location hints
- status badges
- agent assignment selector

### Fields
- requestId
- property
- serviceType
- dueDate
- location
- assignedAgent
- status

### Validations
- assignment eligible only for active requests
- no duplicate assignment
- agent availability check

### Actions
- assign agent
- reassign
- filter queue
- open detail

### API Integrations
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/assignments/{assignmentId}

### Navigation Rules
- item tap -> Task Detail Screen
- assign action -> modal or selection state
- back -> dashboard

### Error Handling
- queue unavailable
- assignment conflict
- no eligible agents

### Loading States
- loading queue skeleton
- assign button spinner

### Permissions
- operations/admin
- agent view of assigned tasks only

---

## Screen 21: Task Detail Screen

### Screen Purpose
Show service request details for the assigned agent and enable work progress.

### UI Components
- request details
- property and service summary
- visit schedule
- status timeline
- action buttons
- notes area

### Fields
- requestId
- propertyId
- customerName
- serviceName
- schedule
- status
- notes

### Validations
- valid task ownership
- valid status transitions

### Actions
- start visit
- update status
- add notes
- upload evidence
- complete task

### API Integrations
- GET /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence

### Navigation Rules
- start visit -> Visit Execution Screen
- upload evidence -> Evidence Upload Screen
- back -> Dashboard or Assignment Queue

### Error Handling
- unauthorized task access
- invalid transition message
- network failure with retry

### Loading States
- task skeleton
- update spinner

### Permissions
- assigned agent
- operations/admin

---

## Screen 22: Visit Execution Screen

### Screen Purpose
Support the agent while executing the visit and recording field activity.

### UI Components
- map or GPS validation
- service checklist
- status controls
- notes input
- action buttons
- evidence section

### Fields
- visitStatus
- startTime
- endTime
- gpsCoordinates
- checklistItems
- comments

### Validations
- required checklist if workflow demands completion
- GPS validation if configured
- completion status must be valid

### Actions
- start visit
- complete visit
- cancel visit
- save notes

### API Integrations
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence
- POST /properties/{propertyId}/verify/gps

### Navigation Rules
- complete -> Task Detail Screen
- cancel -> Assignment Queue
- back -> Task Detail Screen

### Error Handling
- GPS failure with retry
- missing checklist required fields
- network issue on save
- invalid workflow transitions

### Loading States
- map loading
- save spinner
- progress indicator

### Permissions
- assigned agent
- operations support only for override case

---

## Screen 23: Evidence Upload Screen

### Screen Purpose
Allow the agent to upload evidence for a visit or service action.

### UI Components
- upload button
- file list
- preview thumbnails
- notes field
- submit button
- progress bar

### Fields
- fileUpload
- fileType
- description
- relatedRequestId

### Validations
- file size restrictions
- allowed extensions
- required description if policy requires
- file ownership access

### Actions
- upload file
- remove file
- submit evidence
- cancel

### API Integrations
- POST /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/evidence
- object storage upload endpoint

### Navigation Rules
- submit -> return to Task Detail Screen
- cancel -> Visit Execution or Task Detail

### Error Handling
- upload failure with retry
- unsupported file type
- quota or storage issue
- permission denied

### Loading States
- file upload progress
- upload spinner

### Permissions
- assigned agent / support override
- operations/admin read access

---

## Screen 24: Task Status Update Screen

### Screen Purpose
Allow the agent to submit progress updates and close the work item.

### UI Components
- status drop-down
- notes field
- submit button
- reason codes
- required checklist toggle

### Fields
- status
- reasonCode
- notes
- updateTime

### Validations
- valid state transition
- required notes for completion/cancel states
- reason required for failed or cancelled task

### Actions
- update status
- save
- cancel

### API Integrations
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}

### Navigation Rules
- save -> Task Detail Screen
- cancel -> previous screen

### Error Handling
- invalid transition warning
- retry after failure
- no permission message

### Loading States
- action spinner
- save disable state

### Permissions
- assigned agent
- operations/admin for override

---

## Screen 25: Operations Dashboard

### Screen Purpose
Provide administrators and operations staff a view of total service health and workload.

### UI Components
- KPI cards
- service queues
- assignment summary
- recent incidents
- operational alerts
- report links
- recent complaints

### Fields
- metrics
- date range
- service counts
- alerts
- incidents

### Validations
- date range validation
- metric filters must be valid

### Actions
- filter date range
- view queue
- open service detail
- navigate to reports

### API Integrations
- GET /reports/summary
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/agents

### Navigation Rules
- queue item -> Service Queue Screen
- reports -> Report Dashboard
- support -> complaint view

### Error Handling
- empty data state
- failed reports or metric fetch
- role-based access denial

### Loading States
- skeleton KPI cards
- loading chart state

### Permissions
- operations, admin, support, leadership with role-based access

---

## Screen 26: Service Queue Screen

### Screen Purpose
Display all service requests in the operational pipeline for triage and assignment.

### UI Components
- filters by status/date/agent
- table or card list
- assignment controls
- bulk actions
- search field

### Fields
- requestId
- customerName
- property
- serviceType
- assignedAgent
- status
- createdAt

### Validations
- statuses valid
- agent selection valid

### Actions
- assign agent
- view detail
- reassign
- escalate

### API Integrations
- GET /service-requests
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/status

### Navigation Rules
- item click -> Task Detail Screen
- bulk action -> processing modal
- back -> dashboard

### Error Handling
- empty queue
- assignment conflict if agent unavailable
- fetch failure with retry

### Loading States
- list skeleton
- action spinner

### Permissions
- operations/admin/support with proper role

---

## Screen 27: Admin User Management Screen

### Screen Purpose
Manage user roles, access, and account state for administrators.

### UI Components
- user list
- create user action
- role selector
- search and filter
- active/inactive toggle
- audit trail summary

### Fields
- userId
- fullName
- role
- status
- createdAt

### Validations
- role permissions valid
- active/inactive state allowed
- account actions restricted to authorized admins

### Actions
- create user
- update role
- deactivate/activate account
- reset credentials if policy allows

### API Integrations
- GET /admin/users
- POST /admin/users
- PATCH /admin/users/{userId}/role
- PATCH /admin/users/{userId}/status

### Navigation Rules
- create user -> form
- row select -> detail
- back -> dashboard

### Error Handling
- invalid role assignment
- update failure
- access denied warnings

### Loading States
- table skeleton
- save spinner

### Permissions
- admin only

---

## Screen 28: Report Dashboard Screen

### Screen Purpose
Display operational metrics and generated reports for management and support teams.

### UI Components
- date range picker
- KPI widgets
- chart area
- table of report data
- export button
- filter controls

### Fields
- metricName
- metricValue
- timeRange
- category
- exportFormat

### Validations
- date range valid
- allowed export format
- permission to export restricted

### Actions
- change date range
- export CSV/PDF
- view detail report

### API Integrations
- GET /reports/summary
- GET /reports/payments
- GET /reports/agents
- POST /reports/export

### Navigation Rules
- export -> download or preview
- back -> operations dashboard

### Error Handling
- data unavailable
- export failure with retry
- unauthorized access

### Loading States
- charts loading
- export spinner

### Permissions
- operations/admin/support/leadership per role policy

---

# 3. Shared UI Patterns

## Global Patterns
- all primary actions must show loading state
- all destructive actions require confirmation modal
- all error states should provide retry or recovery CTA
- all API-driven screens must use consistent skeleton loading
- all forms should show inline validation and server-side error mapping
- all states should include a “back” or “cancel” control

## Error Handling Standards
- show field-level errors inline for form inputs
- show banner messages for global errors
- use retry, refresh, or back action where appropriate
- avoid silent failure
- log all user-visible error states and API failure reasons

## Loading Standards
- use skeleton placeholders for list/detail screens
- show spinner for mutation actions
- use upload progress for file actions
- disable action buttons while request is in progress

## Accessibility Requirements
- all interactive controls must be keyboard accessible
- toast messages and errors must be readable
- contrast must satisfy accessibility standards
- OTP and form inputs must support screen readers
- permanency and state changes must be announced

---

# 4. Screen Permission Matrix

| Screen | Customer | Agent | Operations | Admin |
|---|---|---|---|---|
| Splash / Welcome | Yes | Yes | Yes | Yes |
| Login | Yes | Yes | Yes | Yes |
| OTP Verification | Yes | Yes | No | No |
| Registration | Yes | No | No | No |
| Profile | Yes | No | Partial | Yes |
| Property List | Yes | No | Partial | Yes |
| Property Registration | Yes | No | Partial | Yes |
| Property Detail | Yes | No | Partial | Yes |
| Service Catalog | Yes | No | Partial | Yes |
| Booking Request | Yes | No | Partial | Yes |
| Booking Summary | Yes | No | Partial | Yes |
| Service Request Detail | Yes | Yes | Yes | Yes |
| Payment | Yes | No | Partial | Yes |
| Notification Center | Yes | Partial | Partial | Yes |
| Complaint Submission | Yes | No | Partial | Yes |
| Subscription Plans | Yes | No | Partial | Yes |
| Subscription Detail | Yes | No | Partial | Yes |
| Agent Login | No | Yes | Yes | Yes |
| Agent Dashboard | No | Yes | Yes | Yes |
| Assignment Queue | No | Partial | Yes | Yes |
| Task Detail | No | Yes | Yes | Yes |
| Visit Execution | No | Yes | Partial | Yes |
| Evidence Upload | No | Yes | Partial | Yes |
| Task Status Update | No | Yes | Partial | Yes |
| Operations Dashboard | No | No | Yes | Yes |
| Service Queue | No | Partial | Yes | Yes |
| Admin User Management | No | No | No | Yes |
| Report Dashboard | No | Partial | Yes | Yes |

---

# 5. Implementation Notes

- All customer app screens should consume a common layout shell and session middleware.
- All operational screens should use role-aware routing and permissions guard.
- All forms should use a common validation layer.
- All API calls should maintain correlation IDs and show stable error maps.
- The OTP, booking, payment, and assignment flows are the primary risk points and need early validation.

---

## Summary

This frontend screen specification provides a developer-ready implementation contract for the PropertyPilot MVP. It aligns user journeys, screen catalog, API contracts, and field expectations into a consistent set of UI requirements for engineering, QA, UX, and product.

The screen inventory covers customer onboarding, property management, service booking, payment, assignments, support, notifications, and operation-level visibility. Implementation should prioritize the MVP screens in the defined flow order and maintain strict alignment with the approved backend API and data model.
