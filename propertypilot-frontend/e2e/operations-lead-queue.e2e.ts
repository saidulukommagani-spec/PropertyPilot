import { expect, test, type Page, type Route } from '@playwright/test';

const leadId = '00000000-0000-4000-8000-000000000901';
const leadRow = {
  leadId,
  source: 'Website enquiry',
  propertyType: 'Apartment',
  location: 'Central District',
  status: 'QUALIFIED',
  customerName: 'Mira Chen',
  customerEmail: 'mira@example.invalid',
  customerPhone: '5550101',
  assignedAgentCode: 'AGT-12',
  createdAt: '2026-10-08T09:30:00Z',
};
const pageResponse = (content = [leadRow], page = 0, size = 25, totalElements = content.length) => ({
  content, page, size, totalElements, totalPages: Math.ceil(totalElements / size), first: page === 0,
  last: page >= Math.ceil(totalElements / size) - 1,
});
const viewports = [
  { width: 360, height: 800 },
  { width: 390, height: 844 },
  { width: 768, height: 1024 },
  { width: 1440, height: 900 },
];

interface QueueMockOptions {
  role: 'ADMIN' | 'OPERATIONS' | 'AGENT' | 'CUSTOMER';
  empty?: boolean;
  failOnce?: boolean;
}

async function json(route: Route, data: unknown, status = 200) {
  await route.fulfill({
    status,
    contentType: 'application/json',
    headers: {
      'access-control-allow-origin': 'http://127.0.0.1:5173',
      'access-control-allow-credentials': 'true',
    },
    body: JSON.stringify(data),
  });
}

async function installQueueMocks(page: Page, options: QueueMockOptions) {
  let failCount = options.failOnce ? 1 : 0;
  const searchRequests: URL[] = [];

  await page.route('http://localhost:8080/api/**', async (route) => {
    const request = route.request();
    const url = new URL(request.url());
    const method = request.method();
    if (method === 'OPTIONS') {
      await route.fulfill({ status: 204, headers: { 'access-control-allow-origin': 'http://127.0.0.1:5173', 'access-control-allow-credentials': 'true', 'access-control-allow-methods': 'GET,POST,PUT,PATCH,DELETE,OPTIONS', 'access-control-allow-headers': 'authorization,content-type' } });
      return;
    }
    if (url.pathname === '/api/v1/auth/login' && method === 'POST') {
      await json(route, { accessToken: 'ui-test-token', tokenType: 'Bearer', expiresIn: 3600 });
      return;
    }
    if (url.pathname === '/api/v1/users/me') {
      await json(route, { userId: 'queue-test-user', fullName: `Reference ${options.role}`, email: 'queue-user@example.invalid', mobileNumber: '5550100', status: 'ACTIVE', roles: [`ROLE_${options.role}`] });
      return;
    }
    if (url.pathname === '/api/v1/leads' && method === 'GET') {
      searchRequests.push(url);
      if (failCount > 0) {
        failCount -= 1;
        await json(route, { message: 'server internals are not rendered' }, 503);
        return;
      }
      const search = url.searchParams.get('search')?.toLowerCase() ?? '';
      const status = url.searchParams.get('status');
      const page = Number(url.searchParams.get('page') ?? 0);
      const size = Number(url.searchParams.get('size') ?? 25);
      const matches = !options.empty && (!search || `${leadRow.customerName} ${leadRow.customerEmail} ${leadRow.source} ${leadRow.location}`.toLowerCase().includes(search)) && (!status || status === leadRow.status);
      const rows = matches ? [leadRow] : [];
      const total = matches ? 37 : 0;
      const offset = page * size;
      const data = matches ? (offset >= total ? [] : rows) : [];
      await json(route, pageResponse(data, page, size, total));
      return;
    }
    if (url.pathname === `/api/v1/leads/${leadId}` && method === 'GET') {
      await json(route, { ...leadRow, budgetMin: null, budgetMax: 400000, notes: 'Internal note excluded from the queue', contactedAt: '2026-10-08T09:00:00Z', convertedAt: null, assignedAgentId: 'private-agent-id', customerId: 'private-customer-id' });
      return;
    }
    if (url.pathname === `/api/v1/leads/${leadId}/activities` && method === 'GET') {
      await json(route, []);
      return;
    }
    if (url.pathname === `/api/v1/leads/${leadId}/service-requests` && method === 'GET') {
      await json(route, []);
      return;
    }
    await json(route, {}, 404);
  });

  return { searchRequests };
}

async function signIn(page: Page, role: QueueMockOptions['role']) {
  await page.goto('/login');
  await page.getByLabel('Work email').fill(`${role.toLowerCase()}@example.invalid`);
  await page.getByLabel('Password').fill('ui-only-password');
  await page.getByRole('button', { name: 'Sign in' }).click();
  const landingPath = role === 'AGENT' ? '/agent/dashboard' : role === 'CUSTOMER' ? '/customer/dashboard' : '/staff';
  await expect(page).toHaveURL(new RegExp(`${landingPath.replaceAll('/', '\\/')}\\/?$`));
}

