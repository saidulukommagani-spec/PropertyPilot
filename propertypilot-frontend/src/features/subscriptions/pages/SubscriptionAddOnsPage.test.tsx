import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import SubscriptionAddOnsPage from './SubscriptionAddOnsPage';

const mocks = vi.hoisted(() => ({
  catalog: [{ subscriptionAddOnId: 'addon-paid', serviceId: 'service-1', serviceName: 'Inspection', name: 'Priority inspection', price: 500, status: 'ACTIVE' }],
  subscriptions: [{ customerSubscriptionId: 'subscription-1', customerId: 'customer-1', planVersionId: 'version-1', planName: 'Essential', status: 'ACTIVE', startDate: '2026-01-01', autoRenew: false }],
  purchase: vi.fn(),
}));

vi.mock('@tanstack/react-query', () => ({ useQueries: () => [] }));
vi.mock('../hooks/useSubscriptions', () => ({
  useAddOnCatalog: () => ({ data: mocks.catalog, isLoading: false, isError: false, refetch: vi.fn() }),
  usePurchaseAddOn: () => ({ mutateAsync: mocks.purchase, isPending: false, isError: false }),
  useSubscriptions: () => ({ subscriptions: { data: mocks.subscriptions, isLoading: false, isError: false, refetch: vi.fn() } }),
}));

afterEach(() => cleanup());

it('does not offer paid add-ons for activation without confirmed payment', () => {
  render(<SubscriptionAddOnsPage />);

  expect(screen.getByText(/Paid add-on checkout is not available yet/)).toBeTruthy();
  expect(screen.getByRole('button', { name: 'Add an add-on' }).hasAttribute('disabled')).toBe(true);
});
