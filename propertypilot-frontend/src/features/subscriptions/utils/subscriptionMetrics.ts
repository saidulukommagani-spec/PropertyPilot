import type { CustomerSubscription, PlanVersion } from '../types/subscription';

function toDateOnly(date: Date): string {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 10);
}

export function getRenewalWindows(today = new Date()): { dueBy: string; expiringBy: string; today: string } {
  const start = new Date(today);
  start.setHours(0, 0, 0, 0);
  const due = new Date(start);
  due.setDate(due.getDate() + 30);
  const expiring = new Date(start);
  expiring.setDate(expiring.getDate() + 60);
  return { today: toDateOnly(start), dueBy: toDateOnly(due), expiringBy: toDateOnly(expiring) };
}

export function formatActiveSubscriptionRevenue(
  subscriptions: CustomerSubscription[],
  versions: PlanVersion[],
): string {
  const activeVersionIds = new Set(subscriptions.map((subscription) => subscription.planVersionId));
  const totals = new Map<string, number>();
  for (const version of versions) {
    if (!activeVersionIds.has(version.planVersionId)) continue;
    const currency = version.currencyCode.trim().toUpperCase() || 'UNKNOWN';
    totals.set(currency, (totals.get(currency) ?? 0) + version.price);
  }
  return [...totals.entries()]
    .sort(([left], [right]) => left.localeCompare(right))
    .map(([currency, amount]) => `${currency} ${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`)
    .join(' · ') || '—';
}

export function isWithinDateWindow(date: string | undefined, from: string, through: string): boolean {
  return Boolean(date && date >= from && date <= through);
}

export function countSubscriptionsInDateWindow(
  subscriptions: CustomerSubscription[],
  from: string,
  through: string,
): number {
  return subscriptions.filter((subscription) => isWithinDateWindow(subscription.endDate, from, through)).length;
}
