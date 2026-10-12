import { Alert, Box, Button, Card, CardContent, LinearProgress, Skeleton, Stack, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useQueries } from '@tanstack/react-query';
import { AgentOperationsApi } from '../services/AgentOperationsApi';
import { useAgentVisits } from '../hooks/useAgentOperations';
import { MetricCards, PageHeading, QueryFailure } from '../components/OperationsStates';

const sameDay = (value: string, target: Date) => { const date = new Date(value); return date.toDateString() === target.toDateString(); };
export default function AgentDashboardPage() {
  const query = useAgentVisits(); const visits = query.data ?? []; const now = new Date();
  const evidenceSummaries = useQueries({ queries: visits.map((visit) => ({ queryKey: ['evidence', 'visit-summary', visit.visitId], queryFn: () => AgentOperationsApi.visitEvidenceSummary(visit.visitId) })) });
  const completed = visits.filter((visit) => visit.status === 'COMPLETED');
  const counts = [
    { label: 'Assigned requests', value: new Set(visits.filter((visit) => ['SCHEDULED', 'IN_PROGRESS'].includes(visit.status)).map((visit) => visit.serviceRequestId)).size },
    { label: "Today's visits", value: visits.filter((visit) => sameDay(visit.scheduledAt, now)).length },
    { label: 'Upcoming visits', value: visits.filter((visit) => visit.status === 'SCHEDULED' && new Date(visit.scheduledAt) > now).length },
    { label: 'Completed visits', value: completed.length },
    { label: 'Overdue visits', value: visits.filter((visit) => visit.status === 'SCHEDULED' && new Date(visit.scheduledAt) < now).length },
    { label: 'Pending evidence', value: evidenceSummaries.filter((summary) => summary.data && summary.data.completionPercentage < 100).length },
  ];
  const dayKeys = Array.from({ length: 7 }, (_, index) => { const date = new Date(now); date.setDate(now.getDate() - 6 + index); return date; });
  const weekly = dayKeys.map((date) => ({ label: date.toLocaleDateString(undefined, { weekday: 'short' }), value: completed.filter((visit) => visit.endedAt && sameDay(visit.endedAt, date)).length }));
  const max = Math.max(1, ...weekly.map((item) => item.value));
  return <Stack spacing={3}>
    <PageHeading title="Agent Dashboard" subtitle="Your assigned field work, visit schedule, and completion progress." action={<Button component={Link} to="/agent/visits" variant="contained">Today's work</Button>} />
    {query.isError && <QueryFailure message={query.error.message} retry={() => void query.refetch()} />}
    {evidenceSummaries.some((summary) => summary.isError) && <Alert severity="error">Some evidence requirements could not be loaded. Refresh the page to retry.</Alert>}
    <MetricCards items={counts} loading={query.isLoading || evidenceSummaries.some((summary) => summary.isLoading)} />
    <Box display="grid" gridTemplateColumns={{ xs: '1fr', lg: '1fr 1fr' }} gap={2}>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Visits completed this week</Typography><Stack spacing={1.5} sx={{ mt: 2 }}>{query.isLoading ? <Skeleton height={170} /> : weekly.map((item) => <Box key={item.label}><Stack direction="row" justifyContent="space-between" sx={{ mb: .5 }}><Typography variant="body2">{item.label}</Typography><Typography variant="body2" fontWeight={700}>{item.value}</Typography></Stack><LinearProgress variant="determinate" value={item.value * 100 / max} aria-label={`${item.label}: ${item.value} completed visits`} /></Box>)}</Stack>{!query.isLoading && !completed.length && <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>Completed visit records will appear here.</Typography>}</CardContent></Card>
      <Card variant="outlined"><CardContent><Typography variant="h6" fontWeight={700}>Productivity trend</Typography><Typography variant="body2" color="text.secondary" sx={{ mt: .5 }}>Completed visits by day, based on recorded visit completion timestamps.</Typography><Stack direction="row" alignItems="end" spacing={1} sx={{ height: 190, mt: 2 }}>{query.isLoading ? <Skeleton width="100%" height={180} /> : weekly.map((item) => <Stack key={item.label} alignItems="center" justifyContent="end" flex={1} sx={{ height: '100%' }}><Typography variant="caption">{item.value}</Typography><Box role="img" aria-label={`${item.label}: ${item.value} visits completed`} sx={{ width: '100%', minHeight: 4, height: `${Math.max(4, item.value * 150 / max)}px`, bgcolor: 'primary.main', borderRadius: 1 }} /><Typography variant="caption" sx={{ mt: .75 }}>{item.label}</Typography></Stack>)}</Stack></CardContent></Card>
    </Box>
    {query.isSuccess && !visits.length && <Alert severity="info">No visits are assigned to your account yet. New work will appear here after assignment.</Alert>}
  </Stack>;
}
