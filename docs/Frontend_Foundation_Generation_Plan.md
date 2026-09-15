<!-- filepath: `Frontend_Foundation_Generation_Plan.md` -->

# Frontend Foundation Generation Plan

Document Type: Frontend Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Frontend Architecture / Product / UX / Engineering

---

# Purpose

This document defines the frontend foundation for PropertyPilot using React 18, TypeScript, Vite, Material UI, Redux Toolkit, React Query, and React Router.

It translates product requirements, screen catalog, UX flows, customer journeys, API contract, and backend domain model into an implementation-ready frontend architecture and delivery plan.

The purpose is to establish:
- a scalable, maintainable frontend structure
- clear module ownership and screen grouping
- shared component and design standards
- precise state management patterns
- secure authentication and route protection
- API integration strategy
- deployment and release readiness
- a sprint-wise delivery plan with effort estimates

---

# Architecture Overview

## Target Frontend Architecture

PropertyPilot frontend will be implemented as a modular React application with clear domain boundaries and role-based application partitions.

Core stack:
- React 18
- TypeScript
- Vite
- Material UI
- Redux Toolkit
- React Query
- React Router
- Axios or generated OpenAPI client
- Formik or react-hook-form
- ESLint + Prettier
- Jest + Testing Library + Playwright

## Architectural Principles

- Domain-first module design
- API contract alignment with backend OpenAPI
- Role-based access and route guard enforcement
- Reusable shared UI components
- Strong form validation and user feedback
- Responsive design for web and tablet experiences
- Performance optimized for dashboards, listings, maps, and uploads
- Feature-based ownership and testability
- Accessible and localization-ready UI

## Application Boundaries

1. Customer App
   - onboarding and customer experience
   - property management
   - service booking
   - payments and subscriptions
   - notifications and complaints

2. Agent App
   - task assignment
   - location verification
   - photo and evidence capture
   - task completion

3. Operations Portal
   - queue monitoring
   - task assignment
   - escalation and actioning

4. Admin Portal
   - user management
   - role management
   - pricing and configuration
   - audit and reporting

5. NRI Module
   - long-distance / investor-oriented experiences
   - property interest and support flows

6. Marketplace
   - public or semi-public listing and discovery flows

7. Analytics
   - KPI dashboards and operational performance views

---

# Folder Structure

