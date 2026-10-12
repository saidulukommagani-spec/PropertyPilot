import { Alert, Button, Card, CardContent, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useOperationsDashboard } from '../hooks/useOperationsDashboard';
import { useEvidenceDashboard } from '../../evidence/hooks/useEvidence';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

const metrics = [
  { key: 'totalRequests', label: 'Service requests', description: 'All requests in the operations queue', to: '/service-requests', action: 'Open requests' },
  { key: 'unassignedRequests', label: 'Awaiting assignment', description: 'New and pending-assignment requests', to: '/operations/assignments?assignment=unassigned', action: 'Assign work' },
  { key: 'activeRequests', label: 'Active requests', description: 'Assigned, accepted, or in progress', to: '/service-requests', action: 'Open requests' },
  { key: 'completedRequests', label: 'Completed requests', description: 'Requests that passed completion', to: '/service-requests?status=COMPLETED', action: 'Open completed' },
  { key: 'failedRequests', label: 'Failed requests', description: 'Requests requiring operational review', to: '/service-requests?status=FAILED', action: 'Review failed requests', accent: 'error' },
  { key: 'overdueInvoices', label: 'Overdue invoices', description: 'Invoices currently past due', to: '/invoices', action: 'Review invoices' },
  { key: 'failedPayments', label: 'Failed payments', description: 'Payments requiring reconciliation', to: '/payments', action: 'Review payments', accent: 'error' },
] as const;

export default function AdminDashboardPage() {
  const query = useOperationsDashboard();
  const evidence = useEvidenceDashboard();

  return <Stack spacing={{ xs: 2, md: 2.5 }}>
    <DashboardPageHeading eyebrow="Operations at a glance" title="Operations Overview" subtitle="Current service delivery and revenue exceptions from PropertyPilot." />
    {query.isError && <Alert severity="error" action={<Button color="inherit" onClick={() => void query.refetch()}>Retry</Button>}>
      {query.error instanceof Error ? query.error.message : 'Operations summary could not be loaded.'}
    </Alert>}
    {evidence.isError && <Alert severity="error" action={<Button color="inherit" onClick={() => void evidence.refetch()}>Retry</Button>}>
      {evidence.error instanceof Error ? evidence.error.message : 'Evidence review summary could not be loaded.'}
    </Alert>}
    <DashboardMetricCards columns={{ xs: 1, sm: 2, lg: 3, xl: 4 }} loading={query.isLoading} items={[
      ...metrics.map((metric) => ({ ...metric, value: query.data?.[metric.key] ?? '—' })),
      { label: 'Evidence awaiting review', value: evidence.isError ? '—' : evidence.data?.pendingValidation ?? 0, description: 'Evidence submissions in the backend review queue', action: 'Review evidence', to: '/evidence/validation', accent: evidence.data?.pendingValidation ? 'warning' as const : 'teal' as const },
    ]} />
    <Card variant="outlined"><CardContent sx={{ p: { xs: 2.25, md: 3 } }}>
      <Typography variant="h5">Operational workspaces</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>Open the queue that needs attention.</Typography>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.25} sx={{ mt: 2.5 }}>
        <Button variant="outlined" component={Link} to="/service-requests">Service requests</Button>
        <Button variant="outlined" component={Link} to="/visits">Visits</Button>
        <Button variant="outlined" component={Link} to="/evidence">Evidence</Button>
        <Button variant="outlined" component={Link} to="/operations/assignments">Agent assignments</Button>
      </Stack>
    </CardContent></Card>
  </Stack>;
}
