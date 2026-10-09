import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import SubscriptionPlanDetailsPage from './SubscriptionPlanDetailsPage';

const state = vi.hoisted(() => ({
  plan: { subscriptionPlanId: 'plan-id', planCode: 'BASIC', planName: 'Basic', description: 'Basic plan', status: 'ACTIVE' },
  versions: [{ planVersionId: 'version-id', subscriptionPlanId: 'plan-id', versionNumber: 1, price: 0, currencyCode: 'INR', effectiveFrom: '2026-01-01T00:00:00Z', status: 'ACTIVE' }],
  entitlements: [{ entitlementId: 'entitlement-id', planVersionId: 'version-id', serviceId: 'service-internal-uuid', quantity: 2, periodType: 'MONTHLY', carryForwardAllowed: false, status: 'ACTIVE' }],
}));

vi.mock('../hooks/useSubscriptions', () => ({
  useSubscriptionPlan: () => ({ data: state.plan, isLoading: false, isError: false, refetch: vi.fn() }),
  useSubscriptionCatalog: () => ({
    versions: { data: state.versions, isLoading: false, isError: false, refetch: vi.fn() },
    entitlements: { data: state.entitlements, isLoading: false, isError: false, refetch: vi.fn() },
    benefits: { data: [], isLoading: false, isError: false, refetch: vi.fn() },
    services: { data: [], isLoading: false, isError: false, refetch: vi.fn() },
  }),
  useAddOnCatalog: () => ({ data: [], isLoading: false, isError: false, refetch: vi.fn() }),
  useCustomerId: () => ({ data: null, isLoading: false, isError: false, refetch: vi.fn() }),
  useSubscriptionRole: () => ({ isCustomer: false, isAdmin: false, isOperations: false }),
  useCreateSubscription: () => ({ isPending: false, isError: false, isSuccess: false, mutateAsync: vi.fn() }),
}));

afterEach(cleanup);

describe('SubscriptionPlanDetailsPage', () => {
  it('does not expose an internal service UUID when the service label is unavailable', () => {
    const { container } = render(<MemoryRouter initialEntries={['/subscriptions/plans/plan-id']}>
      <Routes><Route path="/subscriptions/plans/:id" element={<SubscriptionPlanDetailsPage />} /></Routes>
    </MemoryRouter>);

    expect(screen.getByText('Included service')).toBeTruthy();
    expect(container.textContent).not.toContain('service-internal-uuid');
  });
});