```text
src/
├── app/
│   ├── App.tsx
│   ├── providers/
│   │   ├── AppProviders.tsx
│   │   ├── AuthProvider.tsx
│   │   └── QueryProvider.tsx
│   └── routes/
│       ├── AppRoutes.tsx
│       ├── PublicRoutes.tsx
│       ├── ProtectedRoutes.tsx
│       ├── RoleRoutes.tsx
│       └── routeConfig.ts
├── modules/
│   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   ├── types/
│   │   └── utils/
│   ├── customer/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── agent/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── operations/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── admin/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── nri/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── marketplace/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   ├── analytics/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── store/
│   │   └── types/
│   └── shared/
│       ├── components/
│       ├── hooks/
│       ├── layout/
│       ├── services/
│       ├── store/
│       ├── theme/
│       ├── types/
│       ├── utils/
│       └── validation/
├── pages/
│   ├── LandingPage.tsx
│   ├── NotFoundPage.tsx
│   ├── UnauthorizedPage.tsx
│   ├── LoadingPage.tsx
│   └── ErrorBoundaryPage.tsx
├── components/
│   ├── app-shell/
│   ├── data-grid/
│   ├── forms/
│   ├── media/
│   ├── maps/
│   ├── notifications/
│   ├── tables/
│   ├── dialogs/
│   └── charts/
├── layouts/
│   ├── AuthLayout.tsx
│   ├── AppLayout.tsx
│   ├── SidebarLayout.tsx
│   ├── DashboardLayout.tsx
│   └── EmptyLayout.tsx
├── services/
│   ├── api/
│   │   ├── client.ts
│   │   ├── authApi.ts
│   │   ├── baseApi.ts
│   │   ├── queryKeys.ts
│   │   └── generated/
│   │       └── api.ts
│   ├── storage/
│   │   ├── sessionStorage.ts
│   │   └── localStorage.ts
│   └── external/
│       ├── maps.ts
│       ├── upload.ts
│       └── analytics.ts
├── hooks/
│   ├── useAuth.ts
│   ├── useDebouncedValue.ts
│   ├── usePermissions.ts
│   ├── usePagination.ts
│   └── useQueryParams.ts
├── store/
│   ├── index.ts
│   ├── hooks.ts
│   ├── slices/
│   │   ├── authSlice.ts
│   │   ├── uiSlice.ts
│   │   ├── customerSlice.ts
│   │   ├── agentSlice.ts
│   │   ├── operationsSlice.ts
│   │   └── adminSlice.ts
│   └── middleware/
│       ├── logger.ts
│       └── persistence.ts
├── routes/
│   ├── routes.ts
│   ├── routeDefinitions.ts
│   ├── guards/
│   │   ├── AuthGuard.tsx
│   │   ├── RoleGuard.tsx
│   │   └── TenantGuard.tsx
│   └── loaders/
│       ├── authLoader.ts
│       └── dashboardLoader.ts
├── utils/
│   ├── formatters/
│   │   ├── currency.ts
│   │   ├── date.ts
│   │   └── phone.ts
│   ├── validation/
│   │   ├── schema.ts
│   │   └── validators.ts
│   ├── env.ts
│   ├── error.ts
│   └── helpers.ts
├── assets/
│   ├── images/
│   ├── icons/
│   ├── logos/
│   └── illustrations/
├── theme/
│   ├── theme.ts
│   ├── palette.ts
│   ├── typography.ts
│   └── components.ts
├── styles/
│   ├── globals.css
│   └── overrides.css
├── tests/
│   ├── setup.ts
│   ├── fixtures/
│   └── e2e/
├── main.tsx
├── vite-env.d.ts
├── index.css
└── App.css
```

---

# Screen-to-Module Mapping

This section maps the screen catalog to application modules. The following inventory defines 84 screens across the PropertyPilot platform.

## 1. Customer App: 32 screens

Module: Customer App  
Screen IDs: C01–C32

| Screen ID | Screen Name | Purpose |
|---|---|---|
| C01 | Landing / Welcome | Entry screen for customer onboarding |
| C02 | Sign Up | Customer registration |
| C03 | OTP Verification | Mobile/OTP validation |
| C04 | Login | Username/mobile login |
| C05 | Forgot Password | Password recovery |
| C06 | Dashboard | Primary customer dashboard |
| C07 | My Properties | Property list overview |
| C08 | Add Property | New property form |
| C09 | Property Details | Property detail page |
| C10 | Edit Property | Property update screen |
| C11 | Property Ownership Verification | Ownership verification status |
| C12 | Service Catalog | Available services |
| C13 | Service Details | Service description and pricing |
| C14 | Booking Summary | Booking review |
| C15 | Booking Confirmation | Booking success screen |
| C16 | Service Tracking | Active tracking and lifecycle |
| C17 | Service History | Past requests and statuses |
| C18 | Payment Selection | Payment method selection |
| C19 | Payment Details | Payment form and details |
| C20 | Payment Result | Success/failure acknowledgment |
| C21 | Subscription Plans | Plans and pricing |
| C22 | Subscription Management | Active plan management |
| C23 | Notifications | Activity center |
| C24 | Notification Details | A specific notification record |
| C25 | Complaints | Complaint list |
| C26 | Raise Complaint | Complaint initiation |
| C27 | Complaint Details | Complaint tracking and updates |
| C28 | Profile | User profile |
| C29 | Edit Profile | Profile update screen |
| C30 | Settings | App/account preferences |
| C31 | Support | Help and support contact |
| C32 | App Feedback | Feedback collection |

## 2. Agent App: 19 screens

Module: Agent App  
Screen IDs: A01–A19

