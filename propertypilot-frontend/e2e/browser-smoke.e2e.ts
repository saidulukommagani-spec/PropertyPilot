import { expect, test } from '@playwright/test';

test('browser smoke: the frontend renders the sign-in page', async ({ page }, testInfo) => {
  const pageErrors: string[] = [];
  page.on('pageerror', (error) => pageErrors.push(error.message));

  await page.goto('/login');
  await expect(page.getByRole('heading', { name: 'Sign in' })).toBeVisible();
  await expect(page.getByLabel('Work email')).toBeVisible();
  await expect(page.getByLabel('Password')).toBeVisible();
  await page.screenshot({ path: testInfo.outputPath('login-smoke.png'), fullPage: true });
  expect(pageErrors).toEqual([]);
});
