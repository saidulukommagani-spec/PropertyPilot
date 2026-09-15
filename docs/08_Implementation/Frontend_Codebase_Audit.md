# Frontend Codebase Audit

**Scope:** `propertypilot-frontend/`  
**Audit date:** 2026-09-01  
**TypeScript/TSX files inspected:** 156 of 156

## Executive Result

`propertypilot-frontend/` is not a React application. It is a generated file inventory whose TypeScript, TSX, JSON, HTML, and configuration files contain directory-tree text, installation commands, or planning documentation.

| Classification | Count | Evidence |
| --- | ---: | --- |
| Valid React/TypeScript files | **0** | No imports, exports, declarations, JSX, components, hooks, functions, interfaces, or usable types were found |
| Placeholder files | **156** | Every inspected TS/TSX file is a directory tree, prose roadmap, install command, or empty file |
| Broken TS/TSX files | **156** | None can be parsed as its intended module/configuration |
| Directory-tree placeholders | 150 | Repeated `/src`, `/api`, `/components`, `/layouts`, and similar lines |
| Roadmap documentation | 3 | Long “React Implementation Roadmap” prose stored as TSX |
| Package-install commands | 2 | `npm install ...` stored as TSX |
| Empty files | 1 | No implementation or declaration |

The categories “placeholder” and “broken” overlap: all 156 files are placeholders, and all 156 are broken for their declared purpose.

## Inspection Criteria

Every `.ts` and `.tsx` file under the project root was read and checked for:

- ES module imports and exports
- TypeScript declarations (`type`, `interface`, `enum`, functions, variables, classes)
- JSX elements or React component declarations
- Hook implementations
- Test declarations and assertions
- Valid Vite, Vitest, or Playwright configuration exports
- Directory-tree/prose/install-command placeholder signatures

No file met the minimum criteria for valid TypeScript or React code.

## Valid React Code

**None.**

Specifically:

- `src/main.tsx` does not mount React.
- `src/App.tsx` does not declare a component.
- No TS/TSX file imports React, React Router, Redux Toolkit, TanStack Query, Axios, a local module, or a test library.
- No file exports a component, hook, store, API function, type, or configuration.
- No TSX file contains JSX.
- No test file contains a test suite or assertion.

## Placeholder Classification

### Directory-tree placeholders — 150 files

These files contain a repeated suggested folder layout such as `/src`, `/api`, `/components`, `/layouts`, `/pages`, `/redux`, `/hooks`, `/utils`, `/styles`, and `/tests`. The text is neither TypeScript nor a comment.

This category includes:

- Root configuration: `playwright.config.ts`, `vite.config.ts`, `vitest.config.ts`
- Entrypoints/types: `src/App.tsx`, `src/main.tsx`, `src/vite-env.d.ts`
- All API files
- All app provider, route, and store files
- Almost all component, feature, hook, mock, style, testing, and type files

The complete per-directory inventory appears below.

### Roadmap documentation stored as TSX — 3 files

| File | Actual content |
| --- | --- |
| `src/features/admin/components/AdminStatsGrid.tsx` | Markdown-style production React implementation roadmap |
| `src/features/payments/pages/PaymentListPage.tsx` | Markdown-style production React implementation roadmap |
| `src/testing/render.tsx` | Markdown-style production React implementation roadmap |

These are documentation, not partially implemented components or test utilities.

### Installation commands stored as TSX — 2 files

| File | Actual content |
| --- | --- |
| `src/components/common/Badge.tsx` | A bare `npm install ...` command |
| `src/features/customers/components/CustomerSummaryCard.tsx` | A bare `npm install ...` command |

The command text is invalid TSX and is not a dependency manifest.

### Empty file — 1 file

| File | Actual content |
| --- | --- |
| `src/components/forms/validation.ts` | Empty |

## Complete TS/TSX Inventory

Every file listed below was inspected. Unless called out in the special categories above, its classification is **directory-tree placeholder and broken TypeScript/TSX**.

### Project root — 3

`playwright.config.ts`, `vite.config.ts`, `vitest.config.ts`

### Source root — 3

`App.tsx`, `main.tsx`, `vite-env.d.ts`

### API — 17

`admin.ts`, `auth.ts`, `client.ts`, `complaints.ts`, `customers.ts`, `kyc.ts`, `marketplace.ts`, `notifications.ts`, `operations.ts`, `payments.ts`, `properties.ts`, `queryKeys.ts`, `reports.ts`, `serviceRequests.ts`, `subscriptions.ts`, `types.ts`, `visits.ts`

### Application providers — 2

`AppProviders.tsx`, `QueryProvider.tsx`

### Application routes — 3

`AppRoutes.tsx`, `ProtectedRoute.tsx`, `RoleGuard.tsx`

### Application store — 7