| Screen ID | Screen Name | Purpose |
|---|---|---|
| A01 | Agent Login | Agent sign in |
| A02 | Agent OTP | Secure verification |
| A03 | Agent Dashboard | Queue and assigned work |
| A04 | Assigned Tasks | Current tasks list |
| A05 | Task Details | Full task information |
| A06 | Start Task | Work initiation |
| A07 | Route / GPS Capture | Location verification |
| A08 | Evidence Capture | Media upload |
| A09 | Evidence Review | Captured proof verification |
| A10 | Visit Completion | Finalize task |
| A11 | Task Notes | Notes and remarks |
| A12 | Status Update | Update workflow state |
| A13 | Escalation Request | Route to escalation workflow |
| A14 | Agent Availability | Availability state |
| A15 | Task History | Previously completed tasks |
| A16 | My Profile | Agent profile |
| A17 | My Performance | Agent metrics |
| A18 | Support | Help and troubleshooting |
| A19 | Device Check | Device readiness / permission check |

## 3. Operations Portal: 13 screens

Module: Operations Portal  
Screen IDs: O01–O13

| Screen ID | Screen Name | Purpose |
|---|---|---|
| O01 | Operations Login | Sign in screen |
| O02 | Operations Dashboard | Queue overview |
| O03 | Service Request Queue | Active tasks queue |
| O04 | Task Assignment | Assign task to agent |
| O05 | Reassignment | Reassign a task |
| O06 | Escalation Queue | Escalated items |
| O07 | Case Review | Review and investigate |
| O08 | Agent Workload | Agent capacity view |
| O09 | Service Status Board | Service state overview |
| O10 | Support Case List | Complaint/resolution queue |
| O11 | User Search | Search accounts and records |
| O12 | Report Overview | Standard operational report dashboard |
| O13 | Audit Log Viewer | Governance and audit review |

## 4. Admin Portal: 10 screens

Module: Admin Portal  
Screen IDs: AD01–AD10

| Screen ID | Screen Name | Purpose |
|---|---|---|
| AD01 | Admin Login | Sign in |
| AD02 | Admin Dashboard | Platform overview |
| AD03 | User Management | Manage users and roles |
| AD04 | Role Management | Permissions and assignments |
| AD05 | Pricing Management | Service plan pricing |
| AD06 | Plan Configuration | Subscription configuration |
| AD07 | System Configuration | Global settings |
| AD08 | Audit Logs | Security and admin actions |
| AD09 | Feature Flags | Release toggles |
| AD10 | Platform Health | Status / ops checks |

## 5. NRI Module: 6 screens

Module: NRI Module  
Screen IDs: N01–N06

| Screen ID | Screen Name | Purpose |
|---|---|---|
| N01 | NRI Landing | NRI-specific entry |
| N02 | Interest Form | Property interest capture |
| N03 | NRI Dashboard | Investor summary |
| N04 | Saved Properties | Wish list / saved items |
| N05 | Virtual Tour Request | Tour request |
| N06 | NRI Support | Assistance channel |

## 6. Marketplace: 3 screens

Module: Marketplace  
Screen IDs: M01–M03

| Screen ID | Screen Name | Purpose |
|---|---|---|
| M01 | Marketplace Home | Listing overview |
| M02 | Listing Details | Listing profile page |
| M03 | Listing Inquiry | Inquiry or lead capture |

## 7. Analytics: 1 screen

Module: Analytics  
Screen ID: AN01

| Screen ID | Screen Name | Purpose |
|---|---|---|
| AN01 | Analytics Overview | KPI and operational summary |

Total = 32 + 19 + 13 + 10 + 6 + 3 + 1 = 84

---

# Routing Strategy

## Public Routes
Routes available without authentication:
- landing
- login
- signup
- OTP verification
- forgot password
- password reset
- public marketplace listing pages
- public support or informational pages

## Authenticated Routes
Protected after successful auth:
- dashboard
- property management
- booking and payment flows
- profile and settings
- notifications
- complaints
- reports

## Role-Based Routes

### Customer Routes
- dashboard
- properties
- services
- bookings
- subscriptions
- payments
- notifications
- complaints
- profile

