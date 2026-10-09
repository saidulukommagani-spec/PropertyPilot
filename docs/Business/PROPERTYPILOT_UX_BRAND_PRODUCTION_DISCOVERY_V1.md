# PropertyPilot UX, Brand, and Production Quality Discovery

## Stage A — Discovery

### Mission

Elevate the existing PropertyPilot application into a consistent, readable, responsive, accessible enterprise product while preserving its architecture, business rules, API contracts, role access, and working functionality.

This document records the initial frontend discovery and proposes a bounded reference screen for design-system validation. No application code was changed during this discovery stage.

## Current frontend foundation

- React 18 and TypeScript
- Material UI 6 with Emotion
- React Router 6 and existing role guards
- TanStack React Query
- Vitest and Testing Library for component tests
- Playwright dependency and configuration are present
- Existing role-specific navigation for Customer, Agent, Operations, and Admin

No additional UI framework or dependency is needed for the first design-system stage.

## Findings

### Brand and design system

- The current theme uses primary teal `#176b87` and amber secondary, while the brief specifies deep navy `#102A43` and teal `#0D9488`.
- The theme names Inter as the font, but no font asset or font-loading source was found. The browser therefore falls back to the system font stack.
- Theme coverage is limited to palette, radius, a few typography settings, card borders/shadows, and button sizing. Spacing, elevation, focus, and semantic status tokens are not defined as shared design tokens.
- Styles are split between the MUI theme, `globals.css`, and page-level `sx` props.
- The sidebar currently displays a standalone `P` monogram. No approved production logo asset was found. Do not expand or replace it with an unapproved logo concept.
- `public/favicon.svg` contains an old directory listing rather than SVG markup. `index.html` does not currently link to it.

### Role experience and navigation

- Routes use role guards, and the sidebar provides role-specific groups.
- The shared header always displays “Operations” and “PropertyPilot staff workspace,” including in Customer and Agent workspaces. It should become role-aware while preserving existing role names and guards.
- The app title and description in `index.html` are staff-only even though the same app serves multiple workspaces.

### Testing and quality tooling

- Component tests exist and use Vitest and Testing Library.
- Playwright is configured to launch Vite, but no `e2e/` directory or browser workflow tests were found.
- No automated accessibility package such as axe was found. Keyboard and visual checks will need manual verification unless an accessibility dependency is separately approved and added.
- The last recorded frontend baseline before this discovery was 40 test files / 91 tests passing and a successful production build. These checks were not rerun during Stage A.

## Proposed reference screen

Use the Operations Service Request Detail screen at `/staff/service-requests/:id` as the first reference screen.

It exercises the central operational workflow: request, assignment, visits, evidence, status actions, and activity history. It is a suitable screen to validate information hierarchy, responsive layout, readable record context, accessible actions, loading/error/empty states, and role-sensitive controls before extending shared patterns to other modules.

After the Operations version is validated, verify the shared screen’s Agent route separately and apply the approved shared shell and tokens to Customer and Admin experiences without merging their distinct permissions or workflows.

## Proposed bounded implementation scope

### Foundation

- Define shared palette, typography, spacing, radius, elevation, focus, and semantic status tokens in the existing MUI theme.
- Align the shared shell colors with the supplied palette while maintaining accessible text and control contrast.
- Make the header title and description reflect the active role/workspace.
- Keep existing role guards, navigation destinations, and auth behavior intact.
- Do not introduce a final logo asset until an approved production vector mark is available.
- Correct the favicon only after its intended final mark is approved; avoid deploying a placeholder logo.

### Reference screen

- Apply the shared tokens and hierarchy to the Service Request Detail page and reuse its existing summary, assignment, visit, evidence, status, and activity components.
- Preserve all current API calls, business transitions, role permissions, and data ownership rules.
- Test the Operations and Agent routes independently, including narrow mobile layouts.

## Likely affected files

- `propertypilot-frontend/src/styles/theme.ts`
- `propertypilot-frontend/src/styles/globals.css`
- `propertypilot-frontend/src/components/layouts/DashboardLayout.tsx`
- `propertypilot-frontend/src/components/layouts/Header.tsx`
- `propertypilot-frontend/src/components/layouts/Sidebar.tsx`
- `propertypilot-frontend/src/features/service-requests/pages/ServiceRequestDetailPage.tsx`
- Existing Service Request detail components and their tests
- `propertypilot-frontend/index.html` and `propertypilot-frontend/public/favicon.svg` only after brand-asset decisions
- Focused Playwright tests, if the browser environment is available

## Dependencies

Use the existing MUI, React Router, React Query, Testing Library, Vitest, and Playwright dependencies. No new dependency is proposed for the initial foundation stage.

## Risks and constraints

