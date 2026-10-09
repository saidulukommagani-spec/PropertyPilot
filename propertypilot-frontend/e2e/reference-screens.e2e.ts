import { expect, test, type Page, type Route } from '@playwright/test';

const serviceRequestId = '00000000-0000-4000-8000-000000000104';
const visitId = '00000000-0000-4000-8000-000000000105';
const propertyId = '00000000-0000-4000-8000-000000000101';
const serviceId = '00000000-0000-4000-8000-000000000102';
const quoteId = '00000000-0000-4000-8000-000000000103';
const customerId = '00000000-0000-4000-8000-000000000106';
const reviewEvidenceId = '00000000-0000-4000-8000-000000000107';
const dealId = '00000000-0000-4000-8000-000000000108';
const leadId = '00000000-0000-4000-8000-000000000109';
const dealFixture = {
  dealId, propertyId, sellerCustomerId: customerId, leadId, dealType: 'PROPERTY_SALE', dealStatus: 'NEW',
  expectedAmount: 250000, finalAmount: null, sellerCommissionPercent: 2, buyerCommissionPercent: 2,
  totalCommissionAmount: null, createdAt: '2026-10-09T09:00:00Z', closedAt: null, remarks: null,
  selectedBuyerCustomerId: null, dealSource: 'LEAD', exclusiveListing: false,
  buyerCommissionAmount: null, sellerCommissionAmount: null,
};
const viewports = [
  { width: 360, height: 800 },
  { width: 390, height: 844 },
  { width: 768, height: 1024 },
  { width: 1440, height: 900 },
];

const requestFixture = {
  serviceRequestId,
  propertyId,
  propertyTitle: 'Lakeview House',
  customerId,
  customerName: 'Asha Rao',
  customerEmail: 'asha@example.invalid',
  customerMobile: '5550102',
  requestType: 'Property inspection',
  serviceName: 'Property inspection',
  status: 'NEW',
  priority: 'HIGH',
  amount: 1400,
  description: 'Inspect the rooftop and document any water damage.',
  requestedAt: '2026-10-08T09:00:00Z',
  scheduledAt: '2026-10-15T09:00:00Z',
  slaStatus: 'ON_TRACK',
  slaDueAt: '2026-10-16T17:00:00Z',
  statusHistory: [{ newStatus: 'NEW', changedAt: '2026-10-08T09:00:00Z' }],
};

const visitFixture = {
  visitId,
  serviceRequestId,
  assignmentId: 'assignment-e2e',
  agentId: 'agent-private-id',
  agentName: 'Ravi Kumar',
  status: 'SCHEDULED',
  scheduledAt: '2027-06-15T10:30:00Z',
  createdAt: '2026-10-08T10:00:00Z',
  assignedAt: '2026-10-08T10:00:00Z',
  assignedByName: 'Operations User',
  propertyName: 'Lakeview House',
  customerName: 'Asha Rao',
  serviceName: 'Property inspection',
  notes: 'Call the customer on arrival.',
};

interface MockOptions {
  role: 'ADMIN' | 'OPERATIONS' | 'AGENT' | 'CUSTOMER';
  failRequestOnce?: boolean;
  failSecondUploadOnce?: boolean;
  startInProgress?: boolean;
  gpsResult?: 'VERIFIED' | 'REJECTED';
}

function json(route: Route, data: unknown, status = 200) {
  return route.fulfill({
    status,
    contentType: 'application/json',
    headers: {
      'access-control-allow-origin': 'http://127.0.0.1:5173',
      'access-control-allow-credentials': 'true',
    },
    body: JSON.stringify(data),
  });
}