### Agent Routes
- agent dashboard
- assigned tasks
- GPS capture
- evidence upload
- task completion
- performance

### Operations Routes
- operations dashboard
- queue
- assignment/reassignment
- escalations
- investigations
- support review

### Admin Routes
- admin dashboard
- user management
- role assignment
- system config
- audit logs
- pricing
- feature flags

## Route Guard Model
- PublicRouteGuard: no auth required
- AuthRouteGuard: requires login
- RoleRouteGuard: requires role authorization
- PermissionRouteGuard: requires specific permission
- Tenant/ContextGuard: ensures correct context and ownership
- Route loader: fetch user profile and permission set before rendering

---

# Authentication Design

## OTP Login
Recommended flow:
1. user enters mobile/email
2. backend sends OTP or verification code
3. frontend validates OTP via API
4. token exchange occurs
5. app stores JWT in secure storage
6. user session is established

## JWT Handling
- access token stored in memory or secure storage
- refresh token stored in secure cookie or secure storage
- token refresh handled through a refresh endpoint
- access token expiry short-lived
- invalid/expired tokens redirect to login
- refresh workflow should be silent and retry-aware

## Session Management
- use a session state in Redux
- persist minimal auth metadata
- maintain user role and permissions
- clear stored auth on logout or token invalidation
- include session expiry handling and refresh timing

## Route Guards
- AuthGuard ensures user is logged in
- RoleGuard validates user role against route requirements
- PermissionGuard validates specific actions
- Suspended/blocked users routed to restricted state
- session expiration triggers re-authentication

---

# State Management Strategy

## Redux Store Structure

```text
store/
├── auth/
│   ├── authSlice
│   ├── sessionSlice
│   └── permissionsSlice
├── customer/
│   ├── profileSlice
│   ├── propertySlice
│   ├── bookingSlice
│   └── subscriptionSlice
├── agent/
│   ├── taskSlice
│   ├── assignmentSlice
│   └── evidenceSlice
├── operations/
│   ├── queueSlice
│   ├── escalationSlice
│   └── dashboardSlice
├── admin/
│   ├── userSlice
│   ├── roleSlice
│   ├── configSlice
│   └── auditSlice
├── ui/
│   ├── notificationSlice
│   ├── modalSlice
│   ├── drawerSlice
│   └── spinnerSlice
└── common/
    ├── paginationSlice
    ├── filterSlice
    └── errorSlice
```

## Server State Management
Use React Query for:
- fetching user profiles
- service catalog data
- transactions
- subscriptions
- report results
- queue data
- notifications
- complaints
- analytics

## Caching Strategy
- cache stable reference data aggressively
- cache user-specific data with short TTL
- invalidate on mutation
- optimistic updates for simple UI changes
- cache query keys by domain and user
- use stale-while-revalidate where appropriate

---

# API Integration Strategy

## OpenAPI Client Generation
- generate TypeScript client from OpenAPI specification
- maintain typed request/response models
- align API call names to domain use cases
- keep generated code in a dedicated `services/api/generated` folder
- do not manually edit generated output

## API Layer Pattern
- each module owns service file
- API calls abstracted to domain service functions
- use React Query hooks for data retrieval and mutation
- centralize request headers, auth, timeouts, and retries

## Error Handling
- centralized API response parser
- map backend 4xx/5xx errors to user-friendly messages
- include retry where safe
- strip raw server details from end-user display
- capture errors to monitoring layer

## Retry Strategy
- retry on network failure and 5xx errors
- no retry for validation errors or 4xx payload failures
- exponential backoff for payment, notification, and report calls
- retry disable for destructive or irreversible actions

## Loading States
- skeletons for lists and dashboards
- button loading states
- form-level loading states
- route-level loading boundary
- global progress indicator for async actions

---

# Reusable Component Library

## Forms
- TextField
- Select
- DatePicker
- TimePicker
- Toggle
- Checkbox
- File upload input
- Auto-complete
- Search form
- Address form block
- OTP input group

