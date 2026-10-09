import { Alert, Box, Button, Card, CardActionArea, CardContent, Grid, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useOperationsDashboard } from '../hooks/useOperationsDashboard';
import { useEvidenceDashboard } from '../../evidence/hooks/useEvidence';

const metrics = [
  { key: 'totalRequests', label: 'Service requests', description: 'All requests in the operations queue', to: '/service-requests', action: 'Open requests' },
  { key: 'unassignedRequests', label: 'Awaiting assignment', description: 'New and pending-assignment requests', to: '/operations/assignments?assignment=unassigned', action: 'Assign work' },
  { key: 'activeRequests', label: 'Active requests', description: 'Assigned, accepted, or in progress', to: '/service-requests', action: 'Open requests' },
  { key: 'completedRequests', label: 'Completed requests', description: 'Requests that passed completion', to: '/service-requests?status=COMPLETED', action: 'Open completed' },
  { key: 'failedRequests', label: 'Failed requests', description: 'Requests requiring operational review', to: '/service-requests?status=FAILED', action: 'Review failed requests' },
  { key: 'overdueInvoices', label: 'Overdue invoices', description: 'Invoices currently past due', to: '/invoices', action: 'Review invoices' },
  { key: 'failedPayments', label: 'Failed payments', description: 'Payments requiring reconciliation', to: '/payments', action: 'Review payments' },
] as const;

export default function AdminDashboardPage() {
  const query = useOperationsDashboard();
  const evidence = useEvidenceDashboard();

  return <Stack spacing={{ xs: 2, md: 2.5 }}>
    <Box sx={{ pb: 1.75, borderBottom: '1px solid', borderColor: 'divider' }}>
      <Typography variant="overline" sx={{ color: '#A8792E', fontWeight: 750, fontSize: 10, letterSpacing: 1.25 }}>OPERATIONS AT A GLANCE</Typography>
      <Typography variant="h4" sx={{ mt: 0.15 }}>Operations overview</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, maxWidth: 680 }}>Current service delivery and revenue exceptions from PropertyPilot.</Typography>
      <Box aria-hidden="true" sx={{ mt: 1.4, width: 48, height: 3, borderRadius: 2, background: 'linear-gradient(90deg, #B88A3B 0 35%, #167F83 35% 100%)' }} />
    </Box>
    {query.isError && <Alert severity="error" action={<Button color="inherit" onClick={() => void query.refetch()}>Retry</Button>}>
      {query.error instanceof Error ? query.error.message : 'Operations summary could not be loaded.'}
    </Alert>}
    {evidence.isError && <Alert severity="error" action={<Button color="inherit" onClick={() => void evidence.refetch()}>Retry</Button>}>
      {evidence.error instanceof Error ? evidence.error.message : 'Evidence review summary could not be loaded.'}
    </Alert>}
    <Grid container spacing={2}>
      {metrics.map((metric) => {
        const unavailable = query.isError || !query.data;
        return <Grid item xs={12} sm={6} lg={3} key={metric.key}>
        <Card variant="outlined" sx={{ height: '100%', overflow: 'hidden', position: 'relative', '&::before': { content: '""', position: 'absolute', left: 0, top: 0, width: 4, height: '100%', bgcolor: metric.key === 'failedRequests' || metric.key === 'failedPayments' ? 'error.main' : 'secondary.main', opacity: 0.8 } }}>
          <CardActionArea component={Link} to={metric.to} sx={{ height: '100%', display: 'block', '&:focus-visible': { outline: '3px solid', outlineColor: 'primary.main', outlineOffset: -3 } }}>
          <CardContent sx={{ p: { xs: 2, sm: 2.25 } }}>
            <Typography variant="overline" color="text.secondary" sx={{ fontSize: 9, lineHeight: 1.4, fontWeight: 750, letterSpacing: 0.85 }}>{metric.label}</Typography>
            <Typography variant="h4" sx={{ mt: 0.4, fontVariantNumeric: 'tabular-nums' }}>
              {query.isLoading ? <Skeleton width={72} /> : unavailable ? '—' : query.data[metric.key]}
            </Typography>
            <Typography variant="caption" color="text.secondary">{metric.description}</Typography>
            <Typography variant="caption" sx={{ color: '#0E696E', fontWeight: 700, display: 'block', mt: 1 }}>{metric.action} <Box component="span" aria-hidden="true">→</Box></Typography>
          </CardContent>
          </CardActionArea>
        </Card>
      </Grid>;
      })}
      <Grid item xs={12} sm={6} lg={3}>
        <Card variant="outlined" sx={{ height: '100%', overflow: 'hidden', position: 'relative', '&::before': { content: '""', position: 'absolute', left: 0, top: 0, width: 4, height: '100%', bgcolor: 'secondary.main' } }}>
          <CardActionArea component={Link} to="/evidence/validation" sx={{ height: '100%', display: 'block', '&:focus-visible': { outline: '3px solid', outlineColor: 'primary.main', outlineOffset: -3 } }}>
            <CardContent sx={{ p: { xs: 2, sm: 2.25 } }}>
              <Typography variant="overline" color="text.secondary" sx={{ fontSize: 9, lineHeight: 1.4, fontWeight: 750, letterSpacing: 0.85 }}>Evidence awaiting review</Typography>
              <Typography variant="h4" sx={{ mt: 0.4, fontVariantNumeric: 'tabular-nums' }}>{evidence.isLoading ? <Skeleton width={72} /> : evidence.isError || !evidence.data ? '—' : evidence.data.pendingValidation}</Typography>
              <Typography variant="caption" color="text.secondary">Evidence submissions in the backend review queue</Typography>
              <Typography variant="caption" sx={{ color: '#0E696E', fontWeight: 700, display: 'block', mt: 1 }}>Review evidence <Box component="span" aria-hidden="true">→</Box></Typography>
            </CardContent>
          </CardActionArea>
        </Card>
      </Grid>
    </Grid>
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