async function installUiApiMocks(page: Page, options: MockOptions) {
  let requestFailuresRemaining = options.failRequestOnce ? 1 : 0;
  let uploadAttempts = 0;
  let currentVisitStatus = options.startInProgress ? 'IN_PROGRESS' : 'SCHEDULED';
  let reviewStatus = 'PENDING';
  let reviewComment = '';
  const reviewActions: Array<{ status: string; comment: string }> = [];

  await page.route('http://localhost:8080/api/**', async (route) => {
    const request = route.request();
    const { pathname } = new URL(request.url());
    const method = request.method();

    if (method === 'OPTIONS') {
      return route.fulfill({
        status: 204,
        headers: {
          'access-control-allow-origin': 'http://127.0.0.1:5173',
          'access-control-allow-credentials': 'true',
          'access-control-allow-methods': 'GET,POST,PUT,PATCH,DELETE,OPTIONS',
          'access-control-allow-headers': 'authorization,content-type',
        },
      });
    }

    if (pathname === '/api/v1/auth/login' && method === 'POST') {
      return json(route, { accessToken: 'ui-test-token', tokenType: 'Bearer', expiresIn: 3600 });
    }
    if (pathname === '/api/admin/operations/dashboard' && method === 'GET') {
      return json(route, { totalRequests: 25, unassignedRequests: 3, activeRequests: 8, completedRequests: 12, failedRequests: 2, overdueInvoices: 4, failedPayments: 1 });
    }
    if (pathname === '/api/evidence/dashboard' && method === 'GET') {
      return json(route, { totalEvidence: 20, pendingValidation: 5, approved: 12, rejected: 3, missingEvidence: 0, todaysUploads: 2, byType: {}, byService: {} });
    }
    if (pathname === '/api/v1/users/me') {
      return json(route, {
        userId: 'ui-test-user',
        fullName: `Reference ${options.role}`,
        email: 'reference-ui@example.invalid',
        mobileNumber: '5550100',
        status: 'ACTIVE',
        roles: [`ROLE_${options.role}`],
      });
    }
    if (pathname === '/api/customers/me' && method === 'GET') {
      return json(route, { customerId, fullName: 'Asha Rao', email: 'asha@example.invalid', mobileNumber: '5550102' });
    }
    if (pathname === '/api/v1/properties' && method === 'GET') {
      return json(route, [{ propertyId, customerId, title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' }]);
    }
    if (pathname === '/api/v1/admin/customers' && method === 'GET') {
      return json(route, [{ customerId, fullName: 'Asha Rao', email: 'asha@example.invalid' }]);
    }
    if (pathname === '/api/v1/deals' && method === 'GET') return json(route, [dealFixture]);
    if (pathname === `/api/v1/leads/${leadId}` && method === 'GET') {
      return json(route, { leadId, source: 'Referral', propertyType: 'House', location: 'Central District', status: 'READY_FOR_DEAL', budgetMin: null, budgetMax: 300000, notes: 'Interested in a sale', contactedAt: '2026-10-08T09:00:00Z', convertedAt: null, assignedAgentId: null, assignedAgentCode: null, customerId });
    }
    if (pathname === `/api/v1/leads/${leadId}/activities` && method === 'GET') return json(route, []);
    if (pathname === `/api/v1/leads/${leadId}/service-requests` && method === 'GET') return json(route, []);
    if (pathname === `/api/v1/leads/${leadId}/deals` && method === 'GET') return json(route, [dealFixture]);
    if (pathname === `/api/v1/properties/${propertyId}` && method === 'GET') {
      return json(route, { propertyId, customerId, title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' });
    }
    if (pathname === '/api/services' && method === 'GET') {
      return json(route, [{ serviceId, serviceCode: 'INSPECT', serviceName: 'Property inspection', category: 'Inspection', requiresVisit: true, visitMandatory: true, remote: false }]);
    }
    if (pathname === '/api/v1/quotes' && method === 'GET') {
      return json(route, [{ quoteId, propertyId, propertyName: 'Lakeview House', serviceId, serviceName: 'Property inspection', status: 'ACCEPTED', totalAmount: 1400, currencyCode: 'INR', expiresAt: '2027-01-01T00:00:00Z', acceptedAt: '2026-10-08T10:00:00Z' }]);
    }
    if (pathname === `/api/v1/properties/${propertyId}/documents` && method === 'GET') return json(route, []);
    if (pathname === `/api/service-requests/customer/${customerId}` && method === 'GET') {
      return json(route, [{ ...requestFixture, propertyId, customerId, propertyTitle: 'Lakeview House', serviceName: 'Property inspection' }]);
    }
    if (pathname === '/api/service-requests' && method === 'POST') {
      return json(route, { ...requestFixture, propertyId, customerId, propertyTitle: 'Lakeview House', serviceName: 'Property inspection' }, 201);
    }
    if (pathname === `/api/service-requests/admin/${serviceRequestId}` && method === 'GET') {
      if (requestFailuresRemaining > 0) {
        requestFailuresRemaining -= 1;
        return json(route, { message: 'Temporary request detail failure' }, 500);
      }
      return json(route, requestFixture);
    }
    if (pathname === '/api/evidence/validation' && method === 'GET') {
      return json(route, reviewStatus === 'APPROVED' ? [] : [{
        evidenceId: reviewEvidenceId, serviceRequestId, visitId, evidenceTypeId: 'evidence-type-after',
        evidenceTypeCode: 'AFTER_PHOTO', evidenceTypeName: 'After photo', fileName: 'inspection-after.jpg', mediaType: 'image/jpeg',
        fileUrl: `/api/evidence/${reviewEvidenceId}/file`, validationStatus: reviewStatus,
        capturedAt: '2026-10-08T10:30:00Z', createdAt: '2026-10-08T10:30:00Z', customerName: 'Asha Rao',
        propertyName: 'Lakeview House', serviceName: 'Property inspection', agentName: 'Ravi Kumar', uploadedByName: 'Ravi Kumar',
        visitStatus: 'COMPLETED', visitScheduledAt: '2026-10-08T09:30:00Z', latitude: null, longitude: null,
        validationComment: reviewComment, validatedByName: reviewComment ? 'Operations User' : undefined,
        validatedAt: reviewComment ? '2026-10-09T10:00:00Z' : undefined,
        validationHistory: reviewComment ? [{ historyId: 'review-history-1', status: reviewStatus, comment: reviewComment, userName: 'Operations User', occurredAt: '2026-10-09T10:00:00Z' }] : [],
      }]);
    }
    if (pathname === `/api/evidence/${reviewEvidenceId}/file` && method === 'GET') {
      return route.fulfill({ status: 200, contentType: 'image/png', body: Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/l0sAAAAASUVORK5CYII=', 'base64') });
    }
    if (pathname === `/api/evidence/${reviewEvidenceId}/validation` && method === 'POST') {
      const body = request.postDataJSON() as { status: string; comment: string };
      reviewStatus = body.status;
      reviewComment = body.comment;
      reviewActions.push(body);
      return json(route, {
        evidenceId: reviewEvidenceId, serviceRequestId, visitId, evidenceTypeId: 'evidence-type-after',
        evidenceTypeCode: 'AFTER_PHOTO', evidenceTypeName: 'After photo', fileName: 'inspection-after.jpg', mediaType: 'image/jpeg',
        fileUrl: `/api/evidence/${reviewEvidenceId}/file`, validationStatus: reviewStatus, validationComment: reviewComment,
        customerName: 'Asha Rao', propertyName: 'Lakeview House', serviceName: 'Property inspection', agentName: 'Ravi Kumar',
        visitStatus: 'COMPLETED', visitScheduledAt: '2026-10-08T09:30:00Z', validationHistory: [],
      });
    }
    if (pathname === `/api/service-requests/${serviceRequestId}` && method === 'GET') {
      return json(route, { ...requestFixture, status: 'IN_PROGRESS', assignedToName: 'Ravi Kumar' });
    }
    if (pathname === '/api/service-requests/admin' && method === 'GET') {
      return json(route, [{ ...requestFixture, requestNumber: 'SR-1042' }]);
    }
    if (pathname === `/api/v1/visits/service-request/${serviceRequestId}`) {
      return json(route, [makeVisit()]);
    }
    if (pathname === '/api/v1/visits/customer' && method === 'GET') {
      return json(route, [{ ...makeVisit(), status: 'COMPLETED' }]);
    }
    if (pathname === `/api/v1/service-requests/${serviceRequestId}/assignment-history`) {
      return json(route, []);
    }
    if (pathname === '/api/v1/agents') return json(route, []);
    if (pathname === `/api/evidence/service-request/${serviceRequestId}`) return json(route, []);

    if (pathname === `/api/v1/visits/${visitId}` && method === 'GET') {
      return json(route, makeVisit());
    }
    if (pathname === '/api/v1/visits/mine') return json(route, [makeVisit()]);
    if (pathname === `/api/evidence/visit/${visitId}`) return json(route, []);
    if (pathname === `/api/evidence/visit/${visitId}/summary`) {
      return json(route, {
        visitId,
        requiredEvidenceTypes: ['BEFORE_PHOTO', 'AFTER_PHOTO', 'GPS'],
        missingEvidenceTypes: ['BEFORE_PHOTO', 'AFTER_PHOTO', 'GPS'],
        completionPercentage: 0,
        uploadedEvidence: [{
          evidenceId: 'evidence-pending', visitId, serviceRequestId, evidenceTypeId: 'evidence-type-before',
          evidenceTypeCode: 'BEFORE_PHOTO', evidenceTypeName: 'Before photo', fileUrl: 'https://example.invalid/pending.jpg',
          validationStatus: 'PENDING',
        }],
      });
    }
    if (pathname === '/api/evidence/types') {
      return json(route, [
        { evidenceTypeId: 'evidence-type-before', code: 'BEFORE_PHOTO', name: 'Before photo', category: 'Photo' },
        { evidenceTypeId: 'evidence-type-after', code: 'AFTER_PHOTO', name: 'After photo', category: 'Photo' },
      ]);
    }
    if (pathname === '/api/evidence/upload' && method === 'POST') {
      uploadAttempts += 1;
      if (options.failSecondUploadOnce && uploadAttempts === 2) {
        return json(route, { message: 'Temporary upload failure' }, 500);
      }
      return json(route, {
        evidenceId: `evidence-${uploadAttempts}`, visitId, serviceRequestId,
        evidenceTypeId: 'evidence-type-before', evidenceTypeCode: 'BEFORE_PHOTO', evidenceTypeName: 'Before photo',
        fileUrl: 'https://example.invalid/uploaded.jpg', validationStatus: 'PENDING',
      }, 201);
    }
    if (pathname === `/api/gps-verifications` && method === 'POST') {
      return json(route, {
        gpsVerificationId: 'gps-ui-test', visitId, validationStatus: options.gpsResult ?? 'VERIFIED',
        latitude: 12.9716, longitude: 77.5946, distanceFromExpectedKm: 0.042,
        capturedAt: '2026-10-09T10:00:00Z', validatedAt: '2026-10-09T10:00:01Z',
      });
    }
    if (pathname === `/api/v1/visits/${visitId}/start` && method === 'POST') {
      currentVisitStatus = 'IN_PROGRESS';
      return json(route, makeVisit());
    }
    if (pathname === `/api/v1/visits/${visitId}/complete` && method === 'POST') {
      currentVisitStatus = 'COMPLETED';
      return json(route, makeVisit());
    }

    return json(route, []);
  });

  function makeVisit() {
    return { ...visitFixture, status: currentVisitStatus,
      startedAt: currentVisitStatus === 'IN_PROGRESS' || currentVisitStatus === 'COMPLETED' ? '2026-10-09T10:00:00Z' : undefined,
      endedAt: currentVisitStatus === 'COMPLETED' ? '2026-10-09T11:00:00Z' : undefined };
  }

  return { getUploadAttempts: () => uploadAttempts, getReviewActions: () => reviewActions };
}

async function signInThroughMockedApi(page: Page, role: MockOptions['role']) {
  await page.goto('/login');
  await page.getByLabel('Work email').fill(`${role.toLowerCase()}-ui@example.invalid`);
  await page.getByLabel('Password').fill('ui-only-password');
  await page.getByRole('button', { name: 'Sign in' }).click();
  const workspace = role === 'CUSTOMER' ? '/customer/dashboard' : role === 'AGENT' ? '/agent/dashboard' : '/staff';
  await expect(page).toHaveURL(new RegExp(`${workspace.replaceAll('/', '\\/')}\\/?$`));
}

test.describe('mocked reference-screen UI E2E (API responses are intercepted; not live authorization evidence)', () => {
  test('PropertyPilot brand shell uses the production SVG assets and fits reference viewports', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'ADMIN' });
    await page.goto('/login');
    const favicon = await page.request.get('http://127.0.0.1:5173/favicon.svg');
    expect(favicon.ok()).toBeTruthy();
    expect(await favicon.text()).toContain('<svg');
    await expect(page.locator('img[src="/brand/propertypilot-logo.svg"]')).toBeVisible();
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.screenshot({ path: testInfo.outputPath('brand-login-1440x900.png'), fullPage: true });
    await signInThroughMockedApi(page, 'ADMIN');
    await expect(page.locator('img[src="/brand/propertypilot-mark-light.svg"]').first()).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Operations overview' })).toBeVisible();

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `brand shell overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`brand-shell-admin-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }
  });

  test('role navigation continues to identify Agent, Operations and Customer workspaces', async ({ page }) => {
    for (const role of ['AGENT', 'OPERATIONS', 'CUSTOMER'] as const) {
      const rolePage = await page.context().newPage();
      await installUiApiMocks(rolePage, { role });
      await signInThroughMockedApi(rolePage, role);
      const workspaceName = role === 'AGENT' ? 'Field operations workspace' : role === 'CUSTOMER' ? 'Customer portal workspace' : 'Operations workspace';
      await expect(rolePage.getByRole('group', { name: workspaceName })).toBeVisible();
      if (role === 'AGENT') {
        await expect(rolePage.getByRole('link', { name: 'My Work Queue' })).toBeVisible();
        await expect(rolePage.getByRole('link', { name: 'Users and roles' })).toHaveCount(0);
      }
      if (role === 'CUSTOMER') {
        await expect(rolePage.getByRole('link', { name: 'My Properties' })).toBeVisible();
        await expect(rolePage.getByRole('link', { name: 'Users and roles' })).toHaveCount(0);
      }
      await rolePage.close();
    }
  });

  test('Admin can follow a Lead to its Deal and see truthful transaction status and amounts', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'ADMIN' });
    await signInThroughMockedApi(page, 'ADMIN');

    await page.goto(`/staff/leads/${leadId}`);
    await expect(page.getByRole('heading', { name: 'Lead from Referral' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Lead audit trail' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'View linked deal' })).toHaveAttribute('href', `/deals/${dealId}`);
    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `Lead detail overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`lead-detail-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.goto('/deals/dashboard');
    await expect(page.getByRole('heading', { name: 'Property transaction dashboard' })).toBeVisible();
    await expect(page.getByText('Open Deals')).toBeVisible();
    await expect(page.getByText('Completed Deals')).toBeVisible();
    await expect(page.getByText(/Transaction currency is not provided by the current API/)).toBeVisible();
    await expect(page.getByText('Won Deals')).toHaveCount(0);

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `Deal dashboard overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`lead-deal-dashboard-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.goto('/deals');
    await expect(page.getByRole('heading', { name: 'Property transaction deals' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'View lead' })).toHaveAttribute('href', `/staff/leads/${leadId}`);
    await page.getByRole('textbox', { name: 'Search deals' }).fill('Asha');
    await expect(page.getByRole('link', { name: 'Property Sale' })).toBeVisible();
  });

  test('Agent cannot open the Admin-only Deal workspace route', async ({ page }) => {
    await installUiApiMocks(page, { role: 'AGENT' });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto('/deals/dashboard');
    await expect(page.getByText('Your account does not have an authorized role for this page.')).toBeVisible();
  });

  test('Operations dashboard uses API-backed metrics, opens the unassigned queue, and fits target viewports', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'OPERATIONS' });
    await signInThroughMockedApi(page, 'OPERATIONS');
    await page.goto('/staff');
    await expect(page.getByRole('heading', { name: 'Operations overview' })).toBeVisible();
    await expect(page.getByText('Evidence awaiting review').locator('..')).toContainText('5');

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `Operations dashboard overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`operations-dashboard-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByRole('link', { name: /Awaiting assignment/ }).click();
    await expect(page).toHaveURL(/\/operations\/assignments\?assignment=unassigned$/);
    await expect(page.getByRole('combobox', { name: 'Assignment' })).toContainText('Awaiting assignment');
    await expect(page.getByText('Asha Rao')).toBeVisible();
  });

  test('Operations Service Request Detail has readable context, navigation, and no horizontal overflow at all target viewports', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'OPERATIONS' });
    await signInThroughMockedApi(page, 'OPERATIONS');
    await page.goto(`/staff/service-requests/${serviceRequestId}`);

    await expect(page.getByRole('heading', { name: 'Property inspection' })).toBeVisible();
    await expect(page.getByText('Asha Rao · Lakeview House · Property inspection', { exact: true })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Request details', exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Assign Agent', exact: true })).toBeVisible();
    await expect(page.getByText('Inspect the rooftop and document any water damage.')).toBeVisible();

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      await expect(page.getByRole('heading', { name: 'Request details', exact: true })).toBeVisible();
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `horizontal overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`operations-request-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByText('Back to Requests', { exact: true }).click();
    await expect(page).toHaveURL(/\/staff\/service-requests$/);
  });

  test('Operations Service Request Detail exposes retryable API errors', async ({ page }) => {
    await installUiApiMocks(page, { role: 'OPERATIONS', failRequestOnce: true });
    await signInThroughMockedApi(page, 'OPERATIONS');
    await page.goto(`/staff/service-requests/${serviceRequestId}`);
    await expect(page.getByRole('alert').filter({ hasText: '500' })).toBeVisible();
    await page.getByRole('button', { name: 'Retry' }).click();
    await expect(page.getByRole('heading', { name: 'Property inspection' })).toBeVisible();
  });

  test('Customer is blocked by the frontend staff-route guard (mocked identity only)', async ({ page }) => {
    await installUiApiMocks(page, { role: 'CUSTOMER' });
    await signInThroughMockedApi(page, 'CUSTOMER');
    await page.goto(`/staff/service-requests/${serviceRequestId}`);
    await expect(page.getByText('Your account does not have an authorized role for this page.')).toBeVisible();
  });

  test('Agent and Customer roles see the Operations evidence review role guard (mocked identities)', async ({ page }) => {
    await installUiApiMocks(page, { role: 'AGENT' });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto('/evidence/validation');
    await expect(page.getByText('Your account does not have an authorized role for this page.')).toBeVisible();

    await installUiApiMocks(page, { role: 'CUSTOMER' });
    await signInThroughMockedApi(page, 'CUSTOMER');
    await page.goto('/evidence/validation');
    await expect(page.getByText('Your account does not have an authorized role for this page.')).toBeVisible();
  });

  test('Operations evidence review confirms decisions, records comments, and links request and visit context', async ({ page }, testInfo) => {
    const uiApi = await installUiApiMocks(page, { role: 'OPERATIONS' });
    await signInThroughMockedApi(page, 'OPERATIONS');
    await page.goto(`/evidence/validation?evidenceId=${reviewEvidenceId}`);
    await expect(page.getByRole('heading', { name: 'Evidence review' })).toBeVisible();
    await expect(page.getByText('Asha Rao', { exact: true })).toBeVisible();
    await expect(page.getByText('Lakeview House', { exact: true })).toBeVisible();
    await expect(page.getByText('NEW', { exact: true })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Open service request' })).toHaveAttribute('href', `/service-requests/${serviceRequestId}`);
    await page.getByRole('textbox', { name: 'Review comments' }).fill('Photo is clear and relevant to the work.');
    await page.getByRole('button', { name: 'Approve', exact: true }).click();
    const reviewDialog = page.getByRole('dialog', { name: 'Approve evidence' });
    await expect(reviewDialog).toBeVisible();
    await expect(page.getByText(/may complete the service request after validating/)).toBeVisible();
    await page.keyboard.press('Tab');
    const keyboardFocus = await page.evaluate(() => {
      const active = document.activeElement;
      const dialog = document.querySelector('[role="dialog"]');
      const style = active ? getComputedStyle(active) : null;
      return { trapped: Boolean(active && dialog?.contains(active)), outline: style?.outlineStyle, shadow: style?.boxShadow };
    });
    expect(keyboardFocus.trapped).toBe(true);
    expect(keyboardFocus.outline !== 'none' || keyboardFocus.shadow !== 'none').toBe(true);
    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `evidence review overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`operations-evidence-review-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }
    await page.getByRole('button', { name: 'Approve evidence' }).click();
    await expect(page.getByText('Approve evidence saved.')).toBeVisible();
    expect(uiApi.getReviewActions()).toEqual([{ status: 'APPROVED', comment: 'Photo is clear and relevant to the work.' }]);
    await expect(page.getByText('The review queue is clear.')).toBeVisible();
    await expect(page.getByText('APPROVED', { exact: true })).toBeVisible();
    await expect(page.getByText('NEW', { exact: true })).toBeVisible();
    await page.getByRole('link', { name: 'Open service request' }).click();
    await expect(page).toHaveURL(new RegExp(`/service-requests/${serviceRequestId}$`));
    await expect(page.getByRole('heading', { name: 'Request details', exact: true })).toBeVisible();
  });

  test('Agent Visit Execution has readable context and no horizontal overflow at all target viewports', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'AGENT' });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto(`/agent/visits/${visitId}`);
    await expect(page.getByRole('heading', { name: 'Visit 1' })).toBeVisible();
    await expect(page.getByText('Asha Rao · Lakeview House · Property inspection', { exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Start Visit' })).toBeVisible();
    await expect(page.getByText('Evidence checklist')).toBeVisible();

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      await expect(page.getByRole('button', { name: 'Start Visit' })).toBeVisible();
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `horizontal overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`agent-visit-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }
  });

  test('Agent can perform permitted visit transitions and the UI distinguishes visit from request completion', async ({ page }) => {
    await installUiApiMocks(page, { role: 'AGENT' });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto(`/agent/visits/${visitId}`);
    await page.getByRole('button', { name: 'Start Visit' }).click();
    await expect(page.getByText('Visit started.')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Complete Visit' })).toBeVisible();
    await page.getByRole('button', { name: 'Complete Visit' }).click();
    await expect(page.getByText(/Visit marked complete\. Service request completion is evaluated separately/)).toBeVisible();
  });

  test('Agent reschedule dialog supports keyboard focus and protects unsaved changes', async ({ page }) => {
    await installUiApiMocks(page, { role: 'AGENT' });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto(`/agent/visits/${visitId}`);
    const reschedule = page.getByRole('button', { name: 'Reschedule' });
    await reschedule.focus();
    await page.keyboard.press('Enter');
    const dialog = page.getByRole('dialog', { name: 'Reschedule visit' });
    await expect(dialog).toBeVisible();
    await page.keyboard.press('Tab');
    expect(await dialog.evaluate((element) => element.contains(document.activeElement))).toBe(true);
    await page.getByRole('textbox', { name: 'Reason' }).fill('Customer requested another time');
    await page.getByRole('button', { name: 'Close' }).click();
    await expect(page.getByText('Unsaved schedule changes will be lost.')).toBeVisible();
    await page.getByRole('button', { name: 'Keep editing' }).click();
    await expect(page.getByRole('textbox', { name: 'Reason' })).toHaveValue('Customer requested another time');
  });

  test('Agent evidence batch retry retains only failed files and leaves uploads pending review', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'AGENT', startInProgress: true, failSecondUploadOnce: true });
    const filenames: string[] = [];
    await page.route('http://localhost:8080/api/evidence/upload', async (route) => {
      const request = route.request();
      const body = (await request.postDataBuffer())?.toString('utf8') ?? '';
      const filename = body.match(/filename="([^"]+)"/)?.[1] ?? `upload-${filenames.length + 1}`;
      filenames.push(filename);
      if (filenames.length === 2) return json(route, { message: 'Temporary upload failure' }, 500);
      return json(route, {
        evidenceId: `evidence-${filenames.length}`, visitId, serviceRequestId,
        evidenceTypeId: 'evidence-type-before', evidenceTypeCode: 'BEFORE_PHOTO', evidenceTypeName: 'Before photo',
        fileUrl: 'https://example.invalid/uploaded.jpg', validationStatus: 'PENDING',
      }, 201);
    });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto(`/agent/visits/${visitId}`);
    await page.getByLabel('Evidence type').click();
    await page.getByRole('option', { name: /Before photo.*Photo/ }).click();
    await page.getByLabel('Select files').setInputFiles([
      { name: 'before-one.png', mimeType: 'image/png', buffer: Buffer.from('image-one') },
      { name: 'before-two.png', mimeType: 'image/png', buffer: Buffer.from('image-two') },
    ]);
    await page.getByRole('button', { name: 'Upload evidence' }).click();
    await expect(page.getByText(/1 earlier file was uploaded successfully; retry will continue with the remaining files/)).toBeVisible();
    await page.getByRole('button', { name: 'Upload evidence' }).click();
    await expect(page.getByText('1 file uploaded and sent for validation.')).toBeVisible();
    await expect(page.getByText(/Uploaded.*awaiting review/)).toBeVisible();
    expect(filenames).toEqual(['before-one.png', 'before-two.png', 'before-two.png']);
    expect(filenames).toHaveLength(3);
    expect(await page.getByRole('button', { name: 'Approve evidence' }).count()).toBe(0);
    await page.screenshot({ path: testInfo.outputPath('agent-evidence-pending-review.png'), fullPage: true });
  });

  test('GPS UI displays a result only after the mocked verification endpoint responds', async ({ page }) => {
    await installUiApiMocks(page, { role: 'AGENT', startInProgress: true, gpsResult: 'VERIFIED' });
    await page.context().grantPermissions(['geolocation']);
    await page.context().setGeolocation({ latitude: 12.9716, longitude: 77.5946 });
    await signInThroughMockedApi(page, 'AGENT');
    await page.goto(`/agent/visits/${visitId}`);
    await expect(page.getByText('No location result has been captured in this screen session.')).toBeVisible();
    await page.getByRole('button', { name: 'Verify current location' }).click();
    await expect(page.getByText('Location verified for this visit.')).toBeVisible();
  });

  test('Customer properties and property detail stay readable and carry property context into service creation', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'CUSTOMER' });
    await signInThroughMockedApi(page, 'CUSTOMER');
    await page.goto('/customer/properties');
    await expect(page.getByRole('heading', { name: 'My properties' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'View Lakeview House' })).toBeVisible();

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `customer property list overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`customer-properties-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByRole('link', { name: 'View Lakeview House' }).click();
    await expect(page.getByRole('heading', { name: 'Property overview' })).toBeVisible();
    await expect(page.getByText('Property inspection').first()).toBeVisible();
    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `customer property detail overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`customer-property-detail-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByRole('link', { name: 'Request a service for this property' }).click();
    await expect(page).toHaveURL(new RegExp(`propertyId=${propertyId}`));
    await expect(page.getByRole('combobox', { name: 'Property' })).toContainText('Lakeview House');
  });

  test('Customer can search requests and track request progress, visits, and pending evidence', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'CUSTOMER' });
    await signInThroughMockedApi(page, 'CUSTOMER');
    await page.goto('/customer/service-requests');
    await expect(page.getByRole('heading', { name: 'My service requests' })).toBeVisible();
    await expect(page.getByText('Property inspection')).toBeVisible();
    await page.getByRole('textbox', { name: 'Search requests' }).fill('Lakeview');
    await expect(page.getByRole('link', { name: /Property inspection for Lakeview House/ })).toBeVisible();

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `customer request list overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`customer-request-list-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByRole('link', { name: /Property inspection for Lakeview House/ }).click();
    await expect(page.getByRole('heading', { name: 'Property inspection' })).toBeVisible();
    await expect(page.getByText(/A completed visit does not by itself complete this service request/)).toBeVisible();
    await expect(page.getByText(/Evidence will be shown here when shared/)).toBeVisible();
    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `customer request detail overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`customer-request-detail-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }
  });

  test('Customer service request submission uses an accepted quote, prevents repeat submit, and shows the server result', async ({ page }, testInfo) => {
    await installUiApiMocks(page, { role: 'CUSTOMER' });
    let submittedBody: unknown;
    await page.route('http://localhost:8080/api/service-requests', async (route) => {
      submittedBody = route.request().postDataJSON();
      await new Promise((resolve) => setTimeout(resolve, 300));
      return json(route, { ...requestFixture, propertyId: 'property-private-id', customerId: 'customer-private-id', propertyTitle: 'Lakeview House', serviceName: 'Property inspection' }, 201);
    });
    await signInThroughMockedApi(page, 'CUSTOMER');
    await page.goto(`/customer/service-requests/new?propertyId=${propertyId}`);
    await expect(page.getByRole('combobox', { name: 'Property' })).toContainText('Lakeview House');

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const widths = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(widths.document, `customer request form overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(widths.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`customer-request-form-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }

    await page.getByRole('combobox', { name: 'Service' }).click();
    await page.getByRole('option', { name: /Property inspection/ }).click();
    await page.getByRole('combobox', { name: 'Accepted quote' }).click();
    await page.getByRole('option', { name: /Property inspection.*1,400/ }).click();
    await page.getByRole('textbox', { name: 'Service details' }).fill('Inspect the roof and document any damage.');
    await page.getByRole('button', { name: 'Submit service request' }).click();
    await expect(page.getByRole('button', { name: 'Submitting request…' })).toBeDisabled();
    await expect(page.getByRole('heading', { name: 'Service request submitted' })).toBeVisible();
    expect(submittedBody).toMatchObject({ quoteId, priority: 'MEDIUM', description: 'Inspect the roof and document any damage.' });
    await expect(page.getByRole('link', { name: 'View service request' })).toHaveAttribute('href', `/customer/service-requests/${serviceRequestId}`);
  });
});