## Tables
- DataTable
- SortableTable
- PaginatedTable
- Inline actions table
- Expandable row table
- Filterable table

## Cards
- SummaryCard
- KPIStatCard
- PropertyCard
- ServiceCard
- PaymentCard
- ComplaintCard
- AgentCard

## Dialogs
- ConfirmDialog
- FormDialog
- DetailDialog
- ErrorDialog
- AccessDialog

## Notifications
- Toast system
- inline banner
- success/error snackbars
- notification center panel
- push notification container

## Uploads
- drag-and-drop uploader
- file type validation
- preview panel
- upload progress indicator
- image crop or resize support if necessary

## Maps
- location picker
- map pin
- geofence/route display
- GPS verification display
- property map card

## Media Viewer
- image gallery
- lightbox modal
- document preview
- proof evidence viewer
- photo annotation if required

---

# Form Strategy

## Validation
- Yup or Zod for schema validation
- field errors displayed inline and as helper text
- cross-field validation for bookings, prices, and dates
- required-field validation per API contract
- validation messages in UI and API aligned

## Error Handling
- form-level error summary
- field-level error highlighting
- backend validation mapping into form errors
- consistent busy state and disabling logic

## Dynamic Forms
- conditional fields by property type
- dynamic pricing and plan options
- role-specific forms
- complaint categories and escalation reasons
- different service request fields by service type

## Multi-Step Forms
- property onboarding
- booking form
- payment flow
- complaint creation
- admin configuration flow
- NRI enquiry workflow

---

# UI Standards

## Responsive Design
- mobile-first approach
- web and tablet support
- dashboard layouts adapt to different screens
- dense table and compact list support
- forms optimized for touch interaction

## Accessibility
- WCAG-aligned components
- keyboard navigation
- semantic form labels
- screen reader friendly
- focus management for dialogs and warnings
- color contrast and status semantics
- accessible date/time controls

## Localization Ready
- externalize strings
- date/number formatting abstraction
- language structure prepared for future i18n
- locale-aware currency and formatting

## Theme Strategy
- Material UI custom theme
- design tokens for colors, spacing, and typography
- dark/light mode not required without product scope
- consistent semantic color usage:
  - success
  - warning
  - error
  - info
  - neutral
- maintain product brand and operational clarity

---

# Screen Implementation Roadmap

## Sprint 1: Foundation and Shared UX
Focus:
- app shell
- routing
- auth screens
- design system base
- API client foundation
- Redux and React Query setup

Deliverables:
- Vite project bootstrapped
- MUI theme and layout structure
- auth screens
- route guards
- API client layer
- base form components

---

## Sprint 2: Customer Core
Focus:
- onboarding and dashboard
- property management
- service catalog and booking
- notification center

Deliverables:
- signup/login/OTP flows
- property create/edit/list
- service catalog
- booking flow
- payment initiation
- subscription entry screens

---

## Sprint 3: Agent + Operations
Focus:
- assigned work
- GPS verification
- evidence upload
- task completion
- queue and assignment tools

Deliverables:
- task dashboard
- GPS field flow
- evidence capture screens
- task notes and escalations
- operations dashboard and queue

---

## Sprint 4: Admin + Reports
Focus:
- admin portal
- roles and permissions
- pricing and config
- report dashboards
- audit screens

Deliverables:
- user management
- role management
- pricing management
- system configuration
- report & analytics screens
- audit log review

---

## Sprint 5: NRI + Marketplace + Polish
Focus:
- NRI flows
- marketplace entry pages
- user experience tuning
- accessibility and QA polish

Deliverables:
- NRI landing and interest forms
- listing screens and inquiry flow
- responsive refinements
- UX and QA polish
- final release readiness validations

---

# Testing Strategy

## Unit Tests
- utility functions
- validation schema
- Redux reducers and selectors
- custom hooks
- service layer logic
- mapper logic

## Component Tests
- rendering
- interaction behavior
- form validation
- role-based access rendering
- error and loading states
- accessible keyboard navigation

