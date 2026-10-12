import { Alert, Button } from '@mui/material';
import type { ReactNode } from 'react';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

export function CustomerPageHeading({ title, subtitle, action }: { title: string; subtitle: string; action?: ReactNode }) {
  return <DashboardPageHeading eyebrow="Customer overview" title={title} subtitle={subtitle} action={action} />;
}
export function CustomerMetricCards({ items, loading }: { items: Array<{ label: string; value: number | string }>; loading: boolean }) {
  return <DashboardMetricCards items={items} loading={loading} />;
}
export function CustomerQueryError({ error, retry }: { error: unknown; retry: () => void }) {
  return <Alert severity="error" action={<Button color="inherit" size="small" onClick={retry}>Retry</Button>}>{error instanceof Error ? error.message : 'We could not load this information.'}</Alert>;
}
