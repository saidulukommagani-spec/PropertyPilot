import { Alert, Button, Stack } from '@mui/material';
import { Link } from 'react-router-dom';
import { useMyLeads } from '../hooks/useLeads';
import { LeadErrorState } from '../components/LeadPageStates';
import DashboardMetricCards from '../../../components/layouts/DashboardMetricCards';
import DashboardPageHeading from '../../../components/layouts/DashboardPageHeading';

const statuses = ['NEW', 'CONTACTED', 'QUALIFIED', 'READY_FOR_DEAL', 'DEAL_CONVERTED', 'CONVERTED', 'LOST', 'CLOSED'] as const;

export default function LeadDashboardPage() {
  const query = useMyLeads();
  const leads = query.data ?? [];
  const metrics: Array<[string, number]> = [['Total leads', leads.length], ...statuses.map((status) => [status.replaceAll('_', ' '), leads.filter((lead) => lead.status === status).length] as [string, number])];
  return <Stack spacing={3}>
    <DashboardPageHeading eyebrow="Customer growth" title="Lead Dashboard" subtitle="An overview of your lead enquiries and their current backend status." action={<Button component={Link} to="/customer/leads/new" variant="contained">Create lead</Button>} />
    {query.isError && <LeadErrorState error={query.error} retry={() => void query.refetch()} />}
    <DashboardMetricCards items={metrics.map(([label, value]) => ({ label, value }))} loading={query.isLoading} columns={{ xs: 1, sm: 2, md: 3, xl: 4 }} />
    {!query.isLoading && !query.isError && leads.length === 0 && <Alert severity="info" action={<Button component={Link} to="/customer/leads/new">Create lead</Button>}>No lead data is available yet.</Alert>}
    <Button component={Link} to="/customer/leads">View all leads</Button>
  </Stack>;
}