## Integration Tests
- route + auth flow validation
- API + query integration
- service request creation flow
- payment flow integration
- dashboard loading states
- form submission to backend

## E2E Tests
- signup and login
- property creation
- service booking
- payment confirmation
- agent assignment completion
- operations escalation workflow
- admin role assignment
- complaint submission and resolution

## Test Tools
- Jest
- Testing Library
- MSW or mock API layer
- Playwright for E2E
- coverage thresholds with CI enforcement

---

# Performance Strategy

## Lazy Loading
- route-based lazy loading
- module-level code splitting
- chart libraries loaded when needed
- media viewer loaded only when triggered

## Code Splitting
- split customer, agent, admin, operations, analytics modules
- split heavy features like maps, charts, and upload preview

## Caching
- React Query caching
- memoization for value objects and selectors
- stale state management for reference lists
- reduce re-renders through structural memoization

## Optimization
- virtualized tables for large datasets
- use minimal re-renders
- defer non-critical data
- avoid large unbounded payloads in dashboards
- prioritize responsive loading for large history datasets

---

# Build & Deployment

## Environment Strategy
- Local development
- Development
- QA
- UAT
- Production

## Build Pipelines
- install dependencies
- lint
- type-check
- unit tests
- component/integration tests
- build bundle
- artifact packaging
- environment-specific config injection

## Release Process
- feature branch flow with PR gates
- build verification before merge
- staging deployment before UAT
- production deployment only after sign-off
- version tagging and release notes
- rollback document and artifact retention

## Deployment Considerations
- static asset generation and CDN if applicable
- environment-specific base URLs
- auth redirect configuration
- API URL management
- runtime config with environment variables

---

# Effort Estimates

## Developers
Recommended frontend team size:
- 1 frontend architect / lead
- 2 to 4 frontend engineers
- 1 QA automation engineer
- 1 UX engineer or designer if available
- optional backend-facing integration support

## Timeline
- foundation and shared components: 2 to 3 weeks
- customer flows: 4 to 6 weeks
- agent and operations flows: 3 to 5 weeks
- admin and analytics: 2 to 4 weeks
- release hardening and QA: 2 to 4 weeks

Total MVP frontend delivery timeline:
- 10 to 16 weeks depending on team size and API readiness

## Dependencies
- final screen catalog and UX approval
- backend API contract completion
- auth and role design finalized
- design system tokens approved
- environment setup completed
- test strategy and automation environment ready

## Risks
- late API contract changes
- unstable auth and role data
- inconsistent UX patterns across modules
- screen complexity around service workflows and file upload
- environment drift between QA/UAT and production
- performance degradation with large lists and dashboards
- accessibility gaps in dense operational screens

---

# Implementation Recommendation

The recommended frontend architecture is a modular React application with:
- a shared design system
- domain-based feature modules
- role-aware route architecture
- Redux for UI and session state
- React Query for server state and caching
- consistent API contract usage
- strong testing and QA automation from the start

This approach is suitable for a lean MVP while remaining scalable for future phases and enterprise expansion.

---

# Exit Criteria for Frontend Foundation Completion

The frontend foundation is ready for MVP release when:
- all screen flows are implemented and validated
- auth flows and route guards work correctly
- customer, agent, operations, and admin role flows are tested
- all critical API calls are wired and error-handled
- forms and upload flows are validated
- mobile responsiveness is acceptable
- dashboards and reports load correctly
- performance is acceptable under realistic volume
- build and deploy pipeline is working
- final sign-off is completed by product, QA, and engineering

---

# Final Summary

This frontend foundation plan provides a production-grade blueprint for the PropertyPilot user interface based on the approved screen catalog, user journeys, feature catalog, and API model. It organizes the product by functional modules, uses role-aware routing and state management patterns, and establishes a strong base for UI quality, performance, accessibility, and release readiness.

The architecture balances rapid MVP delivery with scalability, maintainability, and operational quality. With disciplined implementation and QA coverage, it can support both the initial PropertyPilot MVP and the later enterprise-scale expansion.