`hooks.ts`, `index.ts`, `slices/authSlice.ts`, `slices/customersSlice.ts`, `slices/notificationsSlice.ts`, `slices/propertiesSlice.ts`, `slices/uiSlice.ts`

### Authentication components — 4

`LoginForm.tsx`, `MFAForm.tsx`, `RegisterForm.tsx`, `ResetPasswordForm.tsx`

### Common components — 8

`Badge.tsx`, `Button.tsx`, `Card.tsx`, `DataTable.tsx`, `EmptyState.tsx`, `LoadingState.tsx`, `Modal.tsx`, `SearchInput.tsx`

### Form components — 5

`FormField.tsx`, `FormSection.tsx`, `FormWrapper.tsx`, `schema.ts`, `validation.ts`

### Layout components — 6

`AppLayout.tsx`, `AuthLayout.tsx`, `DashboardLayout.tsx`, `Footer.tsx`, `Header.tsx`, `Sidebar.tsx`

### UI components — 5

`Alert.tsx`, `Checkbox.tsx`, `DatePicker.tsx`, `Select.tsx`, `Tabs.tsx`

### Admin feature — 7

`components/AdminStatsGrid.tsx`, `components/AuditTable.tsx`, `pages/AdminDashboardPage.tsx`, `pages/AuditPage.tsx`, `pages/ConfigPage.tsx`, `pages/PricingPlansPage.tsx`, `pages/UserManagementPage.tsx`

### Authentication feature — 6

`components/AuthStatusCard.tsx`, `hooks/useAuth.ts`, `pages/ForgotPasswordPage.tsx`, `pages/LoginPage.tsx`, `pages/ProfilePage.tsx`, `pages/RegisterPage.tsx`

### Customer feature — 8

`components/CustomerForm.tsx`, `components/CustomerSummaryCard.tsx`, `components/CustomerTable.tsx`, `hooks/useCustomers.ts`, `pages/CustomerDetailPage.tsx`, `pages/CustomerFormPage.tsx`, `pages/CustomerListPage.tsx`, `pages/CustomerProfilePage.tsx`

### KYC feature — 3

`components/KycDocumentsUploader.tsx`, `pages/KycReviewQueuePage.tsx`, `pages/KycSubmissionPage.tsx`

### Marketplace feature — 5

`components/QuotationForm.tsx`, `components/VendorCard.tsx`, `pages/QuotationPage.tsx`, `pages/VendorDetailPage.tsx`, `pages/VendorListPage.tsx`

### Notification feature — 2

`components/NotificationList.tsx`, `pages/NotificationsPage.tsx`

### Operations feature — 5

`components/AssignmentBoard.tsx`, `components/WorkloadChart.tsx`, `pages/AssignmentBoardPage.tsx`, `pages/EscalationPage.tsx`, `pages/OperationsDashboardPage.tsx`

### Payment feature — 5

`components/InvoiceTable.tsx`, `components/PaymentMethodForm.tsx`, `pages/CheckoutPage.tsx`, `pages/PaymentDetailPage.tsx`, `pages/PaymentListPage.tsx`

### Property feature — 8

`components/PropertyCard.tsx`, `components/PropertyFilters.tsx`, `components/PropertyMapPanel.tsx`, `hooks/useProperties.ts`, `pages/PropertyDetailPage.tsx`, `pages/PropertyDocumentsPage.tsx`, `pages/PropertyFormPage.tsx`, `pages/PropertyListPage.tsx`

### Report feature — 5

`components/ReportExportPanel.tsx`, `components/ReportFilters.tsx`, `pages/ReportDetailPage.tsx`, `pages/ReportGeneratePage.tsx`, `pages/ReportListPage.tsx`

### Service-request feature — 6

`components/ServiceRequestStatusCard.tsx`, `components/ServiceRequestTable.tsx`, `hooks/useServiceRequests.ts`, `pages/ServiceRequestDetailPage.tsx`, `pages/ServiceRequestFormPage.tsx`, `pages/ServiceRequestListPage.tsx`

### Subscription feature — 5

`components/BillingSummary.tsx`, `components/PlanCard.tsx`, `pages/MySubscriptionsPage.tsx`, `pages/SubscriptionCheckoutPage.tsx`, `pages/SubscriptionPlansPage.tsx`

### Visit feature — 5

`components/VisitChecklist.tsx`, `components/VisitTimeline.tsx`, `pages/VisitDetailPage.tsx`, `pages/VisitListPage.tsx`, `pages/VisitSchedulePage.tsx`

### Shared hooks — 5

`useAuthRedirect.ts`, `useDebounce.ts`, `useLocalStorage.ts`, `usePermissions.ts`, `useQueryParams.ts`

### Mocks — 6

`handlers.ts`, `server.ts`, `data/auth.ts`, `data/customers.ts`, `data/properties.ts`, `data/serviceRequests.ts`