- The worktree already contains numerous modified, deleted, and untracked files from prior development. Preserve them and limit changes to the agreed scope; do not reset, clean, or overwrite unrelated work.
- A final production logo requires an approved vector asset. Do not treat an AI concept, rough sketch, or temporary monogram as an approved brand asset.
- Playwright's configuration does not prove browser workflows are currently covered; browser availability and representative role workflows must be verified before making UX claims.
- Automated build and component tests cannot alone establish WCAG 2.2 AA, real-user usability, Core Web Vitals, or cross-device behavior. Report those checks separately and only claim what was measured.
- Preserve role-specific visibility, backend authorization, customer data isolation, financial behavior, and evidence-verification semantics throughout visual changes.

## Stage A status

Discovery is recorded. No application source files were modified in this stage. The proposed next bounded stage is the shared shell and Operations Service Request Detail reference screen, followed by focused responsive, role, accessibility, test, and build checks before wider rollout.

## Stage 1 - Brand Implementation (2026-10-09)

**Status: shared brand foundation implemented and verified within this bounded scope.** This entry records Stage 1 as an update to the discovery baseline above; the historical Stage A notes remain intact.

### Brand decisions and assets

- Created an original geometric PropertyPilot mark using open parcel-like corner brackets and a rising guidance path with a muted gold waypoint. The mark avoids a house silhouette, shield, or conventional map pin.
- Added editable vector assets under `propertypilot-frontend/public/brand/`: `propertypilot-logo.svg` (light horizontal wordmark and tagline), `propertypilot-logo-dark.svg`, `propertypilot-mark.svg`, `propertypilot-mark-light.svg`, and `propertypilot-logo-monochrome.svg`.
- Replaced `propertypilot-frontend/public/favicon.svg`, which contained directory-listing text rather than SVG, with the compact navy favicon. `index.html` now references the SVG and uses general PropertyPilot title and metadata.
- The compact mark is used in the shared sidebar and header. The horizontal logo is used on sign-in. The sidebar role labels, destinations, route guards, and authentication flow were preserved.

### Design system and shell

- Updated the existing MUI theme and global CSS with the approved canvas `#F5F7FA`, gold and subtle-surface tokens, a documented 4px spacing unit (the existing MUI 8px spacing base remains, so its increments are multiples of 4px), disabled text, and accessible contained-secondary button treatment. Navy remains the structural primary; the brand teal is retained for identity/accent uses.
- Contrast was calculated for key pairings: navy/white 14.64:1, body text/canvas 13.56:1, muted text/canvas 5.32:1, brand teal/white 3.74:1, and white/darker teal button 5.47:1. Teal is not used as small white-background body text. Focus styles are retained.
- Inter is named as a preferred font with system fallbacks, but no Inter file/dependency is present in the frontend assets and no Inter font is installed in the local Windows fonts directory. No remote font dependency was added; the actual fallback is used in this environment.
- Header labels now combine the current route section (without displaying record IDs) with the active role workspace description. Existing Operations, Admin, Agent, and Customer sidebar mappings were preserved.

### Verification and evidence

- Focused Vitest: 3 files / 13 tests passed.
- Full Vitest: 56 files / 166 tests passed. Existing React Router v7 future-flag notices were emitted.
- Microsoft Edge Playwright: 23/23 passed with the existing installed Edge executable. The suite includes mocked UI/API-interception coverage, brand asset/favicon checks, role-navigation checks, existing Operations/Agent/Customer route checks, and no-horizontal-overflow checks at 360x800, 390x844, 768x1024, and 1440x900. This is not live backend authorization evidence.
- Production build: `npm.cmd run build` passed TypeScript and Vite (1,283 modules). Vite emitted existing non-fatal Zod/Rollup pure-annotation warnings.
- After screenshots (ignored test artifacts) are under `propertypilot-frontend/test-results/reference-screens.e2e.ts-m-6d5ce-nd-fits-reference-viewports/`: `brand-login-1440x900.png`, `brand-shell-admin-360x800.png`, `brand-shell-admin-390x844.png`, `brand-shell-admin-768x1024.png`, and `brand-shell-admin-1440x900.png`. Login and desktop/mobile shell screenshots were visually inspected. A before screenshot was not captured before implementation; no before image is claimed.
- No automated WCAG audit, live API test, or live role authorization test was performed. No backend, database, API contract, or dependency changes were made. The large pre-existing worktree was preserved.
- Stage H2 remains a mandatory release gate; this frontend stage does not establish production readiness.
- Final visual refinement: reduced unused horizontal SVG viewBox space after inspecting the login screenshot. The focused Edge brand-shell check passed again (1/1), the updated 1440x900 login screenshot was visually rechecked, and the final production build passed.