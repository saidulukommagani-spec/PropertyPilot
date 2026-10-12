import { Alert, Button } from '@mui/material';
import type { ReactNode } from 'react';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

export function MetricCards({ items, loading }: { items: Array<{ label: string; value: number | string }>; loading: boolean }) {
  return <DashboardMetricCards items={items} loading={loading} />;
}

export function QueryFailure({ message, retry }: { message: string; retry: () => void }) {
  return <Alert severity="error" action={<Button color="inherit" size="small" onClick={retry}>Retry</Button>}>{message}</Alert>;
}

export function PageHeading({ title, subtitle, action }: { title: string; subtitle: string; action?: ReactNode }) {
  return <DashboardPageHeading eyebrow="Field operations" title={title} subtitle={subtitle} action={action} />;
}