### Styles — 2

`theme.ts`, `tokens.ts`

### Testing — 5

`msw.ts`, `render.tsx`, `setup.ts`, `mocks/api.ts`, `mocks/auth.ts`

### Shared types — 5

`api.ts`, `auth.ts`, `entities.ts`, `routes.ts`, `ui.ts`

Inventory total: **156 files**.

## Broken Files

All 156 TS/TSX files are broken for compilation or execution. The failure categories are:

1. Bare path-like text such as `/src` is not valid TypeScript syntax.
2. Markdown headings and prose in TSX files are not comments or JavaScript expressions.
3. Bare `npm install` commands are shell commands, not TSX.
4. The empty validation file provides no implementation.
5. Entrypoints do not import React or mount an application.
6. Components do not return JSX.
7. Hooks do not call or return anything.
8. API modules have no functions, client, request types, or exports.
9. Store files have no Redux store or slices.
10. Test files have no test runner imports, suites, render helpers, mocks, or assertions.
11. Root Vite/Vitest/Playwright files export no configuration.

## Non-TS/TSX Build Blockers

Although the requested classification concerns TS/TSX, these adjacent files prevent the project from being built:

- `package.json` contains a package-install command, not JSON.
- `tsconfig.json` and `tsconfig.node.json` contain directory-tree text, not JSON.
- `index.html` contains directory-tree text, not an HTML application shell.
- `.eslintrc` is not a trustworthy lint configuration until validated/replaced.
- No valid dependency versions or npm scripts exist.
- Node and npm were not available on the audit PATH.

There is no useful `npm install`, TypeScript check, Vite build, Vitest run, or Playwright run that can be attempted against the current contents.

## Files To Keep

No TS/TSX file can be kept as implementation.

The directory and filename inventory may be used as planning input, particularly for the first approved vertical slice. It must not be counted as source-code progress.

Potentially reusable project-level artifacts after content validation:

- `.env.example` as the environment-variable documentation location
- `README.md` as the verified setup guide location
- `public/` as the static asset location
- `src/styles/globals.css` as the global style entrypoint location
- The canonical project path `propertypilot-frontend/`

## Files To Replace

Replace only the files needed for the first executable web slice:

- Valid package manifest, TypeScript configuration, HTML entrypoint, and Vite configuration
- `src/main.tsx` and `src/App.tsx`
- Application providers and routes
- Authentication API/state/route/page/form files
- Customer, property, and service-request API/types/pages/components
- Minimal layouts and shared UI primitives used by those pages
- Real test setup and tests for implemented behavior

Replacement means creating valid implementations from approved API behavior. It does not mean attempting to convert directory-tree text into TypeScript.

## Files To Delete or Defer

Delete placeholder contents and do not recreate these areas until their phases are approved:

- Marketplace
- Payments
- Subscriptions
- KYC
- Advanced admin and operations dashboards
- Reports beyond the MVP contract
- Notifications UI until backend delivery behavior exists
- Generic mocks, stores, hooks, and components with no caller
- Duplicate authentication state approaches until one is selected

Every placeholder file should disappear in the same reviewed change that establishes the valid frontend foundation. Empty feature folders should not be used to imply progress.

## Structure Problems

- The proposed tree mixes global API modules, global entity types, Redux slices, React Query hooks, mocks, and feature folders without an implemented ownership rule.
- API/type logic is split between `src/api`, `src/types`, feature hooks, and testing mocks.
- `components/common` and `components/ui` overlap.
- Authentication appears in `components/auth`, `features/auth`, `api/auth`, store slices, shared hooks, and shared types before a single flow exists.
- Broad future features are scaffolded before core routing, providers, error handling, and API contracts work.
- The package command refers to legacy `react-query`; current dependency choice must follow the approved frontend standard rather than this placeholder command.

## Recommended Recovery Direction

Keep a small feature-oriented React application:

```text
propertypilot-frontend/
|-- package.json
|-- index.html
|-- vite.config.ts
|-- tsconfig.json
|-- src/
|   |-- main.tsx
|   |-- App.tsx
|   |-- app/
|   |   |-- providers/
|   |   `-- routes/
|   |-- shared/
|   |   |-- api/
|   |   |-- components/
|   |   `-- types/
|   `-- features/
|       |-- auth/
|       |-- customers/
|       |-- properties/
|       `-- service-requests/
`-- tests/
```

This is a target organization, not generated code. Add later feature directories only when their OpenAPI operations and backend behavior are implemented.

## Final Decision

- **Valid React code:** none.
- **Placeholder files:** all 156 TS/TSX files.
- **Broken files:** all 156 TS/TSX files.
- **Files safe to keep unchanged as code:** none.
- **Recovery method:** establish a clean minimal frontend foundation after the backend/OpenAPI vertical slice is working; do not repair these placeholders individually.
