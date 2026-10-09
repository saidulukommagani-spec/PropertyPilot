import { describe, expect, it } from 'vitest';
import type { CustomerSubscription, PlanVersion } from '../types/subscription';
import { countSubscriptionsInDateWindow, formatActiveSubscriptionRevenue, getRenewalWindows } from './subscriptionMetrics';

describe('subscription dashboard metrics', () => {
  it('counts all subscriptions due in the window regardless of renewal preference', () => {
    const subscriptions = [
      subscription('a', '2026-10-20', false),
      subscription('b', '2026-11-10'),
      subscription('c', '2026-12-20'),
    ];

    expect(countSubscriptionsInDateWindow(subscriptions, '2026-10-08', '2026-11-07')).toBe(1);
    expect(countSubscriptionsInDateWindow(subscriptions, '2026-10-08', '2026-12-07')).toBe(2);
  });

  it('keeps estimated recurring totals separate for each currency', () => {
    const subscriptions = [
      subscription('a', undefined, false, 'inr-a'),
      subscription('b', undefined, false, 'inr-b'),
      subscription('c', undefined, false, 'usd-a'),
    ];
    const versions = [
      version('inr-a', 1000, 'INR'),
      version('inr-b', 500, 'INR'),
      version('usd-a', 25, 'USD'),
      version('unused', 9000, 'INR'),
    ];

    expect(formatActiveSubscriptionRevenue(subscriptions, versions)).toBe('INR 1,500.00 · USD 25.00');
  });

  it('treats date-only renewal windows inclusively across month boundaries', () => {
    const windows = getRenewalWindows(new Date(2026, 0, 31, 16, 30));

    expect(windows).toEqual({ today: '2026-01-31', dueBy: '2026-03-02', expiringBy: '2026-04-01' });
  });
});

function subscription(id: string, endDate?: string, autoRenew = true, planVersionId = id): CustomerSubscription {
  return { customerSubscriptionId: id, customerId: 'customer', planVersionId, status: 'ACTIVE', startDate: '2026-01-01', endDate, autoRenew };
}

function version(planVersionId: string, price: number, currencyCode: string): PlanVersion {
  return { planVersionId, subscriptionPlanId: 'plan', versionNumber: 1, price, currencyCode, effectiveFrom: '2026-01-01', status: 'ACTIVE' };
}
