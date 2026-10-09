import { Alert, Button, Card, CardContent, Grid, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useMyLeads } from '../hooks/useLeads';
import { LeadErrorState } from '../components/LeadPageStates';

const statuses = ['NEW', 'CONTACTED', 'QUALIFIED', 'READY_FOR_DEAL', 'DEAL_CONVERTED', 'CONVERTED', 'LOST', 'CLOSED'] as const;

export default function LeadDashboardPage() {
  const query = useMyLeads();
  const leads = query.data ?? [];
  const metrics: Array<[string, number]> = [['Total leads', leads.length], ...statuses.map((status) => [status.replaceAll('_', ' '), leads.filter((lead) => lead.status === status).length] as [string, number])];
  return <Stack spacing={3}>
    <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" gap={2}><Stack><Typography variant="h4" fontWeight={750}>Lead dashboard</Typography><Typography color="text.secondary">An overview of your lead enquiries and their current backend status.</Typography></Stack><Button component={Link} to="/customer/leads/new" variant="contained">Create lead</Button></Stack>
    {query.isError && <LeadErrorState error={query.error} retry={() => void query.refetch()} />}
    <Grid container spacing={2}>{metrics.map(([label, value]) => <Grid item xs={12} sm={6} md={4} key={label}><Card variant="outlined"><CardContent><Typography color="text.secondary">{label}</Typography>{query.isLoading ? <Skeleton width={70} height={44} /> : <Typography variant="h4" fontWeight={750}>{value}</Typography>}</CardContent></Card></Grid>)}</Grid>
    {!query.isLoading && !query.isError && leads.length === 0 && <Alert severity="info" action={<Button component={Link} to="/customer/leads/new">Create lead</Button>}>No lead data is available yet.</Alert>}
    <Button component={Link} to="/customer/leads">View all leads</Button>
  </Stack>;
}
