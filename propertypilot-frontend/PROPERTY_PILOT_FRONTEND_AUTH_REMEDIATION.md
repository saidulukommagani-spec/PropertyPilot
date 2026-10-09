# PropertyPilot Frontend Authentication Remediation

## Backend authentication contract

`AuthController` exposes `POST /api/v1/auth/login`.

Request DTO (`LoginRequest`):

```json
{ "email": "admin@propertypilot.com", "password": "..." }
```

Response DTO (`LoginResponse`):

```json
{ "accessToken": "<JWT>", "tokenType": "Bearer", "expiresIn": 3600 }
```

The JWT property is `accessToken`. Login does not return a user profile or role. The follow-up identity request is `GET /api/v1/users/me`. Its current `UserResponse` fields are `userId`, `fullName`, `email`, `mobileNumber`, and `status`; **there is no user role field**. The frontend does not decode roles from JWT claims.

Per the frontend security architecture, the access token is held in memory in `AuthContext` and supplied to Axios through a token provider. It is cleared on sign out or HTTP 401. It is not written to `localStorage` or `sessionStorage`; a full page reload requires a new login because the backend has no refresh-token flow.

## Files fixed or connected

- `src/api/client.ts` — canonical Axios client, configurable API base URL, in-memory token provider, bearer header, and 401 handler.
- `src/api/auth.ts` — typed login/current-user requests, matching backend DTOs, and role normalization for a future role-bearing response.
- `src/api/httpClient.ts` — compatibility re-export to the canonical client.
- `src/features/auth/authApi.ts` — compatibility re-export to the canonical auth API.
- `src/features/auth/AuthContext.tsx` — stores access token and profile in memory, logs in, loads `/users/me`, and clears the session on 401/logout.
- `src/features/auth/hooks/useAuth.ts` — exports the AuthContext hook through the feature hook path.
- `src/features/auth/pages/LoginPage.tsx` — submits email/password through AuthContext and redirects to the originally requested path or `/staff` after successful authentication.
- `src/app/routes/AppRoutes.tsx` — application route composition for login and staff dashboard.
- `src/app/routes/ProtectedRoute.tsx` — redirects unauthenticated users to login and preserves the requested path.
- `src/app/routes/RoleGuard.tsx` — checks authorized `ADMIN`/`OPERATIONS` roles without trusting client-decoded JWT claims.
- `src/App.tsx` — uses the route composition above.

## Audit: remaining incomplete files

### API modules

These files are still directory-outline placeholders and are not usable API integrations:

- `src/api/admin.ts`
- `src/api/complaints.ts`
- `src/api/customers.ts`
- `src/api/kyc.ts`
- `src/api/marketplace.ts`
- `src/api/notifications.ts`
- `src/api/operations.ts`
- `src/api/payments.ts`
- `src/api/properties.ts`
- `src/api/queryKeys.ts`
- `src/api/reports.ts`
- `src/api/serviceRequests.ts`
- `src/api/subscriptions.ts`
- `src/api/types.ts`
- `src/api/visits.ts`

`src/api/httpClient.ts` and `src/features/auth/authApi.ts` now only provide compatibility exports; new API modules should import from `src/api/client.ts` and `src/api/auth.ts`.

### Authentication feature files

- `src/features/auth/components/AuthStatusCard.tsx`
- `src/features/auth/pages/ForgotPasswordPage.tsx`
- `src/features/auth/pages/ProfilePage.tsx`
- `src/features/auth/pages/RegisterPage.tsx`

These remain placeholders. They are outside the login-only Sprint 1 path unless account recovery, registration, profile management, or the old hook/status card are added to the sprint scope.

### Routing audit

`src/app/routes/AppRoutes.tsx`, `ProtectedRoute.tsx`, and `RoleGuard.tsx` now contain working route code. `src/features/auth/ProtectedRoute.tsx` is an older, duplicate guard implementation and is currently unused; consolidate or remove it when the route layer is next cleaned up.

## Remaining Sprint 1 blockers

1. **Role identity contract:** the staff route can authenticate a user but must deny access when roles are absent. Add effective roles to the authorized `/api/v1/users/me` response or provide a protected role/profile endpoint. Do not infer a role from the JWT in the browser.
2. **Role-policy alignment:** normalize the backend policy discrepancy documented in the frontend architecture: `/api/admin/**` is protected as ADMIN in `SecurityConfig`, while `AdminOperationsController` also permits OPERATIONS. Add role/resource authorization tests before enabling both roles.
3. **Install and integration verification:** dependencies have not been restored in this workspace and no successful frontend build/auth integration run is recorded. Run `npm install`, then build and exercise login success, invalid credentials, `/users/me` failure, 401 session clearing, direct protected-route entry, and role denial with backend-backed accounts.
4. **Session behavior:** access tokens intentionally do not survive a full reload under the current security contract. This is expected for the pilot; a persistent session requires a server-managed refresh/session design before adding browser persistence.

No role-specific dashboard is accessible until blocker 1 is resolved. The frontend redirect after a successful credential and profile exchange is implemented, but the role guard will stop users at the staff authorization boundary until the backend supplies trusted role data.