test.describe('mocked Operations Lead queue UI (not live authorization evidence)', () => {
  test('Operations navigation opens the queue and the responsive record context fits all target viewports', async ({ page }, testInfo) => {
    await installQueueMocks(page, { role: 'OPERATIONS' });
    await signIn(page, 'OPERATIONS');
    await page.getByRole('link', { name: 'Lead queue' }).click();

    await expect(page.getByRole('heading', { name: 'Lead queue' })).toBeVisible();
    await expect(page.getByRole('table', { name: 'Operations lead queue' }).getByText('Mira Chen')).toBeVisible();
    await expect(page.getByRole('table', { name: 'Operations lead queue' }).getByText('mira@example.invalid')).toBeVisible();
    await expect(page.getByRole('table', { name: 'Operations lead queue' }).getByText('Central District')).toBeVisible();
    await expect(page.getByText(leadId)).toHaveCount(0);
    await expect(page.getByText('Internal note excluded from the queue')).toHaveCount(0);

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      const dimensions = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: window.innerWidth }));
      expect(dimensions.document, `queue overflow at ${viewport.width}x${viewport.height}`).toBeLessThanOrEqual(dimensions.viewport + 1);
      await page.screenshot({ path: testInfo.outputPath(`operations-lead-queue-${viewport.width}x${viewport.height}.png`), fullPage: true });
    }
  });

  test('search, status, sorting and pagination use server-supported parameters', async ({ page }) => {
    const mocks = await installQueueMocks(page, { role: 'ADMIN' });
    await signIn(page, 'ADMIN');
    await page.goto('/operations/leads');
    await expect(page.getByRole('heading', { name: 'Lead queue' })).toBeVisible();

    await page.getByRole('textbox', { name: 'Search leads' }).fill('Mira');
    await expect.poll(() => mocks.searchRequests.some((url) => url.searchParams.get('search') === 'Mira')).toBeTruthy();
    await page.getByRole('combobox', { name: 'Lead status filter' }).click();
    await page.getByRole('option', { name: 'QUALIFIED' }).click();
    await expect.poll(() => mocks.searchRequests.some((url) => url.searchParams.get('status') === 'QUALIFIED')).toBeTruthy();
    await page.getByRole('button', { name: 'Created date' }).click();
    await expect.poll(() => mocks.searchRequests.some((url) => url.searchParams.get('sort') === 'createdAt' && url.searchParams.get('direction') === 'ASC')).toBeTruthy();
    await page.getByRole('button', { name: 'Go to next page' }).click();
    await expect.poll(() => mocks.searchRequests.some((url) => url.searchParams.get('page') === '1' && url.searchParams.get('status') === 'QUALIFIED')).toBeTruthy();
  });

  test('empty and recoverable error responses render distinct states and retry', async ({ page }) => {
    await installQueueMocks(page, { role: 'OPERATIONS', empty: true });
    await signIn(page, 'OPERATIONS');
    await page.goto('/operations/leads');
    await expect(page.getByText('No leads have been added yet.')).toBeVisible();

    await page.unrouteAll({ behavior: 'wait' });
    await installQueueMocks(page, { role: 'OPERATIONS', failOnce: true });
    await page.goto('/operations/leads');
    await expect(page.getByText('The lead queue could not be loaded. Retry to try again.')).toBeVisible();
    await page.getByRole('button', { name: 'Retry' }).click();
    await expect(page.getByRole('table', { name: 'Operations lead queue' }).getByText('Mira Chen')).toBeVisible();
  });

  test('records open Operations-context details and restricted roles do not receive queue access', async ({ page }) => {
    await installQueueMocks(page, { role: 'OPERATIONS' });
    await signIn(page, 'OPERATIONS');
    await page.goto('/operations/leads');
    await page.getByRole('link', { name: 'Open lead' }).click();
    await expect(page).toHaveURL(new RegExp(`/operations/leads/${leadId}$`));
    await expect(page.getByRole('heading', { name: 'Lead from Website enquiry' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Back to lead queue' })).toHaveAttribute('href', '/operations/leads');
    await expect(page.getByRole('heading', { name: 'Lead status' })).toHaveCount(0);
    await expect(page.getByRole('button', { name: 'Convert to deal' })).toHaveCount(0);

    await page.unrouteAll({ behavior: 'wait' });
    await installQueueMocks(page, { role: 'AGENT' });
    await page.evaluate(() => window.sessionStorage.clear());
    await signIn(page, 'AGENT');
    await page.goto('/operations/leads');
    await expect(page.getByText('Your account does not have an authorized role for this page.')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Lead queue' })).toHaveCount(0);
  });
});
